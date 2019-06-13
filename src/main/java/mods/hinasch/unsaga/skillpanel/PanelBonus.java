package mods.hinasch.unsaga.skillpanel;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;

import mods.hinasch.unsaga.status.UnsagaStatus;
import mods.hinasch.unsaga.util.UnsagaTextFormatting;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttribute;

public class PanelBonus {

//	public static final ImmutableMap<IAttribute,Integer> BASE_MAP;
	public static final ImmutableSet<IAttribute> WATCHING_ATTRIBUTES;
	public static final PanelBonus EMPTY = new PanelBonus(new HashMap<>());
	final ImmutableMap<IAttribute,Integer> attributeBonusMap;
	final float multiply;

	static{
		ImmutableSet.Builder<IAttribute> builder = ImmutableSet.builder();
		builder.add(SharedMonsterAttributes.ATTACK_DAMAGE);
		builder.add(SharedMonsterAttributes.MAX_HEALTH);
		builder.add(UnsagaStatus.DEXTALITY);
		builder.add(UnsagaStatus.MENTAL);
		builder.add(UnsagaStatus.INTELLIGENCE);
		for(IAttribute at:UnsagaStatus.ENTITY_ELEMENTS.values()){
			builder.add(at);
		}
		WATCHING_ATTRIBUTES = builder.build();
	}
	public PanelBonus(Map<IAttribute,Integer> map){
		this(map, 1.0F);
	}

	public PanelBonus(Map<IAttribute,Integer> map,float multiply){
		this.attributeBonusMap = ImmutableMap.copyOf(map);
		this.multiply = multiply;
	}
	public Map<IAttribute,Integer> getBaseBonusMap(){
		return attributeBonusMap;
	}

	public PanelBonus multiplyBy(int value){
		return new PanelBonus(this.attributeBonusMap,(float)value);
	}
	public PanelBonus applyMultiply(){
//		Map<IAttribute,Integer> map = Maps.newHashMap();
//		for(IAttribute at:this.getBaseBonusMap().keySet()){
//			map.put(at, (int) (this.getBaseBonusMap().get(at)*this.getMultiply()));
//		}
		Map<IAttribute,Integer> map = this.getBaseBonusMap().entrySet().stream()
		.collect(Collectors
				.toMap(Entry<IAttribute,Integer>::getKey, in -> (int)(in.getValue()*this.getMultiply())));
		return new PanelBonus(map,1.0F);
	}

	public PanelBonus combine(PanelBonus other){
		Map<IAttribute,Integer> map = Maps.newHashMap(this.getBaseBonusMap());

		other.getBaseBonusMap().forEach((at,value)->{
			map.merge(at, value, (a,b)->a+b);
		});
//		map = this.getBaseBonusMap().entrySet().stream()
//		.collect(Collectors
//				.toMap(Entry<IAttribute,Integer>::getKey, in -> other.getBaseBonusMap().get(in.getKey())
//						,(a,b)->a+b));

//		for(IAttribute at:other.getBaseBonusMap().keySet()){
//			if(map.containsKey(at)){
//				map.put(at, map.get(at)+other.getBaseBonusMap().get(at));
//			}else{
//				map.put(at, other.getBaseBonusMap().get(at));
//			}
//		}
		return new PanelBonus(map,1.0F);
	}

	/**
	 * ベースのボーナスマップをプレイヤーにすぐ適用できるAttributeMapに変換して返す。
	 * @return
	 */
	public Map<IAttribute,Double> buildActualAttributeMap(){
		Map<IAttribute,Double> map = Maps.newHashMap();
		for(IAttribute at:WATCHING_ATTRIBUTES){
			int value = this.getBaseBonusMap().containsKey(at) ? this.getBaseBonusMap().get(at) : 0;
			if(at==SharedMonsterAttributes.ATTACK_DAMAGE){
				map.put(at, 0.1D*value*this.getMultiply());
			}
			if(at==SharedMonsterAttributes.MAX_HEALTH){
				map.put(at, 0.25D*value*this.getMultiply());
			}
			if(at==UnsagaStatus.DEXTALITY || at==UnsagaStatus.MENTAL || at==UnsagaStatus.INTELLIGENCE){
				map.put(at, 0.05D*value*this.getMultiply());
			}
			if(UnsagaStatus.ENTITY_ELEMENTS.values().contains(at)){
				map.put(at,0.25D*value*this.getMultiply());
			}
		}
		return map;
	}

	public float getMultiply(){
		return this.multiply;
	}
//	public double getAttributeValue(IAttribute attribute){
//		return this.getActualAttributeMap().get(attribute)!=null ? this.getActualAttributeMap().get(attribute) : 0;
//	}

//	private String getAttributeText(IAttribute attribute){
//		return UnsagaTextFormatting.getColored(this.getAttributeValue(attribute));
//	}
	public void addTips(List<String> tips){
		Map<IAttribute,Double> map = this.buildActualAttributeMap();
		map.keySet().stream()
		.filter(in -> map.get(in)>0)
		.sorted(ATTRIBUTE_SORTER)
		.forEach(in ->{
			String name = this.getStatusName(in);
			double value = map.get(in);
			String str = String.format("%s : "+UnsagaTextFormatting.POSITIVE.toString()+"%.2f", name,value);
			tips.add(str);
		});
//		Map<IAttribute,Double> map = this.getActualAttributeMap();
//		tips.add("Attack:"+getAttributeText(SharedMonsterAttributes.ATTACK_DAMAGE));
//		tips.add("Max Health:"+getAttributeText(SharedMonsterAttributes.MAX_HEALTH));
//		tips.add("LP Attack/Critical:"+getAttributeText(UnsagaStatus.DEXTALITY));
//		tips.add("Magic Attack:"+getAttributeText(UnsagaStatus.INTELLIGENCE));
//		tips.add("Magic Defence:"+getAttributeText(UnsagaStatus.MENTAL));
//		ElementTable.Mutable mutable = new ElementTable.Mutable();
//		for(FiveElements.Type type:FiveElements.VALUES){
//			mutable.add(type, (float) this.getAttributeValue(UnsagaStatus.getAttribute(type)));
//		}
//		tips.add(mutable.toImmutable().getAmountAsFloatLocalized());
	}
	@Override
	public String toString(){
		return Joiner.on("/").join(this.getBaseBonusMap().entrySet().stream().filter(in -> in.getValue()>0).map(in -> in.getKey().getName()+":"+in.getValue()).collect(Collectors.toList()));
	}

	private String getStatusName(IAttribute attribute){
		if(attribute==SharedMonsterAttributes.MAX_HEALTH)return "Max Health";
		if(attribute==SharedMonsterAttributes.ATTACK_DAMAGE)return "Attack Damage";
		if(attribute==UnsagaStatus.DEXTALITY)return "Dextality";
		if(attribute==UnsagaStatus.INTELLIGENCE)return "Intelligence";
		if(attribute==UnsagaStatus.MENTAL)return "Mental";
		return Optional.ofNullable(UnsagaStatus.getTypeFromAttribute(attribute))
				.map(in -> in.getLocalized()).orElse("empty");
	}
	public static Function<IAttribute,Integer> ATTRIBUTE_PRIORITY = attribute ->{
		if(attribute==SharedMonsterAttributes.MAX_HEALTH)return 5;
		if(attribute==SharedMonsterAttributes.ATTACK_DAMAGE)return 4;
		if(attribute==UnsagaStatus.DEXTALITY)return 3;
		if(attribute==UnsagaStatus.INTELLIGENCE)return 2;
		if(attribute==UnsagaStatus.MENTAL)return 1;
		if(UnsagaStatus.ENTITY_ELEMENTS.values().contains(attribute))return 0;
		return -1;
	};
	public static Comparator<IAttribute> ATTRIBUTE_SORTER = (b,a)-> ATTRIBUTE_PRIORITY.apply(a).compareTo(ATTRIBUTE_PRIORITY.apply(b));
}
