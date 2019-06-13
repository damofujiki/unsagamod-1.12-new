package mods.hinasch.unsaga.villager.bartering;

import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import mods.hinasch.lib.misc.JsonApplier;
import mods.hinasch.lib.misc.JsonApplier.IJsonParser;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.common.LibraryBase;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.material.UnsagaMaterialInitializer;
import mods.hinasch.unsaga.villager.bartering.LibraryItemPrice.ParserPrice;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LibraryItemPrice extends LibraryBase<Integer,ParserPrice>{


	public static final ResourceLocation RES = new ResourceLocation(UnsagaMod.MODID,"data/price_bartering.json");
	Map<Predicate<ItemStack>,Integer> materialRegistry = Maps.<Predicate<ItemStack>,Integer>newHashMap();

	private static LibraryItemPrice INSTANCE;

	private LibraryItemPrice(){

	}

	public static class ParserPrice implements IJsonParser{

		String type;
		String name;
		int price;
		String baseMaterial;
		float amount;

		public ParserPrice(JsonObject jo){
			this.parse(jo);
		}
		@Override
		public void parse(JsonObject jo) {
			this.type = jo.get("type").getAsString();
			this.name = jo.get("name").getAsString();
			String priceStr = jo.get("price").getAsString();
			if(priceStr.isEmpty()){
				this.price = 1;
			}else{
				this.price = Integer.valueOf(priceStr);
			}
			this.baseMaterial = jo.get("material").getAsString();
			String amountStr = jo.get("amount").getAsString();
			if(amountStr.isEmpty()){
				this.amount = 1.0F;
			}else{
				this.amount = Float.valueOf(amountStr);
			}
		}

	}
	public static LibraryItemPrice instance(){
		if(INSTANCE == null){
			INSTANCE = new LibraryItemPrice();
		}
		return INSTANCE;
	}

	private void loadJson(){
		JsonApplier.create(RES, ParserPrice::new, in -> this).parseAndApply();
	}
	public void register(){
		this.loadJson();
//Items
//		this.add(Items.EMERALD, 10000);
//		this.add(Blocks.STONE, UnsagaMaterials.DEBRIS1.price*4);
//		this.add(Blocks.GLASS, 1000);
//		this.add(Blocks.WOOL, 200);
//		this.add(Items.STRING, 200);
//		this.add(Items.WHEAT, 200);
//		this.add(Items.COAL, (int)(UnsagaMaterials.OAK.price*1.2F));
//		this.add(Items.BEEF, 600);
//		this.add(Items.COOKED_BEEF, 400);
//		this.add(Items.CHICKEN, 300);
//		this.add(Items.COOKED_CHICKEN, 300);
//		this.add(Items.BEETROOT, 150);
//		this.add(Items.CARROT, 100);
//		this.add(Items.BLAZE_ROD,10000);
//		this.add(Items.BLAZE_POWDER, 2000);
//		this.add(Items.ENDER_PEARL, 10000);
//		this.add(Items.LEATHER, 700);
//		this.add(Items.POTATO, 60);
//		this.add(Items.BAKED_POTATO, 120);
//		this.add(Items.FISH, 80);
//
//		this.add(Items.COOKED_FISH, 300);
//		this.add(Items.ROTTEN_FLESH, 1);
//		this.add(Items.POISONOUS_POTATO, 1);
//		this.add(Items.BOAT, (int)(UnsagaMaterials.OAK.price*1.5F));
//		this.add(Items.PORKCHOP, 250);
//		this.add(Items.COOKED_PORKCHOP, 500);
//		this.add(ArmorMaterial.GOLD,UnsagaMaterials.GOLD.price);
//		this.add(ToolMaterial.GOLD,UnsagaMaterials.GOLD.price);
//		this.add(ArmorMaterial.IRON,UnsagaMaterials.IRON.price);
//		this.add(ToolMaterial.IRON,UnsagaMaterials.IRON.price);
//		this.add(ArmorMaterial.LEATHER,500);
//		this.add("ingotZinc", 1000);
//		this.add("ingotBismuth", 1500);
//		this.add("ingotBrass", 4000);
//		this.add("ingotBronze", 3000);
//		this.add("ingotSUS", 6000);
//		this.add("record", 10000);
//		this.add("ingotTitanium", 6000);
//		this.add(UnsagaItems.MUSKET, 1000);

	}

	public OptionalInt find(ItemStack is){
		return this.findData(is).isPresent() ? OptionalInt.of(this.findData(is).get()) : OptionalInt.empty();
	}
//	public void add(Predicate<ItemStack> item,int price){
//		this.materialRegistry.put(item, price);
//	}
//	public void add(Item item,int price){
//		this.materialRegistry.put(new PredicateItem(item,OreDictionary.WILDCARD_VALUE), price);
//	}
//	public void add(String string,int price){
//		this.materialRegistry.put(new PredicateOre(string), price);
//	}
//	public void add(Block block,int price){
//		this.add(block,OreDictionary.WILDCARD_VALUE, price);
//	}
//	public void add(Block block,int damage,int price){
//		this.materialRegistry.put(new PredicateItem(Item.getItemFromBlock(block),damage), price);
//	}
//	public void add(ToolMaterial tm,int price){
//		this.materialRegistry.put(new PredicateToolMaterial(tm), price);
//	}
//
//	public void add(ArmorMaterial am,int price){
//		this.materialRegistry.put(new PredicateArmorMaterial(am), price);
//	}

	@Override
	public void applyJson(ParserPrice parser) {
		// TODO 自動生成されたメソッド・スタブ
		int price = Optional.of(parser.baseMaterial)
				.filter(in -> !in.isEmpty())
				.map(in ->{
					UnsagaMaterial mate = UnsagaMaterialInitializer.get(parser.baseMaterial);
					Preconditions.checkNotNull(mate, parser.baseMaterial+" is not found!");
					return (int) (mate.price() * parser.amount);
				}).orElse(parser.price);

		this.register(parser.type, parser.name, price);
//		if(parser.type.equals("item")){
//			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(parser.name));
//			Preconditions.checkNotNull(item, parser.name+" is not found!");
//			this.add(item, price);
//		}
//		if(parser.type.equals("block")){
//			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(parser.name));
//			Preconditions.checkNotNull(block, parser.name+" is not found!");
//			this.add(block, price);
//		}
//		if(parser.type.equals("armor")){
//			Optional<ArmorMaterial> opt = Lists.newArrayList(ArmorMaterial.values()).stream().filter(in -> in.getName().equals(parser.name)).findFirst();
//			if(opt.isPresent()){
//				this.add(opt.get(), price);
//			}
//		}
//		if(parser.type.equals("tool")){
//			Optional<ToolMaterial> opt = Lists.newArrayList(ToolMaterial.values()).stream().filter(in -> in.toString().toLowerCase().equals(parser.name)).findFirst();
//			if(opt.isPresent()){
//				this.add(opt.get(), price);
//			}
//		}
//		if(parser.type.equals("ore")){
//			this.add(parser.name, price);
//		}
	}
}
