package mods.hinasch.unsaga.minsaga;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import io.netty.util.internal.StringUtil;
import mods.hinasch.lib.misc.JsonApplier.IJsonApplyTarget;
import mods.hinasch.lib.registry.RegistryUtil.IUnlocalizedName;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.minsaga.MinsagaMaterialInitializer.Ability;
import mods.hinasch.unsaga.minsaga.MinsagaMaterialInitializer.ParserMaterial;
import mods.hinasch.unsaga.villager.smith.LibraryBlacksmith;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class MinsagaMaterial extends IForgeRegistryEntry.Impl<MinsagaMaterial> implements IStatusModifier,Comparable<MinsagaMaterial>,IUnlocalizedName{

	/** ビルダー*/
	public static class Builder{
		int repairCost =5;
		int repairDamage = 0;
		float attackModifier = 0;
		float efficiencyModifier = 0;
		int durabilityModifier = 0;
		List<Ability> abilities = new ArrayList<>();
		ArmorModifier pairModifier = ArmorModifier.ZERO;
		int weight;
		String orename = StringUtil.EMPTY_STRING;
		@Nullable Predicate<ItemStack> predicateItem;

		String name;
		public Builder(String name){
			this.name = name;
		}

		public Builder(String name,String orename){
			this(name);
			this.orename = orename;
		}
		private Predicate<ItemStack> createOreChecker(String orename){
			return new LibraryBlacksmith.PredicateOre(orename);
		}
		public Builder setAbility(Ability... abilities){
			this.abilities = Lists.newArrayList(abilities);
			return this;
		}
		public Builder setArmorModifier(float armor,float magic) {
			this.pairModifier = new ArmorModifier(armor,magic);
			return this;
		}

		public Builder setAttackModifier(float attackModifier) {
			this.attackModifier = attackModifier;
			return this;
		}

		public Builder setDurabilityModifier(int par1){
			this.durabilityModifier = par1;
			return this;
		}
		public void setEfficiencyModifier(float efficiencyModifier) {
			this.efficiencyModifier = efficiencyModifier;
		}




		public Builder setMaterialChecker(Predicate<ItemStack> predicate){
			this.predicateItem = predicate;
			return this;
		}
		public Builder setRepairCost(int repairCost) {
			this.repairCost = repairCost;
			return this;
		}


		public Builder setRepairDamage(int repairDamage) {
			this.repairDamage = repairDamage;
			return this;
		}


		public Builder setWeight(int weight) {
			this.weight = weight;
			return this;
		}
	}
	/** 工場*/
	public static class Factory implements IJsonApplyTarget<ParserMaterial>{

		Map<String,Builder> map;

		public Factory(){
			this.map = new HashMap<>();
		}

		public void add(Builder b){
			map.put(b.name,b);
		}

		@Override
		public void applyJson(ParserMaterial parser) {
			//			MinsagaMaterial material = allMaterials.getObject(new ResourceLocation(parser.name));
			//			Preconditions.checkNotNull(material,"Material not found!" ,parser.name);

			Optional.of(map.get(new ResourceLocation(parser.name)))
			.ifPresent(material ->{
				material.setAttackModifier(0.25F*(parser.attackModifier*10));
				material.setArmorModifier(0.25F*(parser.armorModifier.melee()*10), 0.25F*(parser.armorModifier.magic()*10));
				material.setDurabilityModifier(parser.durabilityToughness);
				material.setEfficiencyModifier(parser.efficiency);
				material.setRepairCost(parser.repair);
				material.setRepairCost(parser.cost);
			});
			//			if(material!=null){
			//				material.setAttackModifier(0.25F*(parser.attackModifier*10));
			//				material.setArmorModifier(0.25F*(parser.armorModifier.melee()*10), 0.25F*(parser.armorModifier.magic()*10));
			//				material.setDurabilityModifier(parser.durabilityToughness);
			//				material.setEfficiencyModifier(parser.efficiency);
			//				material.setRepairCost(parser.repair);
			//				material.setRepairCost(parser.cost);
			//			}
		}
	}
	final int repairCost;
	final int repairDamage;
	final float attackModifier;
	final float efficiencyModifier;
	final int durabilityModifier;
	final List<Ability> abilities;
	final String orename;
	final ArmorModifier pairModifier;
	String unlocalized;
	final int weight;


	public MinsagaMaterial(Builder builder){
		this.repairCost = builder.repairCost;
		this.repairDamage = builder.repairDamage;
		this.attackModifier = builder.attackModifier;
		this.efficiencyModifier = builder.efficiencyModifier;
		this.durabilityModifier = builder.durabilityModifier;
		this.abilities = ImmutableList.copyOf(builder.abilities);
		this.pairModifier = builder.pairModifier;
		this.weight = builder.weight;
		this.orename = builder.orename;
	}

	public List<Ability> abilities(){
		return this.abilities;
	}


	public ArmorModifier armorModifier() {
		return this.pairModifier;
	}
	public float attackModifier() {
		return attackModifier;
	}

	@Override
	public int compareTo(MinsagaMaterial o) {
		return o.getRegistryName().compareTo(o.getRegistryName());
	}

	public int costModifier() {
		return repairCost;
	}


	/**
	 * 強度。＋で耐久が減りにくく、ーで減りやすくなる
	 */
	@Override
	public int durabilityModifier() {
		// TODO 自動生成されたメソッド・スタブ
		return this.durabilityModifier;
	}


	public float efficiencyModifier() {
		return efficiencyModifier;
	}


	public String getLocalized(){
		return HSLibs.translateKey(this.getUnlocalizedName());
	}


	public int getRepairDamage() {
		return repairDamage;
	}

	@Override
	public String getUnlocalizedName(){
		return this.unlocalized;
	}
	@Override
	public int weightModifier() {
		// TODO 自動生成されたメソッド・スタブ
		return this.weight;
	}
	public boolean hasAbilities(){
		return !this.abilities.isEmpty();
	}

	public boolean hasAbility(Ability ab){
		return this.abilities.contains(ab);
	}
	public boolean isMaterialEqual(ItemStack stack){
		return MinsagaMaterialInitializer.find(stack)
				.map(in ->in==this)
				.orElse(false);
	}

	public String oreName(){
		return this.orename;
	}

	@Override
	public void setUnlocalizedName(String unl) {
		// TODO 自動生成されたメソッド・スタブ
		this.unlocalized = unl;
	}
}