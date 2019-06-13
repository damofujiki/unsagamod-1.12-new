package mods.hinasch.unsaga.plugin.jei.materiallist.minsaga;

import java.util.List;

import com.google.common.collect.Lists;

import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.ingredients.VanillaTypes;
import mezz.jei.api.recipe.IRecipeWrapper;
import mods.hinasch.unsaga.minsaga.MinsagaMaterial;
import mods.hinasch.unsaga.plugin.jei.JeiUnsagaIngredients;
import net.minecraft.item.ItemStack;

public class MinsagaMaterialInfoWrapper implements IRecipeWrapper{

	public MinsagaMaterialWrapper wrapper;
	public MinsagaMaterialInfoWrapper(MinsagaMaterialWrapper wrapper){
		this.wrapper = wrapper;
	}
	@Override
	public void getIngredients(IIngredients ingredients) {

		ingredients.setInput(JeiUnsagaIngredients.MATERIAL_MINSAGA, (MinsagaMaterial)wrapper.getMaterial());
		List<List<ItemStack>> listContainer = Lists.newArrayList();
		listContainer.add(wrapper.getStacks());
		ingredients.setInputLists(VanillaTypes.ITEM, listContainer);
	}

}
