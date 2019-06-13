package mods.hinasch.unsaga.villager.bartering;

import java.util.Collection;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.gson.JsonObject;

import mods.hinasch.lib.misc.JsonApplier;
import mods.hinasch.lib.misc.JsonApplier.IJsonApplyTarget;
import mods.hinasch.lib.misc.JsonApplier.IJsonParser;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.material.UnsagaMaterialInitializer;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import mods.hinasch.unsaga.util.ToolCategory;
import net.minecraft.util.ResourceLocation;

/** 素材を分けるおおまかなカテゴリ*/
public class BarteringMaterialCategory{

	public static class MaterialAssignment implements IJsonApplyTarget<ParserCategory>{
		Map<UnsagaMaterial,Type> map = new HashMap<>();
		Multimap<Type,UnsagaMaterial> map2 = HashMultimap.create();

		public MaterialAssignment(){
			this.add(Type.UNKNOWN, UnsagaMaterials.DUMMY);
		}
		private void add(Type type,Collection<UnsagaMaterial> materials){
			materials.forEach(in ->{
				map.put(in, type);
				map2.put(type, in);
			});
		}

		private void add(Type type,UnsagaMaterial... materials){
			for(UnsagaMaterial mate:materials){
				map.put(mate, type);
				map2.put(type, mate);
			}
		}

		@Override
		public void applyJson(ParserCategory parser) {
			Preconditions.checkArgument(fromString(parser.type)!=Type.UNKNOWN, parser.type+" is not found.");
			Type type = fromString(parser.type);
//			UnsagaMod.logger.trace(this.getClass().getName(), "applied materials to category "+parser.type+".");
			add(type, parser.materials);
		}

		public Collection<UnsagaMaterial> getMaterials(Type type){
			return map2.get(type);
		}

		public Type getType(UnsagaMaterial m){
			return map.getOrDefault(m,Type.UNKNOWN);
		}
	}
	public static class ParserCategory implements IJsonParser{

		String type;
		Set<UnsagaMaterial> materials = new HashSet<>();

		public ParserCategory(JsonObject jo){
			this.parse(jo);
		}
		@Override
		public void parse(JsonObject jo) {
			// TODO 自動生成されたメソッド・スタブ

			this.type = jo.get("type").getAsString();
			String matstr = jo.get("materials").getAsString();
			Splitter.on(",").split(matstr).forEach(in ->{
				UnsagaMaterial m = UnsagaMaterialInitializer.instance().get(in);
				if(m!=null){
					this.materials.add(m);
				}else{
					Preconditions.checkNotNull(m, in+" is not found.");
				}
			});
		}

	}


	public static class ParserShopType implements IJsonParser{

		BarteringShopType type;
		Set<BarteringMaterialCategory.Type> categories = new HashSet<>();
		Set<ToolCategory> toolTypes = new HashSet<>();

		public ParserShopType(JsonObject jo){
			this.parse(jo);
		}

		@Override
		public void parse(JsonObject jo) {
			// TODO 自動生成されたメソッド・スタブ
			String shopid = jo.get("shop").getAsString();
			String materialstr = jo.get("material").getAsString();
			String categorystr = jo.get("category").getAsString();
			this.type = BarteringShopType.fromName(shopid);
			Preconditions.checkArgument(this.type!=BarteringShopType.UNKNOWN, shopid+" is not found.");
			Splitter.on(",").split(materialstr).forEach(in ->{
				BarteringMaterialCategory.Type cate = BarteringMaterialCategory.fromString(in);
				Preconditions.checkArgument(cate!=BarteringMaterialCategory.Type.UNKNOWN, in+" is not found.");
				this.categories.add(cate);
			});
			Splitter.on(",").split(categorystr).forEach(in ->{
				ToolCategory cate = ToolCategory.fromString(in);
				Preconditions.checkArgument(cate!=ToolCategory.NONE, in+" is not found.");
				this.toolTypes.add(cate);
			});
		}

	}

	public static class ShopTypeAssignment implements IJsonApplyTarget<ParserShopType>{

		Map<BarteringShopType,EnumSet<BarteringMaterialCategory.Type>> merchandiseTypeByShop = new HashMap<>();
		Map<BarteringShopType,EnumSet<ToolCategory>> marchandiseToolTypeByShop = new HashMap<>();

		@Override
		public void applyJson(ParserShopType parser) {
			Preconditions.checkArgument(parser.type!=BarteringShopType.UNKNOWN, parser.type+" is not found.");
			merchandiseTypeByShop.put(parser.type,EnumSet.copyOf( parser.categories));
			marchandiseToolTypeByShop.put(parser.type, EnumSet.copyOf(parser.toolTypes));
		}

		public EnumSet<BarteringMaterialCategory.Type> getCategories(BarteringShopType shop){
			return merchandiseTypeByShop.getOrDefault(shop, EnumSet.noneOf(Type.class));
		}

		public EnumSet<ToolCategory> getToolTypes(BarteringShopType shop){
			return marchandiseToolTypeByShop.getOrDefault(shop, EnumSet.noneOf(ToolCategory.class));
		}
	}
	public static enum Type{
		FEATHER("feather"),CLOTH("cloth"),LEATHER("leather"),WOOD("wood"),BONE("bone")
		,SCALE("scale"),JEWEL_MAGICAL("jewel_magical"),
		ORE("ore"),BESTIAL("bestial"),JEWEL("jewel"),METAL("metal"),METAL_PRECIOUS("metal_precious"),RARE("rare"),UNKNOWN("unknown");

		public static Collection<Type> list(){
			return Lists.newArrayList(Type.values());
		}
		final String name;

		private Type(String name){
			this.name = name;
		}

		public String getLocalized(){
			return HSLibs.translateKey("gui.unsaga.bartering.shopType."+this.getName());
		}
		public Collection<UnsagaMaterial> getMaterials(){
			return BarteringMaterialCategory.ASSIGNMENTS_MATERIAL.getMaterials(this);
		}

		public String getName(){
			return this.name;
		}
	}
	public static final ResourceLocation RES = new ResourceLocation(UnsagaMod.MODID,"data/material_category.json");


	public static final ResourceLocation RES2 = new ResourceLocation(UnsagaMod.MODID,"data/shop_types.json");
	public static final MaterialAssignment ASSIGNMENTS_MATERIAL = new MaterialAssignment();

	public static final ShopTypeAssignment ASSIGNMENTS_SHOP = new ShopTypeAssignment();

	public static Type fromString(String id){
		return Type.list().stream()
		.filter(in -> in.getName().equals(id))
		.findFirst().orElse(Type.UNKNOWN);
	}


	public static void init(){
		loadJson();
	}


	private static void loadJson(){
		JsonApplier.create(RES, ParserCategory::new, in -> ASSIGNMENTS_MATERIAL).parseAndApply();
		JsonApplier.create(RES2, ParserShopType::new, in -> ASSIGNMENTS_SHOP).parseAndApply();
	}
	public final EnumSet<Type> merchandises = EnumSet.of(Type.FEATHER,Type.CLOTH,Type.LEATHER
			,Type.WOOD,Type.BONE,Type.SCALE,Type.JEWEL,Type.JEWEL_MAGICAL
			,Type.ORE,Type.BESTIAL,Type.JEWEL,Type.METAL,Type.METAL_PRECIOUS);
}
