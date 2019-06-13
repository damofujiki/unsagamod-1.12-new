package mods.hinasch.unsaga.common.panel_bonus;

import java.util.Set;

import com.google.common.collect.ImmutableSet;

public class Line implements IPanelJoint{
	Set<Hex> list;

	public Line(Hex hex1,Hex hex2,Hex hex3){
		this.list = ImmutableSet.of(hex1,hex2,hex3);
	}

	public Set<Hex> hexes(){
		return this.list;
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
		return 3;
	}

	@Override
	public int getPriority() {
		// TODO 自動生成されたメソッド・スタブ
		return 3;
	}

	@Override
	public String getLayoutName() {
		// TODO 自動生成されたメソッド・スタブ
		return "Line";
	}

}