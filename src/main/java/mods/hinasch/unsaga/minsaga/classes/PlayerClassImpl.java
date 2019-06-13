package mods.hinasch.unsaga.minsaga.classes;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.realmsclient.util.Pair;

import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.skillpanel.ISkillPanel;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class PlayerClassImpl extends IForgeRegistryEntry.Impl<IPlayerClass> implements IPlayerClass{

	public static class Builder{
		String name;
		Set<ISkillPanel> classSkills = ImmutableSet.of();
		Set<ISkillPanel> requireSkills = ImmutableSet.of();
		Map<IAttribute,Double> classGrowth = ImmutableMap.of();
		public Builder(String name){
			this.name = name;
		}
		public IPlayerClass build(){
			return new PlayerClassImpl(this);
		}
		public Builder buildRequiredSkillsFromClassSkills(){
			this.requireSkills = ImmutableSet.copyOf(this.classSkills);
			return this;
		}
		public Builder setClassGrowth(Pair<IAttribute,Double>... pairs){
			Map<IAttribute,Double> map = new HashMap<>();
			for(Pair<IAttribute,Double> pair:pairs){
				map.put(pair.first(), pair.second());
			}
			this.classGrowth = ImmutableMap.copyOf(map);
			return this;
		}

		public Builder setClassSkills(ISkillPanel... panels){
			this.classSkills = ImmutableSet.copyOf(panels);
			return this;
		}

		public Builder setRequireSkills(ISkillPanel... panels){
			this.requireSkills = ImmutableSet.copyOf(panels);
			return this;
		}
	}
	public static final UUID UUID_CLASS_MODIFIER = UUID.fromString("e9083149-63fb-422d-a31d-0508de06ad95");
	final Set<ISkillPanel> classSkills;
	final Set<ISkillPanel> requireSkills;
	final Map<IAttribute,Double> classGrowth;
	String unl;

	public PlayerClassImpl(Builder b) {
//		super(new ResourceLocation(name), name);
		this.classSkills = b.classSkills;
		this.requireSkills = b.requireSkills;
		this.classGrowth = b.classGrowth;
		this.setRegistryName(new ResourceLocation(UnsagaMod.MODID,b.name));
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public Map<IAttribute,Double> classGrowthTable(){
		return this.classGrowth;
	}


	/** このクラスがネイティブに持つスキル。*/
	public Set<ISkillPanel> classSkills(){
		return this.classSkills;
	}


	/** このクラスになる必要なスキル。*/
	public Set<ISkillPanel> requiredSkills(){
		return this.requireSkills;
	}

	@Override
	public String getUnlocalizedName() {
		return "unsaga.class."+this.unl;
	}

	@Override
	public void setUnlocalizedName(String unl) {
		// TODO 自動生成されたメソッド・スタブ
		this.unl = unl;
	}
}
