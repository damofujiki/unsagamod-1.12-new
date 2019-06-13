package mods.hinasch.unsaga.core.entity.ai;

import javax.annotation.Nullable;

import mods.hinasch.lib.entity.RangedHelper;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.entity.ai.EntityAIUnsagaSpell.ISpellCaster;
import mods.hinasch.unsagamagic.spell.CastableSpell;
import mods.hinasch.unsagamagic.spell.ISpell;
import mods.hinasch.unsagamagic.spell.UnsagaSpells;
import mods.hinasch.unsagamagic.spell.action.SpellCastIgniter;
import mods.hinasch.unsagamagic.spell.action.SpellTargetSelector;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

/**
 *
 * AIUseSpellを元にだいぶ雑に改造したAI
 *
 * @param <T>
 */
public class EntityAIUnsagaSpell<T extends EntityLiving & ISpellCaster> extends EntityAIBase{

	protected T caster;
	//	protected Range<Double> spellRange;
	protected ISpell spell;
	protected int spellWarmup;
	protected int spellCooldown;
	protected int spellCastingTimeMultiply;

	//    protected RangeMap<Double,Spell> castableSpells = TreeRangeMap.create();

	public EntityAIUnsagaSpell(T caster,int spellCastingTimeMultiply){
		this.caster = caster;
		this.spell = this.caster.selectSpell(null);
		this.spellCastingTimeMultiply = spellCastingTimeMultiply;
		//		this.spellRange = range;
	}

	public EntityAIUnsagaSpell(T caster){
		this(caster, 6);
		//		this.spellRange = range;
	}
	/**
	 * Returns whether the EntityAIBase should begin execution.
	 */
	public boolean shouldExecute()
	{

		if(!caster.canCastSpell(spell)){
			return false;
		}
		if (caster.getAttackTarget() == null)
		{
			return false;
		}
		else if (caster.isSpellCasting())
		{
			return false;
		}
		else
		{
			return caster.ticksExisted >= this.spellCooldown;
		}
	}

	//	protected boolean existCastableSpell(EntityLivingBase target){
	//		double distance = this.caster.getDistance(target);
	//		return this.spellRange.contains(distance);
	//	}
	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean shouldContinueExecuting()
	{
		return caster.getAttackTarget() != null && this.spellWarmup > 0;
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting()
	{

		if(!this.caster.isSpellCasting()){
			this.spell = this.caster.selectSpell(caster.getAttackTarget());
			if(caster.getRNG().nextFloat()<0.3D){
				this.spell = UnsagaSpells.EMPTY;
			}



			this.spellWarmup = this.getCastWarmupTime();
			caster.setSpellTicks(this.getCastingTime());
			this.spellCooldown = caster.ticksExisted + this.getCastingInterval();
			if(!spell.isEmpty()){
				SoundEvent soundevent = this.getSpellPrepareSound();
				RangedHelper.createChatBroadcaster(caster, 10.0D,HSLibs.translateKey("chat.unsaga.enemy.cast.start",this.caster.getName(),this.spell.getLocalized())).invoke();
				if (soundevent != null)
				{
					caster.playSound(soundevent, 1.0F, 1.0F);
				}
			}

		}


		//		     caster.setCurrentSpell(this.spell);
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	public void updateTask()
	{


		UnsagaMod.logger.trace("warmup", this.spellWarmup);
		--this.spellWarmup;

		if (this.spellWarmup == 0)
		{
			if(caster.getAttackTarget()!=null){
				this.castSpell(caster.getAttackTarget());
				//				caster.playSound(SoundEvents.ENTITY_ILLAGER_CAST_SPELL, 1.0F, 1.0F);
			}

		}
	}

	protected void castSpell(EntityLivingBase target){
		CastableSpell spell = new CastableSpell(this.spell,1.0F,1.0F,false);
		SpellCastIgniter igniter = SpellCastIgniter.ofEnemy(caster.world, caster, spell);
		SpellTargetSelector selector = new SpellTargetSelector(getDefaultInaccuracy());
//		if(igniter.getAction() instanceof ICustomSpellTargetSelector){
//			selector = ((ICustomSpellTargetSelector)igniter.getAction()).getSpellTargetSelector(getDefaultInaccuracy());
//		}

		selector.decideTarget(igniter, caster, target);


		igniter.cast();
//		UnsagaMod.PACKET_DISPATCHER.sendToAllAround(PacketSyncActionPerform.createSyncSpellCastPacket(this.caster.getEntityId(),this.spell),PacketUtil.getTargetPointNear(caster));

	}

	protected int getProb(){
		return 10;
	}

	protected int getDefaultInaccuracy(){
		return 5;
	}
	protected int getCastWarmupTime()
	{
		return 30;
	}

	public int getCastingTime(){
		return this.spell.castingTime() * spellCastingTimeMultiply;
	}

	public int getCastingInterval(){
		return this.spell.castingTime() * (spellCastingTimeMultiply/2);
	}


	protected SoundEvent getSpellPrepareSound(){
		return SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE;
	}


	/**
	 *
	 * updateAIあたりでspellticksを減らしていくこと
	 *
	 */
	public static interface ISpellCaster{
		public boolean isSpellCasting();
		public boolean canCastSpell(ISpell spell);
		@Deprecated
		public int getSpellTicks();
		@Deprecated
		public void setSpellTicks(int ticks);
		/**
		 * ここで使う術を選択する、コンストラクタ時だけtargetがnullで来る
		 * @param target
		 * @return
		 */
		public ISpell selectSpell(@Nullable EntityLivingBase target);
	}


}
