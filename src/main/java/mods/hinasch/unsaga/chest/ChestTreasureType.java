package mods.hinasch.unsaga.chest;

import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import mods.hinasch.lib.item.WeightedRandomItem;
import mods.hinasch.unsaga.common.item.UnsagaItemFactory;
import mods.hinasch.unsaga.material.UnsagaMaterialInitializer;
import mods.hinasch.unsaga.util.ToolCategory;
import mods.hinasch.unsagamagic.spell.tablet.TabletInitializer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

/** 宝箱の中身の大雑把なタイプ。*/
public enum ChestTreasureType{
	ITEM("item",10),MONEY("money",10),TABLET("tablet",1);

	final private String name;
	final private int weight;

	private ChestTreasureType(String name,int weight){
		this.name = name;
		this.weight = weight;
	}

	public String getMessage(){
		switch(this){
		case ITEM:
			return "gui.unsaga.chest.success.divine.item";
		case MONEY:
			return "gui.unsaga.chest.success.divine.money";
		case TABLET:
			return "gui.unsaga.chest.success.divine.tablet";
		default:
			break;

		}
		return "???";
	}
	public int getWeight(){
		return this.weight;
	}
	public String getName(){
		return this.name;
	}

	public ItemStack createTreasure(Random rand,int level,UnsagaItemFactory factory){
		switch(this){
		case ITEM:
			int generateLevel = level/10 + 1;
			Set<ToolCategory> cate = Sets.newHashSet();
			cate.addAll(ToolCategory.MERCHANDISES);
			cate.add(ToolCategory.GUN);
			List<ItemStack> stacks = factory.createMerchandises(5, generateLevel, cate, Sets.newHashSet(UnsagaMaterialInitializer.getMerchandiseMaterials()));
			Collections.shuffle(stacks);
			ItemStack rt = stacks.get(0);
			return rt;
		case MONEY:
			int num = rand.nextInt(level/10+1);
			return new ItemStack(Items.GOLD_NUGGET,MathHelper.clamp(num, 1, 10));
		case TABLET:
			return TabletInitializer.drawRandomTablet(rand).createStack(1);
		default:
			break;

		}
		return ItemStack.EMPTY;
	}
	public WeightedRandomItem<ChestTreasureType> getWeighted(){
		return new WeightedRandomItem(this.getWeight(),this);
	}

	public static List<WeightedRandomItem<ChestTreasureType>> createList(int level){
		return Lists.newArrayList(ChestTreasureType.values()).stream()
				.filter(in -> {
					if(level<15){
						return in!=ChestTreasureType.TABLET;
					}
					return true;
				}).map(in -> new WeightedRandomItem<ChestTreasureType>(in.getWeight(),in)).collect(Collectors.toList());
	}
	public static ChestTreasureType fromName(String name){
		for(ChestTreasureType type:ChestTreasureType.values()){
			if(type.getName().equals(name)){
				return type;
			}
		}
		return MONEY;
	}
}