package mods.hinasch.unsaga.common.specialaction.option;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;

/**
 * アーマーブレスに使う
 */
public class ActionOptionSlotSelector extends ActionOption{

	public ActionOptionSlotSelector(String name) {
		super(name);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public EntityEquipmentSlot getEquipmentSlot(){
		if(this==ActionOptions.OFF_HAND){
			return EntityEquipmentSlot.OFFHAND;
		}
		if(this==ActionOptions.HEAD){
			return EntityEquipmentSlot.HEAD;
		}
		if(this==ActionOptions.BODY){
			return EntityEquipmentSlot.CHEST;
		}
		if(this==ActionOptions.LEGS){
			return EntityEquipmentSlot.LEGS;
		}
		if(this==ActionOptions.FEET){
			return EntityEquipmentSlot.FEET;
		}
		return EntityEquipmentSlot.OFFHAND;
	}
	public ItemStack getStackFromLiving(EntityLivingBase living){
		return living.getItemStackFromSlot(getEquipmentSlot());
	}
}
