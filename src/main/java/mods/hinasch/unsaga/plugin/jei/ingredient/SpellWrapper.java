package mods.hinasch.unsaga.plugin.jei.ingredient;

import mods.hinasch.unsagamagic.spell.ISpell;

public class SpellWrapper implements Comparable<SpellWrapper>{

	final ISpell spell;

	public SpellWrapper(ISpell spell){
		this.spell = spell;
	}

	public ISpell getSpell(){
		return this.spell;
	}

	@Override
	public int compareTo(SpellWrapper o) {
		// TODO 自動生成されたメソッド・スタブ
		return this.getSpell().compareTo(o.getSpell());
	}
}
