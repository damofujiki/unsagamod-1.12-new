package mods.hinasch.unsaga.core.item.misc.skillpanel;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.util.ChatHandler;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.Statics;
import mods.hinasch.unsaga.skillpanel.ISkillPanel;
import mods.hinasch.unsaga.skillpanel.SkillPanel;
import mods.hinasch.unsaga.skillpanel.SkillPanelInitializer;
import mods.hinasch.unsaga.util.UnsagaTextFormatting;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemSkillPanel extends Item implements IItemColor{

	protected final int[] iconColors = {0x7cfc00,0x1e90ff,0xffc0cb,0xff8c00,0xdc143c};

	public ItemSkillPanel(){

		EnumSet.allOf(SkillPanel.IconType.class)
		.forEach(type ->{
			ResourceLocation res = new ResourceLocation(type.getJsonName());
			this.addPropertyOverride(res, new IconGetter(type));
		});
	}

	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack)
	{
		return SkillPanelCapability.adapter.getCapabilityOptional(stack)
				.map(in ->in.hasJointed())
				.orElse(stack.isItemEnchanted());
	}
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
	{
		ItemStack itemStackIn = playerIn.getHeldItem(handIn);
		ActionResult result = Optional.of(playerIn)
				.filter(in ->in.isSneaking())
				.filter(in ->in.isCreative())
				.map(in ->new ActionResult(EnumActionResult.SUCCESS,itemStackIn))
				.orElse(new ActionResult(EnumActionResult.PASS, itemStackIn));
		if(result.getResult()==EnumActionResult.SUCCESS){
			ChatHandler.sendChatToPlayer(playerIn, "cleared skill panel data!");
		}
		return result;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		SkillPanelCapability.adapter.getCapabilityOptional(stack)
		.filter(in ->ClientHelper.getPlayer()!=null)
		.ifPresent(in ->{
			ISkillPanel panel = in.panel();
			int lv = in.level();
			tooltip.add(HSLibs.translateKey("skillPanel."+panel.getUnlocalizedName()+".name"));
			tooltip.add("Level "+lv);
			if(GuiContainer.isShiftKeyDown()){
				if(!panel.getTranslatedDescription().isEmpty()){
					tooltip.add(panel.getTranslatedDescription());
				}
				panel.panelBonus().addTips(tooltip);
			}else{
				tooltip.add(UnsagaTextFormatting.SIGNIFICANT+"Push [Shift Key] To Display Description and Panel Bonus.");
			}
		});

	}
	@Override
	public int colorMultiplier(ItemStack stack, int tintIndex) {
		return SkillPanelCapability.adapter.getCapabilityOptional(stack)
				.filter(in ->tintIndex==0)
				.map(in ->iconColors[MathHelper.clamp(in.level()-1, 0, 4)])
				.orElse(Statics.COLOR_NONE);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if(this.isInCreativeTab(tab)){
			IntStream.range(0, 5)
			.sorted()
			.forEach(level ->{
				SkillPanelInitializer.getAllPanels().stream()
				.sorted()
				.map(in ->SkillPanelInitializer.createStack(in, level))
				.forEach(stack -> items.add(stack));
			});
		}
		//		List<ISkillPanel> panels = Lists.newArrayList(UnsagaRegistries.skillPanel().getValuesCollection());
		//		Collections.sort(panels);
		//		for(int i=0;i<5;i++){
		//			for(ISkillPanel panel:panels){
		//				ItemStack stack = SkillPanelInitializer.createStack(panel, i+1);
		//				items.add(stack);
		//			}
		//		}


	}

	@Override
	public int getEntityLifespan(ItemStack itemStack, World world)
	{
		return 0;
	}
	public static class IconGetter implements IItemPropertyGetter{

		final SkillPanel.IconType type;
		public IconGetter(SkillPanel.IconType type){
			this.type = type;
		}
		@Override
		public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {

			return SkillPanelCapability.adapter.getCapabilityOptional(stack)
					.map(in -> in.panel())
					.filter(in ->in.iconType()==this.type)
					.map(in ->1.0F)
					.orElse(0F);
		}

	}
}
