package mods.hinasch.unsaga.ability.specialmove.action;

import java.util.function.Consumer;

import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.network.PacketUtil;
import mods.hinasch.lib.particle.PacketParticle;
import mods.hinasch.lib.particle.ParticleHelper.MovingType;
import mods.hinasch.lib.particle.ParticleTypeWrapper.Particles;
import mods.hinasch.lib.util.SoundAndSFX;
import mods.hinasch.lib.util.VecUtil;
import mods.hinasch.lib.world.ScannerBuilder;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.ability.specialmove.Techs;
import mods.hinasch.unsaga.core.entity.projectile.custom_arrow.CustomArrowCapability;
import mods.hinasch.unsaga.core.entity.projectile.custom_arrow.SpecialArrowType;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import mods.hinasch.unsaga.damage.AdditionalDamage;
import mods.hinasch.unsaga.damage.DamageHelper;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.Sub;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.BlockWeb;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityTippedArrow;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.IntIdentityHashBiMap;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;

public class MovingStates {

	public static Property  VELOCITY_ZERO = new Property(false,false,(ctx)->{
		ctx.getOwner().setVelocity(0, ctx.getOwner().motionY, 0);
	});

	public static final int FLYING_KNEE_DUR = 60;
	public static Property  FLYING_KNEE = new Property(true,false,(ctx)->{
		Vec3d vec = ctx.getOwner().getLookVec();



//		if(ctx.getOwner().onGround){
//			ctx.expire();
//		}




		if(ctx.getDuration()>(FLYING_KNEE_DUR-5)){
			ctx.getOwner().setVelocity(vec.x, 1.0D, vec.z);
			ctx.getCollisionEnemies().forEach(in ->{
				in.attackEntityFrom(DamageHelper.register(new AdditionalDamage(ctx.getInvoker(),General.PUNCH)),ctx.getInvoker().getStrengthHP());
				ctx.expire();
			});
		}

//		UnsagaMod.logger.trace("duration", ctx.getDuration());
		//		ctx.setCancelFall(10);
	});

	public static Property  HAWK_BLADE = new Property(true,false,(ctx)->{
		Vec3d vec = ctx.getOwner().getLookVec().scale(0.3D);



		ctx.getCollisionEnemies().forEach(in ->{
			in.attackEntityFrom(DamageHelper.register(new AdditionalDamage(ctx.getInvoker(),General.SWORD)),ctx.getInvoker().getStrengthHP());
			in.addPotionEffect(new PotionEffect(UnsagaPotions.AWKWARD_DEX,ItemUtil.getPotionTime(15)));
		});
		ScannerBuilder.create().base(ctx.getOwner()).range(1).ready()
		.stream().forEach(pos ->{
			IBlockState state = ctx.getWorld().getBlockState(pos);
			if(state.getBlock() instanceof BlockLeaves || state.getBlock() instanceof BlockTallGrass || state.getBlock() instanceof BlockWeb){
				SoundAndSFX.playBlockBreakSFX(ctx.getWorld(), pos, state);
			}
		});


		//		if(ctx.getOwner().onGround){
		//
		////			double motionX = ctx.getOwner().motionX - vec.x;
		////			double motionZ = ctx.getOwner().motionZ - vec.z;
		////			ctx.getOwner().setVelocity(motionX >0 ? motionX : 0, 0, motionZ >0 ? motionZ : 0);
		//			ctx.getOwner().motionX += vec.x * 0.5D;
		//			ctx.getOwner().motionZ += vec.z * 0.5D;
		//			ctx.getOwner().motionY = 0;
		//		}else{
		//			ctx.getOwner().motionX += vec.x;
		//			ctx.getOwner().motionZ += vec.z;
		//			ctx.getOwner().motionY = -0.5D;
		////			ctx.getOwner().setVelocity(ctx.getOwner().motionX + vec.x, -1.0D, ctx.getOwner().motionZ + vec.z);
		//		}


		if(ctx.getOwner().onGround){
			ctx.getOwner().addVelocity(vec.x*0.5D, 0, vec.z*0.5D);
		}else{
			ctx.getOwner().addVelocity(vec.x, 0, vec.z);
		}

		if(ctx.getDuration()>ItemUtil.getPotionTime(1)){
			if(ctx.getOwner().motionY>0.0D){
				ctx.getOwner().motionY = 0.0D;
			}else{
				ctx.getOwner().motionY += 0.05D;
			}
		}

		//		ctx.setCancelFall(0);
	});
	public static Property  BLITZ = new Property(false,true,(ctx)->{
		Vec3d vec = ctx.getOwner().getLookVec().scale(1.2D);
		ctx.getOwner().addVelocity(vec.x, 0, vec.z);
		ctx.getCollisionEnemies().forEach(in ->{
			in.attackEntityFrom(DamageHelper.register(new AdditionalDamage(ctx.getInvoker(),General.SPEAR)),ctx.getInvoker().getStrengthHP());
			in.playSound(SoundEvents.ENTITY_IRONGOLEM_HURT, 1.0F, 1.0F);
			//			Vec3d back = vec.rotateYaw((float) Math.toRadians(180));
			ctx.getOwner().setVelocity(-vec.x, 0.3D, -vec.z);
			ctx.expire();

		});
		//		ctx.setCancelHurt();
	});


	public static Property  CUT_IN = new Property(false,true,(ctx)->{
		EntityLivingBase target = ctx.getInvoker().getTarget().get();
		Vec3d vec = VecUtil.getHeadingToEntityVec(ctx.getOwner(), target).normalize().scale(0.4D);
		ctx.getOwner().addVelocity(vec.x, 0D, vec.z);
		ctx.getCollisionEnemies().forEach(in ->{
			in.attackEntityFrom(DamageHelper.register(new AdditionalDamage(ctx.getInvoker(),General.SWORD)),ctx.getInvoker().getStrengthHP());

			//			Vec3d back = vec.rotateYaw((float) Math.toRadians(180));

		});


		//		ctx.setCancelHurt();
	});
	public static Property  GUST = new Property(false,true,(ctx)->{
		Vec3d vec = ctx.getOwner().getLookVec().scale(0.8D);
		ctx.getOwner().addVelocity(vec.x, 0, vec.z);
		ctx.getCollisionEnemies().forEach(in ->{
			if(ctx.getInvoker().getActionProperty()==Techs.GUST){
				in.attackEntityFrom(DamageHelper.register(new AdditionalDamage(ctx.getInvoker(),General.SWORD)),ctx.getInvoker().getStrengthHP());
				in.addPotionEffect(new PotionEffect(UnsagaPotions.AWKWARD_DEX,ItemUtil.getPotionTime(15)));
				ctx.getInvoker().playSound(XYZPos.createFrom(in), SoundEvents.ENTITY_IRONGOLEM_HURT, false);
			}
			if(ctx.getInvoker().getActionProperty()==Techs.SCATTERED_PETALS){
				if(StateCapability.ADAPTER.getCapability(in).isStateActive(UnsagaPotions.SNOWFALL)){
					in.attackEntityFrom(DamageHelper.register(new AdditionalDamage(ctx.getInvoker(),General.SWORD).setSubTypes(Sub.FREEZE)),ctx.getInvoker().getStrengthHP());
					StateCapability.ADAPTER.getCapability(in).removeState(UnsagaPotions.SNOWFALL);
					ctx.getInvoker().playSound(XYZPos.createFrom(in), SoundEvents.ENTITY_IRONGOLEM_HURT, false);
					if(WorldHelper.isClient(ctx.getWorld())){
						HSLib.packetDispatcher().sendToAllAround(PacketParticle.create(XYZPos.createFrom(in), Particles.PETALS, 10),PacketUtil.getTargetPointNear(in));
					}
				}

			}
		});
		if(ctx.getInvoker().getActionProperty()==Techs.GUST){
			ScannerBuilder.create().base(ctx.getOwner()).range(1).ready()
			.stream().forEach(pos ->{
				IBlockState state = ctx.getWorld().getBlockState(pos);
				if(state.getBlock() instanceof BlockLeaves || state.getBlock() instanceof BlockTallGrass || state.getBlock() instanceof BlockWeb){
					SoundAndSFX.playBlockBreakSFX(ctx.getWorld(), pos, state);
				}
			});
		}


		//		ctx.setCancelHurt();
	});

	public static final Property ARROW_RAIN = new Property(false,false,in ->{

		EntityLivingBase target = in.getInvoker().getTarget().get();
		int range = 10;
		for(int i=0;i<5;i++){
			EntityArrow arrow = new EntityTippedArrow(in.getWorld(),in.getOwner());
			double y = target.posY + 8;
			double x = target.posX+ target.getRNG().nextGaussian() * range;
			double z = target.posZ + target.getRNG().nextGaussian() * range;
//			if(i<=0){
//				x = target.posX;
//				z = target.posZ;
//			}

			arrow.setPosition(x, y, z);
			arrow.addVelocity(0, -0.5D, 0);
			arrow.setDamage(in.getInvoker().getStrength().hp());
			arrow.pickupStatus = EntityArrow.PickupStatus.DISALLOWED;
			if(CustomArrowCapability.ADAPTER.hasCapability(arrow)){
				CustomArrowCapability.ADAPTER.getCapability(arrow).setArrowType(SpecialArrowType.ARROW_RAIN);
				CustomArrowCapability.ADAPTER.getCapability(arrow).setArrowLPStrength(in.getInvoker().getStrength().lp());
			}
			if(in.getDuration() % 4 ==0){
				SoundAndSFX.playSound(in.getWorld(), XYZPos.createFrom(arrow), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 1.0F, 1.0F, false);
			}
			WorldHelper.safeSpawn(in.getWorld(), arrow);
		}

	});

	/**
	 * 稲妻キックと共用
	 */
	public static Property  SINKER = new Property(true,true,(ctx)->{
		Vec3d vec = ctx.getOwner().getLookVec();
		if(!ctx.getOwner().onGround){
			ctx.getOwner().addVelocity(vec.x, -0.6D, vec.z);
		}

		//		if(ctx.getOwner().onGround){
		//			ctx.expire();
		//		}
		ctx.getCollisionEnemies().forEach(in ->{

			ctx.expire();

			if(ctx.getInvoker().getActionProperty()==Techs.THUNDER_KICK){
				in.attackEntityFrom(DamageHelper.register(new AdditionalDamage(ctx.getInvoker(),General.PUNCH).setSubTypes(Sub.ELECTRIC)),ctx.getInvoker().getStrengthHP());
				ctx.getInvoker().playSound(XYZPos.createFrom(ctx.getOwner()), SoundEvents.ENTITY_LIGHTNING_IMPACT, false);
				ctx.getInvoker().spawnParticle(MovingType.FLOATING, in, EnumParticleTypes.CRIT, 10, 0.2D);
				VecUtil.knockback(ctx.getOwner(), in, 1.0F, 0.2D);
			}else{
				in.attackEntityFrom(DamageHelper.register(new AdditionalDamage(ctx.getInvoker(),General.PUNCH)),ctx.getInvoker().getStrengthHP());
				ctx.getInvoker().playSound(XYZPos.createFrom(ctx.getOwner()), SoundEvents.ENTITY_IRONGOLEM_HURT, false);
				in.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS,ItemUtil.getPotionTime(12)));
			}

			ctx.getOwner().setVelocity(0, 0, 0);
			ctx.getOwner().addVelocity(-vec.x, 0.5D, -vec.z);
		});

		ctx.setCancelFall(0);
	});

	public static final IntIdentityHashBiMap<Property> REGISTRY = new IntIdentityHashBiMap(30);


	static{
		REGISTRY.put(BLITZ, 0);
		REGISTRY.put(FLYING_KNEE, 1);
		REGISTRY.put(GUST, 2);
		REGISTRY.put(HAWK_BLADE, 3);
		REGISTRY.put(SINKER, 4);
		REGISTRY.put(VELOCITY_ZERO, 5);
		REGISTRY.put(ARROW_RAIN, 6);
	}

	public static class Property{

		public final boolean isCancelFall;
		public final boolean isCancelHurt;
		public final Consumer<IMovingStateAdapter> consumer;

		public Property(boolean fall,boolean hurt,Consumer<IMovingStateAdapter> consumer){
			this.isCancelFall = fall;
			this.isCancelHurt = hurt;
			this.consumer = consumer;
		}
	}
}
