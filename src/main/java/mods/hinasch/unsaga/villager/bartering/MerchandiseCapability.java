package mods.hinasch.unsaga.villager.bartering;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;

import mods.hinasch.lib.capability.CapabilityAdapterFactory.CapabilityAdapterPlanImpl;
import mods.hinasch.lib.capability.CapabilityAdapterFrame;
import mods.hinasch.lib.capability.ComponentCapabilityAdapters;
import mods.hinasch.lib.capability.StorageDummy;
import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.AbilityAPI;
import mods.hinasch.unsaga.init.UnsagaLibrary;
import mods.hinasch.unsaga.material.UnsagaIngredients;
import mods.hinasch.unsaga.material.UnsagaMaterialCapability;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;

/**
 *
 * 店に売れるものとしてのキャパビリティ
 *
 */
public class MerchandiseCapability {

	@CapabilityInject(IMerchandise.class)
	public static Capability<IMerchandise> CAPA;
	public static final String SYNC_ID = "unsaga.merchandise";


	public static CapabilityAdapterFrame<IMerchandise> BUILDER = UnsagaMod.CAPA_ADAPTER_FACTORY.create(
			new CapabilityAdapterPlanImpl(()->CAPA,()->IMerchandise.class,()->DefaultImpl.class,StorageDummy::new));
	public static ComponentCapabilityAdapters.ItemStack<IMerchandise> ADAPTER = BUILDER.createChildItem(SYNC_ID);

	static{
		ADAPTER.setPredicate(ev -> HSLibs.itemStackPredicate(ev, stack -> stack.getItem() instanceof Item));
//		adapter.setRequireSerialize(true);
	}

	public static class DefaultImpl implements IMerchandise{

		boolean isMerchandise = false;
		OptionalInt price = OptionalInt.empty();

		@Override
		public int getPrice(ItemStack stack) {
			if(!this.price.isPresent()){
				UnsagaMaterialCapability.adapter.getCapabilityOptional(stack)
				.ifPresent(in ->{
					int base = in.getMaterial().price();
					this.setPrice((this.getBasePrice(base) + this.applyDamageToPrice(base, stack)));
				});

				UnsagaIngredients.find(stack)
				.ifPresent(in ->{
					int price = in.first().price();
					double amount = in.second();
					int base = (int)((double)price * amount);
					this.setPrice((this.getBasePrice(base) + this.applyDamageToPrice(base, stack)));
				});

				UnsagaLibrary.CATALOG_MATERIAL.find(stack).ifPresent(in ->{
					int base = (int)((float)in.getMaterial().price() * in.getAmount());
					this.setPrice(this.getBasePrice(base) + this.applyDamageToPrice(base, stack));
				});


			}
//			UnsagaMod.logger.trace(stack.getDisplayName(), this.price.getAsInt());
			return this.price.orElse(0);
		}

		public void setPrice(int price){

			this.price = OptionalInt.of(this.price.orElse(price));
//			if(!this.price.isPresent()){
//				this.price = OptionalInt.of(price);
//			}
		}
		public int getBasePrice(int base){
			return (int)((float)base * BarteringUtil.BASE_PRICE);
		}
		public int applyDamageToPrice(int base,ItemStack is){
			int additional = AbilityAPI.getAttachedAbilities(is).size()>0 ? (int)(base * (0.05F + 0.15F*(float)AbilityAPI.getAttachedAbilities(is).size())) : 0;

			return Optional.of(is)
			.filter(in -> in.isItemStackDamageable())
			.map(in ->{
				int durability = ItemUtil.getDurability(is);
				float per = (float)durability / (float)is.getMaxDamage();
				return (int)((float)base * per) + additional;
			}).orElse((int)((float)base * BarteringUtil.RAW_MATERIAL_PRICE) + additional);
		}
		@Override
		public boolean isMerchadise() {
			// TODO 自動生成されたメソッド・スタブ
			return this.isMerchandise;
		}

		@Override
		public void setMerchandise(boolean par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.isMerchandise = par1;
		}

		@Override
		public void setPrice(ItemStack stack) {
			if(!this.price.isPresent()){
				if(UnsagaMaterialCapability.adapter.hasCapability(stack)){
					int base = UnsagaMaterialCapability.adapter.getCapability(stack).getMaterial().price();
					this.price = OptionalInt.of((int)((float)base * 0.2F));
				}
			}

		}

		@Override
		public boolean canSell(ItemStack stack) {

			List<Predicate<ItemStack>> list = new ArrayList<>();

			list.add(in -> UnsagaMaterialCapability.adapter.hasCapability(in));
			list.add(in -> UnsagaIngredients.find(in).isPresent());
			list.add(in -> UnsagaLibrary.CATALOG_MATERIAL.find(in).isPresent());
			list.add(in -> UnsagaLibrary.CATALOG_PRICE.find(stack).isPresent());

			return list.stream().anyMatch(in -> in.test(stack));
		}



	}


	public static void registerAttachEvents(){
		ADAPTER.registerAttachEvent((inst,capa,face,ev)->{
//			if(!inst.getPrice().isPresent()){
//				inst.setPrice(stack);
//			}
		});
	}
}
