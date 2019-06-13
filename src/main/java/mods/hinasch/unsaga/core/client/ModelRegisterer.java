package mods.hinasch.unsaga.core.client;

import com.google.common.collect.Lists;

import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.client.ClientHelper.ModelHelper;
import mods.hinasch.lib.client.ClientHelper.PluralVariantsModelFactory;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.block.BlockUnsagaCube;
import mods.hinasch.unsaga.core.client.particle.ParticleItems;
import mods.hinasch.unsaga.init.UnsagaItems;
import mods.hinasch.unsaga.init.UnsagaOres;
import mods.hinasch.unsaga.material.UnsagaIngredients;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ModelRegisterer {

	private final ModelHelper modelAgent = new ModelHelper(UnsagaMod.MODID);
	private final PluralVariantsModelFactory pluralVariantsModelFactory = PluralVariantsModelFactory.create(modelAgent, null);


	public static void registerEvent(){
		HSLibs.registerEvent(new ModelRegisterer());
	}
	@SubscribeEvent
	public void register(ModelRegistryEvent ev){
		//		List<String> list = Lists.newArrayList();
		//		list.add("sword");
		//		list.add("sword.failed");
		//		list.add("axe");
		//		pluralVariantsModelFactory.create(itemsNew.sword);
		//		.prepareVariants(list)
		//		.attach();

		this.registerModelAndColor(UnsagaItems.SWORD, "sword");
		this.registerModelAndColor(UnsagaItems.AXE, "axe");
		this.registerModelAndColor(UnsagaItems.KNIFE, "knife");
		this.registerModelAndColor(UnsagaItems.STAFF, "staff");
		this.registerModelAndColor(UnsagaItems.BOW, "bow");
		this.registerModelAndColor(UnsagaItems.SPEAR, "spear");
		this.registerModelAndColor(UnsagaItems.GLOVES, "gloves");
		this.registerModelAndColor(UnsagaItems.HELMET, "helmet");
		this.registerModelAndColor(UnsagaItems.ARMOR, "armor");
		this.registerModelAndColor(UnsagaItems.BOOTS, "boots");
		this.registerModelAndColor(UnsagaItems.LEGGINS, "leggins");
		this.registerModelAndColor(UnsagaItems.SHIELD, "shield");
		this.registerModelAndColor(UnsagaItems.ACCESSORY, "accessory");
		this.registerModelAndColor(UnsagaItems.ENTITY_EGGS, "entity_egg",3);
		this.registerModelAndColor(UnsagaItems.GROWTH_PANEL, "skill_panel");
		this.registerModelAndColor(UnsagaItems.AMMO, "ammo");
		this.registerModelAndColor(UnsagaItems.MUSKET, "musket");
		this.registerModelAndColor(UnsagaItems.WAZA_BOOK, "waza_book");
		modelAgent.registerModelMesher(UnsagaItems.ELEMENT_CHECKER, 0,"element_checker");
		//		modelAgent.registerModelMesher(this.itemsNew.iconCondition, 0,"icon.condition");
		//		modelAgent.registerModelMesher(this.itemsNew.iconCondition, 1,"icon.condition.hot");
		//		modelAgent.registerModelMesher(this.itemsNew.iconCondition, 2,"icon.condition.cold");
		//		modelAgent.registerModelMesher(this.itemsNew.iconCondition, 3,"icon.condition.humid");
		//		pluralVariantsModelFactory.create(this.itemsNew.iconCondition)
		//		.prepareVariants(ItemIconCondition.Type.getIconNames())
		//		.attach();

		//		pluralVariantsModelFactory.target(UnsagaItems.RAW_MATERIALS)
		//		.prepareVariants(RawMaterialItemRegistry.instance().getIconNames())
		//		.attachBy(RawMaterialItemRegistry.instance().getProperties(), in -> modelAgent.getNewModelResource(in.getIconName(),"inventory"));;
		//		ClientHelper.registerColorItem(UnsagaItems.RAW_MATERIALS);

		UnsagaIngredients.IngredientType.all().forEach(in ->{
			modelAgent.registerModelMesher(in.getIngredientItem(), 0,modelAgent.getNewModelResource(in.getName(), "inventory"));
			ClientHelper.registerColorItem(in.getIngredientItem());
		});
//		UnsagaItemRegistry.instance().ingredients.getKeys().forEach(in ->{
//			modelAgent.registerModelMesher(in.getItem(), 0,modelAgent.getNewModelResource(in.getKey().getResourcePath(), "inventory"));
//			ClientHelper.registerColorItem(in.getItem());
//		}
//				);

		//		pluralVariantsModelFactory.target(Item.getItemFromBlock(blocks.stonesAndMetals))
		//		.prepareVariants(BlockUnsagaStone.EnumType.getJsonNames())
		//		.attach();


		Lists.newArrayList(BlockUnsagaCube.Type.values()).forEach(in ->
		modelAgent.registerModelMesher(Item.getItemFromBlock(in.getBlock()), 0,modelAgent.getNewModelResource(in.getUnlocalizedName(), "inventory")));



		UnsagaOres.ALL_ORES.forEach(ore -> modelAgent.registerModelMesher(Item.getItemFromBlock(ore.oreBlock()), 0,modelAgent.getNewModelResource(ore.name(), "inventory")));

		this.registerModelAndColor(ParticleItems.BOULDER, "boulder");
		this.registerModelAndColor(ParticleItems.BUBBLE_BALL, "bubble_ball");
		this.registerModelAndColor(ParticleItems.FIRE_BALL, "fire_ball");
		this.registerModelAndColor(ParticleItems.ICE_NEEDLE, "ice_needle");
		this.registerModelAndColor(ParticleItems.THUNDER_CRAP, "thunder_crap");

		//		modelAgent.registerModelMesher(this.itemsNew.iconCondition, 1,"icon.condition");
	}

	private void registerModelAndColor(Item item,String name,int... length){
		int len = 1;
		if(length.length>0){
			len = length[0];
		}
		for(int i=0;i<len;i++){
			modelAgent.registerModelMesher(item, i,name);
			ClientHelper.registerColorItem(item);
		}

	}

}
