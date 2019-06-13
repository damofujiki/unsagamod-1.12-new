package mods.hinasch.unsaga.villager;

import mods.hinasch.lib.capability.CapabilityAdapterFactory.CapabilityAdapterPlanImpl;
import mods.hinasch.lib.capability.CapabilityAdapterFrame;
import mods.hinasch.lib.capability.CapabilityStorage;
import mods.hinasch.lib.capability.ComponentCapabilityAdapters;
import mods.hinasch.unsaga.UnsagaMod;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class VillagerCapabilityUnsaga {

	@CapabilityInject(IUnsagaVillager.class)
	public static Capability<IUnsagaVillager> CAPA;
	public static final String SYNC_ID = "unsaga.villager";

	public static CapabilityAdapterFrame<IUnsagaVillager> BUILDER = UnsagaMod.CAPA_ADAPTER_FACTORY.create(
			new CapabilityAdapterPlanImpl(()->CAPA,()->IUnsagaVillager.class,()->DefaultImpl.class,Storage::new));
	public static ComponentCapabilityAdapters.Entity<IUnsagaVillager> ADAPTER = BUILDER.createChildEntity(SYNC_ID);

	static{
		ADAPTER.setPredicate(ev -> ev.getObject() instanceof EntityVillager);
		ADAPTER.setRequireSerialize(true);
	}

	public static class DefaultImpl implements IUnsagaVillager{

		boolean init = false;

		IVillagerImplimentation implimentaion = new IVillagerImplimentation.Empty();
		VillagerTypeUnsaga villagerType = VillagerTypeUnsaga.UNKNOWN;
		int carrierID = -1;


		@Override
		public boolean hasInitialized() {
			// TODO 自動生成されたメソッド・スタブ
			return init;
		}

		@Override
		public void setInitialized(boolean par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.init = par1;
		}

		@Override
		public int getCarrierID() {
			// TODO 自動生成されたメソッド・スタブ
			return this.carrierID;
		}

		@Override
		public void setCarrierID(int par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.carrierID = par1;
		}

		@Override
		public VillagerTypeUnsaga getVillagerType() {
			// TODO 自動生成されたメソッド・スタブ
			return this.villagerType;
		}

		@Override
		public void setVillagerType(VillagerTypeUnsaga type) {
			// TODO 自動生成されたメソッド・スタブ
			this.villagerType = type;
		}


		@Override
		public IVillagerImplimentation implimentation() {
			// TODO 自動生成されたメソッド・スタブ
			return this.implimentaion;
		}

		@Override
		public void setImplimentation(IVillagerImplimentation impl) {
			// TODO 自動生成されたメソッド・スタブ
			this.implimentaion = impl;
		}


	}

	public static class Storage extends CapabilityStorage<IUnsagaVillager>{

		@Override
		public void writeNBT(NBTTagCompound comp, Capability<IUnsagaVillager> capability, IUnsagaVillager instance,
				EnumFacing side) {
			comp.setByte("villagerType", (byte)instance.getVillagerType().getMeta());
			instance.implimentation().writeNBT(comp, capability, instance, side);
			switch(instance.getVillagerType()){
			case BARTERING:
//				NBTTagList tag1 = UtilNBT.tagList();
//				UtilNBT.writeItemStacksToNBTTag(tag1, instance.getMerchandises());
////				instance.getMerchandises().writeToNBT(tag1);
//				comp.setTag("merchandises", tag1);
//				NBTTagList tag2 = UtilNBT.tagList();
//				UtilNBT.writeItemStacksToNBTTag(tag2, instance.getSecretMerchandises());
////				instance.getSecretMerchandises().writeToNBT(tag2);
//				comp.setTag("secrets", tag2);
//				comp.setInteger("distributionLevel", instance.getDistributionLevel());
//				comp.setLong("purchaseTime", instance.getRecentStockedTime());
//				comp.setInteger("transactionPoint", instance.getTransactionPoint());
//				comp.setByte("shopType", (byte)instance.getShopType().getMeta());
//				comp.setInteger("baseShopLevel", instance.getBaseShopLevel());
//				comp.setBoolean("displayedSercrets", instance.hasDisplayedSecretMerchandises());
				break;
			case CARRIER:
				comp.setInteger("carrierID", instance.getCarrierID());
				break;
			case SMITH:
//				comp.setByte("smithType", (byte)instance.getBlackSmithType().getMeta());
				break;
			case UNKNOWN:
				break;
			default:
				break;

			}

			comp.setBoolean("init", instance.hasInitialized());
		}

		@Override
		public void readNBT(NBTTagCompound comp, Capability<IUnsagaVillager> capability, IUnsagaVillager instance,
				EnumFacing side) {
			if(comp.hasKey("villagerType")){
				instance.setVillagerType(VillagerTypeUnsaga.fromMeta((int)comp.getByte("villagerType")));
			}
			instance.setImplimentation(instance.getVillagerType().createImpl());
			instance.implimentation().readNBT(comp, capability, instance, side);
			switch(instance.getVillagerType()){
			case BARTERING:
//				if(comp.hasKey("merchandises")){
//					NBTTagList tag = UtilNBT.getTagList(comp, "merchandises");
//					instance.setMerchandises(UtilNBT.getItemStacksFromNBT(tag, 9));
//				}
//				if(comp.hasKey("secrets")){
//					NBTTagList tag2 = UtilNBT.getTagList(comp, "secrets");
//					instance.setSecretMerchandises(UtilNBT.getItemStacksFromNBT(tag2, 9));
//				}
//				if(comp.hasKey("purchaseTime")){
//					instance.setStockedTime(comp.getLong("purchaseTime"));
//				}
//				if(comp.hasKey("distributionLevel")){
//					instance.setDistributionLevel(comp.getInteger("distributionLevel"));
//				}
//				if(comp.hasKey("transactionPoint")){
//					instance.setTransactionPoint(comp.getInteger("transactionPoint"));
//				}
//				if(comp.hasKey("shopType")){
//					instance.setBarteringShopType(BarteringShopType.fromMeta((int)comp.getByte("shopType")));
//				}
//				if(comp.hasKey("baseShopLevel")){
//					instance.setBaseShopLevel(comp.getInteger("baseShopLevel"));
//				}
//				if(comp.hasKey("displayedSercrets")){
//					instance.setHasDisplayedSecrets(comp.getBoolean("displayedSercrets"));
//				}
				break;
			case CARRIER:
				if(comp.hasKey("carrierID")){
					instance.setCarrierID(comp.getInteger("carrierID"));
				}
				break;
			case SMITH:
//				if(comp.hasKey("smithType")){
//					instance.setBlackSmithType(BlackSmithType.fromMeta((int)comp.getByte("smithType")));
//				}
				break;
			case UNKNOWN:
				break;
			default:
				break;

			}

			if(comp.hasKey("init")){
				instance.setInitialized(comp.getBoolean("init"));
			}
		}

	}

	public static void registerEvents(){
		ADAPTER.registerAttachEvent((inst,capa,face,ev)->{
			if(ev.getObject() instanceof EntityVillager){
				EntityVillager villager = (EntityVillager) ev.getObject();
				if(!inst.hasInitialized()){
					if(villager.getProfessionForge()==VillagerProfessionUnsaga.MERCHANT){
						inst.setVillagerType(VillagerTypeUnsaga.BARTERING);
					}
					if(villager.getProfessionForge()==VillagerProfessionUnsaga.MAGIC_MERCHANT){
						inst.setVillagerType(VillagerTypeUnsaga.BARTERING);
					}
					if(villager.getProfessionForge()==VillagerProfessionUnsaga.BLACKSMITH){
						inst.setVillagerType(VillagerTypeUnsaga.SMITH);
					}

					inst.setImplimentation(inst.getVillagerType().createImpl());
					inst.implimentation().init(villager,capa, face, ev);


//					if(inst.getVillagerType()==UnsagaVillagerType.BARTERING && WorldHelper.isServer(villager.getEntityWorld())){
//
//						BarteringShopType type = BarteringShopType.decideBarteringType(villager.getEntityWorld(), villager);
//						inst.setBarteringShopType(type);
//						inst.setBaseShopLevel(villager.getRNG().nextInt(9)+1);
//						inst.setInitialized(true);
//
//
//					}
//					if(inst.getVillagerType()==UnsagaVillagerType.SMITH && WorldHelper.isServer(villager.getEntityWorld())){
//
//						int r = villager.getEntityWorld().rand.nextInt(2)+1;
//						BlackSmithType type = BlackSmithType.fromMeta(r);
//						inst.setBlackSmithType(type);
//						inst.setInitialized(true);
//
//					}

					inst.setInitialized(true);
				}

			}
		});
	}
}
