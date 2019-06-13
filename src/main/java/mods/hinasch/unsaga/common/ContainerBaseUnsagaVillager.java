package mods.hinasch.unsaga.common;

import mods.hinasch.lib.container.ContainerBase;
import mods.hinasch.lib.network.PacketGuiButtonBaseNew;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.net.packet.PacketGuiButtonUnsaga;
import mods.hinasch.unsaga.init.UnsagaGui;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public abstract class ContainerBaseUnsagaVillager extends ContainerBase{

	public final EntityPlayer ep;
	public final InventoryPlayer invPlayer;
	public final IMerchant theMerchant;
	public ContainerBaseUnsagaVillager(final IMerchant par1,World par2,EntityPlayer ep){
		super(ep,null);
		this.ep = ep;
		this.invPlayer = ep.inventory;
		this.theMerchant = par1;
		this.theMerchant.setCustomer(ep);
	}

	@Override
	public PacketGuiButtonBaseNew getPacketGuiButton(int guiID,int buttonID,NBTTagCompound args){
		return PacketGuiButtonUnsaga.create(UnsagaGui.Type.fromMeta(guiID),buttonID,args);
	}

	@Override
	public SimpleNetworkWrapper getPacketPipeline() {
		// TODO 自動生成されたメソッド・スタブ
		return UnsagaMod.PACKET_DISPATCHER;
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		if(this.ep!=null && this.ep.equals(entityplayer)){
			return this.ep.openContainer != entityplayer.inventoryContainer;
		}
		return false;
	}

}
