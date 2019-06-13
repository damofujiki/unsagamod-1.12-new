package mods.hinasch.unsaga.core.inventory;

import mods.hinasch.lib.container.inventory.InventoryHandler.ITransferLogic;
import mods.hinasch.unsaga.core.inventory.container.ContainerBlacksmithUnsaga;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;

public class InventorySmithUnsaga extends InventoryBasic implements ITransferLogic{


	protected final IMerchant theSmith;
	public InventorySmithUnsaga(EntityPlayer ep, IMerchant theMerchant) {
		super("",false,4);
		this.theSmith = theMerchant;

	}

	public ItemStack getPayment(){
		return this.getStackInSlot(0);
	}

	public ItemStack getBaseItem(){
		return this.getStackInSlot(1);
	}

	public ItemStack getSubMaterial(){
		return this.getStackInSlot(2);
	}

	public ItemStack getResult(){
		return this.getStackInSlot(3);
	}



	@Override
	public String getName() {
		// TODO 自動生成されたメソッド・スタブ
		return "unsaga.smith.inventory";
	}



	@Override
	public int getInventoryStackLimit() {
		// TODO 自動生成されたメソッド・スタブ
		return 64;
	}



	@Override
	public boolean isUsableByPlayer(EntityPlayer entityplayer) {
		// TODO 自動生成されたメソッド・スタブ

		return this.theSmith!=null && this.theSmith.getCustomer() == entityplayer;
	}

	@Override
	public int getStackLimit(int invNum) {
		if(invNum==ContainerBlacksmithUnsaga.BASE || invNum==ContainerBlacksmithUnsaga.SUB){
			return ContainerBlacksmithUnsaga.STACKLIMIT_MATERIAL;
		}
		return this.getInventoryStackLimit();
	}




}
