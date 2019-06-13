package mods.hinasch.unsaga.ability.slot;

import mods.hinasch.lib.iface.IIntSerializable;
import mods.hinasch.lib.iface.INBTWritable;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.UtilNBT.RestoreFunc;
import net.minecraft.nbt.NBTTagCompound;

public enum AbilitySlotType implements IIntSerializable,INBTWritable{

	/** 技を覚えるタイプ。敵を攻撃時にひらめく*/
	TECH(0),
	/** ブロッキングアビリティ。覚えるのは敵を倒した時*/
	BLOCKING(1),
	/** パッシブアビリティ。覚えるのは敵を倒した時*/
	PASSIVE(2),
	/** 特に機能なし*/
	NO_FUNCTION(3),
	/** 固有アビリティ専用スロット*/
	TECH_MATERIAL(4);

	final int meta;
	private AbilitySlotType(int meta){
		this.meta = meta;
	}

	public static AbilitySlotType fromMeta(int meta){
		return HSLibs.fromMeta(AbilitySlotType.values(), meta);
	}

	@Override
	public int getMeta() {
		// TODO 自動生成されたメソッド・スタブ
		return meta;
	}

	@Override
	public void writeToNBT(NBTTagCompound stream) {
		stream.setInteger("meta", getMeta());

	}

	public static final RestoreFunc<AbilitySlotType> RESTORE = in ->{
		int meta = in.getInteger("meta");
		return AbilitySlotType.fromMeta(meta);
	};
}
