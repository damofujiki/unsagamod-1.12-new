package mods.hinasch.unsaga.core.inventory.container;

import mods.hinasch.lib.container.ContainerBase;
import mods.hinasch.lib.network.PacketGuiButtonBaseNew;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.net.packet.PacketGuiButtonUnsaga;
import mods.hinasch.unsaga.init.UnsagaGui;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class ContainerCarrier extends ContainerBase{

	final World world;
	final IMerchant mentor;
	final EntityPlayer customer;

	public ContainerCarrier(World world,EntityPlayer ep,IMerchant mentor) {
		super(ep, new InventoryBasic("mentor",false,1));
		this.world = world;
		this.mentor = mentor;
		this.mentor.setCustomer(ep);
		this.customer = ep;
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {

		return this.customer!=null && this.customer.equals(var1);
	}
	@Override
	public PacketGuiButtonBaseNew getPacketGuiButton(int guiID, int buttonID,
			NBTTagCompound args) {
		// TODO 自動生成されたメソッド・スタブ

		return PacketGuiButtonUnsaga.create(UnsagaGui.Type.fromMeta(guiID), buttonID, args);//PacketGuiButtonUnsaga.create(UnsagaGui.Type.EQUIPMENT, buttonID, args);
	}

	@Override
	public SimpleNetworkWrapper getPacketPipeline() {
		// TODO 自動生成されたメソッド・スタブ
		return UnsagaMod.PACKET_DISPATCHER;
	}

	@Override
	public int getGuiID() {
		// TODO 自動生成されたメソッド・スタブ
		return UnsagaGui.Type.CARRIER.getMeta();
	}

	@Override
	public void onPacketData() {
		// TODO 自動生成されたメソッド・スタブ

	}

}
