package mods.hinasch.unsaga.ability;

import mods.hinasch.lib.iface.INBTWritable;
import mods.hinasch.lib.registry.RegistryUtil.IUnlocalizedName;
import mods.hinasch.unsaga.ability.specialmove.Tech;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IAbility extends Comparable<IAbility>,INBTWritable,IForgeRegistryEntry<IAbility>,IUnlocalizedName{

	public String getUnlocalizedName();
	public void writeToNBT(NBTTagCompound stream);
	public String getLocalized();
	public String getLocalizedInAbilityList();

	default boolean isTech(){
		return this instanceof Tech;
	}
	default boolean isAbilityEmpty(){
		return this.getRegistryName().getResourcePath().equals("empty");
	}
}
