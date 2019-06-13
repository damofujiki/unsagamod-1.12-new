package mods.hinasch.unsaga.chest;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import mods.hinasch.lib.capability.CapabilityAdapterFactory.CapabilityAdapterPlanImpl;
import mods.hinasch.lib.capability.CapabilityAdapterFrame;
import mods.hinasch.lib.capability.CapabilityStorage;
import mods.hinasch.lib.capability.ComponentCapabilityAdapters;
import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.client.gui.GuiChest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ChestCapability {

	public static class DefaultImpl implements IChestCapability{

		Optional<EntityPlayer> ep = Optional.empty();
		boolean magicLocked = false;
		boolean opened = false;
		boolean analyzed = true;
		Class chestType;
		boolean init = false;
		int level = 0;
		List<ChestTrap> traps = Lists.newArrayList();
		boolean hasLocked = false;
		ChestTreasureType treasureType = ChestTreasureType.MONEY;

		@Override
		public void catchSyncData(NBTTagCompound nbt) {
			CAPA.getStorage().readNBT(CAPA, this, null, nbt);

		}

		@Override
		public Class getChestType() {
			// TODO 自動生成されたメソッド・スタブ
			return this.chestType;
		}

		@Override
		public String getIdentifyName() {
			// TODO 自動生成されたメソッド・スタブ
			return ID_SYNC;
		}

		@Override
		public int level() {
			// TODO 自動生成されたメソッド・スタブ
			return this.level;
		}

		@Override
		public Optional<EntityPlayer> openingPlayer() {
			// TODO 自動生成されたメソッド・スタブ
			return this.ep;
		}

		@Override
		public NBTTagCompound getSendingData() {
			return (NBTTagCompound) CAPA.getStorage().writeNBT(CAPA, this, null);
		}

		@Override
		public List<ChestTrap> traps() {
			// TODO 自動生成されたメソッド・スタブ
			return ImmutableList.copyOf(this.traps);
		}

		@Override
		public boolean hasAnalyzed() {
			// TODO 自動生成されたメソッド・スタブ
			return this.analyzed;
		}

		@Override
		public boolean hasDefused() {
			// TODO 自動生成されたメソッド・スタブ
			return this.traps.isEmpty();
		}

		@Override
		public boolean hasInitialized() {
			// TODO 自動生成されたメソッド・スタブ
			return this.init;
		}

		@Override
		public boolean hasLocked() {
			// TODO 自動生成されたメソッド・スタブ
			return this.hasLocked;
		}

		@Override
		public boolean hasMagicLocked() {
			// TODO 自動生成されたメソッド・スタブ
			return this.magicLocked;
		}

		@Override
		public boolean hasOpened() {
			// TODO 自動生成されたメソッド・スタブ
			return this.opened;
		}

		@Override
		public void onPacket(PacketSyncCapability message, MessageContext ctx) {
			int id = message.getArgs().getInteger("entityid");

			Optional.ofNullable(ClientHelper.getWorld().getEntityByID(id))
			.ifPresent(ent ->{
				ChestCapability.ADAPTER_ENTITY.getCapabilityOptional(ent)
				.ifPresent(cap ->{
					cap.catchSyncData(message.getNbt());
					Optional.of(ClientHelper.getCurrentGui())
					.filter(gui -> gui instanceof GuiChest)
					.map(gui ->(GuiChest)gui)
					.ifPresent(gui -> gui.updateCapability(cap));
				});

			});

		}

		@Override
		public void setAnalyzed(boolean par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.analyzed = par1;
		}

		@Override
		public void setChestType(Class clazz) {
			this.chestType = clazz;

		}

		@Override
		public void setDefused() {
			// TODO 自動生成されたメソッド・スタブ
			this.traps.clear();
		}

		@Override
		public void setInitialized(boolean par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.init = par1;
		}

		@Override
		public void setLevel(int par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.level = par1;
		}

		@Override
		public void setLocked(boolean par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.hasLocked = par1;
		}

		@Override
		public void setMagicLocked(boolean par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.magicLocked = par1;
		}

		@Override
		public void setOpened(boolean par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.opened = par1;
		}

		@Override
		public void setOpeningPlayer(EntityPlayer ep) {
			this.ep = Optional.ofNullable(ep);

		}

		@Override
		public void updateTraps(List<ChestTrap> traps) {
			// TODO 自動生成されたメソッド・スタブ
			this.traps = traps;
		}

		@Override
		public ChestTreasureType treasureType() {
			// TODO 自動生成されたメソッド・スタブ
			return this.treasureType;
		}

		@Override
		public void setTreasureType(ChestTreasureType par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.treasureType = par1;
		}

	}
	public static class Storage extends CapabilityStorage<IChestCapability>{

		@Override
		public void readNBT(NBTTagCompound comp, Capability<IChestCapability> capability, IChestCapability instance, EnumFacing side) {
//			UnsagaMod.logger.trace(this.getClass().getName(), instance.getTraps());
			if(comp.hasKey("level")){
				instance.setLevel(comp.getByte("level"));
			}
			if(comp.hasKey("traps")){
				instance.updateTraps(UtilNBT.readListFromNBT(comp,"traps",ChestTrap::restore));
			}else{
				instance.updateTraps(new ArrayList<>());
			}
			if(comp.hasKey("init")){
				instance.setInitialized(comp.getBoolean("init"));
			}
			if(comp.hasKey("lock")){
				instance.setLocked(comp.getBoolean("lock"));
			}
			if(comp.hasKey("analyzed")){
				instance.setAnalyzed(comp.getBoolean("analyzed"));
			}
			if(comp.hasKey("opened")){
				instance.setOpened(comp.getBoolean("opened"));
			}
			if(comp.hasKey("magicLock")){
				instance.setMagicLocked(comp.getBoolean("magicLock"));
			}
			if(comp.hasKey("treasureType")){
				instance.setTreasureType(ChestTreasureType.fromName(comp.getString("treasureType")));
			}
		}

		@Override
		public void writeNBT(NBTTagCompound comp, Capability<IChestCapability> capability, IChestCapability instance, EnumFacing side) {
			comp.setByte("level", (byte) instance.level());
			if(!instance.traps().isEmpty()){

				UtilNBT.writeListToNBT(instance.traps(), comp, "traps");
			}
			comp.setBoolean("init", instance.hasInitialized());
			comp.setBoolean("lock",instance.hasLocked());
			comp.setBoolean("analyzed", instance.hasAnalyzed());
			comp.setBoolean("opened", instance.hasOpened());
			comp.setBoolean("magicLock", instance.hasMagicLocked());
			comp.setString("treasureType", instance.treasureType().getName());
		}

	}

	@CapabilityInject(IChestCapability.class)
	public static Capability<IChestCapability> CAPA;

	public static final String ID_SYNC = "chest";

	public static CapabilityAdapterFrame<IChestCapability> BUILDER = UnsagaMod.CAPA_ADAPTER_FACTORY.create(
			new CapabilityAdapterPlanImpl(()->CAPA,()->IChestCapability.class,()->DefaultImpl.class,Storage::new));
	public static ComponentCapabilityAdapters.Entity<IChestCapability> ADAPTER_ENTITY =BUILDER.createChildEntity("chest");

	//未使用
	public static ComponentCapabilityAdapters.TileEntity<IChestCapability> adapterTE = BUILDER.createChildTileEntity("techest");

	static{
		ADAPTER_ENTITY.setPredicate(ev -> ev.getObject() instanceof IChestBehavior);
		ADAPTER_ENTITY.setRequireSerialize(true);
	}

	public static void register(){
		Random rand = UnsagaMod.secureRandom;
		ADAPTER_ENTITY.registerAttachEvent((inst,capa,facing,ev)->{
			if(!inst.hasInitialized()){

				inst.setChestType(Entity.class);
				inst.setLevel(rand.nextInt(99)+1);
				//レベルによって鍵率が変わる
				if(inst.level()>15){
					inst.setAnalyzed(false);
					if(rand.nextFloat()<0.5F+0.0049F*inst.level()){
						inst.setLocked(true);
					}

					if(inst.level()>30){
						if(rand.nextFloat()<0.5F+0.0049F*inst.level()){
							inst.setMagicLocked(true);
						}
					}

				}

				//中身のタイプはこの時に決まる
				ChestTreasureType type = WeightedRandom.getRandomItem(rand,ChestTreasureType.createList(inst.level())).getItem();
				inst.setTreasureType(type);
				inst.updateTraps(ChestHelper.getInitializedTraps(inst, rand));
				inst.setInitialized(true);
			}
		});

//		HSLibs.registerEvent(new EventUnlockMagic());
//		HSLibs.registerEvent(new EventGenerateChest());
//		HSLibs.registerEvent(new EventChestAppear());
//		HSLibs.registerEvent(new EventChestSpawn());
		PacketSyncCapability.registerSyncCapability(ID_SYNC, CAPA);
	}

	public static void registerChunkEvent(){
//		HSLibs.registerEvent(new EventGenerateChest());
	}
}
