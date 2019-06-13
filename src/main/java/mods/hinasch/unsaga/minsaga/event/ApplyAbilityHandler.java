package mods.hinasch.unsaga.minsaga.event;

import java.util.Map;

import mods.hinasch.unsaga.minsaga.MinsagaMaterialInitializer;
import mods.hinasch.unsaga.minsaga.MinsagaMaterialInitializer.Ability;
import mods.hinasch.unsaga.minsaga.MinsagaUtil;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class ApplyAbilityHandler {

	public static void onHurt(LivingHurtEvent e){
		Map<Ability,Long> list = MinsagaUtil.getAbilityCount(e.getEntityLiving());

		if(e.getSource().isExplosion() && list.containsKey(MinsagaMaterialInitializer.Ability.BLAST_PROTECTION)){
			long count = list.get(MinsagaMaterialInitializer.Ability.BLAST_PROTECTION);
			float reduction = 0.3F + (0.05F * count);
			reduction = MathHelper.clamp(reduction, 0, 0.9F);
			float amount = e.getAmount() - (e.getAmount() * reduction);
			amount = MathHelper.clamp(amount, 0.0F, amount);
			e.setAmount(amount);
		}
	}

	public static void onUpdate(LivingUpdateEvent e){

		if(e.getEntityLiving().ticksExisted % 100 == 0){
			if(e.getEntityLiving().isPotionActive(MobEffects.WITHER)){
				if(MinsagaUtil.getAbilities(e.getEntityLiving()).contains(MinsagaMaterialInitializer.Ability.WITHER_PROTECTION)){
					e.getEntityLiving().removePotionEffect(MobEffects.WITHER);
				}
			}
		}
	}
}
