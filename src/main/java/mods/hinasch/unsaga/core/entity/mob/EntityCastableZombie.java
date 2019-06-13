package mods.hinasch.unsaga.core.entity.mob;

import java.util.List;

import com.google.common.collect.ImmutableList;

import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.core.entity.ai.EntityAISpellNew;
import mods.hinasch.unsaga.core.entity.ai.EntityAIUnsagaSpell.ISpellCaster;
import mods.hinasch.unsagamagic.spell.ISpell;
import mods.hinasch.unsagamagic.spell.UnsagaSpells;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.world.World;

public abstract class EntityCastableZombie extends EntityZombie implements ISpellCaster{

	int spellTicks = 0;
	public EntityCastableZombie(World worldIn) {
		super(worldIn);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public abstract List<ISpell> getSpellList();

	@Override
	protected void initEntityAI()
	{
		super.initEntityAI();
		this.tasks.addTask(4, new EntityAISpellNew(this));
	}


	@Override
	public boolean isSpellCasting() {
		// TODO 自動生成されたメソッド・スタブ
		return this.spellTicks >0;
	}


	@Override
	public boolean canCastSpell(ISpell spell) {
		// TODO 自動生成されたメソッド・スタブ
		return true;
	}


	@Override
	public int getSpellTicks() {
		// TODO 自動生成されたメソッド・スタブ
		return this.spellTicks;
	}


	@Override
	public void setSpellTicks(int ticks) {
		// TODO 自動生成されたメソッド・スタブ
		this.spellTicks = ticks;
	}


	@Override
	public ISpell selectSpell(EntityLivingBase target) {
		// TODO 自動生成されたメソッド・スタブ
		return HSLibs.randomPick(rand, this.getSpellList());
	}

	@Override
    protected void updateAITasks()
    {
        super.updateAITasks();

        if (this.spellTicks > 0)
        {
            --this.spellTicks;
        }

    }

	public static class Revenant extends EntityCastableZombie{

		public Revenant(World worldIn) {
			super(worldIn);
			// TODO 自動生成されたコンストラクター・スタブ
		}

		@Override
	    protected void applyEntityAttributes()
	    {
	        super.applyEntityAttributes();
	        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(25.0D);

	    }
		@Override
		public List<ISpell> getSpellList() {
			// TODO 自動生成されたメソッド・スタブ
			return ImmutableList.of(UnsagaSpells.FEAR,UnsagaSpells.SPOIL,UnsagaSpells.BLASTER);
		}

	}
}
