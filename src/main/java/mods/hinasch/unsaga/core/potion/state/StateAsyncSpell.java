package mods.hinasch.unsaga.core.potion.state;

import java.util.function.Consumer;

import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.unsaga.core.potion.EntityState;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import mods.hinasch.unsagamagic.spell.action.IAsyncSpellAdapter;
import mods.hinasch.unsagamagic.spell.action.SpellCastIgniter;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class StateAsyncSpell extends EntityState{

	public StateAsyncSpell(String name) {
		super(name);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public boolean isValidPotionEffect(PotionEffect effect){
		return effect instanceof Effect;
	}

	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int meta)
	{
		super.performEffect(entityLivingBaseIn, meta);
		World world = entityLivingBaseIn.getEntityWorld();
		if(StateCapability.getState(entityLivingBaseIn,this) instanceof Effect){
			Effect effect = (Effect) StateCapability.getState(entityLivingBaseIn,this);
			int remaining = effect.getDuration();
			effect.getSpellConsumer().accept(new IAsyncSpellAdapter(){

				@Override
				public SpellCastIgniter getCaster() {
					// TODO 自動生成されたメソッド・スタブ
					return effect.getCaster();
				}

				@Override
				public EntityLivingBase getCasterEntity() {
					// TODO 自動生成されたメソッド・スタブ
					return effect.getCaster().getPerformer();
				}

				@Override
				public World getWorld() {
					// TODO 自動生成されたメソッド・スタブ
					return world;
				}}
			);
		}
	}
	public static class Effect extends PotionEffect{

		SpellCastIgniter invoker;
		Consumer<IAsyncSpellAdapter> behavior;
		boolean hasInitialized = false;
		NBTTagCompound args;
		public Effect(int durationIn, SpellCastIgniter invoker,Consumer<IAsyncSpellAdapter> behavior) {
			super(UnsagaPotions.ASYNC_SPELL, durationIn, 0,false,false);
			this.invoker = invoker;
			this.behavior = behavior;
			this.args = UtilNBT.compound();
			// TODO 自動生成されたコンストラクター・スタブ
		}

		public SpellCastIgniter getCaster(){
			return this.invoker;
		}

		/** 紐付けられた動作consumerを得る。*/
		public Consumer<IAsyncSpellAdapter> getSpellConsumer(){
			return this.behavior;
		}

		public boolean hasInitialized(){
			return this.hasInitialized;
		}

		public void setNBT(NBTTagCompound comp){
			this.args = comp;
		}

		public NBTTagCompound getNBT(){
			return this.args;
		}
	}
}
