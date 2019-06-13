package mods.hinasch.unsaga.skillpanel;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;

import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.util.UtilNBT;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class GrowthPanelStorage {

	public static final int MAX_SIZE = 7;
	public static final UUID UUID_DEBUG = UUID.fromString("db8ff9a2-9ef5-4371-9e98-42d694add74a");
	Map<UUID,NonNullList<ItemStack>> panelDataPerUser = new HashMap<>();

	public static NonNullList makeEmptyList(){
		return NonNullList.withSize(MAX_SIZE, ItemStack.EMPTY);
	}
	public NonNullList<ItemStack> getPanels(UUID uuid) {
		UUID id = HSLib.isDebug() ? UUID_DEBUG : uuid;

		return Optional.ofNullable(this.panelDataPerUser.get(id))
				.orElse(makeEmptyList());
	}



	public void updatePanels(UUID uuid, NonNullList<ItemStack> list) {
		this.panelDataPerUser.put(HSLib.isDebug() ?UUID_DEBUG : uuid , list);
	}


	public void dumpData() {
		// TODO 自動生成されたメソッド・スタブ

	}

	public Map<UUID, NonNullList<ItemStack>> panelMap() {
		// TODO 自動生成されたメソッド・スタブ
		return ImmutableMap.copyOf(this.panelDataPerUser);
	}

	public void clear() {
		// TODO 自動生成されたメソッド・スタブ
		this.panelDataPerUser.clear();
	}

	public void writeToNBT(NBTTagCompound comp){
		UtilNBT.comp(comp)
		.writeList("panel_per_user",
				this.panelMap().entrySet().stream()
				.map(PanelEntry::new)
				.collect(Collectors.toList()));
	}

	public void readFromNBT(NBTTagCompound nbt){
		UtilNBT.comp(nbt)
		.setToField("panel_per_user", (in,key)->{
			UtilNBT.readListFromNBT(nbt, key, PanelEntry.RESTORE_FUNC)
			.forEach(entry -> this.updatePanels(entry.getUUID(), entry.list()));
		});

	}
}
