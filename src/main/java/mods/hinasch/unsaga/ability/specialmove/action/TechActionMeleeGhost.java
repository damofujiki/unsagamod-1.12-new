package mods.hinasch.unsaga.ability.specialmove.action;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;
import com.mojang.realmsclient.util.Pair;

import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker;
import mods.hinasch.unsaga.core.entity.passive.EntityShadowClone;
import mods.hinasch.unsaga.core.entity.passive.EntityShadowClone.MoveType;
import mods.hinasch.unsaga.damage.DamageComponent;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class TechActionMeleeGhost extends TechActionMelee{


	Set<Pair<Float,Integer>> rotations = new HashSet<>();
	final int delayBase;
	Set<Pair<Potion,Integer>> effects = new HashSet<>();
	int attackCount = 1;

	public TechActionMeleeGhost(int delayBase,@Nonnull General... attributes) {
		super(attributes);
		this.delayBase = delayBase;
		this.setAdditionalBehavior((self,target)->{

			int delay = delayBase;
			for(Pair<Float,Integer> rot:rotations){
				this.copyGhost(self, rot.first(), target,rot.second());
//				delay ++;
			}

//			target.setVelocity(0, 1.0D, 0);
			for(Pair<Potion,Integer> p:this.effects){
				target.addPotionEffect(new PotionEffect(p.first(),ItemUtil.getPotionTime(p.second())));
			}
		});
	}

	public void setStatusEffect(Pair<Potion,Integer>... potions){
		this.effects = ImmutableSet.copyOf(potions);
	}

	public void setGhostDegrees(Float... degrees){
		Set<Pair<Float,Integer>> set = new HashSet<>();
		int base = 5;
		for(float d:degrees){
			set.add(Pair.of(d, base));
			base += 5;
		}
		this.rotations = ImmutableSet.copyOf(set);
	}

	/**
	 *
	 * @param count 攻撃回数、設定攻撃値をこれで割ったのが一回の攻撃値になる(LP攻撃力は同じ)
	 */
	public int setAttackCount(int count){
		return this.attackCount;
	}
	@Override
	public DamageComponent getDamage(TechInvoker context,EntityLivingBase target, DamageComponent base) {
		float hpDamage = base.hp()==0 ? 0 : base.hp()/(float)this.attackCount;
		return DamageComponent.of(hpDamage, base.lp());
	}

	/**
	 * 分身を自動で作成、出現させる。その後自動で攻撃する
	 * @param invoker
	 * @param rotation 基準からの角度
	 * @param target
	 */
	private void copyGhost(TechInvoker invoker,float rotation,EntityLivingBase target,int delay){
//		Vec3d targetToAttacker = target.getPositionVector().subtractReverse(invoker.getPerformer().getPositionVector()).normalize();
//		Vec3d rotated = targetToAttacker.rotateYaw((float) Math.toRadians(180-rotation));
//		EntityShadowClone ghost = ActionMultipleMeleeWithGhost.getGhost(invoker.getWorld(),invoker.getPerformer(),target,invoker.getPerformer().getHeldItemMainhand(),invoker.getStrength(),this.attributes);
//		ghost.setPosition(target.posX+rotated.x, target.posY, target.posZ+rotated.z);
//		ghost.setRenderYawOffset(invoker.getPerformer().rotationYaw+180+rotation);
//		ghost.setDelay(delay);
//		WorldHelper.safeSpawn(invoker.getWorld(), ghost);
		Vec3d lookvec = target.getPositionVector().subtractReverse(invoker.getPerformer().getPositionVector()).normalize().scale(1.2D);
		Vec3d rotated = lookvec.rotateYaw((float) Math.toRadians(180-rotation));
		EntityShadowClone ghost = TechActionMeleeGhost.getGhost(invoker.getWorld(),invoker.getPerformer(),target,invoker.getPerformer().getHeldItemMainhand(),invoker.getStrength(),this.attributes);
		ghost.setPosition(target.posX+rotated.x, target.posY, target.posZ+rotated.z);
		ghost.setRenderYawOffset(invoker.getPerformer().rotationYaw+180+rotation);
		ghost.setDelay(delay);
		WorldHelper.safeSpawn(invoker.getWorld(), ghost);
	}

	public static EntityShadowClone getGhost(World world,EntityLivingBase attacker,EntityLivingBase target,ItemStack atrifact,DamageComponent damage,Set<General> generals){
		EntityShadowClone ghost = new EntityShadowClone(world,attacker);
		ItemStack sword = atrifact.isEmpty() ? new ItemStack(Items.IRON_SWORD) : atrifact;
		ghost.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, sword.copy());
		ghost.setGhostAttackTarget(target);
		ghost.setDamage(damage,generals);
		ghost.setMoveType(MoveType.SIMPLE_MELEE);
		return ghost;
	}
}
