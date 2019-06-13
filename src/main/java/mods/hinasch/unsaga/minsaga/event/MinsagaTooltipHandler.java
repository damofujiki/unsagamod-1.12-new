package mods.hinasch.unsaga.minsaga.event;

import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.unsaga.core.client.event.UnsagaTooltips;
import mods.hinasch.unsaga.core.client.gui.GuiToolCustomizeMinsaga;
import mods.hinasch.unsaga.minsaga.MinsagaForgingCapability;
import mods.hinasch.unsaga.minsaga.MinsagaMaterialInitializer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MinsagaTooltipHandler {
	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent e){
		ItemStack is = e.getItemStack();
		MinsagaForgingCapability.ADAPTER.getCapabilityOptional(is)
		.filter(in ->in.hasForged())
		.ifPresent(in ->{
			UnsagaTooltips.addTooltips(is, e.getToolTip(), e.getFlags(), UnsagaTooltips.Type.MINSAGA_MATERIALS);
		});

		if(ClientHelper.getCurrentGui() instanceof GuiToolCustomizeMinsaga){
			MinsagaMaterialInitializer.find(is)
			.ifPresent(in ->{
				UnsagaTooltips.addTooltips(is, e.getToolTip(), e.getFlags(), UnsagaTooltips.Type.MINSAGA_MODIFIERS);
			});
		}
	}
}
