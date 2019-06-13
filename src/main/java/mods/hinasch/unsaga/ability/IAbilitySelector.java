package mods.hinasch.unsaga.ability;

import mods.hinasch.unsaga.ability.slot.AbilitySlotList;

public interface IAbilitySelector {

	public int getMaxAbilitySize();
	public AbilitySlotList createAbilityList();
}
