package mods.hinasch.unsaga.core.inventory.container;

import java.util.Collection;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import mods.hinasch.lib.container.ContainerBase;
import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.network.PacketGuiButtonBaseNew;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.lib.registry.RegistryUtil;
import mods.hinasch.lib.util.ChatHandler;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.client.gui.GuiMentor;
import mods.hinasch.unsaga.core.item.misc.skillpanel.ISkillPanelContainer;
import mods.hinasch.unsaga.core.net.packet.PacketGuiButtonUnsaga;
import mods.hinasch.unsaga.core.world.UnsagaWorldCapability;
import mods.hinasch.unsaga.init.UnsagaGui;
import mods.hinasch.unsaga.init.UnsagaRegistries;
import mods.hinasch.unsaga.minsaga.classes.IPlayerClass;
import mods.hinasch.unsaga.minsaga.classes.PlayerClasses;
import mods.hinasch.unsaga.skillpanel.SkillPanelAPI;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

/**
 *
 * 道場（クラス修練所）
 *
 */
public class ContainerMentor extends ContainerBase{

	final World world;
	final IMerchant mentor;
	final EntityPlayer customer;
	IPlayerClass selected = PlayerClasses.NO_CLASS;

	public ContainerMentor(World world,EntityPlayer ep,IMerchant mentor) {
		super(ep, new InventoryBasic("mentor",false,1));
		this.world = world;
		this.mentor = mentor;
		this.mentor.setCustomer(ep);
		this.customer = ep;

		this.addSlotToContainer(new Slot(this.inv, 0, 134, 61));
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
	public boolean canInteractWith(EntityPlayer var1) {

		return this.customer!=null && this.customer.equals(var1);
	}


	@Override
	public int getGuiID() {
		// TODO 自動生成されたメソッド・スタブ
		return UnsagaGui.Type.MENTOR.getMeta();
	}

	public OptionalInt getChangeableClassLevel(){
		Collection<ISkillPanelContainer> playerPanels = SkillPanelAPI.getPlayerPanels(ep, in ->selected.requiredSkills().contains(in.panel()));
		if(this.selected.requiredSkills().stream().allMatch(in -> SkillPanelAPI.hasPanel(ep, in,true))){

//			UnsagaMod.logger.trace("panels", playerPanels);
			return IntStream.range(1, 6).filter(in -> this.canChangeClass(in, playerPanels)).max();
		}
		return OptionalInt.empty();
	}
	public void consumePayment(){
		if(this.isClassRequirePayment() && this.hasInvPayment()){
			this.inv.getStackInSlot(0).shrink(1);
		}
	}
	public boolean hasInvPayment(){
		return !this.inv.getStackInSlot(0).isEmpty() && this.inv.getStackInSlot(0).getItem()==Items.DIAMOND;
	}

	/** クラス変更に支払いが必要か*/
	public boolean isClassRequirePayment(){
		return !this.selected.equals(PlayerClasses.NO_CLASS);
	}

	/** クラス変更の対価を満たしているか*/
	public boolean isFulfillPayment(){
		if(!this.isClassRequirePayment()){
			return true;
		}
		return this.hasInvPayment();
	}

	public boolean isCurrentClassSame(){
		return UnsagaWorldCapability.playerClassStorage(world).getClass(ep).equals(selected);
	}

	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer)
	{
		super.onContainerClosed(par1EntityPlayer);
		UnsagaWorldCapability.playerClassStorage(world).getClass(ep).refleshClassModifier(ep);
	}
	@Override
	public void onPacketData() {
		// TODO 自動生成されたメソッド・スタブ
		if(this.argsSent.hasKey("selected")){
			String name = this.argsSent.getString("selected");
			IPlayerClass cls = RegistryUtil.getValue(UnsagaRegistries.playerClass(), UnsagaMod.MODID, name);
			this.selected = cls;
		}

		if(this.buttonID==GuiMentor.BUTTON_ACCEPT){
			if(this.isFulfillPayment() && !this.isCurrentClassSame()){
				this.consumePayment();
				int level = this.getChangeableClassLevel().isPresent() ? this.getChangeableClassLevel().getAsInt() : 0;
				UnsagaWorldCapability.playerClassStorage(world).setClass(ep, selected, level);
				if(WorldHelper.isServer(world)){
					HSLib.packetDispatcher().sendTo(PacketSyncCapability.create(UnsagaWorldCapability.CAPA,UnsagaWorldCapability.ADAPTER.getCapability(world)), (EntityPlayerMP) ep);
				}

				ChatHandler.sendChatToPlayer(ep, HSLibs.translateKey("gui.mentor.changed_class",this.selected.equals(PlayerClasses.NO_CLASS) ? 0 : level ,this.selected.getLocalized()));
				this.ep.closeScreen();
			}
		}

		UnsagaMod.logger.trace("button", this.buttonID);

	}

	/**
	 * 全てのパネルが望むレベル以上かどうか
	 * @param desired
	 * @param panels
	 * @return
	 */
	private boolean canChangeClass(int desired,Collection<ISkillPanelContainer> panels){

		return panels.stream().allMatch(in ->in.level()>=desired );
	}
}
