package mods.hinasch.unsaga.ability.specialmove.action;

import java.util.EnumSet;
import java.util.function.Consumer;

import mods.hinasch.unsaga.ability.specialmove.TechInvoker;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker.InvokeType;
import mods.hinasch.unsaga.common.specialaction.ActionBase;

/**
 *
 * 技の動きのベース。
 * TechInvokerから呼び出される。
 */
public class TechActionBase extends ActionBase<TechInvoker>{

	public static final TechActionBase EMPTY_ACTION = new TechActionBase(InvokeType.NONE);

	final EnumSet<InvokeType> invokeTypes;

	public TechActionBase(InvokeType type){
		this.invokeTypes = EnumSet.of(type);
	}

	/**複数のinvoketypeがある場合（大木断など） */
	public TechActionBase(InvokeType type,InvokeType type2){
		this.invokeTypes = EnumSet.of(type,type2);
	}

	/** 技の起動タイプを取得*/
	public EnumSet<InvokeType> getInvokeTypes(){
		return this.invokeTypes;
	}

	public static ISpecialMoveAction of(ISpecialMoveAction invoker){
		return invoker;
	}

	@Override
	public TechActionBase addAction(IAction<TechInvoker> action){
		this.actionList.add(action);
		return this;
	}

	public static TechActionBase create(InvokeType type){
		return new TechActionBase(type);
	}

	public static TechActionBase create(InvokeType type,InvokeType type2){
		return new TechActionBase(type,type2);
	}
	public static interface ISpecialMoveAction extends IAction<TechInvoker>{

	}

	public boolean isEmpty(){
		return EMPTY_ACTION==this;
	}


	@Override
	public boolean isBenefical() {
		// TODO 自動生成されたメソッド・スタブ
		return false;
	}

	public static TechActionBase create(Consumer<TechActionBase> consumer,InvokeType type){
		TechActionBase base = new TechActionBase(type);
		consumer.accept(base);
		return base;
	}

	public static TechActionBase create(Consumer<TechActionBase> consumer,InvokeType type1,InvokeType type2){
		TechActionBase base = new TechActionBase(type1,type2);
		consumer.accept(base);
		return base;
	}
}
