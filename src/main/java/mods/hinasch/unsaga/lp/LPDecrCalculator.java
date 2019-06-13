package mods.hinasch.unsaga.lp;

import java.util.Random;

import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.lib.util.ChatHandler;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.damage.AdditionalDamage;
import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew;
import mods.hinasch.unsaga.status.UnsagaStatus;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;


public class LPDecrCalculator {

	static final float INCREACE_CHECK1= 10.0F; //LP攻撃回数が増えるダメージ１
	static final float INCREACE_CHECK2= 7.0F; //LP攻撃回数が増えるダメージ１
	public static void tryDecrLP(Random rand,EntityLivingBase victim,DamageSource ds,float damageAmount,AdditionalDamage ad){
		int numberOfCheck = ad.getLPAttribute().chances();
		//攻撃回数０ならreturn
		if(numberOfCheck<=0){
			return;
		}
		//無敵時間中はreturn
		if(LifePoint.adapter.hasCapability(victim)){
			if(LifePoint.adapter.getCapability(victim).hurtInterval()>0){
				return;
			}
		}
		double dextality = victim.getEntityAttribute(UnsagaStatus.DEXTALITY).getAttributeValue();
		double lpResistance = victim.getEntityAttribute(UnsagaStatus.RESISTANCE_LP_HURT).getBaseValue();
		float damageRatio = 1.0F-(victim.getHealth()/victim.getMaxHealth())+0.1F;
		float slope; //傾き


//		if(UnsagaMod.configs.getDifficulty().isLPDamageEasy()){
//			//体力が１以下だと途端にLP減少率上昇
//			if(victim.getHealth()<=1.0F){
//				slope = 2.0F;
//			}else{
//				if(victim.getHealth()<=victim.getMaxHealth()/2){
//					slope = 0.3F;
//				}else{
//					slope = 0.15F;
//				}
//			}
//		}else{
//			//体力が１以下だと途端にLP減少率上昇
//			if(victim.getHealth()<=1.0F){
//				slope = 1.0F;
//			}else{
//
//				slope = 0.15F;
//			}
//		}
		float baseRate = UnsagaConfigHandlerNew.LP_SETTING.rateLPDecr;
		if(victim.getHealth()<=1.0F){
			slope = baseRate * 6.6F;
		}else{

			if(victim.getHealth()>=victim.getMaxHealth()){
				slope = 0.01F;
			}else{

				slope = baseRate;
//
//				//最大値半分未満になるとやや確率が上がる
//				if(victim.getHealth()<(victim.getMaxHealth()*0.5F)){
//					slope = 0.15F;
//				}else{
//					slope = 0.05F;
//				}

			}
		}

		if(damageAmount<1.0F){
			slope = 0.1F;
		}
		float lpDecrRatio = (float) ((Math.pow(damageRatio, 3))*slope + 0.03D);
		double dexLPRatio = UnsagaConfigHandlerNew.LP_SETTING.dexLPRatio;
		lpDecrRatio += (dexLPRatio*(1.0D+lpResistance-dextality))+0.5D; //LP防御力を影響させる。だいたい0.1で5%影響


		if(victim.getMaxHealth()<=damageAmount){
			numberOfCheck ++;
		}
		if(victim.getHealth()<=damageAmount){
			numberOfCheck ++;
		}
		if(damageAmount>=INCREACE_CHECK1){
			numberOfCheck ++;
		}
		if(damageAmount>=INCREACE_CHECK2){
			numberOfCheck ++;
		}

		lpDecrRatio = MathHelper.clamp(lpDecrRatio, 0.0F, 1.0F);


		lpDecrRatio += 0.25D * ad.getLPAttribute().amount();

		int lpDecr = 0;

		for(int i=0;i<numberOfCheck;i++){
			if(rand.nextFloat()<lpDecrRatio){
				lpDecr ++;
			}
		}

		if(victim.getHealth()>=victim.getMaxHealth()){
			lpDecr = 0;
		}

		UnsagaMod.logger.trace("lplogic", "lpdamage",lpDecr);

		if(LifePoint.adapter.hasCapability(victim) && lpDecr>0){
			if(HSLib.isDebug()){
				EntityPlayer ep = ds.getTrueSource() instanceof EntityPlayer ?(EntityPlayer) ds.getTrueSource() : victim instanceof EntityPlayer ? (EntityPlayer)victim : null;
				if(ep!=null){
					ChatHandler.sendChatToPlayer(ep, HSLibs.translateKey("chat.unsaga.lp_damaged", victim.getName(),lpDecr));
				}

			}
			LifePoint.adapter.getCapability(victim).decrLifePoint(lpDecr);
			NBTTagCompound nbt = UtilNBT.comp()
					.setInteger("entityid", victim.getEntityId())
					.setBoolean("isRenderLP", true)
					.setInteger("damage", lpDecr).get();
			UnsagaMod.PACKET_DISPATCHER.sendToAll(PacketSyncCapability
					.create(LifePoint.CAPA, LifePoint.adapter.getCapability(victim)
							,nbt));
//			UnsagaMod.packetDispatcher.sendToAllAround(PacketLP.createRenderDamagePacket(victim,(int) lpDecr), PacketUtil.getTargetPointNear(victim));
		}
	}

//	/** ＬＰダメージをブロードキャストする*/
//	public static void broadcastRenderHurtLPPacket(int lp,EntityLivingBase syncEntity,TargetPoint target){
//		if(hasCapability(syncEntity)){
//			//			PacketLPNew psl = PacketLPNew.getPacketRenderDamagedLP(syncEntity.getEntityId(), lp);
//			UnsagaMod.packetDispatcher.sendToAllAround(PacketLP.createRenderDamagePacket(syncEntity, lp), target);
//		}
//
//	}
}
