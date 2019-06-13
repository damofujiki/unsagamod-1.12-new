package mods.hinasch.unsaga.core.inventory;

import mods.hinasch.lib.capability.ISyncCapability;
import mods.hinasch.lib.iface.IRequireInitializing;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public interface IAccessoryInventory extends IRequireInitializing,ISyncCapability{



	public int getArmorValue();
	public void setAccessories(NonNullList<ItemStack> list);
	public NonNullList<ItemStack> getAccessories();
	public boolean hasEmptySlot();
	public void setTablet(ItemStack is);
	public ItemStack getTablet();
}
