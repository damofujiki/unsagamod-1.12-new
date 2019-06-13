package mods.hinasch.unsaga.skillpanel;

import mods.hinasch.lib.registry.RegistryUtil;


public class SkillPanels {

	public static final ISkillPanel DUMMY = SkillPanelInitializer.get(RegistryUtil.EMPTY.toString());
	//宝箱関連

	public static final ISkillPanel LOCKSMITH = SkillPanelInitializer.get("unlock");

	public static final ISkillPanel DEFUSE = SkillPanelInitializer.get("defuse");;
	/** 見破る*/

	public static final ISkillPanel SHARP_EYE = SkillPanelInitializer.get("penetration");

	public static final ISkillPanel FORTUNE = SkillPanelInitializer.get("fortune");

	public static final ISkillPanel AVOID_TRAP = SkillPanelInitializer.get("avoid_trap");
//	public static final ISkillPanel CHEST_MASTER = SkillPanelInitializer.get("unlock");
	//店
	/** 目利き（隠し商品発見・アビ引き出し率アップ）*/

	public static final ISkillPanel ARTISTE = SkillPanelInitializer.get("artiste");
	/** 値切り*/

	public static final ISkillPanel MONGER = SkillPanelInitializer.get("discount");

	public static final ISkillPanel MAHARAJA = SkillPanelInitializer.get("gratuity");

	public static final ISkillPanel FASHIONABLE = SkillPanelInitializer.get("fashionable");
	//術

	public static final ISkillPanel ARCANE_TONGUE = SkillPanelInitializer.get("arcane_tongue");

	public static final ISkillPanel MAGIC_BLEND = SkillPanelInitializer.get("magic_blend");

	public static final ISkillPanel MAGIC_EXPERT = SkillPanelInitializer.get("magic_expert");
	//コンバット

	public static final ISkillPanel WEAPON_MASTER = SkillPanelInitializer.get("weapon_master");

	public static final ISkillPanel PUNCH = SkillPanelInitializer.get("punch");

	public static final ISkillPanel GUN = SkillPanelInitializer.get("gun");
	/** 肉の鎧（防御がアップ）*/

	public static final ISkillPanel IRON_BODY = SkillPanelInitializer.get("iron_body");
	/** 不屈（精神がアップ）*/

	public static final ISkillPanel FORTIFY = SkillPanelInitializer.get("fortify");
	/** タフネス（落下耐性）*/

	public static final ISkillPanel TOUGHNESS = SkillPanelInitializer.get("toughness");
	/** 鋼の意思（ウィザー・腹減り耐性）*/

	public static final ISkillPanel IRON_WILL = SkillPanelInitializer.get("iron_will");
	/** 聞き耳*/

	public static final ISkillPanel EAVESDROP = SkillPanelInitializer.get("eavesdrop");
	/** 盾（盾のアビリティ発動）*/

	public static final ISkillPanel SHIELD = SkillPanelInitializer.get("shield");

	//案内

	public static final ISkillPanel GUIDE_ROAD = SkillPanelInitializer.get("road_adviser");

	public static final ISkillPanel GUIDE_CAVE = SkillPanelInitializer.get("cavern_explorer");

	//アイテム

	public static final ISkillPanel TOOL_CUSTOMIZE = SkillPanelInitializer.get("tool_customize");
	/** 節約魂*/

	public static final ISkillPanel THRIFT_SAVER = SkillPanelInitializer.get("thrift_saver");
	/** 簡易修理*/

	public static final ISkillPanel QUICK_FIX = SkillPanelInitializer.get("quick_fix");
	//ネガティヴ

	public static final ISkillPanel PHOBIA_ZOMBIE = SkillPanelInitializer.get("zombie_phobia");

	public static final ISkillPanel PHOBIA_CREEPER = SkillPanelInitializer.get("creeper_phobia");

	public static final ISkillPanel PACIFIST_ANIMALS = SkillPanelInitializer.get("pacifist_animals");
	//ファミリア

	public static final ISkillPanel FAMILIAR_EARTH = SkillPanelInitializer.get("familiar_earth");

	public static final ISkillPanel FAMILIAR_FIRE = SkillPanelInitializer.get("familiar_fire");

	public static final ISkillPanel FAMILIAR_METAL = SkillPanelInitializer.get("familiar_metal");

	public static final ISkillPanel FAMILIAR_WATER = SkillPanelInitializer.get("familiar_water");

	public static final ISkillPanel FAMILIAR_WOOD = SkillPanelInitializer.get("familiar_wood");
	//動作系

	public static final ISkillPanel SWIMMING = SkillPanelInitializer.get("swimming");

	public static final ISkillPanel ADAPTABILITY = SkillPanelInitializer.get("adaptability");
	/** 身のこなし*/

	public static final ISkillPanel SMART_MOVE = SkillPanelInitializer.get("smart_move");

	public static final ISkillPanel OBSTACLE_CROSSING = SkillPanelInitializer.get("obstacle_crossing");
	//その他
	/** 地味（身のこなしの効力アップ）*/

	public static final ISkillPanel INCONSPICIOUS = SkillPanelInitializer.get("inconspicious");

	//実装するか微妙
//
//
//	public static final SkillPanel ACCESSORY_FORGE = new SkillPanel("accessory_forge");
//	public static final SkillPanel WATCHING_OUT = new SkillPanel("watching_out");
//	public static final SkillPanel KNOWLEDGE_BUILDINGS = new SkillPanel("knowledge_buildings");

//	public static void register(){
//		put(PUNCH,ARCANE_TONGUE,MAGIC_BLEND,LOCKSMITH,DEFUSE,SHARP_EYE,ARTISTE);
//		put(MONGER,MAHARAJA,FORTUNE,PHOBIA_CREEPER,PHOBIA_ZOMBIE,PACIFIST_ANIMALS);
//		put(IRON_BODY,TOUGHNESS,GUIDE_ROAD,GUIDE_CAVE);
//		put(TOOL_CUSTOMIZE,IRON_WILL,FASHIONABLE,GUN);
//		put(SWIMMING,ADAPTABILITY,SHIELD,QUICK_FIX,FORTIFY,INCONSPICIOUS);
//		put(FAMILIAR_EARTH,FAMILIAR_FIRE,FAMILIAR_WATER,FAMILIAR_METAL,FAMILIAR_WOOD);
//		put(THRIFT_SAVER,EAVESDROP,WEAPON_MASTER,MAGIC_EXPERT,SMART_MOVE,OBSTACLE_CROSSING);
//	}
//
//	public static void put(SkillPanel... panels){
//		SkillPanelInitializer.instance().register(panels);
//	}
}
