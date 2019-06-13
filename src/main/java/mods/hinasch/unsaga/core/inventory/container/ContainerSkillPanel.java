package mods.hinasch.unsaga.core.inventory.container;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.Lists;
import com.google.common.collect.Range;

import mods.hinasch.lib.container.ContainerBase;
import mods.hinasch.lib.container.inventory.InventoryHandler;
import mods.hinasch.lib.container.inventory.InventoryHandler.InventoryStatus;
import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.network.PacketGuiButtonBaseNew;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.common.panel_bonus.HexAdapter;
import mods.hinasch.unsaga.core.advancement.UnsagaTriggers;
import mods.hinasch.unsaga.core.client.gui.GuiSkillPanel;
import mods.hinasch.unsaga.core.inventory.InventorySkillPanel;
import mods.hinasch.unsaga.core.inventory.slot.SlotSkillPanel;
import mods.hinasch.unsaga.core.item.misc.skillpanel.SkillPanelCapability;
import mods.hinasch.unsaga.core.net.packet.PacketGuiButtonUnsaga;
import mods.hinasch.unsaga.core.world.UnsagaWorldCapability;
import mods.hinasch.unsaga.init.UnsagaGui;
import mods.hinasch.unsaga.skillpanel.ISkillPanel;
import mods.hinasch.unsaga.skillpanel.SkillPanelInitializer;
import mods.hinasch.unsaga.skillpanel.SkillPanelInitializer.WeightedRandomPanel;
import mods.hinasch.unsaga.status.UnsagaXPCapability;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.WeightedRandom;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class ContainerSkillPanel extends ContainerBase{

	private SkillPanelInitializer skillPanels = SkillPanelInitializer.instance();
	/** プレイヤーの所持するスキルパネル*/
	protected InventorySkillPanel invSkillPanel;
	/** 引いたスキルパネルを一時的においておく場所*/
	protected InventoryBasic invTemp;
	protected World world;
	protected ItemStack undoPanel;
	protected int slotUndo;
	protected ItemStack undoPanelPlayer;
	protected boolean hasDrawedSkillPanel;
	protected boolean hasSetSkillPanel;
	protected int expToConsume;

	public HexAdapter adapter;

	protected static final Map<Integer,List<Integer>> MODIFIERS_PAR_LEVEL;


	public ContainerSkillPanel(EntityPlayer ep) {
		super(ep, new InventorySkillPanel());
		this.world = ep.world;
		this.invSkillPanel = (InventorySkillPanel) this.inv;

		this.invTemp = new InventoryBasic("",false,6);
		this.spreadSlotItems = false;
		this.expToConsume = 5;
		this.hasSetSkillPanel = false;
		this.hasDrawedSkillPanel = false;

		//		this.matrixPanel = new MatrixAdapterItemStack(inv);
		//		this.matrixSpell = new MatrixAdapterElement();

		this.adapter = new HexAdapter(invSkillPanel);



		//		WorldSaveDataSkillPanel data = WorldSaveDataSkillPanel.get(world);


		NonNullList<ItemStack> panelList = UnsagaWorldCapability.ADAPTER.getCapability(world).skillPanelStorage().getPanels(ep.getUniqueID());
		this.invSkillPanel.applyItemStackList(panelList);

		int index = 0;
		for(int j=0;j<2 ;j++){
			addSlotToContainer(new SlotSkillPanel(this.inv,j , 62 + 9 + j * 18, 17 + index * 18).setPlayerPanel(true));
		}
		index +=1;
		for(int j=0;j<3 ;j++){
			addSlotToContainer(new SlotSkillPanel(this.inv,j + 2, 62 + j * 18, 17 + index * 18).setPlayerPanel(true));
		}
		index +=1;
		for(int j=0;j<2 ;j++){
			addSlotToContainer(new SlotSkillPanel(this.inv,j + 5, 62 + 9 +  j * 18, 17 + index * 18).setPlayerPanel(true));
		}

		//右側だけプレイヤーのインベントリでない
		for(int col=0;col<3;col++){
			for(int j=0;j<2 ;j++){
				addSlotToContainer(new SlotSkillPanel(this.invTemp, j + (col*2), 53 + 18*4 +  j * 18, 17 + col * 18).setPlayerPanel(false));
			}
		}


		UnsagaMod.logger.trace("All Layout", this.adapter.buildJointList().dump(this.invSkillPanel));
		//		UnsagaMod.logger.trace("All Bonus", this.adapter.getAllPanelBonus());


	}

	@Override
	public boolean isShowPlayerInv(){
		return this.ep.capabilities.isCreativeMode;
	}
	@Override
	public PacketGuiButtonBaseNew getPacketGuiButton(int guiID, int buttonID,
			NBTTagCompound args) {
		// TODO 自動生成されたメソッド・スタブ
		return PacketGuiButtonUnsaga.create(UnsagaGui.Type.SKILLPANEL, buttonID, args);
	}

	@Override
	public SimpleNetworkWrapper getPacketPipeline() {
		// TODO 自動生成されたメソッド・スタブ
		return UnsagaMod.PACKET_DISPATCHER;
	}

	@Override
	public int getGuiID() {
		// TODO 自動生成されたメソッド・スタブ
		return UnsagaGui.Type.SKILLPANEL.getMeta();
	}

	public int getXPToConsume(){
		return this.expToConsume;
		//		return this.invSkillPanel.getField(InventorySkillPanel.XP_TO_CONSUME);
	}
	public void setXPToConsume(int value){
		this.expToConsume = value;
		//		this.invSkillPanel.setField(InventorySkillPanel.XP_TO_CONSUME, value);
	}
	@Override
	public void onPacketData() {
		UnsagaMod.logger.trace(this.getClass().getName(),"called");
		Optional.ofNullable(GuiSkillPanel.IconType.fromMeta(this.buttonID))
		.ifPresent(in ->{
			switch(in){
			case CHANGE_EXP:
				this.setXPToConsume(this.argsSent.getInteger("exp"));
				break;
			case DRAW_PANELS:
				if(!this.hasDrawedSkillPanel){
					UnsagaXPCapability.adapter.getCapabilityOptional(ep)
					.filter(capa -> capa.getSkillPoint()>=this.getXPToConsume() || this.ep.capabilities.isCreativeMode)
					.ifPresent(capa ->{
						this.drawSkillPanels();
						this.hasDrawedSkillPanel = true;
						if(WorldHelper.isServer(this.world)){
							UnsagaTriggers.BEGIN_SKILL_PANEL.trigger((EntityPlayerMP) this.ep);
						}
					});
				}
				break;
			case UNDO:
				if(this.hasSetSkillPanel){
					this.undo();
					this.hasSetSkillPanel = false;
				}
				break;
			default:
				break;

			}
		});


	}

	@Override
	public void onContainerClosed(EntityPlayer playerIn)
	{
		super.onContainerClosed(playerIn);

		this.applyBonus(playerIn);
		for(int i=0;i<7;i++){
			this.lockPanel(this.invSkillPanel.getStackInSlot(i));
		}
		playerIn.inventory.setItemStack(ItemStack.EMPTY);
		this.savePanelData(playerIn);
	}

	private void lockPanel(ItemStack panel){
		if(!panel.isEmpty()){
			SkillPanelCapability.adapter.getCapabilityOptional(panel)
			.ifPresent(in ->in.setLocked(true));
		}
	}

	public void applyBonus(EntityPlayer playerIn){
		this.adapter.createSummary().applyBonus(playerIn);
		//		boolean lineBonus = !this.getPanelMatrix().checkLine().isEmpty();
		//		HelperSkillPanelBonus.applyBonus(getPanelMatrix(), playerIn, lineBonus);
	}
	public void savePanelData(EntityPlayer ep){
		UnsagaMod.logger.trace(this.getClass().getName(), "パネルをsaveします");

		UnsagaWorldCapability.ADAPTER.getCapabilityOptional(world)
		.map(in ->in.skillPanelStorage())
		.ifPresent(in ->{
			in.dumpData(); //debug
			in.updatePanels(ep.getUniqueID(), this.invSkillPanel.getPlayerPanels());
		});

	}


	/** アンドゥ情報の記憶*/
	public void setUndoInfo(int undoSlot,ItemStack undoStackPlayer,ItemStack undoStackNew){
		this.slotUndo = undoSlot;
		this.undoPanelPlayer = !undoStackPlayer.isEmpty() ?undoStackPlayer.copy() : ItemStack.EMPTY;
		this.undoPanel = !undoStackNew.isEmpty() ?undoStackNew.copy() : ItemStack.EMPTY;
		this.hasSetSkillPanel = true;
	}
	public void undo(){



		this.getSlot(slotUndo).putStack(!undoPanelPlayer.isEmpty()? undoPanelPlayer.copy() : ItemStack.EMPTY);
		this.getEmptyTemporarySlot()
		.ifPresent(in ->this.invTemp.setInventorySlotContents(in, !undoPanel.isEmpty()? undoPanel.copy() : ItemStack.EMPTY));


	}

	public void drawSkillPanels(){
		//Collection<SkillPanels.WeightedRandomPanel> weightedPanels = Unsaga.skillPanels.getWeightedRandomPanels();
		int num = this.getXPToConsume() / 5;
		final List<Integer> modifier = this.MODIFIERS_PAR_LEVEL.get(num-1);


		List<WeightedRandomPanel> availablePanels = WeightedRandomPanel.prepareAll((panel,level) -> !hasPanel(panel,level) && modifier.get(level)>0)
				.stream()
				.map(input -> input.applyModifierToWeight(modifier.get(input.level)))
				.collect(Collectors.toList());
		InventoryHandler.of(this.invTemp)
		.stream(Range.closedOpen(0, 6))
		.forEach(status ->this.preparePanels(availablePanels, status));


		this.consumeExperience();
	}

	private void preparePanels(List<WeightedRandomPanel> availablePanels,InventoryStatus status){
		WeightedRandomPanel drawedPanel = WeightedRandom.getRandomItem(ep.getRNG(), availablePanels);
		ItemStack stackPanel = SkillPanelInitializer.createStack(drawedPanel.panel, drawedPanel.level);
		//			if(isPanel==null)skillPanels.getData(0).getItemStack(); //?
		//			ItemSkillPanel.setLevel(isPanel, drawedLevel);
		status.inventory().setInventorySlotContents(status.getStackNumber(), stackPanel.copy());
		//引いたパネルは除外していく
		availablePanels.remove(drawedPanel);
	}
	public void consumeExperience(){
		UnsagaXPCapability.adapter.getCapabilityOptional(ep)
		.filter(in -> !this.ep.capabilities.isCreativeMode)
		.ifPresent(in ->{
			int skillLevel = in.getSkillPoint();
			if(skillLevel>=this.getXPToConsume()){
				in.addSkillPoint(-this.getXPToConsume());
				if(WorldHelper.isServer(world)){
					HSLib.packetDispatcher().sendTo(PacketSyncCapability.create(UnsagaXPCapability.CAPA,in), (EntityPlayerMP) ep);
				}

			}
		});


	}

	public boolean hasPanel(final ISkillPanel panelIn,final int levelIn){
		return InventoryHandler.of(inv)
				.stream(Range.closedOpen(0, 7))
				.filter(in -> !in.getStack().isEmpty())
				.anyMatch(in -> SkillPanelCapability.adapter.getCapabilityOptional(in.getStack())
						.map(cap -> cap.equalPanel(panelIn, levelIn))
						.orElse(false));

		//		for(int i=0;i<7;i++){
		//			if(this.inv.getStackInSlot(i)!=null){
		//				int damage = this.inv.getStackInSlot(i).getItemDamage();
		//				int lv = ItemSkillPanel.getLevel(this.inv.getStackInSlot(i));
		//				if(data.getNumber()==damage && lv==level){
		//					return true;
		//				}
		//			}
		//
		//
		//		}
		//		return false;
	}

	/** 仮スキルパネルスロットに空きがあるかどうか*/
	public OptionalInt getEmptyTemporarySlot(){

		return IntStream.range(0, 6).filter(in -> this.invTemp.getStackInSlot(in).isEmpty()).findFirst();
	}
	@Override
	public ItemStack slotClick(int par1, int dragType, ClickType clickTypeIn, EntityPlayer player)
	{
		if(par1>0){
			Slot slot = this.getSlot(par1);

			//			if(slot instanceof SlotSkillPanel){
			//				if(!((SlotSkillPanel) slot).isPlayerPanel() && this.getEmptySlot().isPresent()){
			//					return null;
			//				}
			//
			//			}

			if(slot instanceof SlotSkillPanel){
				if(!((SlotSkillPanel)slot).isPlayerPanel()){
					//仮スキルパネルに空き→もうパネルを選択した＝もう仮スロットはクリックできない
					if(this.getEmptyTemporarySlot().isPresent()){
						return ItemStack.EMPTY;
					}
				}
			}

			UnsagaMod.logger.trace("slot", par1);
			ItemStack is = slot.getStack();
			ItemStack hold = player.inventory.getItemStack();
			//			this.applyBonus(this.ep);
			//パネルをスロットにセットした時アンドゥ情報も保存
			if(!hold.isEmpty()){
				this.setUndoInfo(par1, is, hold);
				slot.putStack(hold);
				player.inventory.setItemStack(ItemStack.EMPTY);
				return ItemStack.EMPTY;
			}
			if(slot instanceof SlotSkillPanel){
				//プレイヤーのパネルの場合空を返す＝セットしたパネルは拾えない
				if(((SlotSkillPanel) slot).isPlayerPanel()){
					return ItemStack.EMPTY;
				}

			}



		}

		return super.slotClick(par1, dragType, clickTypeIn, player);

	}


	/** パネルのレベル別分布*/
	static{
		MODIFIERS_PAR_LEVEL = new HashMap<>();
		MODIFIERS_PAR_LEVEL.put(0, Lists.newArrayList(40,15,1,0,0));
		MODIFIERS_PAR_LEVEL.put(1, Lists.newArrayList(7,40,10,0,0));
		MODIFIERS_PAR_LEVEL.put(2, Lists.newArrayList(1,7,40,10,0));
		MODIFIERS_PAR_LEVEL.put(3, Lists.newArrayList(0,3,20,40,1));
		MODIFIERS_PAR_LEVEL.put(4, Lists.newArrayList(0,0,5,40,5));
		MODIFIERS_PAR_LEVEL.put(5, Lists.newArrayList(0,0,0,10,30));
	}


}
