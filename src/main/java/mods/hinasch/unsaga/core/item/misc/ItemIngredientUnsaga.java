package mods.hinasch.unsaga.core.item.misc;

import mods.hinasch.unsaga.material.UnsagaIngredients;
import mods.hinasch.unsaga.material.UnsagaIngredients.IngredientType;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemIngredientUnsaga extends Item implements IItemColor{

	final IngredientType ingredient;


	public ItemIngredientUnsaga(IngredientType ingredient){
		this.ingredient = ingredient;
		this.setUnlocalizedName(this.ingredient.getName());
	}

	public IngredientType asIngredient(){
		return null;
	}
	@Override
	public int colorMultiplier(ItemStack stack, int tintIndex) {
		return UnsagaIngredients.getMaterialColorFrom(this);
	}


	@Override
    public int getItemBurnTime(ItemStack itemStack)
    {
        return this.asIngredient().burnTime().orElse(-1);
    }
}
