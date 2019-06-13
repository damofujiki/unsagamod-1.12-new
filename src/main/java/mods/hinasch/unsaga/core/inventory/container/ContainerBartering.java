package mods.hinasch.unsaga.core.inventory.container;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import mods.hinasch.lib.container.ContainerBase;
import mods.hinasch.lib.container.inventory.InventoryHandler;
import mods.hinasch.lib.container.inventory.InventoryHandler.InventoryStatus;
import mods.hinasch.lib.container.inventory.InventoryHandler.TransferStackLogic;
import mods.hinasch.lib.container.inventory.SlotPlayer;
import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.iface.INBTWritable;
import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.network.PacketGuiButtonBaseNew;
import mods.hinasch.lib.network.PacketServerToGui;
import mods.hinasch.lib.util.SoundAndSFX;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.lib.util.UtilNBT.NBTWrapper;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.advancement.UnsagaTriggers;
import mods.hinasch.unsaga.core.client.gui.GuiBartering;
import mods.hinasch.unsaga.core.entity.UnsagaActionCapability;
import mods.hinasch.unsaga.core.inventory.slot.SlotMerchant;
import mods.hinasch.unsaga.core.net.packet.PacketGuiButtonUnsaga;
import mods.hinasch.unsaga.core.net.packet.PacketSyncSkillPanel;
import mods.hinasch.unsaga.init.UnsagaGui;
import mods.hinasch.unsaga.skillpanel.SkillPanelAPI;
import mods.hinasch.unsaga.skillpanel.SkillPanels;
import mods.hinasch.unsaga.villager.VillagerCapabilityUnsaga;
import mods.hinasch.unsaga.villager.bartering.BarteringUtil;
import mods.hinasch.unsaga.villager.bartering.BarteringUtil.DiscountPair;
import mods.hinasch.unsaga.villager.bartering.MerchandiseCapability;
import mods.hinasch.unsaga.villager.bartering.TraitShopOwner;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerBartering extends ContainerBase{

	public static class CheckBox{
		public static CheckBox readFromNBT(final NBTTagCompound comp){
			final CheckBox cb = new CheckBox();
			if(comp.hasKey("checkBox")){
				UtilNBT.readListFromNBT(comp, "checkBox", ContainerBartering.Entry::restore)
				.stream()
				.forEach(in -> cb.set(in.num,in.sw));
				return cb;
			}
			return null;
		}

		final Map<Integer,Boolean> inner = new HashMap<>();

		@SideOnly(Side.CLIENT)
		public void drawView(final GuiContainer gui){
			this.inner.entrySet().forEach(in ->{
				if(in.getKey()<9 && in.getValue()){
					gui.drawTexturedModalRect(7 + in.getKey()  * 18, 8, 0, 184, 18,18);
				}
				if(in.getKey()>=9 && in.getValue()){
					gui.drawTexturedModalRect(7 + (in.getKey()-9) * 18, 26, 0, 184, 18,18);
				}
			});
		}

		public Collection<Integer> getChecked(){
			return this.inner.entrySet().stream()
					.filter(entry ->entry.getValue())
					.map(in -> in.getKey())
					.collect(Collectors.toSet());
		}

		public void mark(final int num){
			final boolean sw = this.inner.get(num);
			this.set(num, !sw);
		}

		public void set(final int num,final boolean checked){
			this.inner.put(num, checked);
		}
		public void writeToNBT(final NBTTagCompound comp){
			final List<ContainerBartering.Entry> list = this.inner.entrySet()
					.stream()
					.map(Entry::new)
					.collect(Collectors.toList());
			UtilNBT.comp(comp).writeList("checkBox", list);
		}

	}
	public static class Entry implements INBTWritable{

		public static Entry restore(final NBTTagCompound input){
			return UtilNBT.comp(input)
					.map(Entry::new);
		}

		public final int num;
		public final boolean sw;

		public Entry(final int num,final boolean sw){
			this.num = num;
			this.sw = sw;
		}

		public Entry(final Map.Entry<Integer,Boolean> entry){
			this(entry.getKey(),entry.getValue());
		}

		public Entry(final NBTTagCompound in){
			this(in.getByte("num"),in.getBoolean("sw"));
		}
		@Override
		public void writeToNBT(final NBTTagCompound stream) {
			UtilNBT.comp(stream).setByte("num", (byte) this.num).setBoolean("sw", this.sw);
		}
	}
	static final int MERCHANDISE_SLOT_START = 36;
	protected World worldobj;
	protected EntityPlayer theCustomer;
	protected IInventory invEp;
	protected IMerchant theMerchant;
	//	protected SlotMerchant[] merchandiseSlot;
	//	protected IInventory dummyInv;
	protected DiscountPair discounts;
	protected IInventory invSell;
	protected TraitShopOwner trait;
	protected byte selected;
	protected int shopLevel = 0;

	/** チェックボックスの管理マップ*/
	CheckBox checkBox = new CheckBox();


	public ContainerBartering(final World world,final EntityPlayer ep,final IMerchant merchant){

		super(ep, new InventoryBasic("bartering",false,18));
		this.worldobj = world;
		this.theCustomer = ep;
		this.invEp = ep.inventory;
		this.theMerchant = merchant;
		this.theMerchant.setCustomer(theCustomer);
		//		this.invMerchant = (InventoryMerchant) this.inv;
		//		this.merchandiseSlot = new SlotMerchant[9];
		//		this.dummyInv = new InventoryBasic("",false,10); //インベントリの連携はいらないがないとダメ…？
		this.setSpreadSlotItems(false);
		this.invSell = new InventoryBasic("sell",false,7);
		//		this.invResult = new InventoryBasic("result",false,1);

		this.discounts = new DiscountPair(0,0);

		this.setTransferStackLogic(new TransferStackLogic(invEp, invSell, in -> (in instanceof SlotMerchant.PlayerSell)));
		this.transferLogic.setSelfSlotIdentifier(in -> in instanceof SlotPlayer);

		for(int j=0;j<2;j++){
			for (int i = 0; i < 9; ++i)
			{
				this.checkBox.set(i+(9*j), false);
				this.addSlotToContainer(new SlotMerchant.MerchantSell(this.inv, i + (j*9), 8 + i * 18, (j*18)+63-(18*3)));
			}



		}

		for (int i = 0; i < this.invSell.getSizeInventory(); ++i)
		{
			this.addSlotToContainer(new SlotMerchant.PlayerSell(this.invSell, i , 8 + i * 18, 62));
		}
		//		this.addSlotToContainer(new SlotMerchant.Result(this.invResult, 0, 8 + 8 * 18, 62));

		//this.addSlotToContainer(new Slot(this.invMerchant,30,152,42));



		if(this.theMerchant instanceof EntityVillager){
			final EntityVillager villager = (EntityVillager) this.theMerchant;
			this.trait = new TraitShopOwner(world, villager);



			if(WorldHelper.isServer(world)){
				//店レベルの決定。生成レベルに関わる
				this.shopLevel = this.trait.calcShopLevel(villager);

				//仕入れ時間が来た、もしくはイニシャル時は商品入れ替え
				if(this.trait.hasComeUpdateTime()){
					this.trait.resetDisplayedSecretMerchandise();
					this.trait.updateMerchandises(this.ep);
				}



				//				if(UnsagaVillagerCapability.ADAPTER.hasCapability(villager)){
				//				VillagerBarteringImpl impl = (VillagerBarteringImpl) UnsagaVillagerCapability.ADAPTER.getCapability(villager).getImpl();
				final NonNullList<ItemStack> merchandises = this.trait.merchant.getMerchandises();
				final NonNullList<ItemStack> secrets = this.trait.merchant.getSecretMerchandises();
				//					merchandises.setToInventory(0, this.invMerchant);
				//					secrets.setToInventory(9, this.invMerchant);

				//村人のリストからインベントリへ移動
				ItemUtil.transferToInventory(merchandises, inv, 0);
				ItemUtil.transferToInventory(secrets, inv, 9);
				//				}
			}



		}


		//スキルパネルの同期
		if(this.ep instanceof EntityPlayerMP){
			UnsagaMod.PACKET_DISPATCHER.sendTo(PacketSyncSkillPanel.create(ep), (EntityPlayerMP) ep);
		}

	}

	public boolean canBuy(){
		final boolean hasSells = InventoryHandler.of(invSell)
				.stream()
				.anyMatch(in -> !in.getStack().isEmpty());
		final boolean hasPrices = InventoryHandler.of(invSell)
				.stream()
				.filter(in -> !in.getStack().isEmpty())
				.allMatch(in ->MerchandiseCapability.ADAPTER.getCapabilityOptional(in.getStack())
						.map(capa -> capa.getPrice(in.getStack())>0).orElse(false));
		final boolean areCheckedItemsPresent = this.checkBox.getChecked()
				.stream()
				.anyMatch(in -> !this.inv.getStackInSlot(in).isEmpty());
		return hasSells && hasPrices && areCheckedItemsPresent;
	}


	@Override
	public boolean canInteractWith(final EntityPlayer entityplayer) {
		// TODO 自動生成されたメソッド・スタブ
		return this.theCustomer!=null && this.theCustomer.equals(entityplayer);
	}




	private void clearCheckedSlots(){
		this.checkBox.getChecked().stream()
		.forEach(in -> this.inv.setInventorySlotContents(in,ItemStack.EMPTY));

		InventoryHandler.of(this.invSell)
		.fill(ItemStack.EMPTY, 0, 7);
	}

	private void createSecretMerchandises(final int level){

		final NonNullList<ItemStack> secrets = this.trait.createSecretMerchandises(level);
		if(!secrets.isEmpty()){
			for(int i=0;i<secrets.size();i++){
				final Slot slot = this.getSlotFromInventory(inv, 9+i);
				slot.putStack(secrets.get(i));
				this.detectAndSendChanges();
			}
		}
	}
	private void dropCheckedItemStacks(){
		this.checkBox.getChecked().stream()
		.map(in -> this.inv.getStackInSlot(in))
		.filter(in -> !in.isEmpty())
		.forEach(in -> ItemUtil.dropItem(in, ep));
	}

	public int getBuyPrice(){
		//		this.checkBox.entrySet().stream().forEach(in -> UnsagaMod.logger.trace("stream", in));
		return this.checkBox.getChecked().stream()
				.map(in -> this.inv.getStackInSlot(in))
				.filter(in ->!in.isEmpty())
				.mapToInt(this::getPriceFromStack)
				.sum();
	}

	public DiscountPair getDiscount(){
		return this.discounts;
	}
	@Override
	public int getGuiID() {
		// TODO 自動生成されたメソッド・スタブ
		return UnsagaGui.Type.BARTERING.getMeta();
	}



	public IInventory getMerchantInventory(){
		return this.inv;//this.invMerchant;
	}

	@Override
	public PacketGuiButtonBaseNew getPacketGuiButton(final int guiID, final int buttonID,
			final NBTTagCompound args) {
		//		args.setByte("button", this.selected);
		return PacketGuiButtonUnsaga.create(UnsagaGui.Type.fromMeta(guiID), buttonID, args);
	}

	@Override
	public SimpleNetworkWrapper getPacketPipeline() {
		// TODO 自動生成されたメソッド・スタブ
		return UnsagaMod.PACKET_DISPATCHER;
	}

	private int getPriceFromStack(final ItemStack in){
		return MerchandiseCapability.ADAPTER.getCapabilityOptional(in)
				.map(cap -> BarteringUtil.getDiscountPrice((int) (cap.getPrice(in)*BarteringUtil.MERCHANDISE_PRICE), this.discounts))
				.orElse(0);
	}

	public int getSellPrice(){

		return InventoryHandler.of(this.invSell)
				.stream()
				.filter(in -> !in.getStack().isEmpty())
				.mapToInt(this::getSellPrice)
				.sum();
	}

	private int getSellPrice(final InventoryStatus in){
		return MerchandiseCapability.ADAPTER.getCapabilityOptional(in.getStack())
		.map(ca ->ca.getPrice(in.getStack())*in.getStack().getCount())
		.orElse(0);

	}

	public PacketServerToGui getSyncPacketToClient(final EntityPlayer ep){
		final NBTWrapper comp = UtilNBT.comp().setInteger("shopLevel", this.shopLevel);
		if(this.theMerchant instanceof EntityVillager){
			final EntityVillager villager = (EntityVillager) this.theMerchant;
			if(VillagerCapabilityUnsaga.ADAPTER.hasCapability(villager)){
				comp.setInteger("distLV", this.trait.merchant.getDistributionLevel())
				.setInteger("transXP", this.trait.merchant.getTransactionPoint())
				.setByte("shopType", (byte)this.trait.merchant.getShopType().getMeta())
				.setInteger("nextTime", (int)(this.worldobj.getTotalWorldTime()- this.trait.merchant.getRecentStockedTime()));

			}

		}
		this.checkBox.writeToNBT(comp.get());
		return PacketServerToGui.create(comp.get());
	}

	public World getWorld(){
		return this.worldobj;
	}

	//指定のスロットをチェック＆アンチェック
	private void markSlot(final int slot){
		this.checkBox.mark(slot-36);
	}

	@Override
	public void onContainerClosed(final EntityPlayer par1EntityPlayer)
	{
		super.onContainerClosed(par1EntityPlayer);

		UnsagaActionCapability.ADAPTER.getCapabilityOptional(ep).ifPresent(in -> in.setMerchant(Optional.empty()));
		this.inv.closeInventory(par1EntityPlayer);

		this.theMerchant.setCustomer((EntityPlayer)null);

		if(WorldHelper.isServer(this.worldobj)){
			UnsagaMod.logger.trace(this.getClass().getName(), "called");
			this.trait.merchant.setMerchandises(InventoryHandler.of(inv).crop(0, 9));
			this.trait.merchant.setSecretMerchandises(InventoryHandler.of(inv).crop(9, 9));
			InventoryHandler.of(this.invSell).toStream(0, this.invSell.getSizeInventory())
			.forEach(in -> ItemUtil.dropItem(in.getStack(), ep));
		}

	}

	@Override
	public void onPacketData() {

		this.selected = (byte)this.buttonID;
		UnsagaMod.logger.trace(this.getClass().getName(), this.buttonID);
		Optional.ofNullable(GuiBartering.IconType.fromMeta(this.selected))
		.ifPresent(in ->{
			switch(in){
			case BUY:
				if(this.getBuyPrice()<=this.getSellPrice() && this.canBuy()){
					this.processBuying();
				}
				break;
			case PRICE_DOWN:
				this.discounts = BarteringUtil.applyDiscount(ep);
				break;
			case PRICE_UP:
				this.discounts = BarteringUtil.applyGratuity(ep);
				break;
			case SECRET:
				if(!this.trait.hasDisplayedSecrets()){
					SkillPanelAPI.getHighestPanelLevel(ep, SkillPanels.ARTISTE)
					.ifPresent(this::createSecretMerchandises);
				}
				break;
			default:
				break;

			}
		});


	}



	public void processBuying(){
		this.trait.addTransactionPoint(this.getBuyPrice()); //取引ポイントの追加
		this.dropCheckedItemStacks(); //買ったアイテムをドロップ
		this.clearCheckedSlots(); //買ったアイテムをスロットから消す

		UnsagaTriggers.BEGIN_BARTERING.trigger((EntityPlayerMP) ep);
		//		this.ep.addStat(UnsagaAchievementRegistry.instance().bartering1);
	}
	public void setDiscount(final DiscountPair p){
		this.discounts = p;
	}

	public void setMerchandiseTag(){
		//		this.invMerchant.setMerchandiseTag();
	}

	@Override
	public ItemStack slotClick(final int par1, final int dragType, final ClickType clickTypeIn, final EntityPlayer player)
	{

		if(par1>=MERCHANDISE_SLOT_START && MERCHANDISE_SLOT_START+18>par1 ){
			if(WorldHelper.isClient(this.ep.world)){
				SoundAndSFX.playPositionedSoundRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F);
			}
			if(player instanceof EntityPlayerMP){
				this.markSlot(par1);
				HSLib.packetDispatcher().sendTo(this.getSyncPacketToClient(player), (EntityPlayerMP) player);
			}

		}


		return super.slotClick(par1, dragType, clickTypeIn, player);


	}
	public void syncSecretSlot(final ItemStack is){
		//		this.invMerchant.setMerchandise(8, is);
	}


	/**クライアント側 */
	public void updateCheckBox(final CheckBox checkBox){
		this.checkBox = checkBox;
	}
}
