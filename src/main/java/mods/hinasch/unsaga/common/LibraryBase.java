package mods.hinasch.unsaga.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import mods.hinasch.lib.misc.JsonApplier.IJsonApplyTarget;
import mods.hinasch.lib.misc.JsonApplier.IJsonParser;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.villager.smith.PredicateBase;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.oredict.OreDictionary;


/**
 *
 *
 *
 * @param <T> アイテムと交換に渡すデータ（MAPのvalue）
 * @param <K> パーサー。IJsonParserをimplementしてる必要がある
 */
public abstract class LibraryBase<T,K extends IJsonParser> implements IJsonApplyTarget<K>{



	protected Map<Predicate<ItemStack>,T> materialRegistry = new HashMap<>();


	public Optional<T> findData(ItemStack is){
		if(this.materialRegistry.keySet().stream().anyMatch(in -> in.test(is))){
			return this.materialRegistry.keySet().stream().filter(in -> in.test(is)).map(in -> this.materialRegistry.get(in)).findFirst();
		}
		return Optional.empty();
	}

	public void register(String type,String name,T elm){
		if(type.equals("item")){
			Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(name));
			Preconditions.checkNotNull(item, name+" is not found!");
			this.add(item, elm);
		}
		if(type.equals("block")){
			Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(name));
			Preconditions.checkNotNull(block, name+" is not found!");
			this.add(block, elm);
		}
		if(type.equals("armor")){
			Optional<ArmorMaterial> opt = Lists.newArrayList(ArmorMaterial.values()).stream().filter(in -> in.getName().equals(name)).findFirst();
			if(opt.isPresent()){
				this.add(opt.get(), elm);
			}
		}
		if(type.equals("tool")){
			Optional<ToolMaterial> opt = Lists.newArrayList(ToolMaterial.values()).stream().filter(in -> in.toString().toLowerCase().equals(name)).findFirst();
			if(opt.isPresent()){
				this.add(opt.get(), elm);
			}
		}
		if(type.equals("ore")){
			this.add(name, elm);
		}
	}
	public void add(Item item,T price){
		this.materialRegistry.put(new PredicateItem(item,OreDictionary.WILDCARD_VALUE), price);
	}
	public void add(String string,T price){
		this.materialRegistry.put(new PredicateOre(string), price);
	}
	public void add(Block block,T price){
		this.add(block,OreDictionary.WILDCARD_VALUE, price);
	}
	public void add(Block block,int damage,T price){
		this.materialRegistry.put(new PredicateItem(Item.getItemFromBlock(block),damage), price);
	}
	public void add(ToolMaterial tm,T price){
		this.materialRegistry.put(new PredicateToolMaterial(tm), price);
	}

	public void add(ArmorMaterial am,T price){
		this.materialRegistry.put(new PredicateArmorMaterial(am), price);
	}
	public void add(Predicate<ItemStack> item,T price){
		this.materialRegistry.put(item, price);
	}

	public static class PredicateArmorMaterial extends PredicateBase<ArmorMaterial>{

		String armorKey;
		public PredicateArmorMaterial(ArmorMaterial obj) {
			super(obj);
			this.armorKey = obj.getName();
			// TODO 自動生成されたコンストラクター・スタブ
		}

		@Override
		public List<ItemStack> getItemStack() {
			// TODO 自動生成されたメソッド・スタブ
			return new ArrayList<>();
		}

		@Override
		public boolean test(ItemStack t) {
			if(t.getItem() instanceof ItemArmor){
				ItemArmor tool = (ItemArmor) t.getItem();
				return tool.getArmorMaterial().getName().equals(armorKey);
			}
			return false;
		}

	}
	public static class PredicateToolMaterial extends PredicateBase<ToolMaterial>{

		public PredicateToolMaterial(ToolMaterial obj) {
			super(obj);
			// TODO 自動生成されたコンストラクター・スタブ
		}

		@Override
		public List<ItemStack> getItemStack() {
			// TODO 自動生成されたメソッド・スタブ
			return new ArrayList<>();
		}

		@Override
		public boolean test(ItemStack t) {
			if(t.getItem() instanceof ItemTool){
				ItemTool tool = (ItemTool) t.getItem();
				return tool.getToolMaterialName().equals(this.object.toString());
			}
			if(t.getItem() instanceof ItemSword){
				ItemSword sword = (ItemSword)t.getItem();
				return sword.getToolMaterialName().equals(this.object.toString());
			}
			if(t.getItem() instanceof ItemHoe){
				ItemHoe hoe = (ItemHoe)t.getItem();
				return hoe.getMaterialName().equals(this.object.toString());
			}
			return false;
		}

	}

	public static class PredicateOre extends PredicateBase<String>{

//		final String ore;

		public PredicateOre(String ore){
			super(ore);
		}
		@Override
		public boolean test(ItemStack t) {
			return HSLibs.getOreNames(t).stream().anyMatch(in -> in.equals(object));
		}
		@Override
		public List<ItemStack> getItemStack() {
			// TODO 自動生成されたメソッド・スタブ
			return OreDictionary.getOres(this.object);
		}

	}
	public static class PredicateItem extends PredicateBase<Item>{


		final int damage;

		public PredicateItem(Item item,int damage){
			super(item);
			this.damage = damage;
		}
		@Override
		public boolean test(ItemStack t) {
			if(damage==OreDictionary.WILDCARD_VALUE){
				return t.getItem() == this.object;
			}
			return t.getItem()==this.object && t.getItemDamage() == damage;
		}
		@Override
		public List<ItemStack> getItemStack() {
			if(damage==OreDictionary.WILDCARD_VALUE){
				return Lists.newArrayList(new ItemStack(object));
			}
			return Lists.newArrayList(new ItemStack(object,1,damage));
		}


	}
}
