package mods.hinasch.unsaga.damage;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Range;
import com.mojang.realmsclient.util.Pair;

import mods.hinasch.unsaga.lp.LPAttribute;

/**
 *
 * HPダメージとLPダメージをまとめたもの。
 *
 */
public class DamageComponent extends Pair<Float,LPAttribute>{

	protected DamageComponent(Float first, LPAttribute second) {
		super(first, second);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public float hp(){
		return this.first();
	}

	public LPAttribute lp(){
		return this.second();
	}
	public static DamageComponent of(float hp,LPAttribute lp){
		return new DamageComponent(hp,lp);

	}
	public static DamageComponent of(float hp,float lp){
		return new DamageComponent(hp,new LPAttribute(lp,1));

	}

	public static DamageComponent of(String serializedData){
		List<Float> floats = Lists.newArrayList(Splitter.on(",").splitToList(serializedData)).stream().map(in -> Float.valueOf(in)).collect(Collectors.toList());
		if(floats.size()<=0){
			return DamageComponent.ZERO;
		}
		if(floats.size()==1){
			return of(floats.get(0),0.0F);
		}
		if(floats.size()==2){
			return of(floats.get(0),new LPAttribute(floats.get(1)));
		}
		return of(floats.get(0),new LPAttribute(floats.get(1),floats.get(2).intValue()));
	}
	public static final DamageComponent ZERO = DamageComponent.of(0, new LPAttribute(0.0F,0));

	public static class RangedDamageComponent extends DamageComponent{

		final Range<Float> range;
		final float growth;

		public RangedDamageComponent(DamageComponent comp,Range<Float>  range) {
			this(comp,range,1.0F);
		}

		public RangedDamageComponent(DamageComponent comp,Range<Float>  range,float growth) {
			super(comp.first(),comp.second());
			this.range = range;
			this.growth = growth;
		}

		public Range<Float> getRange(){
			return this.range;
		}

		public float getGrowth(){
			return this.growth;
		}
	}
}
