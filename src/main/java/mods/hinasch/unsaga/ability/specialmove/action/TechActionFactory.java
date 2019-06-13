package mods.hinasch.unsaga.ability.specialmove.action;

import java.util.EnumSet;
import java.util.List;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.mojang.realmsclient.util.Pair;

import mods.hinasch.lib.entity.RangedHelper;
import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.network.PacketUtil;
import mods.hinasch.lib.particle.ParticleHelper.MovingType;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.SoundAndSFX;
import mods.hinasch.lib.util.VecUtil;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker.InvokeType;
import mods.hinasch.unsaga.common.specialaction.ActionAsyncEvent;
import mods.hinasch.unsaga.common.specialaction.ActionBase.IAction;
import mods.hinasch.unsaga.common.specialaction.ActionList;
import mods.hinasch.unsaga.common.specialaction.ActionProjectile;
import mods.hinasch.unsaga.common.specialaction.ActionRangedAttack;
import mods.hinasch.unsaga.common.specialaction.ActionRangedAttack.RangedAttackSetting;
import mods.hinasch.unsaga.common.specialaction.ActionRequireJump;
import mods.hinasch.unsaga.common.specialaction.ActionSelf;
import mods.hinasch.unsaga.common.specialaction.ActionSound;
import mods.hinasch.unsaga.common.specialaction.ActionSummon;
import mods.hinasch.unsaga.common.specialaction.ActionTargettable;
import mods.hinasch.unsaga.common.specialaction.ActionThunder;
import mods.hinasch.unsaga.common.specialaction.ActionWorld;
import mods.hinasch.unsaga.core.entity.mob.EntitySwarm;
import mods.hinasch.unsaga.core.entity.passive.EntityBeam;
import mods.hinasch.unsaga.core.entity.passive.EntityEffectSpawner;
import mods.hinasch.unsaga.core.entity.projectile.EntityFlyingAxe;
import mods.hinasch.unsaga.core.entity.projectile.EntityJavelin;
import mods.hinasch.unsaga.core.entity.projectile.EntityThrowingKnife;
import mods.hinasch.unsaga.core.net.packet.PacketClientThunder;
import mods.hinasch.unsaga.core.potion.UnsagaPotionInitializer;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import mods.hinasch.unsaga.core.potion.state.StateGettingThrown;
import mods.hinasch.unsaga.core.potion.state.StateMovingTech;
import mods.hinasch.unsaga.damage.AdditionalDamage;
import mods.hinasch.unsaga.damage.DamageComponent;
import mods.hinasch.unsaga.damage.DamageHelper;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.Sub;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;


public class TechActionFactory {

	public static final IAction<TechInvoker> SWINGSOUND_NOPARTICLE =  in ->{
		in.swingMainHand(true, false);
		return EnumActionResult.PASS;
	};
	public static final IAction<TechInvoker> SWINGSOUND_AND_PARTICLE= in ->{
		in.swingMainHand(true, true);
		return EnumActionResult.PASS;
	};

	public static final IAction<TechInvoker> STAFF_EFFECT = in ->{
		XYZPos pos = new XYZPos(in.getTargetCoordinate().get());
		in.playSound(pos, SoundEvents.ENTITY_GENERIC_EXPLODE, true);

		in.spawnParticle(MovingType.DIVERGE, pos, EnumParticleTypes.SMOKE_NORMAL, 20, 0.5D);
		return EnumActionResult.PASS;
	};
	public static final IAction<TechInvoker> WAVE_PARTICLE = in ->{
		in.playSound(new XYZPos(in.getTargetCoordinate().get()), SoundEvents.ENTITY_GENERIC_EXPLODE, true);
		BlockPos pos = in.getTargetCoordinate().get();
		IBlockState state = in.getWorld().getBlockState(pos);
		Block block = state.getBlock();
		if(block!=Blocks.AIR){
			in.spawnParticle(MovingType.WAVE, new XYZPos(pos.up()), EnumParticleTypes.BLOCK_DUST, 10, 1.2D,Block.getStateId(state));
		}
		return EnumActionResult.PASS;
	};

	/** 独妙点穴*/
	public static TechActionBase acupuncture(){
		TechActionCharged<TechActionMelee> chargedAc = TechActionCharged.simpleMelee(1,0,General.SPEAR,General.PUNCH,General.SWORD).setChargeThreshold(30);
		chargedAc.getAction().setAdditionalBehavior((context,target)->{
			context.playSound(XYZPos.createFrom(target), SoundEvents.ENTITY_IRONGOLEM_HURT, false);
			context.spawnParticle(MovingType.DIVERGE, target, EnumParticleTypes.PORTAL, 25,1.0D);
			target.addPotionEffect(new PotionEffect(UnsagaPotions.AWKWARD_DEX,1,ItemUtil.getPotionTime(30)));
		});
		TechActionBase base = TechActionBase.create(InvokeType.CHARGE).addAction(chargedAc);
		return base;
	}
	public static TechActionBase airThrow(){
		return throwAction(invoker -> {
			UnsagaMod.logger.trace(TechActionBase.class.getName(),invoker.getPerformer().rotationPitch);
			Vec3d vec = invoker.getPerformer().getLookVec().scale(2.5D);
			invoker.getTarget().get().addVelocity(vec.x, 0.5D, vec.z);
		},3,false);
	}
	/** 光の腕*/
	public static TechActionBase armOfLight(){
		return TechActionBase.create(base ->{
			base.addAction(SWINGSOUND_NOPARTICLE);
			base.addAction(TechActionFactory::armOfLightAction);
		},InvokeType.RIGHTCLICK);

	}

	private static EnumActionResult armOfLightAction(TechInvoker t){
		//			if(t.getTarget().isPresent()){
		EntityBeam beam = new EntityBeam(t.getWorld());
		XYZPos pos = XYZPos.createFrom(t.getPerformer());
		t.playSound(pos, SoundEvents.ENTITY_ENDERMEN_TELEPORT, false);
		beam.setPositionAndRotation(pos.dx, pos.dy, pos.dz,t.getPerformer().rotationYaw,t.getPerformer().rotationPitch);
		beam.setOwner(t.getPerformer());
		beam.setDamage(t.getActionStrength());
		//					beam.setTarget(t.getTarget().get());
		if(WorldHelper.isServer(t.getWorld())){
			WorldHelper.safeSpawn(t.getWorld(), beam);
		}
		return EnumActionResult.SUCCESS;
	}
	public static TechActionBase arrowRain(){
		TechActionBase arrowRain = new TechActionBase(InvokeType.BOW);
		IAction<TechInvoker> action = new TechActionAsync(MovingStates.ARROW_RAIN, 40);
		arrowRain.addAction(new ActionTargettable(action));
		return arrowRain;
	}

	/** 電光石火*/
	public static TechActionBase blitz(){
		TechActionBase blitz = new TechActionBase(InvokeType.RIGHTCLICK);
		blitz.addAction(new TechActionAsync(MovingStates.BLITZ, 4)
				.setStartSound(SoundEvents.ENTITY_GHAST_SHOOT, 1.0F));
		return blitz;
	}


	public static TechActionBase caleidoscope(){
		return TechActionBase.create(base ->{
			TechActionMeleeGhost melee = new TechActionMeleeGhost(2,General.SWORD);
			melee.setGhostDegrees(35.0F,-35.0F,70.0F,-70.0F,105.0F,-105.0F);
			melee.setAttackCount(7);
			melee.setStatusEffect(Pair.of(MobEffects.SLOWNESS, 10));
			base.addAction(melee);
		},InvokeType.RIGHTCLICK);
	}

	public static TechActionBase callBack(){
		return throwAction(invoker -> {
			Vec3d vec = invoker.getPerformer().getLookVec().scale(2.5D);
			invoker.getTarget().get().addVelocity(-vec.x, 1.4D, -vec.z);
		},5,false);
	}

	/** 追突剣*/
	public static TechActionBase chargeBlade(){
		TechActionBase chargeBladeAction = new TechActionBase(InvokeType.SPRINT_RIGHTCLICK);
		chargeBladeAction.addAction(t ->{
			t.swingMainHand(true, true);
			boolean flag = RangedHelper.<TechInvoker>create(t.getWorld(), t.getPerformer(), 4.0D).setConsumer((self,target)->{
				//				UnsagaMod.logger.trace("damage", target);
				target.attackEntityFrom(DamageHelper.register(new AdditionalDamage(t,General.PUNCH,General.SWORD)), t.getStrengthHP());
				VecUtil.knockback(t.getPerformer(), target, 1.0D, 0.5D);
				UnsagaPotionInitializer.setStunned(target,ItemUtil.getPotionTime(3),0);
				SoundAndSFX.playSound(t.getWorld(), XYZPos.createFrom(target), SoundEvents.ENTITY_IRONGOLEM_HURT, SoundCategory.HOSTILE, 1.0F, 1.0F, false);
			}).invoke();
			PotionEffect state = StateMovingTech.create(15, t,MovingStates.VELOCITY_ZERO.consumer);
			StateCapability.getCapability(t.getPerformer()).addState(state);
			return flag ? EnumActionResult.SUCCESS : EnumActionResult.FAIL;
		});
		return chargeBladeAction;
	}

	/** 削岩撃*/
	public static TechActionBase connectedBlocksCrasher(){
		return TechActionFactoryHelper.buildMaceTech(in ->{
			in.setAdditionalBehavior((context,target)->{
				context.playSound(XYZPos.createFrom(target), SoundEvents.ENTITY_IRONGOLEM_HURT, false);
				target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS,ItemUtil.getPotionTime(30),0));
			});
		}, new ActionAsyncEvent().setEventFactory(new AsyncTechEvents.CrashConnected("pickaxe")));
	}

	public static TechActionBase cutIn(){
		TechActionBase blitz = new TechActionBase(InvokeType.RIGHTCLICK);
		ActionList<TechInvoker> list = new ActionList();
		TechActionAsync async = new TechActionAsync(MovingStates.CUT_IN, 4)
				.setStartSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F);
		list.addAction(in ->{
			in.getPerformer().addVelocity(0, 0.5D, 0);
			return EnumActionResult.PASS;
		});
		list.addAction(async);
		blitz.addAction(new ActionTargettable(list));

		return blitz;
	}
	//	public static TechActionBase bloodyMary(){
	//		TechActionBase base = new TechActionBase(InvokeType.RIGHTCLICK);
	//
	//		IAction<TechInvoker> action = new ActionMelee(General.SPEAR)
	//				.setAdditionalBehavior((context,target)->{
	//					context.playSound(XYZPos.createFrom(target), SoundEvents.ENTITY_IRONGOLEM_HURT, false,0.5F);
	//					context.spawnParticle(MovingType.FOUNTAIN, new XYZPos(target.getPosition().up()), EnumParticleTypes.REDSTONE, 15, 0.1D);
	//					LivingHelper.getCapability(context.getPerformer()).addState(new StateCombo.Effect(0,4,context.getStrength(),EnumParticleTypes.REDSTONE,context.getArtifact().isPresent() ? context.getArtifact().get() : null));
	//					LivingHelper.getCapability(target).addState(new PotionEffect(UnsagaPotions.HURT,ItemUtil.getPotionTime(2),0));
	//				});
	//		base.addAction(action);
	//		return base;
	//	}
	public static TechActionBase cyclone(){
		return throwAction(TechActionFactory::cycloneAction,2,true);
	}

	private static void cycloneAction(TechInvoker invoker){
		invoker.getTarget().ifPresent(in ->{
			BlockPos down =in.getPosition().down();
			if(invoker.getWorld().getBlockState(down).getBlock()!=Blocks.AIR){
				Block block = invoker.getWorld().getBlockState(down).getBlock();
				invoker.spawnParticle(MovingType.WAVE, XYZPos.createFrom(in), EnumParticleTypes.BLOCK_DUST, 30, 0.03D,Block.getIdFromBlock(block));
			}
			Vec3d vec = invoker.getPerformer().getLookVec();
			in.addVelocity(0, 3.0D, 0);
		});

	}

	/** 土竜撃*/
	public static TechActionBase earthDragon(){
		RangedAttackSetting<TechInvoker> setting = RangedAttackSetting.tech()
				.damageType(General.PUNCH)
				.boundingBox(in -> HSLibs.getBounding(in.getTargetCoordinate().get(), 4.0D, 3.0D))
				.consumer((self,target)->{
					VecUtil.knockback(self.getPerformer(), target, 1.0D, 0.2D);
					UnsagaPotionInitializer.setStunned(target, ItemUtil.getPotionTime(3), 0);
				})
				.selector((self,target)->target.onGround);

		ActionRangedAttack<TechInvoker> rangedAttackAction = new ActionRangedAttack<TechInvoker>(setting);
		TechActionBase base = new TechActionBase(InvokeType.USE)
				.addAction(SWINGSOUND_NOPARTICLE)
				.addAction(WAVE_PARTICLE)
				.addAction(rangedAttackAction);
		return base;
	}

	/** 薪割りダイナマイト。ジャンプ必須。
	 * 得られる枝は一個多い。*/
	public static TechActionBase firewoodChopper(){
		TechActionMelee melee = new TechActionMelee(General.PUNCH,General.SWORD)
				.setDamageDelegate(TechActionFactoryHelper::woodPlantSlayer);

		ActionWorld<TechInvoker> actionWorld = new ActionWorld<TechInvoker>(1, 1)
				.setWorldConsumer(TechActionFactoryHelper::woodChopperConsumer);
		TechActionSelectorInvokeType selector = new TechActionSelectorInvokeType();
		selector.addAction(InvokeType.RIGHTCLICK, melee);
		selector.addAction(InvokeType.USE, actionWorld);
		ActionList actionList = new ActionList();
		actionList.addAction(SWINGSOUND_NOPARTICLE);
		actionList.addAction(selector);
		return TechActionBase.create(in ->in.addAction(new ActionRequireJump(actionList))
				,InvokeType.RIGHTCLICK,InvokeType.USE);
	}
	public static TechActionBase flyingKnee(){
		TechActionBase kick = new TechActionBase(InvokeType.RIGHTCLICK);
		IAction<TechInvoker> action = new TechActionAsync(MovingStates.FLYING_KNEE, MovingStates.FLYING_KNEE_DUR)
				.setStartSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F);
		kick.addAction(new ActionRequireJump(action));
		return kick;
	}
	/** 富嶽八景*/
	public static TechActionBase fujiView(){
		TechActionBase base = new TechActionBase(InvokeType.CHARGE);
		TechActionMelee melee = new TechActionMelee(3,General.PUNCH,General.SWORD)
				.setAdditionalBehavior(TechActionFactoryHelper::fujiViewConsumer);
		base.addAction(SWINGSOUND_NOPARTICLE);
		base.addAction(new TechActionCharged(melee));
		return base;
	}

	/** フルフラット*/
	public static TechActionBase fullFlat(){
		return TechActionFactoryHelper.buildMaceTech(in ->{
			in.setAdditionalBehavior((context,target)->{
				context.playSound(XYZPos.createFrom(target), SoundEvents.ENTITY_IRONGOLEM_HURT, false);
				UnsagaPotionInitializer.setStunned(target, 5, 0);
			});
		}, new ActionAsyncEvent().setEventFactory(new AsyncTechEvents.Flatter()));
	}
	/** ドラ鳴らし*/
	public static TechActionBase gonger(){
		TechActionBase base = new TechActionBase(InvokeType.RIGHTCLICK,InvokeType.USE);
		TechActionMelee melee = new TechActionMelee(General.PUNCH);
		melee.setAdditionalBehavior((context,target)->context.playSound(XYZPos.createFrom(target)
				, SoundEvents.ENTITY_IRONGOLEM_HURT, false));
		ActionList listAction = new ActionList();
		listAction.addAction(TechActionFactory.WAVE_PARTICLE);
		listAction.addAction(new ActionWorld.AirRoomDetector());
		base.addAction(new TechActionSelectorInvokeType()
				.addAction(InvokeType.RIGHTCLICK, melee)
				.addAction(InvokeType.USE, listAction));
		return base;
	}

	public static TechActionBase grandslam(){
		RangedAttackSetting<TechInvoker> setting = RangedAttackSetting.tech()
				.damageType(General.PUNCH)
				.boundingBox(in -> HSLibs.getBounding(in.getTargetCoordinate().get(), 9.0D, 5.0D))
				.attackable(false)
				.selector((self,target)->target.onGround)
				.consumer((self,target)->{
					StateCapability.getCapability(target).addState(new PotionEffect(UnsagaPotions.DELAYED_EXPLODE,ItemUtil.getPotionTime(6)));
					self.spawnParticle(MovingType.FLOATING, target, EnumParticleTypes.EXPLOSION_NORMAL, 3, 0.2D);
					VecUtil.knockback(self.getPerformer(), target, 2.0D, 1.0D);
				});
		ActionRangedAttack<TechInvoker> rangedAction = new ActionRangedAttack(setting);
		TechActionBase grandSlamBase = new TechActionBase(InvokeType.USE)
				.addAction(SWINGSOUND_NOPARTICLE)
				.addAction(WAVE_PARTICLE)
				.addAction(rangedAction);
		return grandSlamBase;
	}

	/** 草伏せ*/
	public static TechActionBase grassHopper(){
		RangedAttackSetting<TechInvoker> setting = RangedAttackSetting.tech()
				.damageType(General.SWORD,General.SPEAR)
				.boundingBoxes(new RangedBoundingBoxFactory.RangeSwing(4))
				.consumer((self,target)->UnsagaPotionInitializer.setStunned(target, ItemUtil.getPotionTime(3), 0))
				.onlyGround();
		TechActionBase base = new TechActionBase(InvokeType.RIGHTCLICK,InvokeType.USE);
		ActionRangedAttack<TechInvoker> rangedAction = new ActionRangedAttack(setting);
		ActionWorld worldAction = new ActionWorld(2,1);
		worldAction.setWorldConsumer(new ActionWorld.WeedCutter());
		base.addAction(SWINGSOUND_AND_PARTICLE);
		base.addAction(new TechActionSelectorInvokeType()
				.addAction(InvokeType.RIGHTCLICK, rangedAction) //空中なら足払い
				.addAction(InvokeType.USE, worldAction)); //ブロックに対してなら草刈り
		return base;
	}

	/** 逆風の太刀*/
	public static TechActionBase gust(){
		TechActionBase gustBase = new TechActionBase(InvokeType.CHARGE);
		TechActionAsync ev = new TechActionAsync(MovingStates.GUST,12).setStartSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 0.5F);

		gustBase.addAction(new TechActionCharged(ev).setChargeThreshold(10));
		return gustBase;
	}

	/** ホークブレード*/
	public static TechActionBase hawkBlade(){
		TechActionBase hawkBlade = new TechActionBase(InvokeType.RIGHTCLICK);
		hawkBlade.addAction(in -> !in.getPerformer().onGround ? EnumActionResult.SUCCESS : EnumActionResult.PASS);
		hawkBlade.addAction(new TechActionAsync(MovingStates.HAWK_BLADE,ItemUtil.getPotionTime(3)).setStartSound(SoundEvents.ENTITY_WITHER_SHOOT, 0.5F));
		return hawkBlade;
	}
	public static TechActionBase javelin(){
		return TechActionFactoryHelper.createProjectile(EntityJavelin::javelin, 20);
	}

	public static TechActionBase kick(){
		TechActionBase kick = new TechActionBase(InvokeType.RIGHTCLICK);
		IAction<TechInvoker> action = new TechActionAsync(MovingStates.SINKER, 10)
				.setStartSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 1.0F);
		kick.addAction(new ActionRequireJump(action));
		return kick;
	}
	public static TechActionBase knifeThrow(){
		return TechActionFactoryHelper.createProjectile(EntityThrowingKnife::knifeThrow, 10);
	}
	public static TechActionBase lightningThrust(){
		TechActionBase base = new TechActionBase(InvokeType.RIGHTCLICK);
		TechActionMelee meleeLightningThrust = new TechActionMelee(General.SPEAR)
				.setAdditionalBehavior((context,target)->{
					UnsagaPotionInitializer.setStunned(target, ItemUtil.getPotionTime(20), 0); //長めのスタン
					UnsagaMod.PACKET_DISPATCHER.sendToAllAround(PacketClientThunder.create(XYZPos.createFrom(target)), PacketUtil.getTargetPointNear(target));
					context.playSound(XYZPos.createFrom(target), SoundEvents.ENTITY_LIGHTNING_THUNDER, false);
				}).setSubAttributes(EnumSet.of(Sub.ELECTRIC));
		base.addAction(meleeLightningThrust);
		return base;
	}
	public static TechActionBase multiWay(){
		TechActionBase base = new TechActionBase(InvokeType.RIGHTCLICK);
		TechActionMeleeGhost melee = new TechActionMeleeGhost(1,General.SWORD);
		melee.setGhostDegrees(35.0F,-35.0F);
		melee.setAttackCount(3);
		base.addAction(melee);
		return base;
	}
	/** クイックチェッカー、溜めいらずの速射*/
	public static TechActionBase quickChecker(){
		TechActionBase base = new TechActionBase(InvokeType.RIGHTCLICK);
		base.addAction(TechActionFactory::quickCheckerAction);
		return base;
	}

	private static EnumActionResult quickCheckerAction(TechInvoker t){
		return t.getArtifact()
				.filter(in ->in.getItem() instanceof ItemBow)
				.map(in ->{
					ItemBow bow = (ItemBow)in.getItem();
					bow.onPlayerStoppedUsing(in, t.getWorld(), t.getPerformer(), 71400);
					return EnumActionResult.SUCCESS;
				}).orElse(EnumActionResult.PASS);
	}
	public static TechActionBase raksha(){
		return TechActionBase.create(in ->{
			TechActionMelee action = new TechActionMelee(General.PUNCH);
			action.setAdditionalBehavior(TechActionFactory::rakshaConsumer);
			in.addAction(action);
		},InvokeType.RIGHTCLICK);
	}

	private static void rakshaConsumer(TechInvoker in,EntityLivingBase target){
		XYZPos p = XYZPos.createFrom(target);
		WorldHelper.createExplosionSafe(in.getWorld(), in.getPerformer(), p, 1.0F, true);
		Vec3d vec = in.getPerformer().getLookVec().scale(2.5D);
		target.addVelocity(vec.x, 0.5D, vec.z);
		StateCapability.getCapability(target).addState(new StateGettingThrown.Effect(ItemUtil.getPotionTime(10), in.getPerformer(),false));
	}

	private static void reverseDeltaConsumer(TechInvoker invoker,EntityLivingBase target){
		invoker.playSound(XYZPos.createFrom(invoker.getPerformer()), SoundEvents.ENTITY_WITHER_SHOOT, false);
		BlockPos origin = invoker.getPerformer().getPosition();
		Vec3d forward = invoker.getPerformer().getLookVec().normalize().scale(3.0D);
		Vec3d ghost1 = forward.rotateYaw((float) Math.toRadians(-30.0F));
		Vec3d ghost2 = forward.rotateYaw((float) Math.toRadians(30.0F));
		BlockPos ghost1Pos = origin.add(ghost1.x,0,ghost1.z);
		BlockPos ghost2Pos = origin.add(ghost2.x,0,ghost2.z);
		Vec3d ghost1Move = forward.rotateYaw((float) Math.toRadians(120.0F)).scale(0.2F);
		Vec3d ghost2Move = ghost1Move.rotateYaw((float) Math.toRadians(120.0F));


		BlockPos pos = target.getPosition();
		TechActionFactoryHelper.copyGhost(invoker, pos, ghost1Move, invoker.getPerformer().rotationYaw+90.0F,target,General.PUNCH,General.SWORD);
		//			TechActionFactoryHelper.copyGhost(invoker, ghost2Pos, ghost2Move, invoker.getPerformer().rotationYaw-90.0F);
		//			TechActionFactoryHelper.copyGhost(invoker, invoker.getPerformer().getPosition(), ghost1.scale(0.2F), invoker.getPerformer().rotationYaw);

		Vec3d move = ghost1.scale(0.5F);
		//			invoker.getPerformer().setVelocity(move.x,0,move.z);

	}
	public static TechActionBase reverseDelta(){
		TechActionMelee melee = new TechActionMelee(General.PUNCH,General.SWORD){
			@Override
			public boolean isThroughAttack() {
				// TODO 自動生成されたメソッド・スタブ
				return true;
			}
		};
		melee.setAdditionalBehavior(TechActionFactory::reverseDeltaConsumer);
		return TechActionBase.create(in -> in.addAction(melee), InvokeType.RIGHTCLICK);
	}



	/** 粉砕撃*/
	public static TechActionBase rockCrasher(){
		return TechActionFactoryHelper.buildMaceTech(in ->{
			in.setAdditionalBehavior((context,target)->{
				context.playSound(XYZPos.createFrom(target), SoundEvents.ENTITY_IRONGOLEM_HURT, false);
				target.addPotionEffect(new PotionEffect(UnsagaPotions.POOR_VITALITY,ItemUtil.getPotionTime(30),0));
			});
		}, new ActionAsyncEvent().setEventFactory(new AsyncTechEvents.RangedCrasher()));

	}
	/** 乱れ雪月花*/
	public static TechActionBase scatteredPetals(){
		TechActionBase base = new TechActionScatteredPetals(InvokeType.RIGHTCLICK_TO_CHARGE);
		return base;
	}

	public static TechActionBase waterMoon(){
		TechActionBase base = new TechActionWaterMoon(InvokeType.RIGHTCLICK_TO_CHARGE);
		return base;
	}

	/**スウォーム */
	public static TechActionBase swarm(){
		TechActionBase base = new TechActionBase(InvokeType.RIGHTCLICK);
		ActionSummon<TechInvoker> summon = new ActionSummon<TechInvoker>(in -> new EntitySwarm(in.getWorld())){
			@Override
			public BlockPos getSummonPosition(TechInvoker ctx){
				return XYZPos.createFrom(ctx.getPerformer()).addGaussian(ctx.getWorld().rand,true);
			}

			@Override
			public int getSummonAmount(){
				return 8;
			}
		};
		base.addAction(SWINGSOUND_NOPARTICLE);
		base.addAction(summon);
		return base;
	}
	public static TechActionBase skullCrasher(){
		TechActionBase skullCrashBase = new TechActionBase(InvokeType.RIGHTCLICK);
		TechActionMelee melee = new TechActionMelee(General.PUNCH)
				.setDamageDelegate(in -> (in.getMiddle() instanceof EntitySkeleton) ? DamageComponent.of(in.getRight().hp() * 1.5F, in.getRight().lp()) : in.getRight())
				.setAdditionalBehavior((context,target)->{
					context.playSound(XYZPos.createFrom(target), SoundEvents.ENTITY_IRONGOLEM_HURT, false);
					target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS,ItemUtil.getPotionTime(30),0));
				});
		skullCrashBase.addAction(melee);
		return skullCrashBase;
	}

	public static TechActionBase skyDrive(){
		TechActionBase skyDriveBase = TechActionBase.create(InvokeType.CHARGE);
		ActionProjectile<TechInvoker> projectileSkyDrive = new ActionProjectile<TechInvoker>()
				.setProjectileFunction(EntityFlyingAxe::skyDrive)
				.setShootSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP);
		skyDriveBase.addAction(new TechActionCharged(new ActionTargettable(projectileSkyDrive)).setChargeThreshold(20));
		return skyDriveBase;
	}
	public static TechActionBase slashOut(){
		TechActionBase base = new TechActionBase(InvokeType.RIGHTCLICK);
		TechActionMelee melee = new TechActionMelee(General.SPEAR,General.SWORD){
			@Override
			public int getMaxAttackableEnemy(){
				return 3;
			}
		};
		melee.setAdditionalBehavior((ctx,in)->UnsagaPotionInitializer.setStunned(in, 3, 0));
		base.addAction(melee);
		return base;
	}

	/** しびれ突き*/
	public static TechActionBase stunner(){
		TechActionBase stunnerBase = new TechActionBase(InvokeType.RIGHTCLICK)
				.addAction(new TechActionMelee(General.SPEAR)
						.setAdditionalBehavior((context,target)->{
							context.spawnParticle(MovingType.DIVERGE, target, EnumParticleTypes.CRIT_MAGIC, 10, 1D);
							target.addPotionEffect(new PotionEffect(UnsagaPotions.STUN,ItemUtil.getPotionTime(10),0));
						})
						);
		return stunnerBase;
	}

	public static TechActionBase swing(){
		RangedAttackSetting<TechInvoker> setting = RangedAttackSetting.tech()
				.damageType(General.SWORD,General.SPEAR)
				.boundingBoxes(new RangedBoundingBoxFactory.RangeSwing(4));
		TechActionBase swingBase = new TechActionBase(InvokeType.RIGHTCLICK)
				.addAction(SWINGSOUND_AND_PARTICLE)
				.addAction(new ActionRangedAttack(setting));
		return swingBase;
	}

	//	public static TechActionBase waterfall(){
	//		TechActionBase base = new TechActionBase(InvokeType.RIGHTCLICK,InvokeType.USE);
	//		TechActionMeleeMultiple action = new TechActionMeleeMultiple(General.PUNCH){
	//
	//			@Override
	//			public int getMultipleMeleeNumber() {
	//				// TODO 自動生成されたメソッド・スタブ
	//				return 5;
	//			}
	//
	//			@Override
	//			public void attack(EntityLivingBase ev,TechInvoker invoker) {
	//				invoker.playSound(XYZPos.createFrom(ev), SoundEvents.ENTITY_IRONGOLEM_HURT, false);
	//				ev.attackEntityFrom(DamageSourceUnsaga.create(invoker.getPerformer(), invoker.getStrength().lp().amount(), General.PUNCH).setModified(true), invoker.getStrengthHP());
	//				ev.setVelocity(0, 0, 0);
	//				invoker.getPerformer().addVelocity(0, 1, 0);
	//				ev.addVelocity(0, 1, 0);
	//			}
	//
	//			@Override
	//			public int getMultipleAttackAllowingTime() {
	//				// TODO 自動生成されたメソッド・スタブ
	//				return 100;
	//			}
	//
	//
	//		};
	//		base.addAction(action);
	//		return base;
	//	}
	public static TechActionBase throwAction(Consumer<TechInvoker> consumer,int range,boolean isCyclone){
		TechActionBase base = new TechActionBase(InvokeType.RIGHTCLICK);
		IAction<TechInvoker> action = t -> {
			t.swingMainHand(true, false);

			List<EntityLivingBase> list = VecUtil.getEntitiesStraight(t.getPerformer(), t.getPerformer().getEntityBoundingBox().grow(1.0D), range);
			if(!list.isEmpty()){
				t.setTarget(list.get(0));
			}


			if(t.getTarget().isPresent()){
				consumer.accept(t);

				EntityLivingBase target = t.getTarget().get();
				//				Vec3d vec = t.getPerformer().getLookVec();
				//				target.addVelocity(vec.x, 1.0D, vec.z);
				StateCapability.getCapability(target).addState(new StateGettingThrown.Effect(ItemUtil.getPotionTime(10), t.getPerformer(),isCyclone));
				return EnumActionResult.SUCCESS;
			}
			return EnumActionResult.PASS;
		};
		base.addAction(action);
		return base;
	}


	//	public static TechActionBase tomahawk(){
	//		TechActionBase tomahawkBase = TechActionBase.create(InvokeType.CHARGE);
	//		ActionProjectile<TechInvoker> projectileTomahawk = new ActionProjectile<TechInvoker>().setProjectileFunction((context,target)->{
	//			EntityFlyingAxe axe = new EntityFlyingAxe(context.getWorld(),context.getPerformer(),context.getArticle().get().copy());
	//			axe.setDamage(context.getStrengthHP());
	//			context.getPerformer().setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
	//			XYZPos pos = XYZPos.createFrom(context.getPerformer());
	//			axe.shoot(context.getPerformer(), context.getPerformer().rotationPitch, context.getPerformer().rotationYaw, 0, 2.0F, 1.0F);
	//
	//			return axe;
	//		}).setShootSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP);
	//		tomahawkBase.addAction(new ActionCharged(projectileTomahawk).setChargeThreshold(20));
	//		return tomahawkBase;
	//	}
	public static TechActionBase tomahawk(){
		return TechActionFactoryHelper.createProjectile(EntityFlyingAxe::tomahawk, 20);
	}

	private static EnumActionResult triangleKickAction(TechInvoker t){
		return t.getTargetCoordinate()
				.map(pos ->{
					BlockPos epos = t.getPerformer().getPosition();
					double dis = pos.getDistance(epos.getX(),epos.getY(),epos.getZ());
					if(dis<=3.0D){
						//				MovingAttack m = MovingAttack.builder().setCancelFall().setExpire(15).setRange(0.5D).setDamage(DamageSourceUnsaga.create(t.getPerformer(), t.getStrength().lp().amount(), General.PUNCH), t.getStrength().hp())
						//						.build(t.getPerformer());
						//				HSLib.addAsyncEvent(t.getPerformer(), m);
						t.playSound(XYZPos.createFrom(t.getPerformer()), SoundEvents.ENTITY_SHEEP_STEP, false);

						if(t.getWorld().isSideSolid(pos,t.getUseFacing())){
							EntityLivingBase p = t.getPerformer();
							StateCapability.ADAPTER.getCapability(p).addState(new PotionEffect(UnsagaPotions.CANCEL_FALL,ItemUtil.getPotionTime(10)));
							Vec3d vec = t.getPerformer().getLookVec().scale(0.2F);
							t.getPerformer().addVelocity(-p.motionX, 0, -p.motionZ);
							t.getPerformer().addVelocity(-vec.x, 0, -vec.z);
							t.getPerformer().setVelocity(t.getPerformer().motionX, 0.65D, t.getPerformer().motionZ);
							return EnumActionResult.SUCCESS;
						}
					}
					return EnumActionResult.PASS;
				}).orElse(EnumActionResult.PASS);
	}
	public static TechActionBase triangleKick(){
		return TechActionBase.create(in ->in.addAction(TechActionFactory::triangleKickAction),InvokeType.USE);
	}
	//	public static TechActionBase flyingKnee(){
	//		TechActionBase base = new TechActionBase(InvokeType.RIGHTCLICK);
	//		IAction<TechInvoker> action = new IAction<TechInvoker>(){
	//
	//			@Override
	//			public EnumActionResult apply(TechInvoker t) {
	//				t.playSound(XYZPos.createFrom(t.getPerformer()), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, false);
	//				Vec3d vec = t.getPerformer().getLookVec();
	//				t.getPerformer().addVelocity(vec.x, 1.0D, vec.z);
	//				//				MovingAttack m = MovingAttack.builder()
	//				//						.setStopOnHit(true).setDamageComsumer((self,target) ->{
	//				//							t.playSound(XYZPos.createFrom(t.getPerformer()), SoundEvents.ENTITY_IRONGOLEM_HURT, false);
	//				//							VecUtil.knockback(t.getPerformer(), target, 1.0F, 0.2D);
	//				//
	//				//						})
	//				//						.setCancelFall()
	//				//						.setExpire(15).setRange(1.0D)
	//				//						.setDamage(DamageSourceUnsaga.create(t.getPerformer(), t.getModifiedStrength().lp(),General.PUNCH), t.getModifiedStrength().hp())
	//				//						.build(t.getPerformer());
	//				PotionEffect state = StateMovingTech.create(15, t,MovingStates.FLYING_KNEE.consumer);
	//				//				PotionMovingState.Data data = PotionMovingState.getData(t.getPerformer());
	//				//				data.setAdditional(new AdditionalDamageData(t,General.PUNCH));
	//				//				data.setDamage(t.getStrength().hp());
	//				//				data.setStopOnHit(true);
	//				//				data.setVelocity(vec.x, 1.0D, vec.z);
	//				//				data.setDamageConsumer((self,target) ->{
	//				//							t.playSound(XYZPos.createFrom(t.getPerformer()), SoundEvents.ENTITY_IRONGOLEM_HURT, false);
	//				//							VecUtil.knockback(t.getPerformer(), target, 1.0F, 0.2D);
	//				//						});
	//				//				data.setStopOnLanded(true);
	//				t.getPerformer().addPotionEffect(state);
	//				//				t.getPerformer().addPotionEffect(new PotionEffect(UnsagaPotions.CANCEL_FALL,15));
	//				//				HSLib.addAsyncEvent(t.getPerformer(), m);
	//				return EnumActionResult.SUCCESS;
	//			}
	//
	//		};
	//		base.addAction(action);
	//		return base;
	//	}


	public static TechActionBase vandalize(){
		TechActionBase base = TechActionBase.create(InvokeType.CHARGE);
		//		IAction<TechInvoker> explode = context ->{
		//			BlockPos pos = context.getTargetCoordinate().get();
		//			context.getWorld().createExplosion(context.getPerformer(), pos.getX(), pos.getY(), pos.getZ(), 3, false);
		//			return EnumActionResult.SUCCESS;
		//		};
		TechActionMelee meleeVandalize = new TechActionMelee(General.SWORD).setAdditionalBehavior((context,target)->{
			XYZPos pos = XYZPos.createFrom(target);
			StateCapability.getCapability(target).addState(new PotionEffect(UnsagaPotions.PARTICLE,ItemUtil.getPotionTime(2)));
			//			explode.apply(context);
			WorldHelper.createExplosionSafe(context.getWorld(), context.getPerformer(), pos, 3, true);
			if(WorldHelper.isClient(context.getWorld())){
				context.getWorld().spawnEntity(EntityEffectSpawner.create(target, 30, 3.0D));
			}
		});
		//		TechActionSelectorInvokeType selector = new TechActionSelectorInvokeType();
		//		selector.addAction(InvokeType.CHARGE, new TechActionCharged(meleeVandalize).setChargeThreshold(25));
		//		selector.addAction(InvokeType.USE, explode);
		base.addAction(new TechActionCharged(meleeVandalize).setChargeThreshold(25));
		return base;
	}

	//	public static TechActionBase kickOld(){
	//		TechActionBase base = new TechActionBase(InvokeType.RIGHTCLICK);
	//		IAction<TechInvoker> action = new IAction<TechInvoker>(){
	//
	//			@Override
	//			public EnumActionResult apply(TechInvoker t) {
	//				t.playSound(XYZPos.createFrom(t.getPerformer()), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, false);
	//				Vec3d vec = t.getPerformer().getLookVec();
	//				PotionEffect state = StateMovingTech.create(20, t,MovingStates.SINKER.consumer);
	//				//				PotionMovingState.Data data = PotionMovingState.getData(t.getPerformer());
	//				//				data.setStopOnLanded(true);
	//				//				data.setStopOnHit(true);
	//				//				data.setAdditional(new AdditionalDamageData(t,General.PUNCH));
	//				//				data.setDamage(t.getStrength().hp());
	//				//				MovingAttack m = MovingAttack.builder().setVelX(vec.x).setVelZ(vec.z).setVelY(-1.0D)
	//				//						.setStopOnHit(true).setDamageComsumer((self,target) ->{
	//				//							if(t.getActionProperty()==TechRegistry.instance().THUNDER_KICK){
	//				//								t.playSound(XYZPos.createFrom(t.getPerformer()), SoundEvents.ENTITY_LIGHTNING_IMPACT, false);
	//				//								VecUtil.knockback(t.getPerformer(), target, 1.0F, 0.2D);
	//				//							}else{
	//				//								t.playSound(XYZPos.createFrom(t.getPerformer()), SoundEvents.ENTITY_IRONGOLEM_HURT, false);
	//				//								target.addPotionEffect(new PotionEffect(MobEffects.WEAKNESS,ItemUtil.getPotionTime(12)));
	//				//							}
	//				//
	//				//							self.getParent().getSender().addVelocity(-vec.x, 0.5D, -vec.z);
	//				//						})
	//				//						.setExpire(100).setRange(1.0D)
	//				//						.setDamage(DamageSourceUnsaga.create(t.getPerformer(), t.getStrength().lp().strength(),General.PUNCH), t.getStrength().hp())
	//				//						.build(t.getPerformer());
	//				//				m.setStopOnLanded(true);
	//				//				HSLib.addAsyncEvent(t.getPerformer(), m);
	////				t.getPerformer().addPotionEffect(state);
	//				LivingCapability.getCapability(t.getPerformer()).addState(state);
	//				return EnumActionResult.SUCCESS;
	//			}
	//
	//		};
	//		base.addAction(action);
	//		return base;
	//	}

	public static TechActionBase thunderBolt(){
		TechActionBase base = new TechActionBase(InvokeType.RIGHTCLICK);
		ActionThunder<TechInvoker> thunder = new ActionThunder(ActionThunder.Type.NORMAL);
		base.addAction(new ActionTargettable(thunder));
		return base;
	}
	public static TechActionBase finalStrike(){
		RangedAttackSetting<TechInvoker> setting = RangedAttackSetting.tech()
				.attackable(true)
				.damageType(General.MAGIC)
				.subType(Sub.FIRE,Sub.FREEZE,Sub.SHOCK)
				.blockable(false)
				.boundingBox(in -> in.getPerformer().getEntityBoundingBox().grow(5.0D));
		ActionRangedAttack<TechInvoker> rangedAttackAction = new ActionRangedAttack<TechInvoker>(setting);

		return TechActionBase.create(base ->{
			ActionList<TechInvoker> list = new ActionList();
			list.addAction(TechActionFactory::finalStrikeAction);
			list.addAction(rangedAttackAction);
			base.addAction(list);
		},InvokeType.RIGHTCLICK);
	}

	private static EnumActionResult finalStrikeAction(TechInvoker t){
		WorldHelper.createExplosionSafe(t.getWorld(), t.getPerformer(), XYZPos.createFrom(t.getPerformer()), 6, true);
		t.getPerformer().setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
		return EnumActionResult.SUCCESS;
	}
	public static TechActionBase featherSeal(){
		TechActionBase base = new TechActionBase(InvokeType.RIGHTCLICK);
		ActionList<TechInvoker> list = new ActionList();
		TechActionBuff buff = new TechActionBuff(1,false,ImmutableList.of(UnsagaPotions.SILENT_MOVE));
		list.addAction(new ActionSound<TechInvoker>(in ->XYZPos.createFrom(in.getPerformer()),SoundEvents.ENTITY_PLAYER_LEVELUP,false));
		list.addAction(buff);
		base.addAction(new ActionSelf(list));
		return base;
	}

	public static TechActionBase bless(){
		TechActionBase base = new TechActionBase(InvokeType.RIGHTCLICK);
		ActionList<TechInvoker> list = new ActionList();
		TechActionBuff buff = new TechActionBuff(0,false,Lists.newArrayList(MobEffects.STRENGTH));
		list.addAction(new ActionSound<TechInvoker>(in ->XYZPos.createFrom(in.getPerformer()),SoundEvents.ENTITY_PLAYER_LEVELUP,false));
		list.addAction(buff);
		base.addAction(new ActionSelf(list));
		return base;
	}
	/** 大木断*/
	public static TechActionBase woodChopper(){
		return TechActionBase.create(base ->{
			TechActionMelee melee =
					new TechActionMelee(General.PUNCH,General.SWORD)
					.setDamageDelegate(TechActionFactoryHelper::woodPlantSlayer);
			melee.setAdditionalBehavior((context,target)->context.playSound(XYZPos.createFrom(target), SoundEvents.ENTITY_IRONGOLEM_HURT, false));
			ActionAsyncEvent chopperAction = new ActionAsyncEvent();
			chopperAction.setEventFactory(new AsyncTechEvents.CrashConnected("axe"));
			base.addAction(SWINGSOUND_NOPARTICLE);
			base.addAction(new TechActionSelectorInvokeType()
					.addAction(InvokeType.RIGHTCLICK, melee)
					.addAction(InvokeType.USE, chopperAction));
		},InvokeType.RIGHTCLICK,InvokeType.USE);
	}

	//	public static interface ProjectileFunctionTechAction extends ProjectileFunction<TechInvoker>{
	//
	//	}
}
