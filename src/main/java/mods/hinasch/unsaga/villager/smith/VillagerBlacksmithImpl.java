package mods.hinasch.unsaga.villager.smith;

import mods.hinasch.unsaga.villager.IUnsagaVillager;
import mods.hinasch.unsaga.villager.IVillagerImplimentation;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;

/**
 *
 * 村人の権能の実装：改造屋
 *
 */
public class VillagerBlacksmithImpl implements IVillagerImplimentation{

	BlacksmithType type = BlacksmithType.NONE;


	public BlacksmithType getBlackSmithType() {
		// TODO 自動生成されたメソッド・スタブ
		return type;
	}


	public void setBlackSmithType(BlacksmithType type) {
		// TODO 自動生成されたメソッド・スタブ
		this.type = type;
	}

	@Override
	public void init(EntityVillager villager, Capability<IUnsagaVillager> capa, EnumFacing face,
			AttachCapabilitiesEvent ev) {
		int r = villager.getEntityWorld().rand.nextInt(2)+1;
		BlacksmithType type = BlacksmithType.fromMeta(r);
		this.setBlackSmithType(type);
	}

	@Override
	public void readNBT(NBTTagCompound comp, Capability<IUnsagaVillager> capability, IUnsagaVillager instance,
			EnumFacing side) {
		if(comp.hasKey("smithType")){
			this.setBlackSmithType(BlacksmithType.fromMeta((int)comp.getByte("smithType")));
		}
	}

	@Override
	public void writeNBT(NBTTagCompound comp, Capability<IUnsagaVillager> capability, IUnsagaVillager instance,
			EnumFacing side) {
		comp.setByte("smithType", (byte)this.getBlackSmithType().getMeta());

	}

}
