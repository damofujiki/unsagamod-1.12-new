package mods.hinasch.unsaga.plugin.jei;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.ingredients.IModIngredientRegistration;
import mezz.jei.api.recipe.IRecipeCategoryRegistration;
import mods.hinasch.lib.registry.RegistryUtil;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.init.UnsagaRegistries;
import mods.hinasch.unsaga.minsaga.MinsagaMaterialInitializer;
import mods.hinasch.unsaga.plugin.jei.forgeunsaga.ForgeUnsagaRecipeCategory;
import mods.hinasch.unsaga.plugin.jei.forgeunsaga.ForgeUnsagaRecipeWrapper;
import mods.hinasch.unsaga.plugin.jei.ingredient.ElementIngredientHelper;
import mods.hinasch.unsaga.plugin.jei.ingredient.ElementIngredientRenderer;
import mods.hinasch.unsaga.plugin.jei.ingredient.MinsagaForgingMaterialHelper;
import mods.hinasch.unsaga.plugin.jei.ingredient.MinsagaForgingMaterialRenderer;
import mods.hinasch.unsaga.plugin.jei.ingredient.SpellIngredientHelper;
import mods.hinasch.unsaga.plugin.jei.ingredient.SpellIngredientRenderer;
import mods.hinasch.unsaga.plugin.jei.ingredient.UnsagaMaterialHelper;
import mods.hinasch.unsaga.plugin.jei.ingredient.UnsagaMaterialRenderer;
import mods.hinasch.unsaga.plugin.jei.magicblend.MagicBlendRecipeCategory;
import mods.hinasch.unsaga.plugin.jei.magicblend.MagicBlendRecipeWrapper;
import mods.hinasch.unsaga.plugin.jei.materiallist.MaterialInfoCategory;
import mods.hinasch.unsaga.plugin.jei.materiallist.MaterialInfoWrapper;
import mods.hinasch.unsaga.plugin.jei.materiallist.MaterialWrapper;
import mods.hinasch.unsaga.plugin.jei.materiallist.minsaga.MinsagaMaterialInfoCategory;
import mods.hinasch.unsaga.plugin.jei.materiallist.minsaga.MinsagaMaterialInfoWrapper;
import mods.hinasch.unsaga.plugin.jei.materiallist.minsaga.MinsagaMaterialWrapper;
import mods.hinasch.unsaga.villager.smith.MaterialTransformRecipeInititalizer.MaterialTransform;
import mods.hinasch.unsagamagic.block.UnsagaMagicBlocks;
import mods.hinasch.unsagamagic.spell.SpellRecipeInitializer.SpellRecipeImpl;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class JEIUnsagaPlugin implements IModPlugin{

	public static final String ID_UNSAGA_FORGE = UnsagaMod.MODID+".customize_tool";
	public static final String ID_MAGIC_BLEND = UnsagaMod.MODID+".magic_blend";
	public static final String ID_MATERIAL_LIST = UnsagaMod.MODID+".material_list";
	public static final String ID_MATERIAL_INFO_MINSAGA = UnsagaMod.MODID+".material_list_minsaga";

	public JEIUnsagaPlugin(){

	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
    	IJeiHelpers helper = registry.getJeiHelpers();
    	//レシピカテゴリ（レシピGUIの表示方法など）を追加
    	registry.addRecipeCategories(new ForgeUnsagaRecipeCategory(helper.getGuiHelper()),new MagicBlendRecipeCategory(helper.getGuiHelper())
    			,new MaterialInfoCategory(helper.getGuiHelper()),new MinsagaMaterialInfoCategory(helper.getGuiHelper()));

	}
    @Override
    public void register(@Nonnull IModRegistry registry) {
    	IJeiHelpers helper = registry.getJeiHelpers();

    	//レシピクラス、レシピラッパーファクトリー、固有IDを登録
    	registry.handleRecipes(MaterialTransform.class, ForgeUnsagaRecipeWrapper::new, JEIUnsagaPlugin.ID_UNSAGA_FORGE);
    	registry.handleRecipes(SpellRecipeImpl.class, MagicBlendRecipeWrapper::new, JEIUnsagaPlugin.ID_MAGIC_BLEND);
    	registry.handleRecipes(MaterialWrapper.class, MaterialInfoWrapper::new, JEIUnsagaPlugin.ID_MATERIAL_LIST);
    	registry.handleRecipes(MinsagaMaterialWrapper.class, MinsagaMaterialInfoWrapper::new, JEIUnsagaPlugin.ID_MATERIAL_INFO_MINSAGA);
//    	registry.addRecipeHandlers(new ForgeUnsagaRecipeHandler(),new MagicBlendRecipeHandler()
//    			,new MaterialListHandler());

    	//実際にレシピを追加
    	this.registerRecipeMagicBlend(registry);
    	this.registerRecipeMaterialUnsaga(registry);
    	this.registerRecipeMaterialMinsaga(registry);
    	this.registerRecipeForgeMaterialTransform(registry);

    	//レシピカタリスト（レシピを組み上げる触媒）
    	registry.addRecipeCatalyst(new ItemStack(UnsagaMagicBlocks.DECIPHERING_TABLE), ID_MAGIC_BLEND);

//    	registry.addIngredientInfo(new ItemStack(UnsagaItems.WAZA_BOOK), ItemStack.class, "This book can save your techs and copy to your weapon(Unsaga-weapon only).");
    }

    public void registerRecipeMaterialUnsaga(IModRegistry reg){
    	List<MaterialWrapper> materials = RegistryUtil.getSortedValues(UnsagaRegistries.material(), in -> !in.isEmpty())
    			.stream()
    			.map(MaterialWrapper::new)
    			.collect(Collectors.toList());
    	reg.addRecipes(materials,ID_MATERIAL_LIST);
    }
    public void registerRecipeMaterialMinsaga(IModRegistry reg){
    	List<MinsagaMaterialWrapper> materials2 = MinsagaMaterialInitializer.all()
    			.stream()
    			.map(MinsagaMaterialWrapper::new)
    			.collect(Collectors.toList());

    	reg.addRecipes(materials2,ID_MATERIAL_INFO_MINSAGA);
    }
    public void registerRecipeMagicBlend(IModRegistry reg){

    	reg.addRecipes(UnsagaRegistries.spellRecipe().getValuesCollection(),ID_MAGIC_BLEND);
    }
    public void registerRecipeForgeMaterialTransform(IModRegistry reg){
//    	List<MaterialTransform> recipeMaterial = Lists.newArrayList();
    	reg.addRecipes(UnsagaRegistries.forgeRecipe().getValuesCollection(),ID_UNSAGA_FORGE);
    }
	@Override
	public void registerIngredients(IModIngredientRegistration ingredientRegistry) {

		//Ingredients（右枠に表示されるItemStackなどの部品）
		ingredientRegistry.register(JeiUnsagaIngredients.MATERIAL, UnsagaIngredientListMaker.createUnsagaMaterials(), new UnsagaMaterialHelper(), new UnsagaMaterialRenderer());
		ingredientRegistry.register(JeiUnsagaIngredients.MATERIAL_MINSAGA,UnsagaIngredientListMaker.createMinsagaMaterials(), new MinsagaForgingMaterialHelper(), new MinsagaForgingMaterialRenderer());
		ingredientRegistry.register(JeiUnsagaIngredients.ELEMENT_PAIR, new ArrayList<>(), new ElementIngredientHelper(), new ElementIngredientRenderer());
		ingredientRegistry.register(JeiUnsagaIngredients.SPELL,UnsagaIngredientListMaker.createSpells(), new SpellIngredientHelper(), new SpellIngredientRenderer());
	}
}
