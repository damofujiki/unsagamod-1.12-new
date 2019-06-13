package mods.hinasch.unsaga.ability.specialmove.action;

import java.util.EnumSet;
import java.util.function.BiConsumer;
import java.util.function.Function;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.tuple.Triple;

import com.google.common.collect.Lists;

import mods.hinasch.unsaga.ability.specialmove.TechInvoker;
import mods.hinasch.unsaga.common.specialaction.ActionBase.IAction;
import mods.hinasch.unsaga.common.specialaction.ISimpleMelee;
import mods.hinasch.unsaga.damage.DamageComponent;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.Sub;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumActionResult;

public class TechActionMelee implements IAction<TechInvoker>,ISimpleMelee<TechInvoker>{

	/** getDamageで返してるFunction.これを置き換えてもgetDamageを上書きしてもどちらでもよい*/
	protected Function<Triple<TechInvoker,EntityLivingBase,DamageComponent>,DamageComponent> damageDelegate = in -> in.getRight();
	protected BiConsumer<TechInvoker,EntityLivingBase> consumer;
	protected EnumSet<General> attributes;
	protected EnumSet<Sub> subAttributes = EnumSet.noneOf(Sub.class);
	int maxAttackableEnemy = 1;
	float knockbackStrength = 0;

	public TechActionMelee(@Nonnull General... attributes) {
		this.attributes = EnumSet.copyOf(Lists.newArrayList(attributes));
	}

	/**
	 *
	 * @param maxAttackble 敵を同時に攻撃できる数。1で単体攻撃
	 * @param attributes
	 */
	public TechActionMelee(int maxAttackble,@Nonnull General... attributes) {
		this(attributes);
		this.maxAttackableEnemy = maxAttackble;
	}
	public TechActionMelee setSubAttributes(EnumSet<Sub> sub){
		this.subAttributes = sub;
		return this;
	}

	/**
	 * 追加効果などはここへ
	 * @param consumer
	 * @return
	 */
	public TechActionMelee setAdditionalBehavior(BiConsumer<TechInvoker,EntityLivingBase> consumer){
		this.consumer = consumer;
		return this;
	}

	@Override
	public int getMaxAttackableEnemy(){
		return maxAttackableEnemy;
	}
	@Override
	public EnumActionResult apply(TechInvoker t) {
		// TODO 自動生成されたメソッド・スタブ
		return this.performSimpleAttack(t);
	}

	public TechActionMelee setKnockbackStrength(float knockback){
		this.knockbackStrength = knockback;
		return this;
	}
	@Override
	public EnumSet<General> getAttributes() {
		// TODO 自動生成されたメソッド・スタブ
		return attributes;
	}

	@Override
	public EnumSet<Sub> getSubAttributes() {
		// TODO 自動生成されたメソッド・スタブ
		return subAttributes;
	}



	@Override
	public BiConsumer<TechInvoker, EntityLivingBase> getAdditionalBehavior() {
		// TODO 自動生成されたメソッド・スタブ
		return consumer;
	}

	/**
	 * baseはTechInvoker.getStrength()の値と同じ
	 */
	@Override
	public DamageComponent getDamage(TechInvoker context,EntityLivingBase target, DamageComponent base) {
		// TODO 自動生成されたメソッド・スタブ
		return damageDelegate.apply(Triple.of(context, target, base));
	}

	@Override
	public float getReach(TechInvoker context) {
		// TODO 自動生成されたメソッド・スタブ
		return context.getReach();
	}

	@Override
	public float getKnockbackStrength() {
		// TODO 自動生成されたメソッド・スタブ
		return this.knockbackStrength;
	}

	public TechActionMelee setDamageDelegate(Function<Triple<TechInvoker,EntityLivingBase,DamageComponent>,DamageComponent> damageDelegate){
		this.damageDelegate = damageDelegate;
		return this;
	}
//	@Override
//	public float getLPDamage(SpecialMoveInvoker context, EntityLivingBase target, float base) {
//		// TODO 自動生成されたメソッド・スタブ
//		return base;
//	}

	/**
	 * 攻撃部分を全てスルーする。追加効果の部分は普通に実行されるので攻撃無しでそこだけ
	 * 使いたい時にtrueにする.
	 */
	@Override
	public boolean isThroughAttack() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}



}
