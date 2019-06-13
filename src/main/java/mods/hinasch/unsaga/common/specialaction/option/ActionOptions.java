package mods.hinasch.unsaga.common.specialaction.option;

import mods.hinasch.unsaga.common.specialaction.IActionPerformer.TargetType;

/**
 * 術使用時のオプションに使う
 *
 */
public class ActionOptions {

	public static IActionOption NONE = new ActionOption("none");
	public static IActionOption SELF = new ActionOption("self");
	public static IActionOption TARGET = new ActionOption("target"){

		@Override
		public TargetType getTargetType(){
			return TargetType.TARGET;
		}
	};
	public static IActionOption OFF_HAND = new ActionOptionSlotSelector("off_hand");
	public static IActionOption EQUIPPED = new ActionOptionSlotSelector("equipped");
	public static IActionOption HEAD = new ActionOptionSlotSelector("head");
	public static IActionOption BODY = new ActionOptionSlotSelector("body");
	public static IActionOption FEET = new ActionOptionSlotSelector("feet");
	public static IActionOption LEGS = new ActionOptionSlotSelector("legs");
	public static IActionOption POSITION = new ActionOption("position"){
		@Override
		public TargetType getTargetType(){
			return TargetType.POSITION;
		}
	};
	public static IActionOption IGNITABLE = new ActionOption("ignitable");
	public static IActionOption SERVANT_MELEE = new ActionOption("servant_melee");
	public static IActionOption SERVANT_BOW = new ActionOption("servant_bow");
	public static IActionOption MAGNET_ITEM = new ActionOption("magnet_item");
	public static IActionOption MAGNET_ALL = new ActionOption("magnet_all");
}
