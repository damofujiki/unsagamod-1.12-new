package mods.hinasch.unsaga.villager.village;

import mods.hinasch.lib.capability.CapabilityAdapterFactory.CapabilityAdapterPlanImpl;
import mods.hinasch.lib.capability.CapabilityAdapterFrame;
import mods.hinasch.lib.capability.CapabilityStorage;
import mods.hinasch.lib.capability.ComponentCapabilityAdapters;
import mods.hinasch.lib.core.HSLib;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.village.Village;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class VillageCapabilityUnsaga {
	public static String SYNC_ID = "unsaga_village";

	@CapabilityInject(IUnsagaVillage.class)
	public static Capability<IUnsagaVillage> CAPA;

	public static final CapabilityAdapterFrame BUILDER = HSLib.capabilityFactory.create(new CapabilityAdapterPlanImpl(()->CAPA,()->IUnsagaVillage.class,()->DefaultImpl.class,Storage::new));
	public static final ComponentCapabilityAdapters.Village<IUnsagaVillage> ADAPTER = BUILDER.ofVillage(SYNC_ID);

	static{
		ADAPTER.setRequireSerialize(true);
		ADAPTER.setPredicate(input -> input.getObject() instanceof Village);
	}
	public static class DefaultImpl implements IUnsagaVillage{

		int distributionLevel = 0;
		@Override
		public int getDistributionPoint() {
			// TODO 自動生成されたメソッド・スタブ
			return this.distributionLevel;
		}

		@Override
		public void setDistributionPoint(int par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.distributionLevel = par1;
		}

	}

	public static class Storage extends CapabilityStorage<IUnsagaVillage>{

		@Override
		public void writeNBT(NBTTagCompound comp, Capability<IUnsagaVillage> capability, IUnsagaVillage instance,
				EnumFacing side) {
			// TODO 自動生成されたメソッド・スタブ
			comp.setInteger("distribution", instance.getDistributionPoint());
		}

		@Override
		public void readNBT(NBTTagCompound comp, Capability<IUnsagaVillage> capability, IUnsagaVillage instance,
				EnumFacing side) {
			// TODO 自動生成されたメソッド・スタブ
			if(comp.hasKey("distribution")){
				instance.setDistributionPoint(comp.getInteger("distribution"));
			}
		}

	}
}
