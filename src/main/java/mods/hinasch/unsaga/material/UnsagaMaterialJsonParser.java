package mods.hinasch.unsaga.material;

import java.util.List;

import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;

import mods.hinasch.lib.misc.JsonApplier.IJsonParser;
import mods.hinasch.unsaga.util.ToolCategory;

public class UnsagaMaterialJsonParser {

	public static class JsonParserColor implements IJsonParser{

		public UnsagaMaterial m;
		public String id;
		public int color;

		@Override
		public void parse(JsonObject jo) {
			// TODO 自動生成されたメソッド・スタブ
			String id = jo.get("id").getAsString();
			this.id = id;
			this.m = UnsagaMaterialInitializer.instance().get(id);
			String col = jo.get("color").getAsString();
			this.color = Integer.parseInt(col,16);
		}

	}

	public static class JsonParserShieldValue implements IJsonParser{

		public UnsagaMaterial m;
		public String id;
		public int value;

		@Override
		public void parse(JsonObject jo) {
			// TODO 自動生成されたメソッド・スタブ
			String id = jo.get("id").getAsString();
			this.id = id;
			this.m = UnsagaMaterialInitializer.instance().get(id);
			String col = jo.get("value").getAsString();
			this.value = Integer.valueOf(col);
		}

	}

	public static class JsonParserSpecialName implements IJsonParser{

		public List<UnsagaMaterial> materials = Lists.newArrayList();
		public List<String> ids = Lists.newArrayList();

		public ToolCategory category;
		public String specialName;

		@Override
		public void parse(JsonObject jo) {
			// TODO 自動生成されたメソッド・スタブ
			String cate = jo.get("category").getAsString();
			this.category = ToolCategory.fromString(cate);
			Preconditions.checkArgument(this.category!=ToolCategory.NONE,cate);
			String mate = jo.get("materials").getAsString();
			Splitter.on(',').split(mate).forEach(in ->{
				Preconditions.checkNotNull(UnsagaMaterialInitializer.instance().get(in),in);
				materials.add(UnsagaMaterialInitializer.instance().get(in));
				this.ids.add(in);
			});

			this.specialName = jo.get("name").getAsString();

		}

	}

	public static class JsonParserMaterial implements IJsonParser{

		public String name;
		public int rank;
		public int weight;
		public int price;
		public int harvestLevel = -1;
		public int maxUses;
		public float efficiency;
		public float attack;
		public int enchantWeapon;
		public int damageFactor = -1;
		public int[] reduction;
		public int enchantArmor;
		public float toughness;

		@Override
		public void parse(JsonObject jo){
			name = jo.get("name").getAsString();
			rank = jo.get("rank").getAsInt();
			weight = jo.get("weight").getAsInt();
			price = jo.get("price").getAsInt();
			if(!jo.get("harvest_level").getAsString().equals("") && jo.has("harvest_level")){
				harvestLevel = jo.get("harvest_level").getAsInt();
				maxUses = jo.get("max_uses").getAsInt();
				efficiency = jo.get("efficiency").getAsFloat();
				attack = jo.get("attack").getAsFloat();
				enchantWeapon = jo.get("w_enchant").getAsInt();
			}

			if(!jo.get("max_damagefactor").getAsString().equals("") && jo.has("max_damagefactor")){
				damageFactor = jo.get("max_damagefactor").getAsInt();
				String str = jo.get("reduction").getAsString();
				String[] strs = str.split(",");
				reduction = new int[strs.length];
				for(int i=0;i<strs.length;i++){
					reduction[i] = Integer.valueOf(strs[i]);
				}
				enchantArmor = jo.get("a_enchant").getAsInt();
				toughness = jo.get("toughness").getAsFloat();
			}


		}
	}

}
