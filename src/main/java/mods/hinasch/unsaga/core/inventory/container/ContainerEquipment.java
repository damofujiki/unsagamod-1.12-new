package mods.hinasch.unsaga.core.inventory.container;

import java.util.Optional;
import java.util.OptionalInt;

import mods.hinasch.lib.container.ContainerBase;
import mods.hinasch.lib.container.inventory.InventoryHandler;
import mods.hinasch.lib.container.inventory.InventoryHandler.TransferStackLogic;
import mods.hinasch.lib.network.PacketGuiButtonBaseNew;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.AbilityCapability;
import mods.hinasch.unsaga.ability.AbilitySpell;
import mods.hinasch.unsaga.ability.RefleshAbilityObserver;
import mods.hinasch.unsaga.core.advancement.UnsagaTriggers;
import mods.hinasch.unsaga.core.client.gui.GuiEquipment.IconType;
import mods.hinasch.unsaga.core.inventory.AccessorySlotCapability;
import mods.hinasch.unsaga.core.inventory.IAccessoryInventory;
import mods.hinasch.unsaga.core.inventory.InventoryEquipment;
import mods.hinasch.unsaga.core.inventory.slot.SlotAccessory;
import mods.hinasch.unsaga.core.inventory.slot.SlotTablet;
import mods.hinasch.unsaga.core.item.wearable.ItemAccessoryUnsaga;
import mods.hinasch.unsaga.core.net.packet.PacketGuiButtonUnsaga;
import mods.hinasch.unsaga.core.world.chunk.IUnsagaChunk;
import mods.hinasch.unsaga.core.world.chunk.UnsagaChunkCapability;
import mods.hinasch.unsaga.init.UnsagaGui;
import mods.hinasch.unsaga.status.UnsagaXPCapability;
import mods.hinasch.unsagamagic.item.ItemMagicTablet;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class ContainerEquipment extends ContainerBase{


	protected EntityPlayer player;
	protected InventoryEquipment invEquipment;
	protected InventoryBasic dummy;
	protected IInventory invPlayer;

	//	public static final String KEY = "unsaga.equipment";


	public ContainerEquipment(final InventoryPlayer playerIn, final EntityPlayer ep)
	{
		super(ep,null);
		this.player = ep;
		this.invPlayer = playerIn;
		this.invEquipment = new InventoryEquipment(ep);
		this.inv = this.invEquipment;
		this.dummy = new InventoryBasic("dummy",false,10);

		this.setTransferStackLogic(new TransferStackLogic(this.player.inventory,this.invEquipment,in -> (in instanceof SlotAccessory)||(in instanceof SlotTablet)){

			@Override
			public OptionalInt findSlot(final InventoryHandler invToSet,final Slot slot,final EnumSide side){
				if(!slot.getStack().isEmpty()){
					if(side==TransferStackLogic.EnumSide.SELF_TO_OTHER){
						if(slot.getStack().getItem() instanceof ItemAccessoryUnsaga){
							if(invToSet.getInv().getStackInSlot(0).isEmpty()){
								return OptionalInt.of(0);
							}
							if(invToSet.getInv().getStackInSlot(1).isEmpty()){
								return OptionalInt.of(1);
							}
						}
						if(slot.getStack().getItem() instanceof ItemMagicTablet){
							if(invToSet.getInv().getStackInSlot(2).isEmpty()){
								return OptionalInt.of(2);
							}
						}

					}else{
						return super.findSlot(invToSet, slot, side);
					}
				}
				return OptionalInt.empty();
			}
		});
		if(WorldHelper.isServer(ep.getEntityWorld()))UnsagaTriggers.OPEN_GUI_UNSAGA.trigger((EntityPlayerMP) ep);
//		ep.addStat(UnsagaModCore.instance().achievements.openInv, 1);

		AccessorySlotCapability.adapter.getCapabilityOptional(ep)
		.ifPresent(this::loadAndSetAccessories);

//		this.setTransferStackLogic(logic);

		this.addSlotToContainer(new SlotAccessory(this.invEquipment, 0, 28, 53-(18*2)));
		this.addSlotToContainer(new SlotAccessory(this.invEquipment, 1, 28+(18*2)-8, 53-(18*2)));
		this.addSlotToContainer(new SlotTablet(this.invEquipment, 2, 56,44));

		//
		//		if(WorldHelper.isServer(this.ep.getEntityWorld())){
		//			UnsagaMod.packetDispatcher.sendTo(PacketSyncSkillPanel.create(this.player), (EntityPlayerMP) player);
		//		}

		//		World w = ep.getEntityWorld();
		//		XYZPos pos = XYZPos.createFrom(ep);
		//		EnvironmentalCondition condition = EnvironmentalManager.getCondition(w, pos, w.getBiome(pos), ep);
		//		this.addSlotToContainer(new SlotIcon(this.dummy, 2,8,64));
		//		dummy.setInventorySlotContents(2, ItemIconCondition.getIcon(condition));
		//		this.addSlotToContainer(new SlotTablet(this.invEquipment,2, 28, 53));
		//this.addSlotToContainer(new SlotMerchantResult(par1InventoryPlayer.player, par2IMerchant, this.merchantInventory, 2, 120, 53));
		//int i;

		//		this.attachIcons(dummy, player, MapSkill.MAP_SKILLS,new XY(28,53),input ->((MapSkill)input).getIconFromPlayer(player));
		//		ListHelper.stream(MapSkill.mapSkills).forEach(new Consumer<MapSkill>(){
		//
		//			@Override
		//			public void accept(MapSkill input) {
		//				addSlotToContainer(new SlotIcon(dummy, input.getNumber(), 28+(18*input.getNumber()), 53));
		//				ItemStack is = input.getIconFromPlayer(player);
		//				if(!input.panelChecker.apply(player)){
		//					ComponentSelectableIcon.setNegative(is, true);
		//				}
		//				dummy.setInventorySlotContents(input.getNumber(), is);
		//			}}
		//		);

		UnsagaXPCapability.syncAdditionalXP(ep);
	}

	protected void loadAndSetAccessories(final IAccessoryInventory data){
		this.invEquipment.setInventorySlotContents(0, data.getAccessories().get(0));
		this.invEquipment.setInventorySlotContents(1, data.getAccessories().get(1));
		this.invEquipment.setInventorySlotContents(2, data.getTablet());
	}
	@Override
	public boolean canInteractWith(final EntityPlayer entityplayer) {
		// TODO 自動生成されたメソッド・スタブ
		return this.player.openContainer != player.inventoryContainer;
	}

	@Override
	public void onContainerClosed(final EntityPlayer par1EntityPlayer)
	{
		this.invEquipment.closeInventory(par1EntityPlayer);
		RefleshAbilityObserver.refresh(par1EntityPlayer);
//		this.checkTrigger();
		return;
	}

	@Override
	public PacketGuiButtonBaseNew getPacketGuiButton(final int guiID, final int buttonID,
			final NBTTagCompound args) {
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
		return UnsagaGui.Type.EQUIPMENT.getMeta();
	}

	@Override
    public void onCraftMatrixChanged(final IInventory inventoryIn)
    {
        super.onCraftMatrixChanged(inventoryIn);

        RefleshAbilityObserver.refresh(player);
//        UnsagaMod.logger.trace("chenge", "cragft");
//        if(this.checkTrigger() && WorldHelper.isServer(player.world)){
//        	UnsagaTriggers.READY_MAGIC_ITEM.trigger((EntityPlayerMP) player);
//        }
    }

	private boolean checkTrigger(){
		if(AccessorySlotCapability.adapter.hasCapability(ep)){
//			IAccessoryInventory instance = AccessorySlotCapability.adapter.getCapability(ep);
			final NonNullList<ItemStack> stacks = NonNullList.from(this.invEquipment.getStackInSlot(0), this.invEquipment.getStackInSlot(1));
			return stacks.stream().filter(in -> !in.isEmpty() && AbilityCapability.adapter.hasCapability(in))
			.flatMap(in -> AbilityCapability.adapter.getCapability(in).getAbilitySlots().toNonNullList().stream())
			.anyMatch(in -> in instanceof AbilitySpell);
		}
		return false;
	}

	@Override
	public void onPacketData() {


		final XYZPos p = XYZPos.createFrom(player);
		Optional.ofNullable(IconType.fromMeta(this.buttonID))
		.ifPresent(in ->in.onCotainerCatchPacket(ep, p, this));
//		switch(this.buttonID){
//		case GuiEquipment.BUTTON_OPEN_SKILLS:
//			UnsagaMod.logger.trace("open skills", "called");
//			p = XYZPos.createFrom(player);
//			this.player.closeScreen();
//			HSLibs.openGui(player, UnsagaMod.instance, UnsagaGui.Type.SKILLPANEL.getMeta(), this.player.world, p);
//
//			break;
//			//		case GuiEquipment.BUTTON_OPEN_BLEND:
//			//			if(SkillPanels.hasPanel(this.ep.worldObj, ep, UnsagaMod.skillPanels.spellBlend)){
//			//				p = XYZPos.createFrom(player);
//			//				this.player.closeScreen();
//			//				HSLibs.openGui(player, UnsagaMod.instance, UnsagaGui.Type.BLENDING.getMeta(), this.player.worldObj, p);
//			//			}
//			//			UnsagaMod.logger.trace("open blends", "called");
//			//			break;
//		case GuiEquipment.ROAD_ADVISER:
//			HSLibs.openGui(player, UnsagaMod.instance, UnsagaGui.Type.MAP_FIELD.getMeta(), this.player.world, p);
//			break;
//		case GuiEquipment.CAVERN_EXPLORER:
//			HSLibs.openGui(player, UnsagaMod.instance, UnsagaGui.Type.MAP_DUNGEON.getMeta(), this.player.world, p);
//			break;
//		case GuiEquipment.SPELL_BLEND:
//			HSLibs.openGui(player, UnsagaMod.instance, UnsagaGui.Type.BLENDING.getMeta(), this.player.world, p);
//			break;
//		case GuiEquipment.WEAPON_FORGE:
//			HSLibs.openGui(player, UnsagaMod.instance, UnsagaGui.Type.SMITH_MINSAGA.getMeta(), this.player.world, p);
//			break;
//		case GuiEquipment.EASY_REPAIR:
//			MapAbilityHelper.onUseRepair(ep.world, ep, this);
//			break;
//		case GuiEquipment.EAVESDROP:
//			MapAbilityHelper.onUseEavesdrop(ep.world, ep, this);
//			break;
//		case GuiEquipment.JUMP:
//			MapAbilityHelper.onUseObstacleCrossing(ep.world, ep, this);
//			break;
//		case GuiEquipment.SPEED:
//			MapAbilityHelper.onUseSmartMove(ep.world, ep, this);
//			break;
//		case GuiEquipment.SWIMMING:
//			MapAbilityHelper.onUseSwimming(ep.world, ep, this);
//			break;
//		}
//		if(this.buttonID>=GuiEquipment.ROAD_ADVISER && WorldHelper.isServer(player.world)){
//			UnsagaTriggers.USE_MAP_SKILL.trigger((EntityPlayerMP) player);
//		}

	}

	private void processChestAppear(){
		final World world = ep.getEntityWorld();
		final Chunk chunk = world.getChunkFromBlockCoords(ep.getPosition());
		final IUnsagaChunk instance = UnsagaChunkCapability.ADAPTER.getCapability(chunk);
//		if(instance.getChunkChestInfo().)
	}


//	@Override
//	public ItemStack transferStackInSlot(Slot slot)
//	{
//		InventoryHandler hinvEp = InventoryHandler.create(this.invPlayer);
//		InventoryHandler hinvBlender = InventoryHandler.create(this.invEquipment);
//
//		if((slot instanceof SlotAccessory)){
//			//		if((slot instanceof SlotAccessory) || (slot instanceof SlotTablet)){
//			if(slot.getStack()!=null && hinvEp.getFirstEmptySlotNum().isPresent()){
//				hinvEp.swapFirstEmptySlot(slot);
//			}
//		}else{
//			if(slot.getStack()!=null && hinvBlender.getFirstEmptySlotNum().isPresent()){
//				ItemStack is = slot.getStack();
//				if((is.getItem() instanceof ItemAccessoryUnsaga) && hinvBlender.getEmptySlots(0,1).isPresent()){
//					hinvBlender.mergeSlot(slot,hinvBlender.getEmptySlots(0,1).get());
//				}
//				//				if((is.getItem() instanceof ItemTablet) && hinvBlender.getEmptySlots(2).isPresent()){
//				//					hinvBlender.mergeSlot(slot, hinvBlender.getEmptySlots(2).get());
//				//				}
//
//			}
//		}
//		return super.transferStackInSlot(slot);
//	}

	//	@Override
	//	public void onSlotClick(int rawSlotNumber,int containerSlotNumber,int clickButton,ClickType clickTypeIn,EntityPlayer ep){
	//		ItemStack is = this.getSlot(rawSlotNumber).getStack();
	//		UnsagaMod.logger.trace("slot1", ComponentSelectableIcon.isNegative(is));
	//		if(is!=null && is.getItem() instanceof ItemIconMapSkill){
	//			this.playClickSound();
	//			int num =  is.getItemDamage();
	//			if(!ComponentSelectableIcon.isNegative(is)){
	//				this.onClickMapSkill(num);
	//			}
	//
	//		}
	//
	//	}


//	@Override
//	public PacketSendGuiInfoToClient getSyncPacketToClient(EntityPlayer ep){
//		boolean hasStat = false;
//		if(ep instanceof EntityPlayerMP){
//			UnsagaTriggers.OPEN_GUI_BARTERING.
//			hasStat = ((EntityPlayerMP)ep).getAdvancements().(UnsagaMod.core.achievements.getTablet);
//		}
//		NBTTagCompound nbt = UtilNBT.compound();
//		nbt.setBoolean("isUnlockDecipher", hasStat);
//		return PacketSendGuiInfoToClient.create(nbt);
//	}
}
