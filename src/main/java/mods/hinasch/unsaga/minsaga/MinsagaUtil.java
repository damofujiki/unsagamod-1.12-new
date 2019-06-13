package mods.hinasch.unsaga.minsaga;

import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import com.google.common.base.Functions;
import com.google.common.collect.Lists;

import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.util.UnsagaTextFormatting;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class MinsagaUtil {

	public static List<ItemStack> getForgedArmors(EntityLivingBase el){
		return Lists.newArrayList(el.getArmorInventoryList()).stream().filter(in -> !in.isEmpty()&&MinsagaForgingCapability.ADAPTER.hasCapability(in))
				.collect(Collectors.toList());
	}

	public static ArmorModifier getArmorModifierAmount(EntityLivingBase el){
		double melee = getForgedArmors(el).stream().mapToDouble(in -> MinsagaForgingCapability.ADAPTER.getCapability(in).armorModifier().melee()).sum();
		double magic = getForgedArmors(el).stream().mapToDouble(in -> MinsagaForgingCapability.ADAPTER.getCapability(in).armorModifier().magic()).sum();
		return new ArmorModifier((float)melee, (float)magic);
	}
	public static List<MinsagaMaterialInitializer.Ability> getAbilities(EntityLivingBase el){
		return getForgedArmors(el).stream().flatMap(in ->MinsagaForgingCapability.ADAPTER.getCapability(in).getAbilities().stream()).collect(Collectors.toList());
	}

	public static Map<MinsagaMaterialInitializer.Ability,Long> getAbilityCount(EntityLivingBase el){
		return getForgedArmors(el).stream().flatMap(in ->MinsagaForgingCapability.ADAPTER.getCapability(in).getAbilities().stream())
				.collect(Collectors.groupingBy(Functions.identity(),Collectors.counting()));
	}
//	public static void damageToolProcess(EntityLivingBase entityIn,ItemStack tool,int modifier,Random rand){
//		int prob = MathHelper.clamp(10 * Math.abs(modifier), 0, 50);
//		if(rand.nextInt(100)<prob){
//			if(modifier<0){
//				tool.damageItem(1, entityIn);
//			}else{
//				if(tool.getItemDamage()>0){
//					tool.damageItem(-1, entityIn);
//				}
//			}
//		}

//	}

	public static String getColored(int f){
		String str = "";
		if(f>0){
			str = UnsagaTextFormatting.POSITIVE.toString();
		}
		if(f<0){
			str = UnsagaTextFormatting.NEGATIVE.toString();
		}
		str += String.format("%d", f);
		return str += TextFormatting.GRAY;
	}

	public static String getColored(float f){
		String str = "";
		if(f>0){
			str = UnsagaTextFormatting.POSITIVE.toString();
		}
		if(f<0){
			str = UnsagaTextFormatting.NEGATIVE.toString();
		}
		str += String.format("%.1f", f);
		return str+= TextFormatting.GRAY;
	}

	public static List<String> buildModifierTips(IStatusModifier status,OptionalInt repairDamage){
		StringBuilder text = new StringBuilder("");
		text.append(HSLibs.translateKey("word.minsaga.modifier.attack")).append(":");
		text.append(getColored(status.attackModifier())).append("/");
		text.append(HSLibs.translateKey("word.minsaga.modifier.efficiency")).append(":");
		text.append(getColored(status.efficiencyModifier()));
		StringBuilder text1 = new StringBuilder("");
		text1.append(HSLibs.translateKey("word.minsaga.modifier.defence.melee")).append(":");
		text1.append(getColored(status.armorModifier().melee())).append("/");
		text1.append(HSLibs.translateKey("word.minsaga.modifier.defence.magic")).append(":");
		text1.append(getColored(status.armorModifier().magic()));
		StringBuilder text2 = new StringBuilder("");
		text2.append(HSLibs.translateKey("word.minsaga.modifier.repair.cost")).append(":");
		text2.append(getColored(status.costModifier())).append("/");
		StringBuilder text3 = new StringBuilder("");
		text2.append(HSLibs.translateKey("word.minsaga.modifier.durability")).append(":");
		text2.append(getColored(status.durabilityModifier())).append("/");
		if(repairDamage.isPresent()){
			text2.append(HSLibs.translateKey("word.minsaga.modifier.repair.damage")).append(":");
			text2.append(getColored(repairDamage.getAsInt()));
		}

		return Lists.newArrayList(text.toString(),text1.toString(),text2.toString());
	}

}
