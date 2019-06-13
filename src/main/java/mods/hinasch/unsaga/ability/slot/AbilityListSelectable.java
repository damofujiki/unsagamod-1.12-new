package mods.hinasch.unsaga.ability.slot;

import java.util.List;
import java.util.stream.Collectors;

import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.core.item.misc.ItemTechBook;
import mods.hinasch.unsaga.util.UnsagaTextFormatting;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class AbilityListSelectable extends AbilitySlotList{

	public AbilityListSelectable(Builder builder) {
		super(builder);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	int selected = 0;



	/**
	 * 選択中の技
	 * @return
	 */
	@Override
	public IAbilitySlot getCurrentTechSlot(ItemStack is){
		return this.getSlot(selected);
	}

	public void addTooltips(List<String> list,EntityPlayer el,ItemStack is){
		if(el==null)return;

		boolean isTechBook = is.getItem() instanceof ItemTechBook;

		if(isTechBook){
			this.addTechBookToolTips(list, el, is);
		}else{
			if(GuiContainer.isShiftKeyDown()){
				if(GuiContainer.isAltKeyDown()){
					for(int i=0;i<this.getSize();i++){
						this.getSlot(i).addTooltipPotentialAbility(list,el,is);
					}
				}else{
					for(int i=0;i<this.getSize();i++){
						this.getSlot(i).addTooltip(list,this.selected == i);
					}
					list.add(UnsagaTextFormatting.PROPERTY_LOCKED+"[ALT] to Show Potential Abilities");
				}
			}else{
				String abs = this.abilitySlots.stream().map(in -> in.ability().getLocalized()).collect(Collectors.joining("/"));
				list.add(HSLibs.translateKey("tooltip.unsaga.ability")+":"+abs);
				list.add(UnsagaTextFormatting.PROPERTY_LOCKED+"[SHIFT] to Expand Ability Info");
			}
		}

	}
	/**
	 * 技スロットの選択を次に送る。
	 */
	public void nextTechSlot(){
		this.selected ++;
		if(this.selected>=this.getSize()){
			this.selected = 0;
		}
	}

	@Override
	public void writeToNBT(NBTTagCompound stream) {
		super.writeToNBT(stream);
		stream.setInteger("selected", this.selected);
	}

	public void load(NBTTagCompound nbt){
//		super.loadAbilities(nbt);
		this.selected = nbt.getInteger("selected");
	}
}
