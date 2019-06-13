package mods.hinasch.unsaga.lp;

import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew;
import mods.hinasch.unsaga.status.LibraryLivingStatus;
import mods.hinasch.unsaga.status.LibraryLivingStatus.LivingStatus;
import mods.hinasch.unsaga.status.UnsagaStatus;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class LPInitializeHelper {

	public static int getLPFrom(EntityLivingBase entity){


		if(entity instanceof EntityPlayer){
			return UnsagaConfigHandlerNew.LP_SETTING.defaultPlayerLP;
		}
		String s = EntityList.getEntityString(entity);
		if(LibraryLivingStatus.find(entity).isPresent()){
			LivingStatus st = LibraryLivingStatus.find(entity).get();
			if(st.getStatusMap().containsKey(UnsagaStatus.MAX_LP)){
				double lp = st.getStatusMap().get(UnsagaStatus.MAX_LP);
				return (int) lp;
			}
		}


		float f1 = MathHelper.clamp(entity.getMaxHealth()*UnsagaConfigHandlerNew.LP_SETTING.ratioMaxLP,1.0F, 256.0F);


		if(entity.getMaxHealth()<=1.0F){
			f1 = 0.0F;
		}

		if(entity instanceof EntityTameable){
			f1 += UnsagaConfigHandlerNew.LP_SETTING.baseLPTameable;
		}
//		UnsagaMod.logger.trace(entity.getName(), f1);
		return (int)f1;
	}
}
