package mods.hinasch.unsaga.villager.smith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableSet;

import mods.hinasch.lib.registry.RegistryUtil;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.init.UnsagaRegistries;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import mods.hinasch.unsaga.villager.bartering.BarteringMaterialCategory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.RegistryBuilder;

public class MaterialTransformRecipeInititalizer {

//	List<MaterialTransform> list = Lists.newArrayList();

//	UnsagaMaterialRegistry m = UnsagaMaterialRegistry.instance();

	public static class MaterialTransform extends IForgeRegistryEntry.Impl<MaterialTransform>{

		final WILDCARD wildcard;

		final UnsagaMaterial base;

		final UnsagaMaterial sub;

		final UnsagaMaterial transformed;
		final float prob;


		public MaterialTransform(String name,UnsagaMaterial base,UnsagaMaterial sub,UnsagaMaterial transform,float prob){
			this.setRegistryName(new ResourceLocation(name));
			this.base = base;
			this.sub = sub;
			this.transformed = transform;
			this.prob = prob;
			this.wildcard = WILDCARD.NONE;
		}
		public MaterialTransform(String name,UnsagaMaterial base,WILDCARD wildcard,UnsagaMaterial transform,float prob){
			this.setRegistryName(new ResourceLocation(name));
			this.base = base;
			this.sub = UnsagaMaterials.DUMMY;
			this.transformed = transform;
			this.prob = prob;
			this.wildcard = wildcard;
		}
		public UnsagaMaterial base() {
			return base;
		}
		public UnsagaMaterial getOutput() {
			return transformed;
		}
		public float chance() {
			return prob;
		}

		public UnsagaMaterial sub() {
			return sub;
		}
		public  boolean match(UnsagaMaterial baseIn,UnsagaMaterial subIn){
			if(this.sub!=UnsagaMaterials.DUMMY){
				return baseIn==this.base && this.sub==subIn;
			}
			if(this.wildcard!=WILDCARD.NONE){
				return baseIn==this.base && this.wildcard.getMaterials().stream().anyMatch(in -> {
//					UnsagaMod.logger.trace("match", subIn,in, in==subIn);
					return in==subIn;
				});
			}
			return false;
		}

		public WILDCARD wildcard(){
			return this.wildcard;
		}
	}

	public static class WeightedRandomTrans extends WeightedRandom.Item{


		public UnsagaMaterial mate;
		public WeightedRandomTrans(int itemWeightIn,UnsagaMaterial mate) {
			super(itemWeightIn);
			this.mate = mate;
			// TODO 自動生成されたコンストラクター・スタブ
		}

	}


	public static enum WILDCARD{
		NONE,WOOD{
			@Override
			public Collection<UnsagaMaterial> getMaterials(){
				return BarteringMaterialCategory.Type.WOOD.getMaterials();
			}
		},BESTIAL{
			@Override
			public Collection<UnsagaMaterial> getMaterials(){
				return BarteringMaterialCategory.Type.BESTIAL.getMaterials()
				.stream().filter(in -> in!=UnsagaMaterials.SERPENTINE)
				.collect(Collectors.toSet());
			}
		},COMMON_SCALE{
			@Override
			public Collection<UnsagaMaterial> getMaterials(){
				return BarteringMaterialCategory.Type.SCALE.getMaterials()
				.stream().filter(in -> in!=UnsagaMaterials.DRAGON_SCALE)
				.filter(in -> in!=UnsagaMaterials.ANCIENT_FISH_SCALE)
				.collect(Collectors.toSet());
			}
		};

		public Collection<UnsagaMaterial> getMaterials(){
			return ImmutableSet.of();
		}
	}

	public static final ResourceLocation MATERIAL_RECIPE = new ResourceLocation(UnsagaMod.MODID,"material_recipe");

	public static @Nonnull UnsagaMaterial decideOutputMaterial(Random rand,UnsagaMaterial baseIn,UnsagaMaterial subIn){
		List<WeightedRandomTrans> list = getRecipes().stream().filter(in -> in.match(baseIn, subIn))
				.map(in -> new WeightedRandomTrans((int)(in.chance()*100.0F),in.getOutput())).collect(Collectors.toList());

		if(list.isEmpty()){
			return baseIn;
		}
		if(list.size()==1){
			return list.get(0).mate;
		}

		WeightedRandomTrans w = WeightedRandom.getRandomItem(rand, list);
		UnsagaMaterial transformed = w.mate;


		return transformed;
	}


	protected static List<UnsagaMaterial> getForgeableMaterials(UnsagaMaterial baseIn,UnsagaMaterial subIn){
		List<MaterialTransform> transforms = getRecipes().stream().filter(in -> in.match(baseIn, subIn)).collect(Collectors.toList());
		return transforms.stream().map(in -> in.getOutput()).collect(Collectors.toList());
	}
	public static Collection<MaterialTransform> getRecipes(){
		return UnsagaRegistries.forgeRecipe().getValuesCollection();
	}
	@SubscribeEvent
	public void makeRegistry(RegistryEvent.NewRegistry ev){
		new RegistryBuilder().setName(MATERIAL_RECIPE).setType(MaterialTransform.class)
		.setIDRange(0, 4096).create();
	}


	private List<MaterialTransform> gather(){
		List<MaterialTransform> list = new ArrayList<>();
		list.add(new MaterialTransform("debris_quartz",UnsagaMaterials.QUARTZ,UnsagaMaterials.WOOD,UnsagaMaterials.DEBRIS1,0.15F));
		list.add(new MaterialTransform("debris_silver",UnsagaMaterials.SILVER,UnsagaMaterials.WOOD,UnsagaMaterials.DEBRIS1,0.15F));
		list.add(new MaterialTransform("debris_obsidian",UnsagaMaterials.OBSIDIAN,UnsagaMaterials.WOOD,UnsagaMaterials.DEBRIS1,0.15F));

		list.add(new MaterialTransform("debris2",UnsagaMaterials.DEBRIS1,UnsagaMaterials.DEBRIS1,UnsagaMaterials.DEBRIS2,1.00F));

		list.add(new MaterialTransform("debris2_bestial",UnsagaMaterials.DEBRIS1,WILDCARD.BESTIAL,UnsagaMaterials.DEBRIS2,1.00F));
		list.add(new MaterialTransform("faerie_silver",UnsagaMaterials.SILVER,WILDCARD.BESTIAL,UnsagaMaterials.FAERIE_SILVER,0.15F));


		list.add(new MaterialTransform("carnelian",UnsagaMaterials.SERPENTINE,UnsagaMaterials.WOOD,UnsagaMaterials.CARNELIAN,0.15F));

		list.add(new MaterialTransform("copper",UnsagaMaterials.COPPER_ORE,WILDCARD.WOOD,UnsagaMaterials.COPPER,1.00F));
		list.add(new MaterialTransform("iron",UnsagaMaterials.IRON_ORE,WILDCARD.WOOD,UnsagaMaterials.IRON,0.85F));
		list.add(new MaterialTransform("steel2_ore",UnsagaMaterials.IRON_ORE,WILDCARD.WOOD,UnsagaMaterials.STEEL2,0.15F));
		list.add(new MaterialTransform("steel2_steel1",UnsagaMaterials.STEEL1,WILDCARD.WOOD,UnsagaMaterials.STEEL2,0.15F));
		list.add(new MaterialTransform("steel2_iron",UnsagaMaterials.IRON,WILDCARD.WOOD,UnsagaMaterials.STEEL2,0.15F));
		list.add(new MaterialTransform("steel",UnsagaMaterials.IRON,WILDCARD.WOOD,UnsagaMaterials.STEEL1,0.85F));


		list.add(new MaterialTransform("steel2_uncraft",UnsagaMaterials.STEEL2,UnsagaMaterials.DEBRIS1,UnsagaMaterials.STEEL1,0.15F));
		list.add(new MaterialTransform("meteoric_iron_fish_scale",UnsagaMaterials.METEORITE,UnsagaMaterials.ANCIENT_FISH_SCALE,UnsagaMaterials.METEORIC_IRON,1.00F));
		list.add(new MaterialTransform("meteoric_iron_dragon_scale",UnsagaMaterials.METEORITE,UnsagaMaterials.DRAGON_SCALE,UnsagaMaterials.METEORIC_IRON,1.00F));
		list.add(new MaterialTransform("meteoric_iron_bone1",UnsagaMaterials.METEORITE,UnsagaMaterials.BONE1,UnsagaMaterials.METEORIC_IRON,0.15F));
		list.add(new MaterialTransform("meteoric_iron_bone2",UnsagaMaterials.METEORITE,UnsagaMaterials.BONE2,UnsagaMaterials.METEORIC_IRON,0.15F));
		list.add(new MaterialTransform("meteoric_iron_common_scale",UnsagaMaterials.METEORITE,WILDCARD.COMMON_SCALE,UnsagaMaterials.METEORIC_IRON,0.15F));

		list.add(new MaterialTransform("damascus",UnsagaMaterials.STEEL2,UnsagaMaterials.DEBRIS2,UnsagaMaterials.DAMASCUS,1.00F));
		list.add(new MaterialTransform("queen_siva",UnsagaMaterials.RUBY,UnsagaMaterials.SAPPHIRE,UnsagaMaterials.SIVA_QUEEN,0.15F));
		list.add(new MaterialTransform("queen_siva2",UnsagaMaterials.SAPPHIRE,UnsagaMaterials.RUBY,UnsagaMaterials.SIVA_QUEEN,0.15F));
		list.add(new MaterialTransform("roadster",UnsagaMaterials.DIAMOND,UnsagaMaterials.DIAMOND,UnsagaMaterials.ROADSTER,0.50F));
		return list;
	}

	@SubscribeEvent(priority = EventPriority.LOWEST)
	public void registerRecipes(RegistryEvent.Register<MaterialTransform> ev){

		RegistryUtil.Helper<MaterialTransform> reg = RegistryUtil.helper(ev, UnsagaMod.MODID, "unsaga");
		List<MaterialTransform> list = this.gather();
		list.forEach(in ->{
			reg.register(in);
		});
	}
}
