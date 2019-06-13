package mods.hinasch.unsaga.plugin.jei.ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.OptionalInt;

import mezz.jei.api.ingredients.IIngredientRenderer;
import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.client.RenderHelperHS;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.minsaga.MinsagaMaterial;
import mods.hinasch.unsaga.minsaga.MinsagaMaterialInitializer;
import mods.hinasch.unsaga.minsaga.MinsagaUtil;
import mods.hinasch.unsaga.plugin.jei.forgeunsaga.ForgeUnsagaRecipeCategory;
import mods.hinasch.unsaga.villager.smith.LibraryBlacksmith.IGetItemStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;

public class MinsagaForgingMaterialRenderer implements IIngredientRenderer<MinsagaMaterial>{


	@Override
	public void render(Minecraft minecraft, int xPosition, int yPosition, MinsagaMaterial ingredient) {
		// TODO 自動生成されたメソッド・スタブ
		ItemStack displayItem = MinsagaMaterialInitializer.fromMaterial(ingredient)
				.filter(in -> in instanceof IGetItemStack)
				.map(in ->(IGetItemStack)in)
				.filter(in -> !in.getItemStack().isEmpty())
				.map(in -> in.getItemStack().get(0))
				.orElse(ItemStack.EMPTY);
		//		if(ingredient.checker() instanceof IGetItemStack){
		//			List<ItemStack> list = ((IGetItemStack)ingredient.checker()).getItemStack();
		//			if(!list.isEmpty()){
		//				s = list.get(0);
		//			}
		//		}


		//		if(s== null){
		//			s = new ItemStack(Items.APPLE);
		//		}

		if(displayItem.isEmpty()){
			GlStateManager.enableAlpha();
			ClientHelper.bindTextureToTextureManager(ForgeUnsagaRecipeCategory.RES);
			RenderHelperHS.drawTexturedRect(xPosition, yPosition, 16, 168, 16, 16);

			return;
		}else{
			RenderHelper.enableGUIStandardItemLighting();
			FontRenderer font = getFontRenderer(minecraft, ingredient);
			minecraft.getRenderItem().renderItemAndEffectIntoGUI(null, displayItem, xPosition, yPosition);
			minecraft.getRenderItem().renderItemOverlayIntoGUI(font, displayItem, xPosition, yPosition, null);
			GlStateManager.disableBlend();
			RenderHelper.disableStandardItemLighting();
		}


	}

	@Override
	public List<String> getTooltip(Minecraft minecraft, MinsagaMaterial ingredient, ITooltipFlag tooltipFlag) {
		List<String> list = new ArrayList<>();
		list.add(HSLibs.translateKey("jei."+MinsagaForgingMaterialHelper.ID));
		list.add(ingredient.getLocalized());
		list.addAll(MinsagaUtil.buildModifierTips(ingredient, OptionalInt.of(ingredient.getRepairDamage())));
		return list;
	}

	@Override
	public FontRenderer getFontRenderer(Minecraft minecraft, MinsagaMaterial ingredient) {
		// TODO 自動生成されたメソッド・スタブ
		return minecraft.fontRenderer;
	}


}
