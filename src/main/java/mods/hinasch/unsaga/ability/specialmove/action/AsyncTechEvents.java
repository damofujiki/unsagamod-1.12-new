package mods.hinasch.unsaga.ability.specialmove.action;

import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import mods.hinasch.lib.sync.AsyncUpdateEvent;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.world.ScannerBuilder;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker;
import mods.hinasch.unsaga.common.UnsagaWeightType;
import mods.hinasch.unsaga.common.specialaction.ActionAsyncEvent.AsyncEventFactory;
import mods.hinasch.unsaga.material.UnsagaMaterialCapability;
import mods.hinasch.unsaga.util.AsyncConnectedBlockBreaker;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumFacing;

public class AsyncTechEvents {

	public static final AsyncUpdateEvent EMPTY = new AsyncUpdateEvent(null, null){

		@Override
		public boolean hasFinished() {
			// TODO 自動生成されたメソッド・スタブ
			return false;
		}

		@Override
		public void loop() {
			// TODO 自動生成されたメソッド・スタブ

		}
	};
	public static class CrashConnected implements AsyncEventFactory<TechInvoker>{

		Set<Block> blackList = Sets.newHashSet(Blocks.COBBLESTONE,Blocks.STONE,Blocks.SANDSTONE);
		String toolClass;
		public CrashConnected(String clazz){
			this.toolClass = clazz;
		}
		@Override
		public AsyncUpdateEvent apply(TechInvoker t) {
			if(WorldHelper.isClient(t.getWorld())){
				return AsyncTechEvents.EMPTY;
			}
			IBlockState state = t.getWorld().getBlockState(t.getTargetCoordinate().get());
			//DynamicTreesのは木こらない（そもそもHarvestできないのでremoveを呼ぶ）
			if(state.getBlock().getRegistryName().getResourceDomain().equals("dynamictrees") && t.getPerformer() instanceof EntityPlayer){
				t.playSound(new XYZPos(t.getTargetCoordinate().get()), SoundEvents.BLOCK_WOOD_BREAK, true);
				state.getBlock().removedByPlayer(state, t.getWorld(), t.getTargetCoordinate().get(), (EntityPlayer) t.getPerformer(), false);
				return AsyncTechEvents.EMPTY;
			}
			if(HSLibs.canBreakAndEffectiveBlock(t.getWorld(), t.getPerformer(), toolClass, t.getTargetCoordinate().get())){
				if(!blackList.contains(state.getBlock())){
					AsyncConnectedBlockBreaker scannerBreak = new AsyncConnectedBlockBreaker(t.getWorld(),5,Sets.newHashSet(state), t.getTargetCoordinate().get(), t.getPerformer());
					return scannerBreak;
				}

			}
			return AsyncTechEvents.EMPTY;
		}

	}


	//	public static class GrandSlam implements AsyncEventFactory<TechInvoker>{
	//
	//		@Override
	//		public AsyncUpdateEvent apply(TechInvoker context) {
	//			boolean heavy = context.getArtifact().isPresent() && UnsagaMaterialCapability.adapter.hasCapability(context.getArtifact().get()) && UnsagaMaterialCapability.adapter.getCapability(context.getArtifact().get()).getWeight()>10;
	//			List<BlockPos> expos = Lists.newArrayList();
	//			AxisAlignedBB bb = context.getPerformer().getEntityBoundingBox().expand(8.0D, 3.0D, 8.0D);
	//			RangedHelper<TechInvoker> helper = RangedHelper.create(context.getWorld(), context.getPerformer(),Lists.newArrayList(bb));
	//			helper.setSelector((self,target)->target.onGround).setConsumer((self,target)->{
	//				target.addVelocity(0, 1.0D, 0);
	//				expos.add(target.getPosition());
	//			}).invoke();
	//			if(!expos.isEmpty()){
	//				return new AsyncGrandslam(context.getWorld(), context.getStrengthHP(), expos, heavy, context.getPerformer());
	//			}
	//			return null;
	//
	//		}
	//
	//	}

	public static class RangedCrasher implements AsyncEventFactory<TechInvoker>{

		@Override
		public AsyncUpdateEvent apply(TechInvoker t) {
			if(WorldHelper.isClient(t.getWorld())){
				return AsyncTechEvents.EMPTY;
			}
			int range = 1;
			if(t.getArtifact().isPresent()){
				UnsagaWeightType weight = UnsagaMaterialCapability.adapter.getCapability(t.getArtifact().get()).getToolWeightType();
				range = weight.isHeavy() ? 2 : 1;
			}

			return new AsyncSafeBlockCrasher(t.getWorld(), t.getPerformer(),ScannerBuilder.create().base(t.getTargetCoordinate().get()).range(range).ready(),Lists.newArrayList("pickaxe"));
		}

	}


	public static class Flatter implements AsyncEventFactory<TechInvoker>{

		@Override
		public AsyncUpdateEvent apply(TechInvoker t) {
			return t.getTargetCoordinate()
					.filter(in -> t.getUseFacing()==EnumFacing.UP)
					.filter(in ->WorldHelper.isServer(t.getWorld()))
					.map(in ->{
						int range = t.getArtifact()
								.map(artifact ->{
									UnsagaWeightType weight = UnsagaMaterialCapability.adapter.getCapability(t.getArtifact().get()).getToolWeightType();
									return weight.isHeavy() ? 2 :1;
								}).orElse(1);


						return (AsyncUpdateEvent)new AsyncSafeBlockCrasher(t.getWorld(), t.getPerformer(),
								ScannerBuilder
								.create()
								.base(in.up(range+1))
								.range(range)
								.ready(),ImmutableList.of("pickaxe","shovel"));
					}).orElse(AsyncTechEvents.EMPTY);

		}

	}


	//	public static class GettingThrown extends SafeUpdateEventByInterval{
	//
	//		TechInvoker invoker;
	//		int time = 0;
	//		double degree = 0;
	//		double speed = 0.01D;
	//		public GettingThrown(EntityLivingBase sender,TechInvoker invoker) {
	//			super(sender, "thrown");
	//			this.invoker = invoker;
	//			// TODO 自動生成されたコンストラクター・スタブ
	//		}
	//
	//		@Override
	//		public int getIntervalThresold() {
	//			// TODO 自動生成されたメソッド・スタブ
	//			return 0;
	//		}
	//
	//		@Override
	//		public void loopByInterval() {
	//
	//			if(invoker.getTarget().isPresent()){
	//				if(invoker.getActionProperty()==TechRegistry.instance().CYCLONE){
	//
	//					degree = MathHelper.wrapDegrees(degree);
	//					Vec3d vec = invoker.getTarget().get().getLookVec().scale(speed);
	//					vec = vec.rotateYaw((float) Math.toRadians(degree));
	//					invoker.getTarget().get().addVelocity(vec.x, 0, vec.z);
	//					speed += 0.01D;
	//					if(speed>3.0D){
	//						speed = 2.0D;
	//					}
	//					degree += 30.0D;
	//				}
	//				invoker.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, invoker.getTarget().get().getEntityBoundingBox(),in -> in!=sender && in!=invoker.getTarget().get())
	//				.forEach(in ->{
	//					in.attackEntityFrom(DamageSourceUnsaga.create(sender, 0.5F, General.PUNCH), 1.0F);
	//				});
	//			}
	//			time ++;
	////			UnsagaMod.logger.trace("wait", time);
	//		}
	//
	//		@Override
	//		public boolean hasFinished() {
	//			// TODO 自動生成されたメソッド・スタブ
	//			return invoker.getTarget().isPresent() ? this.time>5 && !invoker.getTarget().get().isPotionActive(MobEffects.LEVITATION) : true;
	//		}
	//
	//	}

	//	public static class ContinuousAttack extends SafeUpdateEventByInterval implements ISimpleMelee<TechInvoker>{
	//
	//		final int expire;
	//		int time = 0;
	//		World world;
	//		TechInvoker invoker;
	//		public ContinuousAttack(EntityLivingBase sender,int expire,TechInvoker invoker) {
	//			super(sender, "continuousAttack");
	//			this.expire = expire;
	//			this.world = sender.getEntityWorld();
	//			this.invoker = invoker;
	//		}
	//
	//		@Override
	//		public int getIntervalThresold() {
	//			// TODO 自動生成されたメソッド・スタブ
	//			return 2;
	//		}
	//
	//		@Override
	//		public void loopByInterval() {
	//
	//			SoundAndSFX.playSound(world, XYZPos.createFrom(sender), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, SoundCategory.PLAYERS, 1.0F, 1.0F, false);
	//
	//			sender.swingArm(EnumHand.MAIN_HAND);
	//			this.performSimpleAttack(invoker);
	//			time ++;
	//		}
	//
	//		@Override
	//		public boolean hasFinished() {
	//			// TODO 自動生成されたメソッド・スタブ
	//			return expire<time;
	//		}
	//
	//		@Override
	//		public EnumSet<General> getAttributes() {
	//			// TODO 自動生成されたメソッド・スタブ
	//			return EnumSet.of(General.SPEAR);
	//		}
	//
	//		@Override
	//		public EnumSet<Sub> getSubAttributes() {
	//			// TODO 自動生成されたメソッド・スタブ
	//			return EnumSet.noneOf(Sub.class);
	//		}
	//
	//		@Override
	//		public BiConsumer<TechInvoker, EntityLivingBase> getAdditionalBehavior() {
	//			// TODO 自動生成されたメソッド・スタブ
	//			return (context,target)->{
	//				ParticleHelper.MovingType.FOUNTAIN.spawnParticle(world, new XYZPos(target.getPosition().up()), EnumParticleTypes.REDSTONE, world.rand, 10, 0.1D,new int[0]);
	//				target.hurtResistantTime = 0;
	//				target.hurtTime = 0;
	//			};
	//		}
	//
	//
	//
	//		@Override
	//		public float getReach(TechInvoker context) {
	//			// TODO 自動生成されたメソッド・スタブ
	//			return context.getReach() ;
	//		}
	//
	//		@Override
	//		public DamageComponent getDamage(TechInvoker context,EntityLivingBase target, DamageComponent base) {
	//			return invoker.getStrength();
	//		}
	//
	//		@Override
	//		public float getKnockbackStrength() {
	//			// TODO 自動生成されたメソッド・スタブ
	//			return 0;
	//		}
	//
	//		@Override
	//		public int getMaxAttackableEnemy() {
	//			// TODO 自動生成されたメソッド・スタブ
	//			return 0;
	//		}
	//
	//
	//
	//	}
}
