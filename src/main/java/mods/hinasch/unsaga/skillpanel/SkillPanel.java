package mods.hinasch.unsaga.skillpanel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import mods.hinasch.lib.misc.JsonApplier.IJsonApplyTarget;
import mods.hinasch.lib.misc.JsonApplier.IJsonParser;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.skillpanel.SkillPanelInitializer.GrowthParser;
import mods.hinasch.unsaga.skillpanel.SkillPanelInitializer.PanelDataParser;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class SkillPanel extends IForgeRegistryEntry.Impl<ISkillPanel> implements ISkillPanel{

	public static class Builder{
		public IconType iconType ;
		public SkillPanelInitializer.Rarity rarity ;
		public Map<IAttribute,Integer> map = Maps.newHashMap();

		public String name;
		public Builder(String name){
			this.name = name;
		}
		public SkillPanel build(){
			return new SkillPanel(this);
		}
		public void setIconType(IconType iconType) {
			this.iconType = iconType;
		}

		public void setRarity(SkillPanelInitializer.Rarity rarity) {
			this.rarity = rarity;
		}

		public void setGrowth(Map<IAttribute,Integer> map){
			this.map = map;
		}
	}
public static class Factory implements IJsonApplyTarget<IJsonParser>{

		Map<String,Builder> map = new HashMap<>();

		@Override
		public void applyJson(IJsonParser parser) {
			// TODO 自動生成されたメソッド・スタブ
			if(parser instanceof PanelDataParser){
				this.parse((PanelDataParser) parser);
			}
			if(parser instanceof GrowthParser){
				this.parse((GrowthParser) parser);
			}
		}

		public void parse(GrowthParser parser){
			Builder b = this.map.get(parser.name);
			b.setGrowth(parser.map);
		}

		public void parse(PanelDataParser parser){
			Builder b = this.map.get(parser.name);
			b.setIconType(parser.icon);
			b.setRarity(parser.rarity);
		}

		public void register(Builder b){
			map.put(b.name, b);
		}
	}
//	public static class Property{
//		public final IconType iconType ;
//		public final SkillPanelRegistry.Rarity rarity ;
//
//		public Property(IconType icontype,SkillPanelRegistry.Rarity rarity){
//			this.iconType = icontype == null ? IconType.KEY : icontype;
//			this.rarity = rarity == null ? SkillPanelRegistry.Rarity.UNCOMMON : rarity;
//		}
//
//		public Property(IconType icon){
//			this(icon,null);
//		}
//	}
//	@Override
//	public String getUnlocalizedName() {
//		// TODO 自動生成されたメソッド・スタブ
//		return this.unlName;
//	}
//
//	@Override
//	public void setUnlocalizedName(String unl) {
//		// TODO 自動生成されたメソッド・スタブ
//
//	}
	public static enum IconType{
		KEY(0,"key"),MELEE(1,"melee"),ROLL(2,"roll"),COMMUNICATION(3,"communication"),NEGATIVE(4,"negative"),PROTECT(5,"protect"),FAMILIAR(6,"familiar")
		,HAMMER(7,"hammer"),DUMMY(99,"dummy");

		public static IconType fromString(String str){
			for(IconType ic:IconType.values()){
				if(ic.getJsonName().equals(str)){
					return ic;
				}
			}
			return IconType.DUMMY;
		}
		public static List<String> getJsonNames(){
			return Lists.newArrayList(IconType.values()).stream()
					.map(input -> input.getJsonName()).collect(Collectors.toList());
		}
		private int meta;

		private String jsonname;

		private IconType(int meta,String jsonname){
			this.jsonname = jsonname;
			this.meta = meta;
		}
		public String getJsonName(){
			return this.jsonname;
		}

		public int getMeta(){
			return this.meta;
		}
	}

	final public IconType iconType ;
	final PanelBonus growth;
	final SkillPanelInitializer.Rarity rarity ;
	final String unlName;


	public SkillPanel(Builder b) {
		// TODO 自動生成されたコンストラクター・スタブ
		this.setRegistryName(new ResourceLocation(UnsagaMod.MODID,b.name));
		this.unlName = b.name;
		this.iconType = b.iconType;
		this.rarity = b.rarity;
		this.growth = new PanelBonus(b.map);
	}


	@Override
	public IconType iconType(){
		return this.iconType;
	}

	/** スキルパネルのボーナス*/
	@Override
	public PanelBonus panelBonus(){
		return this.growth;
	}

	@Override
	public String getUnlocalizedName() {
		// TODO 自動生成されたメソッド・スタブ
		return this.unlName;
	}

	@Override
	public SkillPanelInitializer.Rarity rarity() {
		return this.rarity;
	}

	@Override
	public void setUnlocalizedName(String unl) {
		// TODO 自動生成されたメソッド・スタブ

	}
}
