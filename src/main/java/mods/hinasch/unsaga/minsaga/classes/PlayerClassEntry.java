package mods.hinasch.unsaga.minsaga.classes;

import java.util.UUID;

import mods.hinasch.lib.iface.INBTWritable;
import mods.hinasch.lib.registry.RegistryUtil;
import mods.hinasch.lib.util.UtilNBT.RestoreFunc;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.init.UnsagaRegistries;
import net.minecraft.nbt.NBTTagCompound;

public class PlayerClassEntry implements INBTWritable{

	public static final RestoreFunc<PlayerClassEntry> RESTORE = in ->{
		UUID uuid = in.getUniqueId("uuid");
		IPlayerClass clazz = RegistryUtil.getValue(UnsagaRegistries.playerClass(), UnsagaMod.MODID, in.getString("class"));
		int level = in.hasKey("level") ? in.getInteger("level") : 0;
		return new PlayerClassEntry(uuid,clazz,level);
	};
	final UUID uuid;
	final IPlayerClass clazz;
	final int level;
	public PlayerClassEntry(UUID uuid,IPlayerClass clazz,int level){
		this.uuid = uuid;
		this.clazz = clazz;
		this.level = level;
	}
	@Override
	public void writeToNBT(NBTTagCompound stream) {
		stream.setInteger("level", this.level());
		stream.setString("class", clazz.getRegistryName().toString());
		stream.setUniqueId("uuid", uuid);
	}

	public int level(){
		return this.level;
	}
	public UUID getUUID(){
		return this.uuid;
	}

	public IPlayerClass playerClass(){
		return this.clazz;
	}

}
