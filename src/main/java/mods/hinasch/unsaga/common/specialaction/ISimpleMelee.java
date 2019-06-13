package mods.hinasch.unsaga.common.specialaction;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import mods.hinasch.lib.util.VecUtil;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.damage.AdditionalDamage;
import mods.hinasch.unsaga.damage.DamageComponent;
import mods.hinasch.unsaga.damage.DamageHelper;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.Sub;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public interface ISimpleMelee<T extends IActionPerformer> {

	public EnumSet<General> getAttributes();
	public EnumSet<Sub> getSubAttributes();
	public DamageComponent getDamage(T context,EntityLivingBase target,DamageComponent base);
	//	public LPStrPair getLPDamage(T context,EntityLivingBase target,LPStrPair base);

	/**
	 * ターゲット攻撃後の作用
	 * @return
	 */
	public BiConsumer<T,EntityLivingBase> getAdditionalBehavior();
	public float getReach(T context);
	public float getKnockbackStrength();
	public boolean isThroughAttack();

	public int getMaxAttackableEnemy();

	/** ActionMeleeクラスのapplyから呼ばれたりする*/
	public default EnumActionResult performSimpleAttack(T context) {

		context.playSound(XYZPos.createFrom(context.getPerformer()), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, false);
		context.getPerformer().swingArm(EnumHand.MAIN_HAND);

		//		List<EntityLivingBase> list = Lists.newArrayList();
		//		for(int i=0;i<this.getReach(context);i++){
		//			Vec3d lookvec = context.getPerformer().getLookVec().normalize().scale(1.0D * (i+1));
		//			AxisAlignedBB bb = context.getPerformer().getEntityBoundingBox().offset(lookvec.x,lookvec.y,lookvec.z);
		//			context.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, bb,in ->in!=context.getPerformer()).forEach(in ->{
		//				list.add(in);
		//
		//			});
		//		}
		List<EntityLivingBase> list = Stream.iterate(0, i ->i + 1)
				.flatMap(i ->{
					Vec3d lookvec = context.getPerformer().getLookVec().normalize().scale(1.0D * (i+1));
					AxisAlignedBB bb = context.getPerformer().getEntityBoundingBox().offset(lookvec.x,lookvec.y,lookvec.z);
					return context.getWorld().getEntitiesWithinAABB(EntityLivingBase.class, bb,in ->in!=context.getPerformer()).stream();
				})
				.limit((int)this.getReach(context))
				.distinct()
				.collect(Collectors.toList());


		List<EntityLivingBase> trimmed = list.stream()
				.sorted(Comparator.<EntityLivingBase>comparingDouble(in -> in.getDistance(context.getPerformer())))
				.limit(getMaxAttackableEnemy())
				.collect(Collectors.toList());



		trimmed.forEach(el ->{
			if(!this.isThroughAttack()){
				DamageComponent damage = this.getDamage(context, el, context.getStrength());
				el.attackEntityFrom(DamageHelper.register(new AdditionalDamage(AdditionalDamage.getSource(context.getPerformer()),damage.lp(),this.getAttributes())),damage.hp());
				if(this.getKnockbackStrength()>0){
					VecUtil.knockback(context.getPerformer(), el, getKnockbackStrength(), 1.0D);
				}
			}

			Optional.ofNullable(this.getAdditionalBehavior())
			.ifPresent(in ->in.accept(context,el));
		});



		return trimmed.isEmpty() ? EnumActionResult.PASS : EnumActionResult.SUCCESS;

	}


}
