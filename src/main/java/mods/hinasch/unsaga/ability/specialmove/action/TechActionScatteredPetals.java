package mods.hinasch.unsaga.ability.specialmove.action;

import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker.InvokeType;
import mods.hinasch.unsaga.common.specialaction.ActionTargettable;
import mods.hinasch.unsaga.common.specialaction.IRightClickToCharge;
import mods.hinasch.unsaga.core.entity.passive.EntitySnowFall;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;

public class TechActionScatteredPetals extends TechActionBase implements IRightClickToCharge{

	public TechActionScatteredPetals(InvokeType type) {
		super(type);
		TechActionSelectorInvokeType selector = new TechActionSelectorInvokeType();
		selector.addAction(InvokeType.RIGHTCLICK, t ->{

			if(t.getInvokeType()==InvokeType.RIGHTCLICK && !StateCapability.isStateActive(t.getTarget().get(), UnsagaPotions.SNOWFALL)){
				t.playSound(XYZPos.createFrom(t.getTarget().get()), SoundEvents.ENTITY_ENDERMEN_TELEPORT, false);

				WorldHelper.safeSpawn(t.getWorld(), makeSnow(t, 45.0F));
				WorldHelper.safeSpawn(t.getWorld(), makeSnow(t, -45.0F));
				StateCapability.ADAPTER.getCapability(t.getTarget().get()).addState(new PotionEffect(UnsagaPotions.SNOWFALL,ItemUtil.getPotionTime(10)));
			}

			return EnumActionResult.PASS;
		});
		TechActionAsync gust = new TechActionAsync(MovingStates.GUST,12).setStartSound(SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, 0.5F);

		selector.addAction(InvokeType.CHARGE, new TechActionCharged(gust).setChargeThreshold(10));

		this.addAction(new ActionTargettable(selector));
	}

	@Override
	public boolean canCharge(TechInvoker invoker) {
		UnsagaMod.logger.trace("cancharge", 1);
		if(invoker.getTarget().isPresent()){
			UnsagaMod.logger.trace("cancharge", 2);
			if(StateCapability.isStateActive(invoker.getTarget().get(), UnsagaPotions.SNOWFALL)){
				UnsagaMod.logger.trace("cancharge", 3);
				return true;
			}
		}
		return false;
	}

	public EntitySnowFall makeSnow(TechInvoker t,float yaw){
			EntitySnowFall snow = new EntitySnowFall(t.getWorld(),t.getTarget().get());
			XYZPos p = XYZPos.createFrom(t.getTarget().get());
			snow.setPosition(p.dx,p.dy,p.dz);
			snow.rotationYaw = yaw;
			snow.setRenderYawOffset(yaw);
			return snow;
	}
}
