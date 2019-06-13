package mods.hinasch.unsaga.core.event;

import mods.hinasch.unsaga.core.potion.EntityState;
import mods.hinasch.unsaga.core.potion.UnsagaPotionInitializer;
import mods.hinasch.unsaga.skillpanel.SkillPanelEventHandler;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** 落下ダメージのキャンセルなど*/
public class CancelFallingEventHandler {

	@SubscribeEvent
	public void onFall(LivingFallEvent e){
		EntityState.onFall(e); //落下キャンセル状態
		UnsagaPotionInitializer.onFall(e); //重力時などに落下ダメージ増加
		SkillPanelEventHandler.onFall(e); //落下緩和などのスキル
	}
}
