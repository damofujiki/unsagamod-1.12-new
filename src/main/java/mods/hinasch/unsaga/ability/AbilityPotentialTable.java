package mods.hinasch.unsaga.ability;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Table;

import mods.hinasch.unsaga.util.ToolCategory;

public class AbilityPotentialTable<T>{

	Table<ToolCategory,T,List<IAbility>> map = HashBasedTable.create();


	public List<IAbility> get(ToolCategory cate,T tag){
		return Optional.ofNullable(map.get(cate, tag)).orElse(this.getEmptyList());
	}

	public List<IAbility> getEmptyList(){
		return ImmutableList.of();
	}
}
