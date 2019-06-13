package mods.hinasch.unsaga.core.item.misc.skillpanel;

import mods.hinasch.lib.capability.CapabilityAdapterFactory.CapabilityAdapterPlanImpl;
import mods.hinasch.lib.capability.CapabilityAdapterFrame;
import mods.hinasch.lib.capability.CapabilityStorage;
import mods.hinasch.lib.capability.ComponentCapabilityAdapters;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.skillpanel.ISkillPanel;
import mods.hinasch.unsaga.skillpanel.SkillPanelInitializer;
import mods.hinasch.unsaga.skillpanel.SkillPanels;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

public class SkillPanelCapability {

	@CapabilityInject(ISkillPanelContainer.class)
	public static Capability<ISkillPanelContainer> CAPA;

	public static CapabilityAdapterFrame<ISkillPanelContainer> adapterBase =
			UnsagaMod.CAPA_ADAPTER_FACTORY.create(
					new CapabilityAdapterPlanImpl(()->CAPA,()->ISkillPanelContainer.class,()->DefaultImpl.class,Storage::new));


	public static class DefaultImpl implements ISkillPanelContainer{

		boolean hasLocked = false;
		boolean hasJointed = false;
		ISkillPanel panel = SkillPanels.DUMMY;
		int lv = 0;
		@Override
		public ISkillPanel panel() {
			// TODO 自動生成されたメソッド・スタブ
			return panel;
		}

		@Override
		public int level() {
			// TODO 自動生成されたメソッド・スタブ
			return lv;
		}

		@Override
		public void setPanel(ISkillPanel panel) {
			this.panel = panel;

		}

		@Override
		public void setLevel(int level) {
			this.lv = level;

		}

		@Override
		public boolean hasJointed() {
			// TODO 自動生成されたメソッド・スタブ
			return this.hasJointed;
		}

		@Override
		public boolean hasLocked() {
			// TODO 自動生成されたメソッド・スタブ
			return this.hasLocked;
		}

		/** ジョイント済フラグを立てる。クライアントでの表示に使う*/
		@Override
		public void setJointed(boolean par1) {
			this.hasJointed = par1;

		}

		@Override
		public void setLocked(boolean par1) {
			// TODO 自動生成されたメソッド・スタブ

			this.hasLocked = par1;
		}

	}

	public static class Storage extends CapabilityStorage<ISkillPanelContainer>{

		@Override
		public void writeNBT(NBTTagCompound comp, Capability<ISkillPanelContainer> capability, ISkillPanelContainer instance,
				EnumFacing side) {
			comp.setString("id", instance.panel().getRegistryName().toString());
			comp.setInteger("level", instance.level());
			comp.setBoolean("lock", instance.hasLocked());
		}

		@Override
		public void readNBT(NBTTagCompound comp, Capability<ISkillPanelContainer> capability, ISkillPanelContainer instance,
				EnumFacing side) {

			UtilNBT.comp(comp)
			.setToField("id", (nbt,key)->
				instance.setPanel(SkillPanelInitializer.get(nbt.getString(key))))
			.setToField("level", (nbt,key)->instance.setLevel(nbt.getInteger(key)))
			.setToField("lock", (nbt,key)->instance.setLocked(nbt.getBoolean(key)));

		}

	}


	public static ComponentCapabilityAdapters.ItemStack<ISkillPanelContainer> adapter = adapterBase.createChildItem("unsagaAbilityAttachable");

	static{
		adapter.setPredicate(ev -> HSLibs.itemStackPredicate(ev, in -> in.getItem() instanceof ItemSkillPanel));
		adapter.setRequireSerialize(true);
	}

	public static void registerEvents(){
		adapter.registerAttachEvent();
	}

	public static ISkillPanel getPanel(ItemStack stack){
		return adapter.getCapability(stack).panel();
	}

	public static int getLevel(ItemStack is){
		return adapter.getCapability(is).level();
	}
}
