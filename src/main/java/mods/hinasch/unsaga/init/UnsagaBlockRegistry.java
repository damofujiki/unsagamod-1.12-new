package mods.hinasch.unsaga.init;

import javax.annotation.Nullable;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;

import mods.hinasch.lib.block.BlockOreBase;
import mods.hinasch.lib.registry.RegistryUtil;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.block.BlockUnsagaCube;
import mods.hinasch.unsaga.core.item.UnsagaCreativeTabs;
import mods.hinasch.unsaga.init.UnsagaOreRegistry.OreUnsaga;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.oredict.OreDictionary;

public class UnsagaBlockRegistry{

//	public RegistrySimple<Integer,Block> ores = new RegistrySimple();
//	public List<ResourceLocation> queue = Lists.newArrayList();
//	public RegistrySimple<BlockUnsagaCube.Type,Block> stones = new RegistrySimple();
//	public Block bonusChest;
//	public Block stonesAndMetals;

	private BiMap<UnsagaOreRegistry.OreUnsaga,Block> oresMapping = HashBiMap.create();
	private BiMap<BlockUnsagaCube.Type,Block> cubesMapping = HashBiMap.create();


	protected static UnsagaBlockRegistry INSTANCE;

	public static UnsagaBlockRegistry instance(){
		if(INSTANCE == null){
			INSTANCE = new UnsagaBlockRegistry();
		}
		return INSTANCE;
	}

	protected UnsagaBlockRegistry() {
//		super(UnsagaMod.MODID);
//		this.setUnlocalizedNamePrefix("unsaga");
	}

	@SubscribeEvent
	public void registerItemBlocks(RegistryEvent.Register<Item> ev){
		RegistryUtil.Helper<Item> helper = RegistryUtil.helper(ev, UnsagaMod.MODID, "unsaga");

//		ForgeRegistries.BLOCKS.get
		oresMapping.forEach((ore,block) ->helper.registerItemBlock(block));
		cubesMapping.forEach((stone,block) ->helper.registerItemBlock(block));
	}

	@SubscribeEvent
	public void register(RegistryEvent.Register<Block> ev) {
		RegistryUtil.Helper<Block> helper = RegistryUtil.helper(ev, UnsagaMod.MODID, "unsaga");

		UnsagaOres.ALL_ORES.forEach(input ->{
			Block block = new BlockOreBase(input, () -> UnsagaMod.secureRandom);
//			Block block = put(new BlockOreBase(UnsagaOreRegistry.getOre(input.getId()), () -> UnsagaMod.secureRandom){}
//					,input.getPropertyName(),UnsagaCreativeTabs.MISC,ev);
			helper.registerBlock(block, input.name(), UnsagaCreativeTabs.MISC);
//			ores.putObject(input.getId(), block);
			oresMapping.put(input, block);
//			queue.add()
		});

//		bonusChest = put(new BlockUnsagaChest().setHardness(Statics.HARDNESS_COBBLE_STONE),"bonusChest",CreativeTabsUnsaga.tabUnsaga);
//		stonesAndMetals = put(new BlockUnsagaStone(Material.ROCK).setHardness(1.5F),"stones_and_metals",UnsagaCreativeTabs.MISC
//				,block ->{
//					ItemBlock item = new ItemMultiTexture(block, block, is ->{
//						return BlockUnsagaStone.EnumType.fromMeta(is.getItemDamage()).getUnlocalizedName();
//					});
//
//					Arrays.stream(BlockUnsagaStone.EnumType.values()).forEach(in ->{
//						item.addPropertyOverride(new ResourceLocation(in.getName()), new BlockVariantGetter(in));
//					});
//
//					return item;
//				}
//		);

		Lists.newArrayList(BlockUnsagaCube.Type.values())
		.forEach(in ->{
			Block block = new BlockUnsagaCube(in);
			helper.registerBlock(block, in.getUnlocalizedName(), UnsagaCreativeTabs.MISC);
//			Block block = put(new BlockUnsagaCube(in),in.getUnlocalizedName(),UnsagaCreativeTabs.MISC,ev);
//			stones.putObject(in, block);
			this.cubesMapping.put(in, block);
		});

//		GameRegistry.registerTileEntity(TileEntityUnsagaChest.class, UnsagaMod.MODID+":chest");
	}

	/**
	 * レシピや鉱石辞書へブロック登録。
	 */
	public void registerOreDicts(){
		OreDictionary.registerOre("oreSerpentine", BlockUnsagaCube.Type.SERPENTINE.getStack(1));
		OreDictionary.registerOre("stoneSerpentine", BlockUnsagaCube.Type.SERPENTINE.getStack(1));
		OreDictionary.registerOre("blockLead", BlockUnsagaCube.Type.LEAD.getStack(1));
		OreDictionary.registerOre("blockDamascus", BlockUnsagaCube.Type.DAMASCUS.getStack(1));
		OreDictionary.registerOre("blockFaerieSilver", BlockUnsagaCube.Type.FAERIE_SILVER.getStack(1));
		OreDictionary.registerOre("blockSilver", BlockUnsagaCube.Type.SILVER.getStack(1));
		OreDictionary.registerOre("blockMeteoricIron", BlockUnsagaCube.Type.METEORIC_IRON.getStack(1));
		OreDictionary.registerOre("blockCopper", BlockUnsagaCube.Type.COPPER.getStack(1));
		OreDictionary.registerOre("blockSteel", BlockUnsagaCube.Type.STEEL.getStack(1));
		OreDictionary.registerOre("blockSteel", BlockUnsagaCube.Type.STEEL2.getStack(1));
		OreDictionary.registerOre("blockMeteorite", BlockUnsagaCube.Type.METEORITE.getStack(1));


//		RecipeUtilNew.RecipeShaped.create().setBase(RecipeUtilNew.Recipes.CUBE_MIDDLE)
//		.addAssociation('B', BlockUnsagaStone.EnumType.SERPENTINE.getStack(1)).setOutput(BlockUnsagaStone.EnumType.SERPENTINE_SMOOTH.getStack(4))
//		.register();
//		RecipeUtilNew.RecipeShapeless shapeless = new RecipeUtilNew.RecipeShapeless();
//
//		for(BlockUnsagaStone.EnumType type:BlockUnsagaStone.EnumType.values()){
//			if(type.getBaseItem()!=null){
//				RecipeUtilNew.RecipeShaped.create().setBase(RecipeUtilNew.Recipes.FILLED)
//				.addAssociation('B', type.getBaseItem()).setOutput(type.getStack(1))
//				.register();
////				shaped.setOutput(type.getStack(1));
////				shaped.setBase(RecipeUtilNew.Recipes.FILLED);
////				shaped.addAssociation('B', type.getBaseItem());
////				shaped.register();
////				shaped.clear();
//				RecipeUtilNew.RecipeShaped.create().setBase("S")
//				.addAssociation('S',type.getStack(1)).setOutput(type.getBaseItem(9))
//				.register();
//
//			}
//
//		}


	}

	public static @Nullable OreUnsaga getOre(Block block){
		return instance().oresMapping.inverse().get(block);
	}

	public static Block getBlock(OreUnsaga ore){
		return instance().oresMapping.getOrDefault(ore,Blocks.AIR);
	}

	public static @Nullable BlockUnsagaCube.Type getStone(Block block){
		return instance().cubesMapping.inverse().get(block);
	}

	public static Block getBlock(BlockUnsagaCube.Type ore){
		return instance().cubesMapping.getOrDefault(ore,Blocks.AIR);
	}
	public static class BlockVariantGetter implements IItemPropertyGetter{

		BlockUnsagaCube.Type type;
		public BlockVariantGetter(BlockUnsagaCube.Type type){
			this.type = type;
		}
		@Override
		public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {
			if(stack.getItemDamage()==type.getMeta()){
				return 1.0F;
			}
			return 0;
		}

	}
}
