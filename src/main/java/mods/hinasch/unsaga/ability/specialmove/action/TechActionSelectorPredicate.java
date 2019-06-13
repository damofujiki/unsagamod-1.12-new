package mods.hinasch.unsaga.ability.specialmove.action;

import java.util.function.Predicate;

import mods.hinasch.unsaga.ability.specialmove.TechInvoker;
import mods.hinasch.unsaga.common.specialaction.ActionBase.IAction;
import mods.hinasch.unsaga.common.specialaction.ActionSelector;

public class TechActionSelectorPredicate extends ActionSelector<TechInvoker,Boolean>{

	Predicate<TechInvoker> predicate = in -> true;

	@Override
	public IAction<TechInvoker> getKey(TechInvoker context) {
		// TODO 自動生成されたメソッド・スタブ
		return this.selectableMap.getOrDefault(predicate.test(context),TechActionSelectorInvokeType.EMPTY);
	}

	public TechActionSelectorPredicate setPredicate(Predicate<TechInvoker> predicate){
		this.predicate = predicate;
		return this;
	}
}
