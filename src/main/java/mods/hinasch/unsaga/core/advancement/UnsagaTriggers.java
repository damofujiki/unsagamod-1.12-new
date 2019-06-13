package mods.hinasch.unsaga.core.advancement;

public class UnsagaTriggers {

    public static final UnsagaCustomTrigger OPEN_GUI_UNSAGA= new UnsagaCustomTrigger("open_gui_unsaga");
    public static final UnsagaCustomTrigger OPEN_GUI_BARTERING = new UnsagaCustomTrigger("open_gui_bartering");
    public static final UnsagaCustomTrigger OPEN_GUI_CHEST = new UnsagaCustomTrigger("open_gui_chest");
    public static final UnsagaCustomTrigger CHEST_OPEN = new UnsagaCustomTrigger("chest_open");
    public static final UnsagaCustomTrigger CHEST_FIND = new UnsagaCustomTrigger("chest_find");
    public static final UnsagaCustomTrigger CHEST_UNLOCK_MAGIC = new UnsagaCustomTrigger("chest_unlock_magic");
    public static final UnsagaCustomTrigger GET_TABLET = new UnsagaCustomTrigger("get_tablet");
    public static final UnsagaCustomTrigger LEARN_FORBIDDEN_SPELL = new UnsagaCustomTrigger("learn_forbidden_spell");
    public static final UnsagaCustomTrigger CREATE_NEW_SPELL = new UnsagaCustomTrigger("create_new_spell");
    public static final UnsagaCustomTrigger BEGIN_BARTERING = new UnsagaCustomTrigger("begin_bartering");
    public static final UnsagaCustomTrigger BEGIN_FORGE = new UnsagaCustomTrigger("begin_forge");
    public static final UnsagaCustomTrigger BEGIN_DECIPHERING = new UnsagaCustomTrigger("begin_deciphering");
    public static final UnsagaCustomTrigger BEGIN_SKILL_PANEL = new UnsagaCustomTrigger("begin_skill_panel");
    public static final UnsagaCustomTrigger USE_MAP_SKILL = new UnsagaCustomTrigger("use_map_skill");
   public static final UnsagaCustomTrigger FINISH_DECIPHERING = new UnsagaCustomTrigger("finish_deciphering");
    public static final UnsagaCustomTrigger READY_MAGIC_ITEM = new UnsagaCustomTrigger("ready_magic_item");
    public static final UnsagaCustomTrigger TRANSCRIPT_SPELL = new UnsagaCustomTrigger("transcript_spell");
    public static final UnsagaCustomTrigger LEARN_ABILITY = new UnsagaCustomTrigger("learn_ability");
    public static final UnsagaCustomTrigger LEARN_TECH = new UnsagaCustomTrigger("learn_specialmove");
    public static final UnsagaCustomTrigger FORGE_STEEL = new UnsagaCustomTrigger("forge_steel");
    public static final UnsagaCustomTrigger FORGE_DEBRIS2 = new UnsagaCustomTrigger("forge_debris2");
    public static final UnsagaCustomTrigger FORGE_STEEL2 = new UnsagaCustomTrigger("forge_steel2");
    public static final UnsagaCustomTrigger FORGE_DAMASCUS = new UnsagaCustomTrigger("forge_damascus");
    public static final UnsagaCustomTrigger BREAK_CHEST = new UnsagaCustomTrigger("break_chest");
    public static final UnsagaCustomTrigger FIRST_FORGE = new UnsagaCustomTrigger("first_forge");
    public static final UnsagaCustomTrigger START_BLEND = new UnsagaCustomTrigger("start_blend");
//    public static final UnsagaCustomTrigger FIRST_DECIPHER = new UnsagaCustomTrigger("first_decipher");
    public static final UnsagaCustomTrigger BARTERING_TIER1 = new UnsagaCustomTrigger("bartering_tier1");
    public static final UnsagaCustomTrigger BARTERING_TIER2 = new UnsagaCustomTrigger("bartering_tier2");
    public static final UnsagaCustomTrigger BARTERING_TIER3 = new UnsagaCustomTrigger("bartering_tier3");
    public static final UnsagaCustomTrigger LP_RESTORE = new UnsagaCustomTrigger("lp_restore");
    public static final UnsagaCustomTrigger LP_RESTORE_OTHERS= new UnsagaCustomTrigger("lp_restore_others");
    public static final UnsagaCustomTrigger FIRST_SPARK = new UnsagaCustomTrigger("first_spark");
    public static final UnsagaCustomTrigger FIRST_ABILITY = new UnsagaCustomTrigger("first_ability");

    public static final UnsagaCustomTrigger[] TRIGGER_ARRAY = new UnsagaCustomTrigger[] {
    		OPEN_GUI_UNSAGA,GET_TABLET,LP_RESTORE,LP_RESTORE_OTHERS
    		,BEGIN_DECIPHERING,FINISH_DECIPHERING,READY_MAGIC_ITEM,TRANSCRIPT_SPELL
    		,LEARN_FORBIDDEN_SPELL,CREATE_NEW_SPELL
    		,BEGIN_BARTERING,BARTERING_TIER1,BARTERING_TIER2
    		,BEGIN_FORGE,FORGE_STEEL,FORGE_DEBRIS2,FORGE_STEEL2,FORGE_DAMASCUS
    		,FIRST_SPARK,FIRST_ABILITY,BEGIN_SKILL_PANEL,USE_MAP_SKILL
    		,CHEST_OPEN,CHEST_FIND,CHEST_UNLOCK_MAGIC

    };
}
