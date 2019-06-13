package mods.hinasch.unsaga.common.panel_bonus;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class Triangle implements IPanelJoint{
	Set<Hex> list;

	public Triangle(Hex hex1,Hex hex2,Hex hex3){
		this.list = ImmutableSet.of(hex1,hex2,hex3);
	}

//	@Override
//	public boolean containsAny(IPanelBonusLayout jointed) {
//		// TODO 自動生成されたメソッド・スタブ
//		return this.isSame(jointed) || jointed.getList().stream().anyMatch(in -> this.getList().contains(in));
//	}

	@Override
	public int compareTo(IPanelJoint o) {
		return this.compareLayouts(o);
	}

	@Override
	public int getBonusMultiply() {
		// TODO 自動生成されたメソッド・スタブ
		return 2;
	}

	@Override
	public Set<Hex> hexes() {
		// TODO 自動生成されたメソッド・スタブ
		return list;
	}

	@Override
	public int getPriority() {
		// TODO 自動生成されたメソッド・スタブ
		return 2;
	}

	@Override
	public String getLayoutName() {
		// TODO 自動生成されたメソッド・スタブ
		return "Triangle";
	}
}