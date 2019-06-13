package mods.hinasch.unsaga.init;

import java.util.Optional;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import mods.hinasch.lib.item.ItemCustomEntityEgg;
import mods.hinasch.lib.item.ItemCustomEntityEgg.IPreSpawn;
import mods.hinasch.lib.item.SimpleCreativeTab;
import mods.hinasch.lib.registry.RegistryUtil;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.Abilities;
import mods.hinasch.unsaga.ability.AbilityCapability;
import mods.hinasch.unsaga.ability.AbilityPotentialTableProvider;
import mods.hinasch.unsaga.ability.IAbility;
import mods.hinasch.unsaga.core.item.UnsagaCreativeTabs;
import mods.hinasch.unsaga.core.item.misc.ItemElementChecker;
import mods.hinasch.unsaga.core.item.misc.ItemIngredientUnsaga;
import mods.hinasch.unsaga.core.item.misc.ItemTechBook;
import mods.hinasch.unsaga.core.item.misc.skillpanel.ItemSkillPanel;
import mods.hinasch.unsaga.core.item.weapon.ItemAxeUnsaga;
import mods.hinasch.unsaga.core.item.weapon.ItemBowUnsaga;
import mods.hinasch.unsaga.core.item.weapon.ItemGloveUnsaga;
import mods.hinasch.unsaga.core.item.weapon.ItemGunUnsaga;
import mods.hinasch.unsaga.core.item.weapon.ItemKnifeUnsaga;
import mods.hinasch.unsaga.core.item.weapon.ItemSpearUnsaga;
import mods.hinasch.unsaga.core.item.weapon.ItemStaffUnsaga;
import mods.hinasch.unsaga.core.item.weapon.ItemSwordUnsaga;
import mods.hinasch.unsaga.core.item.wearable.ItemAccessoryUnsaga;
import mods.hinasch.unsaga.core.item.wearable.ItemArmorUnsaga;
import mods.hinasch.unsaga.core.item.wearable.ItemShieldUnsaga;
import mods.hinasch.unsaga.material.UnsagaIngredients;
import mods.hinasch.unsaga.material.UnsagaIngredients.IngredientType;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.material.UnsagaMaterialCapability;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import mods.hinasch.unsaga.util.ToolCategory;
import mods.hinasch.unsaga.villager.VillagerProfessionUnsaga;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

public class UnsagaItemRegisterer{

	public static class PreProfessionSetter implements IPreSpawn{

		final VillagerProfession prof;
		public PreProfessionSetter(VillagerProfession prof){
			this.prof = prof;
		}
		@Override
		public Entity preSpawn(World world, Entity entity, XYZPos pos) {
			if(entity instanceof EntityVillager){
				((EntityVillager)entity).setProfession(prof);
			}
			return entity;
		}

	}

//	private static final BiMap<Ingredient,Item> INGREDIENT_ITEM = HashBiMap.create();
	private static final BiMap<IngredientType,Item> INGREDIENT_ITEM = HashBiMap.create();

	public static ItemStack createStack(Item item,UnsagaMaterial mate,int meta){
		ItemStack newStack = new ItemStack(item,1,meta);
		final UnsagaMaterial material = item==UnsagaItems.MUSKET ? UnsagaMaterials.IRON : mate;


		//最初から覚えているアビリティを付与

//		if(material==UnsagaMaterials.instance().dragonHeart){
//			if(AbilityCapability.adapter.hasCapability(newStack)){
//				AbilityCapability.adapter.getCapability(newStack).setAbility(3, AbilityRegistry.instance().superHealing);
//			}
//		}
		UnsagaMaterialCapability.adapter.getCapabilityOptional(newStack).ifPresent(capMate ->{
			AbilityCapability.adapter.getCapabilityOptional(newStack).ifPresent(capAbility ->{
				IAbility ab = AbilityPotentialTableProvider.TABLE_PASSIVE.getInherentAbility(mate).orElse(Abilities.EMPTY);
				if(!ab.isAbilityEmpty()){
					capAbility.getAbilitySlots().updateSlot(3, ab);
				}
			});
			capMate.setMaterial(material);
			capMate.setWeight(material.weight().getValue());
		});

		return newStack;
	}


	public static Optional<IngredientType> to(Item item){
		return Optional.ofNullable(INGREDIENT_ITEM.inverse().get(item));
	}
	public static Optional<Item> fromIngredient(IngredientType item){
		return Optional.ofNullable(INGREDIENT_ITEM.get(item));
	}

	public static void init(){
		registerEntityEggs();
	}

	/**
	 * 村人のスポーンエッグの設定
	 */
	private static void registerEntityEggs(){
		int c1 = 0xff0000;
		int c2 = 0x000000;

		UnsagaItems.ENTITY_EGGS.addMaping(0, EntityVillager.class, new ResourceLocation("unsaga.villager.merchant"), c1, c2,new PreProfessionSetter(VillagerProfessionUnsaga.MERCHANT));
		UnsagaItems.ENTITY_EGGS.addMaping(1, EntityVillager.class, new ResourceLocation("unsaga.villager.magic_merchant"), c1, c2,new PreProfessionSetter(VillagerProfessionUnsaga.MAGIC_MERCHANT));
		UnsagaItems.ENTITY_EGGS.addMaping(2, EntityVillager.class, new ResourceLocation("unsaga.villager.blacksmith"), c1, c2,new PreProfessionSetter(VillagerProfessionUnsaga.BLACKSMITH));
		UnsagaItems.ENTITY_EGGS.addMaping(3, EntityVillager.class, new ResourceLocation("unsaga.villager.mentor"), c1, c2,new PreProfessionSetter(VillagerProfessionUnsaga.MENTOR));
		UnsagaItems.ENTITY_EGGS.addMaping(4, EntityVillager.class, new ResourceLocation("unsaga.villager.carrier"), c1, c2,new PreProfessionSetter(VillagerProfessionUnsaga.CARRIER));

	}
	public UnsagaItemRegisterer() {

	}

	@SubscribeEvent
	public void register(RegistryEvent.Register<Item> ev) {
		SimpleCreativeTab tabTools = UnsagaCreativeTabs.TOOLS;
		SimpleCreativeTab tabMisc = UnsagaCreativeTabs.MISC;
		SimpleCreativeTab tabSkillPanels = UnsagaCreativeTabs.PANEL_GROWTH;

		RegistryUtil.Helper<Item> helper = RegistryUtil.helper(ev, UnsagaMod.MODID, "unsaga");

		helper.registerItem(new ItemSwordUnsaga(), "sword", tabTools);
		helper.registerItem(new ItemAxeUnsaga(), "axe", tabTools);
		helper.registerItem(new ItemKnifeUnsaga(), "knife", tabTools);
		helper.registerItem(new ItemStaffUnsaga(), "staff", tabTools);
		helper.registerItem(new ItemBowUnsaga(), "bow", tabTools);
		helper.registerItem(new ItemSpearUnsaga(), "spear", tabTools);
		helper.registerItem(new ItemGloveUnsaga(), "gloves", tabTools);
		helper.registerItem(new ItemArmorUnsaga(ToolCategory.HELMET), "helmet", tabTools);
		helper.registerItem(new ItemArmorUnsaga(ToolCategory.BOOTS), "boots", tabTools);
		helper.registerItem(new ItemArmorUnsaga(ToolCategory.LEGGINS), "leggins", tabTools);
		helper.registerItem(new ItemArmorUnsaga(ToolCategory.ARMOR), "armor", tabTools);
		helper.registerItem(new ItemShieldUnsaga(), "shield", tabTools);
		helper.registerItem(new ItemAccessoryUnsaga(), "accessory", tabTools);
		helper.registerItem(new ItemElementChecker(), "element_checker", tabMisc);
		helper.registerItem(new ItemSkillPanel(), "skill_panel", tabSkillPanels);

//		this.put(new ItemSwordUnsaga(), "sword",tabTools,ev);
//		this.put(new ItemAxeUnsaga(), "axe", tabTools,ev);
//		this.put(new ItemKnifeUnsaga(), "knife", tabTools,ev);
//		this.put(new ItemStaffUnsaga(), "staff", tabTools,ev);
//		this.put(new ItemBowUnsaga(), "bow", tabTools,ev);
//		this.put(new ItemSpearUnsaga(), "spear", tabTools,ev);
//		this.put(new ItemGloveUnsaga(), "gloves", tabTools,ev);
//		this.put(new ItemArmorUnsaga(ToolCategory.HELMET), "helmet", tabTools,ev);
//		this.put(new ItemArmorUnsaga(ToolCategory.BOOTS), "boots", tabTools,ev);
//		this.put(new ItemArmorUnsaga(ToolCategory.LEGGINS), "leggins", tabTools,ev);
//		this.put(new ItemArmorUnsaga(ToolCategory.ARMOR), "armor", tabTools,ev);
//		this.put(new ItemShieldUnsaga(), "shield", tabTools,ev);
//		this.put(new ItemAccessoryUnsaga(), "accessory", tabTools,ev);
//		this.put(new ItemElementChecker(), "element_checker", tabMisc,ev);
//		this.put(new ItemSkillPanel(), "skill_panel", tabSkillPanels,ev);


//		this.iconCondition = (Item) this.put(new ItemIcon(), "icon.condition", null);
//		this.put(new ItemRawMaterial("unsaga.raw_material"), "raw_material", tabMisc);

		UnsagaIngredients.IngredientType.all()
		.stream()
		.sorted()
		.forEach(in->{
			Item item = new ItemIngredientUnsaga(in);
			helper.registerItem(item, in.getName(),tabMisc);
//			Item item = this.put(new ItemIngredientUnsaga(in), in.getKey().getResourcePath(), tabMisc,ev);
//			ingredients.putObject(in, item);
			INGREDIENT_ITEM.put(in, item);
		});

		helper.registerItem(new ItemCustomEntityEgg(), "entity_egg",tabMisc);
//		this.put(new ItemCustomEntityEgg(),"entity_egg",tabMisc,ev);



		helper.registerItem(new ItemGunUnsaga(), "musket",tabTools);
		helper.registerItem(new Item(), "ammo",tabMisc);
		helper.registerItem(new ItemTechBook(), "waza_book",tabMisc);
//		this.put(new ItemGunUnsaga(), "musket", tabMisc,ev);
//		this.put(new Item(), "ammo", tabMisc,ev);
//		this.put(new ItemTechBook(), "waza_book", tabMisc,ev);



		//破裂パーティクル用の空アイテム
		helper.registerItem(new Item(), "fire_ball");
		helper.registerItem(new Item(), "bubble_ball");
		helper.registerItem(new Item(), "boulder");
		helper.registerItem(new Item(), "thunder_crap");
		helper.registerItem(new Item(), "ice_needle");

//		this.put(new Item(), "fire_ball", null,ev);
//		this.put(new Item(), "bubble_ball", null,ev);
//		this.put(new Item(), "boulder", null,ev);
//		this.put(new Item(), "thunder_crap", null,ev);
//		this.put(new Item(), "ice_needle", null,ev);
//		UnsagaItems.test();




	}

	public void registerRecipes(){
//		RecipeUtilNew.RecipeShaped.create().setBase("G I"," P ").addAssociation('G', Items.GUNPOWDER)
//		.addAssociation('I', Items.IRON_INGOT).addAssociation('P', Items.PAPER).setOutput(new ItemStack(ammo,4)).register();
	}
}
