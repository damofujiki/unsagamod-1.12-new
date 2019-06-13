package mods.hinasch.unsaga.ability.specialmove.action;

import mods.hinasch.unsaga.ability.specialmove.TechInvoker;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker.InvokeType;
import mods.hinasch.unsaga.common.specialaction.ActionBase;
import mods.hinasch.unsaga.common.specialaction.ActionSelector;
import mods.hinasch.unsaga.common.specialaction.ActionBase.IAction;
import net.minecraft.util.EnumActionResult;

public class TechActionSelectorInvokeType extends ActionSelector<TechInvoker,InvokeType>{

	public static final IAction<TechInvoker> EMPTY = new IAction<TechInvoker>(){

		@Override
		public EnumActionResult apply(TechInvoker t) {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}

		@Override
		public boolean isEmpty(){
			return true;
		}
	};



	@Override
	public IAction<TechInvoker> getKey(TechInvoker context) {
		// TODO 自動生成されたメソッド・スタブ
		return this.selectableMap.getOrDefault(context.getInvokeType(), EMPTY);
	}
}
