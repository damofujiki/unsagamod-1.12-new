package mods.hinasch.unsaga.ability.specialmove.action;

import javax.annotation.Nullable;

import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker;
import mods.hinasch.unsaga.common.specialaction.ActionBase.IAction;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import mods.hinasch.unsaga.core.potion.state.StateMovingTech;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundEvent;

/**
 *
 * 非同期の動きをするAction
 *MovingStates.Propertyが必要
 */
public class TechActionAsync implements IAction<TechInvoker>{

	final MovingStates.Property property;
	final int time;
	@Nullable SoundEvent sound;
	float pitch = 1.0F;

	public TechActionAsync(MovingStates.Property consumer,int time){
		this.property = consumer;
		this.time = time;
	}
	@Override
	public EnumActionResult apply(TechInvoker t) {
		if(!StateCapability.isStateActive(t.getPerformer(), UnsagaPotions.MOVING_STATE)){
			if(sound!=null)t.playSound(XYZPos.createFrom(t.getPerformer()), sound, false,pitch);
			if(property.isCancelHurt){
				StateCapability.getCapability(t.getPerformer()).addState(new PotionEffect(UnsagaPotions.CANCEL_HURT,time,0,false,false));
			}
			if(property.isCancelFall){
				StateCapability.getCapability(t.getPerformer()).addState(new PotionEffect(UnsagaPotions.CANCEL_FALL,time,0,false,false));
			}
			PotionEffect state = StateMovingTech.create(time, t,property.consumer);
			StateCapability.getCapability(t.getPerformer()).addState(state);
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}


	public TechActionAsync setStartSound(SoundEvent sound,float pitch){
		this.sound = sound;
		this.pitch = pitch;
		return this;
	}
}
