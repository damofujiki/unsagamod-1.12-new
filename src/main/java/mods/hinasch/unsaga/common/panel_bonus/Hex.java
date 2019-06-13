package mods.hinasch.unsaga.common.panel_bonus;

import mods.hinasch.lib.misc.XY;

/** XYが等価ならequalsなHexになる。*/
public class Hex implements Comparable<Hex>{

	public static final Hex NULL_HEX = new Hex(0,0,-1);

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((position == null) ? 0 : position.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Hex other = (Hex) obj;
		if (position == null) {
			if (other.position != null)
				return false;
		} else if (!position.equals(other.position))
			return false;
		return true;
	}

	final XY position;
	final int slot;
	public Hex(int x,int y,int slot){
		this.position = new XY(x,y);
		this.slot = slot;
	}

	public boolean isNullHex(){
		return this.equals(NULL_HEX);
	}
	public int getSlotNumber(){
		return this.slot;
	}

	public XY getPosition(){
		return this.position;
	}

	public int getPriority(){
		return this.getPosition().getX() + this.getPosition().getY();
	}
	public boolean isJointableSkill(Hex hex){
		return true;
	}

	@Override
	public String toString(){
		if(this.equals(NULL_HEX)){
			return "NULL_HEX!";
		}
		return this.getPosition().toString();
	}

	@Override
	public int compareTo(Hex o) {
		// TODO 自動生成されたメソッド・スタブ
		return Integer.compare(getPriority(), o.getPriority());
	}
}