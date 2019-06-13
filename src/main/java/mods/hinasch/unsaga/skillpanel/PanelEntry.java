package mods.hinasch.unsaga.skillpanel;

import java.util.Map.Entry;
import java.util.UUID;

import mods.hinasch.lib.iface.INBTWritable;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.lib.util.UtilNBT.RestoreFunc;
import mods.hinasch.unsaga.UnsagaMod;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

public class PanelEntry implements INBTWritable{

	final UUID uuid;
	final NonNullList<ItemStack> list;
	public PanelEntry(UUID uuid,NonNullList<ItemStack> panels){

		this.uuid = uuid;
		this.list = panels;
	}
	public PanelEntry(Entry<UUID,NonNullList<ItemStack>> entry){

		this.uuid = entry.getKey();
		this.list = entry.getValue();
	}
	public UUID getUUID(){
		return this.uuid;
	}

	public NonNullList<ItemStack> list(){
		return this.list;

	}
	@Override
	public void writeToNBT(NBTTagCompound stream) {
		UtilNBT.comp(stream)
		.setUUID("uuid", uuid)
		.setItemStacks("items", list);

		UnsagaMod.logger.trace(this.getClass().getName(),uuid, this.list);

	}

	public static final RestoreFunc<PanelEntry> RESTORE_FUNC = input ->
		UtilNBT.comp(input).mapTo(in ->{
			UUID uuid = in.get().getUniqueId("uuid");
			return new PanelEntry(uuid,in.getItemStacks("items", GrowthPanelStorage.MAX_SIZE));
		});

}