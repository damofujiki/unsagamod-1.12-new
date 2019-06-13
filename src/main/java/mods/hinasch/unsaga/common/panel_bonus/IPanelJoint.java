package mods.hinasch.unsaga.common.panel_bonus;

import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import net.minecraft.inventory.IInventory;

/**
 *ボーナスになるレイアウトの基本インターフェース。
 *
 * クラスが同じ(joint&jointなど)
 *で構成ヘックスが同じなら同じジョイントになる（逆パターンが同じになる）
 */
public interface IPanelJoint extends Comparable<IPanelJoint>{


	public int getPriority();
	default boolean isSame(IPanelJoint jointed){
		if(this.getClass()!=jointed.getClass()){
			return false;
		}
		return jointed.hexes().stream().allMatch(in -> this.hexes().contains(in));
	}

	default String dump(IInventory inv){
		return Joiner.on(",").join(hexes().stream().map(in -> in.getPosition().toString()+":"+inv.getStackInSlot(in.getSlotNumber()).toString()).collect(Collectors.toList()));
	}

	/**
	 * このジョイント内にotherの構成ヘックスを一つでも含むかどうか。
	 * @param jointed 比べるジョイント
	 * @return
	 */
	default boolean containsAny(IPanelJoint other){
		return !Sets.intersection(other.hexes(), this.hexes()).isEmpty();
//		return other.getSet().stream().anyMatch(in -> this.getList().contains(in));
	}

	default int compareLayouts(IPanelJoint other){
		if(other.getPriority()==this.getPriority()){
			return Integer.compare(this.hexes().stream().mapToInt(in -> in.getPriority()).sum(), other.hexes().stream().mapToInt(in -> in.getPriority()).sum());
		}
		return Integer.compare(this.getPriority(), other.getPriority());
	}

	/** ボーナスレイアウトの構成ヘックスをセットで取得*/
	public Set<Hex> hexes();

	public int getBonusMultiply();

	public String getLayoutName();
}
