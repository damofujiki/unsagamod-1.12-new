package mods.hinasch.unsaga.core.client.event;

import java.util.List;
import java.util.stream.Collectors;

import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.core.client.gui.GuiBartering;
import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew;
import mods.hinasch.unsaga.villager.bartering.MerchandiseCapability;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventUnsagaTooltip {

	@SubscribeEvent
	public void onTooltip(ItemTooltipEvent e){

		/** 耐久力表示ONの場合だけ*/
		if(e.getItemStack().isItemStackDamageable() && UnsagaConfigHandlerNew.GENERAL.enableDisplayDurability){
			UnsagaTooltips.addTooltips(e.getItemStack(), e.getToolTip(), e.getFlags(), UnsagaTooltips.Type.DURABILITY);
		}

		/** 取引時にだけ表示*/
		if(ClientHelper.getCurrentGui() instanceof GuiBartering && MerchandiseCapability.ADAPTER.hasCapability(e.getItemStack())){
			UnsagaTooltips.addTooltips(e.getItemStack(), e.getToolTip(), e.getFlags(), UnsagaTooltips.Type.MERCHANT_PRICE);
		}

		if(HSLib.isDebug()){
			List<String> list = HSLibs.getOreNames(e.getItemStack());
			if(!list.isEmpty()){
				e.getToolTip().add(list.stream().collect(Collectors.joining(",")));
			}

//			e.getToolTip().add(e.getItemStack().getItem().getRegistryName().toString());
		}
	}
}
