package mods.hinasch.unsaga.util;

import com.google.common.collect.ImmutableMap;

public class BlockableWeapon {

	final int guardProgressTime;
	final int guardCoolingTime;

	private BlockableWeapon(int a,int b){
		this.guardCoolingTime = a;
		this.guardProgressTime = b;
	}
	public int coolingTime(){
		return this.guardCoolingTime;
	}

	public int guardTime(){
		return this.guardProgressTime;
	}

	private static final BlockableWeapon DEFAULT = new BlockableWeapon(10,10);
	private static final ImmutableMap<ToolCategory,BlockableWeapon> map;

	public static BlockableWeapon get(ToolCategory cate){
		return map.getOrDefault(cate, DEFAULT);
	}
	static{
		ImmutableMap.Builder<ToolCategory,BlockableWeapon> b = ImmutableMap.builder();
		b.put(ToolCategory.KNIFE,new BlockableWeapon(15,4));
		b.put(ToolCategory.SWORD,new BlockableWeapon(18,7));
		b.put(ToolCategory.STAFF,new BlockableWeapon(17,6));
		map = b.build();
	}
}
