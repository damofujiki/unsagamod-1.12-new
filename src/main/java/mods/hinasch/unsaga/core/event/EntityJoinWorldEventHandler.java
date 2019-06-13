package mods.hinasch.unsaga.core.event;

import mods.hinasch.unsaga.core.entity.projectile.custom_arrow.CustomArrowEventHandler;
import mods.hinasch.unsaga.core.inventory.AccessorySlotCapability;
import mods.hinasch.unsaga.core.world.UnsagaWorldCapability;
import mods.hinasch.unsaga.lp.LifePoint;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityJoinWorldEventHandler {


	@SubscribeEvent
	public void onEntityJoinWorld(EntityJoinWorldEvent e){
		ReplaceFoodStatsHandler.updateFood(e);
		AccessorySlotCapability.syncAccessories(e);
		LifePoint.onEntityJoin(e);
		UnsagaWorldCapability.onEntityJoinWorld(e);
		CustomArrowEventHandler.onEntityJoin(e);
	}
}
