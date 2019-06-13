package mods.hinasch.unsaga.util;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import mods.hinasch.lib.registry.RegistryUtil;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.common.UnsagaWeight;
import mods.hinasch.unsaga.common.UnsagaWeightType;
import mods.hinasch.unsaga.core.item.misc.ItemIngredientUnsaga;
import mods.hinasch.unsaga.init.UnsagaItems;
import mods.hinasch.unsaga.init.UnsagaRegistries;
import mods.hinasch.unsaga.material.IUnsagaCategoryTool;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.material.UnsagaMaterialCapability;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public enum ToolCategory {
	KNIFE("knife"),SWORD("sword"),STAFF("staff"),SPEAR("spear"),BOW("bow"),AXE("axe"),INGREDIENT("ingredient"),
	ARMOR("armor"),HELMET("helmet"),LEGGINS("leggins"),BOOTS("boots"),ACCESSORY("accessory"),PICKAXE("pickaxe"),GUN("gun"),SHIELD("shield"),
	GLOVES("gloves"),NONE("none");

	final String prefix;

	private static final Map<ToolCategory,Supplier<Item>> CATEGORY_ITEM_MAP ;

	static{
		ImmutableMap.Builder<ToolCategory,Supplier<Item>> builder = ImmutableMap.builder();
		builder.put(ToolCategory.ACCESSORY,()->UnsagaItems.ACCESSORY);
		builder.put(ToolCategory.ARMOR, ()->UnsagaItems.ARMOR);
		builder.put(ToolCategory.AXE, ()->UnsagaItems.AXE);
		builder.put(ToolCategory.BOOTS, ()->UnsagaItems.BOOTS);
		builder.put(ToolCategory.BOW, ()->UnsagaItems.BOW);
		builder.put(ToolCategory.GUN, ()->UnsagaItems.MUSKET);
		builder.put(ToolCategory.HELMET, ()->UnsagaItems.HELMET);
		builder.put(ToolCategory.KNIFE, ()->UnsagaItems.KNIFE);
		builder.put(ToolCategory.LEGGINS, ()->UnsagaItems.LEGGINS);
//		categoryItemMap.put(ToolCategory.RAW_MATERIAL, UnsagaItems.RAW_MATERIALS);
		builder.put(ToolCategory.SHIELD, ()->UnsagaItems.SHIELD);
		builder.put(ToolCategory.SPEAR, ()->UnsagaItems.SPEAR);
		builder.put(ToolCategory.STAFF, ()->UnsagaItems.STAFF);
		builder.put(ToolCategory.SWORD, ()->UnsagaItems.SWORD);
		builder.put(ToolCategory.GLOVES, ()->UnsagaItems.GLOVES);
		CATEGORY_ITEM_MAP = builder.build();
	}

	private ToolCategory(String str){

		this.prefix = str;

	}

	public String getPrefix(){
		return this.prefix;
	}

	public boolean isWeapon(){
		return WEAPONS.contains(this);
	}


	/**
	 *
	 * @param el グローブの場合だけ必要
	 * @param is
	 * @return
	 */
	public UnsagaWeight calcWeight(EntityLivingBase el,ItemStack is){
		if(this==ToolCategory.GLOVES && el!=null){
			new UnsagaWeight(UnsagaWeightType.calcArmorWeight(el));
		}
		return UnsagaMaterialCapability.adapter.getCapabilityOptional(is).map(in -> in.getWeight()).orElse(UnsagaMaterials.DUMMY.weight());
	}
	public int getCategoryPrice(int base,ItemStack stack){
		float basePrice = (float)base;
		if(stack.getItem() instanceof IUnsagaCategoryTool){
			float durability = (float)stack.getMaxDamage() - (float)stack.getItemDamage();
			float dura_p = durability / stack.getMaxDamage() * 100;
			if(WEAPONS.contains(this) || this==ACCESSORY || this==SHIELD){
				return (int)(basePrice * dura_p * 0.01F);
			}
			if(this==HELMET){
				return (int)(basePrice * 0.6F);
			}
			if(this==ARMOR){
				return (int)(basePrice * 0.75F);
			}
			if(this==LEGGINS || this==BOOTS){
				return (int)(basePrice * 0.45F);
			}
			return (int)(basePrice * 0.3F);
		}
		return 0;
	}

	public Collection<UnsagaMaterial> getSuitables(){
		if(this==ToolCategory.GUN){
			return ImmutableSet.of(UnsagaMaterials.IRON);
		}
		return RegistryUtil.getValues(UnsagaRegistries.material(), in -> in.isSuitable(this));
	}
	/**
	 * RAWMATERIALの場合ランダムに選ばれる
	 *
	 * @return
	 */
	public Item getAssociatedItem(){
		if(CATEGORY_ITEM_MAP.containsKey(this)){
			return CATEGORY_ITEM_MAP.get(this).get();
		}
//		if(this==ToolCategory.INGREDIENT){
//			return UnsagaIngredients.instance().getRandomObject(UnsagaMod.secureRandom).getItem();
//		}
		return UnsagaItems.SWORD;

	}
	public static ToolCategory fromItem(Item itemIn){
		return Optional.ofNullable(itemIn)
		.map(item ->{
			if(item instanceof ItemIngredientUnsaga){
				return ToolCategory.INGREDIENT;
			}
			Optional<ToolCategory> cate = CATEGORY_ITEM_MAP.entrySet().stream()
					.filter(in -> in.getValue()==item)
					.map(in -> in.getKey()).findFirst();
			return cate.orElse(ToolCategory.ACCESSORY);
		}).orElse(ToolCategory.NONE);

	}
	public static ToolCategory fromString(String str){
		for(ToolCategory cate:ToolCategory.values()){
			if(cate.prefix.equals(str.toLowerCase())){
				return cate;
			}
		}
		return ToolCategory.NONE;
	}

	public EntityEquipmentSlot getEquipmentSlot(){
		if(this==ARMOR){
			return EntityEquipmentSlot.CHEST;
		}
		if(this==HELMET){
			return EntityEquipmentSlot.HEAD;
		}
		if(this==LEGGINS){
			return EntityEquipmentSlot.LEGS;
		}
		if(this==BOOTS){
			return EntityEquipmentSlot.FEET;
		}
		return EntityEquipmentSlot.MAINHAND;
	}
	/**
	 * 今のところ宝箱・店で使用。アビリティテーブル読む時の武器／防具判断
	 */
	public static final Set<ToolCategory> WEAPONS = Sets.immutableEnumSet(ToolCategory.SWORD,ToolCategory.STAFF,ToolCategory.SPEAR,ToolCategory.BOW,ToolCategory.AXE,ToolCategory.KNIFE,ToolCategory.GLOVES);
	/**
	 * 今のところ使ってない。
	 */
	public static final Set<ToolCategory> ARMORS = Sets.immutableEnumSet(ToolCategory.HELMET,ToolCategory.LEGGINS,ToolCategory.ARMOR,ToolCategory.BOOTS);
	public static final Set<ToolCategory> MERCHANDISES = Sets.immutableEnumSet(ToolCategory.SWORD,ToolCategory.STAFF,ToolCategory.SPEAR,ToolCategory.BOW,ToolCategory.AXE,ToolCategory.ARMOR,
			ToolCategory.HELMET,ToolCategory.LEGGINS,ToolCategory.BOOTS,ToolCategory.ACCESSORY,ToolCategory.KNIFE,ToolCategory.SHIELD,ToolCategory.INGREDIENT,ToolCategory.GLOVES);

	/**
	 * used in Smith now.
	 * ここに追加すると鍛冶のジャンルに現れる。
	 */
	public static final List<ToolCategory> FORGEABLES = Lists.newArrayList(ToolCategory.SWORD,ToolCategory.STAFF,ToolCategory.SPEAR,ToolCategory.BOW,ToolCategory.AXE,ToolCategory.ACCESSORY,ToolCategory.KNIFE,ToolCategory.SHIELD);

//	public static final Set<ToolCategory> WEAPONS_EXCEPT_BOW = Sets.immutableEnumSet(ToolCategory.SWORD,ToolCategory.STAFF,ToolCategory.SPEAR,ToolCategory.AXE,ToolCategory.KNIFE);

	public static HashSet<String> toString(ImmutableSet<ToolCategory> immutableSet){
		HashSet<String> newSet = new HashSet();
		for(Iterator<ToolCategory> ite=immutableSet.iterator();ite.hasNext();){
			newSet.add(ite.next().toString());
		}
		return newSet;
	}
//
	public String getLocalized(){
		return HSLibs.translateKey("unsaga.category."+this.getPrefix());
	}

	public static void registerAssociation(){
//		categoryItemMap.put(ToolCategory.ACCESSORY, UnsagaItems.ACCESSORY);
//		categoryItemMap.put(ToolCategory.ARMOR, UnsagaItems.ARMOR);
//		categoryItemMap.put(ToolCategory.AXE, UnsagaItems.AXE);
//		categoryItemMap.put(ToolCategory.BOOTS, UnsagaItems.BOOTS);
//		categoryItemMap.put(ToolCategory.BOW, UnsagaItems.BOW);
//		categoryItemMap.put(ToolCategory.GUN, UnsagaItems.MUSKET);
//		categoryItemMap.put(ToolCategory.HELMET, UnsagaItems.HELMET);
//		categoryItemMap.put(ToolCategory.KNIFE, UnsagaItems.KNIFE);
//		categoryItemMap.put(ToolCategory.LEGGINS, UnsagaItems.LEGGINS);
////		categoryItemMap.put(ToolCategory.RAW_MATERIAL, UnsagaItems.RAW_MATERIALS);
//		categoryItemMap.put(ToolCategory.SHIELD, UnsagaItems.SHIELD);
//		categoryItemMap.put(ToolCategory.SPEAR, UnsagaItems.SPEAR);
//		categoryItemMap.put(ToolCategory.STAFF, UnsagaItems.STAFF);
//		categoryItemMap.put(ToolCategory.SWORD, UnsagaItems.SWORD);
//		categoryItemMap.put(ToolCategory.GLOVES, UnsagaItems.GLOVES);
	}
	public static boolean categoryContains(ToolCategory cate,Collection colle){
		boolean isString = false;
		for(Iterator ite=colle.iterator();ite.hasNext();){
			if(ite.next() instanceof String){
				isString = true;
			}
		}
		if(isString){
			return colle.contains(cate.toString());
		}
		return colle.contains(cate);
	}
}
