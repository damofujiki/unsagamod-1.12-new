package mods.hinasch.unsaga.ability;

import com.google.common.collect.ImmutableSet;

import net.minecraft.potion.Potion;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class AbilityAntiDebuff extends AbilityTickableBase{

	final ImmutableSet<Potion> debuffs;
	public AbilityAntiDebuff(String name,Potion... debuffs) {
		super(name);
		this.debuffs = ImmutableSet.copyOf(debuffs);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public void onLivingUpdate(LivingUpdateEvent e) {
		//デバフが消えるかどうかはインターバルごとにチェック
		if(e.getEntityLiving().ticksExisted % ANTI_DEBUFF_INTERVAL ==0){

			for(Potion potion:debuffs){
				if(e.getEntityLiving().isPotionActive(potion)){

					e.getEntityLiving().removeActivePotionEffect(potion);
				}
			}


		}
	}

}
