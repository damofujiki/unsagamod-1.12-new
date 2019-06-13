package mods.hinasch.unsaga.common.panel_bonus;

import java.util.Set;

import com.google.common.collect.ImmutableSet;
import com.mojang.realmsclient.util.Pair;

/**
 *
 * ジョイントボーナス。
 *
 */
public class Jointed implements IPanelJoint{
	Set<Hex> list;

	public static Jointed of(Pair<Hex,Hex> pair){
		return new Jointed(pair.first(),pair.second());
	}
	public Jointed(Hex hex1,Hex hex2){
		this.list = ImmutableSet.of(hex1,hex2);
	}

	public Set<Hex> hexes(){
		return this.list;
	}

	@Override
	public int compareTo(IPanelJoint o) {
		return this.compareLayouts(o);
	}

	@Override
	public int getBonusMultiply() {
		// TODO 自動生成されたメソッド・スタブ
		return 1;
	}

	@Override
	public int getPriority() {
		// TODO 自動生成されたメソッド・スタブ
		return 1;
	}

	@Override
	public String getLayoutName() {
		// TODO 自動生成されたメソッド・スタブ
		return "Joint";
	}
}