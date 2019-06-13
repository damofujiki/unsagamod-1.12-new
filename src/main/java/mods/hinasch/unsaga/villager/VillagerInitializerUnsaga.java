package mods.hinasch.unsaga.villager;

import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.villager.bartering.BarteringMaterialCategory;
import mods.hinasch.unsaga.villager.bartering.MerchandiseCapability;
import mods.hinasch.unsaga.villager.smith.AbilityMutationManager;
import mods.hinasch.unsaga.villager.smith.EventDisplaySmithTooltip;
import mods.hinasch.unsaga.villager.smith.MaterialTransformRecipeInititalizer;

public class VillagerInitializerUnsaga {

//	public static final UnsagaVillagerProfession PROFESSION = UnsagaVillagerProfession.instance();
//	public static final BlackSmithRegistries BLACKSMITH_REGISTRIES = new BlackSmithRegistries();
//	public static final BarteringMaterialCategory BARTERING_SHOP_TYPES = BarteringMaterialCategory.instance();

	private static VillagerInitializerUnsaga INSTANCE;

	public static VillagerInitializerUnsaga instance(){
		if(INSTANCE == null){
			INSTANCE = new VillagerInitializerUnsaga();
		}
		return INSTANCE;
	}
	public void registerCapabilities(){
		VillagerCapabilityUnsaga.BUILDER.registerCapability();
//		InteractionInfoCapability.adapterBase.registerCapability();
		MerchandiseCapability.BUILDER.registerCapability();
	}

	public void registerCapabilityAttachEvents(){
		VillagerCapabilityUnsaga.registerEvents();
//		InteractionInfoCapability.registerEvents();
		MerchandiseCapability.registerAttachEvents();
	}
	public void preInit(){
		HSLibs.registerEvent(new VillagerProfessionUnsaga());
	}

	public void init(){
		BarteringMaterialCategory.init(); //物々交換の店によるタイプ
//		BlacksmithRegistries.VALID_PAYMENTS.register(); //支払いに使える貴金属の登録
		HSLibs.registerEvent(new MaterialTransformRecipeInititalizer()); //素材の変化
		HSLibs.registerEvent(new AbilityMutationManager());
//		BlacksmithRegistries.ABILITY_MUTATION.register(); //アビリティの変異

//		DisplayPriceEvent.register();
		HSLibs.registerEvent(new EventDisplaySmithTooltip());
//		HSLibs.registerEvent(new EventVillagerBirth());
	}


}
