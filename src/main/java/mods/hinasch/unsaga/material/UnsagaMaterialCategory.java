package mods.hinasch.unsaga.material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class UnsagaMaterialCategory{
	private static Map<UnsagaMaterial,UnsagaMaterialCategory> parentMap = new HashMap<>();
	public static final UnsagaMaterialCategory BESTIALS = new UnsagaMaterialCategory("bestials",UnsagaMaterials.CARNELIAN,UnsagaMaterials.TOPAZ,UnsagaMaterials.RAVENITE,UnsagaMaterials.LAZULI,UnsagaMaterials.OPAL);
	public static final UnsagaMaterialCategory DEBRIS = new UnsagaMaterialCategory("debris",UnsagaMaterials.DEBRIS1,UnsagaMaterials.DEBRIS2);
	public static final UnsagaMaterialCategory WOODS = new UnsagaMaterialCategory("woods",UnsagaMaterials.CYPRESS,UnsagaMaterials.OAK,UnsagaMaterials.TONERIKO,UnsagaMaterials.JUNGLE_WOOD
			,UnsagaMaterials.BIRCH,UnsagaMaterials.SPRUCE,UnsagaMaterials.ACACIA,UnsagaMaterials.DARK_OAK);
	public static final UnsagaMaterialCategory TUSKS = new UnsagaMaterialCategory("tusks",UnsagaMaterials.TUSK1,UnsagaMaterials.TUSK2);
	public static final UnsagaMaterialCategory BONES = new UnsagaMaterialCategory("bones",UnsagaMaterials.BONE1,UnsagaMaterials.BONE2);
	public static final UnsagaMaterialCategory SCALES = new UnsagaMaterialCategory("scales",UnsagaMaterials.THIN_SCALE,UnsagaMaterials.CHITIN,UnsagaMaterials.ANCIENT_FISH_SCALE,UnsagaMaterials.DRAGON_SCALE,UnsagaMaterials.PRISMARINE,UnsagaMaterials.SHULKER);
	public static final UnsagaMaterialCategory ROCKS = new UnsagaMaterialCategory("rocks",UnsagaMaterials.SERPENTINE,UnsagaMaterials.QUARTZ,UnsagaMaterials.COPPER_ORE,UnsagaMaterials.IRON_ORE,UnsagaMaterials.METEORITE,UnsagaMaterials.OBSIDIAN,UnsagaMaterials.DIAMOND);
	public static final UnsagaMaterialCategory COLUNDUMS = new UnsagaMaterialCategory("corundums",UnsagaMaterials.RUBY,UnsagaMaterials.SAPPHIRE);
	public static final UnsagaMaterialCategory METALS = new UnsagaMaterialCategory("metals",UnsagaMaterials.LEAD,UnsagaMaterials.IRON,UnsagaMaterials.SILVER,UnsagaMaterials.METEORIC_IRON,UnsagaMaterials.FAERIE_SILVER,UnsagaMaterials.DAMASCUS,UnsagaMaterials.COPPER);
	public static final UnsagaMaterialCategory STEELS = new UnsagaMaterialCategory("steels",UnsagaMaterials.STEEL1,UnsagaMaterials.STEEL2);
	public static final UnsagaMaterialCategory CLOTHES = new UnsagaMaterialCategory("clothes",UnsagaMaterials.COTTON,UnsagaMaterials.SILK,UnsagaMaterials.VELVET,UnsagaMaterials.LIVE_SILK);
	public static final UnsagaMaterialCategory LEATHERS = new UnsagaMaterialCategory("leathers",UnsagaMaterials.FUR,UnsagaMaterials.SNAKE_LEATHER,UnsagaMaterials.CROCODILE_LEATHER,UnsagaMaterials.HYDRA_LEATHER);
	public static final ImmutableList<UnsagaMaterialCategory> CATEGORIES = ImmutableList.of(BESTIALS,DEBRIS,WOODS
			,TUSKS,BONES,SCALES,ROCKS,COLUNDUMS,METALS,STEELS,CLOTHES,LEATHERS);

	public class PropertyGetter implements IItemPropertyGetter{

		UnsagaMaterialCategory cate;
		public PropertyGetter(UnsagaMaterialCategory m){

			this.cate = m;
		}
		@Override
		public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {

			return UnsagaMaterialCapability.adapter.getCapabilityOptional(stack)
			.map(in -> in.getMaterial()).filter(in -> in.getParent().isPresent() && in.getParent().get()==this.cate)
			.isPresent() ? 1.0F : 0;
//			if(!UnsagaMaterialCapability.getMaterial(stack).isEmpty()){
//				UnsagaMaterial mat = UnsagaMaterialCapability.getMaterial(stack);
//				if(mat.getParent().isPresent() && mat.getParent().get()==this.cate){
//					//						UnsagaMod.logger.trace(this.getClass().getName(),this.m);
//					return 1.0F;
//				}
//			}
//			return 0;

		}

	}
//	public static final UnsagaMaterial DEFAULT = new UnsagaMaterial("test");



	final String name;



	List<UnsagaMaterial> list = Lists.newArrayList();
	UnsagaMaterial defaultMaterial = UnsagaMaterials.DUMMY;
//	boolean isUseParentMaterial = false;
//	Map<ToolCategory,Boolean> isUseParentMaterialMap = Maps.newHashMap();


	public UnsagaMaterialCategory(String name,UnsagaMaterial... materials){
		for(UnsagaMaterial m:materials){
			parentMap.put(m, this);
		}
		this.name = name;
		this.list = ImmutableList.copyOf(Lists.newArrayList(materials));
	}
	public void addMaterial(UnsagaMaterial m){
		this.list.add(m);
	}
	public void contains(UnsagaMaterial m){
		this.list.contains(m);
	}
	public List<UnsagaMaterial> getChildMaterials(){
		return this.list;
	}

	public UnsagaMaterial getDefaultMaterial(){
		return this.defaultMaterial;
	}
	public String getName() {
		return name;
	}
	public IItemPropertyGetter getPropertyGetter(){
		return new PropertyGetter(this);
	}

	public static @Nullable UnsagaMaterialCategory getCategory(UnsagaMaterial m){
		return parentMap.get(m);
	}

	//		public boolean isUseParentMaterial(ToolCategory cate) {
	//			if(!this.isUseParentMaterial){
	//				if(this.isUseParentMaterialMap.containsKey(cate)){
	//					return this.isUseParentMaterialMap.get(cate);
	//				}
	//			}
	//			return isUseParentMaterial;
	//		}

//	public void setDefaultMaterial(UnsagaMaterial m){
//		this.defaultMaterial = m;
//	}

	//		public void setUseParentMaterial(boolean isUseParentMaterial) {
	//			this.isUseParentMaterial = isUseParentMaterial;
	//		}
	//
	//		public void setUseParentMaterial(ToolCategory cate,boolean isUseParentMaterial) {
	//			this.isUseParentMaterialMap.put(cate, isUseParentMaterial);
	//		}
}