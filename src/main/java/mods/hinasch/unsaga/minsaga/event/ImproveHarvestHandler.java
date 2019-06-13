package mods.hinasch.unsaga.minsaga.event;

import java.util.Set;

import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.unsaga.minsaga.MinsagaForgingCapability;
import mods.hinasch.unsaga.minsaga.MinsagaMaterialInitializer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent.HarvestCheck;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * 採掘レベルアップの実装
 */
public class ImproveHarvestHandler {

	@SubscribeEvent
	public void onHarvestCheck(HarvestCheck e){
		ItemStack held = e.getEntityLiving().getHeldItemMainhand();
		if(ItemUtil.isItemStackPresent(held)){
			if(MinsagaForgingCapability.ADAPTER.hasCapability(held)){
				if(!MinsagaForgingCapability.ADAPTER.getCapability(held).getAbilities().contains(MinsagaMaterialInitializer.Ability.HARVEST)){
					return;
				}
			}
			Set<String> classes = held.getItem().getToolClasses(held);
			int blockHarvestLevel = e.getTargetBlock().getBlock().getHarvestLevel(e.getTargetBlock());
			if(classes.stream().anyMatch(in ->{
				int hl = held.getItem().getHarvestLevel(held, in, e.getEntityPlayer(), e.getTargetBlock());
				return hl + 1 >= blockHarvestLevel;
			})){
				e.setCanHarvest(true);
			}
		}



	}
}
