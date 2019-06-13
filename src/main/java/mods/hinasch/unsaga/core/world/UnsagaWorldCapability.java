package mods.hinasch.unsaga.core.world;

import java.util.UUID;

import mods.hinasch.lib.capability.CapabilityAdapterFactory.CapabilityAdapterPlanImpl;
import mods.hinasch.lib.capability.CapabilityAdapterFrame;
import mods.hinasch.lib.capability.CapabilityStorage;
import mods.hinasch.lib.capability.ComponentCapabilityAdapters;
import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.common.panel_bonus.HexAdapter;
import mods.hinasch.unsaga.core.inventory.InventorySkillPanel;
import mods.hinasch.unsaga.minsaga.classes.PlayerClassStorage;
import mods.hinasch.unsaga.skillpanel.GrowthPanelStorage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UnsagaWorldCapability {
	@CapabilityInject(IUnsagaWorld.class)
	public static Capability<IUnsagaWorld> CAPA;
	public static final String SYNC_ID = "unsaga.world";
	public static final UUID UUID_DEBUG = UUID.fromString("db8ff9a2-9ef5-4371-9e98-42d694add74a");

	public static final CapabilityAdapterFrame<IUnsagaWorld> BUILDER = UnsagaMod.CAPA_ADAPTER_FACTORY.create(
			new CapabilityAdapterPlanImpl(()->CAPA,()->IUnsagaWorld.class,()->DefaultImpl.class,Storage::new));
	public static final ComponentCapabilityAdapters.World<IUnsagaWorld> ADAPTER = BUILDER.createChildWorld(SYNC_ID);

	static{
		ADAPTER.setPredicate(ev -> ev.getObject() instanceof World);
	}

	public static class DefaultImpl implements IUnsagaWorld{

		WorldStructureStorage worldStructureInfo = new WorldStructureStorage();
		PlayerClassStorage playerClassStore = new PlayerClassStorage();
		GrowthPanelStorage skillPanelStore = new GrowthPanelStorage();


		@Override
		public NBTTagCompound getSendingData() {
			// TODO 自動生成されたメソッド・スタブ
			return (NBTTagCompound) CAPA.writeNBT(this, null);
		}

		@Override
		public void catchSyncData(NBTTagCompound nbt) {
			// TODO 自動生成されたメソッド・スタブ
			CAPA.readNBT(this, null, nbt);
		}

		@Override
		public void onPacket(PacketSyncCapability message, MessageContext ctx) {
			if(ctx.side.isServer()){ //スキルパネルの同期
				EntityPlayer ep = ctx.getServerHandler().player;
				World world = ctx.getServerHandler().player.getEntityWorld();
				HSLib.packetDispatcher().sendTo(PacketSyncCapability.create(CAPA, UnsagaWorldCapability.ADAPTER.getCapability(world)), (EntityPlayerMP) ep);
				InventorySkillPanel inv = new InventorySkillPanel();
				NonNullList<ItemStack> panelList = UnsagaWorldCapability.ADAPTER.getCapability(world).skillPanelStorage().getPanels(ep.getUniqueID());
				inv.applyItemStackList(panelList);
				HexAdapter hexAdapter = new HexAdapter(inv);
				hexAdapter.createSummary().applyBonus(ep);
				UnsagaWorldCapability.playerClassStorage(world).getClass(ep).refleshClassModifier(ep);
			}
			if(ctx.side.isClient()){

				UnsagaMod.logger.trace(this.getClass().getName(), "スキルパネル同期完了");
				UnsagaWorldCapability.ADAPTER.getCapability(ClientHelper.getWorld()).catchSyncData(message.getNbt());


			}
		}

		@Override
		public String getIdentifyName() {
			// TODO 自動生成されたメソッド・スタブ
			return SYNC_ID;
		}

		@Override
		public WorldStructureStorage structureStorage() {
			// TODO 自動生成されたメソッド・スタブ
			return this.worldStructureInfo;
		}

		@Override
		public void setStructureStorage(WorldStructureStorage info) {
			// TODO 自動生成されたメソッド・スタブ
			this.worldStructureInfo = info;
		}

		@Override
		public PlayerClassStorage playerClassStorage() {
			// TODO 自動生成されたメソッド・スタブ
			return this.playerClassStore;
		}

		@Override
		public GrowthPanelStorage skillPanelStorage() {
			// TODO 自動生成されたメソッド・スタブ
			return this.skillPanelStore;
		}
	}

	public static class Storage extends CapabilityStorage<IUnsagaWorld>{

		@Override
		public void writeNBT(NBTTagCompound comp, Capability<IUnsagaWorld> capability,
				IUnsagaWorld instance, EnumFacing side) {
			//			UnsagaMod.logger.trace(this.getClass().getName(), "saving...");
			instance.skillPanelStorage().writeToNBT(comp);
			instance.playerClassStorage().writeToNBT(comp);
			instance.structureStorage().writeToNBT(comp);
		}

		@Override
		public void readNBT(NBTTagCompound nbt, Capability<IUnsagaWorld> capability, IUnsagaWorld instance,
				EnumFacing side) {
			//			UnsagaMod.logger.trace(this.getClass().getName(), "loading...");
			//			panelDataPerUser = Maps.newHashMap();
			instance.skillPanelStorage().readFromNBT(nbt);
			WorldStructureStorage info = WorldStructureStorage.restore(nbt);
			instance.setStructureStorage(info);

			instance.playerClassStorage().readFromNBT(nbt);
		}

	}


	public static void registerEvents(){
		PacketSyncCapability.registerSyncCapability(SYNC_ID, CAPA);
		ADAPTER.registerAttachEvent();

		//		HSLibs.registerEvent(new SkillPanelSyncEvent());
	}

	public static void onEntityJoinWorld(EntityJoinWorldEvent e){
		//クライアントからリクエストを送って同期
		if(e.getEntity() instanceof EntityPlayer && WorldHelper.isClient(e.getWorld())){
			HSLib.packetDispatcher().sendToServer(PacketSyncCapability.createRequest(CAPA, ADAPTER.getCapability(e.getWorld())));
		}
	}

	public static PlayerClassStorage playerClassStorage(World world){
		return ADAPTER.getCapability(world).playerClassStorage();
	}

	public static GrowthPanelStorage getPanelManager(World world){
		return ADAPTER.getCapability(world).skillPanelStorage();
	}
}
