package mods.hinasch.unsaga.common.specialaction;

import java.util.HashMap;
import java.util.Map;

import mods.hinasch.unsaga.common.specialaction.ActionBase.IAction;
import net.minecraft.util.EnumActionResult;

public abstract class ActionSelector<K extends IActionPerformer,T>  implements IAction<K>{

	protected Map<T,IAction<K>> selectableMap = new HashMap<>();

	public ActionSelector addAction(T type,IAction<K> action){
		this.selectableMap.put(type, action);
		return this;
	}

	@Override
	public EnumActionResult apply(K context) {
		IAction<K> action = this.getKey(context);
		if(!action.isEmpty()){
			return action.apply(context);
		}
		return EnumActionResult.PASS;
	}

	public abstract IAction<K> getKey(K context);
}
