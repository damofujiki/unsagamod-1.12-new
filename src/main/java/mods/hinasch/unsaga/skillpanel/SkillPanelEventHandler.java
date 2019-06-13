package mods.hinasch.unsaga.skillpanel;

import java.util.Set;

import com.google.common.collect.Sets;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;

public class SkillPanelEventHandler {

	/** タフネスによる軽減効果 落下ダメージ*(1-(base*lv))*/
	public static final float TOUGHNESS_BASE = 0.05F;
	/** 鋼の意思による軽減効果 ダメージ-(base*LV)*/
	public static final float IRON_WILL_BASE = 0.15F;
	/** ネガティブパネルで対象を敵を倒した場合のダメージ*/
	public static final float HURT_NEGATIVE_SKILL = 0.5F;
	public static void register(){
//		EventShield.registerEvents();
//		HSLibs.registerEvent(new EventSaveDamage());


	}

	public static void onFall(LivingFallEvent e){
		if(SkillPanelAPI.hasPanel(e.getEntityLiving(),SkillPanels.TOUGHNESS)){
			float damage = (1.0F - (0.2F + TOUGHNESS_BASE * SkillPanelAPI.getHighestPanelLevel(e.getEntityLiving(), SkillPanels.TOUGHNESS).getAsInt()));
			e.setDamageMultiplier(MathHelper.clamp(damage, 0.1F, damage));
		}
	}

	public static Set<DamageSource> AGAINST_IRON_WILL = Sets.newHashSet(DamageSource.STARVE,DamageSource.WITHER,DamageSource.LAVA);
	public static void onLivingDeath(LivingDeathEvent e){
		if(e.getSource().getTrueSource() instanceof EntityPlayer){
			EntityPlayer ep = (EntityPlayer) e.getSource().getTrueSource();
			SkillPanelAPI.getRegisteredPanels(in -> in instanceof SkillPanelNegative)
			.forEach(in ->{
				SkillPanelNegative negative = (SkillPanelNegative) in;
				if(negative.getNegativeType()==SkillPanelNegative.Type.DAMAGE && SkillPanelAPI.hasPanel(ep, in)){
					if(negative.getTargetEntityFilter().test(e.getEntityLiving())){
						ep.attackEntityFrom(DamageSource.GENERIC, HURT_NEGATIVE_SKILL);
					}
				}
			}); //ダメージを受けるタイプのネガティブパネルなら敵死亡時にダメージ
		}
	}
	public static void onLivingDamage(LivingDamageEvent e){
		if(SkillPanelAPI.hasPanel(e.getEntityLiving(), SkillPanels.IRON_WILL)){
			if(AGAINST_IRON_WILL.contains(e.getSource())){
				float damage = e.getAmount() - (IRON_WILL_BASE * SkillPanelAPI.getHighestPanelLevel(e.getEntityLiving(), SkillPanels.IRON_WILL).getAsInt());
				e.setAmount(MathHelper.clamp(damage, 0.01F, damage));
			}
		}
		if(e.getSource().getTrueSource() instanceof EntityPlayer){
			EntityPlayer ep = (EntityPlayer) e.getSource().getTrueSource();
			SkillPanelAPI.getRegisteredPanels(in -> in instanceof SkillPanelNegative)
			.forEach(in ->{
				SkillPanelNegative negative = (SkillPanelNegative) in;
				if(negative.getNegativeType()==SkillPanelNegative.Type.WEAKNESS && SkillPanelAPI.hasPanel(ep, in)){
					if(negative.getTargetEntityFilter().test(e.getEntityLiving())){
						e.setAmount(e.getAmount() * 0.5F);
					}
				}
			}); //ダメージが減るタイプのネガティブパネルならここで
		}

	}
}
