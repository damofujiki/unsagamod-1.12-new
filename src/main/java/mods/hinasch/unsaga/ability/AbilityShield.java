package mods.hinasch.unsaga.ability;

import mods.hinasch.unsaga.damage.AdditionalDamageTypes;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.Sub;

public class AbilityShield extends Ability{

	final AdditionalDamageTypes blockables;
	final float baseValue;

	/** バニラのブロック倍率*/
	public static final int VANILLA_BLOCK_VALUE = 33;
	/** バニラの33%+(基本盾の能力値xこの倍率)がブロック割合になる*/
	public static final float SHIELD_RATIO = 0.01F;
	/** 基本の盾能力 + スキルレベル*この値がアビリティの発動率 */
	public static final int INVOKE_RATIO = 5;

	public AbilityShield(String name,AdditionalDamageTypes blockable,float baseValue) {
		super(name);
		// TODO 自動生成されたコンストラクター・スタブ
		this.blockables = blockable;
		this.baseValue = baseValue;
	}
	public AbilityShield(String name,float value,Sub... subs) {
		this(name,new AdditionalDamageTypes(General.MAGIC).setSubTypes(subs),value);
	}

	public AbilityShield(String name,General... gens) {
		this(name,new AdditionalDamageTypes(gens),1.0F);
	}
	public AdditionalDamageTypes getBlockableTypes(){
		return this.blockables;
	}

	/** 盾スキルの優先度。基本複数スキルが適合する時はどれであろうが結果一緒なので
	 * あんまり優先度は意味ない*/
	public float getBlockableValue(){
		return this.baseValue;
	}
}
