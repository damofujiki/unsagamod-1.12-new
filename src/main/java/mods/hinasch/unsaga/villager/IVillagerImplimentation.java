package mods.hinasch.unsaga.villager;

import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;

/** 村人のCapabilityの実装部分を分けたもの*/
public interface IVillagerImplimentation {

	public void init(EntityVillager villager, Capability<IUnsagaVillager> capa, EnumFacing face, AttachCapabilitiesEvent ev);

	public void readNBT(NBTTagCompound comp, Capability<IUnsagaVillager> capability, IUnsagaVillager instance,
			EnumFacing side);

	public void writeNBT(NBTTagCompound comp, Capability<IUnsagaVillager> capability, IUnsagaVillager instance,
			EnumFacing side) ;

	/** 初期状態*/
	public static class Empty implements IVillagerImplimentation{

		@Override
		public void readNBT(NBTTagCompound comp, Capability<IUnsagaVillager> capability, IUnsagaVillager instance,
				EnumFacing side) {
			// TODO 自動生成されたメソッド・スタブ

		}

		@Override
		public void writeNBT(NBTTagCompound comp, Capability<IUnsagaVillager> capability, IUnsagaVillager instance,
				EnumFacing side) {
			// TODO 自動生成されたメソッド・スタブ

		}


		@Override
		public void init(EntityVillager villager, Capability<IUnsagaVillager> capa, EnumFacing face,
				AttachCapabilitiesEvent ev) {
			// TODO 自動生成されたメソッド・スタブ

		}





	}
}
