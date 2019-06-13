package mods.hinasch.unsaga.ability.specialmove;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;

import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.misc.JsonApplier;
import mods.hinasch.lib.misc.JsonApplier.IJsonParser;
import mods.hinasch.lib.network.PacketSound;
import mods.hinasch.lib.network.PacketUtil;
import mods.hinasch.lib.registry.RegistryUtil;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.AbilityInitializer;
import mods.hinasch.unsaga.ability.IAbility;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker.InvokeType;
import mods.hinasch.unsaga.ability.specialmove.action.ComboActionProperty;
import mods.hinasch.unsaga.ability.specialmove.action.TechActionBase;
import mods.hinasch.unsaga.ability.specialmove.action.TechActionCharged;
import mods.hinasch.unsaga.ability.specialmove.action.TechActionFactory;
import mods.hinasch.unsaga.ability.specialmove.action.TechActionFactoryHelper;
import mods.hinasch.unsaga.ability.specialmove.action.TechArrow;
import mods.hinasch.unsaga.common.specialaction.ActionBase.IAction;
import mods.hinasch.unsaga.common.specialaction.IActionBase;
import mods.hinasch.unsaga.core.entity.projectile.EntityJavelin;
import mods.hinasch.unsaga.core.entity.projectile.custom_arrow.SpecialArrowType;
import mods.hinasch.unsaga.damage.DamageComponent;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import mods.hinasch.unsaga.init.UnsagaRegistries;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TechInitializer {

	public static final ResourceLocation RES_PROP = new ResourceLocation(UnsagaMod.MODID,"data/tech.json");
	private static Map<Tech,IActionBase<TechInvoker>> TECH_ACTION_MAP = new HashMap<>();

	public static void init() {

		associate();

	}


	protected static void associate(){



		//弓系
		IAction<TechInvoker> arrowSound = in ->{
			HSLib.packetDispatcher().sendToAllAround(PacketSound.atEntity(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, in.getPerformer()), PacketUtil.getTargetPointNear(in.getPerformer()));
			return EnumActionResult.PASS;
		};

		TECH_ACTION_MAP.put(Techs.QUICK_CHECKER, TechActionFactory.quickChecker());
		TECH_ACTION_MAP.put(Techs.EXORCIST, new TechArrow().setArrowType(SpecialArrowType.EXORCIST));
		TECH_ACTION_MAP.put(Techs.PHOENIX, new TechArrow().setArrowType(SpecialArrowType.PHOENIX).addAction(arrowSound,0));
		TECH_ACTION_MAP.put(Techs.ZAPPER, new TechArrow().setArrowType(SpecialArrowType.ZAPPER).addAction(arrowSound,0));
		TECH_ACTION_MAP.put(Techs.ARROW_RAIN, TechActionFactory.arrowRain());
//		moveActionAssociation.put(doubleShot, new SpecialMoveRapidArrow(1));
//		moveActionAssociation.put(tripleShot, new SpecialMoveRapidArrow(2));
		TECH_ACTION_MAP.put(Techs.SHADOW_STITCHING, new TechArrow().setArrowType(SpecialArrowType.SHADOW_STITCH));
		TECH_ACTION_MAP.put(Techs.POWER_SHOT, new TechArrow().setArrowType(SpecialArrowType.POWER));



		//槍
		TECH_ACTION_MAP.put(Techs.SWING, TechActionFactory.swing());
		TECH_ACTION_MAP.put(Techs.GRASSHOPPER, TechActionFactory.grassHopper());
		TECH_ACTION_MAP.put(Techs.AIMING, TechActionBase.create(InvokeType.CHARGE)
				.addAction(TechActionCharged.simpleMelee(1,0,General.SPEAR,General.PUNCH,General.SWORD).setChargeThreshold(15)));
//		moveActionAssociation.put(aiming, new SpecialMoveChargedAttack(InvokeType.CHARGE,General.SPEAR,General.PUNCH,General.SWORD).setChargeThreshold(15));
		TECH_ACTION_MAP.put(Techs.ACUPUNCTURE,TechActionFactory.acupuncture());
		TECH_ACTION_MAP.put(Techs.ARM_OF_LIGHT, TechActionFactory.armOfLight());
		TECH_ACTION_MAP.put(Techs.TRIPLE_SUPREMACY, TechActionFactoryHelper.createMultiAttack(ComboActionProperty.TRIPLE_SUPREMACY,false,false,true,General.SPEAR));
		TECH_ACTION_MAP.put(Techs.JAVELIN, TechActionFactory.javelin());
		TECH_ACTION_MAP.put(Techs.WIND_MILL, TechActionFactoryHelper.createProjectile(EntityJavelin::swinging, 10));
		//杖
		TECH_ACTION_MAP.put(Techs.GRANDSLAM, TechActionFactory.grandslam());
		TECH_ACTION_MAP.put(Techs.EARTH_DRAGON, TechActionFactory.earthDragon());
		TECH_ACTION_MAP.put(Techs.PULVERIZER, TechActionFactory.connectedBlocksCrasher());
		TECH_ACTION_MAP.put(Techs.ROCK_CRASHER, TechActionFactory.rockCrasher());
		TECH_ACTION_MAP.put(Techs.GONGER, TechActionFactory.gonger());
		TECH_ACTION_MAP.put(Techs.SKULL_CRASHER, TechActionFactory.skullCrasher());
		TECH_ACTION_MAP.put(Techs.FULL_FLAT, TechActionFactory.fullFlat());
		TECH_ACTION_MAP.put(Techs.WATER_MOON, TechActionFactory.waterMoon());
		//剣
		TECH_ACTION_MAP.put(Techs.MULTI_WAY, TechActionFactory.multiWay());
		TECH_ACTION_MAP.put(Techs.KALEIDOSCOPE, TechActionFactory.caleidoscope());
		TECH_ACTION_MAP.put(Techs.GUST, TechActionFactory.gust());
		TECH_ACTION_MAP.put(Techs.HAWK_BLADE, TechActionFactory.hawkBlade());//短剣でも
		TECH_ACTION_MAP.put(Techs.CHARGE_BLADE, TechActionFactory.chargeBlade());
		TECH_ACTION_MAP.put(Techs.SMASH, TechActionBase.create(InvokeType.CHARGE)
				.addAction(TechActionCharged.simpleMelee(3,2.5F,General.SWORD).setChargeThreshold(15)));
		TECH_ACTION_MAP.put(Techs.VANDALIZE, TechActionFactory.vandalize());
		TECH_ACTION_MAP.put(Techs.TWIN_BLADE, TechActionFactoryHelper.createMultiAttack(ComboActionProperty.TWIN_BLADE,false,true,false,General.SWORD));
		TECH_ACTION_MAP.put(Techs.SCATTERED_PETALS, TechActionFactory.scatteredPetals());

		//斧

		TECH_ACTION_MAP.put(Techs.WOOD_CHOPPER, TechActionFactory.woodChopper());
		TECH_ACTION_MAP.put(Techs.FUJI_VIEW, TechActionFactory.fujiView());
		TECH_ACTION_MAP.put(Techs.TOMAHAWK, TechActionFactory.tomahawk());
		TECH_ACTION_MAP.put(Techs.SKY_DRIVE, TechActionFactory.skyDrive());
		TECH_ACTION_MAP.put(Techs.YOYO, TechActionFactory.tomahawk());
		TECH_ACTION_MAP.put(Techs.FIREWOOD_CHOPPER, TechActionFactory.firewoodChopper());
		TECH_ACTION_MAP.put(Techs.REVERSE_DELTA, TechActionFactory.reverseDelta());
		//短剣

		TECH_ACTION_MAP.put(Techs.BLITZ, TechActionFactory.blitz());
		TECH_ACTION_MAP.put(Techs.BLOODMARY, TechActionFactoryHelper.createMultiAttack(ComboActionProperty.BLOODY_MARY,false,false,false,General.SPEAR));
		TECH_ACTION_MAP.put(Techs.STUNNER, TechActionFactory.stunner());
		TECH_ACTION_MAP.put(Techs.LIGHTNING_THRUST, TechActionFactory.lightningThrust());
		TECH_ACTION_MAP.put(Techs.KNIFE_THROW, TechActionFactory.knifeThrow());
		TECH_ACTION_MAP.put(Techs.SLASH_OUT, TechActionFactory.slashOut());
		TECH_ACTION_MAP.put(Techs.CUT_IN, TechActionFactory.cutIn());

		TECH_ACTION_MAP.put(Techs.AIR_THROW, TechActionFactory.airThrow());
		TECH_ACTION_MAP.put(Techs.CALLBACK, TechActionFactory.callBack());
		TECH_ACTION_MAP.put(Techs.CYCLONE, TechActionFactory.cyclone());
		TECH_ACTION_MAP.put(Techs.SINKER, TechActionFactory.kick());
		TECH_ACTION_MAP.put(Techs.THUNDER_KICK, TechActionFactory.kick());
		TECH_ACTION_MAP.put(Techs.FLYING_KNEE, TechActionFactory.flyingKnee());
		TECH_ACTION_MAP.put(Techs.RAKSHA, TechActionFactory.raksha());
		TECH_ACTION_MAP.put(Techs.TRIANGLE_KICK, TechActionFactory.triangleKick());

		TECH_ACTION_MAP.put(Techs.SWARM, TechActionFactory.swarm());
		TECH_ACTION_MAP.put(Techs.BLESS, TechActionFactory.bless());
		TECH_ACTION_MAP.put(Techs.FEATHER_SEAL, TechActionFactory.featherSeal());
		TECH_ACTION_MAP.put(Techs.FINAL_STRIKING, TechActionFactory.finalStrike());
		TECH_ACTION_MAP.put(Techs.THUNDER_BOLT, TechActionFactory.thunderBolt());
//		techActionMap.put(WATERFALL, TechActionFactory.waterfall());

		TECH_ACTION_MAP = ImmutableMap.copyOf(TECH_ACTION_MAP);
	}

	public static Collection<Tech> getAllTechs(){
		return RegistryUtil.getSortedValues(UnsagaRegistries.ability(), in -> in instanceof Tech).stream().map(in ->(Tech)in).collect(Collectors.toList());
	}

	public static TechActionBase getAssociatedAction(Tech move){
		return (TechActionBase) TECH_ACTION_MAP.getOrDefault(move,TechActionBase.EMPTY_ACTION);
	}

	public static class JsonParserProperty implements IJsonParser{

		public String id;
//		public Tech tech;
		public DamageComponent damage;
		public int cost;
		public int coolingTime;

		public JsonParserProperty(JsonObject jo){

		}
		@Override
		public void parse(JsonObject jo) {
			// TODO 自動生成されたメソッド・スタブ
			String id = jo.get("name").getAsString();
			this.id = id;
//			this.tech =  (Tech) AbilityAPI.getAbilityByID(id);
			this.cost = Integer.valueOf(jo.get("cost").getAsString());
			this.coolingTime = Integer.valueOf(jo.get("cooling").getAsString());
			this.damage = DamageComponent.of(jo.get("damage").getAsString());
		}

	}

	public static Tech get(String name){
		return Optional.of(AbilityInitializer.get(name))
				.filter(in -> in instanceof Tech)
				.map(in ->(Tech)in)
				.orElse(Techs.GONGER);
	}
	@EventBusSubscriber(modid=UnsagaMod.MODID)
	public static class Registerer{
		@SubscribeEvent
		public void registerTechs(RegistryEvent.Register<IAbility> ev){
			RegistryUtil.Helper<IAbility> reg = RegistryUtil.helper(ev, UnsagaMod.MODID, "unsaga");
			Tech.Factory factory = new Tech.Factory();
			/** マｓルチウェイ*/
			factory.register(new Tech.Builder("multi_way"));
			/** 変幻自在*/
			factory.register(new Tech.Builder("kaleidoscope"));
			/** スマッシュ*/
			factory.register(new Tech.Builder("smash"));
			/** ヴァンダライズ*/
			factory.register(new Tech.Builder("vandalize"));
			/** 追突剣 */
			factory.register(new Tech.Builder("charge_blade"));
			/** 逆風の太刀 */
			factory.register(new Tech.Builder("gust"));
			/** ホークブレード */
			factory.register(new Tech.Builder("hawk_blade"));
			/** 二段斬り（天地二段） */
			factory.register(new Tech.Builder("twin_blade"));
			/** 乱れ雪月花*/
			factory.register(new Tech.Builder("scattered_petals").setRequireTarget(true));


			/** トマホーク*/
			factory.register(new Tech.Builder("tomahawk"));
			/** 富嶽百景 */
			factory.register(new Tech.Builder("fuji_view"));
			/** スカイドライブ*/
			factory.register(new Tech.Builder("sky_drive").setRequireTarget(true));
			/** 大木断*/
			factory.register(new Tech.Builder("wood_chopper"));
			/** 夜叉横断（未実装）*/
			factory.register(new Tech.Builder("crossing"));
			/** ヨーヨー（未実装）*/
			factory.register(new Tech.Builder("yoyo"));
			/** マキ割りダイナミック*/
			factory.register(new Tech.Builder("firewood_chopper"));
			/** 高速ナブラ */
			factory.register(new Tech.Builder("reverse_delta"));

			/** エイミング*/
			factory.register(new Tech.Builder("aiming"));
			/** 独妙点穴*/
			factory.register(new Tech.Builder("acupuncture"));
			/** スウィング*/
			factory.register(new Tech.Builder("swing"));
			/** 草伏せ*/
			factory.register(new Tech.Builder("grasshopper"));
			/** 光の腕*/
			factory.register(new Tech.Builder("arm_of_light"));
			/** 無双三段 */
			factory.register(new Tech.Builder("triple_supremacy"));
			/** ジャベリン*/
			factory.register(new Tech.Builder("javelin"));
			/** 風車 */
			factory.register(new Tech.Builder("wind_mill"));

			//杖
			/** 土龍撃*/
			factory.register(new Tech.Builder("earth_dragon"));
			/** スカルクラッシュ*/
			factory.register(new Tech.Builder("skull_crasher"));
			/** 粉砕撃*/
			factory.register(new Tech.Builder("pulverizer"));
			/** グランドスラム*/
			factory.register(new Tech.Builder("grand_slam"));
			/** どら鳴らし*/
			factory.register(new Tech.Builder("gonger"));
			/** 削岩撃*/
			factory.register(new Tech.Builder("rock_crasher"));
			/** フルフラット */
			factory.register(new Tech.Builder("full_flat"));
			/** ウォータームーン */
			factory.register(new Tech.Builder("water_moon").setRequireTarget(true));

			//弓
			/** 二本射ち（未実装）*/
			factory.register(new Tech.Builder("double_shot"));
			/** 三本射ち（未実装）*/
			factory.register(new Tech.Builder("triple_shot"));
			/** ザップショット*/
			factory.register(new Tech.Builder("zapper"));
			/** */
			factory.register(new Tech.Builder("exorcist"));
			/** 影縫い*/
			factory.register(new Tech.Builder("shadow_stitching"));
			/** フェニックスアロー*/
			factory.register(new Tech.Builder("phoenix"));
			/** アローレイン（未実装）*/
			factory.register(new Tech.Builder("arrow_rain"));
			/** クイックチェッカー */
			factory.register(new Tech.Builder("quick_checker"));
			/** 力矢 */
			factory.register(new Tech.Builder("power_shot"));

			//短剣
			factory.register(new Tech.Builder("knife_throw"));
			factory.register(new Tech.Builder("stunner"));
			factory.register(new Tech.Builder("bloody_mary"));
			factory.register(new Tech.Builder("slash_out"));
			factory.register(new Tech.Builder("cut_in"));
			factory.register(new Tech.Builder("blitz"));
			factory.register(new Tech.Builder("lightning_thrust"));

			//体術
			factory.register(new Tech.Builder("air_throw"));
			factory.register(new Tech.Builder("cyclone"));
			factory.register(new Tech.Builder("callback"));
			factory.register(new Tech.Builder("sinker"));
			factory.register(new Tech.Builder("waterfall"));
			/** 羅刹衝*/
			factory.register(new Tech.Builder("raksha"));
			factory.register(new Tech.Builder("triangle_kick"));
			factory.register(new Tech.Builder("thunder_kick"));
			factory.register(new Tech.Builder("flying_knee"));
			factory.register(new Tech.Builder("three_dragon"));

			//固有
			factory.register(new Tech.Builder("final_strike"));
			factory.register(new Tech.Builder("swarm"));
			factory.register(new Tech.Builder("bless"));
			factory.register(new Tech.Builder("feather_seal"));
			factory.register(new Tech.Builder("thunder_bolt"));

			JsonApplier.create(RES_PROP,JsonParserProperty::new,in -> factory).parseAndApply();;

			factory.get().forEach((in,builder)->{
				reg.register(new Tech(builder));
			});
		}
	}
}
