package mods.hinasch.unsaga.minsaga.classes;

import mods.hinasch.lib.registry.RegistryUtil;


public class PlayerClasses {

	/** すっぴん*/
	public static final IPlayerClass NO_CLASS = PlayerClassRegisterer.get(RegistryUtil.EMPTY.toString());
	/** 城塞騎士、盾スキルの発動率が上がる*/
	public static final IPlayerClass HEAVY_FIGHTER = PlayerClassRegisterer.get("heavy_fighter");
	/** 鍵屋、鍵解除、罠解除を持つ*/
	public static final IPlayerClass LOCKSMITH = PlayerClassRegisterer.get("locksmith");
	/** ウィザード、intが高くなる*/
	public static final IPlayerClass WIZARD = PlayerClassRegisterer.get("wizard");
	/** 製図家、道案内・洞窟案内を持つ*/
	public static final IPlayerClass CARTOGRAPHER = PlayerClassRegisterer.get("cartographer");
	/** 細工師、節約魂を持つ*/
	public static final IPlayerClass CRAFTSMAN = PlayerClassRegisterer.get("craftsman");
	public static final IPlayerClass ARCHER = PlayerClassRegisterer.get("archer");
}
