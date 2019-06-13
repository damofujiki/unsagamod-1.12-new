package mods.hinasch.unsaga.core.inventory;

import mods.hinasch.lib.item.ItemUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class InventorySkillPanel extends InventoryBasic{

	int xpToConsume = 5;

	public static final int XP_TO_CONSUME = 0;

	public InventorySkillPanel() {
		super("skillpanel",false, 9);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public NonNullList<ItemStack> getPlayerPanels(){
		NonNullList<ItemStack> list = ItemUtil.createStackList(7);

		for(int i=0;i<7;i++){
			list.set(i, !ItemUtil.isItemStackNull(this.getStackInSlot(i)) ? this.getStackInSlot(i).copy() : ItemStack.EMPTY);

		}
		return list;
	}
	@Override
	public boolean isUsableByPlayer(EntityPlayer entityplayer) {
		// TODO 自動生成されたメソッド・スタブ

		return entityplayer.openContainer != entityplayer.inventoryContainer;
	}

	@Override
	public int getInventoryStackLimit() {
		// TODO 自動生成されたメソッド・スタブ
		return 1;
	}

//	@Override
//    public int getFieldCount()
//    {
//        return 1;
//    }

//	@Override
//    public int getField(int id)
//    {
//		switch(id){
//		case InventorySkillPanel.XP_TO_CONSUME:
//	        return this.xpToConsume;
//		}
//        return 0;
//    }
//
//	@Override
//    public void setField(int id, int value)
//    {
//		switch(id){
//		case InventorySkillPanel.XP_TO_CONSUME:
//	        this.xpToConsume = value;
//		}
//    }

	public void applyItemStackList(NonNullList<ItemStack> panelList){
		if(panelList!=null){
			for(int i=0;i<7;i++){
				if(ItemUtil.isItemStackPresent(panelList.get(i))){
					this.setInventorySlotContents(i, panelList.get(i));
				}

			}
		}
	}
}
