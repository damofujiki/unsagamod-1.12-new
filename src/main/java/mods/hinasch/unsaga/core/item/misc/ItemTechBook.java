package mods.hinasch.unsaga.core.item.misc;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.util.ChatHandler;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.ability.AbilityCapability;
import mods.hinasch.unsaga.ability.IAbility;
import mods.hinasch.unsaga.ability.IAbilitySelector;
import mods.hinasch.unsaga.ability.AbilityPotentialTableProvider;
import mods.hinasch.unsaga.ability.slot.AbilitySlotList;
import mods.hinasch.unsaga.ability.slot.AbilitySlotType;
import mods.hinasch.unsaga.ability.specialmove.Tech;
import mods.hinasch.unsaga.ability.specialmove.TechInitializer;
import mods.hinasch.unsaga.common.item.IComponentDisplayInfo;
import mods.hinasch.unsaga.core.client.event.UnsagaTooltips;
import mods.hinasch.unsaga.init.UnsagaItems;
import mods.hinasch.unsaga.material.IUnsagaCategoryTool;
import mods.hinasch.unsaga.util.ToolCategory;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemTechBook extends Item implements IAbilitySelector{

	public ImmutableList<IComponentDisplayInfo> displayInfoComponents;

	public ItemTechBook(){
		//		ImmutableList.Builder<IComponentDisplayInfo> list = ImmutableList.builder();
		//		list.add(new ComponentDisplayAbility());
		//		this.displayInfoComponents = list.build();
		this.setMaxStackSize(1);
	}


	@SideOnly(Side.CLIENT)
	@Override
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		if(GuiContainer.isShiftKeyDown()){
			tooltip.add(HSLibs.translateKey("item.unsaga.waza_book.desc1"));
			tooltip.add(HSLibs.translateKey("item.unsaga.waza_book.desc2"));
			if(ClientHelper.getPlayer()!=null && ClientHelper.getPlayer().isCreative()){
				tooltip.add(HSLibs.translateKey("item.unsaga.waza_book.desc3"));
			}
		}else{
			tooltip.add("Press [SHIFT] To Show Description");
		}

		UnsagaTooltips.addTooltips(stack, tooltip, flagIn, UnsagaTooltips.Type.ABILITY);
		//		tooltip.add(TextFormatting.YELLOW+HSLibs.translateKey("item.unsaga.waza_book.desc1"));
		//		tooltip.add(TextFormatting.YELLOW+HSLibs.translateKey("item.unsaga.waza_book.desc2"));
		//    	this.displayInfoComponents.forEach(in -> in.addInfo(stack, ClientHelper.getPlayer(), tooltip, flagIn.isAdvanced()));
	}

	@Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
    	ItemStack target = playerIn.getHeldItemOffhand();
    	ItemStack book = playerIn.getHeldItemMainhand();
    	/** 転写できるかどうか。クリエイティブならなんでも付与できる、サバイバルなら極意書から極意書のみ*/
    	Supplier<Boolean> canTranscript = () ->{
    		if(!target.isEmpty() && handIn==EnumHand.MAIN_HAND){
    			if(playerIn.isCreative()){
    				return AbilityCapability.adapter.hasCapability(target);
    			}else{
    				return target.getItem() instanceof ItemTechBook;
    			}
    		}
    		return false;
    	};
        if(canTranscript.get()){
        		IAbility ab = AbilityCapability.adapter.getCapability(book).getAbilitySlots().getAbility(0);
        		AbilityCapability.adapter.getCapability(target).getAbilitySlots().updateSlot(0, ab);
        		ChatHandler.sendChatToPlayer(playerIn, "Transcripted.");
        		return new ActionResult(EnumActionResult.SUCCESS,book);
        }

        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
	@Override
	public int getMaxAbilitySize() {
		// TODO 自動生成されたメソッド・スタブ
		return 1;
	}
	@Override
	@SideOnly(Side.CLIENT)
	public boolean hasEffect(ItemStack stack)
	{
		return !AbilityCapability.adapter.getCapability(stack).getAbilitySlots().isAllEmpty();
	}




	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if (this.isInCreativeTab(tab))
		{
			ItemStack empty = new ItemStack(UnsagaItems.WAZA_BOOK);
			items.add(empty);

			for(Tech tech:TechInitializer.getAllTechs().stream().sorted().collect(Collectors.toList())){
				ItemStack book = new ItemStack(UnsagaItems.WAZA_BOOK);
				AbilityCapability.adapter.getCapabilityOptional(book).ifPresent(in ->{
					in.getAbilitySlots().updateSlot(0, tech);
					items.add(book);
				});
			}
		}
	}


	@Override
	public AbilitySlotList createAbilityList() {
		// TODO 自動生成されたメソッド・スタブ
		return AbilitySlotList.builder().slot(1, AbilitySlotType.NO_FUNCTION).build();
	}

	public static class EventHandler{

		public EventHandler(){

		}
		public boolean hasTechBookWritten(ItemStack book){

			return !book.isEmpty() && book.getItem()==UnsagaItems.WAZA_BOOK && !AbilityCapability.adapter.getCapability(book).getAbilitySlots().isAllEmpty();
		}

		public boolean canWriteTech(ItemStack tool,ItemStack book){
			if(AbilityCapability.adapter.hasCapability(tool)){
				if(this.hasTechBookWritten(book)){
					if(tool.getItem() instanceof IUnsagaCategoryTool){
						return true;
					}

				}
			}
			return false;
		}

		@SubscribeEvent
		public void onAnvilUpdate(AnvilUpdateEvent e){
			ItemStack book = e.getRight();
			ItemStack tool = e.getLeft();
			if(!tool.isEmpty() && !book.isEmpty()){
				//			UnsagaMod.logger.splitter("1");
				if(!this.canWriteTech(tool,book)){
					return;
				}
				//			UnsagaMod.logger.splitter( "2");
				ItemStack newStack = tool.copy();
				ToolCategory cate = ((IUnsagaCategoryTool)tool.getItem()).getCategory();
				IAbility ab = AbilityCapability.adapter.getCapability(book).getAbilitySlots().getAbility(0);

				if(ab instanceof Tech && AbilityPotentialTableProvider.TABLE_TECH.isTechApplicable(cate, (Tech) ab)){
					//				UnsagaMod.logger.splitter( "3");
					Tech tech = (Tech) ab;

					AbilityCapability.adapter.getCapability(newStack).getAbilitySlots().updateSlot(0, tech);
					e.setOutput(newStack);
					e.setCost(5);


				}


			}
		}
	}
}
