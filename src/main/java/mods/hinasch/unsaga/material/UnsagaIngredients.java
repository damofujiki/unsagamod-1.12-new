package mods.hinasch.unsaga.material;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.base.Preconditions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.mojang.realmsclient.util.Pair;

import io.netty.util.internal.StringUtil;
import mods.hinasch.lib.util.Statics;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.block.BlockUnsagaCube;
import mods.hinasch.unsaga.init.UnsagaItemRegisterer;
import mods.hinasch.unsaga.init.UnsagaOres;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;

/** UnsagaMaterialの原材料、アイテムとしての生素材 Raw Materials*/
public class UnsagaIngredients{

	protected static UnsagaIngredients INSTANCE;

	//
	//	public static final Ingredient COTTON = new Ingredient(1,"cotton",UnsagaMaterials.COTTON);
	//	public static final Ingredient SILK = new Ingredient(2,"silk",UnsagaMaterials.SILK);
	//	public static final Ingredient VELVET = new Ingredient(3,"velvet",UnsagaMaterials.VELVET);
	//	public static final Ingredient LIVE_SILK = new Ingredient(4,"live_silk",UnsagaMaterials.LIVE_SILK);
	//	public static final Ingredient FUR = new Ingredient(5,"fur",UnsagaMaterials.FUR);
	//	public static final Ingredient SNAKE_LEATHER = new Ingredient(6,"snake_leather",UnsagaMaterials.SNAKE_LEATHER);
	////	silk = this.put(new Ingredient(2,"silk",UnsagaMaterials.SILK).setOreDictID("cloth").setIconName("cloth"));
	////	velvet = this.put(new Ingredient(3,"velvet",UnsagaMaterials.VELVET).setOreDictID("cloth").setIconName("cloth"));
	////	liveSilk = this.put(new Ingredient(4,"live_silk",UnsagaMaterials.LIVE_SILK).setOreDictID("cloth").setIconName("cloth"));
	////	fur = this.put(new Ingredient(5,"fur",UnsagaMaterials.FUR));
//	public Ingredient cotton,silk,liveSilk,velvet,fur,snakeLeather,hydraLeather;
//	public Ingredient cypress,oak,fraxinus,tusk1,tusk2,bone,thinScale;
//	public Ingredient chitin,ancientFishScale,dragonScale,crocodileLeather,lightStone,darkStone;
//	public Ingredient debris1,debris2,carnelian,ravenite,opal,topaz,lapis;
//	public Ingredient meteorite,silver,ruby,sapphire,copper,lead,meteoricIron;
//	public Ingredient steel1,steel2,faerieSilver,damascus,dragonHeart;
//	public Ingredient jungleWood,birch,darkOak,spruce,acacia;

	public static float AMOUNT_HALF = 0.5F;
	protected UnsagaIngredients(){


	}
	//	UnsagaMaterialRegistry m = UnsagaRegistries.MATERIALS;

	public static void init() {



//		DictionaryUnsagaMaterial.associateToMaterial();
		registerOreDicts();
		validateMerchandise();
//		HSLibs.registerEvent(new UnsagaIngredients());
	}


	public static void preInit() {
//		this.registerObjects();

	}

	public static ItemStack toStack(UnsagaMaterial m){
		return materialItemStackMap.getOrDefault(m,Pair.of(ItemStack.EMPTY,0F)).first();
	}


	public static Optional<Pair<UnsagaMaterial,Float>> find(ItemStack is){
		return materialItemStackMap.entrySet()
				.stream()
				.filter(in -> in.getValue().first().isItemEqual(is))
				.map(in -> Pair.of(in.getKey(),in.getValue().second()))
				.findFirst();
	}


	public static ItemStack makeStack(UnsagaMaterial m,int amount){
		return new ItemStack(toStack(m).getItem(),amount);
	}
	protected void registerObjects() {
		// TODO 自動生成されたメソッド・スタブ
//		cotton = this.put(new Ingredient(1,"cotton",UnsagaMaterials.COTTON).setOreDictID("cloth").setIconName("cloth"));
//		silk = this.put(new Ingredient(2,"silk",UnsagaMaterials.SILK).setOreDictID("cloth").setIconName("cloth"));
//		velvet = this.put(new Ingredient(3,"velvet",UnsagaMaterials.VELVET).setOreDictID("cloth").setIconName("cloth"));
//		liveSilk = this.put(new Ingredient(4,"live_silk",UnsagaMaterials.LIVE_SILK).setOreDictID("cloth").setIconName("cloth"));
//		fur = this.put(new Ingredient(5,"fur",UnsagaMaterials.FUR));
//		snakeLeather = this.put(new Ingredient(6,"snake_leather",UnsagaMaterials.SNAKE_LEATHER).setOreDictID("leather"));
//		hydraLeather = this.put(new Ingredient(7,"hydra_leather",UnsagaMaterials.HYDRA_LEATHER).setOreDictID("leather"));
//		crocodileLeather = this.put(new Ingredient(8,"crocodile_leather",UnsagaMaterials.CROCODILE_LEATHER).setOreDictID("leather"));
//		cypress = this.put(new Ingredient(9,"cypress",UnsagaMaterials.CYPRESS).setItemColored(true).setIconName("woodpile").setOreDictID("woodpile"));
//		oak = this.put(new Ingredient(10,"oak",UnsagaMaterials.OAK).setItemColored(true).setIconName("woodpile").setOreDictID("woodpile"));
//		fraxinus = this.put(new Ingredient(11,"toneriko",UnsagaMaterials.TONERIKO).setItemColored(true).setIconName("woodpile").setOreDictID("woodpile"));
//		tusk1 = this.put(new Ingredient(12,"tusk1","tusk",UnsagaMaterials.TUSK1));
//		tusk2 = this.put(new Ingredient(13,"tusk2","tusk",UnsagaMaterials.TUSK2));
//		bone = this.put(new Ingredient(14,"bone",UnsagaMaterials.BONE2).setOreDictID("bone"));
//		thinScale = this.put(new Ingredient(15,"thin_scale",UnsagaMaterials.THIN_SCALE));
//		chitin = this.put(new Ingredient(16,"chitin",UnsagaMaterials.CHITIN));
//		ancientFishScale = this.put(new Ingredient(17,"ancient_fish_scale",UnsagaMaterials.ANCIENT_FISH_SCALE).setOreDictID("ancientFishScale"));
//		dragonScale = this.put(new Ingredient(18,"dragon_scale",UnsagaMaterials.DRAGON_SCALE));
//		lightStone = this.put(new Ingredient(19,"light_stone",UnsagaMaterials.LIGHT_STONE).setOreDictID("gemLightStone"));
//		darkStone = this.put(new Ingredient(20,"dark_stone",UnsagaMaterials.DARK_STONE).setOreDictID("gemDarkStone"));
//		debris1 = this.put(new Ingredient(21,"debris1","debris",UnsagaMaterials.DEBRIS1).setOreDictID("debris")).setAmount(0.125F);
//		debris2 = this.put(new Ingredient(22,"debris2","debris",UnsagaMaterials.DEBRIS2).setOreDictID("debris")).setAmount(0.125F);
//		carnelian = this.put(new Ingredient(23,"carnelian",UnsagaMaterials.CARNELIAN).setOreDictID("gemCarnelian"));
//		topaz = this.put(new Ingredient(24,"topaz",UnsagaMaterials.TOPAZ).setOreDictID("gemTopaz"));
//		opal = this.put(new Ingredient(25,"opal",UnsagaMaterials.OPAL).setOreDictID("gemOpal"));
//		ravenite = this.put(new Ingredient(26,"ravenite",UnsagaMaterials.RAVENITE).setOreDictID("gemRavenite"));
//		lapis = this.put(new Ingredient(27,"lapis",UnsagaMaterials.LAZULI).setOreDictID("gemLapis"));
//		meteorite = this.put(new Ingredient(28,"meteorite",UnsagaMaterials.METEORITE).setOreDictID("stoneMeteorite"));
//		silver = this.put(new Ingredient(29,"silver",UnsagaMaterials.SILVER).setOreDictID("ingotSilver"));
//		ruby = this.put(new Ingredient(30,"ruby",UnsagaMaterials.RUBY).setOreDictID("gemRuby"));
//		sapphire = this.put(new Ingredient(31,"sapphire",UnsagaMaterials.SAPPHIRE).setOreDictID("gemSapphire"));
//		copper = this.put(new Ingredient(32,"copper",UnsagaMaterials.COPPER).setOreDictID("ingotCopper"));
//		lead = this.put(new Ingredient(33,"lead",UnsagaMaterials.LEAD).setOreDictID("ingotLead"));
//		meteoricIron = this.put(new Ingredient(34,"meteoric_iron",UnsagaMaterials.METEORIC_IRON).setOreDictID("ingotMeteoricIron"));
//		steel1 = this.put(new Ingredient(35,"steel1","steel",UnsagaMaterials.STEEL1).setOreDictID("ingotSteel"));
//		steel2 = this.put(new Ingredient(36,"steel2","steel",UnsagaMaterials.STEEL2).setOreDictID("ingotSteel"));
//		faerieSilver = this.put(new Ingredient(37,"faerie_silver",UnsagaMaterials.FAERIE_SILVER).setOreDictID("ingotFaerieSilver"));
//		damascus = this.put(new Ingredient(38,"damascus",UnsagaMaterials.DAMASCUS).setOreDictID("ingotDamascus"));
//		dragonHeart = this.put(new Ingredient(39,"dragon_heart",UnsagaMaterials.DRAGON_HEART)).setAmount(1.0F);
//		jungleWood = this.put(new Ingredient(40,"jungle_wood",UnsagaMaterials.JUNGLE_WOOD).setItemColored(true).setIconName("woodpile").setOreDictID("woodpile"));
//		birch = this.put(new Ingredient(41,"birch",UnsagaMaterials.BIRCH).setItemColored(true).setIconName("woodpile").setOreDictID("woodpile"));
//		spruce = this.put(new Ingredient(42,"spruce",UnsagaMaterials.SPRUCE).setItemColored(true).setIconName("woodpile").setOreDictID("woodpile"));
//		acacia = this.put(new Ingredient(43,"acacia",UnsagaMaterials.ACACIA).setItemColored(true).setIconName("woodpile").setOreDictID("woodpile"));
//		darkOak = this.put(new Ingredient(44,"dark_oak",UnsagaMaterials.DARK_OAK).setItemColored(true).setIconName("woodpile").setOreDictID("woodpile"));
	}

//	private static final Map<UnsagaMaterial,Ingredient> ingredientList = new HashMap<>();
//
	public static final ResourceLocation INGREDIENTS = new ResourceLocation(UnsagaMod.MODID,"ingredients");

//	@SubscribeEvent
//	public void makeRegistry(RegistryEvent.NewRegistry ev){
//		new RegistryBuilder().setName(INGREDIENTS).setType(Ingredient.class)
//		.setIDRange(0, 4096).create();
//	}

	public static enum IngredientType{
		COTTON("cotton"){
			@Override
			public String oreID(){
				return "cloth";
			}
		},SILK("silk"){
			@Override
			public String oreID(){
				return "cloth";
			}
		},VELVET("velvet"){
			@Override
			public String oreID(){
				return "cloth";
			}
		},LIVE_SILK("live_silk"){
			@Override
			public String oreID(){
				return "cloth";
			}
		},FUR("fur")
		,SNAKE_LEATHER("snake_leather"){
			@Override
			public String oreID(){
				return "leather";
			}
		},HYDRA_LEATHER("hydra_leather"){
			@Override
			public String oreID(){
				return "leather";
			}
		}
		,CROCODILE_LEATHER("crocodile_leather"){
			@Override
			public String oreID(){
				return "leather";
			}
		},CYPRESS("cypress"){
			@Override
			public String oreID(){
				return "woodpile";
			}
		},OAK("oak"){
			@Override
			public String oreID(){
				return "woodpile";
			}
		}
		,TONERIKO("toneriko"){
			@Override
			public String oreID(){
				return "woodpile";
			}
		},TUSK1("tusk1"),TUSK2("tusk2")
		,BONE2("bone"),THIN_SCALE("thin_scale"),CHITIN("chitin")
		,ANCIENT_FISH_SCALE("ancient_fish_scale"),DRAGON_SCALE("dragon_scale")
		,LIGHT_STONE("light_stone"){
			@Override
			public String oreID(){
				return "gemLightStone";
			}
		},DARK_STONE("dark_stone"){
			@Override
			public String oreID(){
				return "gemDarkStone";
			}
		},DEBRIS1("debris1"){
			@Override
			public String oreID(){
				return "debris";
			}

			@Override
			public float amount(){
				return 0.125F;
			}
		}
		,DEBRIS2("debris2"){
			@Override
			public String oreID(){
				return "debris";
			}

			@Override
			public float amount(){
				return 0.125F;
			}
		},CARNELIAN("carnelian"){
			@Override
			public String oreID(){
				return "gemCarnelian";
			}
		},TOPAZ("topaz"){
			@Override
			public String oreID(){
				return "gemTopaz";
			}
		},OPAL("opal"){
			@Override
			public String oreID(){
				return "gemOpal";
			}
		},RAVENITE("ravenite"){
			@Override
			public String oreID(){
				return "gemRavenite";
			}
		},LAPIS("lapis"){
			@Override
			public String oreID(){
				return "gemLapis";
			}
		}
		,METEORITE("meteorite"){
			@Override
			public String oreID(){
				return "stoneMeteorite";
			}
		},SILVER("silver"){
			@Override
			public String oreID(){
				return "ingotSilver";
			}
		},RUBY("ruby"){
			@Override
			public String oreID(){
				return "gemRuby";
			}
		},SAPPHIRE("sapphire"){
			@Override
			public String oreID(){
				return "gemSapphire";
			}
		},COPPER("copper"){
			@Override
			public String oreID(){
				return "ingotCopper";
			}
		},LEAD("lead"){
			@Override
			public String oreID(){
				return "ingotLead";
			}
		}
		,METEORITC_IRON("meteoric_iron"){
			@Override
			public String oreID(){
				return "ingotMeteoricIron";
			}
		},STEEL1("steel1"){
			@Override
			public String oreID(){
				return "ingotSteel";
			}
		},STEEL2("steel2"){
			@Override
			public String oreID(){
				return "ingotSteel";
			}
		}
		,FAREIE_SILVER("faerie_silver"){
			@Override
			public String oreID(){
				return "ingotFaerieSilver";
			}
		},DAMASCUS("damascus"){
			@Override
			public String oreID(){
				return "ingotDamascus";
			}
		},DRAGON_HEART("dragon_heart")
		,JUNGLE_WOOD("jungle_wood"){
			@Override
			public String oreID(){
				return "woodpile";
			}
		},BIRCH("birch"){
			@Override
			public String oreID(){
				return "woodpile";
			}
		},SPRUCE("spruce"){
			@Override
			public String oreID(){
				return "woodpile";
			}
		}
		,ACACIA("acacia"){
			@Override
			public String oreID(){
				return "woodpile";
			}
		},DARK_OAK("dark_oak"){
			@Override
			public String oreID(){
				return "woodpile";
			}
		},VANILLA_ITEM("vanilla"){
			@Override
			public Optional<Item> getItem(UnsagaMaterial material){
				return Optional.ofNullable(materialItemStackMap.get(material))
						.map(in -> in.first().getItem());
			}
		};

		private IngredientType(String name) {
			this.name = name;
		}

		final String name;

		public String getName(){
			return this.name;
		}


		public Optional<Integer> burnTime(){
			if(this.oreID().equals("woodpile")){
				return Optional.of(80);
			}
			return Optional.empty();
		}

		public float amount(){
			return AMOUNT_HALF;
		}
		public String oreID(){
			return StringUtil.EMPTY_STRING;
		}

		public boolean isRequiredItemColored(){
			return this.oreID().equals("woodpile");
		}
		public Optional<Item> getItem(UnsagaMaterial material){
			return UnsagaItemRegisterer.fromIngredient(this);
		}

		public @Nullable Item getIngredientItem(){
			return UnsagaItemRegisterer.fromIngredient(this).orElse(null);
		}
		public ItemStack getIngredientStack(int amount){
			return Optional.of(getIngredientItem())
					.map(in -> new ItemStack(in,amount))
					.orElse(ItemStack.EMPTY);
		}


		public static Collection<IngredientType> all(){
			return Lists.newArrayList(IngredientType.values());
		}
	}


	static BiMap<IngredientType,UnsagaMaterial> ingredientMaterialMap;
	static Map<UnsagaMaterial,Pair<ItemStack,Float>> materialItemStackMap;
	public void integrate(){
		Map<IngredientType,UnsagaMaterial> map = new HashMap<>();
		map.put(IngredientType.COTTON, UnsagaMaterials.COTTON);
		map.put(IngredientType.SILK, UnsagaMaterials.SILK);
		map.put(IngredientType.VELVET, UnsagaMaterials.VELVET);
		map.put(IngredientType.LIVE_SILK, UnsagaMaterials.LIVE_SILK);
		map.put(IngredientType.FUR, UnsagaMaterials.FUR);
		map.put(IngredientType.SNAKE_LEATHER, UnsagaMaterials.SNAKE_LEATHER);
		map.put(IngredientType.HYDRA_LEATHER, UnsagaMaterials.HYDRA_LEATHER);
		map.put(IngredientType.CROCODILE_LEATHER, UnsagaMaterials.CROCODILE_LEATHER);
		map.put(IngredientType.CYPRESS, UnsagaMaterials.CYPRESS);
		map.put(IngredientType.OAK, UnsagaMaterials.OAK);
		map.put(IngredientType.TONERIKO, UnsagaMaterials.TONERIKO);
		map.put(IngredientType.TUSK1, UnsagaMaterials.TUSK1);
		map.put(IngredientType.TUSK2, UnsagaMaterials.TUSK2);
		map.put(IngredientType.BONE2, UnsagaMaterials.BONE2);
		map.put(IngredientType.THIN_SCALE, UnsagaMaterials.THIN_SCALE);
		map.put(IngredientType.CHITIN, UnsagaMaterials.CHITIN);
		map.put(IngredientType.ANCIENT_FISH_SCALE, UnsagaMaterials.ANCIENT_FISH_SCALE);
		map.put(IngredientType.DRAGON_SCALE, UnsagaMaterials.DRAGON_SCALE);
		map.put(IngredientType.LIGHT_STONE, UnsagaMaterials.LIGHT_STONE);
		map.put(IngredientType.DARK_STONE, UnsagaMaterials.DARK_STONE);
		map.put(IngredientType.DEBRIS1, UnsagaMaterials.DEBRIS1);
		map.put(IngredientType.DEBRIS2, UnsagaMaterials.DEBRIS2);
		map.put(IngredientType.CARNELIAN, UnsagaMaterials.CARNELIAN);
		map.put(IngredientType.OPAL, UnsagaMaterials.OPAL);
		map.put(IngredientType.TOPAZ, UnsagaMaterials.TOPAZ);
		map.put(IngredientType.RAVENITE, UnsagaMaterials.RAVENITE);
		map.put(IngredientType.LAPIS, UnsagaMaterials.LAZULI);
		map.put(IngredientType.METEORITE, UnsagaMaterials.METEORITE);
		map.put(IngredientType.SILVER, UnsagaMaterials.SILVER);
		map.put(IngredientType.RUBY, UnsagaMaterials.RUBY);
		map.put(IngredientType.SAPPHIRE, UnsagaMaterials.SAPPHIRE);
		map.put(IngredientType.COPPER, UnsagaMaterials.COPPER);
		map.put(IngredientType.LEAD, UnsagaMaterials.LEAD);
		map.put(IngredientType.METEORITC_IRON, UnsagaMaterials.METEORIC_IRON);
		map.put(IngredientType.STEEL1, UnsagaMaterials.STEEL1);
		map.put(IngredientType.STEEL2, UnsagaMaterials.STEEL2);
		map.put(IngredientType.FAREIE_SILVER, UnsagaMaterials.FAERIE_SILVER);
		map.put(IngredientType.DAMASCUS, UnsagaMaterials.DAMASCUS);
		map.put(IngredientType.DRAGON_HEART, UnsagaMaterials.DRAGON_HEART);
		map.put(IngredientType.JUNGLE_WOOD, UnsagaMaterials.JUNGLE_WOOD);
		map.put(IngredientType.BIRCH, UnsagaMaterials.BIRCH);
		map.put(IngredientType.SPRUCE, UnsagaMaterials.SPRUCE);
		map.put(IngredientType.ACACIA, UnsagaMaterials.ACACIA);
		map.put(IngredientType.DARK_OAK, UnsagaMaterials.DARK_OAK);

		ingredientMaterialMap = HashBiMap.create(map);
//		map.put(EnumIngredient.VANILLA_ITEM, UnsagaMaterials.FEATHER);
//		map.put(EnumIngredient.VANILLA_ITEM, UnsagaMaterials.WOOD);
//		map.put(EnumIngredient.VANILLA_ITEM, UnsagaMaterials.BONE1);
//		map.put(EnumIngredient.VANILLA_ITEM, UnsagaMaterials.STONE);
//		map.put(EnumIngredient.VANILLA_ITEM, UnsagaMaterials.QUARTZ);
//		map.put(EnumIngredient.VANILLA_ITEM, UnsagaMaterials.OBSIDIAN);
//		map.put(EnumIngredient.VANILLA_ITEM, UnsagaMaterials.IRON);
//		map.put(EnumIngredient.VANILLA_ITEM, UnsagaMaterials.IRON_ORE);
//		map.put(EnumIngredient.VANILLA_ITEM, UnsagaMaterials.DIAMOND);
//		map.put(EnumIngredient.VANILLA_ITEM, UnsagaMaterials.PRISMARINE);
//		map.put(EnumIngredient.VANILLA_ITEM, UnsagaMaterials.SHULKER);

		Map<UnsagaMaterial,Pair<ItemStack,Float>> materialItemMap = new HashMap<>();
		float half = AMOUNT_HALF;
		materialItemMap.put(UnsagaMaterials.FEATHER, Pair.of(new ItemStack(Items.FEATHER), half));
		materialItemMap.put(UnsagaMaterials.BONE1, Pair.of(new ItemStack(Items.BONE), 0.1F));
		materialItemMap.put(UnsagaMaterials.QUARTZ, Pair.of(new ItemStack(Items.QUARTZ),half));
		materialItemMap.put(UnsagaMaterials.OBSIDIAN, Pair.of(new ItemStack(Blocks.OBSIDIAN), half));
		materialItemMap.put(UnsagaMaterials.IRON, Pair.of(new ItemStack(Items.IRON_INGOT), half));
		materialItemMap.put(UnsagaMaterials.IRON_ORE, Pair.of(new ItemStack(Blocks.IRON_ORE), half));
		materialItemMap.put(UnsagaMaterials.DIAMOND, Pair.of(new ItemStack(Items.DIAMOND), half));
		materialItemMap.put(UnsagaMaterials.PRISMARINE, Pair.of(new ItemStack(Items.PRISMARINE_SHARD), half));
		materialItemMap.put(UnsagaMaterials.SHULKER, Pair.of(new ItemStack(Items.SHULKER_SHELL), half));
		materialItemMap.put(UnsagaMaterials.SERPENTINE, Pair.of(BlockUnsagaCube.Type.SERPENTINE.getStack(1), half));
		materialItemMap.put(UnsagaMaterials.COPPER_ORE, Pair.of(new ItemStack(UnsagaOres.COPPER.oreBlock()), half));

		map.forEach((ingre,m)->{
			materialItemMap.put(m, Pair.of(ingre.getIngredientStack(1),ingre.amount()));
		});
		materialItemStackMap = ImmutableMap.copyOf(materialItemMap);
	}

	public static int getMaterialColorFrom(Item m){
		return Optional.ofNullable(ingredientMaterialMap.get(m))
				.map(in -> in.materialColor())
				.orElse(Statics.COLOR_NONE);
	}
	private void putIngrediet(IngredientType ingre,UnsagaMaterial m){
		Map<UnsagaMaterial,IngredientType> map = new HashMap<>();

	}
//	@SubscribeEvent(priority = EventPriority.LOWEST)
//	public void registerMaterials(RegistryEvent.Register<UnsagaMaterial> ev){
//
//		List<IngredientBuilder> list = new ArrayList<>();
//		list.add(new IngredientBuilder(1,"cotton",UnsagaMaterials.COTTON).setOreDictID("cloth").setIconName("cloth"));
//		list.add(new IngredientBuilder(2,"silk",UnsagaMaterials.SILK).setOreDictID("cloth").setIconName("cloth"));
//		list.add(new IngredientBuilder(3,"velvet",UnsagaMaterials.VELVET).setOreDictID("cloth").setIconName("cloth"));
//		list.add(new IngredientBuilder(4,"live_silk",UnsagaMaterials.LIVE_SILK).setOreDictID("cloth").setIconName("cloth"));
//		list.add(new IngredientBuilder(5,"fur",UnsagaMaterials.FUR));
//		list.add(new IngredientBuilder(6,"snake_leather",UnsagaMaterials.SNAKE_LEATHER).setOreDictID("leather"));
//		list.add(new IngredientBuilder(7,"hydra_leather",UnsagaMaterials.HYDRA_LEATHER).setOreDictID("leather"));
//		list.add(new IngredientBuilder(8,"crocodile_leather",UnsagaMaterials.CROCODILE_LEATHER).setOreDictID("leather"));
//		list.add(new IngredientBuilder(9,"cypress",UnsagaMaterials.CYPRESS).setItemColored(true).setIconName("woodpile").setOreDictID("woodpile"));
//		list.add(new IngredientBuilder(10,"oak",UnsagaMaterials.OAK).setItemColored(true).setIconName("woodpile").setOreDictID("woodpile"));
//		list.add(new IngredientBuilder(11,"toneriko",UnsagaMaterials.TONERIKO).setItemColored(true).setIconName("woodpile").setOreDictID("woodpile"));
//		list.add(new IngredientBuilder(12,"tusk1","tusk",UnsagaMaterials.TUSK1));
//		list.add(new IngredientBuilder(13,"tusk2","tusk",UnsagaMaterials.TUSK2));
//		list.add(new IngredientBuilder(14,"bone",UnsagaMaterials.BONE2).setOreDictID("bone"));
//		list.add(new IngredientBuilder(15,"thin_scale",UnsagaMaterials.THIN_SCALE));
//		list.add(new IngredientBuilder(16,"chitin",UnsagaMaterials.CHITIN));
//		list.add(new IngredientBuilder(17,"ancient_fish_scale",UnsagaMaterials.ANCIENT_FISH_SCALE).setOreDictID("ancientFishScale"));
//		list.add(new IngredientBuilder(18,"dragon_scale",UnsagaMaterials.DRAGON_SCALE));
//		list.add(new IngredientBuilder(19,"light_stone",UnsagaMaterials.LIGHT_STONE).setOreDictID("gemLightStone"));
//		list.add(new IngredientBuilder(20,"dark_stone",UnsagaMaterials.DARK_STONE).setOreDictID("gemDarkStone"));
//		list.add(new IngredientBuilder(21,"debris1","debris",UnsagaMaterials.DEBRIS1).setOreDictID("debris").setAmount(0.125F));
//		list.add(new IngredientBuilder(22,"debris2","debris",UnsagaMaterials.DEBRIS2).setOreDictID("debris").setAmount(0.125F));
//		list.add(new IngredientBuilder(23,"carnelian",UnsagaMaterials.CARNELIAN).setOreDictID("gemCarnelian"));
//		list.add(new IngredientBuilder(24,"topaz",UnsagaMaterials.TOPAZ).setOreDictID("gemTopaz"));
//		list.add(new IngredientBuilder(25,"opal",UnsagaMaterials.OPAL).setOreDictID("gemOpal"));
//		list.add(new IngredientBuilder(26,"ravenite",UnsagaMaterials.RAVENITE).setOreDictID("gemRavenite"));
//		list.add(new IngredientBuilder(27,"lapis",UnsagaMaterials.LAZULI).setOreDictID("gemLapis"));
//		list.add(new IngredientBuilder(28,"meteorite",UnsagaMaterials.METEORITE).setOreDictID("stoneMeteorite"));
//		list.add(new IngredientBuilder(29,"silver",UnsagaMaterials.SILVER).setOreDictID("ingotSilver"));
//		list.add(new IngredientBuilder(30,"ruby",UnsagaMaterials.RUBY).setOreDictID("gemRuby"));
//		list.add(new IngredientBuilder(31,"sapphire",UnsagaMaterials.SAPPHIRE).setOreDictID("gemSapphire"));
//		list.add(new IngredientBuilder(32,"copper",UnsagaMaterials.COPPER).setOreDictID("ingotCopper"));
//		list.add(new IngredientBuilder(33,"lead",UnsagaMaterials.LEAD).setOreDictID("ingotLead"));
//		list.add(new IngredientBuilder(34,"meteoric_iron",UnsagaMaterials.METEORIC_IRON).setOreDictID("ingotMeteoricIron"));
//		list.add(new IngredientBuilder(35,"steel1","steel",UnsagaMaterials.STEEL1).setOreDictID("ingotSteel"));
//		list.add(new IngredientBuilder(36,"steel2","steel",UnsagaMaterials.STEEL2).setOreDictID("ingotSteel"));
//		list.add(new IngredientBuilder(37,"faerie_silver",UnsagaMaterials.FAERIE_SILVER).setOreDictID("ingotFaerieSilver"));
//		list.add(new IngredientBuilder(38,"damascus",UnsagaMaterials.DAMASCUS).setOreDictID("ingotDamascus"));
//		list.add(new IngredientBuilder(39,"dragon_heart",UnsagaMaterials.DRAGON_HEART).setAmount(1.0F));
//		list.add(new IngredientBuilder(40,"jungle_wood",UnsagaMaterials.JUNGLE_WOOD).setItemColored(true).setIconName("woodpile").setOreDictID("woodpile"));
//		list.add(new IngredientBuilder(41,"birch",UnsagaMaterials.BIRCH).setItemColored(true).setIconName("woodpile").setOreDictID("woodpile"));
//		list.add(new IngredientBuilder(42,"spruce",UnsagaMaterials.SPRUCE).setItemColored(true).setIconName("woodpile").setOreDictID("woodpile"));
//		list.add(new IngredientBuilder(43,"acacia",UnsagaMaterials.ACACIA).setItemColored(true).setIconName("woodpile").setOreDictID("woodpile"));
//		list.add(new IngredientBuilder(44,"dark_oak",UnsagaMaterials.DARK_OAK).setItemColored(true).setIconName("woodpile").setOreDictID("woodpile"));
//
//		RegistryUtil.Helper<Ingredient> helper = RegistryUtil.helper(ev, UnsagaMod.MODID, "unsaga");
//
//		list.forEach(in ->{
//			helper.register(in.build());
//		});
//	}

	protected void associate(){

	}

	protected static void validateMerchandise(){
		UnsagaMaterialInitializer.getAllMaterials().stream()
		.filter(in -> in.isMerchandise())
		.forEach(in ->{
			Preconditions.checkArgument(!in.itemStack().isEmpty());
		});
	}
	protected static void registerOreDicts(){
		IngredientType.all().forEach(in -> {
			if(!in.oreID().isEmpty() && !in.getIngredientStack(1).isEmpty()){
				OreDictionary.registerOre(in.oreID(), in.getIngredientStack(1));
			}
		});
		OreDictionary.registerOre("gemBestial", UnsagaMaterials.CARNELIAN.itemStack());
		OreDictionary.registerOre("gemBestial",UnsagaMaterials.OPAL.itemStack());
		OreDictionary.registerOre("gemBestial", UnsagaMaterials.TOPAZ.itemStack());
		OreDictionary.registerOre("gemBestial", UnsagaMaterials.RAVENITE.itemStack());
		OreDictionary.registerOre("gemBestial", UnsagaMaterials.LAZULI.itemStack());



	}


//	public static class Ingredient extends IForgeRegistryEntry.Impl<Ingredient> implements IIconName,Comparable<Ingredient>{
//
//		final UnsagaMaterial m;
//		final boolean isItemColor;
//		final String iconname;
//		final float amount;
//		final Optional<String> oreDict;
//		public Ingredient(IngredientBuilder builder) {
//			this.isItemColor = builder.isItemColor;
//			this.m = builder.m;
//			this.iconname = builder.iconname;
//			this.amount = builder.amount;
//			this.oreDict = builder.oreDict;
//			this.setRegistryName(new ResourceLocation(UnsagaMod.MODID,builder.name));
//		}
//
//		public Optional<String> oreID() {
//			// TODO 自動生成されたメソッド・スタブ
//			return this.oreDict;
//		}
//
//
//		public boolean isRequiredItemColor(){
//			return this.isItemColor;
//		}
//
//
//		/** ツール生成時の耐久力の割合。初期値は0.5（半分）*/
//		public float amount(){
//			return this.amount;
//		}
//
//		public UnsagaMaterial material(){
//			return this.m;
//		}
//
//		public Item getItem() {
//			return UnsagaItemRegisterer.toItem(this);
//		}
//
//		public ItemStack createStack(int amount){
//			return new ItemStack(this.getItem(),amount);
//		}
//
//		@Override
//		public int getMeta() {
//			// TODO 自動生成されたメソッド・スタブ
//			return 0;
//		}
//
//		@Override
//		public String iconName() {
//			// TODO 自動生成されたメソッド・スタブ
//			return this.iconname;
//		}
//
//		@Override
//		public int compareTo(Ingredient o) {
//			return Integer.valueOf(this.material().rank()).compareTo(((Ingredient) o).material().rank());
//		}
//	}
//
//	public static class IngredientBuilder{
//
//		Optional<String> oreDict;
//		String name;
//		UnsagaMaterial m;
//		boolean isItemColor = false;
//		String iconname;
//		float amount = 0.5F;
//		public IngredientBuilder(int id, String name,UnsagaMaterial m) {
//			this(id,name,name,m);
//			// TODO 自動生成されたコンストラクター・スタブ
//		}
//
//
//		public IngredientBuilder setIconName(String string) {
//			// TODO 自動生成されたメソッド・スタブ
//			this.iconname = string;
//			return this;
//		}
//
//		public IngredientBuilder setOreDictID(String string) {
//			// TODO 自動生成されたメソッド・スタブ
//			this.oreDict = Optional.of(string);
//			return this;
//		}
//
//		public IngredientBuilder(int id, String name,String unlname,UnsagaMaterial m) {
////			super(id, name,unlname);
//			this.name = name;
//			this.m = m;
//			this.iconname = unlname;
//			// TODO 自動生成されたコンストラクター・スタブ
//		}
//
//		public IngredientBuilder setItemColored(boolean par1){
//			this.isItemColor = par1;
//			return this;
//		}
//
//		public IngredientBuilder setAmount(float par1){
//			this.amount = par1;
//			return this;
//		}
//
//
//		public Ingredient build(){
//			return new Ingredient(this);
//		}
//	}
}
