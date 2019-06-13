package mods.hinasch.unsaga.common;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import mods.hinasch.unsaga.common.item.ComponentUnsagaWeapon;
import mods.hinasch.unsaga.material.UnsagaMaterialCapability;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemArmor;

public enum UnsagaWeightType {




	HEAVY,LIGHT;

	public static final int THRESHOLD_ARMOR = 10;
	public static int calcArmorWeight(@Nullable EntityLivingBase ep){
		if(ep==null){
			return 0;
		}

		int weight = Lists.newArrayList(ep.getArmorInventoryList()).stream().mapToInt(in ->{
			if(in.isEmpty()){
				return 0;
			}
			if(UnsagaMaterialCapability.adapter.hasCapability(in)){
				return UnsagaMaterialCapability.adapter.getCapability(in).getMaterial().weight().getValue();
			}
			if(in.getItem() instanceof ItemArmor){
				ItemArmor armor = (ItemArmor) in.getItem();
				switch(armor.getArmorMaterial()){
				case CHAIN:
					return UnsagaMaterials.SILVER.weight().getValue();
				case DIAMOND:
					return UnsagaMaterials.DIAMOND.weight().getValue();
				case GOLD:
					return UnsagaMaterials.GOLD.weight().getValue();
				case IRON:
					return UnsagaMaterials.IRON.weight().getValue();
				case LEATHER:
					return UnsagaMaterials.CROCODILE_LEATHER.weight().getValue();
				default:
					break;

				}
			}
			return UnsagaMaterials.SILVER.weight().getValue();
		}).sum();

		return weight;
	}

	public static UnsagaWeightType calcArmorWeightType(EntityLivingBase ep){
		if(calcArmorWeight(ep)>THRESHOLD_ARMOR){
			return HEAVY;
		}
		return LIGHT;
	}

	public static UnsagaWeightType fromString(String string){
		if(string.equals("heavy")){
			return HEAVY;
		}
		return LIGHT;
	}

	public static UnsagaWeightType fromWeight(int weight){
		if(ComponentUnsagaWeapon.THRESHOLD_WEIGHT<weight){
			return HEAVY;
		}
		return LIGHT;
	}

	public String getName(){
		return this.isHeavy() ? "Heavy" : "Light";
	}

	public String getRegitryName(){
		return this.getName().toLowerCase();
	}
	public boolean isHeavy(){
		return this == HEAVY;
	}
}
