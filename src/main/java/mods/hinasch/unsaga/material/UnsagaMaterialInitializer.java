package mods.hinasch.unsaga.material;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import mods.hinasch.lib.misc.JsonApplier;
import mods.hinasch.lib.misc.JsonApplier.IJsonParser;
import mods.hinasch.lib.registry.RegistryUtil;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.item.wearable.MaterialArmorTextureSetting;
import mods.hinasch.unsaga.util.ToolCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

public class UnsagaMaterialInitializer{

	private static IForgeRegistry<UnsagaMaterial> REGISTRY;
//	public static class MaterialColorReg implements IJsonApplyTarget<UnsagaMaterialJsonParser.JsonParserColor>{
//
//		static Map<UnsagaMaterial,Integer> map = new HashMap<>();
//		@Override
//		public void applyJson(UnsagaMaterialJsonParser.JsonParserColor parser) {
//
//			map.put(parser.m, parser.color);
//		}
//
//		public int get(UnsagaMaterial m){
//			return map.getOrDefault(m, Statics.COLOR_NONE);
//		}
//	}
//	public static class MaterialSpecialNameReg implements IJsonApplyTarget<UnsagaMaterialJsonParser.JsonParserSpecialName>{
//
//		static Table<UnsagaMaterial,ToolCategory,String> map = HashBasedTable.create();
//
//		@Override
//		public void applyJson(UnsagaMaterialJsonParser.JsonParserSpecialName parser) {
//			parser.materials.forEach(in ->{
//				map.put(in, parser.category, parser.specialName);
//			});
//		}
//
//		public String get(UnsagaMaterial m,ToolCategory c){
//			if(this.map.contains(m,c)){
//				return map.get(m, c);
//			}
//			return StringUtil.EMPTY_STRING;
//		}
//
//	}

	//	public List<UnsagaMaterialCategory> categories = Lists.newArrayList();

//	public static class ShieldPowerReg implements IJsonApplyTarget<UnsagaMaterialJsonParser.JsonParserShieldValue>{
//
//		static Map<UnsagaMaterial,Integer> map = new HashMap<>();
//		@Override
//		public void applyJson(UnsagaMaterialJsonParser.JsonParserShieldValue parser) {
//			// TODO 自動生成されたメソッド・スタブ
//			map.put(parser.m, parser.value);
//		}
//
//		public int get(UnsagaMaterial m){
//			return map.getOrDefault(m, 0);
//		}
//	}
	private static UnsagaMaterialInitializer instance;
	//	protected Map<String,List<UnsagaMaterial>> categorised = Maps.newHashMap();
	public static final MaterialSuitabilityRegistry SUITABILITY = new MaterialSuitabilityRegistry();
	public static final ResourceLocation MATERIALS = new ResourceLocation(UnsagaMod.MODID,"materials");
	public static final ResourceLocation MATERIAL_DATA = new ResourceLocation(UnsagaMod.MODID,"data/materials.json");
	public static final ResourceLocation MATERIAL_COLOR = new ResourceLocation(UnsagaMod.MODID,"data/material_color.json");
	public static final ResourceLocation SHIELD_VALUE = new ResourceLocation(UnsagaMod.MODID,"data/shield_value.json");
	public static final ResourceLocation SPECIAL_NAME = new ResourceLocation(UnsagaMod.MODID,"data/special_name.json");
	public static List<UnsagaMaterial> getMerchandiseMaterials(){
		return UnsagaMaterialInitializer.instance().merchandiseMaterial;
	}
	public static UnsagaMaterialInitializer instance(){
		if(instance==null){
			instance = new UnsagaMaterialInitializer();
		}
		return instance;
	}
//	private MaterialColorReg registryColor = new MaterialColorReg();
//	private MaterialSpecialNameReg registrySpecialName = new MaterialSpecialNameReg();
//	private ShieldPowerReg registryShieldPower = new ShieldPowerReg();
//	private List<String> iconGetterList = new ArrayList<>();
//	private Map<UnsagaMaterial,String> materialToIconGetterMap = new HashMap<>();

	protected ImmutableList<UnsagaMaterial> merchandiseMaterial;

	private UnsagaMaterialInitializer(){

	}

//	@Override
	public void applyJson(IJsonParser p) {
		// TODO 自動生成されたメソッド・スタブ
		//		if(p instanceof JsonParserColor){
		//			JsonParserColor parser = (JsonParserColor) p;
		//			parser.m.setMaterialColor(parser.color);
		//		}
		//		if(p instanceof JsonParserShieldValue){
		//			JsonParserShieldValue parser = (JsonParserShieldValue) p;
		//			parser.m.setShieldModifier(parser.value);
		//		}
		//		if(p instanceof JsonParserSpecialName){
		//			JsonParserSpecialName parser = (JsonParserSpecialName) p;
		//			parser.materials.forEach(in ->{
		//				in.setAnotherName(parser.category, parser.specialName);
		//			});
		//		}
	}


//	public int getMaterialColor(UnsagaMaterial m){
//		return this.registryColor.get(m);
//	}

	public static UnsagaMaterial get(String name){
		return RegistryUtil.getValue(REGISTRY, UnsagaMod.MODID, name);
	}
//	public @Nullable Set<UnsagaMaterial> getMaterialsByCategoryID(String id){
//		if(id.equals("category_wood")){
//			return Sets.newHashSet(UnsagaMaterialCategory.WOODS.getChildMaterials());
//		}
//		return null;
//	}

	public static Collection<UnsagaMaterial> getAllMaterials(){
		return RegistryUtil.getValues(REGISTRY, in -> !in.isEmpty());
	}
	public List<UnsagaMaterial> getMaterialsByRank(int rank){
		return getAllMaterials().stream().filter(in->in.rank()==rank).collect(Collectors.toList());
	}

	public List<UnsagaMaterial> getMaterialsByRank(int min,int max){
		return getAllMaterials().stream().filter(in->in.rank()>=min && in.rank()<=max).collect(Collectors.toList());
	}
	public List<UnsagaMaterial> getMerchandiseMaterials(int min,int max){
		return this.merchandiseMaterial.stream().filter(in->in.rank()>=min && in.rank()<=max).collect(Collectors.toList());
	}
//	public int getShieldValue(UnsagaMaterial m){
//		return this.registryShieldPower.get(m);
//	}
//
//
//	public String getSpecialName(UnsagaMaterial m,ToolCategory c){
//		return this.registrySpecialName.get(m, c);
//	}

//	public Collection<String> getSubIconGetterNames(){
//		return iconGetterList;
//	}

//	public String getSubIconName(UnsagaMaterial m){
//		return this.materialToIconGetterMap.getOrDefault(m, StringUtil.EMPTY_STRING);
//	}
//	@Override
	public void init() {
//
//		this.registerIconGetter("allDebris",UnsagaMaterials. DEBRIS1,UnsagaMaterials.DEBRIS2);
//		this.registerIconGetter("bestials",UnsagaMaterials. CARNELIAN,UnsagaMaterials.OPAL,UnsagaMaterials.TOPAZ,UnsagaMaterials.RAVENITE,UnsagaMaterials.LAZULI);
//		this.registerIconGetter("woods",UnsagaMaterials. WOOD,UnsagaMaterials.TONERIKO,UnsagaMaterials.CYPRESS,UnsagaMaterials.OAK,UnsagaMaterials.BIRCH,UnsagaMaterials.SPRUCE,UnsagaMaterials.JUNGLE_WOOD,UnsagaMaterials.DARK_OAK,UnsagaMaterials.ACACIA);
//		this.registerIconGetter("tusks",UnsagaMaterials. TUSK1,UnsagaMaterials.TUSK2);
//		this.registerIconGetter("bones",UnsagaMaterials. BONE1,UnsagaMaterials.BONE2);
//		this.registerIconGetter("scales",UnsagaMaterials. ANCIENT_FISH_SCALE,UnsagaMaterials.THIN_SCALE,UnsagaMaterials.DRAGON_SCALE,UnsagaMaterials.CHITIN,UnsagaMaterials.PRISMARINE,UnsagaMaterials.SHULKER);
//		this.registerIconGetter("corundums",UnsagaMaterials. RUBY,UnsagaMaterials.SAPPHIRE);
//		this.registerIconGetter("metals",UnsagaMaterials. LEAD,UnsagaMaterials.IRON,UnsagaMaterials.SILVER,UnsagaMaterials.METEORIC_IRON,UnsagaMaterials.FAERIE_SILVER,UnsagaMaterials.DAMASCUS,UnsagaMaterials.COPPER);
//		this.registerIconGetter("steels",UnsagaMaterials. STEEL1,UnsagaMaterials.STEEL2);
//		this.registerIconGetter("rocks",UnsagaMaterials. SERPENTINE,UnsagaMaterials.QUARTZ,UnsagaMaterials.COPPER_ORE,UnsagaMaterials.IRON_ORE,UnsagaMaterials.METEORITE,UnsagaMaterials.OBSIDIAN,UnsagaMaterials.DIAMOND);
//		this.registerIconGetter("clothes",UnsagaMaterials. COTTON,UnsagaMaterials.SILK,UnsagaMaterials.VELVET,UnsagaMaterials.LIVE_SILK);
//		this.registerIconGetter("leathers",UnsagaMaterials. FUR,UnsagaMaterials.SNAKE_LEATHER,UnsagaMaterials.CROCODILE_LEATHER,UnsagaMaterials.HYDRA_LEATHER);


		this.SUITABILITY.registerSuitableMaterials();

//		JsonApplier.create(SPECIAL_NAME, in -> new UnsagaMaterialJsonParser.JsonParserSpecialName(), in -> registrySpecialName).parseAndApply();;
//
//
//		JsonApplier.create(MATERIAL_COLOR,in ->new UnsagaMaterialJsonParser.JsonParserColor(),in -> registryColor).parseAndApply();


//		this.initArmorMaterial();
//		this.initToolMaterial();
//		this.initShields();
		this.initMerchandiseSettings();

		MaterialArmorTextureSetting.register();

	}

	protected void initArmorMaterial(){
		//		UnsagaMaterials.DIAMOND.setArmorMaterial(ArmorMaterial.GOLD);
		//		UnsagaMaterials.GOLD.setArmorMaterial(ArmorMaterial.GOLD);
		//		UnsagaMaterials.IRON.setArmorMaterial(ArmorMaterial.IRON);
	}

	protected void initMerchandiseSettings(){
		List<UnsagaMaterial> mutable = Lists.newArrayList();
		MinecraftForge.EVENT_BUS.post(new RegisterMerchandise(mutable));
		this.merchandiseMaterial = ImmutableList.copyOf(mutable);


	}
	protected void initShields(){



	}


	protected void initToolMaterial(){
		//		UnsagaMaterials.TONERIKO.setToolMaterial(UtilUnsagaMaterial.getVanilla("toneriko", ToolMaterial.WOOD, -1F,-1F,-1F,-1F,35F));
		//		UnsagaMaterials.SILVER.setToolMaterial(UtilUnsagaMaterial.getVanilla("silver", ToolMaterial.GOLD, 1F,130F,7.0F,-1F,25F));
		//
		//		UnsagaMaterials.DIAMOND.setToolMaterial(ToolMaterial.DIAMOND);
		//		UnsagaMaterials.GOLD.setToolMaterial(ToolMaterial.GOLD);
		//		UnsagaMaterials.IRON.setToolMaterial(ToolMaterial.IRON);
		//		UnsagaMaterials.STONE.setToolMaterial(ToolMaterial.STONE);
	}
//	@Override
	public void preInit() {

	}
	//	protected void put(UnsagaMaterial... materials){
	//		for(UnsagaMaterial mat:materials){
	//			this.put(mat);
	//		}
	//	}

//	public void register(UnsagaMaterial... m){
//		this.put(m);
//	}

//	protected void registerIconGetter(String s,UnsagaMaterial... materials){
//		List<UnsagaMaterial> list = Lists.newArrayList();
//		for(UnsagaMaterial m:materials){
//			list.add(m);
//			this.materialToIconGetterMap.put(m, s);
//			//			m.setSubIconGetter(s);
//		}
//		//		this.categorised.put(s, Lists.newArrayList(materials));
//		//		this.subIconGetterNames.add(s);
//		this.iconGetterList.add(s);
//	}
//	@Override
//	protected void registerObjects() {
//
//		UnsagaMaterials.register();
//
//		ResourceLocation res = new ResourceLocation(UnsagaMod.MODID,"data/materials.json");
//
//		JsonApplier.create(res,in -> new UnsagaMaterialJsonParser.JsonParserMaterial(),in -> this.get(in.name)).parseAndApply();;
//
//	}

	@SubscribeEvent
	public void registerMerchandises(RegisterMerchandise ev){
		List<UnsagaMaterial> mutable = ev.getList();
		mutable.addAll(UnsagaMaterialCategory.CATEGORIES.stream().flatMap(in ->
		in.getChildMaterials().stream()).filter(in-> in != UnsagaMaterials.PRISMARINE && in!=UnsagaMaterials.SHULKER).collect(Collectors.toList()));
		mutable.add(UnsagaMaterials.DARK_STONE);
		mutable.add(UnsagaMaterials.LIGHT_STONE);
	}

	public static class RegisterMerchandise extends Event{
		final List<UnsagaMaterial> list;

		public RegisterMerchandise(List<UnsagaMaterial> list){
			this.list = list;
		}

		public List<UnsagaMaterial> getList(){
			return this.list;
		}
	}


	@Mod.EventBusSubscriber(modid=UnsagaMod.MODID)
	public static class Handler{

		@SubscribeEvent
		public void makeRegistry(RegistryEvent.NewRegistry ev){
			UnsagaMaterialInitializer.REGISTRY=new RegistryBuilder().setName(MATERIALS).setType(UnsagaMaterial.class)
			.setIDRange(0, 4096).setDefaultKey(new ResourceLocation(UnsagaMod.MODID,"empty")).create();
		}

		@SubscribeEvent
		public void registerMaterials(RegistryEvent.Register<UnsagaMaterial> ev){
			//まずビルダーから
			UnsagaMaterial.Factory map = new UnsagaMaterial.Factory();

			map.register(new UnsagaMaterial.Builder("dummy"));
			map.register(new UnsagaMaterial.Builder("feather"));
			map.register(new UnsagaMaterial.Builder("cotton"));

			map.register(new UnsagaMaterial.Builder("silk"));
			map.register(new UnsagaMaterial.Builder("velvet"));
			map.register(new UnsagaMaterial.Builder("live_silk"));
			map.register(new UnsagaMaterial.Builder("fur"));

			map.register(new UnsagaMaterial.Builder("snake_leather"));
			map.register(new UnsagaMaterial.Builder("crocodile_leather"));
			map.register(new UnsagaMaterial.Builder("hydra_leather"));
			/** 雑木*/
			map.register(new UnsagaMaterial.Builder("wood"));
			/** ヒノキ*/
			map.register(new UnsagaMaterial.Builder("cypress"));
			map.register(new UnsagaMaterial.Builder("oak"));
			map.register(new UnsagaMaterial.Builder("toneriko"));
			map.register(new UnsagaMaterial.Builder("tusk1","tusk"));

			map.register(new UnsagaMaterial.Builder("tusk2","tusk"));
			map.register(new UnsagaMaterial.Builder("bone1","bone"));
			map.register(new UnsagaMaterial.Builder("bone2","bone"));
			map.register(new UnsagaMaterial.Builder("thin_scale"));
			/** 甲板*/
			map.register(new UnsagaMaterial.Builder("chitin"));
			map.register(new UnsagaMaterial.Builder("ancient_fish_scale"));

			map.register(new UnsagaMaterial.Builder("dragon_scale"));
			map.register(new UnsagaMaterial.Builder("light_stone"));

			map.register(new UnsagaMaterial.Builder("dark_stone"));
			map.register(new UnsagaMaterial.Builder("debris1","debris"));
			map.register(new UnsagaMaterial.Builder("debris2","debris"));
			/** 朱雀石*/
			map.register(new UnsagaMaterial.Builder("carnelian"));
			/** 黄龍石（土）*/
			map.register(new UnsagaMaterial.Builder("topaz"));
			/** 玄武石（水）*/
			map.register(new UnsagaMaterial.Builder("ravenite"));
			/** 蒼龍石（木）*/
			map.register(new UnsagaMaterial.Builder("lazuli"));
			/** 白虎石（金）*/
			map.register(new UnsagaMaterial.Builder("opal"));
			map.register(new UnsagaMaterial.Builder("serpentine"));
			map.register(new UnsagaMaterial.Builder("copper_ore"));
			map.register(new UnsagaMaterial.Builder("quartz"));
			map.register(new UnsagaMaterial.Builder("meteorite"));
			map.register(new UnsagaMaterial.Builder("iron_ore"));
			map.register(new UnsagaMaterial.Builder("silver"));

			map.register(new UnsagaMaterial.Builder("obsidian"));
			map.register(new UnsagaMaterial.Builder("ruby","corundum"));
			map.register(new UnsagaMaterial.Builder("sapphire","corundum"));
			map.register(new UnsagaMaterial.Builder("diamond"));
			map.register(new UnsagaMaterial.Builder("copper"));
			map.register(new UnsagaMaterial.Builder("lead"));
			map.register(new UnsagaMaterial.Builder("iron"));

			map.register(new UnsagaMaterial.Builder("meteoric_iron"));
			map.register(new UnsagaMaterial.Builder("steel1","steel"));
			map.register(new UnsagaMaterial.Builder("steel2","steel"));
			map.register(new UnsagaMaterial.Builder("faerie_silver")
					.addSuitableBonus(ToolCategory.KNIFE, 2.0F));
			map.register(new UnsagaMaterial.Builder("damascus"));
			map.register(new UnsagaMaterial.Builder("dragon_heart"));
			map.register(new UnsagaMaterial.Builder("siva_queen")
					.addSuitableBonus(ToolCategory.AXE, 2.0F).addSuitableBonus(ToolCategory.SPEAR, 2.0F));
			map.register(new UnsagaMaterial.Builder("roadster")
					.addSuitableBonus(ToolCategory.SWORD, 2.0F).addSuitableBonus(ToolCategory.KNIFE, 1.0F));
			//////////////////こっから追加要素

			map.register(new UnsagaMaterial.Builder("jungle_wood"));
			map.register(new UnsagaMaterial.Builder("spruce"));
			map.register(new UnsagaMaterial.Builder("birch"));
			map.register(new UnsagaMaterial.Builder("acacia"));
			map.register(new UnsagaMaterial.Builder("dark_oak"));
			map.register(new UnsagaMaterial.Builder("gold"));
			map.register(new UnsagaMaterial.Builder("brass"));
			map.register(new UnsagaMaterial.Builder("nickel_silver"));
			map.register(new UnsagaMaterial.Builder("chalcedony"));


			map.register(new UnsagaMaterial.Builder("bamboo"));
			map.register(new UnsagaMaterial.Builder("osmium"));

			map.register(new UnsagaMaterial.Builder("stone"));
			map.register(new UnsagaMaterial.Builder("prismarine"));
			map.register(new UnsagaMaterial.Builder("shulker"));
			JsonApplier.create(MATERIAL_DATA,in -> new UnsagaMaterialJsonParser.JsonParserMaterial(),in -> map).parseAndApply();
			JsonApplier.create(SPECIAL_NAME, in -> new UnsagaMaterialJsonParser.JsonParserSpecialName(), in -> map).parseAndApply();;
			JsonApplier.create(MATERIAL_COLOR,in ->new UnsagaMaterialJsonParser.JsonParserColor(),in -> map).parseAndApply();
			JsonApplier.create(SHIELD_VALUE,in ->new UnsagaMaterialJsonParser.JsonParserShieldValue(),in -> map).parseAndApply();


			RegistryUtil.Helper<UnsagaMaterial> reg = RegistryUtil.helper(ev,UnsagaMod.MODID,"unsaga");
			map.get().forEach((in,builder)->{
				reg.register(new UnsagaMaterial(builder),builder.name,builder.unlname);
			});




		}
	}
}
