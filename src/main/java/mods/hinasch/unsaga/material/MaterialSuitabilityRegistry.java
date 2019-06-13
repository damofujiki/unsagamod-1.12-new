package mods.hinasch.unsaga.material;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonObject;

import mods.hinasch.lib.misc.JsonApplier;
import mods.hinasch.lib.misc.JsonApplier.IJsonApplyTarget;
import mods.hinasch.lib.misc.JsonApplier.IJsonParser;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.material.MaterialSuitabilityRegistry.ParserSuitable;
import mods.hinasch.unsaga.util.ToolCategory;
import net.minecraft.util.ResourceLocation;



/**
 *
 * 素材→ツールの適合性。
 *
 */
public class MaterialSuitabilityRegistry implements IJsonApplyTarget<ParserSuitable>{

	public static class ParserSuitable implements IJsonParser{

		Set<UnsagaMaterial> m = Sets.newHashSet();
		Set<ToolCategory> categories = Sets.newHashSet();
		@Override
		public void parse(JsonObject jo) {
			String mn = jo.get("material").getAsString();
			this.m.add(UnsagaMaterialInitializer.instance().get(mn));
			if(mn.equals("category_wood")){
				this.m = Sets.newHashSet(UnsagaMaterialCategory.WOODS.getChildMaterials());
			}
			com.google.common.base.Preconditions.checkNotNull(this.m, mn);
			com.google.common.base.Preconditions.checkArgument(!this.m.isEmpty(),mn);
			String cate = jo.get("value").getAsString();
			for(String c:Splitter.on(',').split(cate)){
				ToolCategory category = ToolCategory.fromString(c);
				com.google.common.base.Preconditions.checkNotNull(category,cate);
				this.categories.add(category);
			}

		}

	}

	//	//何か特別な動きがいると思ってラップしたけど必要なさそう…
	//	public static class SuitableList{
	//
	//		List<UnsagaMaterial> list = Lists.newArrayList();
	//
	//		public void add(List<UnsagaMaterial> materials){
	//			Preconditions.checkNotNull(materials);
	//			list.addAll(materials);
	//		}
	//
	//		public void add(UnsagaMaterial m){
	//			list.add(m);
	//		}
	//
	//		public void add(UnsagaMaterial... materials){
	//			for(UnsagaMaterial m:materials){
	//				list.add(m);
	//			}
	//		}
	//
	//		public void add(UnsagaMaterialCategory cate){
	//			for(UnsagaMaterial m:cate.getChildMaterials()){
	//				list.add(m);
	//			}
	//		}
	//
	//		public boolean contain(UnsagaMaterial m){
	//			return this.list.contains(m);
	//		}
	//		public void exclude(UnsagaMaterial m){
	//			list.remove(m);
	//		}
	//		public UnsagaMaterial getRandom(Random rand,boolean isMerchandise){
	//			List<UnsagaMaterial> copylist;
	//			if(isMerchandise){
	//				copylist = list.stream().filter(in -> UnsagaMaterialRegistry.instance().merchandiseMaterial.contains(in)).collect(Collectors.toList());
	//			}else{
	//				copylist = Lists.newArrayList(list);
	//			}
	//			Collections.shuffle(copylist);
	//			return copylist.get(0);
	//		}
	//
	//		public List<UnsagaMaterial> values(){
	//			return this.list;
	//		}
	//	}
	protected static MaterialSuitabilityRegistry INSTANCE;

	private static ResourceLocation RESOURCE_SUITABLE = new ResourceLocation(UnsagaMod.MODID,"data/material_suitable.json");

	/** getSuitablesから店売り可能な素材を抜き出したもの。*/
	public static Set<UnsagaMaterial> getSuitableMerchadises(ToolCategory cate){

		return getSuitables(cate).stream().filter(in -> UnsagaMaterialInitializer.getMerchandiseMaterials().contains(in)).collect(Collectors.toSet());
	}
	/** カテゴリに適合する素材セットを返す。銃はIRON固定*/
	public static Set<UnsagaMaterial> getSuitables(ToolCategory cate){
		return instance().categorySuitablesMap.get(cate);
	}

	public static MaterialSuitabilityRegistry instance(){
//		if(INSTANCE == null){
//			INSTANCE = new MaterialSuitabilityRegistry();
//		}
//		return INSTANCE;
		return UnsagaMaterialInitializer.SUITABILITY;
	}
	//	Map<ToolCategory,SuitableList> suitableMap;
	Map<ToolCategory,Set<UnsagaMaterial>> categorySuitablesMap = Maps.newHashMap();

	protected MaterialSuitabilityRegistry(){

	}
	@Override
	public void applyJson(ParserSuitable parser) {
		// TODO 自動生成されたメソッド・スタブ
		//		this.suitableMap.

		for(UnsagaMaterial m:parser.m){
			for(ToolCategory cate:parser.categories){
				Set<UnsagaMaterial> set = Sets.newHashSet();
				if(this.categorySuitablesMap.get(cate)!=null){
					if(!this.categorySuitablesMap.get(cate).isEmpty()){
						set.addAll(this.categorySuitablesMap.get(cate));
					}
				}
				set.add(m);
				UnsagaMod.logger.trace("registered suitables", cate,m,set);
				this.categorySuitablesMap.put(cate, ImmutableSet.copyOf(set));
			}
		}

	}
	public void registerSuitableMaterials(){
		JsonApplier.create(RESOURCE_SUITABLE, in -> new ParserSuitable(), in -> this).parseAndApply();
		this.categorySuitablesMap.put(ToolCategory.GLOVES, Sets.newHashSet(UnsagaMaterials.COTTON,UnsagaMaterials.SILK,UnsagaMaterials.VELVET,UnsagaMaterials.LIVE_SILK));

	}

}
