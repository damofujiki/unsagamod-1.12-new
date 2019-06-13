package mods.hinasch.unsaga.core.entity.passive;

import java.util.Random;

import mods.hinasch.lib.entity.RangedEffect;
import mods.hinasch.lib.particle.ParticleHelper;
import mods.hinasch.lib.util.VecUtil;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.damage.AdditionalDamage;
import mods.hinasch.unsaga.damage.DamageHelper;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import mods.hinasch.unsaga.lp.LPAttribute;
import mods.hinasch.unsagamagic.spell.ISpell;
import mods.hinasch.unsagamagic.spell.UnsagaSpells;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityFireWall extends Entity{

	float scale = 2.0F;
	EntityLivingBase owner;
	final ISpell property = UnsagaSpells.FIRE_WALL;
	float damage = 1.0F;
	int life;
	public EntityFireWall(World worldIn) {
		super(worldIn);
		this.setSize(0.85F, 1.85F);
		this.life = 1000;
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public EntityFireWall(World worldIn,EntityLivingBase owner,float damage) {
		this(worldIn);
		this.owner = owner;
		this.damage = damage;
		// TODO 自動生成されたコンストラクター・スタブ
	}
	@Override
	protected void entityInit() {
		// TODO 自動生成されたメソッド・スタブ

	}

	public DamageSource getDamageSource(){
		LPAttribute damage = property.strength().lp();
		return DamageHelper.register(new AdditionalDamage(DamageSource.causeMobDamage(owner).setFireDamage(),damage,General.MAGIC));
//		return DamageSourceUnsaga.create(owner, property.getEffectStrength().lp(), General.MAGIC);
	}
	@Override
    public void onEntityUpdate()
    {
		super.onEntityUpdate();

		this.life --;

		if(this.life<0){
			this.setDead();
		}

		if(WorldHelper.isServer(world)){

			RangedEffect.builder()
			.owner(owner)
			.boundingBoxes(this.getEntityBoundingBox())
			.decideSelctorFromOwnerType()
			.consumer((self,target)->{
				if(target!=this.owner){
					target.attackEntityFrom(this.getDamageSource(), this.damage);
					target.setFire(100);
					VecUtil.knockback(this, (EntityLivingBase) target, 0.2D, 0.2D);
				}
			}).build(world).invoke();
//			RangedHelper<EntityFireWall> ranged = RangedHelper.create(world, this.owner, this.getEntityBoundingBox());
//
//			ranged.setSelectorFromOrigin().setParent(this);
//
//			ranged.setConsumer((self,target)->{
//				if(target!=this.owner){
//					target.attackEntityFrom(this.getDamageSource(), this.damage);
//					target.setFire(100);
//					VecUtil.knockback(this, (EntityLivingBase) target, 0.2D, 0.2D);
//				}
//
//			})
//			.invoke();

			this.world.getEntitiesWithinAABB(Entity.class, this.getEntityBoundingBox(),in -> in instanceof EntityThrowable || in instanceof EntityArrow)
			.forEach(in ->in.setDead());
		}

		if(this.getEntityWorld().rand.nextInt(8)==0){
			Random rand = this.getEntityWorld().rand;
			float f = scale /2;
			XYZPos pos = new XYZPos(this.getPosition());
			pos = pos.addDouble(0.5D, -0.5D, 0.5D);
			ParticleHelper.MovingType.FLOATING.spawnParticle(world, pos, EnumParticleTypes.FLAME, rand, 10, 0.1D);
		}

    }
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		this.damage = compound.getFloat("damage");
		this.life = compound.getInteger("life");

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		compound.setFloat("damage", damage);
		compound.setInteger("life", life);
	}



//	@Override
//	protected void readEntityFromNBT(NBTTagCompound compound) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
//
//	@Override
//	protected void writeEntityToNBT(NBTTagCompound compound) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}

}
