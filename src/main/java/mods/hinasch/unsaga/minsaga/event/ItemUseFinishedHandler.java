package mods.hinasch.unsaga.minsaga.event;

import java.util.List;
import java.util.stream.Collectors;

import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.unsaga.minsaga.MinsagaForgingCapability;
import mods.hinasch.unsaga.minsaga.MinsagaMaterialInitializer;
import mods.hinasch.unsaga.minsaga.MinsagaUtil;
import mods.hinasch.unsaga.minsaga.MinsagaMaterialInitializer.Ability;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemFood;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemUseFinishedHandler {


	@SubscribeEvent
	public void onFoodEaten(LivingEntityUseItemEvent.Finish ev){
		if(ev.getItem().getItem() instanceof ItemFood){
			List<MinsagaMaterialInitializer.Ability> list = MinsagaUtil.getForgedArmors(ev.getEntityLiving()).stream().flatMap(in -> MinsagaForgingCapability.ADAPTER.getCapability(in).getAbilities().stream()).collect(Collectors.toList());
			if(list.contains(MinsagaMaterialInitializer.Ability.SEA)){
				int amount = (int) list.stream().filter(in -> in==MinsagaMaterialInitializer.Ability.SEA).count();
				ev.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING,ItemUtil.getPotionTime(10*amount),0));
			}
			if(list.contains(MinsagaMaterialInitializer.Ability.FAERIE)){
				int amount = (int) list.stream().filter(in -> in==MinsagaMaterialInitializer.Ability.FAERIE).count();
				ev.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST,ItemUtil.getPotionTime(10*amount),0));
				ev.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.SPEED,ItemUtil.getPotionTime(10*amount),0));
			}
		}
	}
}
