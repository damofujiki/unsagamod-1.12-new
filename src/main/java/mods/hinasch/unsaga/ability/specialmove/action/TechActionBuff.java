package mods.hinasch.unsaga.ability.specialmove.action;

import java.util.List;

import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker;
import mods.hinasch.unsaga.common.specialaction.ActionStatusEffect;
import net.minecraft.potion.Potion;

public class TechActionBuff extends ActionStatusEffect<TechInvoker>{

	final int level;
	public TechActionBuff(int level,boolean isDebuff, List<Potion> potions) {
		super(isDebuff, potions);
		this.level = level;
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public int decideEffectLevel(TechInvoker context) {
		// TODO 自動生成されたメソッド・スタブ
		return level;
	}

	@Override
	public int decideEffectDuration(TechInvoker context) {
		// TODO 自動生成されたメソッド・スタブ
		return ItemUtil.getPotionTime(15);
	}

}
