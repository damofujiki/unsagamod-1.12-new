package mods.hinasch.unsaga.core.entity;

import java.util.Optional;

import mods.hinasch.lib.capability.CapabilityAdapterFactory.CapabilityAdapterPlanImpl;
import mods.hinasch.lib.capability.CapabilityAdapterFrame;
import mods.hinasch.lib.capability.CapabilityStorage;
import mods.hinasch.lib.capability.ComponentCapabilityAdapters;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.common.item.ItemWeaponUnsaga;
import mods.hinasch.unsaga.core.entity.passive.EntityUnsagaChest;
import mods.hinasch.unsaga.util.BlockableWeapon;
import mods.hinasch.unsaga.util.ToolCategory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UnsagaActionCapability {

	public static String SYNC_ID = "unsaga_living_helper";

	@CapabilityInject(IUnsagaAction.class)
	public static Capability<IUnsagaAction> CAPA;

	public static final CapabilityAdapterFrame BUILDER = UnsagaMod.CAPA_ADAPTER_FACTORY.create(new CapabilityAdapterPlanImpl(()->CAPA,()->IUnsagaAction.class,()->DefaultImpl.class,Storage::new));
	public static final ComponentCapabilityAdapters.Entity<IUnsagaAction> ADAPTER = BUILDER.createChildEntity(SYNC_ID);

	static{
		ADAPTER.setRequireSerialize(true);
		ADAPTER.setPredicate(input -> input.getObject() instanceof EntityLivingBase);
	}

	public static class DefaultImpl implements IUnsagaAction{

		int guardCooling = 0;
		int guardProgress = 0;
		int sprintTimer = 0;
		boolean init = false;
		boolean isTechActionProgress = false;

		Optional<EntityVillager> villager = Optional.empty();
		Optional<EntityUnsagaChest> chest = Optional.empty();

		@Override
		public int getWeaponGuardCooling() {
			// TODO 自動生成されたメソッド・スタブ
			return this.guardCooling;
		}

		@Override
		public void decrWeaponGuardProgress() {
			this.guardProgress -= 1;
			if(this.guardProgress<0){
				this.guardProgress = 0;
			}
		}

		@Override
		public void resetWeaponGuardProgress(ToolCategory cate) {
			BlockableWeapon prop = BlockableWeapon.get(cate);
			this.guardCooling = prop.coolingTime();
			this.guardProgress = prop.guardTime();
			return;
		}

		@Override
		public void decrWeaponGuardCooling() {
			// TODO 自動生成されたメソッド・スタブ
			this.guardCooling -= 1;
			if(this.guardCooling<0){
				this.guardCooling = 0;
			}
		}

		@Override
		public int getWeaponGuardProgress() {
			// TODO 自動生成されたメソッド・スタブ
			return this.guardProgress;
		}

		@Override
		public int getSprintTimer() {
			// TODO 自動生成されたメソッド・スタブ
			return this.sprintTimer;
		}

		@Override
		public void setSprintTimer(int timer) {
			this.sprintTimer = timer;
		}

		@Override
		public void setMerchant(Optional<EntityVillager> villager) {
			this.villager = villager;
		}

		@Override
		public Optional<EntityVillager> getMerchant() {
			return this.villager;
		}

		@Override
		public void setEntityChest(EntityUnsagaChest chest) {
			this.chest = Optional.ofNullable(chest);
		}

		@Override
		public Optional<EntityUnsagaChest> getChest() {
			// TODO 自動生成されたメソッド・スタブ
			return this.chest;
		}

		@Override
		public NBTTagCompound getSendingData() {
			return (NBTTagCompound) CAPA.getStorage().writeNBT(CAPA, this, null);
		}

		@Override
		public void catchSyncData(NBTTagCompound nbt) {
			// TODO 自動生成されたメソッド・スタブ
			CAPA.getStorage().readNBT(CAPA, this, null, nbt);
		}

		@Override
		public void onPacket(PacketSyncCapability message, MessageContext ctx) {
			// TODO 自動生成されたメソッド・スタブ
			if(ctx.side.isServer()){ //サーバ側
				EntityPlayer ep = ctx.getServerHandler().player;
				int timer = message.getNbt().getInteger("sprint");
				ADAPTER.getCapability(ep).catchSyncData(message.getNbt());
				ItemStack is = ep.getHeldItemMainhand();
				if(!is.isEmpty() && is.getItem() instanceof ItemWeaponUnsaga){
					is.getItem().onItemRightClick(ep.getEntityWorld(), ep, EnumHand.MAIN_HAND);
				}
			}
		}

		@Override
		public String getIdentifyName() {
			// TODO 自動生成されたメソッド・スタブ
			return SYNC_ID;
		}


	}

	public static void onLivingUpdate(LivingUpdateEvent e){
		if(ADAPTER.hasCapability(e.getEntityLiving())){
			IUnsagaAction instance = ADAPTER.getCapability(e.getEntityLiving());

			if(instance.getWeaponGuardProgress()>0){
				instance.decrWeaponGuardProgress();
			}
			if(instance.getWeaponGuardCooling()>0){
				instance.decrWeaponGuardCooling();
			}


		}
	}

	public static class Storage extends CapabilityStorage<IUnsagaAction>{

		@Override
		public void writeNBT(NBTTagCompound comp, Capability<IUnsagaAction> capability, IUnsagaAction instance,
				EnumFacing side) {
			// TODO 自動生成されたメソッド・スタブ
			comp.setInteger("sprint", instance.getSprintTimer());
		}

		@Override
		public void readNBT(NBTTagCompound comp, Capability<IUnsagaAction> capability, IUnsagaAction instance,
				EnumFacing side) {
			// TODO 自動生成されたメソッド・スタブ
			if(comp.hasKey("sprint")){
				instance.setSprintTimer(comp.getInteger("sprint"));
			}
		}

	}

	public static void registerEvents(){
		PacketSyncCapability.registerSyncCapability(SYNC_ID, CAPA);
		ADAPTER.registerAttachEvent();
	}

}
