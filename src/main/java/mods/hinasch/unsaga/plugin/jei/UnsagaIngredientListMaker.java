package mods.hinasch.unsaga.plugin.jei;

import java.util.List;
import java.util.stream.Collectors;

import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.material.UnsagaMaterialInitializer;
import mods.hinasch.unsaga.minsaga.MinsagaMaterial;
import mods.hinasch.unsaga.minsaga.MinsagaMaterialInitializer;
import mods.hinasch.unsaga.plugin.jei.ingredient.SpellWrapper;
import mods.hinasch.unsagamagic.spell.SpellInitializer;

public class UnsagaIngredientListMaker {


	public static List<UnsagaMaterial> createUnsagaMaterials(){
		return UnsagaMaterialInitializer.getAllMaterials()
				.stream()
				.sorted()
				.filter(in -> !in.itemStack().isEmpty())
				.collect(Collectors.toList());
	}

	public static List<MinsagaMaterial> createMinsagaMaterials(){
		return MinsagaMaterialInitializer.all().stream().sorted().collect(Collectors.toList());
	}

	public static List<SpellWrapper> createSpells(){
		return SpellInitializer.all()
				.stream()
				.sorted()
				.map(SpellWrapper::new)
				.collect(Collectors.toList());
	}
}
