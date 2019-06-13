package mods.hinasch.unsaga.init;

import mods.hinasch.unsaga.UnsagaMod;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Config(modid = UnsagaMod.MODID,category = "")
public class UnsagaConfigHandlerNew {

	private UnsagaConfigHandlerNew(){

	}

	@Mod.EventBusSubscriber(modid = UnsagaMod.MODID)
	private static class EventHandler{

		private EventHandler(){

		}
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent e){
			UnsagaMod.logger.trace("config here", e.getConfigID());
			if(e.getModID().equals(UnsagaMod.MODID)){
				ConfigManager.sync(UnsagaMod.MODID, Config.Type.INSTANCE);
			}
		}
	}
//	@Mod.EventHandler
//	public void preInit(FMLPreInitializationEvent ev){
//		HSLibs.registerEvent(this);
//	}

	@Config.LangKey("gui.config.unsaga.general.name")
	public static final General GENERAL = new General();
	@Config.LangKey("gui.config.unsaga.lp.name")
	public static final LP LP_SETTING = new LP();
	@Config.LangKey("gui.config.unsaga.mob.name")
	public static final Enemy ENEMY = new Enemy();
	@Config.LangKey("gui.config.unsaga.aspect.name")
	public static final Aspect ASPECT = new Aspect();
	@Config.LangKey("gui.config.unsaga.generation.name")
	public static final Generation GENERATION = new Generation();
	@Config.LangKey("gui.config.unsaga.display.name")
	public static final Display DISPLAY = new Display();
	@Config.LangKey("gui.config.unsaga.panel_growth.name")
	public static final PanelGrowth PANEL_GROWTH = new PanelGrowth();
	public static final Debug DEBUG = new Debug();

	public static class General{


		/**
		 * LPシステムを有効にするかどうか
		 * （オフの場合、技がクリティカル威力に。LP防御力はクリティカル防御力に
		 */
		@Config.LangKey("gui.config.unsaga.general.enable_lp.name")
		@Config.Comment("Enables LP System.")
		public boolean enableLPSystem = true;

		/**
		 * LPシステムがオフの時、かわりのクリティカルシステムを有効にするかどうか
		 */
		@Config.LangKey("gui.config.unsaga.general.enable_crit.name")
		@Config.Comment("When LP System Disabled,Whether Enables Critical & Miss Hit or Not.")
		public boolean enableCritical = true;

		/**
		 * 敵の自動回復をオンにするかどうか
		 */
		@Config.LangKey("gui.config.unsaga.general.enable_enemy_auto_heal.name")
		@Config.Comment("Enables Mob's Regeneration.")
		public boolean enableEnemyAutoHeal = true;
		/**
		 * ターゲティングの有効範囲
		 */
		@Config.LangKey("gui.config.unsaga.general.targetting_range.name")
		@Config.Comment("Spell/Tech Targetting Range[Horizontal,Vertical]")
		@Config.RangeDouble(min=0,max = 1000)
		public double[] targettingRange = {20.0D,10.0D};

		/**
		 * 耐久力の表示機能を使うかどうか
		 */
		@Config.LangKey("gui.config.unsaga.general.display_durability.name")
		@Config.Comment("Displays Durability In Tooltip.")
		public boolean enableDisplayDurability = false;

		/**
		 * 商人の小屋を生成するかどうか
		 */
		@Config.LangKey("gui.config.unsaga.general.gen_merchant_house.name")
		@Config.Comment("Allows Genelation Merchant House.")
		public boolean enableGenerationMerchantHouse = true;

		@Config.LangKey("gui.config.unsaga.general.display_alt_material.name")
		@Config.Comment("Displays Hidden Alternative Material")
		public boolean isDisplayAltMaterial = false;
	}
	public static class LP{



		/**
		 * プレイヤーのデフォルトのLP
		 */
		@Config.LangKey("gui.config.unsaga.lp.default_lp.name")
		@Config.Comment("Player's Default LP")
		public int defaultPlayerLP = 8;

		/**
		 * 最大LPが設定されていない敵を自動で最大LPを設定する際の
		 * Max Healthとの比率
		 */
		@Config.LangKey("gui.config.unsaga.lp.ratio_define_lp.name")
		@Config.Comment("Max Health/Max LP Ratio")
		public float ratioMaxLP = 0.25F;

		@Config.LangKey("gui.config.unsaga.lp.base_lp_tameable.name")
		@Config.Comment("Additional Max LP of Tameable Mob")
		public int baseLPTameable = 3;

		@Config.LangKey("gui.config.unsaga.lp.lp_saturation_threshold.name")
		@Config.Comment("Threshold LP Saturation")
		public float thresholdLPSaturation = 20.0F;

		/**
		 * LPの減りやすさ
		 */
		@Config.LangKey("gui.config.unsaga.lp.lp_decr_slope.name")
		@Config.Comment("LP Decrease Frequency")
		public float rateLPDecr = 0.15F;

		/**
		 * DexとLPの影響係数
		 */
		@Config.LangKey("gui.config.unsaga.lp.lp_dex_ratio.name")
		@Config.Comment("LP Decr./DEX Ratio[(LP DEF-DEX)*Ratio]")
		public float dexLPRatio = -0.5F;
	}

	public static class Enemy{

		@Config.LangKey("gui.config.unsaga.enemy.base_rate.name")
		@Config.Comment({"Base Spawn Rate of Monsters[Base Spawn Rate*Monster Spawn Rate]","Set 0 to Disable Spawn."})
		public int spawnBase = 10;

		/**
		 * レブナント
		 */
		@Config.LangKey("gui.config.unsaga.enemy.rate_revenant.name")
		@Config.Comment("Revenant")
		public int spawnRevenant = 3;
		/**
		 * シグナルツリー
		 */
		@Config.LangKey("gui.config.unsaga.enemy.rate_signal_tree.name")
		@Config.Comment("Signal Tree")
		public int spawnSignalTree = 3;
		/**
		 * ラッフルツリー
		 */
		@Config.LangKey("gui.config.unsaga.enemy.rate_ruffle_tree.name")
		@Config.Comment("Ruffle Tree")
		public int spawnRuffleTree = 3;

		@Config.LangKey("gui.config.unsaga.enemy.rate_storm_eater.name")
		@Config.Comment("Storm Eater")
		public int spawnStormEater = 3;

		@Config.LangKey("gui.config.unsaga.enemy.rate_poison_eater.name")
		@Config.Comment("Poison Eater")
		public int spawnPoisonEater = 3;
	}

	public static class Aspect{

		@Config.LangKey("gui.config.unsaga.aspect.process_speed.name")
		@Config.Comment("Process Interval of Five Elements Aspect Equilibrium(tick)")
		public int processSpeed = 25;

		@Config.LangKey("gui.config.unsaga.aspect.effect_to_spell.name")
		@Config.Comment("Aspect/Spell Max Amplify")
		public float maxAmplify = 2.5F;
	}
	public static class Generation{

		@Config.LangKey("gui.config.unsaga.generation.force_merchant_house.name")
		@Config.Comment("Forces Generation Merchant House When First Spawn.")
		public boolean enableForceMerchantHouseFirst = false;

		@Config.LangKey("gui.config.unsaga.generation.rate_bonus_chest.name")
		@Config.Comment("Generation Rate of Bonus Field Chest")
		@Config.RangeInt(min = 0,max = 20)
		public int bonusChestGenerationRate = 10;

		@Config.LangKey("gui.config.unsaga.generation.rate_drop_chest.name")
		@Config.Comment("Spawn Rate of Mob Drop Chest")
		public int mobDropChestRate = 10;


	}

	public static class Debug{
		@Config.LangKey("gui.config.unsaga.debug.always_day.name")
		public boolean enableAlwaysDay = false;

		@Config.LangKey("gui.config.unsaga.debug.minsaga_quick_fitting.name")
		public boolean enableMinsagaQuickFitting = false;
	}
	public static class Display{

		@Config.LangKey("gui.config.unsaga.display.offset_lp.name")
		@Config.Comment("Offset Player LP HUD Display")
		public int[] offsetLPDisplay = {0,0};

		@Config.LangKey("gui.config.unsaga.display.offset_aspect.name")
		@Config.Comment("Offset Aspect HUD Display")
		public int[] offsetAspect = {60,5};

		@Config.LangKey("gui.config.unsaga.display.same_team_lp.name")
		@Config.Comment("Displays LP of Mob Nearby Player")
		public boolean isRenderNearLP = true;
	}

	public static class PanelGrowth{
		@Config.LangKey("gui.config.unsaga.panel_growth.multiply_skill_point.name")
		@Config.Comment("XP*Skill Point Multiply Rate")
		public float multiplySkillPoint = 1.0F;

		@Config.LangKey("gui.config.unsaga.panel_growth.base_time_skill.name")
		@Config.Comment("Map Skill Base Time[Sec]")
		public int mapSkillBaseTime = 20;

		@Config.LangKey("gui.config.unsaga.panel_growth.base_repair.name")
		@Config.Comment("Base Damage of Repair Skill")
		public int repairBaseDamage = 10;
	}
}
