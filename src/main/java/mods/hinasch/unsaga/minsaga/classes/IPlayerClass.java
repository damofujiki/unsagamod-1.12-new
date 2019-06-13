package mods.hinasch.unsaga.minsaga.classes;

import java.util.Collection;
import java.util.Map;
import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.Collectors;

import com.mojang.realmsclient.util.Pair;

import mods.hinasch.lib.entity.ModifierHelper;
import mods.hinasch.lib.registry.RegistryUtil;
import mods.hinasch.lib.registry.RegistryUtil.IUnlocalizedName;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.Statics;
import mods.hinasch.unsaga.init.UnsagaRegistries;
import mods.hinasch.unsaga.skillpanel.ISkillPanel;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IPlayerClass extends IForgeRegistryEntry<IPlayerClass>,Comparable<IPlayerClass>,IUnlocalizedName{

	default int compareTo(IPlayerClass o) {

		if(this==PlayerClasses.NO_CLASS){
			return 1;
		}
		if(o==PlayerClasses.NO_CLASS){
			return -1;
		}
		return this.getRegistryName().compareTo(o.getRegistryName());
	}
	default void refleshClassModifier(EntityPlayer ep){
		RegistryUtil.getValues(UnsagaRegistries.playerClass(), in -> in!=PlayerClasses.NO_CLASS)
		.stream().flatMap(in -> in.classGrowthTable().keySet().stream()).forEach(at ->{
			double value = this.classGrowthTable().containsKey(at) ? this.classGrowthTable().get(at) : 0;
			ModifierHelper.refleshModifier(ep, at, new AttributeModifier(PlayerClassImpl.UUID_CLASS_MODIFIER, "class_modifier."+at.getName(),value, Statics.OPERATION_INCREMENT));
		});
	}
	default String getLocalized(){
		return HSLibs.translateKey(getUnlocalizedName());
	}
	default OptionalInt onCheckPanelLevel(EntityPlayer ep,ISkillPanel panel,int level){
		return this.classSkills().contains(panel) ? OptionalInt.of(level) : OptionalInt.empty();
	}

	default Collection<Pair<ISkillPanel,Integer>> classSkillsWithLevel(int level){
		return this.classSkills().stream().map(in -> Pair.of(in,level)).collect(Collectors.toList());
	}
	public Set<ISkillPanel> classSkills();

	/** このクラスになる必要なスキル。*/
	public Set<ISkillPanel> requiredSkills();


	public Map<IAttribute,Double> classGrowthTable();
}
