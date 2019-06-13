package mods.hinasch.unsaga.chest;

import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.network.PacketSound;
import mods.hinasch.lib.util.ChatHandler;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.advancement.UnsagaTriggers;
import mods.hinasch.unsaga.core.entity.passive.EntityUnsagaChest;
import mods.hinasch.unsaga.core.entity.projectile.EntityMagicBall;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.SoundEvents;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

/** 魔法鍵の解除イベント*/
public class EventUnlockMagic {


	public static final EventUnlockMagic INSTANCE = new EventUnlockMagic();

	public void onAttack(LivingAttackEvent ev){

		if(ev.getEntityLiving() instanceof EntityUnsagaChest){
			if(ev.getSource().getTrueSource() instanceof EntityPlayer){
				UnsagaMod.logger.trace(this.getClass().getName(), ev.getSource().isMagicDamage());
				if(this.isUnlockableEntity(ev.getSource().getImmediateSource()) || ev.getSource().isMagicDamage()){
					this.tryUnlock((EntityUnsagaChest)ev.getEntityLiving(), (EntityPlayer)ev.getSource().getTrueSource());
					ev.setCanceled(true);
				}
//				if(ev.getSource().isMagicDamage() || this.isFireArrow(ev.getSource().getSourceOfDamage())){
//					if(this.unlockMagicLock((EntityUnsagaChestNew)ev.getEntityLiving(), (EntityPlayer)ev.getSource().getEntity())){
//						ev.setCanceled(true);
//					}
//				}
			}
		}

	}

	//マジックダメージだったらそれでいいのでこれはいらない…？
	@Deprecated
	private boolean isUnlockableEntity(Entity entity){
		if(entity instanceof EntityMagicBall){
			return true;
		}
//		if(entity instanceof EntityCustomArrow && EntityStateCapability.adapter.hasCapability(entity)){
//			StateArrow.Type type = ((EntityCustomArrow)entity).getArrowType();
//			return type==StateArrow.Type.MAGIC_ARROW;
//		}
//		if(entity instanceof EntityBlaster){
//			return true;
//		}
//		if(entity instanceof EntityBubbleBlow){
//			return true;
//		}
		if(entity instanceof EntityPotion){
			return true;
		}
		return false;
	}


	public boolean tryUnlock(EntityUnsagaChest chest,EntityPlayer ep){
		if(WorldHelper.isServer(ep.getEntityWorld())){
			if(chest.getEntityWorld().rand.nextInt(3)==0){ //失敗
				HSLib.packetDispatcher().sendTo(PacketSound.atEntity(SoundEvents.BLOCK_ANVIL_LAND,chest), (EntityPlayerMP) ep);
				ChatHandler.sendChatToPlayer(ep, HSLibs.translateKey("gui.unsaga.chest.failed.unlock.magicLock"));
			}else{ //成功
				if(ChestCapability.ADAPTER_ENTITY.hasCapability(chest) && ChestCapability.ADAPTER_ENTITY.getCapability(chest).hasMagicLocked()){
					ChestCapability.ADAPTER_ENTITY.getCapability(chest).setMagicLocked(false);
					chest.sync(ep);
					HSLib.packetDispatcher().sendTo(PacketSound.atEntity(SoundEvents.ENTITY_PLAYER_LEVELUP,chest), (EntityPlayerMP) ep);
					ChatHandler.sendChatToPlayer(ep, HSLibs.translateKey("gui.unsaga.chest.success.unlock.magicLock"));
					UnsagaTriggers.CHEST_UNLOCK_MAGIC.trigger((EntityPlayerMP) ep);
					return true;
				}
			}

		}
		return false;
	}
}
