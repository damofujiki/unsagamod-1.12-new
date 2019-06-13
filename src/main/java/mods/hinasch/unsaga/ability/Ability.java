package mods.hinasch.unsaga.ability;

import mods.hinasch.lib.util.HSLibs;

public class Ability extends AbilityBase{



//	String desc = "empty";

	public Ability(String name) {
		super(name);

	}





	protected String getDescriptionKey(){
		return null;
//		return Abilities.getDescription(this).isEmpty() ? "empty" : Abilities.getDescription(this);
	}

	public String getTranslatedDescription(){
		return HSLibs.translateKey(this.getDescriptionKey()+".desc");
	}



	@Override
	public String getLocalizedInAbilityList() {
		// TODO 自動生成されたメソッド・スタブ
		return this.getLocalized();
	}





	@Override
	public int compareTo(IAbility o) {
		// TODO 自動生成されたメソッド・スタブ
		return this.getRegistryName().toString().compareTo(o.getRegistryName().toString());
	}







}
