package mods.hinasch.unsaga.core.potion.state;

import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.sync.AsyncUpdateEvent;
import mods.hinasch.unsaga.ability.specialmove.action.AsyncTechEvents;
import mods.hinasch.unsaga.core.potion.EntityState;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;

/** 古い自作非同期イベントを無理やり対応させるやつ*/
public class StateUpdateEvent extends EntityState{

	public StateUpdateEvent(String name) {
		super(name);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public boolean isValidPotionEffect(PotionEffect effect){
		return effect instanceof Effect;
	}
	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int amp)
	{
		super.performEffect(entityLivingBaseIn, amp);
		if(!StateCapability.ADAPTER.hasCapability(entityLivingBaseIn)){
			return;
		}
		if(StateCapability.isStateActive(entityLivingBaseIn,UnsagaPotions.ASYNC_EVENT)){
			Effect effect = (Effect) StateCapability.ADAPTER.getCapability(entityLivingBaseIn).getState(UnsagaPotions.ASYNC_EVENT);
			if(effect.getEvent()!=AsyncTechEvents.EMPTY){
				AsyncUpdateEvent event = effect.getEvent();
				event.loop();
				if(event.hasFinished()){
					StateCapability.ADAPTER.getCapability(entityLivingBaseIn).removeStateWithSync(entityLivingBaseIn,UnsagaPotions.ASYNC_EVENT);
				}
			}
		}
	}
	public static class Effect extends PotionEffect{

		AsyncUpdateEvent asyncEvent = AsyncTechEvents.EMPTY;
		public Effect(AsyncUpdateEvent asyncEvent,int duration) {
			super(UnsagaPotions.ASYNC_EVENT,duration);
			this.asyncEvent = asyncEvent;
		}

		public AsyncUpdateEvent getEvent(){
			return this.asyncEvent;
		}
	}
}
