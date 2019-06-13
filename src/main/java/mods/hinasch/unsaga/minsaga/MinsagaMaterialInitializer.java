package mods.hinasch.unsaga.minsaga;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;

import mods.hinasch.lib.misc.JsonApplier;
import mods.hinasch.lib.misc.JsonApplier.IJsonParser;
import mods.hinasch.lib.registry.RegistryUtil;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import mods.hinasch.unsaga.minsaga.MinsagaMaterial.Builder;
import mods.hinasch.unsaga.villager.smith.LibraryBlacksmith;
import mods.hinasch.unsaga.villager.smith.LibraryBlacksmith.IGetItemStack;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

public class MinsagaMaterialInitializer{
	private static final ResourceLocation MATERIALS_RES = new ResourceLocation(UnsagaMod.MODID,"data/minsaga_materials.json");
	public static enum Ability implements IStringSerializable{SEA("Sea"),ABYSS("Abyss"),LOOT("Looting"),FAERIE("Faerie")
		,QUICKSILVER("Quicksilver"),HARVEST("Harvest +1"),METEOR("Meteor"),DARK("dark"),WEAKNESS("weak"),WITHER_PROTECTION("wither_protection"),BLAST_PROTECTION("blast_protection");

		String name;

		private Ability(String name){
			this.name = name;
		}
		@Override
		public String getName() {
			// TODO 自動生成されたメソッド・スタブ
			return name;
		}

		public String getLocalized(){
			return HSLibs.translateKey("minsaga.ability."+this.getName());
		}


	};

	public static final ResourceLocation MATERIAL_MINSAGA = new ResourceLocation(UnsagaMod.MODID,"minsaga_material");
	public static class ParserMaterial implements IJsonParser{

		String name;
		float attackModifier;
		int durabilityToughness;
		int repair;
		ArmorModifier armorModifier;
		float efficiency;
		int cost;

		@Override
		public void parse(JsonObject jo) {
			this.name = jo.get("material").getAsString();
			this.durabilityToughness = Integer.valueOf(jo.get("durability").getAsString());
			this.repair = Integer.valueOf(jo.get("repair").getAsString());
			float armor_melee = Float.valueOf(jo.get("armor.melee").getAsString());
			float armor_magic = Float.valueOf(jo.get("armor.magic").getAsString());
			this.armorModifier = new ArmorModifier(armor_melee,armor_magic);
			this.efficiency = Float.valueOf(jo.get("efficiency").getAsString());
			this.cost = Integer.valueOf(jo.get("cost").getAsString());
			this.attackModifier = Float.valueOf(jo.get("attack").getAsString());
		}

	}

	private static IForgeRegistry<MinsagaMaterial> REGISTRY;
	private static final Multimap<MinsagaMaterial,Predicate<ItemStack>> MATERIAL_TO_STACK = HashMultimap.create();


	public static void init(){
		UnsagaMod.logger.get().info("registering minsaga forging properties...");
		registerItemPredicate();
		MinsagaForgingEvent.registerEvents();
	}

	public static MinsagaMaterial get(String name){
		return RegistryUtil.getValue(REGISTRY, UnsagaMod.MODID, name);
	}
	@Mod.EventBusSubscriber(modid=UnsagaMod.MODID)
	public static class Registerer{

		@SubscribeEvent
		public void makeRegistry(RegistryEvent.NewRegistry ev){
			REGISTRY = new RegistryBuilder().setName(MATERIAL_MINSAGA).setType(MinsagaMaterial.class)
			.setIDRange(0, 4096).setDefaultKey(RegistryUtil.EMPTY).create();
		}

		@SubscribeEvent
		public void register(RegistryEvent.Register<MinsagaMaterial> ev){
			MinsagaMaterial.Factory factory = new MinsagaMaterial.Factory();

			factory.add( new Builder("empty"));
			factory.add( new Builder("silver","ingotSilver"));
			factory.add( new Builder("bronze","ingotBronze"));
			/** 朱砂*/
			factory.add( new Builder("cinnabar","crystalCinnabar"));
			/** 蒼鉛*/
			factory.add( new Builder("bismuth","ingotBismuth"));
			factory.add( new Builder("electrum","ingotElectrum"));
			/** 白鉄鉱*/
			factory.add( new Builder("marcasite","oreMarcasite"));
			factory.add( new Builder("meteoric_iron","ingotMeteoricIron").setAbility(Ability.BLAST_PROTECTION));
			/** 甲獣のカラ*/
			factory.add( new Builder("hard_shell","hardShell"));
			factory.add( new Builder("damascus","ingotDamascusSteel"));
			factory.add( new Builder("abyss_crystal","enderpearl"));

			factory.add( new Builder("dark_crystal","gemDemonite"));
			/** 魔草の種子*/
			factory.add( new Builder("devilsweed_seed","cropNetherWart"));
			factory.add( new Builder("gold","ingotGold"));
			/** 怪魚の石鱗*/
			factory.add( new Builder("ancient_fish_scale","scaleFish"));
			/** 精霊銀プレート*/
			factory.add( new Builder("faerie_silver","ingotFaerieSilver").setAbility(Ability.WITHER_PROTECTION));
			/** 岩塩*/
			factory.add( new Builder("halite"));
			/** 樹精結晶*/
			factory.add( new Builder("amber","gemAmber"));
			factory.add( new Builder("steel"));
			factory.add( new Builder("iron","ingotSteel"));
			factory.add( new Builder("sticky_string"));
			factory.add( new Builder("reinforced_wing"));
			factory.add( new Builder("debris1").setMaterialChecker(stack->UnsagaMaterials.DEBRIS1.itemStack().isItemEqual(stack)));
			factory.add( new Builder("debris2").setMaterialChecker(stack ->UnsagaMaterials.DEBRIS2.itemStack().isItemEqual(stack)));
			factory.add( new Builder("debris3"));
			factory.add( new Builder("debris4"));
			factory.add( new Builder("debris5"));
			factory.add( new Builder("debris6"));
			factory.add( new Builder("debris7"));

			JsonApplier<MinsagaMaterial.Factory,ParserMaterial> applier = new JsonApplier(MATERIALS_RES,ParserMaterial::new,in ->factory);
			applier.parseAndApply();

		}


	}

	public static Collection<MinsagaMaterial> all(){
		return RegistryUtil.getValuesExceptEmpty(REGISTRY);
	}

	public static Optional<Predicate<ItemStack>> fromMaterial(MinsagaMaterial m){

		return MATERIAL_TO_STACK.entries().stream()
		.filter(in -> in.getKey()==m)
		.map(in -> in.getValue())
		.sorted()
		.findFirst();
	}
	public static Optional<MinsagaMaterial> find(ItemStack is){
		return MATERIAL_TO_STACK.entries().stream()
		.filter(in ->in.getValue().test(is))
		.map(in ->in.getKey())
		.sorted()
		.findFirst();
	}

	public static void registerItemPredicate(){
		MATERIAL_TO_STACK.put(MinsagaMaterials.DEBRIS1, stack ->stack.isItemEqual(UnsagaMaterials.DEBRIS1.itemStack()));
		MATERIAL_TO_STACK.put(MinsagaMaterials.DEBRIS2, stack ->stack.isItemEqual(UnsagaMaterials.DEBRIS2.itemStack()));
		all().stream().filter(in ->!in.oreName().isEmpty())
		.forEach(in ->{
			MATERIAL_TO_STACK.put(in, new LibraryBlacksmith.PredicateOre(in.oreName()));
		});

	}

	private Predicate<ItemStack> createOreChecker(String orename){
		return new LibraryBlacksmith.PredicateOre(orename);
	}

	private Predicate<ItemStack> createItemChecker(Item item,int damage){
		return new LibraryBlacksmith.PredicateItem(item, damage);
	}


	public static class OreNameChecker implements Predicate<ItemStack>,IGetItemStack{

		final String orename;

		public OreNameChecker(String orename){
			this.orename = orename;
		}

		public String getOreName(){
			return this.orename;
		}
		@Override
		public boolean test(ItemStack is) {

			//			UnsagaMod.logger.trace("ores", this.getOreName(),HSLibs.getOreNames(is));
			if(!is.isEmpty()){

				return HSLibs.getOreNames(is).stream().anyMatch(in -> in.equals(this.getOreName()));
			}
			return false;
		}

		@Override
		public List<ItemStack> getItemStack() {
			// TODO 自動生成されたメソッド・スタブ
			return OreDictionary.getOres(orename);
		}

	}


}
