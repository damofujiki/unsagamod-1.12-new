package mods.hinasch.unsaga.status;

import java.util.EnumSet;
import java.util.Optional;

import mods.hinasch.lib.capability.CapabilityAdapterFactory.CapabilityAdapterPlanImpl;
import mods.hinasch.lib.capability.CapabilityAdapterFrame;
import mods.hinasch.lib.capability.CapabilityStorage;
import mods.hinasch.lib.capability.ComponentCapabilityAdapters;
import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.advancement.UnsagaTriggers;
import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew;
import mods.hinasch.unsagamagic.item.UnsagaMagicItems;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UnsagaXPCapability {


	public static final String ID_SYNC = "exp";
	@CapabilityInject(IUnsagaXP.class)
	public static Capability<IUnsagaXP> CAPA;

	public static class Storage extends CapabilityStorage<IUnsagaXP>{


		@Override
		public void writeNBT(NBTTagCompound comp, Capability<IUnsagaXP> capability, IUnsagaXP instance,
				EnumFacing side) {
			comp.setInteger("skillPoint", instance.getSkillPointPiece());
			comp.setInteger("decipheringPoint", instance.getDecipheringPoint());
			comp.setInteger("skillLevel", instance.getSkillPoint());


		}

		@Override
		public void readNBT(NBTTagCompound comp, Capability<IUnsagaXP> capability, IUnsagaXP instance,
				EnumFacing side) {
			instance.setDecipheringPoint(comp.getInteger("decipheringPoint"));
			instance.setSkillPointPiece(comp.getInteger("skillPoint"));
			instance.setSkillPoint(comp.getInteger("skillLevel"));
		}

	}

	public static CapabilityAdapterFrame<IUnsagaXP> base = UnsagaMod.CAPA_ADAPTER_FACTORY.create(
			new CapabilityAdapterPlanImpl(()->CAPA,()->IUnsagaXP.class,()->DefaultImpl.class,Storage::new));
	public static ComponentCapabilityAdapters.Entity<IUnsagaXP> adapter = base.createChildEntity("skillPoint");
	static{
		adapter.setPredicate(ev -> ev.getObject() instanceof EntityPlayer);
		adapter.setRequireSerialize(true);
	}


	public static class DefaultImpl implements IUnsagaXP{

		int skillPointPiece = 0;
		int skillPoint = 0;
		int decipheringPoint = 0;



		static final int SKILLPOINT_MAX = 10000;
		static final int SKILLPOINT_LEVEL_MAX = 99;
		static final int DECIPHERING_MAX = 50;
		@Override
		public int getSkillPointPiece() {
			// TODO 自動生成されたメソッド・スタブ
			return this.skillPointPiece;
		}

		@Override
		public void setSkillPointPiece(int par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.skillPointPiece = par1;
		}

		@Override
		public void addSkillPointPiece(int par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.skillPointPiece += (par1 *2);
			this.skillPointPiece = MathHelper.clamp(skillPointPiece, 0, SKILLPOINT_MAX);
			int next = this.skillPoint >=30 ? 62 + (this.skillPoint -30) *7 : (this.skillPoint>=15 ? 17+(this.skillPoint - 15)*3 :17);
			if(this.skillPoint<SKILLPOINT_LEVEL_MAX){
				if(next<this.skillPointPiece){
					this.skillPoint += 1;
					this.skillPointPiece = 0;
				}
			}

		}

		@Override
		public int getDecipheringPoint() {
			// TODO 自動生成されたメソッド・スタブ
			return this.decipheringPoint;
		}

		@Override
		public void setDecipheringPoint(int par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.decipheringPoint = par1;
			if(this.decipheringPoint<0){
				this.decipheringPoint = 0;
			}
		}

		@Override
		public void addDecipheringPoint(int par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.decipheringPoint += par1;
			this.decipheringPoint = MathHelper.clamp(decipheringPoint, 0, DECIPHERING_MAX);
		}

		@Override
		public int getSkillPoint() {
			// TODO 自動生成されたメソッド・スタブ
			return this.skillPoint;
		}

		@Override
		public void addSkillPoint(int par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.skillPoint += par1;
		}

		@Override
		public void setSkillPoint(int par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.skillPoint = par1;
		}

		@Override
		public NBTTagCompound getSendingData() {
			// TODO 自動生成されたメソッド・スタブ
			return (NBTTagCompound) CAPA.writeNBT(this, null);
		}

		@Override
		public void catchSyncData(NBTTagCompound nbt) {
			CAPA.readNBT(this, null, nbt);

		}

		@Override
		public void onPacket(PacketSyncCapability message, MessageContext ctx) {
			if(ClientHelper.getPlayer()!=null){
				UnsagaXPCapability.adapter.getCapabilityOptional(ClientHelper.getPlayer())
				.ifPresent(in -> in.catchSyncData(message.getNbt()));
				UnsagaMod.logger.trace(this.getClass().getName(),"sync!");
			}
		}



		@Override
		public String getIdentifyName() {
			// TODO 自動生成されたメソッド・スタブ
			return ID_SYNC;
		}

	}

	public static enum Type{
		SKILL,DECIPHER;
	}
	public static boolean hasCapability(EntityPlayer ep){
		return adapter.hasCapability(ep);
	}

	public static IUnsagaXP getCapability(EntityPlayer ep){
		return adapter.getCapability(ep);
	}


	public static void syncAdditionalXP(EntityPlayer ep){
		if(WorldHelper.isServer(ep.world)){
			UnsagaXPCapability.adapter.getCapabilityOptional(ep)
			.ifPresent(in ->HSLib.packetDispatcher().sendTo(PacketSyncCapability.create(CAPA,in), (EntityPlayerMP) ep));
		}
	}

	public static void registerEvents(){
		PacketSyncCapability.registerSyncCapability(ID_SYNC, CAPA);
		adapter.registerAttachEvent();
		HSLibs.registerEvent(new UnsagaXPCapability());
	}

	@SubscribeEvent
	public void onTabletPickUp(EntityItemPickupEvent ev){
		EntityPlayerMP ep = (EntityPlayerMP) ev.getEntityPlayer();
		Optional.of(ev.getItem().getItem())
		.filter(in -> !in.isEmpty())
		.filter(in -> in.getItem()==UnsagaMagicItems.TABLET)
		.filter(in -> WorldHelper.isServer(ep.getEntityWorld()))
		.ifPresent(in ->{
			UnsagaTriggers.GET_TABLET.trigger(ep);
		});
	}
	@SubscribeEvent
	public void onExpPickUp(PlayerPickupXpEvent ev){
		UnsagaXPCapability.adapter.getCapabilityOptional(ev.getEntityPlayer())
		.ifPresent(in ->{
			int xpValue = ev.getOrb().getXpValue();
			in.addSkillPointPiece((int) (xpValue*UnsagaConfigHandlerNew.PANEL_GROWTH.multiplySkillPoint));
		});
//		if(!AccessorySlotCapability.adapter.getCapability(ev.getEntityPlayer()).getTablet().isEmpty()){
//			//				if(UnsagaUnlockableContentCapability.adapter.getCapability(ev.getEntityPlayer()).hasUnlockedDeciphering()){
//			capabilityInstance.addDecipheringPoint(xpValue*UnsagaMod.configs.getDecipheringXPMultiply());
//			//				}
//		}



	}

	public static void displayAdditionalXP(EntityPlayer player,FontRenderer fontRendererObj,EnumSet<Type> types,int x,int y,int color){
		UnsagaXPCapability.adapter.getCapabilityOptional(player).ifPresent(in ->{
			if(types.contains(UnsagaXPCapability.Type.DECIPHER)){
				fontRendererObj.drawString(String.valueOf(in.getDecipheringPoint()), x,y,color);
			}
			if(types.contains(UnsagaXPCapability.Type.SKILL)){
				fontRendererObj.drawString(String.valueOf(in.getSkillPoint()), x,y+20,color);
			}
		});


	}
}
