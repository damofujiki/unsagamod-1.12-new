package mods.hinasch.unsaga.minsaga.event;

import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.minsaga.MaterialLayer;
import mods.hinasch.unsaga.minsaga.MinsagaForgingCapability;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MinsagaAnvilHandler {


	@SubscribeEvent
	public void onAnvilUpdate(AnvilUpdateEvent e){


		//		UnsagaMod.logger.trace("anvil", "called");

		if(e.getLeft().isEmpty() || e.getRight().isEmpty()){
			return;
		}

		ItemStack base = e.getLeft();
		ItemStack material = e.getRight();
		MinsagaForgingCapability.ADAPTER.getCapabilityOptional(base)
		.ifPresent(capa ->{
//			IMinsagaForge capa = MinsagaForgingCapability.ADAPTER.getCapability(e.getLeft());
			if(capa.hasForged()){

				MaterialLayer layer = capa.getCurrentFittingLayer();
				int cost = e.getCost() * capa.costModifier();
				//					e.setCanceled(true);

				UnsagaMod.logger.trace("material", layer.getMaterial(),material,e.getCost(),cost);
				//一番新しい素材と一致するとき
				if(layer.getMaterial().isMaterialEqual(material)){

					e.setCost(MathHelper.clamp(cost, 1, 256));
					int damage = base.getItemDamage() - layer.getMaterial().getRepairDamage();
					ItemStack newStack = base.copy();
					newStack.setItemDamage(MathHelper.clamp(damage, 0, newStack.getMaxDamage()));
					newStack.setRepairCost(e.getCost()+1);
					e.setOutput(newStack);

				}else{
					e.setCanceled(true);
				}
			}
		});


	}
}
