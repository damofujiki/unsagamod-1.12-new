package mods.hinasch.unsaga.ability.specialmove.action;

import java.util.Random;
import java.util.function.Consumer;
import java.util.stream.Stream;

import org.apache.commons.lang3.tuple.Triple;

import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.particle.ParticleHelper.MovingType;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.SoundAndSFX;
import mods.hinasch.lib.world.ScannerBuilder;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker.InvokeType;
import mods.hinasch.unsaga.common.specialaction.ActionBase.IAction;
import mods.hinasch.unsaga.common.specialaction.ActionList;
import mods.hinasch.unsaga.common.specialaction.ActionProjectile;
import mods.hinasch.unsaga.common.specialaction.ActionProjectile.ProjectileFunction;
import mods.hinasch.unsaga.common.specialaction.ActionRequireJump;
import mods.hinasch.unsaga.core.entity.mob.UnsagaCreatureAttribute;
import mods.hinasch.unsaga.core.entity.passive.EntityEffectSpawner;
import mods.hinasch.unsaga.core.entity.passive.EntityShadowClone;
import mods.hinasch.unsaga.core.entity.passive.EntityShadowClone.MoveType;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import mods.hinasch.unsaga.core.potion.state.StateCombo;
import mods.hinasch.unsaga.damage.DamageComponent;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class TechActionFactoryHelper {
	public static TechActionBase createProjectile(ProjectileFunction<TechInvoker> func,int chargeTime){
		TechActionBase actionBase = TechActionBase.create(InvokeType.CHARGE);
		ActionProjectile<TechInvoker> projectile = new ActionProjectile<TechInvoker>()
				.setProjectileFunction(func)
				.setShootSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP);
		actionBase.addAction(new TechActionCharged(projectile).setChargeThreshold(chargeTime));
		return actionBase;
	}

	//	/** DamageDelegate用*/
	//	public static Function<Triple<TechInvoker,EntityLivingBase,DamageComponent>,DamageComponent> getWoodPlantSlayer(){
	//		return in ->in.getMiddle().getCreatureAttribute()==UnsagaCreatureAttribute.PLANT ? DamageComponent.of(in.getRight().hp() * 1.5F, in.getRight().lp()) : in.getRight();
	//	}

	public static void fujiViewConsumer(TechInvoker self,EntityLivingBase target){
		Vec3d lookVec = self.getPerformer().getLookVec().normalize().scale(0.6D);
		lookVec = lookVec.rotateYaw((float) Math.toRadians(180));
		self.getPerformer().addVelocity(lookVec.x,0.3D, lookVec.z); //反動
		Random rand = self.getWorld().rand;
		XYZPos targetPos = XYZPos.createFrom(target);
		self.playSound(XYZPos.createFrom(target), SoundEvents.ENTITY_GENERIC_EXPLODE, false);

		IBlockState down = self.getWorld().getBlockState(targetPos.down());
		if(down.getBlock()!=Blocks.AIR){
			int id = Block.getIdFromBlock(down.getBlock());
			self.spawnParticle(MovingType.FOUNTAIN,XYZPos.createFrom(target), EnumParticleTypes.BLOCK_DUST, 30, 0.1D,id);
		}
		//打ち上げる
		target.addVelocity(0, self.getPerformer().onGround ? 3.0D : 1.5D, 0);
		StateCapability.getCapability(target).addState(new PotionEffect(UnsagaPotions.PARTICLE,ItemUtil.getPotionTime(3),0));


		//			self.spawnParticle(MovingType.FLOATING,target, EnumParticleTypes.VILLAGER_HAPPY, 20, 0.1D);

		if(WorldHelper.isServer(self.getWorld())){
			self.getWorld().spawnEntity(EntityEffectSpawner.create(target, 45, 3.0D));
		}
		self.spawnParticle(MovingType.FOUNTAIN,target, EnumParticleTypes.SMOKE_LARGE, 25, 0.05D);


		//ブロック破壊
		if(!self.getPerformer().onGround){
			ScannerBuilder.create()
			.base(target)
			.range(1)
			.ready()
			.stream()
			.forEach(pos ->{
				NonNullList<ItemStack> list = collectBlockToItemList(self, pos);
				list.forEach(in ->ItemUtil.dropAndFlyItem(self.getWorld(), in,new XYZPos(pos)));
			});
		}
	}
	public static DamageComponent woodPlantSlayer(Triple<TechInvoker,EntityLivingBase,DamageComponent> in){
		return in.getMiddle().getCreatureAttribute()==UnsagaCreatureAttribute.PLANT ? DamageComponent.of(in.getRight().hp() * 1.5F, in.getRight().lp()) : in.getRight();
	}


	public static NonNullList<ItemStack> collectBlockToItemList(TechInvoker self,BlockPos pos){
		NonNullList<ItemStack> list = NonNullList.create();
		if(!self.getWorld().isAirBlock(pos)){
			IBlockState state = self.getWorld().getBlockState(pos);
			if(HSLibs.canBreakAndEffectiveBlock(self.getWorld(), null,"pickaxe", pos,new ItemStack(Items.IRON_PICKAXE))){

				state.getBlock().getDrops(list,self.getWorld(), pos, state, 0);
				WorldHelper.setAir(self.getWorld(), pos);
			}else{
				if(HSLibs.canBreakAndEffectiveBlock(self.getWorld(), null, "shovel", pos,new ItemStack(Items.IRON_SHOVEL))){

					state.getBlock().getDrops(list,self.getWorld(), pos, state, 0);
					WorldHelper.setAir(self.getWorld(), pos);
				}
			}

		}
		return list;
	}

	/** ブロックへの効果と攻撃ハイブリッドな典型的杖技を作る*/
	public static TechActionBase buildMaceTech(Consumer<TechActionMelee> cons,IAction useAction){
		TechActionBase pulverizerBase = new TechActionBase(InvokeType.RIGHTCLICK,InvokeType.USE);
		TechActionMelee melee = new TechActionMelee(General.PUNCH);
		cons.accept(melee);
		ActionList list = new ActionList();
		list.addAction(TechActionFactory.STAFF_EFFECT);
		list.addAction(useAction);
		pulverizerBase.addAction(new TechActionSelectorInvokeType()
				.addAction(InvokeType.RIGHTCLICK, melee)
				.addAction(InvokeType.USE, list));
		return pulverizerBase;
	}
	/**マキ割ダイナミックのconsumer用 */
	public static EnumActionResult woodChopperConsumer(TechInvoker self,BlockPos pos){
		IBlockState state = self.getWorld().getBlockState(pos);
		if(state.getBlock().isWood(self.getWorld(), pos) && state.getBlock().getRegistryName().getResourceDomain().equals("dynamictrees")){
			SoundAndSFX.playBlockBreakSFX(self.getWorld(), pos, state,false);
			int drop = self.getArtifact().map(in ->{
				int fortune = EnchantmentHelper.getEnchantments(self.getArtifact().get()).get(Enchantments.FORTUNE);
				return 9 + fortune;
			}).orElse(9);

			Stream.generate(()->new ItemStack(Items.STICK,1))
			.limit(drop)
			.forEach(in ->ItemUtil.dropAndFlyItem(self.getWorld(), in, new XYZPos(pos)));
			return EnumActionResult.SUCCESS;
		}
		return EnumActionResult.PASS;
	}
	public static void copyGhost(TechInvoker invoker,BlockPos ghostPos,Vec3d ghostVec,float ghostYaw,EntityLivingBase target,General... types){
		EntityShadowClone ghost = new EntityShadowClone(invoker.getWorld(),invoker.getPerformer());
		ItemStack axe = invoker.getArtifact().isPresent() ? invoker.getArtifact().get() : new ItemStack(Items.IRON_AXE);
		ghost.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, axe.copy());
		ghost.setPosition(ghostPos.getX(),ghostPos.getY(),ghostPos.getZ());
		//			ghost.setBaseVelocity(ghostVec);
		ghost.setRenderYawOffset(ghostYaw);
		ghost.setGhostAttackTarget(target);
		ghost.setDamage(invoker.getStrength(),types);
		ghost.setMoveType(MoveType.REVERSE_DELTA);
		WorldHelper.safeSpawn(invoker.getWorld(), ghost);
	}

	public static TechActionBase createMultiAttack(ComboActionProperty prop,boolean cancelHurtResistance,boolean requireJump,boolean isFinishDashAttack,General... types){
		TechActionBase base = new TechActionBase(InvokeType.RIGHTCLICK);

		IAction<TechInvoker> action = new TechActionMelee(types)
				.setAdditionalBehavior((context,target)->{
					ComboActionProperty.Element elm = prop.getElement(0);
					context.playSound(XYZPos.createFrom(target), elm.getHitSound(), false,elm.getSoundPitch());
					context.spawnParticle(MovingType.FOUNTAIN, new XYZPos(target.getPosition().up()), elm.getHitParticle(), elm.getParticleDensity(), 0.1D,Block.getIdFromBlock(elm.getParticleBlock()));
					StateCapability.getCapability(context.getPerformer()).addState(new StateCombo.Effect(1,context.getStrength(),prop,context.getArtifact().isPresent() ? context.getArtifact().get() : ItemStack.EMPTY));
					if(cancelHurtResistance){
						StateCapability.getCapability(target).addState(new PotionEffect(UnsagaPotions.CANCEL_HURT_TIME,ItemUtil.getPotionTime(2),0));
					}

				})
				.setDamageDelegate(in ->{
					float at = (float) in.getLeft().getPerformer().getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
					return DamageComponent.of(at, in.getRight().lp());
				});

		if(requireJump){
			base.addAction(new ActionRequireJump(action));
		}else{
			base.addAction(action);
		}
		return base;
	}
}
