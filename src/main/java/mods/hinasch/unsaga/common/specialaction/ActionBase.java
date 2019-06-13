package mods.hinasch.unsaga.common.specialaction;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.Lists;

import mods.hinasch.unsaga.UnsagaMod;
import net.minecraft.util.EnumActionResult;

/**
 *
 * IActionBaseの実装。
 *
 * @param <T> IActionPerformer
 */
public abstract class ActionBase<T extends IActionPerformer> implements IActionBase<T>{

	/** アクションリスト、順に起動する。最後に返したリサルトが帰ってくる*/
	protected List<IAction<T>> actionList = Lists.newArrayList();



	boolean isBenefical = false;



	public boolean isBenefical() {
		return isBenefical;
	}

	public List<IAction<T>> getActions(){
		return this.actionList;
	}
	public IActionBase<T> setBenefical(boolean isBenefical) {
		this.isBenefical = isBenefical;
		return this;
	}

//	public EnumActionResult prepareCharge(World world,EntityPlayer ep){
//		return EnumActionResult.PASS;
//	}
	@Override
	public EnumActionResult perform(T context) {
		UnsagaMod.logger.trace(this.getClass().getName(), context.getWorld());
		EnumActionResult result = EnumActionResult.PASS;
		for(IAction<T> act:this.actionList){
			result = act.apply(context);
			UnsagaMod.logger.trace(this.getClass().getName(), result,act);
		}
		return result;
	}

	public ActionBase<T> addAction(IAction<T> action,int num){
		this.actionList.add(num,action);
		return this;
	}
	public ActionBase<T> addAction(IAction<T> action){
		this.actionList.add(action);
		return this;
	}

	public ActionBase<T> addActions(List<IAction<T>> action){
		for(IAction<T> elm:action){
			this.actionList.add(elm);
		}

		return this;
	}
	@Override
	public Consumer<T> getPrePerform() {

		return null;
	}



	/**
	 *
	 * ActionBaseのListに配置できるActionのinterface
	 * EnumActionResultを返すapply(V)のみ持つ
	 *
	 * @param <V> IActionPerformer
	 */
	public static interface IAction<V extends IActionPerformer> extends Function<V,EnumActionResult>{

		/** PASSを返すとコストは消費されない*/
		EnumActionResult apply(V t);

		default boolean isEmpty(){
			return false;
		}


	}

}
