package mods.hinasch.unsaga.minsaga.classes;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;

import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.unsaga.skillpanel.GrowthPanelStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerClassStorage {


	Map<UUID,PlayerClassEntry> playerClassMapper = new HashMap<>();


	public int getLevel(EntityPlayer ep){
		return this.getEntry(ep).level();
	}
	public @Nonnull IPlayerClass getClass(EntityPlayer ep) {
		return this.getEntry(ep).playerClass();
	}

	public @Nonnull PlayerClassEntry getEntry(EntityPlayer ep) {
		UUID uuid = HSLib.isDebug() ? GrowthPanelStorage.UUID_DEBUG : ep.getGameProfile().getId();
		return Optional.ofNullable(this.playerClassMapper.get(uuid))
				.orElse(new PlayerClassEntry(uuid,PlayerClasses.NO_CLASS,0));
	}
	public void setClass(EntityPlayer ep, IPlayerClass clazz,int level) {
		UUID uuid = HSLib.isDebug() ? GrowthPanelStorage.UUID_DEBUG : ep.getGameProfile().getId();
		Optional.ofNullable(uuid).ifPresent(in -> this.playerClassMapper.put(uuid, new PlayerClassEntry(uuid,clazz,level)));
	}


	public Collection<PlayerClassEntry> getAllEntries() {
		return ImmutableSet.copyOf(this.playerClassMapper.values());
	}

	private void setPlayerClasses(Collection<PlayerClassEntry> entries){
		this.playerClassMapper = entries.stream().collect(Collectors.toMap(PlayerClassEntry::getUUID, in -> in));
	}

	public void writeToNBT(NBTTagCompound nbt){
		UtilNBT.writeListToNBT(getAllEntries(), nbt, "player_classes");
	}

	public void readFromNBT(NBTTagCompound nbt){
		this.setPlayerClasses(UtilNBT.readListFromNBT(nbt, "player_classes", PlayerClassEntry.RESTORE));
	}
}
