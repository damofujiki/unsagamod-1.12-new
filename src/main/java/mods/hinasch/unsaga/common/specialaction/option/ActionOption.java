package mods.hinasch.unsaga.common.specialaction.option;

import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.common.specialaction.IActionPerformer.TargetType;

public class ActionOption implements IActionOption {

	final String name;
	public ActionOption(String name){
		this.name = name;
	}
	@Override
	public String getName() {
		// TODO 自動生成されたメソッド・スタブ
		return this.name;
	}
	@Override
	public String getLocalized() {
		// TODO 自動生成されたメソッド・スタブ
		return HSLibs.translateKey("unsaga.item.option."+this.getName());
	}

	public TargetType getTargetType(){
		return TargetType.OWNER;
	}

}
