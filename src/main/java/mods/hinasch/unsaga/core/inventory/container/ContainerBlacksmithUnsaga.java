package mods.hinasch.unsaga.core.inventory.container;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.mojang.realmsclient.util.Pair;

import mods.hinasch.lib.container.ContainerBase;
import mods.hinasch.lib.container.inventory.InventoryHandler;
import mods.hinasch.lib.container.inventory.InventoryHandler.TransferStackLogic;
import mods.hinasch.lib.container.inventory.SlotPlayer;
import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.misc.Triplet;
import mods.hinasch.lib.network.PacketGuiButtonBaseNew;
import mods.hinasch.lib.network.PacketServerToGui;
import mods.hinasch.lib.network.PacketSound;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.advancement.UnsagaTriggers;
import mods.hinasch.unsaga.core.client.gui.GuiBlacksmithUnsaga;
import mods.hinasch.unsaga.core.inventory.InventorySmithUnsaga;
import mods.hinasch.unsaga.core.inventory.slot.SlotBlacksmith;
import mods.hinasch.unsaga.core.net.packet.PacketGuiButtonUnsaga;
import mods.hinasch.unsaga.init.UnsagaGui;
import mods.hinasch.unsaga.init.UnsagaLibrary;
import mods.hinasch.unsaga.material.UnsagaIngredients;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.material.UnsagaMaterialCapability;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import mods.hinasch.unsaga.util.ToolCategory;
import mods.hinasch.unsaga.villager.VillagerCapabilityUnsaga;
import mods.hinasch.unsaga.villager.smith.AbilityMutationManager;
import mods.hinasch.unsaga.villager.smith.BlacksmithToolBaker;
import mods.hinasch.unsaga.villager.smith.BlacksmithType;
import mods.hinasch.unsaga.villager.smith.TraitToolForger;
import mods.hinasch.unsaga.villager.smith.TraitToolForger.ForgeResult;
import mods.hinasch.unsaga.villager.smith.ValidPayments;
import mods.hinasch.unsaga.villager.smith.VillagerBlacksmithImpl;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class ContainerBlacksmithUnsaga extends ContainerBase{

	//	protected ForgingFactory forgingFactory;
	final protected IMerchant theMerchant;
	public final EntityPlayer ep;
	final protected World worldobj;
	final protected InventorySmithUnsaga inventorySmith;
	final protected IInventory invPlayer;
	public static final int STACKLIMIT_MATERIAL = 2;
	public static final int PAYMENT = 0;
	public static final int BASE = 1;
	public static final int SUB = 2;
	public static final int FORGED = 3;
	protected byte currentCategory = 0; //GUI側と同期される
	//	protected DebugUnsaga debug;

	/**
	 * 鍛冶屋の特性。未完成
	 */
	BlacksmithType type = BlacksmithType.NONE;


	TraitToolForger trait;



	boolean isForgeable = false;
	Optional<BlacksmithToolBaker> process = Optional.empty();

	protected int id = 0;


	protected final InventoryHandler.TransferStackLogic transferLogic;

	public ContainerBlacksmithUnsaga(Triplet<IMerchant,World,EntityPlayer> tri){
		this(tri.first, tri.second, tri.third());
	}
	public ContainerBlacksmithUnsaga(final IMerchant par1,World par2,EntityPlayer ep){

		super(ep, new InventorySmithUnsaga(ep,par1));
		this.ep = ep;
		this.invPlayer = ep.inventory;
		this.worldobj = par2;
		//		if(debug==null){
		//			debug = new DebugUnsaga();
		//		}

		this.transferLogic = new TransferStackLogic(this.invPlayer, this.inv, in -> (in instanceof SlotBlacksmith.Payment)||(in instanceof SlotBlacksmith.Material));
		this.transferLogic.setSelfSlotIdentifier(in -> in instanceof SlotPlayer);

		//this.helper = new SmithHelper();
		this.theMerchant = par1;
		this.theMerchant.setCustomer(ep);

		UnsagaMod.logger.trace("inv", ep.getEntityWorld().isRemote);
		//		if(this.theMerchant!=null){
		//
		//		}




		if(this.theMerchant instanceof EntityVillager){
			EntityVillager villager = (EntityVillager) this.theMerchant;
			VillagerCapabilityUnsaga.ADAPTER.getCapabilityOptional(villager)
			.filter(in -> in instanceof VillagerBlacksmithImpl)
			.ifPresent(impl ->{
				this.type = ((VillagerBlacksmithImpl) impl).getBlackSmithType();
				UnsagaMod.logger.trace(this.getClass().getName(), this.type);
			});
		}

		this.trait = new TraitToolForger(this.worldobj.rand,this);



		this.inventorySmith = (InventorySmithUnsaga) this.inv;
		this.addSlotToContainer(new SlotBlacksmith.Payment(this.inventorySmith, this.PAYMENT, 28, 53)); //Emerald
		this.addSlotToContainer(new SlotBlacksmith.Material(this.inventorySmith, this.BASE, 28, 53-(18*2))); //Base Material
		this.addSlotToContainer(new SlotBlacksmith.Material(this.inventorySmith, this.SUB, 28+(18*2)-8, 53-(18*2))); //Material2
		this.addSlotToContainer(new SlotBlacksmith.Material(this.inventorySmith, this.FORGED, 28+(18*6)+1, 52));

		//		forgingFactory = new ForgingFactory(ep,this.smithType);


		//		this.ep.addStat(UnsagaModCore.instance().achievements.openVillager);
		if(WorldHelper.isServer(worldobj)){
			UnsagaTriggers.BEGIN_FORGE.trigger((EntityPlayerMP) this.ep);
		}
	}


	public BlacksmithType getSmithType(){
		return this.type;
	}

	public ToolCategory getCurrentCategory(){
		return ToolCategory.FORGEABLES.get(this.currentCategory);
	}


	@Override
	public int getGuiID(){
		return UnsagaGui.Type.SMITH.getMeta();
	}

	@Override
	public SimpleNetworkWrapper getPacketPipeline() {
		// TODO 自動生成されたメソッド・スタブ
		return UnsagaMod.PACKET_DISPATCHER;
	}


	@Override
	public PacketGuiButtonBaseNew getPacketGuiButton(int guiID,int buttonID,NBTTagCompound args){
		return PacketGuiButtonUnsaga.create(UnsagaGui.Type.fromMeta(guiID),buttonID,args);
	}

	@Deprecated
	public void onClientGuiOpened(){
		//		UnsagaMod.logger.trace("smith", par2);
		//		if(WorldHelper.isServer(this.worldobj)){
		//			NBTTagCompound args = UtilNBT.createCompound();
		//			args.setInteger("type", this.smithType.getMeta());
		//			UnsagaMod.packetDispatcher.sendTo(PacketSyncGui.create(Type.SMITH_PROFESSIONALITY, args), (EntityPlayerMP) ep);
		//		}
	}

	public List<ToolCategory> getAllowedToolCategories(){
		return ToolCategory.FORGEABLES;
	}

	/**
	 * 素材として使えるかどうかのメソッド
	 * 仕えるならマテリアルを返す、そうでないならempty
	 * @param base
	 * @return
	 */
	public UnsagaMaterial findMaterial(ItemStack base){
		//		UnsagaMaterial mate = UnsagaMaterials.DUMMY;
		List<UnsagaMaterial> list = new ArrayList<>();
		UnsagaMaterialCapability.adapter.getCapabilityOptional(base)
		.ifPresent(in ->list.add(in.getMaterial()));

		UnsagaIngredients.find(base)
		.ifPresent(in -> list.add(in.first()));

		UnsagaLibrary.CATALOG_MATERIAL.find(base)
		.ifPresent(in -> list.add(in.getMaterial()));

		return Optional.of(list).filter(in -> !in.isEmpty()).map(in -> in.get(0)).orElse(UnsagaMaterials.DUMMY);
	}
	public boolean isValidSubItem(){
		ItemStack sub = this.getSubStack();
		if(!sub.isEmpty()){
			return !this.findMaterial(sub).isEmpty();
		}
		return false;
	}

	public boolean isValidPayment(){
		ItemStack pay = this.inventorySmith.getPayment();
		if(!pay.isEmpty()){
			return ValidPayments.findValue(pay).isPresent();
		}
		return false;
	}
	public boolean hasSubMaterialAbilityMutation(){
		if(this.isValidSubItem()){
			return AbilityMutationManager.canMutate(this.findMaterial(getSubStack()));
		}
		return false;
	}

	/**
	 * ベースアイテムとして使えるか（サブ素材として使え、なおかつ適合素材）
	 * @return
	 */
	public boolean isValidBaseItem(){
		if(!this.getBaseStack().isEmpty()){
			ItemStack base = this.getBaseStack();
			if(!this.findMaterial(base).isEmpty()){
				//				UnsagaMod.logger.trace("ddd", this.getMaterialOrNot(base).get());
				UnsagaMaterial mate = this.findMaterial(base);
				ToolCategory cate = this.getAllowedToolCategories().get(currentCategory);
				//				UnsagaMod.logger.trace("zzz", SuitableLists.getSuitableList(cate).values());
				if(mate.isSuitable(cate)){

					return true;
				}
			}


		}
		return false;
	}
	@Override
	public void onPacketData() {
		//		Unsaga.debug(this.id);
		this.currentCategory = this.argsSent.getByte("category");
		final ToolCategory category = this.getAllowedToolCategories().get(currentCategory);


		if(this.buttonID==GuiBlacksmithUnsaga.DOFORGE){
			//			final IMaterialAnalyzer analyzerBase = MaterialAnalyzer.hasCapability(this.inventorySmith.getBaseItem()) ? MaterialAnalyzer.getCapability(this.inventorySmith.getBaseItem()) : null;
			//			final IMaterialAnalyzer analyzerSub = MaterialAnalyzer.hasCapability(this.inventorySmith.getMaterial()) ? MaterialAnalyzer.getCapability(this.inventorySmith.getMaterial()) : null;
			//			final MaterialAnalyzer baseItemInfo = this.inventorySmith.getBaseItem()!=null ? new MaterialAnalyzer(this.inventorySmith.getBaseItem()) : null;
			//			final MaterialAnalyzer subItemInfo = this.inventorySmith.getMaterial()!=null ? new MaterialAnalyzer(this.inventorySmith.getMaterial()) : null;



			if(this.isValidBaseItem() && this.isValidPayment() && this.isValidSubItem() && ItemUtil.isItemStackNull(this.inventorySmith.getResult())){
				BlacksmithToolBaker builder = this.trait.toolBuilder(this.findMaterial(getBaseStack()), this.findMaterial(getSubStack()), getBaseStack(), getSubStack());
				ForgeResult result =  builder.decideForgedDurability().decideForgedMaterial()
						.decideForgedWeight().make();
				if(!result.getResult().isEmpty()){
					//					ep.addStat(UnsagaAchievementRegistry.instance().firstSmith);
					if(WorldHelper.isServer(worldobj)){
						if(ep instanceof EntityPlayerMP){
							UnsagaTriggers.FIRST_FORGE.trigger((EntityPlayerMP) ep);
							HSLib.packetDispatcher().sendTo(PacketSound.atPos(result.type().getResultSound(), XYZPos.createFrom(ep)), (EntityPlayerMP) this.ep);
						}
						this.inventorySmith.setInventorySlotContents(BASE, ItemStack.EMPTY);
						this.inventorySmith.setInventorySlotContents(SUB, ItemStack.EMPTY);
						this.inventorySmith.decrStackSize(PAYMENT, 1);
						this.inventorySmith.setInventorySlotContents(FORGED, result.getResult());
					}

				}
			}

		}

	}

	public ItemStack getForgedInv(){
		return this.inventorySmith.getStackInSlot(FORGED);
	}

	//	public EnumPayValues getPaymentValue(){
	//		if(this.inventorySmith.getPayment()!=null){
	//			return ValidPayments.getValueFromItemStack(this.inventorySmith.getPayment());
	//		}
	//		return null;
	//	}


	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer)
	{
		super.onContainerClosed(par1EntityPlayer);
		if(this.theMerchant!=null){
			this.theMerchant.setCustomer(null);
		}

	}


	public EntityLivingBase getSmith(){
		if(this.theMerchant==null){
			return this.ep;
		}
		return (EntityLivingBase) this.theMerchant;
	}

	public ItemStack getBaseStack(){
		return this.inventorySmith.getBaseItem();
	}

	public ItemStack getSubStack(){
		return this.inventorySmith.getSubMaterial();
	}

	public ItemStack getPaymentStack(){
		return this.inventorySmith.getPayment();
	}

	/** クライアント側*/
	public List<UnsagaMaterial> getForgeableMaterials(){
		List<UnsagaMaterial> list = new ArrayList<>();
		//		UnsagaMod.logger.trace("fff", this.isValidBaseItem(),this.isValidSubItem());
		if(this.isValidBaseItem() && this.isValidSubItem()){
			//			list.add(this.getMaterialOrNot(getBaseStack()).get());
			list.addAll(BlacksmithToolBaker.getForgeableMaterials(this.findMaterial(getBaseStack()),this.findMaterial(getSubStack())));
		}
		return list;
	}

	public void setCurrentCategory(byte cate){
		this.currentCategory = cate;
	}
	public Pair<Integer,Integer> getRepairAmount(){
		if(this.isValidBaseItem() && this.isValidSubItem()){
			int a =  BlacksmithToolBaker.getDurability(this.findMaterial(getBaseStack()), this.getBaseStack());
			int b = BlacksmithToolBaker.getRepairAmount(this.findMaterial(getBaseStack()), this.findMaterial(this.getSubStack()), this.getSubStack());
			return Pair.of(a, b);
		}
		return Pair.of(0, 0);
	}


	@Override
	public PacketServerToGui getSyncPacketToClient(EntityPlayer ep){
		return PacketServerToGui.create(UtilNBT.comp().setByte("smithType", (byte) this.type.getMeta()).get());
	}

	@Override
	public void onSlotClick(int rawSlotNumber,int containerSlotNumber,int clickButton,ClickType clickTypeIn,EntityPlayer ep){
		super.onSlotClick(rawSlotNumber, containerSlotNumber, clickButton, clickTypeIn, ep);


		//		this.isForgeable = this.isValidBaseItem() && this.isValidSubItem();
		//
		if(this.isValidBaseItem() && this.isValidSubItem()){
			this.process = Optional.of(this.trait.toolBuilder(this.findMaterial(this.inventorySmith.getBaseItem()), this.findMaterial(this.inventorySmith.getSubMaterial())
					, this.inventorySmith.getBaseItem(), this.inventorySmith.getSubMaterial()));
		}else{
			this.process = Optional.empty();
		}

		//		if(this.ep instanceof EntityPlayerMP){
		//			HSLib.core().getPacketDispatcher().sendTo(this.getSyncPacketToClient(ep), (EntityPlayerMP) ep);
		//		}
		//

	}
	@Override
	public ItemStack transferStackInSlot(Slot slot)
	{
		return this.transferLogic.transferStackInSlot(slot);
	}
}
