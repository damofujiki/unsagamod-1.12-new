package mods.hinasch.unsaga.core.net;

import mods.hinasch.lib.network.ProxyBase;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaModEvents;
import mods.hinasch.unsaga.ability.AbilityInitializer;
import mods.hinasch.unsaga.ability.specialmove.LearningFacilityHelper;
import mods.hinasch.unsaga.core.advancement.UnsagaTriggerRegisterer;
import mods.hinasch.unsaga.core.client.KeyBindingUnsaga;
import mods.hinasch.unsaga.core.entity.UnsagaEntityRegistry;
import mods.hinasch.unsaga.core.item.UnsagaCreativeTabs;
import mods.hinasch.unsaga.core.potion.UnsagaPotionInitializer;
import mods.hinasch.unsaga.core.world.WorldGeneratorUnsaga;
import mods.hinasch.unsaga.element.BiomeElementsRegistry;
import mods.hinasch.unsaga.init.UnsagaBlockRegistry;
import mods.hinasch.unsaga.init.UnsagaCapabilityRegisterer;
import mods.hinasch.unsaga.init.UnsagaGui;
import mods.hinasch.unsaga.init.UnsagaItemRegisterer;
import mods.hinasch.unsaga.init.UnsagaItems;
import mods.hinasch.unsaga.init.UnsagaLibrary;
import mods.hinasch.unsaga.init.UnsagaOreRegistry;
import mods.hinasch.unsaga.material.UnsagaIngredients;
import mods.hinasch.unsaga.material.UnsagaMaterialInitializer;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import mods.hinasch.unsaga.minsaga.MinsagaMaterialInitializer;
import mods.hinasch.unsaga.minsaga.classes.PlayerClassRegisterer;
import mods.hinasch.unsaga.skillpanel.SkillPanelInitializer;
import mods.hinasch.unsaga.skillpanel.SkillPanels;
import mods.hinasch.unsaga.villager.VillagerInitializerUnsaga;
import mods.hinasch.unsagamagic.UnsagaMagic;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class CommonProxy extends ProxyBase implements IGuiHandler{

	@Override
	public void registerRenderers() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void registerKeyHandlers() {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void registerEntityRenderers() {
		// TODO 自動生成されたメソッド・スタブ

	}

	public void initGameRegistration(){
		//アイテム
		UnsagaItemRegisterer.init();
		//素材
		UnsagaMaterialInitializer.instance().init();
		//アビリティ
		AbilityInitializer.init();
		//キャパビリティ（イベント）
		UnsagaCapabilityRegisterer.registerEvents();
		//イベント
		UnsagaModEvents.instance().regiser();
		//ライブラリ（アイテム価格・アイテム素材情報）
		UnsagaLibrary.initCatalogues();
		//ポーショｐン
		UnsagaPotionInitializer.registerEvent();
		//鉱石
		UnsagaOreRegistry.init();
		//ブロック
		UnsagaBlockRegistry.instance().registerOreDicts();
		//素材の原料
		UnsagaIngredients.init();
		//素材＜＞アイテムのリンク
//		DictionaryUnsagaMaterial.instance().init();
		//村人
		VillagerInitializerUnsaga.instance().init();
//		//カテゴリ＜＞アイテム
//		ToolCategory.registerAssociation();
		//ひらめき率
		LearningFacilityHelper.register();
		//ミンサガのカスタマイズ要素
		MinsagaMaterialInitializer.init();
		//魔法要素
		UnsagaMagic.instance().init();
		//エンティティ
		UnsagaEntityRegistry.instance().init();
		//スキルパネル
		SkillPanelInitializer.instance().init();
		//ワールドジェネレーターを登録
		GameRegistry.registerWorldGenerator(WorldGeneratorUnsaga.instance(), 8);
		//進捗の独自トリガー
		UnsagaTriggerRegisterer.register();


		//五行相
		BiomeElementsRegistry.registerBiomes();
		//クリエイティブタブのアイコン登録
		UnsagaCreativeTabs.TOOLS.setIconItemStack(UnsagaItemRegisterer.createStack(UnsagaItems.AXE,UnsagaMaterials.DAMASCUS, 0));
		UnsagaCreativeTabs.PANEL_GROWTH.setIconItemStack(SkillPanels.LOCKSMITH.getItemStack(1));
		UnsagaCreativeTabs.MISC.setIconItemStack(UnsagaMaterials.CARNELIAN.itemStack());
	}

	public void preInitGameRegistration(){
		//キャパビリティ
		UnsagaCapabilityRegisterer.registerBases();
		//素材
//		HSLibs.registerEvent(UnsagaMaterialRegisterer.instance());
		//素材の原料（生素材）
		UnsagaIngredients.preInit();
		//アビリティ
		AbilityInitializer.preInit();
		//鉱石
//		UnsagaOreRegistry.instance().preInit();
		//アイテム
		HSLibs.registerEvent(new UnsagaItemRegisterer());
		//ブロック
		HSLibs.registerEvent(UnsagaBlockRegistry.instance());
		//村人
		VillagerInitializerUnsaga.instance().preInit();
		//エンティティ
		UnsagaEntityRegistry.instance().preInit();
		//魔法
		UnsagaMagic.instance().preInit();
		//スキルパネル（パネルグロース）
		SkillPanelInitializer.instance().preInit();
		//ポーション
		HSLibs.registerEvent(new UnsagaPotionInitializer());
		//バイオーム<>五行相
		BiomeElementsRegistry.register();
		//クラス（ミンサガ）
		HSLibs.registerEvent(new PlayerClassRegisterer());
//		StateRegistry.instance().preInit();

	}
	public XYZPos getDebugPos(int bank){
		return null;
	}
	public void setDebugPos(int bank,XYZPos pos){

	}
	public void registerEvents(){

	}
	public KeyBindingUnsaga getKeyBindings(){
		return null;
	}

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return UnsagaGui.Type.fromMeta(ID).getContainer(world, player,new XYZPos(x,y,z));
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		return UnsagaGui.Type.fromMeta(ID).getGui(world, player, new XYZPos(x,y,z));
	}
}
