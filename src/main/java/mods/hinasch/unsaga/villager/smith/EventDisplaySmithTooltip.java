package mods.hinasch.unsaga.villager.smith;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.core.client.gui.GuiBlacksmithUnsaga;
import mods.hinasch.unsaga.init.UnsagaLibrary;
import mods.hinasch.unsaga.material.UnsagaIngredients;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.material.UnsagaMaterialCapability;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import mods.hinasch.unsaga.util.ToolCategory;
import mods.hinasch.unsaga.util.UnsagaTextFormatting;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * TIPSへの表示
 */
public class EventDisplaySmithTooltip {


	@SubscribeEvent
	public void onDisplayToolTip(ItemTooltipEvent ev){
		ItemStack stackSelected = ev.getItemStack();
		Optional.of(ClientHelper.getCurrentGui())
		.filter(in -> !stackSelected.isEmpty())
		.filter(in -> in instanceof GuiBlacksmithUnsaga)
		.map(in ->(GuiBlacksmithUnsaga)in)
		.ifPresent(guiInBlacksmith ->{
			Optional.ofNullable(ToolCategory.FORGEABLES.get(guiInBlacksmith.getCurrentCategory()))
			.ifPresent(selectedCategory ->{

				UnsagaMaterial material = this.findMaterial(stackSelected);
				ev.getToolTip().add(UnsagaTextFormatting.SIGNIFICANT+HSLibs.translateKey("gui.unsaga.smith.material_info", material.getLocalized()));
				if(material.isSuitable(selectedCategory)){
					ev.getToolTip().add(UnsagaTextFormatting.SIGNIFICANT+HSLibs.translateKey("gui.unsaga.smith.suitable_info", selectedCategory.getLocalized()));
				}
			});






			ValidPayments.findValue(stackSelected).ifPresent(in ->
			ev.getToolTip().add(UnsagaTextFormatting.SIGNIFICANT+HSLibs.translateKey("gui.unsaga.smith.payment_info",in.getName())));

		});


	}

	private UnsagaMaterial findMaterial(ItemStack stack){
		List<UnsagaMaterial> list = new ArrayList<>();

		UnsagaMaterialCapability.adapter.getCapabilityOptional(stack)
		.ifPresent(in -> list.add(in.getMaterial()));

		UnsagaIngredients.find(stack)
		.ifPresent(in -> list.add(in.first()));

		UnsagaLibrary.CATALOG_MATERIAL.find(stack)
		.ifPresent(in ->list.add(in.getMaterial()));


		return Optional.of(list).filter(in ->!in.isEmpty()).map(in -> in.get(0)).orElse(UnsagaMaterials.DUMMY);
	}
}
