package mods.hinasch.unsaga.villager.bartering;

import java.util.Random;

import mods.hinasch.unsaga.ability.AbilityCapability;
import mods.hinasch.unsaga.ability.Abilities;
import mods.hinasch.unsaga.ability.AbilitySpell;
import mods.hinasch.unsaga.ability.IAbility;
import mods.hinasch.unsaga.common.item.UnsagaItemFactory;
import mods.hinasch.unsaga.material.UnsagaMaterialCapability;
import net.minecraft.item.ItemStack;

public class MerchandiseFactory extends UnsagaItemFactory{

	public MerchandiseFactory(Random rand) {
		super(rand);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	//	public NonNullList<ItemStack> createMerchandises(int amount,int generateLevel,Collection<ToolCategory> availables,Set<UnsagaMaterial> strictMaterials){
	//		NonNullList<ItemStack> stacks = super.createMerchandises(amount, generateLevel, availables, strictMaterials);
	//		stacks.forEach(in ->{
	//
	//			//売品認識タグをつける
	//			if(MerchandiseCapability.ADAPTER.hasCapability(in)){
	//				MerchandiseCapability.ADAPTER.getCapability(in).setMerchandise(true);
	//			}
	//
	//		});
	//
	//		return stacks;
	//	}

	@Override
	protected void postOrderedItem(ItemStack ordered){
		super.postOrderedItem(ordered);
		//売品認識タグをつける
		MerchandiseCapability.ADAPTER.getCapabilityOptional(ordered).ifPresent(in -> in.setMerchandise(true));

	}

	public static class Magic extends MerchandiseFactory{

		public Magic(Random rand) {
			super(rand);
			// TODO 自動生成されたコンストラクター・スタブ
		}


		@Override
		protected void postOrderedItem(ItemStack ordered){
			super.postOrderedItem(ordered);
			//魔法アイテム屋なら術アビリティを付与
			IAbility spellAbility = UnsagaMaterialCapability.adapter.getCapabilityOptional(ordered)
					.map(in -> AbilitySpell.getAbilityFromMaterial(in.getMaterial())).orElse(Abilities.EMPTY);
			if(!spellAbility.isAbilityEmpty()){
				AbilityCapability.adapter.getCapabilityOptional(ordered).ifPresent(in ->in.getAbilitySlots().updateSlot(1, spellAbility));
			}
		}
	}
}
