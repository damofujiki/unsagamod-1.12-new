package mods.hinasch.unsaga.ability.specialmove.action;

import javax.annotation.Nonnull;

import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker;
import mods.hinasch.unsaga.common.specialaction.IActionMultipleMelee;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import mods.hinasch.unsaga.damage.DamageComponent;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;

public abstract class TechActionMeleeMultiple extends TechActionMelee implements IActionMultipleMelee{

	public TechActionMeleeMultiple(@Nonnull General... attributes) {
		super(attributes);
		this.setAdditionalBehavior((inveoker,target)->{
			Potion multipleAttackAllower = UnsagaPotions.ALLOW_MULTIPLE_ATTACK;
			if(StateCapability.isStateActive(target, multipleAttackAllower)){


				target.setVelocity(0,0,0);
				target.addVelocity(0, 1, 0);
				inveoker.getPerformer().addVelocity(0, 1, 0);
				int attackCount = StateCapability.getState(target, multipleAttackAllower).getAmplifier();
//				int attackCount = target.getActivePotionEffect(multipleAttackAllower).getAmplifier();



				attackCount --; //アタックカウントを減らす
				target.removePotionEffect(multipleAttackAllower);
				if(attackCount>0){
					//アタックカウント１以上ならアタックカウントを減らしてステイトを更新
					inveoker.playSound(XYZPos.createFrom(target), SoundEvents.ENTITY_IRONGOLEM_HURT, false);
					StateCapability.getCapability(target).addState(new PotionEffect(multipleAttackAllower,ItemUtil.getPotionTime(2),attackCount));
//					target.addPotionEffect(new PotionEffect(multipleAttackAllower,ItemUtil.getPotionTime(2),attackNum));
				}else{
					//アタックカウントが０以下なら
					inveoker.playSound(XYZPos.createFrom(target), SoundEvents.ENTITY_GENERIC_EXPLODE, false);
				}

			}else{
				target.setVelocity(0,0,0);
				target.addVelocity(0, 1, 0);
				inveoker.getPerformer().addVelocity(0, 1, 0);
				StateCapability.getCapability(target).addState(new PotionEffect(multipleAttackAllower,ItemUtil.getPotionTime(2),3));
//				target.addPotionEffect(new PotionEffect(multipleAttackAllower,ItemUtil.getPotionTime(2),3));
			}
//			if(EntityStateCapability.adapter.hasCapability(target)){
//				target.setVelocity(0,0,0);
//				target.addVelocity(0, 1, 0);
//				inveoker.getPerformer().addVelocity(0, 1, 0);
//
//				StateSpecialMove state = (StateSpecialMove) EntityStateCapability.adapter.getCapability(target).getState(StateRegistry.instance().stateSpecialMove);
//				state.setMultipleAttackComponent(new MultipleAttackComponent(inveoker, this.getMultipleAttackAllowingTime(), this.getMultipleMeleeNumber()));
//			}
		});
	}

	@Override
	public DamageComponent getDamage(TechInvoker context, EntityLivingBase target, DamageComponent base) {
		if(target.isPotionActive(UnsagaPotions.ALLOW_MULTIPLE_ATTACK)){
			int num = target.getActivePotionEffect(UnsagaPotions.ALLOW_MULTIPLE_ATTACK).getAmplifier();
			if(num==1){
				return DamageComponent.of(base.hp()+2.0F, base.lp());
			}
		}
		return base;
	}
	public static class MultipleAttackComponent{

		final TechInvoker invoker;
		final int allowedComboTimeMax;
		int allowedComboTime = 0;
		int attackNumber = 0;
		public MultipleAttackComponent(TechInvoker invoker, int allowingTime, int attackNumber) {
			super();
			this.invoker = invoker;
			this.allowedComboTimeMax = allowingTime;
			this.allowedComboTime = allowingTime;
			this.attackNumber = attackNumber;
		}

		public TechInvoker getInvoker(){
			return this.invoker;

		}

		public boolean isMultipleAttackAllowing(){
			return this.getAllowingTime()>0 && this.getAttackNumber()>0;
		}
		public int getAllowingTime(){
			return this.allowedComboTime;
		}

		public int getAttackNumber(){
			return this.attackNumber;
		}

		public void resetComboTime(){
			this.allowedComboTime = this.allowedComboTimeMax;
		}
		public void reduceComboTime(){
			if(this.allowedComboTime>0){
				this.allowedComboTime --;
				UnsagaMod.logger.trace("combo", this.allowedComboTime);
			}

		}

		public void reduceAttackNumber(){
			if(this.attackNumber>0){
				this.attackNumber --;
			}
		}
	}
}
