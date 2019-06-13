package mods.hinasch.unsaga.core.client.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import mods.hinasch.lib.client.GuiContainerBase;
import mods.hinasch.lib.container.inventory.InventoryHandler;
import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.misc.XY;
import mods.hinasch.lib.network.PacketServerToGui;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.common.IconSkillAssociated;
import mods.hinasch.unsaga.core.inventory.container.ContainerBartering;
import mods.hinasch.unsaga.core.inventory.container.ContainerBartering.CheckBox;
import mods.hinasch.unsaga.skillpanel.ISkillPanel;
import mods.hinasch.unsaga.skillpanel.SkillPanelAPI;
import mods.hinasch.unsaga.skillpanel.SkillPanels;
import mods.hinasch.unsaga.util.UnsagaTextFormatting;
import mods.hinasch.unsaga.villager.bartering.BarteringShopType;
import mods.hinasch.unsaga.villager.bartering.BarteringUtil;
import mods.hinasch.unsaga.villager.bartering.BarteringUtil.DiscountPair;
import mods.hinasch.unsaga.villager.bartering.MerchandiseCapability;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class GuiBartering extends GuiContainerBase{

	public static class ClientInfomation{
		int shopLevel = 0;
		int distributionLevel = 0;
		int transactionXP = 0;
		int nextStockTime = 0;
		public ClientInfomation(final int shopLevel, final int distributionLevel, final int transactionXP, final int nextStockTime) {
			super();
			this.shopLevel = shopLevel;
			this.distributionLevel = distributionLevel;
			this.transactionXP = transactionXP;
			this.nextStockTime = nextStockTime;
		}

		public void readFromNBT(NBTTagCompound message){
			this.shopLevel = message.getInteger("shopLevel");
			if(message.hasKey("distLV")){
				this.transactionXP = message.getInteger("transXP");
				this.distributionLevel = message.getInteger("distLV");
				final int meta = (int)message.getByte("shopType");
				this.nextStockTime = message.getInteger("nextTime");
			}
		}
	}
	public static class IconSkill extends IconSkillAssociated<GuiBartering>{

		public IconSkill(final int id, final int x, final int y, final int u, final int v, final boolean hover, final ISkillPanel skill) {
			super(id, x, y, u, v, hover, skill);
			// TODO 自動生成されたコンストラクター・スタブ
		}

		@Override
		public EntityPlayer getPlayer(final GuiBartering gui) {
			// TODO 自動生成されたメソッド・スタブ
			return gui.ep;
		}

	}
	public static enum IconType{
		SECRET(0, -32, 48, 16, 168){
			@Override
			public List<String> getHoverTexts(final GuiBartering self){
				final List<String> list = new ArrayList<>();
				list.add(HSLibs.translateKey("skillPanel.luckyFind.name"));
				list.add(HSLibs.translateKey("gui.unsaga.bartering.luckyFind"));
				if(SkillPanelAPI.hasPanel(self.getOpenPlayer(),  this.asSkillPanel())){
					list.add(HSLibs.translateKey("gui.unsaga.bartering.available"));
				}else{
					list.add(UnsagaTextFormatting.PROPERTY_LOCKED+HSLibs.translateKey("gui.unsaga.bartering.unavailable"));
				}
				return ImmutableList.copyOf(list);
			}
		},PRICE_UP(1, -32, 64, 32, 168){
			@Override
			public List<String> getHoverTexts(final GuiBartering self){
				final List<String> list = new ArrayList<>();
				list.add(HSLibs.translateKey("skillPanel.gratuity.name"));
				list.add(HSLibs.translateKey("gui.unsaga.bartering.gratuity"));

				if(SkillPanelAPI.hasPanel(self.getOpenPlayer(), this.asSkillPanel())){
					list.add(HSLibs.translateKey("gui.unsaga.bartering.available"));
				}else{
					list.add(UnsagaTextFormatting.PROPERTY_LOCKED+HSLibs.translateKey("gui.unsaga.bartering.unavailable"));
				}
				return ImmutableList.copyOf(list);
			}
		},PRICE_DOWN(2, -32, 32, 0, 168){
			@Override
			public List<String> getHoverTexts(final GuiBartering self){
				final List<String> list = new ArrayList<>();
				list.add(HSLibs.translateKey("skillPanel.discount.name"));
				list.add(HSLibs.translateKey("gui.unsaga.bartering.discount"));
				if(SkillPanelAPI.hasPanel(self.getOpenPlayer(),  this.asSkillPanel())){
					list.add(HSLibs.translateKey("gui.unsaga.bartering.available"));
				}else{
					list.add(UnsagaTextFormatting.PROPERTY_LOCKED+HSLibs.translateKey("gui.unsaga.bartering.unavailable"));
				}
				return ImmutableList.copyOf(list);
			}
		}
		,INFO(4, -32, 0, 48, 168){
			public List<String> getHoverTexts(final GuiBartering self){
				final List<String> list = new ArrayList<>();
				final ClientInfomation info = self.getClientInfomation();
				if(self.hasSync){
					list.add(HSLibs.translateKey("gui.bartering.shop_level")+":"+info.shopLevel);
					list.add(HSLibs.translateKey("gui.bartering.distribution_level")+":"+info.distributionLevel);
					list.add(HSLibs.translateKey("gui.bartering.transaction_point",info.transactionXP,BarteringUtil.calcNextTransactionThreshold(info.distributionLevel)));
					list.add(HSLibs.translateKey("gui.bartering.next_stock")+":"+(24000-info.nextStockTime));
					self.getShopType().addTips(list);
				}else{
					self.setHasSync(true);
					HSLib.packetDispatcher().sendToServer(PacketServerToGui.request());
				}
				return ImmutableList.copyOf(list);
			}
		},BUY(5, 150, 61, 80, 168){
			@Override
			public List<String> getHoverTexts(final GuiBartering self){
				return ImmutableList.of("BUY THEM!");
			}
		};

		public static Collection<IconType> all(){
			return EnumSet.allOf(IconType.class);
		}
		public static IconType fromMeta(final int meta){
			return all().stream().filter(in ->in.meta==meta).findFirst().orElse(null);
		}
		int meta;

		XY pos;

		XY uv;

		private IconType(final int meta,final int x,final int y,final int u,final int v) {
			this.meta = meta;
			this.pos = XY.of(x, y);
			this.uv = XY.of(u, v);
		}
		public ISkillPanel asSkillPanel(){
			switch(this){
			case PRICE_DOWN:
				return SkillPanels.MONGER;
			case PRICE_UP:
				return SkillPanels.MAHARAJA;
			case SECRET:
				return SkillPanels.ARTISTE;
			default:
				break;

			}
			return SkillPanels.DUMMY;
		}

		public Icon createIcon(){
			switch(this){
			case BUY:
				return new IconButton(this.meta, pos.getX(),pos.getY(), uv.getX(), uv.getY(), this.isPopHover());
			case INFO:
				return new Icon(this.meta, pos.getX(),pos.getY(), uv.getX(), uv.getY(), this.isPopHover());
			case SECRET:
			case PRICE_DOWN:
			case PRICE_UP:
				return new IconSkill(this.meta, pos.getX(),pos.getY(), uv.getX(), uv.getY(), this.isPopHover(),this.asSkillPanel());
			default:
				break;

			}
			return null;
		}
		public List<String> getHoverTexts(final GuiBartering self){
			return ImmutableList.of();
		}

		public boolean isPopHover(){
			return true;
		}

		public void prePacket(EntityPlayer ep,ContainerBartering container){
			if(this==PRICE_DOWN){
				container.setDiscount(BarteringUtil.applyDiscount(ep));
			}
			if(this==PRICE_UP){
				container.setDiscount(BarteringUtil.applyGratuity(ep));
			}
		}

	}

	//protected final ResourceLocation background = new ResourceLocation(Unsaga.DOMAIN + ":textures/gui/shop.png");
	protected ContainerBartering container;
	protected IMerchant merchant;

	protected EntityPlayer ep;

	protected World world;

	boolean hasSync = false;
	ClientInfomation clientInfomation = new ClientInfomation(0,0,0,0);
	BarteringShopType type = BarteringShopType.UNKNOWN;
	ContainerBartering.CheckBox cb = new ContainerBartering.CheckBox();

	public GuiBartering(final IMerchant merchant,final World world,final EntityPlayer ep) {
		super(new ContainerBartering(world,ep,merchant));
		// TODO 自動生成されたコンストラクター・スタブ
		this.merchant = merchant;
		this.world = world;
		this.container = (ContainerBartering)this.inventorySlots;
		this.ep = ep;
		IconType.all().forEach(in ->this.addIcon(in.createIcon()));
	}

	@Override
	protected void drawGuiContainerForegroundLayer(final int par1,final int par2)
	{


		super.drawGuiContainerForegroundLayer(par1, par2);
		this.cb.drawView(this);

		fontRenderer.drawString(HSLibs.translateKey("gui.unsaga.bartering.amount.sell")+this.container.getSellPrice(),8,170,0xffffff);
		fontRenderer.drawString(HSLibs.translateKey("gui.unsaga.bartering.amount.buy")+this.container.getBuyPrice(),8,186,0xffffff);
		//		if(this.container!=null){
		//			for(int i=0;i<9;i++){
		//				ItemStack is = this.container.getMerchantInventory().getMerchandise(i);
		//				if(is!=null){
		//					BarteringPriceSupplier.getCapability(is).setMerchandiseItem(true);
		//				}
		//			}
		//		}
		//		fontRendererObj.drawString(I18n.translateToLocal("gui.bartering.amount")+this.container.getSellPrice(),8,48,0x404040);
	}

	public ClientInfomation getClientInfomation(){
		return this.clientInfomation;
	}
	public DiscountPair getDiscount(){
		return this.container.getDiscount();
	}
	@Override
	public String getGuiName(){
		return "";
	}

	@Override
	public String getGuiTextureName(){
		return UnsagaMod.MODID + ":textures/gui/container/shop.png";
	}
	@Override
	public List<String> getIconHoverText(final Icon icon){
		final List<String> list = Lists.newArrayList();
		Optional.ofNullable(IconType.fromMeta(icon.id))
		.ifPresent(in ->list.addAll(in.getHoverTexts(this)));
		return list;
	}

	public EntityPlayer getOpenPlayer(){
		return this.ep;
	}
	public BarteringShopType getShopType(){
		return this.type;
	}
	public boolean hasSync(){
		return this.hasSync;
	}

	@Override
	public void initGui()
	{
		super.initGui();
		final int i = width  - xSize >> 1;
		final int j = height - ySize >> 1;


	}

	@Override
	public int MessageYpos(){
		return 100;

	}

	@Override
	public void onGuiClosed()
	{
		super.onGuiClosed();
		this.merchant.setCustomer(null);
	}
	public void onPacketFromServer(final NBTTagCompound message){
		HSLib.logger.trace(this.getGuiName(), "届いてます"+message.getString("test"));
		this.clientInfomation.readFromNBT(message);
		Optional.ofNullable(BarteringShopType.fromMeta((int)message.getByte("shopType")))
		.ifPresent(in ->this.type = in);
		Optional.ofNullable(CheckBox.readFromNBT(message))
		.ifPresent(in -> this.container.updateCheckBox(in));

		//		this.hasSync = true;
	}


	@Override
	public void prePacket(final GuiButton par1GuiButton){

		//		/** 値段上下系パネルの数を調べて返す、おしゃれがあれば加算*/
		//		Function panelChecker = new Function<PanelData,Integer>(){
		//
		//			@Override
		//			public Integer apply(PanelData input) {
		//				int base = SkillPanels.getHighestLevelPanel(ep.worldObj, ep, input) + 1;
		//				if(SkillPanels.hasPanel(ep.worldObj, ep, Unsaga.skillPanels.fashionable)){
		//					base += SkillPanels.getHighestLevelPanel(ep.worldObj, ep, Unsaga.skillPanels.fashionable) + 1;
		//				}
		//				return base;
		//			}
		//		};
		//		switch(par1GuiButton.id){
		//		case BUTTON_UP_PRICE:
		//			if(SkillPanels.hasPanel(ep.worldObj, ep, Unsaga.skillPanels.gratuity)){
		//				this.setPriceUp((Integer) panelChecker.apply(SkillPanels.getInstance().gratuity));
		//				Unsaga.debug(this.priceUp,this.getClass());
		//			}
		//
		//
		//			break;
		//		case BUTTON_DOWN_PRICE:
		//			if(SkillPanels.hasPanel(ep.worldObj, ep, Unsaga.skillPanels.discount)){
		//				this.setPriceDown((Integer) panelChecker.apply(SkillPanels.getInstance().discount));
		//				Unsaga.debug(this.priceDown,this.getClass());
		//			}
		//
		//			break;
		//		}
	}

	@Override
	public void prePacket(final Icon icon){
		Optional.ofNullable(IconType.fromMeta(icon.id))
		.ifPresent(in ->in.prePacket(ep, container));
	}


	public void setHasSync(final boolean par1){
		this.hasSync = par1;
	}

	public void setMerchandiseTag(){
		InventoryHandler.of(this.container.getMerchantInventory()).toStream(0, this.container.getMerchantInventory().getSizeInventory())
		.filter(in -> !in.getStack().isEmpty())
		.map(in -> in.getStack())
		.forEach(in ->MerchandiseCapability.ADAPTER.getCapabilityOptional(in).ifPresent(in2 ->in2.setMerchandise(true)));
	}
}
