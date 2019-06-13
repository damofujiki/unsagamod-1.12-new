package mods.hinasch.unsaga.common.specialaction;

import java.util.function.Function;

import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.sync.AsyncUpdateEvent;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.specialmove.action.AsyncTechEvents;
import mods.hinasch.unsaga.common.specialaction.ActionBase.IAction;
import mods.hinasch.unsaga.common.specialaction.IActionPerformer.TargetType;
import mods.hinasch.unsaga.core.potion.state.StateUpdateEvent;
import net.minecraft.util.EnumActionResult;

public class ActionAsyncEvent<T extends IActionPerformer> implements IAction<T>{


	Function<T,AsyncUpdateEvent> eventGetter;

	@Override
	public EnumActionResult apply(T context) {
		UnsagaMod.logger.trace("magic", context.getWorld());
		if(context.getTargetType()==TargetType.POSITION){
			if(context.getTargetCoordinate().isPresent()){
				AsyncUpdateEvent event = this.eventGetter.apply(context);
				if(event!=null && event!=AsyncTechEvents.EMPTY){
					if(StateCapability.ADAPTER.hasCapability(context.getPerformer())){
						StateCapability.ADAPTER.getCapability(context.getPerformer()).addState(new StateUpdateEvent.Effect(event, 1000));
					}
//					HSLib.addAsyncEvent(event.getSender(), event);
				}

				return EnumActionResult.SUCCESS;
			}

		}else{
			AsyncUpdateEvent event = this.eventGetter.apply(context);

			if(event!=null && event!=AsyncTechEvents.EMPTY){
//				HSLib.addAsyncEvent(event.getSender(), event);
				if(StateCapability.ADAPTER.hasCapability(event.getSender())){
					StateCapability.ADAPTER.getCapability(event.getSender()).addState(new StateUpdateEvent.Effect(event, 1000));
				}
			}

			return EnumActionResult.SUCCESS;
		}


		return EnumActionResult.PASS;
	}

	public ActionAsyncEvent setEventFactory(AsyncEventFactory<T> factory){
		this.eventGetter = factory;
		return this;
	}


	public static interface AsyncEventFactory<V> extends Function<V,AsyncUpdateEvent>{

	}
}
