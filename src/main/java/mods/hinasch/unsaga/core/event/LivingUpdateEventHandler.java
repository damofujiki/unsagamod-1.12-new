package mods.hinasch.unsaga.core.event;

import mods.hinasch.lib.core.HSLib;
import mods.hinasch.unsaga.ability.AbilityInitializer;
import mods.hinasch.unsaga.core.entity.UnsagaActionCapability;
import mods.hinasch.unsaga.core.potion.UnsagaPotionInitializer;
import mods.hinasch.unsaga.core.world.chunk.ChestDetectionEventHandler;
import mods.hinasch.unsaga.core.world.chunk.UnsagaChunkCapability;
import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew;
import mods.hinasch.unsaga.lp.LifePoint;
import mods.hinasch.unsaga.lp.event.LPHurtIntervalCountHandler;
import mods.hinasch.unsaga.lp.event.LPRestoreHandler;
import mods.hinasch.unsaga.minsaga.event.ApplyAbilityHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** LivingUpdateイベントをまとめたもの*/
public class LivingUpdateEventHandler {

	@SubscribeEvent
	public void onLivingUpdate(LivingUpdateEvent e){
//		UnsagaMod.logger.trace("livingupdate", "called");
		UnsagaPotionInitializer.onLivingUpdate(e); //ぽーしょん
		SprintTimerHandler.onLivingUpdate(e); //スプリント情報
		LPRestoreHandler.onLivingUpdate(e); //LP回復
		LPHurtIntervalCountHandler.update(e); //LPダメージ表示時間
		LifePoint.onUpdate(e.getEntityLiving()); //自然回復
//		StateCapability.onLivingUpdate(e); //ステイト
		UnsagaActionCapability.onLivingUpdate(e); //スプリントタイマー・ブロッキングなど
		UnsagaChunkCapability.onLivingUpdate(e); //五行相平衡
		AbilityInitializer.onLivingUpdate(e); //パッシブアビリティ
		ChestDetectionEventHandler.onLivingUpdate(e); //チャンクチェスト（財宝）の発見
		ApplyAbilityHandler.onUpdate(e); //ミンサガ補強のアビリティ実装
		if(HSLib.isDebug())this.debug(e);
	}

	private void debug(LivingUpdateEvent e){
		if(UnsagaConfigHandlerNew.DEBUG.enableAlwaysDay){
			World world = e.getEntityLiving().world;
			if(e.getEntityLiving() instanceof EntityPlayer){
				EntityPlayer ep = (EntityPlayer) e.getEntityLiving();
				if(ep.isCreative()){
					if(!world.isDaytime() && world instanceof WorldServer){
						WorldServer server = (WorldServer) world;
						this.setAllWorldTimes(server.getMinecraftServer(), 0);
					}
				}
			}
		}
	}


    protected void setAllWorldTimes(MinecraftServer server, int time)
    {
        for (int i = 0; i < server.worlds.length; ++i)
        {
            server.worlds[i].setWorldTime((long)time);
        }
    }
}
