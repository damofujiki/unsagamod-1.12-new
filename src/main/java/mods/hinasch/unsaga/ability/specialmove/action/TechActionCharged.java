package mods.hinasch.unsaga.ability.specialmove.action;

import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker;
import mods.hinasch.unsaga.common.specialaction.ActionBase;
import mods.hinasch.unsaga.common.specialaction.ActionBase.IAction;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import net.minecraft.util.EnumActionResult;

public class TechActionCharged<T extends IAction<TechInvoker>> implements IAction<TechInvoker>{

	int threshold = 20;

	T action;

	public TechActionCharged(T act){
		this.action = act;
	}
	@Override
	public EnumActionResult apply(TechInvoker context) {
		UnsagaMod.logger.trace(this.getClass().getName(), context.getChargedTime());
		if(context.getChargedTime()>=this.getChargeThreshold()){

			return this.action.apply(context);
		}
		return EnumActionResult.PASS;
	}


	public int getChargeThreshold() {
		// TODO 自動生成されたメソッド・スタブ
		return this.threshold;
	}

	public T getAction(){
		return this.action;
	}

	public TechActionCharged setAction(T action){
		this.action = action;
		return this;
	}

	public TechActionCharged setChargeThreshold(int time) {
		this.threshold = time;
		return this;
	}

	public static TechActionCharged simpleMelee(int maxAttack,float knockback,General... general){
		TechActionMelee melee = new TechActionMelee(maxAttack,general);
		melee.setKnockbackStrength(knockback);
		TechActionCharged rt = new TechActionCharged(melee);
		return rt;
	}
}
