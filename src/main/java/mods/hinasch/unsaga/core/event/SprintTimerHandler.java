package mods.hinasch.unsaga.core.event;

import mods.hinasch.unsaga.core.entity.IUnsagaAction;
import mods.hinasch.unsaga.core.entity.UnsagaActionCapability;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class SprintTimerHandler {

	//	static Map<EntityLivingBase,Integer> map = Maps.newHashMap();

	public static int getSprintTimer(EntityLivingBase ep){
		return UnsagaActionCapability.ADAPTER.hasCapability(ep) ? UnsagaActionCapability.ADAPTER.getCapability(ep).getSprintTimer() : 0;
	}
	public static void resetTimer(EntityLivingBase ep){
		UnsagaActionCapability.ADAPTER.getCapability(ep).setSprintTimer(0);
	}

	public static void onLivingUpdate(LivingUpdateEvent e){
		if(e.getEntityLiving() instanceof EntityPlayer){
			IUnsagaAction instance = UnsagaActionCapability.ADAPTER.getCapability(e.getEntityLiving());

			if(e.getEntityLiving().isSprinting()){
				int sprint = instance.getSprintTimer();

				sprint ++;
				//				UnsagaMod.logger.trace("spritn", sprint);
				if(sprint>100){
					sprint = 100;
				}
				instance.setSprintTimer(sprint);
			}else{
				int sprint = instance.getSprintTimer();

				sprint --;
				//				UnsagaMod.logger.trace("spritn", sprint);
				if(sprint<0){
					sprint = 0;

				}
				instance.setSprintTimer(sprint);
			}
		}

	}
}
