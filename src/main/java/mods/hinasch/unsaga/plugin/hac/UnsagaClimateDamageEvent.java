package mods.hinasch.unsaga.plugin.hac;

import java.util.List;

import defeatedcrow.hac.api.damage.ClimateDamageEvent;
import defeatedcrow.hac.api.damage.DamageSourceClimate;
import mods.hinasch.unsaga.ability.Ability;
import mods.hinasch.unsaga.ability.AbilityAPI;
import mods.hinasch.unsaga.ability.Abilities;
import mods.hinasch.unsaga.core.entity.passive.EntityUnsagaChest;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import mods.hinasch.unsaga.damage.AdditionalDamage;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.Sub;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UnsagaClimateDamageEvent {

	public static AdditionalDamage onfigureBaseAdditionalDamage(LivingHurtEvent e){
		if(e.getSource()==DamageSourceClimate.climateColdDamage){
			return new AdditionalDamage(e.getSource(),General.MAGIC, 0F).setSubTypes(Sub.FREEZE);
		}
		if(e.getSource()==DamageSourceClimate.climateHeatDamage){
			return new AdditionalDamage(e.getSource(),General.MAGIC, 0F).setSubTypes(Sub.FIRE);
		}
		return null;
	}

	@SubscribeEvent
	public void onClimateDamage(ClimateDamageEvent e){
//		UnsagaMod.logger.trace("called", this.getClass().getName());
		List<Ability> abilities = AbilityAPI.getEffectiveAllPassiveAbilities(e.getEntityLiving());
		float reduce = 0.0F;
		if(e.getEntityLiving() instanceof EntityUnsagaChest){
			reduce += 100.0F;
		}
		if(e.getSource()==DamageSourceClimate.climateColdDamage){
			reduce += 0.5F * AbilityAPI.countAbility(e.getEntityLiving(), Abilities.RESIST_COLD);
			reduce += 1.0F * AbilityAPI.countAbility(e.getEntityLiving(), Abilities.RESIST_COLD_EX);
			if(e.getEntityLiving().isPotionActive(UnsagaPotions.SELF_BURNING)){
				reduce += 10.0F;
			}
		}
		if(e.getSource()==DamageSourceClimate.climateHeatDamage){
			reduce += 0.5F * AbilityAPI.countAbility(e.getEntityLiving(), Abilities.RESIST_FIRE);
			reduce += 1.0F * AbilityAPI.countAbility(e.getEntityLiving(), Abilities.RESIST_FIRE_EX);
			if(e.getEntityLiving().isPotionActive(UnsagaPotions.WATER_SHIELD)){
				reduce += 10.0F;
			}
		}
		float amount = e.getAmount() - reduce;
		if(amount<=0.0F){
			e.setCanceled(true);
		}else{

			e.setAmount(amount);
		}

	}
}
