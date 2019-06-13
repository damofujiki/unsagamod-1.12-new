package mods.hinasch.unsaga.common.specialaction;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Function;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import mods.hinasch.lib.entity.RangedEffect;
import mods.hinasch.lib.entity.RangedHelper;
import mods.hinasch.lib.entity.RangedHelper.RangedSelector;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker;
import mods.hinasch.unsaga.common.specialaction.ActionBase.IAction;
import mods.hinasch.unsaga.damage.AdditionalDamage;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.Sub;
import mods.hinasch.unsagamagic.spell.action.SpellCastIgniter;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class ActionRangedAttack<T extends IActionPerformer> implements IAction<T>{

	final boolean isAttack;
	final boolean isBlockable;
	final BBMaker<T> boundingBoxMaker;
//	final AttackSelector<T> entitySelector;
//	final SubConsumer<T> subConsumer;
	BiPredicate<T,EntityLivingBase> entitySelector2 = (self,target) -> true;
	BiConsumer<T,EntityLivingBase> subConsumer2 = (self,target)->{};
	final Optional<ActionStatusEffect<T>> debuffSetter;
	final EnumSet<General> damageType;
	final EnumSet<Sub> subDamageType;


	public ActionRangedAttack(RangedAttackSetting setting) {
		this.isAttack = setting.isAttack;
		this.isBlockable = setting.isBlockable;
		this.boundingBoxMaker = setting.boundingBoxMaker;
//		this.entitySelector = setting.entitySelector;
//		this.subConsumer = setting.subConsumer;
		this.debuffSetter = setting.debuffSetter!=null ? Optional.of(setting.debuffSetter) : Optional.empty();
		this.damageType = setting.damageType;
		this.subDamageType = setting.subDamageType;
	}
//	public ActionRangedAttack(@Nullable General... damageType) {
//		if(damageType==null || damageType.length<=0){
//			this.damageType = EnumSet.copyOf(Lists.newArrayList(General.PUNCH));
//		}else{
//			this.damageType = EnumSet.copyOf(Lists.newArrayList(damageType));
//		}
//
//		// TODO 自動生成されたコンストラクター・スタブ
//	}



	public EnumActionResult apply(T context) {
		BiPredicate<T,EntityLivingBase> attackConsumer = (self,target)->{
			boolean flag = false;
			if(this.isAttack){
				DamageSource ds = AdditionalDamage.getSource(context.getPerformer());
				if(!this.isBlockable){
					ds.setDamageBypassesArmor();
				}
				AdditionalDamage ad = new AdditionalDamage(ds,context.getStrength().lp(),this.damageType);

				if(!this.subDamageType.isEmpty()){
					ad.setSubTypes(subDamageType);
				}

				target.attackEntityFrom(AdditionalDamage.register(ad), context.getStrength().hp());
				flag = true;
//				self.setFlag(true);
			}
			if(this.debuffSetter.isPresent()){
//				UnsagaMod.logger.trace(this.getClass().getName(), "called");
				context.setTarget(target);
				EnumActionResult result = this.debuffSetter.get().apply(context);
				flag = result ==EnumActionResult.SUCCESS ? true : false;
//				self.setFlag(result ==EnumActionResult.SUCCESS ? true : false);
			}
			UnsagaMod.logger.trace("log", "log");
			this.subConsumer2.accept(self, target);
			return flag;
		};
//		context.swingMainHand(true, isRenderSweepParticle);
//		RangedHelper<T> rangedHelperOld = RangedHelper.<T>create(context.getWorld(), context.getPerformer(), boundingBoxMaker.apply(context))
//		.setSelector(entitySelector).setParent(context).setConsumer(attackConsumer);


		boolean flag = 	RangedEffect.builder()
				.owner(context.getPerformer())
				.boundingBoxes(boundingBoxMaker.apply(context))
				.selector((self,target)->this.entitySelector2.test( context, target))
				.predicateConsumer((self,target)->attackConsumer.test(context, target))
				.build(context.getWorld()).invoke();
//		RangedEffectTech.builder(context)
//		.owner(context.getPerformer())
//		.boundingBoxes(boundingBoxMaker.apply(context))
//		.selector((self,target)->this.entitySelector2.test((RangedEffectTech<T>) self, target))
//		.consumer((self,target)->this.subConsumer2.accept((RangedEffectTech<T>) self, target))
//		.build(context.getWorld()).invoke();
		return flag ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
	}


	public static class RangedAttackSetting<T extends IActionPerformer>{
		boolean isAttack = true;
		boolean isBlockable = true;
		BBMaker<T> boundingBoxMaker = in -> Lists.newArrayList();
//		AttackSelector<T> entitySelector = (self,target)->true;
//		SubConsumer<T> subConsumer = (self,target)->{};
		BiPredicate<T,EntityLivingBase> entitySelector2 = (self,target) -> true;
		BiConsumer<T,EntityLivingBase> subConsumer2 = (self,target)->{};
		@Nullable
		ActionStatusEffect<T> debuffSetter;
		EnumSet<General> damageType = EnumSet.of(General.NONE);
		EnumSet<Sub> subDamageType = EnumSet.noneOf(Sub.class);

		/**
		 * 直接攻撃技を作る
		 * @return
		 */
		public static RangedAttackSetting<TechInvoker> tech(){
			return new RangedAttackSetting().attackable(true).blockable(true);
		}

		/**
		 * 攻撃要素のない術を作る
		 * @return
		 */
		public static RangedAttackSetting<SpellCastIgniter> spell(){
			return new RangedAttackSetting().attackable(false).blockable(false);
		}

		/**
		 * 攻撃の術を作る
		 * @return
		 */
		public static RangedAttackSetting<SpellCastIgniter> attackSpell(){
			return new RangedAttackSetting().attackable(true).damageType(General.MAGIC).blockable(false);
		}
		public RangedAttackSetting<T> attackable(boolean par1){
			this.isAttack = par1;
			return this;
		}

		public RangedAttackSetting<T> boundingBoxes(BBMaker<T> maker){
			this.boundingBoxMaker = maker;
			return this;
		}

		public RangedAttackSetting<T> boundingBox(Function<T,AxisAlignedBB> func){
			this.boundingBoxMaker = in -> Lists.newArrayList(func.apply(in));
			return this;
		}
		public RangedAttackSetting<T> blockable(boolean par1){
			this.isBlockable = par1;
			return this;
		}

		public RangedAttackSetting<T> onlyGround(){
//			this.selector((self,target)->target.onGround);
			this.selector((self,target)->target.onGround);
			return this;
		}

//		public RangedAttackSetting<T> selector(AttackSelector<T> selector){
//			this.entitySelector = selector;
//			return this;
//		}
//		public RangedAttackSetting<T> selector(BiPredicate<RangedEffectTech<T>,EntityLivingBase> selector){
//			this.entitySelector2 = selector;
//			return this;
//		}

		public RangedAttackSetting<T> selector(BiPredicate<T,EntityLivingBase> selector){
			this.entitySelector2 = selector;
			return this;
		}
//		public RangedAttackSetting<T> consumer(SubConsumer<T> cons){
//			this.subConsumer = cons;
//			return this;
//		}
//		public RangedAttackSetting<T> consumer(BiConsumer<RangedEffectTech<T>,EntityLivingBase> cons){
//			this.subConsumer2 = cons;
//			return this;
//		}

		public RangedAttackSetting<T> consumer(BiConsumer<T,EntityLivingBase> cons){
			this.subConsumer2 = cons;
			return this;
		}
		public RangedAttackSetting<T> statusEffect(ActionStatusEffect<T> debuff){
			this.debuffSetter = debuff;
			return this;
		}

		public RangedAttackSetting<T> damageType(General... generals){
			this.damageType = EnumSet.copyOf(ImmutableSet.copyOf(generals));
			return this;
		}

		public RangedAttackSetting<T> subType(Sub... subs){
			this.subDamageType = EnumSet.copyOf(ImmutableSet.copyOf(subs));
			return this;
		}


	}

	/**
	 *
	 * {@code
	 * Function<T,List<AxisAlignedBB>
	 * }
	 *
	 * @param <T>
	 */
	public static interface BBMaker<T extends IActionPerformer> extends Function<T,List<AxisAlignedBB>>{

	}

	/**
	 *
	 * {@code RangedSelector<T,EntityLivingBase>
	 * }
	 *
	 * @param <T>
	 */
	public static interface AttackSelector<T extends IActionPerformer> extends RangedSelector<T,EntityLivingBase>{

	}
	public static class RangeSurroundings<V extends IActionPerformer> implements Function<V,List<AxisAlignedBB>>{

		final double horizontal;
		final double vertical;
		public RangeSurroundings(double horizontal,double vertical){
			this.horizontal = horizontal;
			this.vertical = vertical;
		}
		@Override
		public List<AxisAlignedBB> apply(V t) {
			return ImmutableList.of(t.getPerformer().getEntityBoundingBox().grow(horizontal,vertical,horizontal));
		}

	}

	public static class RangeSwing implements Function<TechInvoker,List<AxisAlignedBB>>{

		/** 多くするほど当たり判定が細かい*/
		final int resolution;
		public RangeSwing(int resolution){
			this.resolution = resolution;
		}
		@Override
		public List<AxisAlignedBB> apply(TechInvoker context) {
			List<AxisAlignedBB> list = new ArrayList<>();
			EntityLivingBase el = context.getPerformer();
			int rotatePar = 180 / this.resolution;
			for(int i=0;i<(int)context.getReach();i++){
				Vec3d v1 = el.getLookVec().normalize().scale(i+1);

				for(int r=0;r<resolution+i;r++){
					Vec3d v2 = v1.rotateYaw((float) Math.toRadians(90-(rotatePar*r)));
					AxisAlignedBB bb = el.getEntityBoundingBox().expand(1.5F, 0, 1.5F).offset(v2.x,v2.y,v2.z);
					list.add(bb);
				}
			}


			return list;
		}

	}

	public static interface SubConsumer<T> extends BiConsumer<RangedHelper<T>,EntityLivingBase>{

	}
	public static class PlayerBoundingBox<V extends IActionPerformer> implements BBMaker<V> {

		final double range;
		public PlayerBoundingBox(double range){
			this.range = range;
		}
		@Override
		public List<AxisAlignedBB> apply(V t) {
			// TODO 自動生成されたメソッド・スタブ
			return Lists.newArrayList(t.getPerformer().getEntityBoundingBox().grow(range));
		}


	}
}
