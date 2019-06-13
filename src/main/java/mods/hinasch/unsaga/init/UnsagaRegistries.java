package mods.hinasch.unsaga.init;

import mods.hinasch.unsaga.ability.IAbility;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.minsaga.classes.IPlayerClass;
import mods.hinasch.unsaga.skillpanel.ISkillPanel;
import mods.hinasch.unsaga.villager.smith.AbilityMutationManager;
import mods.hinasch.unsaga.villager.smith.MaterialTransformRecipeInititalizer.MaterialTransform;
import mods.hinasch.unsagamagic.spell.ISpell;
import mods.hinasch.unsagamagic.spell.SpellRecipeInitializer.ISpellBlendRecipe;
import mods.hinasch.unsagamagic.spell.spellbook.SpellBookTypeInitializer.ISpellBookType;
import mods.hinasch.unsagamagic.spell.tablet.IMagicTablet;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

public class UnsagaRegistries {


	public static IForgeRegistry<ISkillPanel> skillPanel(){
		return GameRegistry.findRegistry(ISkillPanel.class);
	}

	public static IForgeRegistry<ISpell> spell(){
		return GameRegistry.findRegistry(ISpell.class);
	}

	public static IForgeRegistry<IAbility> ability(){
		return GameRegistry.findRegistry(IAbility.class);
	}
	public static IForgeRegistry<MaterialTransform> forgeRecipe(){
		return GameRegistry.findRegistry(MaterialTransform.class);
	}
//	public static IForgeRegistry<UnsagaIngredients.Ingredient> ingredient(){
//		return GameRegistry.findRegistry(UnsagaIngredients.Ingredient.class);
//	}
	public static IForgeRegistry<AbilityMutationManager.Recipe> abilityMutation(){
		return GameRegistry.findRegistry(AbilityMutationManager.Recipe.class);
	}

	public static IForgeRegistry<UnsagaMaterial> material(){
		return GameRegistry.findRegistry(UnsagaMaterial.class);
	}
	public static IForgeRegistry<IMagicTablet> tablet(){
		return GameRegistry.findRegistry(IMagicTablet.class);
	}

	public static IForgeRegistry<ISpellBookType> spellBookType(){
		return GameRegistry.findRegistry(ISpellBookType.class);
	}

	public static IForgeRegistry<ISpellBlendRecipe> spellRecipe(){
		return GameRegistry.findRegistry(ISpellBlendRecipe.class);
	}

	public static IForgeRegistry<IPlayerClass> playerClass(){
		return GameRegistry.findRegistry(IPlayerClass.class);
	}
}
