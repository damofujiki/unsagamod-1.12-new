package mods.hinasch.unsaga.core.entity.passive;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.Sets;

import io.netty.buffer.ByteBuf;
import mods.hinasch.lib.iface.IIntSerializable;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.VecUtil;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.unsaga.damage.AdditionalDamage;
import mods.hinasch.unsaga.damage.DamageComponent;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

/**
 *
 * 分身技の分身として使われる。攻撃は当たらないがすぐ消える。
 *
 */
public class EntityShadowClone extends EntityCreature implements IEntityAdditionalSpawnData{


	public static enum MoveType implements IIntSerializable{
		NO_SET(0),SIMPLE_MELEE(1),REVERSE_DELTA(2);

		final int meta;
		private MoveType(int meta){
			this.meta = meta;
		}
		@Override
		public int getMeta() {
			// TODO 自動生成されたメソッド・スタブ
			return meta;
		}

		public static MoveType fromMeta(int meta){return HSLibs.fromMeta(MoveType.values(), meta);}
	}

	int life = 0;
	int delayTime = 0;

	@Nullable EntityLivingBase owner;
	boolean hasAttacked = false;
	float rotation_velocity = 0;
	float current_rotation = 0;
	float prev_rotation = 0;
	int countAttacked = 0;
	int rotated = 0;
	DamageComponent damageCmp = DamageComponent.ZERO;
	Set<General> attackTypes = new HashSet<>();
	@Nullable EntityLivingBase ghostAttackTarget;
	MoveType moveType = MoveType.NO_SET;

	public EntityShadowClone(World worldIn) {
		super(worldIn);

//		this.setRenderYawOffset(rand.nextInt(180)-90);


        this.isImmuneToFire = true;
        this.setEntityInvulnerable(true);
//        this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(Items.IRON_SWORD));
		// TODO 自動生成されたコンストラクター・スタブ
	}
	public EntityShadowClone(World worldIn,EntityLivingBase owner) {
		this(worldIn);
		this.owner = owner;

	}
	@Override
    public boolean attackEntityFrom(DamageSource source, float amount)
    {
    	return false;
    }

	@Override
    public boolean canEntityBeSeen(Entity entityIn)
    {
        return false;
    }
    @Override
    public boolean canBePushed()
    {
        return false;
    }

	@Override
    protected void collideWithEntity(Entity entityIn)
    {
    }
	@Override
    protected void collideWithNearbyEntities()
    {
    }
	@Override
    public void fall(float distance, float damageMultiplier)
    {
    }

	@Override
	public void move(MoverType type, double x, double y, double z)
	{
		super.move(type, x, y, z);
		this.doBlockCollisions();
	}

    /**
	 * Called to update the entity's position/logic.
	 */
	@Override
	public void onUpdate()
	{
		this.noClip = true;
		super.onUpdate();
		this.noClip = false;
		this.setNoGravity(true);

		switch(this.moveType){
		case NO_SET:
			this.setDead();
			break;
		case REVERSE_DELTA:
			this.onUpdateReverseDelta();
			break;
		case SIMPLE_MELEE:
			this.onUpdateSimpleMelee();
			break;
		default:
			break;

		}



		this.updateArmSwingProgress();

	}

	//高速ナブラ用
	private void onUpdateReverseDelta(){
		this.setVelocity(0, 0, 0);
		if(this.ghostAttackTarget!=null && this.life<30){
			this.setPosition(ghostAttackTarget.posX, ghostAttackTarget.posY, ghostAttackTarget.posZ);
		}
		life ++;
		if(WorldHelper.isServer(world)){

//			this.setVelocity(vec.x, 0, vec.z);
//			if(this.life > 5){
//				this.life = 0;
//				this.rotated ++;
//				this.vec = vec.rotateYaw((float) Math.toRadians(120.0F));
//			}
//			if(this.rotated>2){
//				this.setDead();
//			}
			if(life > 80){
				this.setDead();
			}
		}
		if(life==30){
			this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
			this.swingArm(EnumHand.MAIN_HAND);
			if(WorldHelper.isServer(world)){
				this.attackByGhost();
				VecUtil.knockback(owner, ghostAttackTarget, 0.3D, 1.0D);
			}

		}
		if(life<30){
			rotation_velocity += 0.03F;
		}else{
			rotation_velocity -= 0.022F;
			if(rotation_velocity<0){
				rotation_velocity = 0;
			}
		}
//		UnsagaMod.logger.trace("rotation", this.rotation_velocity);
		this.prev_rotation = this.current_rotation;
		this.current_rotation += rotation_velocity;

	}

	//単純な分身攻撃
	private void onUpdateSimpleMelee(){
		this.setVelocity(0, 0, 0);
		if(this.ghostAttackTarget!=null ){
			this.setPosition(ghostAttackTarget.posX, ghostAttackTarget.posY, ghostAttackTarget.posZ);
		}
		if(WorldHelper.isServer(world)){
			life ++;
			if(this.hasAttacked){
				this.countAttacked ++;
			}
			if(life >100 || this.countAttacked>3){
				this.setDead();
			}

			if(this.life > this.delayTime && !this.hasAttacked){
//				this.setActiveHand(EnumHand.MAIN_HAND);
//				List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(1.0D),in -> in!=this);
				Optional.ofNullable(this.ghostAttackTarget)
				.ifPresent(in ->{
					in.hurtResistantTime = 0;
					in.hurtTime = 0;
					this.attackByGhost();
					this.hasAttacked = true;
				});
				this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
				this.swingArm(EnumHand.MAIN_HAND);
			}
		}
	}

	private void onUpdateWaitingAttack(){
		this.setVelocity(0, 0, 0);
		Optional.ofNullable(this.ghostAttackTarget)
		.ifPresent(in ->this.setPosition(in.posX, in.posY, in.posZ));
		life ++;
		if(WorldHelper.isServer(world)){
			if(life >100){
				this.setDead();
			}
		}
	}

	protected boolean attackByGhost(){
		if(this.ghostAttackTarget==null){
			return false;
		}
		if(!this.getHeldItemMainhand().isEmpty()){
			this.getHeldItemMainhand().getAttributeModifiers(EntityEquipmentSlot.MAINHAND).get(SharedMonsterAttributes.ATTACK_DAMAGE.getName());

		}

		Function<EntityShadowClone,DamageSource> damageGetter = in -> {
			if(this.owner instanceof EntityPlayer){
				return DamageSource.causeIndirectDamage(this,(EntityPlayer) this.owner);
			}
				return DamageSource.causeMobDamage(this);

		};

		this.ghostAttackTarget.attackEntityFrom(AdditionalDamage
				.register(new AdditionalDamage(damageGetter
						.apply(this),this.damageCmp.lp(),this.attackTypes)), this.damageCmp.hp());
		return true;
	}


	public float getPrevRotation(){
		return this.current_rotation;
	}
	public float getRotation(){
		return this.current_rotation;
	}
	@Override
	public void readSpawnData(ByteBuf additionalData) {
		int meta = additionalData.readInt();
		MoveType type = MoveType.fromMeta(meta);
		this.setMoveType(type);
		float rotation = additionalData.readFloat();
		this.setRenderYawOffset(rotation);
		int entityid = additionalData.readInt();
		Entity entity = this.world.getEntityByID(entityid);
		this.setAttackTarget((EntityLivingBase) entity);
	}
	public void setDamage(DamageComponent damage,General... types){
		this.damageCmp = damage;
		this.attackTypes = Sets.newHashSet(types);
	}

	public void setDamage(DamageComponent damage,Set<General> types){
		this.damageCmp = damage;
		this.attackTypes = types;
	}

	public void setDelay(int tick){
		this.delayTime = tick;
	}

	public void setMoveType(MoveType type){
		this.moveType = type;
	}

	public MoveType getMoveType(){
		return this.moveType;
	}
	@Override
    public void setRenderYawOffset(float offset)
    {
        this.rotationYaw = offset;
        this.rotationYawHead = offset;
        super.setRenderYawOffset(offset);
    }
	public void setGhostAttackTarget(EntityLivingBase target){
		this.ghostAttackTarget = target;
	}


	@Override
    protected void updateFallState(double y, boolean onGroundIn, IBlockState state, BlockPos pos)
    {
    }

	@Override
	public void writeSpawnData(ByteBuf buffer) {
		int meta = this.getMoveType().getMeta();
		buffer.writeInt(meta);
		float rotation = this.renderYawOffset;
		buffer.writeFloat(rotation);
		int id = Optional.ofNullable(this.ghostAttackTarget)
				.map(in ->in.getEntityId())
				.orElse(-1);
		buffer.writeInt(id);

	}

//	public static class ReverseDelta extends EntityShadowClone{
//
//		public ReverseDelta(World worldIn) {
//			super(worldIn);
//			// TODO 自動生成されたコンストラクター・スタブ
//		}
//
//		public ReverseDelta(World world, EntityLivingBase performer) {
//			super(world,performer);
//		}
//
//		@Override
//		public void onUpdate(){
//			super.onUpdate();
//			this.setVelocity(0, 0, 0);
//			if(this.ghostAttackTarget!=null && this.life<30){
//				this.setPosition(ghostAttackTarget.posX, ghostAttackTarget.posY, ghostAttackTarget.posZ);
//			}
//			life ++;
//			if(WorldHelper.isServer(world)){
//
////				this.setVelocity(vec.x, 0, vec.z);
////				if(this.life > 5){
////					this.life = 0;
////					this.rotated ++;
////					this.vec = vec.rotateYaw((float) Math.toRadians(120.0F));
////				}
////				if(this.rotated>2){
////					this.setDead();
////				}
//				if(life > 80){
//					this.setDead();
//				}
//			}
//			if(life==30){
//				this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
//				this.swingArm(EnumHand.MAIN_HAND);
//				if(WorldHelper.isServer(world)){
//					this.attackByGhost();
//					VecUtil.knockback(owner, ghostAttackTarget, 0.3D, 1.0D);
//				}
//
//			}
//			if(life<30){
//				rotation_velocity += 0.03F;
//			}else{
//				rotation_velocity -= 0.022F;
//				if(rotation_velocity<0){
//					rotation_velocity = 0;
//				}
//			}
////			UnsagaMod.logger.trace("rotation", this.rotation_velocity);
//			this.prev_rotation = this.current_rotation;
//			this.current_rotation += rotation_velocity;
//
//		}
//	}
//
//	public static class SimpleMelee extends EntityShadowClone{
//
//		public SimpleMelee(World worldIn) {
//			super(worldIn);
//			// TODO 自動生成されたコンストラクター・スタブ
//		}
//
//		public SimpleMelee(World world, EntityLivingBase performer) {
//			super(world,performer);
//		}
//
//		@Override
//		public void onUpdate()
//		{
//			super.onUpdate();
//			this.setVelocity(0, 0, 0);
//			if(WorldHelper.isServer(world)){
//				life ++;
//				if(this.hasAttacked){
//					this.countAttacked ++;
//				}
//				if(life >100 || this.countAttacked>3){
//					this.setDead();
//				}
//
//				if(this.life > this.delayTime && !this.hasAttacked){
////					this.setActiveHand(EnumHand.MAIN_HAND);
////					List<EntityLivingBase> list = this.world.getEntitiesWithinAABB(EntityLivingBase.class, this.getEntityBoundingBox().grow(1.0D),in -> in!=this);
//					if(this.ghostAttackTarget!=null){
////						EntityLivingBase target = list.get(0);
//						ghostAttackTarget.hurtResistantTime = 0;
//						ghostAttackTarget.hurtTime = 0;
////						float attack = 1.0F;
//						this.attackByGhost();
//
//						this.hasAttacked = true;
//					}
//					this.playSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F, 1.0F);
//					this.swingArm(EnumHand.MAIN_HAND);
//				}
//			}
//		}
//	}
}
