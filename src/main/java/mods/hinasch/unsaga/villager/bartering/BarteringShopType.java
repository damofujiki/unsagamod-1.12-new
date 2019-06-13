package mods.hinasch.unsaga.villager.bartering;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.iface.IIntSerializable;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import mods.hinasch.unsaga.util.ToolCategory;
import mods.hinasch.unsaga.villager.VillagerProfessionUnsaga;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.util.WeightedRandom;
import net.minecraft.village.Village;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

/** 店のタイプ。売り物が変わる*/
public enum BarteringShopType implements IIntSerializable{

	UNKNOWN(0,"unknown"),ALL(1,"all"),MAGIC(2,"magic"),SNOWY_VILLAGE(3,"snowy_village"),TROPICAL_VILLAGE(4,"tropical_village"),PORTTOWN(5,"port_town"),WANDA(6,"wanda");

	final int meta;
	final String name;

	private BarteringShopType(int par1,String name){
		this.meta = par1;
		this.name = name;

	}

	public String getName(){
		return this.name;
	}

	public void addTips(List<String> list){
		if(this!=BarteringShopType.UNKNOWN){
			List<ToolCategory> category = this.getAvailableToolCategory().stream().sorted().collect(Collectors.toList());
			String msg1 = Joiner.on(",").join(category.stream().map(in->in.getLocalized()).collect(Collectors.toList()));
			list.add(HSLibs.translateKey("gui.unsaga.bartering.info.category")+":"+msg1);

			List<BarteringMaterialCategory.Type> types = this.getAvailableTypes().stream().sorted().collect(Collectors.toList());
			String msg2 = Joiner.on(",").join(types.stream().map(in->in.getLocalized()).collect(Collectors.toList()));
			list.add(HSLibs.translateKey("gui.unsaga.bartering.info.material")+":"+msg2);
			if(HSLib.isDebug()){
				list.add("type:"+this);
			}
		}
	}

	public EnumSet<BarteringMaterialCategory.Type> getAvailableTypes(){
		return BarteringMaterialCategory.ASSIGNMENTS_SHOP.getCategories(this);
	}

	public Set<UnsagaMaterial> getAvailableMerchandiseMaterials(){
		Set<UnsagaMaterial> rt = this.getAvailableTypes().stream()
				.flatMap(in -> in.getMaterials().stream()
						.filter(mate ->this==MAGIC && mate!=UnsagaMaterials.SERPENTINE))
				.filter(in -> in.isMerchandise())
				.collect(Collectors.toSet());
		return ImmutableSet.copyOf(rt);
	}

	public EnumSet<ToolCategory> getAvailableToolCategory(){
		return BarteringMaterialCategory.ASSIGNMENTS_SHOP.getToolTypes(this);

	}



	@Override
	public int getMeta() {
		// TODO 自動生成されたメソッド・スタブ
		return meta;
	}

	public static BarteringShopType fromMeta(int meta){
		return HSLibs.fromMeta(BarteringShopType.values(), meta);
	}

	public static BarteringShopType fromName(String name){
		for(BarteringShopType type:BarteringShopType.values()){
			if(type.getName().equals(name)){
				return type;
			}
		}
		return BarteringShopType.UNKNOWN;
	}

	public static BarteringShopType decideBarteringType(World world,EntityVillager villager){
		List<WeightedRandomType> list = Lists.newArrayList();
		Biome biome = world.getBiome(villager.getPosition());
		//魔法屋なら魔法タイプで固定
		if(villager.getProfessionForge()==VillagerProfessionUnsaga.MAGIC_MERCHANT){
			return BarteringShopType.MAGIC;
		}
		//村の大きさで少し変わる
		Optional.ofNullable(world.getVillageCollection())
		.ifPresent(in ->{
			Village village = in.getNearestVillage(villager.getPosition(), 32);
			if(village!=null){
				list.add(new WeightedRandomType(village.getNumVillagers()>15 ? 10 : 4,BarteringShopType.PORTTOWN));
				list.add(new WeightedRandomType(village.getNumVillagers()>30 ? 10 : 2,BarteringShopType.ALL));
			}else{
				list.add(new WeightedRandomType(2,BarteringShopType.PORTTOWN));
				list.add(new WeightedRandomType(1,BarteringShopType.ALL));
			}
		});


		//バイオームでも変わる（砂漠タイプ、寒冷地タイプなど）
		list.add(new WeightedRandomType(BiomeDictionary.hasType(biome, BiomeDictionary.Type.SANDY) ? 5 : 10,BarteringShopType.TROPICAL_VILLAGE));
		list.add(new WeightedRandomType(BiomeDictionary.hasType(biome, BiomeDictionary.Type.COLD) ? 10 : 5,BarteringShopType.SNOWY_VILLAGE));
		list.add(new WeightedRandomType(5,BarteringShopType.WANDA));
		return WeightedRandom.getRandomItem(world.rand, list).type;
	}

	public static class WeightedRandomType extends WeightedRandom.Item{

		public BarteringShopType type;
		public WeightedRandomType(int itemWeightIn,BarteringShopType type) {
			super(itemWeightIn);
			this.type =type;
			// TODO 自動生成されたコンストラクター・スタブ
		}

	}
}
