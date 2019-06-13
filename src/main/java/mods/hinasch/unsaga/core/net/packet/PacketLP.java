package mods.hinasch.unsaga.core.net.packet;

import java.util.Optional;
import java.util.UUID;

import com.mojang.realmsclient.util.Pair;

import io.netty.buffer.ByteBuf;
import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.lib.network.PacketUtil;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.lp.LifePoint;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class PacketLP implements IMessage{


	NBTTagCompound nbt;
	private UUID uuid;
	public UUID getUuid() {
		return uuid;
	}
	private int lp;
	private int entityid = -1;
	private boolean isPlayer;
	public boolean isPlayer() {
		return isPlayer;
	}
	private boolean isRenderDamagePacket;
	private boolean isRequestPacket;
	public PacketLP(){
		this.isRenderDamagePacket = false;
		this.isRequestPacket = false;
		this.isPlayer = false;
	}

	protected PacketLP(final int entityid,final int lp){
		this.entityid = entityid;
		this.lp = lp;
		this.isRenderDamagePacket = false;
	}
	//	protected PacketLP(UUID entityid,int lp){
	//		this.uuid = entityid;
	//		this.lp = lp;
	//		this.isRenderDamagePacket = false;
	//	}
	public static PacketLP createRenderDamagePacket(final EntityLivingBase entityid,final int lpdamage){
		final PacketLP psl = new PacketLP(entityid.getEntityId(),lpdamage);
		psl.isRenderDamagePacket = true;
		return psl;
	}
	public static PacketLP createRequest(final EntityLivingBase entityid){

		UnsagaMod.logger.trace("entitjoin", "リクエストパケットを生成");
		if(entityid instanceof EntityPlayer){
			final PacketLP psl = new PacketLP(-1,-1);
			psl.isRequestPacket = true;
			psl.isPlayer = true;
			return psl;
		}
		final PacketLP psl = new PacketLP(entityid.getEntityId(),-1);
		psl.isRequestPacket = true;
		return psl;

	}


	public static PacketLP create(final int entityid,final int lp){
		return new PacketLP(entityid,lp);

	}
	public static PacketLP create(final EntityLivingBase entityid,final int lp){
		return new PacketLP(entityid.getEntityId(),lp);

	}
	@Override
	public void fromBytes(final ByteBuf buf) {
		final int length = buf.readInt();
		final ByteBuf bytes = buf.readBytes(length);
		this.nbt = PacketUtil.bytesToNBT(bytes);
		this.entityid = nbt.getInteger("entityid");
		this.lp = nbt.getByte("lp");
		this.isRenderDamagePacket = nbt.getBoolean("isRender");
		this.isRequestPacket = nbt.getBoolean("isRequest");
		this.isPlayer = nbt.getBoolean("isPlayer");
		if(nbt.hasKey("uuid")){
			this.uuid = nbt.getUniqueId("uuid");
		}

		//		this.entityid = buf.readInt();
		//		this.lp = buf.readInt();
		//
		//		this.isRenderDamagePacket = buf.readBoolean();
		//		this.isRequestPacket = buf.readBoolean();
	}

	@Override
	public void toBytes(final ByteBuf buf) {
		this.nbt = UtilNBT.comp().setInteger("entityid", entityid)
				.setByte("lp", (byte)lp).setBoolean("isRender", this.isRenderDamagePacket)
				.setBoolean("isRequest", this.isRequestPacket)
				.setBoolean("isPlayer", this.isPlayer).get();

		if(this.uuid!=null){
			this.nbt.setUniqueId("uuid", uuid);
		}
		final byte[] bytes = PacketUtil.nbtToBytes(nbt);
		buf.writeInt(bytes.length);
		buf.writeBytes(bytes);
		//
		//		buf.writeInt(entityid);
		//		buf.writeInt(lp);
		//		buf.writeBoolean(isRenderDamagePacket);
		//		buf.writeBoolean(isRequestPacket);
	}

	public boolean isRenderRequest(){
		return this.isRenderDamagePacket;
	}
	public int getEntityID(){
		return this.entityid;
	}

	public int lp(){
		return this.lp;
	}
	public static class PacketLPHandler implements IMessageHandler<PacketLP,IMessage>{

		private IMessage onServerMessage(final Pair<PacketLP,Entity> ctx){
			final PacketLP message = ctx.first();
			final Entity entity = ctx.second();
			if(message.isRequestPacket){
				//						AsyncLPInitializer event = new AsyncLPInitializer(world,message.getEntityID(),message.getLP());
				//						HSLib.scannerEventPool.addEvent(event);
				//						return null;
				UnsagaMod.logger.trace("LP同期", entity,message.getEntityID());


				return LifePoint.adapter.getCapabilityOptional(entity)
						.filter(in -> entity instanceof EntityLivingBase)
						.map(in ->{
							UnsagaMod.logger.trace("LP同期リクエストを確認", in.lifePoint());
							return PacketSyncCapability.create(LifePoint.CAPA, in
									,UtilNBT.comp().setInteger("entityid", entity.getEntityId()).get());
						}).orElse(null);

			}
			if(entity instanceof EntityLivingBase){
				final EntityLivingBase living = (EntityLivingBase) entity;
				//						ExtendedDataLP data = Unsaga.lpLogicManager.getData(living);
				//						ILifePoint capa = LPLogicManager.getCapability(living);
				//						capa.setLifePoint(message.getLP());
				LifePoint.adapter.getCapabilityOptional(living)
				.ifPresent(in ->in.setLifePoint(message.lp()));
			}
			return null;
		}
		private void onClientMessage(final Pair<PacketLP,EntityLivingBase> ctx){
			final PacketLP message = ctx.first();
			if(message.isRenderRequest()){
				final EntityLivingBase living = (EntityLivingBase) ctx.second();

				LifePoint.adapter.getCapabilityOptional(living)
				.ifPresent(in ->{
					in.setRenderingDamage(message.lp());
					in.markStartRenderingDamage(true);
				});

			}else{
				if(ctx.second() instanceof EntityLivingBase){
					UnsagaMod.logger.trace("LP sync!"+message.lp());
					final EntityLivingBase living = (EntityLivingBase) ctx.second();
					LifePoint.adapter.getCapabilityOptional(living)
					.ifPresent(in ->in.setLifePoint(message.lp()));
				}
			}
		}
		@Override
		public IMessage onMessage(final PacketLP message, final MessageContext ctx) {

			if(ctx.side.isClient()){

				Optional.ofNullable(ClientHelper.getPlayer())
				.ifPresent(player ->{
					final World world = player.getEntityWorld();
					Optional.ofNullable(world.getEntityByID(message.getEntityID()))
					.filter(in ->in instanceof EntityLivingBase)
					.map(in ->Pair.of(message,(EntityLivingBase)in))
					.ifPresent(this::onClientMessage);
				});


			}else{
				//Server Side


				final EntityPlayerMP player = ctx.getServerHandler().player;
				final World world = player.world;
				return Optional.ofNullable(message.isPlayer ? player : world.getEntityByID(message.getEntityID()))
						.map(in ->Pair.of(message,in))
						.map(this::onServerMessage)
						.orElse(null);
				//				entity = world.getEntityByID(message.getEntityID());
				//				UnsagaMod.logger.trace("entityid", world.getLoadedEntityList().stream().map(in -> String.valueOf(in.getEntityId())+","+in.getName()).collect(Collectors.toList()));

			}
			return null;
		}

	}

	//	/**
	//	 * 非同期
	//	 *
	//	 */
	//	public static class AsyncLPInitializer extends AsyncUpdateEvent{
	//
	//		World world;
	//		int id;
	//		int lp;
	//		boolean finishSync = false;
	//		public AsyncLPInitializer(World world,int entityid,int lp){
	//			super(null,"lp");
	//			UnsagaMod.logger.trace("LP非同期イニシャライザー", entityid);
	//			this.id = entityid;
	//			this.lp = lp;
	//			this.world = world;
	//		}
	//		@Override
	//		public boolean hasFinished() {
	//			// TODO 自動生成されたメソッド・スタブ
	//			return finishSync;
	//		}
	//
	//		@Override
	//		public void loop() {
	//			// TODO 自動生成されたメソッド・スタブ
	//			if(world.getEntityByID(id)!=null){
	//				Entity entity = world.getEntityByID(id);
	//				if(entity instanceof EntityLivingBase){
	//					UnsagaMod.logger.trace("LP同期します", this.id);
	//					EntityLivingBase living = (EntityLivingBase) entity;
	//					UnsagaMod.PACKET_DISPATCHER.sendToAll(PacketLP.create(living.getEntityId(),lp));
	////					if(LPLogicManager.hasCapability(living)){
	////						LPLogicManager.getCapability(living).setLifePoint(lp);
	//						this.finishSync = true;
	////					}
	//				}
	//			}
	//		}
	//
	//	}
}
