package mods.hinasch.unsaga.ability.specialmove;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netty.util.internal.StringUtil;
import mods.hinasch.lib.misc.JsonApplier.IJsonApplyTarget;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.ability.AbilityBase;
import mods.hinasch.unsaga.ability.IAbility;
import mods.hinasch.unsaga.ability.AbilityPotentialTableProvider;
import mods.hinasch.unsaga.ability.specialmove.TechInitializer.JsonParserProperty;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker.InvokeType;
import mods.hinasch.unsaga.ability.specialmove.action.TechActionBase;
import mods.hinasch.unsaga.damage.DamageComponent;
import mods.hinasch.unsaga.util.ToolCategory;

/**
 *
 * またはSpecialMove。
 *
 */
public class Tech extends AbilityBase{


//	public static class Property{
//
//		int coolingTime;
//		int cost;
//		DamageComponent str;
//
//		public Property(int cost,DamageComponent dc,int coolingtime){
//			this.coolingTime = coolingtime;
//			this.cost = cost;
//			this.str = dc;
//		}
//	}

	public static class Builder{
		String name;
		boolean isRequireTarget = false;
		int cost = 0;
		int cooltime = 0;
		DamageComponent damage = DamageComponent.ZERO;
		public Builder(String name) {
			super();
			this.name = name;
		}

		public void setCoolTime(int cooltime) {
			this.cooltime = cooltime;
		}

		public void setCost(int cost) {
			this.cost = cost;
		}
		public void setDamage(DamageComponent damage) {
			this.damage = damage;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Builder setRequireTarget(boolean par1){
			this.isRequireTarget = par1;
			return this;
		}


	}

	public static class Factory implements IJsonApplyTarget<JsonParserProperty>{

		Map<String,Builder> list = new HashMap<>();

		@Override
		public void applyJson(JsonParserProperty parser) {
			// TODO 自動生成されたメソッド・スタブ
			Builder b = this.list.get(parser.id);
			b.setCoolTime(parser.coolingTime);
			b.setCost(parser.cost);
			b.setDamage(parser.damage);

		}

		public Map<String,Builder> get(){
			return this.list;
		}

		public void register(Builder b){
			list.put(b.name, b);
		}

	}
	public static final TechActionBase EMPTY = new TechActionBase(InvokeType.BOW);
//	List<ToolCategory> restrictionList = new ArrayList<>();
	final int cooingTime;
	final int cost;
	final DamageComponent dmg;
	final boolean isRequireTarget;


	public Tech(Builder b) {
		super(b.name);
		this.cooingTime = b.cooltime;
		this.cost = b.cost;
		this.dmg = b.damage;
		this.isRequireTarget = b.isRequireTarget;
	}

	@Override
	public int compareTo(IAbility o) {
		if(o instanceof Tech){
			return this.getSortString().compareTo(((Tech) o).getSortString());
		}
		return -1;
	}

	/** 技の動作(TechActionBase)を取得*/
	public TechActionBase getAction(){
		if(!TechInitializer.getAssociatedAction(this).isEmpty()){
			return TechInitializer.getAssociatedAction(this);
		}
		return EMPTY;
	}


	public int getCoolingTime(){
		return this.cooingTime;
	}



//	public Tech setRestriction(ToolCategory... tools){
//		this.restrictionList = Lists.newArrayList(tools);
//		return this;
//	}

	public int getCost() {
		// TODO 自動生成されたメソッド・スタブ
		return this.cost;
	}

	@Override
	public String getLocalized() {
		// TODO 自動生成されたメソッド・スタブ
		return HSLibs.translateKey(getLocalized());
	}

	@Override
	public String getLocalizedInAbilityList() {
		// TODO 自動生成されたメソッド・スタブ


		return this.getLocalized() + "[Cost:"+this.getCost()+"]";
	}


	/** 覚える事ができる武器種。技書で使う*/
	public List<ToolCategory> getRestrictions(){
		List<ToolCategory> list = new ArrayList<>();
		for(ToolCategory cate:ToolCategory.WEAPONS){
			if(AbilityPotentialTableProvider.TABLE_TECH.isTechApplicable(cate, this)){
				list.add(cate);
			}
		}
		return list;
	}

	public String getSortString(){
		if(this.getRestrictions().isEmpty()){
			return "0";
		}
		return this.getRestrictions().get(0).getPrefix();
	}

	public DamageComponent getStrength(){
		return this.dmg;
	}


	public String getTranslatedDescription() {
		String key = this.getUnlocalizedName()+".desc";
		return HSLibs.canTranslate(key) ? HSLibs.translateKey(key) : StringUtil.EMPTY_STRING;
	}

	public boolean isRequireTarget(){
		return this.isRequireTarget;
	}
//
//	private Property property(){
//		return Techs.getTechProperty(this);
//	}

}
