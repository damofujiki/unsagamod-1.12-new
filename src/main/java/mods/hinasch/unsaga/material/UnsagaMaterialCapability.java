package mods.hinasch.unsaga.material;

import mods.hinasch.lib.capability.CapabilityAdapterFactory.ICapabilityAdapterPlan;
import mods.hinasch.lib.capability.CapabilityAdapterFrame;
import mods.hinasch.lib.capability.CapabilityStorage;
import mods.hinasch.lib.capability.ComponentCapabilityAdapters;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.lib.registry.RegistryUtil;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.common.UnsagaWeight;
import mods.hinasch.unsaga.common.UnsagaWeightType;
import mods.hinasch.unsaga.init.UnsagaRegistries;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UnsagaMaterialCapability {


	@CapabilityInject(IUnsagaMaterialTool.class)
	public static Capability<IUnsagaMaterialTool> CAPA;
	public static final String SYNC_ID = "unsaga_forgeable_tool";

	public static ICapabilityAdapterPlan<IUnsagaMaterialTool> blueprint = new ICapabilityAdapterPlan(){

		@Override
		public Capability getCapability() {
			// TODO 自動生成されたメソッド・スタブ
			return CAPA;
		}

		@Override
		public Class getCapabilityClass() {
			// TODO 自動生成されたメソッド・スタブ
			return IUnsagaMaterialTool.class;
		}

		@Override
		public Class getDefault() {
			// TODO 自動生成されたメソッド・スタブ
			return DefaultImpl.class;
		}

		@Override
		public IStorage getStorage() {
			// TODO 自動生成されたメソッド・スタブ
			return new Storage();
		}

	};

	public static CapabilityAdapterFrame<IUnsagaMaterialTool> base = UnsagaMod.CAPA_ADAPTER_FACTORY.create(blueprint);
	public static ComponentCapabilityAdapters.ItemStack<IUnsagaMaterialTool> adapter = base.createChildItem(SYNC_ID);

	static{
		adapter.setPredicate(ev -> {
//			UnsagaMod.logger.trace("capa", ev.getItemStack().getItem());
			return HSLibs.itemStackPredicate(ev, in -> in.getItem() instanceof IUnsagaCategoryTool);
		});
		adapter.setRequireSerialize(true);
	}
	public static class DefaultImpl implements IUnsagaMaterialTool{



		boolean hasInitialized = false;
		UnsagaWeight weight = new UnsagaWeight(0);
		UnsagaMaterial material = UnsagaMaterials.DUMMY;
		@Override
		public boolean hasInitialized() {
			// TODO 自動生成されたメソッド・スタブ
			return this.hasInitialized;
		}

		@Override
		public void setInitialized(boolean par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.hasInitialized = par1;
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
			// TODO 自動生成されたメソッド・スタブ

		}

		@Override
		public String getIdentifyName() {
			// TODO 自動生成されたメソッド・スタブ
			return SYNC_ID;
		}

		@Override
		public void setWeight(int weight) {
			// TODO 自動生成されたメソッド・スタブ
			this.weight = new UnsagaWeight(weight);
		}



		@Override
		public UnsagaMaterial getMaterial() {
			// TODO 自動生成されたメソッド・スタブ
			return this.material;
		}

		@Override
		public void setMaterial(UnsagaMaterial m) {
			// TODO 自動生成されたメソッド・スタブ
			this.material = m;
		}

		@Override
		public UnsagaWeightType getToolWeightType() {
			// TODO 自動生成されたメソッド・スタブ
			return this.getWeight().type();
		}

		@Override
		public UnsagaWeight getWeight() {
			// TODO 自動生成されたメソッド・スタブ
			return this.weight;
		}





	}

	public static class Storage extends CapabilityStorage<IUnsagaMaterialTool>{

		@Override
		public void writeNBT(NBTTagCompound comp, Capability<IUnsagaMaterialTool> capability,
				IUnsagaMaterialTool instance, EnumFacing side) {
			comp.setString("material", instance.getMaterial().getRegistryName().toString());
			comp.setInteger("weight", instance.getWeight().getValue());
			comp.setBoolean("initialized", instance.hasInitialized());

		}

		@Override
		public void readNBT(NBTTagCompound comp, Capability<IUnsagaMaterialTool> capability,
				IUnsagaMaterialTool instance, EnumFacing side) {

			UtilNBT.comp(comp)
			.setToField("material", (in,nbtkey)->{
				UnsagaMaterial m = RegistryUtil.getValue(UnsagaRegistries.material(), UnsagaMod.MODID, in.getString(nbtkey));
				instance.setMaterial(m);
			})
			.setToField("weight", (in,key)->instance.setWeight(comp.getInteger(key)))
			.setToField("initialized", (in,key)->instance.setInitialized(comp.getBoolean(key)));

		}

	}

	public static UnsagaMaterial getMaterial(ItemStack is){
		return UnsagaMaterialCapability.adapter.hasCapability(is) ? UnsagaMaterialCapability.adapter.getCapability(is).getMaterial() : UnsagaMaterials.DUMMY;
	}
	public static void register(){



		adapter.registerAttachEvent((inst,capa,facing,ev)->{
//			UnsagaMod.logger.trace("capa", "attach");
			if(ev.getObject() instanceof IUnsagaCategoryTool){
				if(!inst.hasInitialized()){
					inst.setMaterial(UnsagaMaterials.DUMMY);
					inst.setWeight(1);
					inst.setInitialized(true);
				}

			}
		});
	}
}
