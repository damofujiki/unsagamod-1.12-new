package mods.hinasch.unsaga.core.entity.mob;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.Statics;
import mods.hinasch.unsaga.core.client.render.entity.RenderGelatinous;
import mods.hinasch.unsaga.core.entity.ai.EntityAISpellNew;
import mods.hinasch.unsaga.core.entity.ai.EntityAIUnsagaSpell.ISpellCaster;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import mods.hinasch.unsaga.damage.AdditionalDamage;
import mods.hinasch.unsaga.damage.DamageHelper;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import mods.hinasch.unsaga.lp.LPAttribute;
import mods.hinasch.unsagamagic.spell.ISpell;
import mods.hinasch.unsagamagic.spell.UnsagaSpells;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIFindEntityNearest;
import net.minecraft.entity.ai.EntityAIFindEntityNearestPlayer;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public abstract class EntityGelatinous extends EntitySlime implements ISpellCaster{

	public static class GelatinousMatter extends EntityGelatinous{

		public GelatinousMatter(World worldIn) {
			super(worldIn);
			// TODO 自動生成されたコンストラクター・スタブ
		}

		@Override
		public List<ISpell> getSpellList() {
			// TODO 自動生成されたメソッド・スタブ
			return ImmutableList.of(UnsagaSpells.CALL_THUNDER);
		}

		@Override
		public ResourceLocation getTextureName() {
			// TODO 自動生成されたメソッド・スタブ
			return RenderGelatinous.GELATINOUS_MATTER_TEX;
		}

	}


	public static class GoldenBaum extends EntityGelatinous{

		public GoldenBaum(World worldIn) {
			super(worldIn);
			// TODO 自動生成されたコンストラクター・スタブ
		}

		@Override
		public List<ISpell> getSpellList() {
			// TODO 自動生成されたメソッド・スタブ
			return ImmutableList.of(UnsagaSpells.ICE_NEEDLE,UnsagaSpells.PURIFY,UnsagaSpells.FEAR,UnsagaSpells.DEADLY_DRIVE,UnsagaSpells.WATER_SHIELD);
		}

		@Override
		public ResourceLocation getTextureName() {
			// TODO 自動生成されたメソッド・スタブ
			return RenderGelatinous.GOLDENBAUM_TEX;
		}

	}
	private static final DataParameter<Integer> SLIME_TYPE = EntityDataManager.<Integer>createKey(EntityTreasureSlimeNew.class, DataSerializers.VARINT);

//	public static enum SlimeType implements IIntSerializable{GELATINOUS_MATTER(0),GOLDENBAUM(1);
//
//		private final int meta;
//
//		private SlimeType(int meta){
//			this.meta = meta;
//		}
//
//		public List<Spell> getCastableSpells(){
//			switch(this){
//			case GELATINOUS_MATTER:
//				return ImmutableList.of(SpellRegistry.CALL_THUNDER);
//			case GOLDENBAUM:
//				return ImmutableList.of(SpellRegistry.ICE_NEEDLE,SpellRegistry.PURIFY,SpellRegistry.FEAR,SpellRegistry.DEADLY_DRIVE,SpellRegistry.WATER_SHIELD);
//			default:
//				break;
//
//			}
//			return ImmutableList.of();
//		}
//
//		public String getTextureName(){
//			switch(this){
//			case GELATINOUS_MATTER:
//				return "gelotinous_matter";
//			case GOLDENBAUM:
//				return "goldenbaum";
//			default:
//				break;
//
//			}
//			return StringUtil.EMPTY_STRING;
//		}
//		@Override
//		public int getMeta() {
//			// TODO 自動生成されたメソッド・スタブ
//			return this.meta;
//		}
//
//		public static SlimeType fromMeta(int meta){return HSLibs.fromMeta(SlimeType.values(), meta);}
//	}


	int spellTicks = 0;

	ISpell currentSpell = UnsagaSpells.EMPTY;
	public EntityGelatinous(World worldIn) {
		super(worldIn);
		// TODO 自動生成されたコンストラクター・スタブ
	}
	@Override
	protected void applyEntityAttributes()
	{
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).applyModifier(new AttributeModifier(UUID.fromString("fe2d002e-1597-4655-93bb-abde70bbe8cd"),"gelatinous_life_additional", 15.0D, Statics.OPERATION_INCREMENT));
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0F);
	}
	@Override
	public boolean canCastSpell(ISpell spell) {
		// TODO 自動生成されたメソッド・スタブ
		return this.isPotionActive(UnsagaPotions.LOCK_SLIME) ? false : true;
	}

	@Override
    protected void dealDamage(EntityLivingBase entityIn)
    {
		int i = this.getSlimeSize();
		AdditionalDamage data = new AdditionalDamage(DamageSource.causeMobDamage(this),new LPAttribute(0.3F,1),General.PUNCH);
        if (this.canEntityBeSeen(entityIn) && this.getDistanceSq(entityIn) < 0.6D * (double)i * 0.6D * (double)i && entityIn.attackEntityFrom(DamageHelper.register(data), this.getAttackStrength()))
        {
            this.playSound(SoundEvents.ENTITY_SLIME_ATTACK, 1.0F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.2F + 1.0F);
            this.applyEnchantments(this, entityIn);
        }
    }


	@Override
	protected int getAttackStrength()
	{
		return (int) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
	}

	@Override
    public int getSlimeSize()
    {
        return 4;
    }


	public abstract List<ISpell> getSpellList();

	@Override
	public int getSpellTicks() {
		// TODO 自動生成されたメソッド・スタブ
		return this.spellTicks;
	}


	public abstract ResourceLocation getTextureName();


	@Override
	protected void initEntityAI()
	{
		super.initEntityAI();
		this.tasks.addTask(4, new EntityAISpellNew(this));
		this.targetTasks.addTask(1, new EntityAIFindEntityNearestPlayer(this));
		this.targetTasks.addTask(3, new EntityAIFindEntityNearest(this, EntityIronGolem.class));
	}

	@Override
	public boolean isSpellCasting() {
		// TODO 自動生成されたメソッド・スタブ
		return this.getSpellTicks() > 0;
	}

    @Nullable
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData livingdata)
    {
        this.getEntityAttribute(SharedMonsterAttributes.FOLLOW_RANGE).applyModifier(new AttributeModifier("Random spawn bonus", this.rand.nextGaussian() * 0.05D, 1));

        if (this.rand.nextFloat() < 0.05F)
        {
            this.setLeftHanded(true);
        }
        else
        {
            this.setLeftHanded(false);
        }

        return livingdata;
    }

	@Override
	public void readEntityFromNBT(NBTTagCompound compound)
	{
		super.readEntityFromNBT(compound);
//		this.setSlimeType(Type.fromMeta(compound.getInteger("slimeType")));
	}

	@Override
	public ISpell selectSpell(EntityLivingBase target) {

		return HSLibs.randomPick(rand, this.getSpellList());
	}

	@Override
    public void setDead()
    {
        this.isDead = true;
    }


	@Override
	public void setSpellTicks(int ticks) {
		// TODO 自動生成されたメソッド・スタブ
		this.spellTicks = ticks;
	}

	@Override
	public void writeEntityToNBT(NBTTagCompound compound)
	{
		super.writeEntityToNBT(compound);
//		compound.setInteger("slimeType", this.getSlimeType().getMeta());
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
}
