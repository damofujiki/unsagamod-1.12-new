package mods.hinasch.unsaga.villager.smith;

import mods.hinasch.lib.iface.IIntSerializable;
import mods.hinasch.lib.util.HSLibs;

public enum BlacksmithType implements IIntSerializable{

	NONE(0,"none"),
	/**
	 * 耐久性が得意
	 */
	DURABILITY(1,"durability")
	/**
	 * アビリティの引き出しが得意
	 */
	,ABILITY(2,"ability");

	final String name;
	final int meta;
	private  BlacksmithType(int meta,String name){
		this.meta = meta;
		this.name = name;
	}
	@Override
	public int getMeta() {
		// TODO 自動生成されたメソッド・スタブ
		return meta;
	}

	public String getName(){
		return this.name;
	}
	public String getLocalized(){
		return HSLibs.translateKey("gui.unsaga.smith.type."+this.getName());
	}
	public static BlacksmithType fromMeta(int meta){
		return HSLibs.fromMeta(BlacksmithType.values(), meta);
	}

}
