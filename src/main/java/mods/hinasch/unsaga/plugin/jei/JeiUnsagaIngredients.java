package mods.hinasch.unsaga.plugin.jei;

import mezz.jei.api.recipe.IIngredientType;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.minsaga.MinsagaMaterial;
import mods.hinasch.unsaga.plugin.jei.ingredient.ElementPair;
import mods.hinasch.unsaga.plugin.jei.ingredient.SpellWrapper;

public class JeiUnsagaIngredients {

	public static final IIngredientType<UnsagaMaterial> MATERIAL = () -> UnsagaMaterial.class;
	public static final IIngredientType<MinsagaMaterial>  MATERIAL_MINSAGA = () -> MinsagaMaterial.class;
	public static final IIngredientType<ElementPair>  ELEMENT_PAIR = () -> ElementPair.class;
	public static final IIngredientType<SpellWrapper>  SPELL = () -> SpellWrapper.class;



}
