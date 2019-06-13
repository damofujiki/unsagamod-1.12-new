package mods.hinasch.unsaga.villager;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

import mods.hinasch.lib.client.IGuiAttribute;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.entity.UnsagaActionCapability;
import mods.hinasch.unsaga.init.UnsagaGui;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

/**
 *
 * 村人に右クリックした時の反応とGUIの関係
 *
 */
public class VillagerInteractionUnsaga {

	public static final Map<VillagerProfession,UnsagaGui.Type> GUI_VILLAGER ;

	static{
		Map<VillagerProfession,UnsagaGui.Type> b = new HashMap<>();
		b.put(VillagerProfessionUnsaga.BLACKSMITH, UnsagaGui.Type.SMITH);
		b.put(VillagerProfessionUnsaga.MERCHANT, UnsagaGui.Type.BARTERING);
		b.put(VillagerProfessionUnsaga.MAGIC_MERCHANT, UnsagaGui.Type.BARTERING);
		b.put(VillagerProfessionUnsaga.MENTOR, UnsagaGui.Type.MENTOR);
		b.put(VillagerProfessionUnsaga.CARRIER, UnsagaGui.Type.CARRIER);
		GUI_VILLAGER = ImmutableMap.copyOf(b);
	}
	@SubscribeEvent
	public void onInteract(EntityInteract e){
		Optional.of(e.getTarget())
		.filter(in -> in instanceof EntityVillager)
		.map(in ->(EntityVillager)in)
		.filter(in -> VillagerProfessionUnsaga.isUnsagaVillager(in))
		.ifPresent(villager ->{
			e.setCanceled(true);

			UnsagaActionCapability.ADAPTER.getCapabilityOptional(e.getEntityPlayer())
			.filter(in -> !villager.isChild()) //子供を除く
			.ifPresent(in ->{
				in.setMerchant(Optional.of(villager));
				UnsagaMod.logger.trace("villager", villager.getEntityWorld().isRemote,in.getMerchant());
				if(GUI_VILLAGER.containsKey(villager.getProfessionForge())){
					this.openGui(e, GUI_VILLAGER.get(villager.getProfessionForge()));
				}
			});


		});

	}

	private void openGui(EntityInteract e,IGuiAttribute gui){
		if(WorldHelper.isServer(e.getWorld()) && e.getEntityPlayer().openContainer instanceof ContainerPlayer){
			HSLibs.openGui(e.getEntityPlayer(), UnsagaMod.instance, gui.getMeta(), e.getWorld(), XYZPos.createFrom(e.getEntityPlayer()));;
		}
	}
}
