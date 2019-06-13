package mods.hinasch.unsaga.material;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;

import com.google.common.base.Preconditions;

import mods.hinasch.unsaga.core.block.BlockUnsagaCube;
import mods.hinasch.unsaga.init.UnsagaLibrary;
import mods.hinasch.unsaga.init.UnsagaOres;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * CatalogBlacksmithと統合したほうがいい…？
 * */
public class DictionaryUnsagaMaterial {


	private static DictionaryUnsagaMaterial INSTANCE;
	public static DictionaryUnsagaMaterial instance(){
		if(INSTANCE == null){
			INSTANCE = new DictionaryUnsagaMaterial();
		}
		return INSTANCE;
	}

	@Deprecated
	public static Optional<UnsagaMaterial> lookUpMaterial(ItemStack is){
		return instance().dictionaryMaterial.entrySet().stream()
				.filter(in -> in.getValue().isItemEqual(is))
				.map(in -> in.getKey())
				.findFirst();
	}

	@Deprecated
	public static OptionalDouble lookUpRepairAmount(ItemStack other){
		if(UnsagaLibrary.CATALOG_MATERIAL.find(other).isPresent()){
			return OptionalDouble.of(UnsagaLibrary.CATALOG_MATERIAL.find(other).get().getAmount());
		}
		return instance().dictionaryRepairAmount.entrySet()
				.stream()
				.filter(in -> in.getKey().isItemEqual(other))
				.mapToDouble(in -> in.getValue())
				.findFirst();
	}

	@Deprecated
	public static ItemStack lookUpStack(UnsagaMaterial m){
		return Optional.ofNullable(instance().dictionaryMaterial.get(m))
				.orElse(ItemStack.EMPTY);
	}

	Map<UnsagaMaterial,ItemStack> dictionaryMaterial = new HashMap<>();
	Map<ItemStack,Float> dictionaryRepairAmount = new HashMap<>();
//	UnsagaMaterialRegistry materials = UnsagaMaterialRegistry.instance();

	protected DictionaryUnsagaMaterial(){

	}


	public void addItem(UnsagaMaterial m,ItemStack is){
		dictionaryMaterial.put(m, is);
	}
	protected void addItem(UnsagaMaterial m,ItemStack is,float amount){
		Preconditions.checkNotNull(is);
		dictionaryMaterial.put(m, is);
		this.dictionaryRepairAmount.put(is, amount);
	}

	public static void associateToMaterial(){
//		UnsagaIngredients.EnumIngredient.all().forEach(in ->{
//			ItemStack is = in.getIngredientItem();
//			instance().addItem(in.material(), is);
//			instance().dictionaryRepairAmount.put(is,in.amount());
//		});
	}
	protected void addVanillaThings(){

		addItem(UnsagaMaterials.FEATHER, new ItemStack(Items.FEATHER,1),0.5F);
		addItem(UnsagaMaterials.WOOD, new ItemStack(Items.STICK,1),0.1F);
		addItem(UnsagaMaterials.BONE1, new ItemStack(Items.BONE,1),0.5F);
		addItem(UnsagaMaterials.QUARTZ, new ItemStack(Items.QUARTZ,1),0.5F);
		addItem(UnsagaMaterials.OBSIDIAN, new ItemStack(Blocks.OBSIDIAN,1),0.5F);
		addItem(UnsagaMaterials.IRON, new ItemStack(Items.IRON_INGOT,1),0.5F);
		addItem(UnsagaMaterials.IRON_ORE, new ItemStack(Blocks.IRON_ORE,1),0.5F);
		addItem(UnsagaMaterials.DIAMOND, new ItemStack(Items.DIAMOND,1),0.5F);
		addItem(UnsagaMaterials.PRISMARINE, new ItemStack(Items.PRISMARINE_SHARD,1),0.5F);
		addItem(UnsagaMaterials.SHULKER, new ItemStack(Items.SHULKER_SHELL,1),0.5F);

		OreDictionary.registerOre("ancientFishScale", new ItemStack(Items.PRISMARINE_SHARD));
	}
	public void init(){
		this.addVanillaThings();
		this.registerOthers();
		this.validate();
	}


	protected void registerOreDictionary(){

	}
	protected void registerOthers(){
		addItem(UnsagaMaterials.SERPENTINE, BlockUnsagaCube.Type.SERPENTINE.getStack(1),0.5F);
		addItem(UnsagaMaterials.COPPER_ORE, new ItemStack(UnsagaOres.COPPER.oreBlock(),1),0.5F);
	}
	protected void validate(){
		UnsagaMaterialInitializer.getMerchandiseMaterials().stream().forEach(in ->{
//			UnsagaMod.logger.trace("register", in);
			Preconditions.checkNotNull(dictionaryMaterial.get(in),in);
			ItemStack is = dictionaryMaterial.get(in);
//			Preconditions.checkArgument(lookUpRepairAmount(is).isPresent());
		});
	}
}
