package mods.hinasch.unsaga.ability.specialmove.action;

import java.util.Random;

import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker.InvokeType;
import mods.hinasch.unsaga.common.specialaction.ActionTargettable;
import mods.hinasch.unsaga.common.specialaction.IRightClickToCharge;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import mods.hinasch.unsaga.damage.AdditionalDamage;
import mods.hinasch.unsaga.damage.DamageHelper;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;

public class TechActionWaterMoon extends TechActionBase implements IRightClickToCharge{

	public TechActionWaterMoon(InvokeType type) {
		super(type);
		TechActionSelectorInvokeType selector = new TechActionSelectorInvokeType();
		selector.addAction(InvokeType.RIGHTCLICK, in ->{
			in.swingMainHand(false, false);
			in.getTarget().ifPresent(target ->{
				in.playSound(XYZPos.createFrom(target), SoundEvents.ENTITY_ENDERMEN_TELEPORT, false);
				StateCapability.getCapability(target).addState(new PotionEffect(UnsagaPotions.WATER_MOON,ItemUtil.getPotionTime(15)));
			});
			return EnumActionResult.PASS;
		});
		selector.addAction(InvokeType.CHARGE, t ->{
			t.swingMainHand(true, false);
			return t.getTarget().map(target ->{
				if(StateCapability.isStateActive(target, UnsagaPotions.WATER_MOON)){
					if(t.getPerformer().getDistance(target)<10.0D){
						Random rand = t.getWorld().rand;
				        float f2 = (float)MathHelper.floor(target.getEntityBoundingBox().minY);
				        for (int j = 0; (float)j < 1.0F + target.width * 200.0F; ++j)
				        {
				            float f5 = (rand.nextFloat() * 2.0F - 1.0F) * target.width;
				            float f6 = (rand.nextFloat() * 2.0F - 1.0F) * target.width;
				            t.getWorld().spawnParticle(EnumParticleTypes.WATER_SPLASH, target.posX + (double)f5, (double)(f2 + 1.0F), target.posZ + (double)f6, target.motionX, target.motionY, target.motionZ);
				        }
						t.playSound(XYZPos.createFrom(target), SoundEvents.ENTITY_PLAYER_SPLASH, true);
						AdditionalDamage ad = new AdditionalDamage(t,General.PUNCH);
						target.attackEntityFrom(DamageHelper.register(ad), t.getActionStrength().hp());
						target.addPotionEffect(new PotionEffect(UnsagaPotions.DEPRESSED_MENTAL,ItemUtil.getPotionTime(15)));
						StateCapability.getCapability(target).removeState(UnsagaPotions.WATER_MOON);
						return EnumActionResult.SUCCESS;
					}

				}
				return EnumActionResult.PASS;
			}).orElse(EnumActionResult.PASS);

		});
		this.addAction(new ActionTargettable(selector));

	}

	@Override
	public boolean canCharge(TechInvoker invoker) {
		return invoker.getTarget()
				.filter(target ->StateCapability.isStateActive(target, UnsagaPotions.WATER_MOON)).isPresent();
	}

}
