package mods.hinasch.unsaga.ability;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.mojang.realmsclient.util.Pair;

import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.slot.AbilitySlotList;
import mods.hinasch.unsaga.ability.slot.IAbilitySlot;
import mods.hinasch.unsaga.ability.specialmove.Tech;
import mods.hinasch.unsaga.common.UnsagaWeight;
import mods.hinasch.unsaga.core.inventory.AccessorySlotCapability;
import mods.hinasch.unsaga.damage.AdditionalDamageTypes;
import mods.hinasch.unsaga.material.IUnsagaCategoryTool;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.material.UnsagaMaterialCapability;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import mods.hinasch.unsaga.util.ToolCategory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Tuple;

public class AbilityAPI {


	public static enum EquipmentSlot{
		MAIN_HAND(EntityEquipmentSlot.MAINHAND),
		OFF_HAND(EntityEquipmentSlot.OFFHAND),
		CHEST(EntityEquipmentSlot.CHEST),
		HELMET(EntityEquipmentSlot.HEAD),
		FEET(EntityEquipmentSlot.FEET),
		LEGS(EntityEquipmentSlot.LEGS),
		ACCESSORY_1(null),
		ACCESSORY_2(null);

		public static Collection<EquipmentSlot> all(){
			return Lists.newArrayList(EquipmentSlot.values());
		}
		public static @Nullable EquipmentSlot fromName(String name){
			return all().stream().filter(in ->in.getName().equals(name)).findFirst().orElse(null);
		}

		private @Nullable EntityEquipmentSlot vanillaslot;

		private EquipmentSlot(EntityEquipmentSlot slot){
			this.vanillaslot = slot;
		}
		public String getName(){
			return Optional.ofNullable(this.vanillaslot)
					.map(in -> in.getName())
					.orElse(this==ACCESSORY_1 ? "accessory_1" : "accessory_2");
		}
		public ItemStack getStackFrom(EntityLivingBase living){
			if(this.vanillaslot!=null){
				return living.getItemStackFromSlot(vanillaslot);
			}
			return AccessorySlotCapability.adapter.getCapabilityOptional(living)
					.filter(in -> this.isAccessory())
					.map(in -> in.getAccessories().get(this==ACCESSORY_1 ? 0 : 1))
					.orElse(ItemStack.EMPTY);
		}

		public boolean isAccessory(){
			return this==ACCESSORY_1 || this==ACCESSORY_2;
		}
		public boolean isHand(){
			return Optional.ofNullable(this.vanillaslot)
					.map(in -> in.getSlotType())
					.map(in -> in==EntityEquipmentSlot.Type.HAND)
					.orElse(false);

		}
	}
	static final EntityEquipmentSlot ACCESSORY = null;
	//	public static AbilityRegistry ability(){
	//		return AbilityRegistry.instance();
	//	}


	//	public static void addAbility(ItemStack is,ToolCategory category,IAbility ab){
	//		if(category.isWeapon()){
	//			AbilityCapability.adapter.getCapability(is).setAbility(0, ab);
	//		}else{
	//
	//		}
	//	}

	/**
	 * アイテムにランダムなアビリティを付与。EntityLivingは格闘技の場合だけ必要、あとはItemStackだけでよい。
	 * @param rand
	 * @param is
	 * @param el
	 * @return
	 */
	public static Optional<IAbility> attachRandomAbility(Random rand,ItemStack is,Optional<EntityLivingBase> optel){
		EntityLivingBase el = optel.orElse(null);
		return AbilityCapability.adapter.getCapabilityOptional(is)
				.map(ab ->{
					AbilitySlotList abilityList = ab.getAbilitySlots();
					UnsagaMod.logger.trace("覚えられる", abilityList.existLearnableSlot());
					if(!abilityList.existLearnableSlot()){
						return null;
					}
					ToolCategory cate = ToolCategory.fromItem(is.getItem());
					UnsagaMaterial mate = UnsagaMaterialCapability.adapter.getCapabilityOptional(is).map(in -> in.getMaterial()).orElse(UnsagaMaterials.DUMMY);
					if(mate.isEmpty()){
						return null;
					}
					UnsagaWeight weight = cate.calcWeight(el, is);
					//			int weight = cate == ToolCategory.GLOVES ? UnsagaWeightType.calcArmorWeight(el) : UnsagaMaterialCapability.adapter.getCapability(is).getWeight().getValue();

					List<IAbilitySlot> slots = abilityList.getLearnableSlots(cate,mate, weight.type());
					UnsagaMod.logger.trace("覚えられる2", slots);
					if(slots.isEmpty()){
						return null;
					}

					//覚えるスロットをランダムに選択
					IAbilitySlot slot = HSLibs.randomPick(rand, slots);
					//スロットに応じてアビリティリストを取得
					Set<IAbility> learnable = slot.getLearnableAbilities(cate, mate, weight.type())
							.stream().filter(in -> !in.isAbilityEmpty()).filter(in -> !abilityList.getUnlockedAbilities().contains(in))
							.collect(Collectors.toSet());

					if(learnable.isEmpty()){
						return null; //もしリストが空ならなにもなし
					}


					//あればひとつだけピック
					IAbility picked = HSLibs.randomPick(rand, Lists.newArrayList(learnable));
					//選んだスロットにセット

					//			slot.setAbility(picked);
					abilityList.updateSlot(slot.getIndex(), picked);
					ab.setAbilityList(abilityList);

					return picked;
				});

	}


	public static void forgetRandomAbility(Random rand,ItemStack is){

		AbilityCapability.adapter.getCapabilityOptional(is)
		.ifPresent(in ->{

			if(!in.getAbilitySlots().getUnlockedSlots().isEmpty()){
				IAbilitySlot slot = HSLibs.randomPick(rand, in.getAbilitySlots().getUnlockedSlots());
				in.getAbilitySlots().updateSlot(slot.getIndex(), Abilities.EMPTY);
			}
		});

	}

	public static int countAbility(EntityLivingBase el,IAbility ab){
		return (int)getEffectiveAllAbilities(el).stream().filter(in -> in==ab).count();
	}
	/**
	 * ドメインを省略するとunsagamod_modidになる
	 * @param id
	 * @return
	 */
	public static @Nonnull IAbility getAbilityByID(String id){
		return AbilityInitializer.get(id);
	}


	/**
	 * そのアイテムで覚える事のできるアビリティを返す。
	 * @param is
	 * @return
	 */
	//	public static LearnableAbilityTable getAbilityTable(ItemStack is){
	//		return getAbilityTable(is,null);
	//	}

	/**
	 * そのアイテムで覚える事のできるアビリティを返す。
	 * @param is
	 * @return
	 */
	public static NonNullList<Set<IAbility>> getAbilityTable(ItemStack is,@Nullable EntityLivingBase el){
		final NonNullList<Set<IAbility>> emptylist = NonNullList.create();
		return AbilityCapability.adapter.getCapabilityOptional(is)
				.map(cap ->{

					if(cap.isUniqueItem()){ //ユニークアイテム（未実装）の場合代わりにユニークアイテムのテーブルを渡す
						List<Set<IAbility>> list = cap.getLearnableUniqueAbilities().stream().map(in -> Sets.newHashSet(in)).collect(Collectors.toList());
						return ItemUtil.toNonNull(list, new HashSet<IAbility>());
					}
					ToolCategory cate = Optional.of(is).filter(in -> in.getItem() instanceof IUnsagaCategoryTool)
							.map(in ->(IUnsagaCategoryTool)in.getItem())
							.map(in -> in.getCategory())
							.orElse(ToolCategory.NONE);
					if(cate==ToolCategory.NONE){
						return emptylist;
					}
					UnsagaMaterial mate = UnsagaMaterialCapability.adapter.getCapabilityOptional(is)
							.map(in -> in.getMaterial())
							.orElse(UnsagaMaterials.DUMMY);
					if(!mate.isEmpty()){
						return emptylist;
					}

					return cap.getAbilitySlots().getLearnableAbilities(cate,mate, cate.calcWeight(el, is).type());
				}).orElse(emptylist);
	}

	//	public static String getAbilityTableString(ItemStack is){
	//		return getAbilityTable(is).stream().map(in -> in.stream().map(i -> i.getLocalized()).collect(Collectors.joining(",")))
	//				.map(in ->"["+in+"]").collect(Collectors.joining());
	//	}


	//	/**
	//	 * 回復アビリティの効果量を全て足した数を返す。最小０。
	//	 * @param el
	//	 * @return
	//	 */
	//	public static int getHealAbilityAmount(EntityLivingBase el){
	//		return getEffectiveAllAbilities(el).stream().filter(in -> in instanceof AbilityNaturalHeal)
	//		.map(in -> (AbilityNaturalHeal)in).mapToInt(in ->in.getHealAmount()).sum();
	//	}

	public static List<Tuple<IAttribute,Double>> getAllAbilityModifiers(EntityLivingBase el){
		return getEffectiveAllAbilities(el).stream()
				.filter(in -> in instanceof AbilityStatusModifier)
				.map(in -> (AbilityStatusModifier)in)
				.filter(in -> in.getAttributeModifier()!=null)
				.map(in -> in.getAttributeModifier())
				.collect(Collectors.toList());
	}

	public static Double getModifierAmount(IAttribute attribute,EntityLivingBase el){
		return AbilityAPI.getAllAbilityModifiers(el).stream()
				.filter(in -> in.getFirst().equals(attribute))
				.mapToDouble(in -> in.getSecond())
				.sum();
	}
	public static List<Pair<AbilityAPI.EquipmentSlot,ItemStack>> getAllEquippedArmors(EntityLivingBase el){
		return getAllEquippedItems(el,true);
	}

	public static List<Pair<AbilityAPI.EquipmentSlot,ItemStack>> getAllEquippedItems(EntityLivingBase el,boolean ignoreHelds){
		return EquipmentSlot.all().stream()
				.map(in -> Pair.of(in,in.getStackFrom(el)))
				.filter(in -> !in.second().isEmpty())
				.filter(in ->(ignoreHelds && !in.first().isHand())||(!ignoreHelds))
				.collect(Collectors.toList());
	}
	/**
	 * 覚えているアビリティを取得
	 *
	 *
	 *
	 * @param is
	 * @return
	 */
	public static List<IAbility> getAttachedAbilities(ItemStack is){
		return AbilityCapability.adapter.getCapabilityOptional(is)
				.map(in -> in.getAbilitySlots().getUnlockedAbilities())
				.orElse(new ArrayList<>());
	}
	/**
	 * 覚えているアビリティの内からパッシブアビリティを抜き出す。{@link #getAttachedAbilities(ItemStack)}を使用
	 * @param is
	 * @return
	 */
	public static List<Ability> getAttachedPassiveAbilities(ItemStack is){
		return AbilityCapability.adapter.getCapabilityOptional(is)
				.map(in -> in.getAbilitySlots()
						.getUnlockedAbilities(ab -> ab instanceof Ability).stream()
						.map(ab ->(Ability)ab).collect(Collectors.toList()))
				.orElse(new ArrayList<>());
	}

	/**
	 * 身体、手、アクセサリの身につけているもの全部のアビリティを取得
	 * @param el
	 * @return
	 */
	public static List<IAbility> getEffectiveAllAbilities(EntityLivingBase el){
		List<ItemStack> stacks = getAllEquippedItems(el,false).stream().map(in -> in.second()).collect(Collectors.toList());
		if(!stacks.isEmpty()){
			return stacks.stream().flatMap(stack ->
			AbilityCapability.adapter.getCapabilityOptional(stack)
			.map(capa -> capa.getAbilitySlots().getUnlockedAbilities().stream())
			.orElse(Stream.empty()))
					.filter(in -> !in.isAbilityEmpty())
					.collect(Collectors.toList());

		}

		return new ArrayList<>();
	}
	/** パッシブアビリティ(Ability not SpecialMove)を取得する*/
	public static List<Ability> getEffectiveAllPassiveAbilities(EntityLivingBase el){
		return getEffectiveAllAbilities(el).stream().filter(in -> in instanceof Ability).map(in ->(Ability)in).collect(Collectors.toList());
	}
	public static Optional<Tech> getLearnedSpecialMove(ItemStack is){
		return getAttachedAbilities(is).stream().filter(in -> in instanceof Tech).map(in -> (Tech)in).findFirst();
	}
	public static IAbility getCurrentTech(ItemStack is){
		return AbilityCapability.adapter.getCapabilityOptional(is)
				.map(in -> in.getAbilitySlots().getCurrentTechSlot(is).ability())
				.filter(in -> in instanceof Tech).orElse(Abilities.EMPTY);
	}

	/** ダメージにマッチする盾アビリティを装備アビリティから全て取得*/
	public static List<AbilityShield> getMatchedShieldAbility(ItemStack is,AdditionalDamageTypes other){
		return getAttachedPassiveAbilities(is).stream().filter(in -> in instanceof AbilityShield).map(in ->(AbilityShield)in).filter(in -> other.containsAnyType(in.getBlockableTypes())).collect(Collectors.toList());
	}

	public static boolean hasAbility(ItemStack is,IAbility ab){
		return AbilityCapability.adapter.getCapabilityOptional(is).map(in -> in.getAbilitySlots().hasAbility(ab)).orElse(false);
	}

	public static boolean hasBlockingAbility(ItemStack is){
		return AbilityCapability.adapter.getCapabilityOptional(is)
				.map(cap -> cap.getAbilitySlots().getUnlockedAbilities().stream()
						.anyMatch(in -> in instanceof AbilityBlocking)).orElse(false);
	}

	//	public static boolean hasWeaponSpecialMove(ItemStack is){
	//		if(AbilityCapability.adapter.hasCapability(is) && UnsagaMaterialCapability.adapter.hasCapability(is)){
	//			return AbilityCapability.adapter.getCapability(is).getAbilityList().getCurrentTechSlot(is).getAbility() instanceof Tech;
	////			if(AbilityCapability.adapter.getCapability(is).getLearnedAbilities().stream().anyMatch(in -> in instanceof Tech)){
	////				return true;
	////			}
	//		}
	//		return false;
	//	}
}
