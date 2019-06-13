package mods.hinasch.unsaga.villager.smith;

import mods.hinasch.lib.registry.RegistryUtil;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.Abilities;
import mods.hinasch.unsaga.ability.IAbility;
import mods.hinasch.unsaga.init.UnsagaRegistries;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import mods.hinasch.unsaga.villager.smith.MaterialTransformRecipeInititalizer.MaterialTransform;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

public class AbilityMutationManager {

	public static class Recipe extends IForgeRegistryEntry.Impl<Recipe>{


		final UnsagaMaterial material;

		final IAbility ability;

		public Recipe(String name,UnsagaMaterial material, IAbility ability) {
			super();
			this.setRegistryName(new ResourceLocation(name));
			this.material = material;
			this.ability = ability;
		}
		public IAbility getAbility() {
			return ability;
		}

		public UnsagaMaterial getMaterial() {
			return material;
		}
	}


	public static final ResourceLocation ABILITY_MUTATION = new ResourceLocation(UnsagaMod.MODID,"ability_mutation");
	/** 素材から変異するアビリティをひく。*/
	public static IAbility getMutableAbility(UnsagaMaterial m){
		return UnsagaRegistries.abilityMutation().getValuesCollection().stream().filter(in -> in.getMaterial()==m).map(in -> in.getAbility()).findFirst().orElse(Abilities.EMPTY);
	}


	public static boolean canMutate(UnsagaMaterial m){
		return UnsagaRegistries.abilityMutation().getValuesCollection().stream().anyMatch(in -> in.getMaterial()==m);
	}

	@SubscribeEvent
	public void makeRegistry(RegistryEvent.NewRegistry ev){
		new RegistryBuilder().setName(ABILITY_MUTATION).setType(Recipe.class)
		.setIDRange(0, 4096).create();
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void registerRecipes(RegistryEvent.Register<MaterialTransform> ev){

		RegistryUtil.Helper<Recipe> reg = RegistryUtil.helper(ev, UnsagaMod.MODID, "unsaga");
		reg.register(new Recipe("fire",UnsagaMaterials.CARNELIAN, Abilities.SPELL_FIRE));
		reg.register(new Recipe("water",UnsagaMaterials.RAVENITE, Abilities.SPELL_WATER));
		reg.register(new Recipe("metal",UnsagaMaterials.OPAL, Abilities.SPELL_METAL));
		reg.register(new Recipe("earth",UnsagaMaterials.TOPAZ, Abilities.SPELL_EARTH));
		reg.register(new Recipe("wood",UnsagaMaterials.LAZULI, Abilities.SPELL_WOOD));
		reg.register(new Recipe("forbidden",UnsagaMaterials.DARK_STONE, Abilities.SPELL_FORBIDDEN));
	}
}
