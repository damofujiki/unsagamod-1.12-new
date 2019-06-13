package mods.hinasch.unsaga.villager.smith;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import mods.hinasch.lib.misc.JsonApplier;
import mods.hinasch.lib.misc.JsonApplier.IJsonParser;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.common.LibraryBase;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.material.UnsagaMaterialInitializer;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPlanks;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class LibraryBlacksmith extends LibraryBase<LibraryBlacksmith.CatalogItem,LibraryBlacksmith.ParserMaterial>{

//	UnsagaMaterialRegistry reg = UnsagaMaterialRegistry.instance();
	public static final ResourceLocation RES = new ResourceLocation(UnsagaMod.MODID,"data/material_blacksmith.json");
//	Map<Predicate<ItemStack>,CatalogItem> materialRegistry = new HashMap<>();
//	protected static CatalogBlacksmith INSTANCE;

//	public static CatalogBlacksmith instance(){
//		if(INSTANCE == null){
//			INSTANCE = new CatalogBlacksmith();
//		}
//		return INSTANCE;
//	}

	public static class ParserMaterial implements IJsonParser{

		String type;
		String name;
		String material;
		float amount;

		public ParserMaterial(JsonObject jo){
			this.parse(jo);
		}
		@Override
		public void parse(JsonObject jo) {
			this.type = jo.get("type").getAsString();
			this.name = jo.get("name").getAsString();
			this.material = jo.get("material").getAsString();
			String am = jo.get("amount").getAsString();
			this.amount = Float.valueOf(am);

		}

	}
	public LibraryBlacksmith(){

	}

	private void loadJson(){
		JsonApplier.create(RES, ParserMaterial::new, in -> this).parseAndApply();
	}

	public void register(){
		this.loadJson();
//		this.add(Items.STICK,UnsagaMaterials.WOOD, 0.125F);
//		this.add(Items.BOW, UnsagaMaterials.WOOD, 1.0F);
//		this.addBlockAll(Blocks.IRON_BARS,UnsagaMaterials.IRON,1.0F);
//		this.add(Items.BUCKET, UnsagaMaterials.IRON, 1.0F);
//		this.add(Items.WOODEN_AXE, UnsagaMaterials.WOOD, 1.0F);
//		this.add(Items.WOODEN_HOE, UnsagaMaterials.WOOD, 1.0F);
//		this.add(Items.WOODEN_PICKAXE, UnsagaMaterials.WOOD, 1.0F);
//		this.add(Items.WOODEN_SHOVEL, UnsagaMaterials.WOOD, 1.0F);
//		this.add(Items.WOODEN_SWORD, UnsagaMaterials.WOOD, 1.0F);
//		this.add(Items.IRON_AXE, UnsagaMaterials.IRON, 1.0F);
//		this.add(Items.IRON_HOE, UnsagaMaterials.IRON, 1.0F);
//		this.add(Items.IRON_HORSE_ARMOR, UnsagaMaterials.IRON, 1.0F);
//		this.add(Items.IRON_PICKAXE, UnsagaMaterials.IRON, 1.0F);
//		this.add(Items.IRON_SHOVEL, UnsagaMaterials.IRON, 1.0F);
//		this.add(Items.IRON_SWORD, UnsagaMaterials.IRON, 1.0F);
//		this.add(Items.IRON_BOOTS, UnsagaMaterials.IRON, 1.0F);
//		this.add(Items.IRON_CHESTPLATE, UnsagaMaterials.IRON, 1.0F);
//		this.add(Items.IRON_HELMET, UnsagaMaterials.IRON, 1.0F);
//		this.add(Items.IRON_LEGGINGS, UnsagaMaterials.IRON, 1.0F);
//		this.add(Items.IRON_DOOR, UnsagaMaterials.IRON, 1.0F);
//		this.add(Items.STONE_AXE, UnsagaMaterials.STONE, 1.0F);
//		this.add(Items.STONE_HOE, UnsagaMaterials.STONE, 1.0F);
//		this.add(Items.STONE_PICKAXE, UnsagaMaterials.STONE, 1.0F);
//		this.add(Items.STONE_SHOVEL, UnsagaMaterials.STONE, 1.0F);
//		this.add(Items.STONE_SWORD, UnsagaMaterials.STONE, 1.0F);
		this.addBlock(Blocks.PLANKS,BlockPlanks.EnumType.OAK.getMetadata(),UnsagaMaterials.OAK,0.25F);
		this.addBlock(Blocks.PLANKS,BlockPlanks.EnumType.BIRCH.getMetadata(),UnsagaMaterials.BIRCH,0.25F);
		this.addBlock(Blocks.PLANKS,BlockPlanks.EnumType.SPRUCE.getMetadata(),UnsagaMaterials.SPRUCE,0.25F);
		this.addBlock(Blocks.PLANKS,BlockPlanks.EnumType.JUNGLE.getMetadata(),UnsagaMaterials.JUNGLE_WOOD,0.25F);
		this.addBlock(Blocks.PLANKS,BlockPlanks.EnumType.ACACIA.getMetadata()-4,UnsagaMaterials.ACACIA,0.25F);
		this.addBlock(Blocks.LOG,BlockPlanks.EnumType.OAK.getMetadata(),UnsagaMaterials.OAK,1.0F);
		this.addBlock(Blocks.LOG,BlockPlanks.EnumType.BIRCH.getMetadata(),UnsagaMaterials.BIRCH,1.0F);
		this.addBlock(Blocks.LOG,BlockPlanks.EnumType.SPRUCE.getMetadata(),UnsagaMaterials.SPRUCE,1.0F);
		this.addBlock(Blocks.LOG,BlockPlanks.EnumType.JUNGLE.getMetadata(),UnsagaMaterials.JUNGLE_WOOD,1.0F);
		this.addBlock(Blocks.LOG2,BlockPlanks.EnumType.ACACIA.getMetadata(),UnsagaMaterials.ACACIA,1.0F);
		this.addBlock(Blocks.LOG2,BlockPlanks.EnumType.DARK_OAK.getMetadata(),UnsagaMaterials.DARK_OAK,1.0F);
//		this.add("ingotIron", UnsagaMaterials.IRON,0.5F);
//		this.add("oreIron", UnsagaMaterials.IRON_ORE, 0.5F);
//		this.add("ingotCopper", UnsagaMaterials.COPPER, 0.5F);
//		this.add("oreCopper", UnsagaMaterials.COPPER_ORE, 0.5F);
//		this.add("gemRuby", UnsagaMaterials.RUBY, 0.5F);
//		this.add("gemSapphire", UnsagaMaterials.SAPPHIRE,0.5F);
//		this.add("ingotDamuscus", UnsagaMaterials.DAMASCUS, 0.5F);
//		this.add("ingotOsmium", UnsagaMaterials.OSMIUM, 0.5F);
//		this.add("ingotGold", UnsagaMaterials.GOLD, 0.5F);
//		this.add("ingotSteel", UnsagaMaterials.STEEL1, 0.5F);
//		this.add("ingotBrass", UnsagaMaterials.BRASS, 0.5F);
//		this.add("ingotSilver", UnsagaMaterials.SILVER, 0.5F);
//		this.add("ingotLead", UnsagaMaterials.LEAD, 0.5F);
//		this.add("nuggetGold", UnsagaMaterials.GOLD,0.052F);
//		this.add("gemChalcedonyBlue", UnsagaMaterials.CHALCEDONY, 0.5F);
//		this.add("gemChalcedonyWhite", UnsagaMaterials.CHALCEDONY, 0.5F);
//		this.add("gemChalcedonyRed", UnsagaMaterials.CARNELIAN, 0.5F);
//		this.add("gemQuartz", UnsagaMaterials.QUARTZ, 0.5F);
//		this.add("gemChalcedonyRed", UnsagaMaterials.CARNELIAN, 0.5F);
//		this.add("gemLapis", UnsagaMaterials.LAZULI, 0.5F);
//		this.add("gemOpal", UnsagaMaterials.OPAL, 0.5F);
//		this.add("gemTopaz", UnsagaMaterials.TOPAZ, 0.5F);
//		this.add("oreSerpentine", UnsagaMaterials.SERPENTINE, 0.5F);
//		this.addBlockAll(Blocks.COBBLESTONE,UnsagaMaterials.DEBRIS1,0.5F);
//		this.add(UnsagaItems.MUSKET,UnsagaMaterials.IRON ,0.5F);
	}

	private void addBlock(Block block, int metadata, UnsagaMaterial material, float f) {
		this.add(block, metadata,new CatalogItem(material,f));

	}
	public List<Predicate<ItemStack>> findByMaterial(UnsagaMaterial m){
		return this.materialRegistry.entrySet().stream().filter(in -> in.getValue().getMaterial()==m).map(in -> in.getKey()).collect(Collectors.toList());
	}

	public List<ItemStack> findItemStacksByMaterial(UnsagaMaterial m){
		if(m!=null){
			return this.findByMaterial(m).stream().filter(in -> in instanceof IGetItemStack).map(in -> (IGetItemStack)in)
					.flatMap(in -> in.getItemStack().stream()).collect(Collectors.toList());
		}
		return Lists.newArrayList();
	}
	public Optional<CatalogItem> find(ItemStack is){
		return Optional.of(is)
				.filter(in -> !in.isEmpty())
				.map(in -> this.findData(in))
				.orElse(Optional.empty());
	}

//	public void add(Predicate<ItemStack> item,UnsagaMaterial m,float f){
//		this.materialRegistry.put(item, new Info(m,f));
//	}
//	public void add(Item item,UnsagaMaterial m,float f){
//		this.materialRegistry.put(new PredicateItem(item,OreDictionary.WILDCARD_VALUE), new Info(m,f));
//	}
//	public void add(String string,UnsagaMaterial m,float f){
//		this.materialRegistry.put(new PredicateOre(string), new Info(m,f));
//	}
//	public void add(Block block,UnsagaMaterial m,float f){
//		this.add(block,OreDictionary.WILDCARD_VALUE, m, f);
//	}
//	public void add(Block block,int damage,UnsagaMaterial m,float f){
//		this.materialRegistry.put(new PredicateItem(Item.getItemFromBlock(block),damage), new Info(m,f));
//	}



	public static class CatalogItem{

		final float amount;
		public float getAmount() {
			return amount;
		}

		public UnsagaMaterial getMaterial() {
			return material;
		}

		final UnsagaMaterial material;

		public CatalogItem(UnsagaMaterial m,float am){
			this.amount = am;
			this.material = m;
		}
	}

	public static interface IGetItemStack{

		public List<ItemStack> getItemStack();
	}
//	@Override
//	public Info preRegister(Object... in) {
//		// TODO 自動生成されたメソッド・スタブ
//		return new Info((UnsagaMaterial)in[0],(Float)in[1]);
//	}

	@Override
	public void applyJson(LibraryBlacksmith.ParserMaterial parser) {
		float amount = parser.amount;
		UnsagaMaterial material = UnsagaMaterialInitializer.instance().get(parser.material);
		Preconditions.checkNotNull(material, parser.material+" is not found!");
		this.register(parser.type, parser.name, new CatalogItem(material,amount));
//		if(parser.type.equals("item")){
//			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(parser.name));
//			Preconditions.checkNotNull(item, parser.name+" is not found!");
//			this.materialRegistry.put(new PredicateItem(item,OreDictionary.WILDCARD_VALUE), new Info(material,amount));
//		}
//		if(parser.type.equals("block")){
//			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(parser.name));
//			Preconditions.checkNotNull(block, parser.name+" is not found!");
//			this.materialRegistry.put(new PredicateItem(Item.getItemFromBlock(block),OreDictionary.WILDCARD_VALUE), new Info(material,amount));
//		}
//		if(parser.type.equals("armor")){
//			Optional<ArmorMaterial> opt = Lists.newArrayList(ArmorMaterial.values()).stream().filter(in -> in.getName().equals(parser.name)).findFirst();
//			if(opt.isPresent()){
//				this.materialRegistry.put(new PredicateArmorMaterial(opt.get()), new Info(material,amount));
//			}
//		}
//		if(parser.type.equals("tool")){
//			Optional<ToolMaterial> opt = Lists.newArrayList(ToolMaterial.values()).stream().filter(in -> in.toString().toLowerCase().equals(parser.name)).findFirst();
//			if(opt.isPresent()){
//				this.materialRegistry.put(new PredicateToolMaterial(opt.get()), new Info(material,amount));
//			}
//		}
//		if(parser.type.equals("ore")){
//			this.materialRegistry.put(new PredicateOre(parser.name), new Info(material,amount));
//		}
	}
}
