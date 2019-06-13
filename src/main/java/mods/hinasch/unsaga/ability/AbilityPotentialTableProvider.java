package mods.hinasch.unsaga.ability;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;

import mods.hinasch.lib.misc.JsonApplier;
import mods.hinasch.lib.misc.JsonApplier.IJsonApplyTarget;
import mods.hinasch.lib.misc.JsonApplier.IJsonParser;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.specialmove.Tech;
import mods.hinasch.unsaga.ability.specialmove.Techs;
import mods.hinasch.unsaga.common.UnsagaWeightType;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.material.UnsagaMaterialCategory;
import mods.hinasch.unsaga.material.UnsagaMaterialInitializer;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import mods.hinasch.unsaga.util.ToolCategory;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;

public class AbilityPotentialTableProvider{

	private static final ResourceLocation HELMET = new ResourceLocation(UnsagaMod.MODID,"data/ability.helmet.json");
	private static final ResourceLocation FOOT = new ResourceLocation(UnsagaMod.MODID,"data/ability.foot.json");
	private static final ResourceLocation ARMOR = new ResourceLocation(UnsagaMod.MODID,"data/ability.armor.json");
	private static final ResourceLocation ACCESSORY = new ResourceLocation(UnsagaMod.MODID,"data/ability.accessory.json");
	private static final ResourceLocation SHIELD = new ResourceLocation(UnsagaMod.MODID,"data/ability.shield.json");
	private static final ResourceLocation SPECIAL_MOVE = new ResourceLocation(UnsagaMod.MODID,"data/specialmoves.json");

	public static final PassiveAbilityTable TABLE_PASSIVE = new PassiveAbilityTable();
	public static final TechTable TABLE_TECH = new TechTable();

	public static class ParserAbility implements IJsonParser{


		ToolCategory category;
		boolean isOverride = true;
		Set<UnsagaMaterial> m;
		NonNullList<IAbility> ability = NonNullList.withSize(4, Abilities.EMPTY);
		int armor;

		public ParserAbility(ToolCategory cate){
			this.category = cate;
		}


		@Override
		public void parse(JsonObject jo) {
			String name = jo.get("id").getAsString();

			this.m = Sets.newHashSet(UnsagaMaterialInitializer.instance().get(name));
			if(name.equals("category_wood")){
				this.m = Sets.newHashSet(UnsagaMaterialCategory.WOODS.getChildMaterials());
				isOverride = false;
			}
			Preconditions.checkNotNull(this.m,name);
			String abilityString = jo.get("ability").getAsString();
			if(abilityString!=null && !abilityString.equals("")){
				int index = 0;
				for(String id:Splitter.on(",").split(abilityString)){
					IAbility ab = AbilityAPI.getAbilityByID(id);
					Preconditions.checkArgument(!ab.isAbilityEmpty());
					if(!ab.isAbilityEmpty()){
						ability.set(index,ab);

					}
					index ++;
				}
			}


			String arm = jo.get("armor").getAsString();
			this.armor = arm.equals("") ? 0 : Integer.valueOf(arm);

		}

	}
	public static class ParserSpecialMove implements IJsonParser{


		ToolCategory category;
		UnsagaWeightType weight;
		List<IAbility> ability = Lists.newArrayList();



		@Override
		public void parse(JsonObject jo) {
			String cateStr = jo.get("category").getAsString();
			this.category = ToolCategory.fromString(cateStr);
			String weight = jo.get("weight").getAsString();
			this.weight = UnsagaWeightType.fromString(weight);
			String abilityString = jo.get("ability").getAsString();
			for(String id:Splitter.on(",").split(abilityString)){
				IAbility ab = AbilityAPI.getAbilityByID(id);
				Preconditions.checkArgument(!ab.isAbilityEmpty());
				if(!ab.isAbilityEmpty()){
					ability.add(ab);
				}
			}

//			int armor = jo.get("armor").getAsInt();

		}

	}



	public static void preinit(){

		String abilityPrefix = "ability.";
		loadJson(HELMET, ToolCategory.HELMET);
		loadJson(FOOT, ToolCategory.LEGGINS);
		loadJson(ACCESSORY, ToolCategory.ACCESSORY);
		loadJson(ARMOR, ToolCategory.ARMOR);
		loadJson(SHIELD, ToolCategory.SHIELD);
		loadJsonSpecialMove(SPECIAL_MOVE);

	}

	private static void loadJsonSpecialMove(ResourceLocation loadFile){
//		WeightAbilityRegistry newReg = new WeightAbilityRegistry(in -> new ResourceLocation(in.weight.getRegitryName()),in -> in.ability);
//		this.spreg.put(cate, newReg);

		JsonApplier<TechTable,ParserSpecialMove> helper = new JsonApplier<TechTable,ParserSpecialMove>(loadFile,in -> new ParserSpecialMove(),in -> TABLE_TECH);
		helper.parseAndApply();
	}
	private static void loadJson(ResourceLocation loadFile,ToolCategory cate){
//		MaterialAbilityRegistry newReg = new MaterialAbilityRegistry(in -> new ResourceLocation(in.name),in -> in.ability);
//		this.abreg.put(cate, newReg);

		JsonApplier<PassiveAbilityTable,ParserAbility> helper = new JsonApplier<PassiveAbilityTable,ParserAbility>(loadFile,in -> new ParserAbility(cate),in -> TABLE_PASSIVE);
		helper.parseAndApply();
	}


	/**
	 * 技を重量・カテゴリから得る（ArrayListで帰ってくる。）
	 * @param cate カテゴリ
	 * @param type 重量
	 * @return
	 */
	public List<IAbility> getTechs(ToolCategory cate,UnsagaWeightType type){
		return TABLE_TECH.get(cate, type);

	}

	/**
	 * アビリティを素材・カテゴリから得る（Empty Abilityのindexも含んだサイズ４のNonNullListで帰ってくる）
	 * @param cate カテゴリ
	 * @param m 素材
	 * @return
	 */
	public NonNullList<IAbility> getAbilityList(ToolCategory cate,UnsagaMaterial m){

		return (NonNullList<IAbility>) TABLE_PASSIVE.get(cate, m);
	}


	public static class TechTable extends AbilityPotentialTable<UnsagaWeightType> implements IJsonApplyTarget<ParserSpecialMove>{

		private Map<UnsagaMaterial,Set<IAbility>> materialUniqueAbilityMap = new HashMap<>();

		public TechTable(){
			Set<IAbility> tech1 = ImmutableSet.of(Techs.FINAL_STRIKING,Techs.SWARM);
			Set<IAbility> tech2 = ImmutableSet.of(Techs.FINAL_STRIKING,Techs.BLESS);
			Set<IAbility> tech3 = ImmutableSet.of(Techs.FINAL_STRIKING,Techs.THUNDER_BOLT);
			Set<IAbility> tech4 = ImmutableSet.of(Techs.FINAL_STRIKING,Techs.FEATHER_SEAL);
			Set<IAbility> tech5 = ImmutableSet.of(Techs.FEATHER_SEAL,Techs.THUNDER_BOLT);
			Set<IAbility> tech6 = ImmutableSet.of(Techs.FEATHER_SEAL,Techs.BLESS);
			this.materialUniqueAbilityMap.put(UnsagaMaterials.TUSK1, tech4);
			this.materialUniqueAbilityMap.put(UnsagaMaterials.TUSK2, tech2);
			this.materialUniqueAbilityMap.put(UnsagaMaterials.BONE1, ImmutableSet.of(Techs.FINAL_STRIKING));
			this.materialUniqueAbilityMap.put(UnsagaMaterials.BONE2, ImmutableSet.of(Techs.FINAL_STRIKING));
			this.materialUniqueAbilityMap.put(UnsagaMaterials.THIN_SCALE, tech4);
			this.materialUniqueAbilityMap.put(UnsagaMaterials.CHITIN, tech1);
			this.materialUniqueAbilityMap.put(UnsagaMaterials.ANCIENT_FISH_SCALE, tech3);
			this.materialUniqueAbilityMap.put(UnsagaMaterials.METEORIC_IRON,tech5);
			this.materialUniqueAbilityMap.put(UnsagaMaterials.METEORITE,tech5);
			this.materialUniqueAbilityMap.put(UnsagaMaterials.FAERIE_SILVER,tech6);
		}

		public boolean isTechApplicable(ToolCategory cate,Tech tech){
			return this.get(cate, UnsagaWeightType.LIGHT).contains(tech) || this.get(cate, UnsagaWeightType.HEAVY).contains(tech);

		}

		@Override
		public void applyJson(ParserSpecialMove parser) {
			Preconditions.checkNotNull(parser.category, parser.category);
//			Preconditions.checkArgument(parser.ability.isEmpty(), "ability is empty");
			this.map.put(parser.category,parser.weight, parser.ability);
		}

		public Set<IAbility> getMaterialUniqueAbility(UnsagaMaterial material){
			return this.materialUniqueAbilityMap.getOrDefault(material, Sets.newHashSet());
		}



	}
	public static class PassiveAbilityTable extends AbilityPotentialTable<UnsagaMaterial> implements IJsonApplyTarget<ParserAbility>{

		private Map<UnsagaMaterial,Integer> accessoryModifiers = new HashMap<>();
		private Map<IAbility,Predicate<UnsagaMaterial>> inherentAbilityList = new HashMap<>();

		public PassiveAbilityTable(){
			this.inherentAbilityList.put(Abilities.SUPER_HEALING, m ->m==UnsagaMaterials.DRAGON_HEART);
		}
		@Override
		public List<IAbility> get(ToolCategory cate,UnsagaMaterial tag){
			ToolCategory category = cate;
			if(cate==ToolCategory.BOOTS){
				category = ToolCategory.LEGGINS;
			}
			return super.get(category, tag);
		}

		public int getArmorModifier(UnsagaMaterial m){
			return Optional.ofNullable(this.accessoryModifiers.get(m)).orElse(0);
		}

		public Optional<IAbility> getInherentAbility(UnsagaMaterial m){
			return this.inherentAbilityList.entrySet().stream().filter(in -> in.getValue().test(m))
					.map(in -> in.getKey()).findFirst();
		}

		@Override
		public List<IAbility> getEmptyList(){
			return NonNullList.withSize(4, Abilities.EMPTY);
		}

		@Override
		public void applyJson(ParserAbility parser) {
			for(UnsagaMaterial m:parser.m){
				if(this.map.contains(parser.category, m) && parser.isOverride){
					this.map.put(parser.category,m, parser.ability);
				}else{
					this.map.put(parser.category,m, parser.ability);
				}

				if(parser.category==ToolCategory.ACCESSORY && parser.armor>0){
					this.accessoryModifiers.put(m, parser.armor);
				}
			}

		}
	}
}
