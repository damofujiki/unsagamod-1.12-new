package mods.hinasch.unsaga.plugin.jei.materiallist.minsaga;

import java.util.List;

import com.google.common.collect.Lists;

import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeCategory;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.minsaga.MinsagaMaterial;
import mods.hinasch.unsaga.plugin.jei.JEIUnsagaPlugin;
import mods.hinasch.unsaga.plugin.jei.JeiUnsagaIngredients;
import mods.hinasch.unsaga.plugin.jei.ingredient.MinsagaForgingMaterialRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

public class MinsagaMaterialInfoCategory implements IRecipeCategory<MinsagaMaterialInfoWrapper>{

	public static final ResourceLocation RES = new ResourceLocation(UnsagaMod.MODID,"textures/gui/container/material_list.png");
	private final String title;
	private final IDrawable background;
	private final IDrawable icon;
	public MinsagaMaterialInfoCategory(IGuiHelper guiHelper){
		this.title = HSLibs.translateKey("jei."+this.getUid()+".title");

		this.background = guiHelper.createDrawable(RES, 8, 8, 160, 56);
		this.icon = guiHelper.createDrawable(RES, 0, 168, 16, 16);
	}
	@Override
	public String getUid() {
		// TODO 自動生成されたメソッド・スタブ
		return JEIUnsagaPlugin.ID_MATERIAL_INFO_MINSAGA;
	}

	@Override
	public String getTitle() {
		// TODO 自動生成されたメソッド・スタブ
		return this.title;
	}

	@Override
	public IDrawable getBackground() {
		// TODO 自動生成されたメソッド・スタブ
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		// TODO 自動生成されたメソッド・スタブ
		return this.icon;
	}

	@Override
	public void drawExtras(Minecraft minecraft) {
		// TODO 自動生成されたメソッド・スタブ

	}



	@Override
	public void setRecipe(IRecipeLayout recipeLayout, MinsagaMaterialInfoWrapper recipeWrapper, IIngredients ingredients) {

		MinsagaMaterial m = ingredients.getInputs(JeiUnsagaIngredients.MATERIAL_MINSAGA).get(0).get(0);
		recipeLayout.getIngredientsGroup(JeiUnsagaIngredients.MATERIAL_MINSAGA).init(0, true, new MinsagaForgingMaterialRenderer(), 39, 16, 16, 16, 0,0);
		recipeLayout.getIngredientsGroup(JeiUnsagaIngredients.MATERIAL_MINSAGA).set(0,ingredients.getInputs(JeiUnsagaIngredients.MATERIAL_MINSAGA).get(0));


		recipeLayout.getItemStacks().init(1, true, 111, 16);
		recipeLayout.getItemStacks().set(1,ingredients.getInputs(VanillaTypes.ITEM).get(0));
	}

	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		// TODO 自動生成されたメソッド・スタブ
		return Lists.newArrayList();
	}
	@Override
	public String getModName() {
		// TODO 自動生成されたメソッド・スタブ
		return UnsagaMod.MODID;
	}

}
