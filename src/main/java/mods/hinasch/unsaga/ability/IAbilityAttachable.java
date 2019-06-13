package mods.hinasch.unsaga.ability;

import mods.hinasch.lib.capability.ISyncCapability;
import mods.hinasch.lib.iface.IRequireInitializing;
import mods.hinasch.unsaga.ability.slot.AbilitySlotList;
import net.minecraft.util.NonNullList;

public interface IAbilityAttachable extends IRequireInitializing,ISyncCapability{


	public AbilitySlotList getAbilitySlots();
	public int getSelectedIndex();
	public void setSelectedIndex(int index);
	public void setAbilityList(AbilitySlotList list);
	public boolean isUniqueItem();
	public NonNullList<IAbility> getLearnableUniqueAbilities();
	public void setLearnableUniqueAbilities(NonNullList<IAbility> list);
}
