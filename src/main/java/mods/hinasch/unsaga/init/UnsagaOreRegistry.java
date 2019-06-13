package mods.hinasch.unsaga.init;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import mods.hinasch.lib.block.BlockOreBase.IOre;
import mods.hinasch.lib.block.BlockOreProperty;
import mods.hinasch.lib.registry.RegistryUtil;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import net.minecraft.block.Block;
import net.minecraft.init.Items;
import net.minecraft.item.Item;

public class UnsagaOreRegistry{

	public static class OreUnsaga implements IOre{

		final String oreDictID;
		final float exp;
		final String name;
		final int harvestLevel;

		public OreUnsaga(int id, String name,String oreDictID, float exp, int harvest) {
			this.oreDictID = oreDictID;
			this.name = name;
			this.harvestLevel = harvest;
			this.exp = exp;
			// TODO 自動生成されたコンストラクター・スタブ
		}

		@Override
		public Supplier<Item> breaked() {
			// TODO 自動生成されたメソッド・スタブ
			return UnsagaOreRegistry.getBreaked(this);
		}



		@Override
		public float exp() {
			// TODO 自動生成されたメソッド・スタブ
			return this.exp;
		}



		@Override
		public int harvestLevel() {
			// TODO 自動生成されたメソッド・スタブ
			return this.harvestLevel;
		}



		@Override
		public Block oreBlock() {
			// TODO 自動生成されたメソッド・スタブ
			return UnsagaBlockRegistry.getBlock(this);
		}



		@Override
		public String oreDict() {
			// TODO 自動生成されたメソッド・スタブ
			return this.oreDictID;
		}

		@Override
		public Supplier<Item> smelt() {
			// TODO 自動生成されたメソッド・スタブ
			return UnsagaOreRegistry.getSmelt(this);
		}

		@Override
		public String name() {
			// TODO 自動生成されたメソッド・スタブ
			return name;
		}

	}

//	@Override
//	public void initSmelted() {
////		this.map.put(UnsagaOres.LEAD, new )
//		this.registerOreSmelt(UnsagaOres.LEAD, new ContainerItem(() -> UnsagaIngredients.instance().lead,() -> ItemProperty.EMPTY));
//		Supplier<ItemProperty> ruby = () -> UnsagaIngredients.instance().ruby;
//		this.registerOreSmelt(UnsagaOres.RUBY, new ContainerItem(ruby,ruby));
//		Supplier<ItemProperty> sapphire = () -> UnsagaIngredients.instance().sapphire;
//		this.registerOreSmelt(UnsagaOres.SAPPHIRE, new ContainerItem(sapphire,sapphire));
//		this.registerOreSmelt(UnsagaOres.SILVER, new ContainerItem(() -> UnsagaIngredients.instance().silver,() -> ItemProperty.EMPTY));
//		this.registerOreSmelt(UnsagaOres.COPPER, new ContainerItem(() -> UnsagaIngredients.instance().copper,() -> ItemProperty.EMPTY));
//		Supplier<ItemProperty> lightStone = () -> UnsagaIngredients.instance().lightStone;
//		this.registerOreSmelt(UnsagaOres.ANGELITE, new ContainerItem(lightStone,lightStone));
//		Supplier<ItemProperty> darkStone = () -> UnsagaIngredients.instance().darkStone;
//		this.registerOreSmelt(UnsagaOres.DEMONITE, new ContainerItem(darkStone,darkStone));
//
//	}

	private static Map<OreUnsaga,Supplier<Item>> breakedMap = new HashMap<>();
	private static Map<OreUnsaga,Supplier<Item>> smeltMap = new HashMap<>();

	public static Supplier<Item> getBreaked(OreUnsaga ore){
		return breakedMap.get(ore);
	}

	public static Supplier<Item> getSmelt(OreUnsaga ore){
		return smeltMap.get(ore);
	}
	public static void init(){
		initSmelt();
		registerSmelt();
	}
//	public static BlockOreProperty getOre(int id) {
//		// TODO 自動生成されたメソッド・スタブ
//		return instance().getObjectById(id);
//	}
	private static void initSmelt(){
		initSmelt(UnsagaOres.LEAD,()->Items.AIR,()->UnsagaMaterials.LEAD.itemStack().getItem());
		initSmelt(UnsagaOres.RUBY,()->UnsagaMaterials.RUBY.itemStack().getItem(),()->UnsagaMaterials.RUBY.itemStack().getItem());
		initSmelt(UnsagaOres.SAPPHIRE,()->UnsagaMaterials.SAPPHIRE.itemStack().getItem(),()->UnsagaMaterials.SAPPHIRE.itemStack().getItem());
		initSmelt(UnsagaOres.SILVER,()->Items.AIR,()->UnsagaMaterials.SILVER.itemStack().getItem());
		initSmelt(UnsagaOres.COPPER,()->Items.AIR,()->UnsagaMaterials.COPPER.itemStack().getItem());
		initSmelt(UnsagaOres.ANGELITE,()->UnsagaMaterials.LIGHT_STONE.itemStack().getItem(),()->UnsagaMaterials.LIGHT_STONE.itemStack().getItem());
		initSmelt(UnsagaOres.DEMONITE,()->UnsagaMaterials.DARK_STONE.itemStack().getItem(),()->UnsagaMaterials.DARK_STONE.itemStack().getItem());

	}

	private static void initSmelt(OreUnsaga ore,Supplier<Item> breaked,Supplier<Item> smelt){
		breakedMap.put(ore, breaked);
		smeltMap.put(ore, smelt);
	}


	private static void registerSmelt(){
		RegistryUtil.registerSmeltAndDict(UnsagaOres.LEAD);
		RegistryUtil.registerSmeltAndDict(UnsagaOres.RUBY);
		RegistryUtil.registerSmeltAndDict(UnsagaOres.SAPPHIRE);
		RegistryUtil.registerSmeltAndDict(UnsagaOres.SILVER);
		RegistryUtil.registerSmeltAndDict(UnsagaOres.COPPER);
		RegistryUtil.registerSmeltAndDict(UnsagaOres.ANGELITE);
		RegistryUtil.registerSmeltAndDict(UnsagaOres.DEMONITE);
	}

	private void registerOreSmelt(OreUnsaga prop,BlockOreProperty.ContainerItem ore){
//		BlockOreProperty.InsideRegistry.register(prop, ore);
	}


}
