package mods.hinasch.unsaga.init;

import mods.hinasch.unsaga.ability.AbilityCapability;
import mods.hinasch.unsaga.chest.ChestCapability;
import mods.hinasch.unsaga.core.advancement.UnsagaUnlockableContentCapability;
import mods.hinasch.unsaga.core.entity.UnsagaActionCapability;
import mods.hinasch.unsaga.core.entity.projectile.custom_arrow.CustomArrowCapability;
import mods.hinasch.unsaga.core.inventory.AccessorySlotCapability;
import mods.hinasch.unsaga.core.item.misc.skillpanel.SkillPanelCapability;
import mods.hinasch.unsaga.core.item.weapon.ItemGunUnsaga;
import mods.hinasch.unsaga.core.world.UnsagaWorldCapability;
import mods.hinasch.unsaga.core.world.chunk.UnsagaChunkCapability;
import mods.hinasch.unsaga.lp.LifePoint;
import mods.hinasch.unsaga.material.UnsagaMaterialCapability;
import mods.hinasch.unsaga.minsaga.MinsagaForgingCapability;
import mods.hinasch.unsaga.status.TargetHolderCapability;
import mods.hinasch.unsaga.status.UnsagaXPCapability;
import mods.hinasch.unsaga.villager.VillagerInitializerUnsaga;
import mods.hinasch.unsaga.villager.village.VillageCapabilityUnsaga;

public class UnsagaCapabilityRegisterer {

	public static void registerBases(){
		LifePoint.BUILDER.registerCapability();
		UnsagaMaterialCapability.base.registerCapability();
		AbilityCapability.adapterBase.registerCapability();
		AccessorySlotCapability.adapterBase.registerCapability();
//		EquipmentCacheCapability.adapterBase.registerCapability();
//		EntityStateCapability.adapterBase.registerCapability();
		SkillPanelCapability.adapterBase.registerCapability();
		UnsagaXPCapability.base.registerCapability();
		TargetHolderCapability.adapterBase.registerCapability();
		ChestCapability.BUILDER.registerCapability();
		VillagerInitializerUnsaga.instance().registerCapabilities();
		ItemGunUnsaga.adapterBase.registerCapability();
		MinsagaForgingCapability.BUILDER.registerCapability();
		UnsagaWorldCapability.BUILDER.registerCapability();
		UnsagaUnlockableContentCapability.builder.registerCapability();
//		ExtendedPotionCapability.adapterBase.registerCapability();
//		StateCapability.BUILDER.registerCapability();
		UnsagaActionCapability.BUILDER.registerCapability();
//		AdditionalDamageAttributeCapability.builder.registerCapability();
		UnsagaChunkCapability.BUILDER.registerCapability();
		CustomArrowCapability.BUILDER.registerCapability();
		VillageCapabilityUnsaga.BUILDER.registerCapability();
	}

	/** Capabilityのattachまたは初期化イベント*/
	public  static void registerEvents(){
		LifePoint.registerEvents();
		UnsagaMaterialCapability.register();
		AbilityCapability.registerEvents();
//		EquipmentCacheCapability.registerEvents();
		AccessorySlotCapability.registerEvents();
//		EntityStateCapability.register();
		SkillPanelCapability.registerEvents();
		UnsagaXPCapability.registerEvents();
		TargetHolderCapability.registerEvents();
		ChestCapability.register();
		VillagerInitializerUnsaga.instance().registerCapabilityAttachEvents();
		ItemGunUnsaga.adapter.registerAttachEvent();
		MinsagaForgingCapability.registerEvents();
		UnsagaWorldCapability.registerEvents();
		UnsagaUnlockableContentCapability.adapter.registerAttachEvent();
//		ExtendedPotionCapability.adapter.registerAttachEvent();
//		AdditionalDamageAttributeCapability.adapter.registerAttachEvent();
//		StateCapability.registerEvents();
		UnsagaActionCapability.registerEvents();
		UnsagaChunkCapability.registerEvents();
		CustomArrowCapability.registerEvents();
		VillageCapabilityUnsaga.ADAPTER.registerAttachEvent();
	}
}
