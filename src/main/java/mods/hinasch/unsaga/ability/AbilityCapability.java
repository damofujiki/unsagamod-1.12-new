package mods.hinasch.unsaga.ability;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import mods.hinasch.lib.capability.CapabilityAdapterFactory.CapabilityAdapterPlanImpl;
import mods.hinasch.lib.capability.CapabilityAdapterFrame;
import mods.hinasch.lib.capability.CapabilityStorage;
import mods.hinasch.lib.capability.ComponentCapabilityAdapters;
import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.slot.AbilitySlotList;
import mods.hinasch.unsaga.ability.slot.AbilitySlotType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class AbilityCapability {


	@CapabilityInject(IAbilityAttachable.class)
	public static Capability<IAbilityAttachable> CAPA;
	public static final String SYNC_ID = "unsaga.ability_attachable";

	public static CapabilityAdapterFrame<IAbilityAttachable> adapterBase = UnsagaMod.CAPA_ADAPTER_FACTORY.create(
			new CapabilityAdapterPlanImpl(()->CAPA,()->IAbilityAttachable.class,()->DefaultImpl.class,Storage::new));
	public static ComponentCapabilityAdapters.ItemStack<IAbilityAttachable> adapter = adapterBase.createChildItem(SYNC_ID);

	static{

		adapter.setPredicate(ev -> HSLibs.itemStackPredicate(ev, in -> in.getItem() instanceof IAbilitySelector));
		adapter.setRequireSerialize(true);
	}

	public static class DefaultImpl implements IAbilityAttachable{

		int selectedAbility = 0;
		int maxAbilitySize = 1;
		AbilitySlotList abilityList = AbilitySlotList.builder().slot(1,AbilitySlotType.NO_FUNCTION).build();
		NonNullList<IAbility> uniqueAbility = NonNullList.create();
		boolean hasInitialized = false;


		@Override
		public boolean hasInitialized() {
			// TODO 自動生成されたメソッド・スタブ
			return hasInitialized;
		}

		@Override
		public void setInitialized(boolean par1) {
			this.hasInitialized = par1;
		}

		@Override
		public NBTTagCompound getSendingData() {
			return (NBTTagCompound) CAPA.writeNBT(this, null);
		}

		@Override
		public void catchSyncData(NBTTagCompound nbt) {
			CAPA.readNBT(this, null, nbt);

		}

		@Override
		public void onPacket(PacketSyncCapability message, MessageContext ctx) {
			// TODO 自動生成されたメソッド・スタブ
			if(ctx.side.isClient()){
				EntityPlayer ep = ClientHelper.getPlayer();
				Consumer<PacketSyncCapability> syncWeapon = nbt ->{
					ItemStack held = ep.getHeldItemMainhand();
					if(!held.isEmpty() && adapter.hasCapability(held)){
						adapter.getCapability(held).catchSyncData(nbt.getNbt());
					}
				};
				Consumer<PacketSyncCapability> syncSlot = args ->{
					String id = args.getArgs().getString("slot");
					AbilityAPI.EquipmentSlot slot = AbilityAPI.EquipmentSlot.fromName(id);
					ItemStack stack = slot.getStackFrom(ep);
					adapter.getCapabilityOptional(stack).ifPresent(in -> in.catchSyncData(args.getArgs()));
				};
				Optional.ofNullable(message.getArgs())
				.map(in -> syncSlot) //argsがあればスロットをシンク
				.orElse(syncWeapon)
				.accept(message);


			}else{
				EntityPlayer ep = ctx.getServerHandler().player;
				if(!ep.getHeldItemMainhand().isEmpty()){
					adapter.getCapabilityOptional(ep.getHeldItemMainhand())
					.ifPresent(in -> in.catchSyncData(message.getNbt()));
				}
			}
		}

		@Override
		public String getIdentifyName() {
			// TODO 自動生成されたメソッド・スタブ
			return SYNC_ID;
		}

		@Override
		public boolean isUniqueItem() {
			// TODO 自動生成されたメソッド・スタブ
			return !this.uniqueAbility.isEmpty();
		}


		@Override
		public void setLearnableUniqueAbilities(NonNullList<IAbility> in) {
			// TODO 自動生成されたメソッド・スタブ
			this.uniqueAbility =  in;
		}

		@Override
		public NonNullList<IAbility> getLearnableUniqueAbilities() {
			// TODO 自動生成されたメソッド・スタブ
			return this.uniqueAbility;
		}

		@Override
		public AbilitySlotList getAbilitySlots() {
			// TODO 自動生成されたメソッド・スタブ
			return this.abilityList;
		}

		@Override
		public void setAbilityList(AbilitySlotList list) {
			// TODO 自動生成されたメソッド・スタブ
			this.abilityList = list;
		}

		@Override
		public int getSelectedIndex() {
			// TODO 自動生成されたメソッド・スタブ
			return this.selectedAbility;
		}

		@Override
		public void setSelectedIndex(int index) {
			// TODO 自動生成されたメソッド・スタブ
			this.selectedAbility = index;
		}

	}

	public static class Storage extends CapabilityStorage<IAbilityAttachable>{

		@Override
		public void writeNBT(NBTTagCompound comp, Capability<IAbilityAttachable> capability,
				IAbilityAttachable instance, EnumFacing side) {
			comp.setBoolean("initialized", instance.hasInitialized());
//			comp.setByte("size", (byte)instance.getMaxAbilitySize());
//			UtilNBT.writeListToNBT(instance.getLearnedAbilities(), comp, "abilities");
			instance.getAbilitySlots().writeToNBT(comp);
			if(instance.isUniqueItem()){
				UtilNBT.writeListToNBT(instance.getLearnableUniqueAbilities(), comp, "egoAbilities");
			}
		}

		@Override
		public void readNBT(NBTTagCompound comp, Capability<IAbilityAttachable> capability, IAbilityAttachable instance,
				EnumFacing side) {
			if(comp.hasKey("initialized")){
				instance.setInitialized(comp.getBoolean("initialized"));
			}
//			if(comp.hasKey("size")){
//				instance.setMaxAbilitySize((int)comp.getByte("size"));
//			}
			if(comp.hasKey("abilities")){
//				List<IAbility> list = UtilNBT.readListFromNBT(comp, "abilities", Ability.FUNC_RESTORE);
//				instance.setAbilities(ItemUtil.toNonNull(list,AbilityRegistry.EMPTY));
				instance.setAbilityList(new AbilitySlotList(comp));
			}
			if(comp.hasKey("egoAbilities")){
				List<IAbility> list = UtilNBT.readListFromNBT(comp, "egoAbilities", Ability.FUNC_RESTORE);
				instance.setLearnableUniqueAbilities(ItemUtil.toNonNull(list, Abilities.EMPTY));
			}

		}

	}

	public static void registerEvents(){
		PacketSyncCapability.registerSyncCapability(AbilityCapability.SYNC_ID, AbilityCapability.CAPA);
		adapter.registerAttachEvent((inst,capa,face,ev)->{
			ItemStack obj = (ItemStack) ev.getObject();
			if(obj.getItem() instanceof IAbilitySelector && !inst.hasInitialized()){
				IAbilitySelector intf = (IAbilitySelector) obj.getItem();
				inst.setAbilityList(intf.createAbilityList());
				inst.setInitialized(true);
			}
		});
	}
}
