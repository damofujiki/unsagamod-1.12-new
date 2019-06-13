package mods.hinasch.unsaga.ability;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.material.UnsagaMaterials;

public class AbilitySpell extends Ability{

	private static BiMap<UnsagaMaterial,IAbility> materialAbilityBimap = HashBiMap.create();
	public AbilitySpell(String name) {
		super(name);
		// TODO 自動生成されたコンストラクター・スタブ
	}


	public static void registerBimap(){
		materialAbilityBimap.put(UnsagaMaterials.CARNELIAN, Abilities.SPELL_FIRE);
		materialAbilityBimap.put(UnsagaMaterials.LAZULI, Abilities.SPELL_WOOD);
		materialAbilityBimap.put(UnsagaMaterials.OPAL, Abilities.SPELL_METAL);
		materialAbilityBimap.put(UnsagaMaterials.RAVENITE, Abilities.SPELL_WATER);
		materialAbilityBimap.put(UnsagaMaterials.TOPAZ, Abilities.SPELL_EARTH);
		materialAbilityBimap.put(UnsagaMaterials.DARK_STONE, Abilities.SPELL_FORBIDDEN);
	}

	public static UnsagaMaterial getMaterialFromAbility(Ability a){
		return materialAbilityBimap.inverse().getOrDefault(a, UnsagaMaterials.DUMMY);
	}

	public static IAbility getAbilityFromMaterial(UnsagaMaterial m){
		return materialAbilityBimap.getOrDefault(m, Abilities.EMPTY);
	}
}
