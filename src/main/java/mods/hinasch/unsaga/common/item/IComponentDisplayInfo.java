package mods.hinasch.unsaga.common.item;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

@Deprecated
public interface IComponentDisplayInfo<T> extends Comparable<IComponentDisplayInfo>{

	public boolean predicate(ItemStack is, EntityPlayer ep, final List dispList, boolean par4);

	public void addInfo(ItemStack is, EntityPlayer ep, final List dispList, boolean par4);


}