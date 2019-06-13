package mods.hinasch.unsaga.chest;

import mods.hinasch.lib.iface.INBTWritable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;


public abstract class ChestTrap implements INBTWritable{



	public static ChestTrap restore(NBTTagCompound input){
		if(input.hasKey("key")){
			String key = input.getString("key");
			return ChestTraps.getTrap(new ResourceLocation(key));
		}


		return ChestTraps.DUMMY;
	}
	final String name;
	final ResourceLocation res;
	public ChestTrap(Integer number,String name){
		this.name = name;
		this.res = new ResourceLocation(name);

	}
	public abstract void activate(IChestBehavior chest,EntityPlayer ep);

	/** 罠作動させた時、罠が消えるかどうか*/
	public boolean canRemove(IChestBehavior chest,EntityPlayer ep){
		return true;
	}

	@Override
	public void writeToNBT(NBTTagCompound stream) {
		stream.setString("key", this.res.toString());

	}
}
