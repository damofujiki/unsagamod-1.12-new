package mods.hinasch.unsaga.ability.slot;

import java.util.List;
import java.util.Set;

import mods.hinasch.unsaga.ability.IAbility;
import mods.hinasch.unsaga.common.UnsagaWeightType;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.util.ToolCategory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

public interface IAbilitySlot {

	public int getIndex();
	public boolean isSlotLearnable();
	public AbilitySlotType getType();
	public IAbility ability();
	@Deprecated
	public void initPotentialAbility(ToolCategory category,UnsagaMaterial m,UnsagaWeightType weight);
	public Set<IAbility> getLearnableAbilities(ToolCategory category,UnsagaMaterial material,UnsagaWeightType weight);
	public void addTooltip(List<String> list,boolean isSelected);
	public void addTooltipPotentialAbility(List<String> list,EntityLivingBase el,ItemStack is);
	public AbilitySlot update(IAbility a);
}
