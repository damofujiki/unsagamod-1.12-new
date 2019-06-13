package mods.hinasch.unsaga.core.world.chunk;

import java.util.Optional;

import mods.hinasch.lib.capability.CapabilityAdapterFactory.CapabilityAdapterPlanImpl;
import mods.hinasch.lib.capability.CapabilityAdapterFrame;
import mods.hinasch.lib.capability.CapabilityStorage;
import mods.hinasch.lib.capability.ComponentCapabilityAdapters;
import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.iface.IIntSerializable;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.chest.ChunkChestStorage;
import mods.hinasch.unsaga.chest.ClientChestPosCache;
import mods.hinasch.unsaga.element.AspectOffset;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UnsagaChunkCapability {

	public static final int CACHE_CHEST_POS = 1;
	public static final int SYNC_ASPECT = 2;

	public static enum PacketOperation implements IIntSerializable{
		CACH_CHEST_POS(1),SYNC_ASPECT(2);

		int meta;
		private PacketOperation(int meta){
			this.meta = meta;
		}

		public int meta(){
			return this.meta;
		}

		public static PacketOperation fromMeta(int meta){
			return HSLibs.fromMeta(PacketOperation.values(),meta);
		}

		@Override
		public int getMeta() {
			// TODO 自動生成されたメソッド・スタブ
			return this.meta;
		}
	}
	@CapabilityInject(IUnsagaChunk.class)
	public static Capability<IUnsagaChunk> CAPA;
	public static final String SYNC_ID = "unsaga.chunk";

	public static final CapabilityAdapterFrame<IUnsagaChunk> BUILDER = UnsagaMod.CAPA_ADAPTER_FACTORY.create(new CapabilityAdapterPlanImpl(
			()->CAPA,()->IUnsagaChunk.class,()->DefaultImpl.class,Storage::new));
	public static final ComponentCapabilityAdapters.Chunk<IUnsagaChunk> ADAPTER = BUILDER.createChildChunk(SYNC_ID);

	static{

		ADAPTER.setPredicate(ev -> ev.getObject() instanceof Chunk);
		ADAPTER.setRequireSerialize(true);
	}

	public static class DefaultImpl implements IUnsagaChunk{
		AspectOffset aspect = AspectOffset.ZERO;
		//		ElementTable fluctuation = ElementTable.ZERO;
		ChunkChestStorage chestInfo = new ChunkChestStorage();


		@Override
		public AspectOffset getAspectOffset() {
			// TODO 自動生成されたメソッド・スタブ
			return this.aspect;
		}

		@Override
		public void setAspectOffset(AspectOffset elm) {
			// TODO 自動生成されたメソッド・スタブ

		}

		@Override
		public ChunkChestStorage chunkChestStorage() {
			// TODO 自動生成されたメソッド・スタブ
			return this.chestInfo;
		}

		@Override
		public void setChunkChestStorage(ChunkChestStorage info) {
			// TODO 自動生成されたメソッド・スタブ
			this.chestInfo = info;
		}

		@Override
		public NBTTagCompound getSendingData() {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}

		@Override
		public void catchSyncData(NBTTagCompound nbt) {
			// TODO 自動生成されたメソッド・スタブ

		}

		@Override
		public void onPacket(PacketSyncCapability message, MessageContext ctx) {
			// TODO 自動生成されたメソッド・スタブ
			if(ctx.side.isServer()){
				World world = ctx.getServerHandler().player.world;
				Chunk chunk = world.getChunkFromBlockCoords(ctx.getServerHandler().player.getPosition());
				AspectOffset aspect = UnsagaChunkCapability.ADAPTER.getCapability(chunk).getAspectOffset();

				HSLib.packetDispatcher().sendTo(PacketSyncCapability.create(CAPA, ADAPTER.getCapability(chunk)
						,UtilNBT.comp()
						.write(in ->aspect.writeToNBT(in))
						.setInteger("operation", SYNC_ASPECT)
						.get())
						, ctx.getServerHandler().player);
			}
			if(ctx.side.isClient()){

				Optional.ofNullable(PacketOperation.fromMeta(message.getArgs().getInteger("operation")))
				.ifPresent(operation ->{
					switch(operation){
					case CACH_CHEST_POS:
						WorldClient world = (WorldClient) ClientHelper.getWorld();
						//					int operation = message.getArgs().getInteger("op");
						UtilNBT.comp(message.getArgs())
						.extract(in ->UtilNBT.readListFromNBT(in, "chests", ChunkChestStorage::restore)
								, chests -> ClientChestPosCache.setChestPosCache(0,chests));
						break;
					case SYNC_ASPECT:
						Chunk chunk = ClientHelper.getWorld().getChunkFromBlockCoords(ClientHelper.getPlayer().getPosition());
						UnsagaChunkCapability.ADAPTER.getCapability(chunk).setAspectOffset(AspectOffset.restore(message.getArgs()));
						break;
					default:
						break;

					}

				});



			}

		}

		@Override
		public String getIdentifyName() {
			// TODO 自動生成されたメソッド・スタブ
			return SYNC_ID;
		}

	}

	public static class Storage extends CapabilityStorage<IUnsagaChunk>{

		@Override
		public void writeNBT(NBTTagCompound comp, Capability<IUnsagaChunk> capability,
				IUnsagaChunk instance, EnumFacing side) {

			UtilNBT.comp(comp)
			.setTag("chestInfo", UtilNBT.comp()
					.write(child ->instance.chunkChestStorage().writeToNBT(child)).get());

		}

		@Override
		public void readNBT(NBTTagCompound comp, Capability<IUnsagaChunk> capability,
				IUnsagaChunk instance, EnumFacing side) {

			UtilNBT.comp(comp)
			.extract(in ->ChunkChestStorage.restore((NBTTagCompound) in.getTag("chestInfo"))
					, instance::setChunkChestStorage);


		}

	}

	public static void onLivingUpdate(LivingUpdateEvent e){
		World world = e.getEntityLiving().getEntityWorld();
		Chunk chunk = world.getChunkFromBlockCoords(e.getEntityLiving().getPosition());
		ADAPTER.getCapabilityOptional(chunk)
		.ifPresent(in ->{
			long current = world.getTotalWorldTime();
			if(in.getAspectOffset().getNextEffectTime()>0){
				if(in.getAspectOffset().getNextEffectTime()<current){
					UnsagaMod.logger.trace("aspect", "平衡がすすみました");
					in.getAspectOffset().processEquilibrium(world.rand, current);
				}
			}else{
				in.setAspectOffset(AspectOffset.ZERO);
			}
		});

	}
	public static void registerEvents(){
		//		HSLibs.registerEvent(new EventChunkLoad());
		PacketSyncCapability.registerSyncCapability(SYNC_ID, CAPA);
		ADAPTER.registerAttachEvent((inst,capa,facing,ev)->{

		});
	}


}
