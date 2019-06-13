package mods.hinasch.unsaga.common.specialaction;

import mods.hinasch.unsaga.common.specialaction.ActionBase.IAction;
import net.minecraft.util.EnumActionResult;

public class ActionSelf<T extends IActionPerformer> implements IAction<T> {

	final IAction<T> action;

	public ActionSelf(IAction<T> act){
		this.action = act;
	}

	@Override
	public EnumActionResult apply(T t) {
		t.setTarget(t.getPerformer());
		return action.apply(t);
	}

}
