package mods.hinasch.unsaga.damage;

import java.util.EnumSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonObject;

import io.netty.util.internal.StringUtil;
import mods.hinasch.lib.misc.JsonApplier;
import mods.hinasch.lib.misc.JsonApplier.IJsonApplyTarget;
import mods.hinasch.lib.misc.JsonApplier.IJsonParser;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.Sub;
import mods.hinasch.unsaga.init.UnsagaLibrary;
import mods.hinasch.unsaga.lp.LPAttribute;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;

/**
 *
 * 攻撃を受けた時の属性値、エンティティとアイテムどちらでも
 *
 */
public class LibraryItemDamageAttribute{


	public static final ResourceLocation RES = new ResourceLocation(UnsagaMod.MODID,"data/item_attribute.json");

	ImmutableMap<String,Attributes> attributeLookUps;


	public static LibraryItemDamageAttribute instance(){
		return UnsagaLibrary.ITEM_ATTRIBUTE;
	}

	public static class Attributes{
		String name = StringUtil.EMPTY_STRING;
		EnumSet<DamageTypeUnsaga.General> generals = EnumSet.noneOf(General.class);
		EnumSet<DamageTypeUnsaga.Sub> subs = EnumSet.noneOf(Sub.class);
		LPAttribute lpAttribute = LPAttribute.ZERO;
		public EnumSet<Sub> getSubTypes(){
			return this.subs;
		}

		public EnumSet<General> getTypes(){
			return this.generals;
		}

		public String getItemClassName(){
			return this.name;
		}

		public LPAttribute getLPDamage(){
			return this.lpAttribute;
		}

		public AdditionalDamage toAdditionalDamage(DamageSource ds){
			AdditionalDamage ad = new AdditionalDamage(ds,this.getLPDamage().amount(),this.getLPDamage().chances(),this.getTypes());
			if(!this.getSubTypes().isEmpty()){
				ad.setSubTypes(this.getSubTypes());
			}
			return ad;
		}
	}

	public void register(){

		this.loadJson();
	}
	private void loadJson(){
		MapBuilder builder = new MapBuilder();
		JsonApplier.create(RES, AttributeParser::new, in -> builder).parseAndApply();
		this.attributeLookUps = ImmutableMap.copyOf(builder.builder);
	}
	public static class AttributeParser extends Attributes implements IJsonParser{


		public AttributeParser(JsonObject jo){
			this.parse(jo);
		}

		@Override
		public void parse(JsonObject jo) {
			// TODO 自動生成されたメソッド・スタブ
			this.name = jo.get("name").getAsString();
			Set<General> set = new HashSet<>();
			Splitter.on(",").split(jo.get("type").getAsString()).forEach(in ->{
				set.add(General.fromString(in));
			});
			Set<Sub> set2 = new HashSet<>();
			Splitter.on(",").split(jo.get("sub").getAsString()).forEach(in ->{
				set2.add(Sub.fromString(in));
			});
			this.generals = EnumSet.copyOf(set);
			this.subs = EnumSet.copyOf(set2);
			this.lpAttribute = LPAttribute.of(jo.get("lp_damage").getAsString());
		}
		public EnumSet<Sub> getSubTypes(){
			return this.subs;
		}

		public EnumSet<General> getTypes(){
			return this.generals;
		}

		public String getItemClassName(){
			return this.name;
		}
	}

	/**
	 * アイテムのLP攻撃力データを検索。ItemStackはクラスのシンプルネームに変換される
	 * @param stack
	 * @return
	 */
	public Optional<Attributes> find(ItemStack stack){
		return instance().findData(stack.getItem().getClass().getSimpleName());

	}

	public Optional<Attributes> find(Entity entity){
		return instance().findData(entity.getClass().getSimpleName());

	}
	public Optional<Attributes> findData(String className){
		return this.attributeLookUps.containsKey(className) ? Optional.of(this.attributeLookUps.get(className)) : Optional.empty();
	}

	public static class MapBuilder implements IJsonApplyTarget<AttributeParser>{

		Map<String,Attributes> builder = Maps.newHashMap();

		@Override
		public void applyJson(AttributeParser parser) {
			// TODO 自動生成されたメソッド・スタブ
			if(!parser.name.isEmpty()){
				this.builder.put(parser.name, parser);
			}
		}
	}


}
