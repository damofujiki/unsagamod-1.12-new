package mods.hinasch.unsaga.lp.event;

import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew;
import mods.hinasch.unsaga.lp.LifePoint;
import mods.hinasch.unsaga.lp.LifePoint.ILifePoint;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

/** LPダメージの無敵時間の実装*/
public class LPHurtIntervalCountHandler{

	public static void update(LivingUpdateEvent e) {
		if(!UnsagaConfigHandlerNew.GENERAL.enableLPSystem){
			return;
		}

		if(!LifePoint.adapter.hasCapability(e.getEntityLiving())){
			return;
		}
		ILifePoint capa = LifePoint.adapter.getCapability(e.getEntityLiving());

		if(e.getEntityLiving().ticksExisted % 20 * 12 == 0){
			if(capa.hurtInterval()>0){
				capa.setHurtInterval(capa.hurtInterval() -1);
			}

		}



	}


}
