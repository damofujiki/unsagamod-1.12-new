package mods.hinasch.unsaga.status;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonObject;

import io.netty.util.internal.StringUtil;
import mods.hinasch.lib.misc.JsonApplier;
import mods.hinasch.lib.misc.JsonApplier.IJsonApplyTarget;
import mods.hinasch.lib.misc.JsonApplier.IJsonParser;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.Sub;
import mods.hinasch.unsaga.init.UnsagaLibrary;
import mods.hinasch.unsaga.status.LibraryLivingStatus.ParserStatus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.util.ResourceLocation;

public class LibraryLivingStatus implements IJsonApplyTarget<ParserStatus>{

	public static final ResourceLocation RES = new ResourceLocation(UnsagaMod.MODID,"data/mob_status.json");

	public static final Map<String,IAttribute> attributeLookup;

	public Map<String,LivingStatus> statusLookUp = new HashMap();


	public static LibraryLivingStatus instance(){
		return UnsagaLibrary.MOB_STATUS;
	}
	static{
		attributeLookup = new HashMap<>();
		attributeLookup.put("dex", UnsagaStatus.DEXTALITY);
		attributeLookup.put("int", UnsagaStatus.INTELLIGENCE);
		attributeLookup.put("men", UnsagaStatus.MENTAL);
		attributeLookup.put("spark", UnsagaStatus.SPARK_LV);
		attributeLookup.put("lp", UnsagaStatus.MAX_LP);
		attributeLookup.put("def_lp", UnsagaStatus.RESISTANCE_LP_HURT);
		attributeLookup.put("def_punch", UnsagaStatus.GENERALS.get(General.PUNCH));
		attributeLookup.put("def_sword", UnsagaStatus.GENERALS.get(General.SWORD));
		attributeLookup.put("def_pierce", UnsagaStatus.GENERALS.get(General.SPEAR));
		attributeLookup.put("def_magic", UnsagaStatus.GENERALS.get(General.MAGIC));
		attributeLookup.put("def_fire", UnsagaStatus.SUBS.get(Sub.FIRE));
		attributeLookup.put("def_freeze", UnsagaStatus.SUBS.get(Sub.FREEZE));
		attributeLookup.put("def_electric", UnsagaStatus.SUBS.get(Sub.ELECTRIC));
		attributeLookup.put("def_shock", UnsagaStatus.SUBS.get(Sub.SHOCK));
	}

	public void register(){

		this.loadJson();
	}
	private void loadJson(){
		JsonApplier.create(RES, ParserStatus::new, in -> this).parseAndApply();
	}

	public static Optional<LivingStatus> find(Entity entity){
		if(instance().findStatus(getEntityName(entity)).isPresent()){
			return Optional.of(instance().findStatus(getEntityName(entity)).get());
		}
		return Optional.empty();
	}

	private static String getEntityName(Entity e){
		return e.getClass().getSimpleName();
	}
	public Optional<LivingStatus> findStatus(String name){
		if(name==null)return Optional.empty();
		return this.statusLookUp.containsKey(name) ? Optional.of(this.statusLookUp.get(name)) : Optional.empty();
	}
	public static class LivingStatus{

		String entityname = StringUtil.EMPTY_STRING;
		ImmutableMap<IAttribute,Double> statusMap;

		public LivingStatus(){

		}
		public String getEntityName(){
			return this.entityname;
		}

		public Map<IAttribute,Double> getStatusMap(){
			return this.statusMap;
		}
	}

	public static class ParserStatus extends LivingStatus implements IJsonParser{

		public ParserStatus(JsonObject jo){
			this.parse(jo);
		}
		@Override
		public void parse(JsonObject jo) {
			ImmutableMap.Builder<IAttribute,Double> map = ImmutableMap.builder();
			this.entityname = jo.get("name").getAsString();
			for(Entry<String,IAttribute> entry:attributeLookup.entrySet()){
				if(jo.has(entry.getKey())){
					double value = jo.get(entry.getKey()).getAsDouble();
					map.put(entry.getValue(), value);
				}
			}
			this.statusMap = map.build();
		}


	}



	@Override
	public void applyJson(ParserStatus parser) {
		// TODO 自動生成されたメソッド・スタブ
		if(!parser.entityname.isEmpty()){
			UnsagaMod.logger.trace("loading entity status...", parser.entityname);
			this.statusLookUp.put(parser.entityname, parser);
		}
	}

}
