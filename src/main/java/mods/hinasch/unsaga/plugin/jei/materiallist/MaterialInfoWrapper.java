package mods.hinasch.unsaga.plugin.jei.materiallist;

import java.util.List;

import com.google.common.collect.Lists;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.plugin.jei.JeiUnsagaIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class MaterialInfoWrapper implements IRecipeWrapper {

	public MaterialWrapper wrapper;
	public MaterialInfoWrapper(MaterialWrapper wrapper){
		this.wrapper = wrapper;
	}
	@Override
	public void getIngredients(IIngredients ingredients) {
		ingredients.setInput(JeiUnsagaIngredients.MATERIAL, (UnsagaMaterial)wrapper.getMaterial());

		List<List<ItemStack>> listContainer = Lists.newArrayList();
		listContainer.add(wrapper.getStacks());
		ingredients.setInputLists(VanillaTypes.ITEM, listContainer);
	}
//
//	public boolean isMinsagaForging(){
//		return this.wrapper.getMaterial() instanceof MinsagaMaterial;
//	}
//

	@Override
	public void drawInfo(Minecraft minecraft, int recipeWidth, int recipeHeight, int mouseX, int mouseY) {
		// TODO 自動生成されたメソッド・スタブ

	}



	@Override
	public List<String> getTooltipStrings(int mouseX, int mouseY) {
		// TODO 自動生成されたメソッド・スタブ
		return Lists.newArrayList();
	}

	@Override
	public boolean handleClick(Minecraft minecraft, int mouseX, int mouseY, int mouseButton) {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

}
