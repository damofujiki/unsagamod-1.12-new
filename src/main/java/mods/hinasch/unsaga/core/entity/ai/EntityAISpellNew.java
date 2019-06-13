package mods.hinasch.unsaga.core.entity.ai;

import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.entity.RangedHelper;
import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.network.PacketSound;
import mods.hinasch.lib.network.PacketUtil;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.entity.ai.EntityAIUnsagaSpell.ISpellCaster;
import mods.hinasch.unsaga.core.net.packet.PacketSyncActionPerform;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import mods.hinasch.unsaga.core.potion.state.StateCasting;
import mods.hinasch.unsagamagic.spell.CastableSpell;
import mods.hinasch.unsagamagic.spell.ISpell;
import mods.hinasch.unsagamagic.spell.UnsagaSpells;
import mods.hinasch.unsagamagic.spell.action.SpellActionBase;
import mods.hinasch.unsagamagic.spell.action.SpellCastIgniter;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundEvent;

public class EntityAISpellNew<T extends EntityLiving & ISpellCaster> extends EntityAIBase {

	final T caster;
	ISpell castingSpell = UnsagaSpells.EMPTY;
	int cooling = 0;
	int executeValue = 65;

	public EntityAISpellNew(T caster){
		this.caster = caster;
	}

    public void resetTask()
    {
        super.resetTask();
        this.castingSpell = UnsagaSpells.EMPTY;
    }

    @Override
	public boolean shouldExecute()
	{
		if(!caster.canCastSpell(this.castingSpell)){
			return false;
		}
		if (caster.getAttackTarget() == null)
		{
			return false;
		}
		else if (StateCapability.isStateActive(caster, UnsagaPotions.CASTING))
		{
			return false;
		}
		return true;
	}

	public void updateTask()
	{

		this.cooling --;

//		UnsagaMod.logger.trace("cooling", this.cooling);
		if(this.cooling<=0){
			this.cooling = 0;
		}

		if(this.cooling<=0){
			if(this.caster.getRNG().nextInt(100)<=this.executeValue && this.caster.getAttackTarget()!=null){
				ISpell spell = caster.selectSpell(this.caster.getAttackTarget());
				this.castSpell(caster.getAttackTarget(),spell);
				this.cooling = this.getSpellCoolingTime() + spell.castingTime() *3;
			}else{
				this.cooling = this.getInterval();
		}

		}
	}

	public void startExecuting()
	{

//		UnsagaMod.logger.trace("exe", this.cooling);
		this.cooling = this.getInterval() + 30;


	}

	protected int getSpellCoolingTime(){
		return 50;
	}

	protected int getInterval(){
		return 30;
	}

	protected void castSpell(EntityLivingBase target,ISpell spell){
		CastableSpell component = new CastableSpell(spell,1.0F,1.0F,false);
		SpellCastIgniter igniter = SpellCastIgniter.ofEnemy(caster.world, caster, component);
		SpellActionBase base = (SpellActionBase)igniter.getAction();

		base.getSpellTargetSelector(getDefaultInaccuracy()).decideTarget(igniter, caster, target);

		SoundEvent soundevent = this.getSpellPrepareSound();
		RangedHelper.createChatBroadcaster(caster, 10.0D,HSLibs.translateKey("chat.unsaga.enemy.cast.start",this.caster.getName(),spell.getLocalized())).invoke();
		if (soundevent != null)
		{
			HSLib.packetDispatcher().sendToAllAround(PacketSound.atPos(soundevent, XYZPos.createFrom(caster)), PacketUtil.getTargetPointNear(caster));
//			caster.playSound(soundevent, 1.0F, 1.0F);
		}


		StateCapability.ADAPTER.getCapability(this.caster).addState(StateCasting.create(igniter, component.rawSpell().castingTime()*3));
		UnsagaMod.PACKET_DISPATCHER.sendToAllAround(PacketSyncActionPerform.createSyncSpellCastingState(component.rawSpell().castingTime()*3, caster.getEntityId()), PacketUtil.getTargetPointNear(caster));
	}
	protected SoundEvent getSpellPrepareSound(){
		return SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE;
	}

	protected int getDefaultInaccuracy(){
		return 3;
	}
}
