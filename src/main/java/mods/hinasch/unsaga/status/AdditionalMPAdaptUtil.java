package mods.hinasch.unsaga.status;

import mods.hinasch.unsaga.UnsagaMod;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class AdditionalMPAdaptUtil {



	public static void consumeMP(EntityPlayer ep,float value){
		if(ep.isCreative()){
			return;
		}
		float cost = MathHelper.clamp(value, 0, ep.getFoodStats().getFoodLevel());
		ep.getFoodStats().addStats(-(int)cost, 0.0F);
		float exhaust = value - ep.getFoodStats().getFoodLevel();
		if(exhaust>0.0F){
			ep.getFoodStats().addExhaustion(exhaust);
			UnsagaMod.logger.trace("exhaust", exhaust);
		}

	}
	public static void consumeMPByExhaustion(EntityPlayer ep,float value){
		if(ep.isCreative()){
			return;
		}
//		float cost = MathHelper.clamp(value, 1, MAX);
		ep.getFoodStats().addExhaustion(value);
	}
	public static boolean canConsume(EntityPlayer ep,float value){
		if(ep.isCreative()){
			return true;
		}
//		float cost = MathHelper.clamp(value, 0, MAX);
//		return ep.getFoodStats().getFoodLevel() >= cost;
		return true;
	}
}
