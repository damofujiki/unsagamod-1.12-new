package mods.hinasch.unsaga.ability.slot;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;

import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.ability.Abilities;
import mods.hinasch.unsaga.ability.AbilityAPI;
import mods.hinasch.unsaga.ability.AbilityPotentialTableProvider;
import mods.hinasch.unsaga.ability.IAbility;
import mods.hinasch.unsaga.common.UnsagaWeightType;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.util.ToolCategory;
import mods.hinasch.unsaga.util.UnsagaTextFormatting;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class AbilitySlot implements IAbilitySlot{

	final IAbility ability;
//	Set<IAbility> potential = new HashSet<>();
	final AbilitySlotType type;
	final int slotNumber;

	public static final AbilitySlot EMPTY = new AbilitySlot(AbilitySlotType.NO_FUNCTION,-1);

	public AbilitySlot(AbilitySlotType type,int slotnum){
		this(type,slotnum,Abilities.EMPTY);
	}

	public AbilitySlot(AbilitySlotType type,int slotnum,IAbility ab){
		this.type = type;
		this.slotNumber = slotnum;
		this.ability = ab;
	}

	@Override
	public AbilitySlotType getType() {
		// TODO 自動生成されたメソッド・スタブ
		return this.type;
	}


	@Override
	public IAbility ability() {
		// TODO 自動生成されたメソッド・スタブ
		return this.ability;
	}


	@Override
	public void initPotentialAbility(ToolCategory category,UnsagaMaterial m,UnsagaWeightType weight){
		switch(this.type){
		case BLOCKING:
			if(weight==UnsagaWeightType.LIGHT){
				if(category==ToolCategory.KNIFE){
					this.setPotentialAbility(Abilities.KNIFE_GUARD);
				}
				if(category==ToolCategory.SWORD){
					this.setPotentialAbility(Abilities.DEFLECT);
				}
				if(category==ToolCategory.STAFF){
					this.setPotentialAbility(Abilities.BLOCKING);
				}
			}
		case NO_FUNCTION:
			break;
		case PASSIVE:
			List<IAbility> list =AbilityPotentialTableProvider.TABLE_PASSIVE.get(category, m);
			this.setPotentialAbility(list.get(this.getIndex()));
		case TECH:
			this.setPotentialAbility(AbilityPotentialTableProvider.TABLE_TECH.get(category, weight));
		default:
			break;

		}
	}
	@Override
	public Set<IAbility> getLearnableAbilities(ToolCategory category,UnsagaMaterial m,UnsagaWeightType weight) {
//		return this.potential;
		switch(this.type){
		case BLOCKING:
			if(weight==UnsagaWeightType.LIGHT){
				if(category==ToolCategory.KNIFE){
					return Sets.newHashSet(Abilities.KNIFE_GUARD);
				}
				if(category==ToolCategory.SWORD){
					return Sets.newHashSet(Abilities.DEFLECT);
				}
				if(category==ToolCategory.STAFF){
					return Sets.newHashSet(Abilities.BLOCKING);
				}
			}
		case NO_FUNCTION:
			break;
		case PASSIVE:
			List<IAbility> list =AbilityPotentialTableProvider.TABLE_PASSIVE.get(category, m);
			return Sets.newHashSet(list.get(this.getIndex()));
		case TECH:
			return Sets.newHashSet(AbilityPotentialTableProvider.TABLE_TECH.get(category, weight));
		case TECH_MATERIAL:
			return AbilityPotentialTableProvider.TABLE_TECH.getMaterialUniqueAbility(m);
		default:
			break;

		}
		return new HashSet<>();
	}


	/**
	 * ひらめき／引き出し処理をするかどうか
	 */
	@Override
	public boolean isSlotLearnable() {
		switch(this.type){
		case BLOCKING:
			return true;
		case NO_FUNCTION:
			return false;
		case PASSIVE:
			return true;
		case TECH:
			return true;
		case TECH_MATERIAL:
			return true;
		default:
			break;

		}
		return false;
	}



	@Override
	public int getIndex() {
		// TODO 自動生成されたメソッド・スタブ
		return this.slotNumber;
	}


	@Override
	public void addTooltip(List<String> list,boolean isSelected) {
		String header = HSLibs.translateKey("tooltip.unsaga.ability")+String.valueOf(this.getIndex()+1)+":";
		String selected = isSelected ? "> " : "";
		if(this.ability().isAbilityEmpty()){
			list.add(selected+header+this.ability().getLocalizedInAbilityList());
		}else{
			list.add(selected+header+UnsagaTextFormatting.SIGNIFICANT+this.ability().getLocalizedInAbilityList());
		}

	}



	@Override
	public void addTooltipPotentialAbility(List<String> list, EntityLivingBase el, ItemStack is) {
		String header = HSLibs.translateKey("tooltip.unsaga.ability")+String.valueOf(this.getIndex()+1)+":";
		NonNullList<Set<IAbility>> potentials = AbilityAPI.getAbilityTable(is, el);
		if(potentials.size()>this.getIndex() && !potentials.isEmpty()){
			String potentials_str = potentials.get(getIndex()).stream().map(in -> in.getLocalized()).collect(Collectors.joining("/"));
			list.add(header+UnsagaTextFormatting.PROPERTY_LOCKED+potentials_str);
		}else{
			list.add(header+UnsagaTextFormatting.PROPERTY_LOCKED+"--");
		}

	}


	protected void setPotentialAbility(IAbility ability) {
		// TODO 自動生成されたメソッド・スタブ
//		this.potential = Sets.newHashSet(ability);

	}

	protected void setPotentialAbility(Collection<IAbility> ability) {
		// TODO 自動生成されたメソッド・スタブ
//		this.potential = Sets.newHashSet(ability);

	}


	@Deprecated
	@Override
	public AbilitySlot update(IAbility a) {
		// TODO 自動生成されたメソッド・スタブ
		return new AbilitySlot(this.type,this.slotNumber,a);
	}


}
