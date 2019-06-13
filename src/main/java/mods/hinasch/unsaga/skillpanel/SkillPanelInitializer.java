package mods.hinasch.unsaga.skillpanel;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import mods.hinasch.lib.misc.JsonApplier;
import mods.hinasch.lib.misc.JsonApplier.IJsonParser;
import mods.hinasch.lib.registry.RegistryUtil;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.item.misc.skillpanel.SkillPanelCapability;
import mods.hinasch.unsaga.element.FiveElements;
import mods.hinasch.unsaga.element.FiveElements.Type;
import mods.hinasch.unsaga.init.UnsagaItems;
import mods.hinasch.unsaga.skillpanel.SkillPanel.IconType;
import mods.hinasch.unsaga.status.UnsagaStatus;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

public class SkillPanelInitializer{

	protected static SkillPanelInitializer INSTANCE;

	protected static IForgeRegistry<ISkillPanel> REGISTRY;

	public static enum Rarity{
		MIRACLE("miracle",2),UTRLA_RARE("ultrarare",5),RARE("rare",10)
		,UNCOMMON("uncommon",20),COMMON("common",30);

		final int rarity;
		final String name;
		private Rarity(String name,int rarity){
			this.rarity = rarity;
			this.name = name;
		}

		public int value(){
			return this.rarity;
		}

		public static Rarity fromString(String str){
			for(Rarity r:Rarity.values()){
				if(r.name.equals(str)){
					return r;
				}
			}
			return Rarity.COMMON;
		}
	}

	//	public static final AttributeModifier SWIM_MODIFIER = new AttributeModifier(UUID.fromString("509167a3-a2ef-42fb-9dda-f4258b6a88f7"),"skillPanel.swim",30.0D,Statics.OPERATION_INCREMENT);
	public static final ResourceLocation RES_PANEL_GROWTH = new ResourceLocation(UnsagaMod.MODID,"data/panel_growth.json");
	public static final ResourceLocation RES_PANEL_PROP = new ResourceLocation(UnsagaMod.MODID,"data/skill_panel.json");
	public static final ResourceLocation SKILL_PANEL = new ResourceLocation(UnsagaMod.MODID,"skill_panel");

	//	public static final SkillPanel.Property DEF_PROP = new SkillPanel.Property(null, null);
	//	private PropertyRegistry registryProperty = new PropertyRegistry();
	//	private PanelBonusRegistry registryPanelBonus = new PanelBonusRegistry();

	public static class PanelDataParser implements IJsonParser{

		String name;
		Rarity rarity;
		IconType icon;

		public PanelDataParser(JsonObject jo){
			this.parse(jo);
		}

		@Override
		public void parse(JsonObject jo) {
			this.name = jo.get("name").getAsString();
			this.rarity = Rarity.fromString(jo.get("rarity").getAsString());
			this.icon = IconType.fromString(jo.get("icon").getAsString());
		}

	}
	public static class GrowthParser implements IJsonParser{

		String name;
		Map<IAttribute,Integer> map = Maps.newHashMap();

		public GrowthParser(JsonObject jo){
			this.parse(jo);
		}
		@Override
		public void parse(JsonObject jo) {
			this.name = jo.get("name").getAsString();
			map.put(SharedMonsterAttributes.MAX_HEALTH,this.getInt(jo, "life"));
			map.put(SharedMonsterAttributes.ATTACK_DAMAGE,this.getInt(jo, "str"));
			map.put(UnsagaStatus.DEXTALITY,this.getInt(jo, "dex"));
			map.put(UnsagaStatus.INTELLIGENCE,this.getInt(jo, "int"));
			map.put(UnsagaStatus.MENTAL,this.getInt(jo, "mental"));

			map.put(UnsagaStatus.getAttribute(Type.EARTH),this.getInt(jo, "earth"));
			map.put(UnsagaStatus.getAttribute(Type.FIRE),this.getInt(jo, "fire"));
			map.put(UnsagaStatus.getAttribute(Type.WATER),this.getInt(jo, "water"));
			map.put(UnsagaStatus.getAttribute(Type.METAL),this.getInt(jo, "metal"));
			map.put(UnsagaStatus.getAttribute(Type.WOOD),this.getInt(jo, "wood"));
		}

	}

	public static SkillPanelInitializer instance(){
		if(INSTANCE==null){
			INSTANCE = new SkillPanelInitializer();
		}
		return INSTANCE;
	}
	protected SkillPanelInitializer(){

	}

	@SubscribeEvent
	public void makeRegistry(RegistryEvent.NewRegistry ev){
		REGISTRY = new RegistryBuilder().setName(SKILL_PANEL).setType(ISkillPanel.class)
		.setIDRange(0, 4096).setDefaultKey(RegistryUtil.EMPTY).create();
	}

	public void init() {
		// TODO 自動生成されたメソッド・スタブ
		//		EventSkillPanel.register();
	}


	public void preInit() {
		// TODO 自動生成されたメソッド・スタブ
		//		this.registerObjects();
		//		JsonApplier.create(RES_PANEL_PROP, PanelDataParser::new, in -> this.registryProperty).parseAndApply();
		//		JsonApplier.create(RES_PANEL_GROWTH, GrowthParser::new, in -> this.registryPanelBonus).parseAndApply();
	}



	//	public static SkillPanel.Property getPanelProperty(SkillPanel pane){
	//		return instance().registryProperty.get(pane);
	//	}

	protected void registerObjects() {
		//		SkillPanels.register();
	}

	@SubscribeEvent
	public void registerPanels(RegistryEvent.Register<ISkillPanel> ev){
		SkillPanel.Factory f = new SkillPanel.Factory();
		f.register(new SkillPanel.Builder("dummy"));
		//宝箱関連
		f.register(new SkillPanel.Builder("unlock"));
		f.register(new SkillPanel.Builder("defuse"));
		/** 見破る*/
		f.register(new SkillPanel.Builder("penetration"));
		f.register(new SkillPanel.Builder("fortune"));
		f.register(new SkillPanel.Builder("avoid_trap"));
		//		f.register(new SkillPanel.Builder("chest_master").setIcon(IconType.KEY).setRarity(SkillPanel.BuilderRegistry.RARITY_ULTRARARE));
		//店
		/** 目利き（隠し商品発見・アビ引き出し率アップ）*/
		f.register(new SkillPanel.Builder("artiste"));
		/** 値切り*/
		f.register(new SkillPanel.Builder("discount"));
		f.register(new SkillPanel.Builder("gratuity"));
		f.register(new SkillPanel.Builder("fashionable"));
		//術
		f.register(new SkillPanel.Builder("arcane_tongue"));
		f.register(new SkillPanel.Builder("magic_blend"));
		f.register(new SkillPanel.Builder("magic_expert"));
		//コンバット
		f.register(new SkillPanel.Builder("weapon_master"));
		f.register(new SkillPanel.Builder("punch"));
		f.register(new SkillPanel.Builder("gun"));
		/** 肉の鎧（防御がアップ）*/
		f.register(new SkillPanelModifier.Builder("iron_body",SharedMonsterAttributes.ARMOR, new AttributeModifier(UUID.fromString("1a2e5ddc-08f5-491c-a5a1-9af9d30aae34"),"SkillPanel.Builder.ironBody",0.25D,0)));
		/** 不屈（精神がアップ）*/
		f.register(new SkillPanelModifier.Builder("fortify",UnsagaStatus.MENTAL, new AttributeModifier(UUID.fromString("fe8f7d76-6f97-4c50-b133-a4d36cbee89d"),"SkillPanel.Builder.fortify",0.25D,0)));
		/** タフネス（落下耐性）*/
		f.register(new SkillPanel.Builder("toughness"));
		/** 鋼の意思（ウィザー・腹減り耐性）*/
		f.register(new SkillPanel.Builder("iron_will"));
		/** 聞き耳*/
		f.register(new SkillPanel.Builder("eavesdrop"));
		/** 盾（盾のアビリティ発動）*/
		f.register(new SkillPanel.Builder("shield"));

		//案内
		f.register(new SkillPanel.Builder("road_adviser"));
		f.register(new SkillPanel.Builder("cavern_explorer"));

		//アイテム
		f.register(new SkillPanel.Builder("tool_customize"));
		/** 節約魂*/
		f.register(new SkillPanel.Builder("thrift_saver"));
		/** 簡易修理*/
		f.register(new SkillPanel.Builder("quick_fix"));
		//ネガティヴ
		f.register(new SkillPanelNegative.Builder("zombie_phobia",in -> in instanceof EntityZombie,SkillPanelNegative.Type.WEAKNESS));
		f.register(new SkillPanelNegative.Builder("creeper_phobia",in -> in instanceof EntityCreeper,SkillPanelNegative.Type.WEAKNESS));
		f.register(new SkillPanelNegative.Builder("pacifist_animals",in -> in instanceof EntityAnimal,SkillPanelNegative.Type.DAMAGE));
		//ファミリア
		f.register(new SkillPanelFamiliar.Builder("familiar_earth",FiveElements.Type.EARTH));
		f.register(new SkillPanelFamiliar.Builder("familiar_fire",FiveElements.Type.FIRE));
		f.register(new SkillPanelFamiliar.Builder("familiar_metal",FiveElements.Type.METAL));
		f.register(new SkillPanelFamiliar.Builder("familiar_water",FiveElements.Type.WATER));
		f.register(new SkillPanelFamiliar.Builder("familiar_wood",FiveElements.Type.WOOD));
		//動作系
		f.register(new SkillPanel.Builder("swimming"));
		f.register(new SkillPanel.Builder("adaptability"));
		/** 身のこなし*/
		f.register(new SkillPanel.Builder("smart_move"));
		f.register(new SkillPanel.Builder("obstacle_crossing"));
		//その他
		/** 地味（身のこなしの効力アップ）*/
		f.register(new SkillPanel.Builder("inconspicious"));

		JsonApplier.create(RES_PANEL_PROP, PanelDataParser::new, in -> f).parseAndApply();
		JsonApplier.create(RES_PANEL_GROWTH, GrowthParser::new, in -> f).parseAndApply();

		RegistryUtil.Helper<ISkillPanel> reg = RegistryUtil.helper(ev, UnsagaMod.MODID, "unsaga");
		f.map.forEach((in,builder)->{
			reg.register(builder.build());
		});
	}

	public static ISkillPanel get(String name){
		return RegistryUtil.getValue(REGISTRY, UnsagaMod.MODID, name);
	}
	public static Collection<ISkillPanel> getAllPanels(){
		return RegistryUtil.getValuesExceptEmpty(REGISTRY);
	}
	public void register(SkillPanel... panels){
		//		this.put(panels);
	}
	public static ItemStack createStack(ISkillPanel panel,int level){
		ItemStack stack = new ItemStack(UnsagaItems.GROWTH_PANEL,1);
		SkillPanelCapability.adapter.getCapabilityOptional(stack)
		.ifPresent(in ->{
			in.setPanel(panel);
			in.setLevel(MathHelper.clamp(level, 1, 5));
		});
		return stack;
	}
	public static class WeightedRandomPanel extends WeightedRandom.Item{

		public final ISkillPanel panel;
		public int level;
		public WeightedRandomPanel(int par1,ISkillPanel data) {
			super(par1);
			this.panel = data;
			// TODO 自動生成されたコンストラクター・スタブ
		}

		public WeightedRandomPanel(int par1,ISkillPanel data,int level){
			this(par1,data);
			this.level = level;

		}

		public WeightedRandomPanel applyModifierToWeight(int modifier){
			int modifieredWeight = this.itemWeight + modifier;
			return new WeightedRandomPanel(modifieredWeight,this.panel,this.level);
		}
		@Override
		public String toString(){
			return "["+this.panel.getUnlocalizedName()+" level:"+this.level+" weight:"+this.itemWeight+"]";
		}

		/**
		 * 全てのスキルパネル（レベル違い含む）の重み付きリストを用意する。
		 * @return
		 */
		public static List<WeightedRandomPanel> prepareAll(BiPredicate<ISkillPanel,Integer> filter){

			return SkillPanelInitializer.getAllPanels()
					.stream()
					.flatMap(panel ->IntStream.range(0, 5)
							.filter(i ->filter.test(panel, i))
							.mapToObj(i ->new WeightedRandomPanel(panel.rarity().value(),panel,i)))
					.collect(Collectors.toList());

		}
	}


}
