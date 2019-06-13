package mods.hinasch.unsaga.core.inventory;

import mods.hinasch.lib.capability.CapabilityAdapterFactory.CapabilityAdapterPlanImpl;
import mods.hinasch.lib.capability.CapabilityAdapterFrame;
import mods.hinasch.lib.capability.CapabilityStorage;
import mods.hinasch.lib.capability.ComponentCapabilityAdapters;
import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.material.UnsagaMaterialCapability;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AccessorySlotCapability {

	@CapabilityInject(IAccessoryInventory.class)
	public static Capability<IAccessoryInventory> CAPA;
	public static final String SYNC_ID = "unsaga.accessory_slot";


	public static CapabilityAdapterFrame<IAccessoryInventory> adapterBase = UnsagaMod.CAPA_ADAPTER_FACTORY.create(new CapabilityAdapterPlanImpl(
			()->CAPA,()->IAccessoryInventory.class,()->DefaultImpl.class,Storage::new));
	public static ComponentCapabilityAdapters.Entity<IAccessoryInventory> adapter = adapterBase.createChildEntity(SYNC_ID);

	static{
		adapter.setPredicate(ev -> ev.getObject() instanceof EntityPlayer);
		adapter.setRequireSerialize(true);
	}

	public static class DefaultImpl implements IAccessoryInventory{

		NonNullList<ItemStack> accessorySlots = ItemUtil.createStackList(2);
		ItemStack tabletSlot = ItemStack.EMPTY;
//		ItemStack[] accessorySlot = new ItemStack[2];
		boolean hasInitialized = false;
		@Override
		public boolean hasInitialized() {
			// TODO 自動生成されたメソッド・スタブ
			return hasInitialized;
		}

		@Override
		public void setInitialized(boolean par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.hasInitialized = par1;
		}

		@Override
		public NBTTagCompound getSendingData() {
			return (NBTTagCompound) CAPA.writeNBT(this, null);
		}

		@Override
		public void catchSyncData(NBTTagCompound nbt) {
			CAPA.readNBT(this, null, nbt);

		}

		@Override
		public void onPacket(PacketSyncCapability message, MessageContext ctx) {
			// TODO 自動生成されたメソッド・スタブ

		}

		@Override
		public String getIdentifyName() {
			// TODO 自動生成されたメソッド・スタブ
			return SYNC_ID;
		}



		@Override
		public NonNullList<ItemStack> getAccessories() {
			// TODO 自動生成されたメソッド・スタブ
			return this.accessorySlots;
		}


		@Override
		public boolean hasEmptySlot() {
			return this.accessorySlots.stream().anyMatch(slot -> slot.isEmpty());
		}

		@Override
		public void setAccessories(NonNullList<ItemStack> list) {
			// TODO 自動生成されたメソッド・スタブ
			this.accessorySlots = list;
		}

		@Override
		public void setTablet(ItemStack is) {
			// TODO 自動生成されたメソッド・スタブ
			this.tabletSlot = is;
		}

		@Override
		public ItemStack getTablet() {
			// TODO 自動生成されたメソッド・スタブ
			return this.tabletSlot;
		}

		@Override
		public int getArmorValue() {
			// TODO 自動生成されたメソッド・スタブ
			return this.getAccessories().stream().filter(in -> !in.isEmpty())
			.mapToInt(in -> UnsagaMaterialCapability.adapter.getCapabilityOptional(in).map(cap -> cap.getMaterial()).orElse(UnsagaMaterials.DUMMY).getArmorValueForAccessory())
			.sum();
		}




	}

	public static class Storage extends CapabilityStorage<IAccessoryInventory>{

		@Override
		public void writeNBT(NBTTagCompound comp, Capability<IAccessoryInventory> capability,
				IAccessoryInventory instance, EnumFacing side) {

			UtilNBT.comp(comp)
			.setBoolean("initialized", instance.hasInitialized())
			.setItemStacks("items", instance.getAccessories());

			if(!instance.getTablet().isEmpty()){
				comp.setTag("tablet", UtilNBT.comp().write(in -> instance.getTablet().writeToNBT(in)).get());
			}
		}

		@Override
		public void readNBT(NBTTagCompound comp, Capability<IAccessoryInventory> capability,
				IAccessoryInventory instance, EnumFacing side) {
//			UnsagaMod.logger.trace(this.getClass().getName(), "ok");

			UtilNBT.comp(comp)
			.setToField("initialized", (in,key) ->instance.setInitialized(in.getBoolean(key)))
			.setToField("items", (in,key)->{
				NonNullList<ItemStack> list = UtilNBT.getItemStacksFromNBT(UtilNBT.getTagList(in, key), 2);
				instance.setAccessories(list);
			})
			.setToField("tablet", (in,key)->instance.setTablet(new ItemStack((NBTTagCompound) in.getTag(key))));

		}

	}

	public static void onLivingDeath(LivingDeathEvent e){
		adapter.getCapabilityOptional(e.getEntityLiving())
		.ifPresent(cap ->{
			cap.getAccessories().stream()
			.forEach(in -> ItemUtil.dropItem(e.getEntityLiving().getEntityWorld(), in,XYZPos.createFrom(e.getEntityLiving())));
		});

	}
//	public static class AccessoryDropEvent{
//		@SubscribeEvent
//		public void onLivingDeath(LivingDeathEvent e){
//			if(adapter.hasCapability(e.getEntityLiving())){
//				adapter.getCapability(e.getEntityLiving()).getEquippedList().stream()
//				.forEach(in -> ItemUtil.dropItem(e.getEntityLiving().getEntityWorld(), in,XYZPos.createFrom(e.getEntityLiving())));
//			}
//		}
//	}

	public static void syncAccessories(EntityJoinWorldEvent e){
		if(e.getEntity() instanceof EntityPlayerMP && WorldHelper.isClient(e.getWorld())){
			IAccessoryInventory inst = AccessorySlotCapability.adapter.getCapability(e.getEntity());
			HSLib.packetDispatcher().sendToServer(PacketSyncCapability.createRequest(AccessorySlotCapability.CAPA,inst));
//			HSLib.core().getPacketDispatcher().
		}
	}

	public static int getArmorModifierAmount(EntityLivingBase el){
		return AccessorySlotCapability.adapter.getCapability(el).getArmorValue();
	}
	public static void registerEvents(){
		adapter.registerAttachEvent();
		PacketSyncCapability.registerSyncCapability(SYNC_ID, CAPA);


//		EventEntityJoinWorld.addEvent(e ->{
//			if(e.getEntity() instanceof EntityPlayerMP){
//				IAccessoryInventory inst = AccessorySlotCapability.adapter.getCapability(e.getEntity());
//				HSLib.getPacketDispatcher().sendTo(PacketSyncCapability.create(AccessorySlotCapability.CAPA,inst), (EntityPlayerMP)e.getEntity());
////				HSLib.core().getPacketDispatcher().
//			}
//		});
//		HSLibs.registerEvent(new AccessoryDropEvent());
	}
}
