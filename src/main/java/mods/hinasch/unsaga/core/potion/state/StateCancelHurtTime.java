package mods.hinasch.unsaga.core.potion.state;

import mods.hinasch.unsaga.core.potion.EntityState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class StateCancelHurtTime extends EntityState{

	protected StateCancelHurtTime(String name) {
		super(name);
		// TODO 自動生成されたコンストラクター・スタブ
	}


	@Override
	public void onTick(World world, EntityLivingBase living, int amp) {
		living.hurtResistantTime --;
		if(living.hurtResistantTime<0){
			living.hurtResistantTime = 0;
		}
		living.hurtTime --;
		if(living.hurtTime<0){
			living.hurtTime = 0;
		}
	}
}
