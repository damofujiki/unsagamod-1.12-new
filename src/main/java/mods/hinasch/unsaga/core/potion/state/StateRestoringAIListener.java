package mods.hinasch.unsaga.core.potion.state;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.potion.EntityState;
import mods.hinasch.unsaga.core.potion.PotionDisturbAI;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAITasks;
import net.minecraft.potion.PotionEffect;

public class StateRestoringAIListener extends EntityState{

	public StateRestoringAIListener(String name) {
		super(name);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public boolean isValidPotionEffect(PotionEffect effect){
		return effect instanceof Effect;
	}

	@Override
	public void performEffect(EntityLivingBase el, int amp)
	{
		super.performEffect(el, amp);
		if(getAIRestoringListener(el)!=null){
			Effect listener = getAIRestoringListener(el);
			if(listener.getState()==Effect.ListenerState.LISTENING){
				if(!el.isPotionActive(listener.getParent())){
					this.restoreAI(el, listener);
				}
			}
			if(listener.getState()==Effect.ListenerState.OVER){
				StateCapability.ADAPTER.getCapability(el).removeState(this);
			}
		}
	}

	public void restoreAI(EntityLivingBase living,Effect effect){

		if(effect!=null && effect.hasAddedTasks() && living instanceof EntityCreature){
			restoreTasks((EntityCreature) living, effect);
			effect.setState(Effect.ListenerState.OVER);
			UnsagaMod.logger.trace(PotionDisturbAI.class.getName(), "AIを復元しました。");
		}
		//		}
	}



	private void restoreTasks(EntityCreature creature,Effect data){
		for(EntityAITasks.EntityAITaskEntry  entry:data.backup){
			creature.tasks.addTask(entry.priority, entry.action);
		}
		if(!data.getAddedTasks().isEmpty()){
			for(Function<EntityCreature,EntityAIBase> func:data.addedTasks){
				creature.tasks.removeTask(func.apply(creature));
			}

		}

	}

	public static @Nullable StateRestoringAIListener.Effect getAIRestoringListener(EntityLivingBase living){
		if(StateCapability.getState(living, UnsagaPotions.RESTORING_AI) instanceof StateRestoringAIListener.Effect){
			return  (mods.hinasch.unsaga.core.potion.state.StateRestoringAIListener.Effect) StateCapability.getState(living, UnsagaPotions.RESTORING_AI);
		}
		return null;
	}
	public static class Effect extends PotionEffect{

		public enum ListenerState {READY,LISTENING,OVER};
		private Set<EntityAITasks.EntityAITaskEntry> backup = new HashSet<>();
		private List<Function<EntityCreature,EntityAIBase>> addedTasks = new ArrayList<>();
		private ListenerState state = Effect.ListenerState.READY;
		private final PotionDisturbAI parent;
		public Effect(PotionDisturbAI parent, int durationIn) {
			super(UnsagaPotions.RESTORING_AI, durationIn);
			this.parent = parent;
			// TODO 自動生成されたコンストラクター・スタブ
		}

		public void backUpTask(EntityCreature c){
			this.backup.addAll(c.tasks.taskEntries);
		}

		public List<Function<EntityCreature,EntityAIBase>> getAddedTasks(){
			return this.addedTasks;
		}

		public void setAddedTasks(List<Function<EntityCreature,EntityAIBase>> addedTasks){
			this.addedTasks = addedTasks;
		}

		public void setState(ListenerState par1){
			this.state = par1;
		}

		public boolean hasAddedTasks(){
			return this.state==Effect.ListenerState.LISTENING || this.state==Effect.ListenerState.OVER;
		}

		public PotionDisturbAI getParent(){
			return this.parent;
		}

		public Effect.ListenerState getState(){
			return this.state;
		}

	}
}
