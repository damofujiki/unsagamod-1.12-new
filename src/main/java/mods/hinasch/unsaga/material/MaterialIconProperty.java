package mods.hinasch.unsaga.material;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableSet;

public class MaterialIconProperty {


	static{
		Set<MaterialIconProperty> set = new HashSet();
		set.add(new MaterialIconProperty("allDebris",UnsagaMaterials. DEBRIS1,UnsagaMaterials.DEBRIS2));
		set.add(new MaterialIconProperty("bestials",UnsagaMaterials. CARNELIAN,UnsagaMaterials.OPAL,UnsagaMaterials.TOPAZ,UnsagaMaterials.RAVENITE,UnsagaMaterials.LAZULI));
		set.add(new MaterialIconProperty("woods",UnsagaMaterials. WOOD,UnsagaMaterials.TONERIKO,UnsagaMaterials.CYPRESS,UnsagaMaterials.OAK,UnsagaMaterials.BIRCH,UnsagaMaterials.SPRUCE,UnsagaMaterials.JUNGLE_WOOD,UnsagaMaterials.DARK_OAK,UnsagaMaterials.ACACIA));
		set.add(new MaterialIconProperty("tusks",UnsagaMaterials. TUSK1,UnsagaMaterials.TUSK2));
		set.add(new MaterialIconProperty("bones",UnsagaMaterials. BONE1,UnsagaMaterials.BONE2));
		set.add(new MaterialIconProperty("scales",UnsagaMaterials. ANCIENT_FISH_SCALE,UnsagaMaterials.THIN_SCALE,UnsagaMaterials.DRAGON_SCALE,UnsagaMaterials.CHITIN,UnsagaMaterials.PRISMARINE,UnsagaMaterials.SHULKER));
		set.add(new MaterialIconProperty("corundums",UnsagaMaterials. RUBY,UnsagaMaterials.SAPPHIRE));
		set.add(new MaterialIconProperty("metals",UnsagaMaterials. LEAD,UnsagaMaterials.IRON,UnsagaMaterials.SILVER,UnsagaMaterials.METEORIC_IRON,UnsagaMaterials.FAERIE_SILVER,UnsagaMaterials.DAMASCUS,UnsagaMaterials.COPPER));
		set.add(new MaterialIconProperty("steels",UnsagaMaterials. STEEL1,UnsagaMaterials.STEEL2));
		set.add(new MaterialIconProperty("rocks",UnsagaMaterials. SERPENTINE,UnsagaMaterials.QUARTZ,UnsagaMaterials.COPPER_ORE,UnsagaMaterials.IRON_ORE,UnsagaMaterials.METEORITE,UnsagaMaterials.OBSIDIAN,UnsagaMaterials.DIAMOND));
		set.add(new MaterialIconProperty("clothes",UnsagaMaterials. COTTON,UnsagaMaterials.SILK,UnsagaMaterials.VELVET,UnsagaMaterials.LIVE_SILK));
		set.add(new MaterialIconProperty("leathers",UnsagaMaterials. FUR,UnsagaMaterials.SNAKE_LEATHER,UnsagaMaterials.CROCODILE_LEATHER,UnsagaMaterials.HYDRA_LEATHER));
		LOOKUP = ImmutableSet.copyOf(set);
	}

	public static final ImmutableSet<MaterialIconProperty> LOOKUP;

	public static Set<MaterialIconProperty> getIconProperties(UnsagaMaterial m){
		return LOOKUP.stream().filter(in -> in.materials.contains(m)).collect(Collectors.toSet());
	}

	public static boolean isValidPropertyName(UnsagaMaterial m,String name){
		return getIconProperties(m).stream().anyMatch(in -> in.getTagName().equals(name));
	}

	public static Set<String> getAllPropertyNames(){
		return LOOKUP.stream().map(in -> in.getTagName()).collect(Collectors.toSet());
	}

	final String tag;
	final ImmutableSet<UnsagaMaterial> materials;
	public MaterialIconProperty(String name,UnsagaMaterial... materials){
		this.tag = name;
		this.materials = ImmutableSet.copyOf(materials);
	}


	public String getTagName(){
		return this.tag;
	}

	public boolean contains(UnsagaMaterial m){
		return this.materials.contains(m);
	}
}
