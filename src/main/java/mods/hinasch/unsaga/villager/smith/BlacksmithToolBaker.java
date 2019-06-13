package mods.hinasch.unsaga.villager.smith;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Random;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import mods.hinasch.unsaga.ability.Abilities;
import mods.hinasch.unsaga.ability.AbilityAPI;
import mods.hinasch.unsaga.ability.AbilityCapability;
import mods.hinasch.unsaga.ability.IAbility;
import mods.hinasch.unsaga.core.advancement.UnsagaTriggers;
import mods.hinasch.unsaga.init.UnsagaItemRegisterer;
import mods.hinasch.unsaga.material.UnsagaIngredients;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.material.UnsagaMaterialCapability;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import mods.hinasch.unsaga.util.ToolCategory;
import mods.hinasch.unsaga.villager.smith.TraitToolForger.ForgeResult;
import mods.hinasch.unsaga.villager.smith.TraitToolForger.ForgeResultType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;

public class BlacksmithToolBaker{
	TraitToolForger parent;
	public UnsagaMaterial getBaseMaterial() {
		return baseMaterial;
	}

	public UnsagaMaterial getSubMaterial() {
		return subMaterial;
	}

	public ItemStack getBaseStack() {
		return baseStack;
	}

	public ItemStack getSubStack() {
		return subStack;
	}

	UnsagaMaterial baseMaterial;
	UnsagaMaterial subMaterial;
	ItemStack baseStack;
	ItemStack subStack;
	Random rand;

	public BlacksmithToolBaker(TraitToolForger parent,UnsagaMaterial base,ItemStack baseStack,UnsagaMaterial sub,ItemStack subStack){
		this.baseMaterial = Preconditions.checkNotNull(base);
		this.subMaterial = Preconditions.checkNotNull(sub);
		this.baseStack = Preconditions.checkNotNull(baseStack);
		this.subStack = Preconditions.checkNotNull(subStack);
		this.parent = parent;
		this.rand = this.parent.rand;
	}

	public static List<UnsagaMaterial> getForgeableMaterials(UnsagaMaterial base,UnsagaMaterial sub){
		return MaterialTransformRecipeInititalizer.getForgeableMaterials(base, sub);
	}

	public static int getDurability(UnsagaMaterial baseMaterial,ItemStack baseStack){

		if(baseStack.isItemStackDamageable()){
			return baseStack.getMaxDamage() - baseStack.getItemDamage();
		}else{
			int baseDurability = baseMaterial.getToolMaterial().getMaxUses();
			float amount = (float) (UnsagaIngredients.find(baseStack).map(in -> in.second()).orElse(1.0F));
			int repair = (int)((float)baseDurability * amount);
			return repair * baseStack.getCount();
		}
	}

	public Optional<ToolCategory> getBaseToolCategory(){
		if(ToolCategory.fromItem(this.baseStack.getItem())!=ToolCategory.NONE){
			return Optional.of(ToolCategory.fromItem(this.baseStack.getItem()));
		}
		return Optional.empty();
	}

	public boolean canChangeMaterial(){
		return this.getBaseToolCategory().filter(in -> in!=this.parent.getToolCategory()).isPresent();
	}
	public static int getRepairAmount(UnsagaMaterial baseMaterial,UnsagaMaterial subMaterial,ItemStack subStack){
		int repair = getDurability(subMaterial, subStack);
		List<Double> modifiers = Lists.newArrayList();
		if(baseMaterial!=subMaterial){

			if(baseMaterial.isSameBarteringCategory(subMaterial)){
				modifiers.add(0.4D);
			}

			if(baseMaterial.rank()>subMaterial.rank()){
				modifiers.add(0.5D);
			}
		}
		return (int) (repair * modifiers.stream().mapToDouble(in -> in).sum());
	}
	public static int getForgedDurability(UnsagaMaterial baseMaterial,UnsagaMaterial subMaterial,ItemStack baseStack,ItemStack subStack){

		int base = getDurability(baseMaterial, baseStack);

		int repair = getRepairAmount(baseMaterial,subMaterial, subStack);


		return base + repair;
	}

	Optional<UnsagaMaterial> forgedMaterial = Optional.empty();
	OptionalInt forgedDurability = OptionalInt.empty();
	Optional<ItemStack> forgedStack = Optional.empty();
	OptionalInt forgedWeight = OptionalInt.empty();
	//		Optional<LinkedList<IAbility>> forgedAbility = Optional.empty();
	public BlacksmithToolBaker decideForgedMaterial(){
		this.forgedMaterial = Optional.of(MaterialTransformRecipeInititalizer.decideOutputMaterial(this.parent.rand, baseMaterial, this.subMaterial));
		Optional.of(this.parent.parentContainer.ep)
		.filter(in -> in instanceof EntityPlayerMP).map(in ->(EntityPlayerMP)in)
		.ifPresent(in ->{
			if(this.forgedMaterial.get()==UnsagaMaterials.STEEL1){
				UnsagaTriggers.FORGE_STEEL.trigger(in);
			}
			if(this.forgedMaterial.get()==UnsagaMaterials.STEEL2){
				UnsagaTriggers.FORGE_STEEL2.trigger(in);
			}
			if(this.forgedMaterial.get()==UnsagaMaterials.DEBRIS2){
				UnsagaTriggers.FORGE_DEBRIS2.trigger(in);
			}
			if(this.forgedMaterial.get()==UnsagaMaterials.DAMASCUS){
				UnsagaTriggers.FORGE_DAMASCUS.trigger(in);
			}
		});

		return this;
	}

	public BlacksmithToolBaker decideForgedDurability(){

		ValidPayments.Value value = this.parent.getPaymentValue();
		int base = value.decideForgedDurability(getForgedDurability(),this.parent.smithType()==BlacksmithType.DURABILITY);
		this.forgedDurability = OptionalInt.of(base);
		return this;
	}

	public int getWeight(UnsagaMaterial base,ItemStack stack){
		return UnsagaMaterialCapability.adapter.getCapabilityOptional(stack).map(in -> in.getWeight().getValue())
				.orElse(base.weight().getValue());
	}

	public NonNullList<IAbility> decideAbility(ItemStack forged){

		return (NonNullList<IAbility>) AbilityCapability.adapter.getCapabilityOptional(forged).map(capaForged ->{
			int size = capaForged.getAbilitySlots().getSize();
			if(this.canChangeMaterial()){
				return NonNullList.withSize(size,Abilities.EMPTY);
			}
			return AbilityCapability.adapter.getCapabilityOptional(baseStack)
					.map(capaBase -> capaBase.getAbilitySlots().toNonNullList()).orElse(NonNullList.withSize(size,Abilities.EMPTY));
		}).orElse(NonNullList.withSize(4,Abilities.EMPTY));

	}
	public BlacksmithToolBaker decideForgedWeight(){

		int baseWeight = this.getWeight(baseMaterial, getBaseStack());
		int subWeight = this.getWeight(this.subMaterial, this.getSubStack());
		int fix = this.parent.rand.nextInt(2);
		int forgedWeight = baseWeight + (baseWeight>subWeight ? -fix : +fix);

		forgedWeight = MathHelper.clamp(forgedWeight, 0, 10);
		this.forgedWeight = OptionalInt.of(forgedWeight);
		return this;
	}

	/**
	 * アビリティの変化。大体原作準拠
	 * @param is
	 */
	public void transformAbility(ItemStack is){

		AbilityCapability.adapter.getCapabilityOptional(is)
		.filter(in -> AbilityMutationManager.canMutate(subMaterial))
		.filter(in -> this.parent.rand.nextFloat()<0.2F)
		.ifPresent(in ->{
			int index = in.getAbilitySlots().getReplaceableSlot();
			if(index>=0){
				IAbility ab = AbilityMutationManager.getMutableAbility(subMaterial);
				in.getAbilitySlots().updateSlot(index, ab);
			}

		});

	}

	/**
	 * 耐久度などを決定したあと実際に改造後のアイテムを焼き付ける（製造する）
	 * このあとgetForgedStack()で改造後のアイテムを取得できる
	 * @return
	 */
	public ForgeResult make(){
		//			ForgeResult result = ForgeResult.GOOD;
		if(this.forgedDurability.isPresent() && this.forgedMaterial.isPresent()
				&& this.forgedWeight.isPresent()){

			final float fix = this.parent.smithType()==BlacksmithType.ABILITY ? 0.7F : 0;


			ItemStack forged = UnsagaItemRegisterer.createStack(this.parent.getToolCategory().getAssociatedItem(), this.forgedMaterial.get(), 0);
			int durability = MathHelper.clamp(this.forgedDurability.getAsInt(), 1, forged.getMaxDamage());
			int weight = this.forgedWeight.getAsInt();
			NonNullList<IAbility> abilities = this.decideAbility(forged);

			int damage = forged.getMaxDamage() - durability;
			forged.setItemDamage(damage);


			ForgeResultType result = this.parent.getPaymentValue().decideForgeResult(rand, fix);

			AbilityCapability.adapter.getCapabilityOptional(forged).ifPresent(in ->{
				in.getAbilitySlots().apply(abilities);

				//うまくいくとアビリティが増え、ダメだと減る
				if(result==ForgeResultType.BAD){
					AbilityAPI.forgetRandomAbility(this.parent.rand, forged);
				}
				if(result==ForgeResultType.VERY_GOOD){
					AbilityAPI.attachRandomAbility(this.parent.rand, forged,Optional.empty());
				}



			});

			UnsagaMaterialCapability.adapter.getCapability(forged).setWeight(weight);
			this.refleshWeightModifier(forged);
			this.transformAbility(forged);
			this.forgedStack = Optional.of(forged);




			return new ForgeResult(result,this.forgedStack.get());
		}
		return new ForgeResult(ForgeResultType.GOOD,ItemStack.EMPTY);
	}
	public List<UnsagaMaterial> getForgeableMaterials(){
		return getForgeableMaterials(this.baseMaterial,this.subMaterial);
	}

	public int getForgedDurability(){
		return getForgedDurability(this.baseMaterial,this.subMaterial,this.baseStack,this.subStack);
	}

	/**
	 * 改造後のアイテムを取得する。bakeForgedStack()で焼き上げた後じゃないとempty
	 * @return
	 */
	public Optional<ItemStack> getForgedStack(){
		return this.forgedStack;
	}

	/**
	 * 重さから攻撃速度を再計算
	 * @param is
	 */
	public void refleshWeightModifier(ItemStack is){
		//			Multimap<String, AttributeModifier> multimap = is.getAttributeModifiers(EntityEquipmentSlot.MAINHAND);
		//            if(UnsagaMaterialCapability.adapter.hasCapability(is)){
		//            	int weight = UnsagaMaterialCapability.adapter.getCapability(is).getWeight();
		//            	if(weight>=0){
		//            		double mod = ComponentUnsagaWeapon.getSpeedModifierFromWeight(weight);
		//                	multimap.put(SharedMonsterAttributes.ATTACK_SPEED.getName(), new AttributeModifier(ComponentUnsagaWeapon.WEIGHT_EFFECTED_SPEED, "Weapon modifier", mod, 0));
		//
		//            	}
		//            }
	}
	@Override
	public String toString(){
		StringBuilder builder = new StringBuilder("");
		builder.append(this.baseMaterial);
		builder.append(this.subMaterial);
		builder.append(this.baseStack);
		builder.append(this.subStack);
		return builder.toString();
	}
}