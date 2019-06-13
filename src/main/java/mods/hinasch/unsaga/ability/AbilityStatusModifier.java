package mods.hinasch.unsaga.ability;

import java.util.UUID;

import com.google.common.collect.ImmutableMap;

import io.netty.util.internal.StringUtil;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.Sub;
import mods.hinasch.unsaga.element.FiveElements;
import mods.hinasch.unsaga.status.UnsagaStatus;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.util.Tuple;

public class AbilityStatusModifier extends Ability{

//	@Nullable Tuple<IAttribute,Double> modifiers = null;

//	private static final Map<AbilityStatusModifier,Tuple<IAttribute,Double>> MODIFIER_ABILITY_MAP = new HashMap<>();
	private final String descKey;
	public static final ImmutableMap<IAttribute,AttributeModifier> LISTENABLE;
	public static final UUID HEAL_UUID = UUID.fromString("579cc71e-ab95-4c54-af23-300047895df8");
	public final Tuple<IAttribute,Double> modifier;

	public AbilityStatusModifier(String name,IAttribute at,double d) {
		super(name);
		modifier = new Tuple(at,d);
		this.descKey = StringUtil.EMPTY_STRING;
	}

	public AbilityStatusModifier(String name,String desc,IAttribute at,double d) {
		super(name);
		modifier = new Tuple(at,d);
		this.descKey = desc;
	}

//	public Ability registerAttributeModifier(IAttribute attribute,double amount){
//		AttributeModifier md = LISTENABLE.get(attribute);
////		this.modifiers = new Tuple(attribute, amount);
//		MODIFIER_ABILITY_MAP.put(this, new Tuple(attribute, amount));
//		return this;
//	}

//	@Override
//	protected String getDescriptionKey(){
//		return Abilities.getDescription(this).isEmpty() ? "empty" : Abilities.getDescription(this);
//	}

	public Tuple<IAttribute,Double> getAttributeModifier(){
		return this.modifier;
	}

//	public static void registerAttribute(AbilityStatusModifier ab,IAttribute attribute,double amount){
//		AttributeModifier md = LISTENABLE.get(attribute);
//		MODIFIER_ABILITY_MAP.put(ab, new Tuple(attribute, amount));
//	}


//	public static final AttributeModifier HEAL = new AttributeModifier(HEAL_UUID,"ability.naturlaHeal",0,0);
//	public static final AttributeModifier ELM_FIRE = new AttributeModifier(UUID.fromString("d27c283b-65a7-4cde-ba95-6f1956f4566a"),"ability.supportFire",0,0);
//	public static final AttributeModifier ELM_WATER = new AttributeModifier(UUID.fromString("7bb8d1ee-3770-4439-a499-dca4194d7aee"),"ability.supportWater",0,0);
//	public static final AttributeModifier ELM_EARTH = new AttributeModifier(UUID.fromString("92f2d519-2fd3-4ec3-a79d-98ad983a6aad"),"ability.supportEarth",0,0);
//	public static final AttributeModifier ELM_METAL = new AttributeModifier(UUID.fromString("86818af7-6fe6-4743-930c-c31c8edfc7f0"),"ability.supportMetal",0,0);
//	public static final AttributeModifier ELM_WOOD = new AttributeModifier(UUID.fromString("a24275a1-031d-48dd-b97a-e7a2b8d75b72"),"ability.supportWood",0,0);
//	public static final AttributeModifier ELM_FORBIDDEN = new AttributeModifier(UUID.fromString("46dba524-b2a4-4763-a4a5-f52dce6575d3"),"ability.supportForbidden",0,0);
//	public static final AttributeModifier ATK_SWORD = new AttributeModifier(UUID.fromString("e5d714b9-0b68-421c-9734-1dd58c31ba46"),"ability.armorSlash",0,0);
//	public static final AttributeModifier ATK_PUNCH = new AttributeModifier(UUID.fromString("fe95abf1-1c68-4bd1-9ff7-7469ad9f4402"),"ability.armorPunch",0,0);
//	public static final AttributeModifier ATK_SPEAR = new AttributeModifier(UUID.fromString("945ec805-fe23-4353-9882-0e7901bd0348"),"ability.armorSpear",0,0);
//	public static final AttributeModifier ATK_MAGIC = new AttributeModifier(UUID.fromString("878cdefc-5a6b-4f58-bb3a-ae1069044675"),"ability.armorMagic",0,0);
//	public static final AttributeModifier ATK_FIRE = new AttributeModifier(UUID.fromString("6cad0b49-b6f0-4847-8c21-716a9d5c390f"),"ability.armorFire",0,0);
//	public static final AttributeModifier ATK_FREEZE = new AttributeModifier(UUID.fromString("28cdddf3-e062-48a8-9ba5-f30e2c90a86d"),"ability.armorFreeze",0,0);
//	public static final AttributeModifier ATK_ELECTRIC = new AttributeModifier(UUID.fromString("470b75f4-aded-4ee0-86bc-91f2feba5b7a"),"ability.armorElectric",0,0);
//	public static final AttributeModifier ATK_SHOCK = new AttributeModifier(UUID.fromString("2891b53b-7ed9-4424-a80d-0726c505ad74"),"ability.armorShock",0,0);
//	public static final AttributeModifier RESIST_LP = new AttributeModifier(UUID.fromString("95f178e8-97d3-48a5-9390-7187831ba7f1"),"ability.resitanceLP",0,0);





	static{
		ImmutableMap.Builder<IAttribute,AttributeModifier> map = ImmutableMap.builder();
		map.put(UnsagaStatus.HEAL_THRESHOLD, new AttributeModifier(HEAL_UUID,"ability.naturlaHeal",0,0));
		map.put(UnsagaStatus.ENTITY_ELEMENTS.get(FiveElements.Type.FIRE), new AttributeModifier(UUID.fromString("d27c283b-65a7-4cde-ba95-6f1956f4566a"),"ability.supportFire",0,0));
		map.put(UnsagaStatus.ENTITY_ELEMENTS.get(FiveElements.Type.WATER), new AttributeModifier(UUID.fromString("7bb8d1ee-3770-4439-a499-dca4194d7aee"),"ability.supportWater",0,0));
		map.put(UnsagaStatus.ENTITY_ELEMENTS.get(FiveElements.Type.EARTH), new AttributeModifier(UUID.fromString("92f2d519-2fd3-4ec3-a79d-98ad983a6aad"),"ability.supportEarth",0,0));
		map.put(UnsagaStatus.ENTITY_ELEMENTS.get(FiveElements.Type.METAL), new AttributeModifier(UUID.fromString("86818af7-6fe6-4743-930c-c31c8edfc7f0"),"ability.supportMetal",0,0));
		map.put(UnsagaStatus.ENTITY_ELEMENTS.get(FiveElements.Type.WOOD), new AttributeModifier(UUID.fromString("a24275a1-031d-48dd-b97a-e7a2b8d75b72"),"ability.supportWood",0,0));
		map.put(UnsagaStatus.ENTITY_ELEMENTS.get(FiveElements.Type.FORBIDDEN), new AttributeModifier(UUID.fromString("46dba524-b2a4-4763-a4a5-f52dce6575d3"),"ability.supportForbidden",0,0));
		map.put(UnsagaStatus.GENERALS.get(General.SWORD), new AttributeModifier(UUID.fromString("e5d714b9-0b68-421c-9734-1dd58c31ba46"),"ability.armorSlash",0,0));
		map.put(UnsagaStatus.GENERALS.get(General.PUNCH), new AttributeModifier(UUID.fromString("fe95abf1-1c68-4bd1-9ff7-7469ad9f4402"),"ability.armorPunch",0,0));
		map.put(UnsagaStatus.GENERALS.get(General.SPEAR), new AttributeModifier(UUID.fromString("945ec805-fe23-4353-9882-0e7901bd0348"),"ability.armorSpear",0,0));
		map.put(UnsagaStatus.GENERALS.get(General.MAGIC), new AttributeModifier(UUID.fromString("878cdefc-5a6b-4f58-bb3a-ae1069044675"),"ability.armorMagic",0,0));
		map.put(UnsagaStatus.SUBS.get(Sub.FIRE), new AttributeModifier(UUID.fromString("6cad0b49-b6f0-4847-8c21-716a9d5c390f"),"ability.armorFire",0,0));
		map.put(UnsagaStatus.SUBS.get(Sub.FREEZE), new AttributeModifier(UUID.fromString("28cdddf3-e062-48a8-9ba5-f30e2c90a86d"),"ability.armorFreeze",0,0));
		map.put(UnsagaStatus.SUBS.get(Sub.ELECTRIC), new AttributeModifier(UUID.fromString("470b75f4-aded-4ee0-86bc-91f2feba5b7a"),"ability.armorElectric",0,0));
		map.put(UnsagaStatus.SUBS.get(Sub.SHOCK), new AttributeModifier(UUID.fromString("2891b53b-7ed9-4424-a80d-0726c505ad74"),"ability.armorShock",0,0));
		map.put(UnsagaStatus.RESISTANCE_LP_HURT, new AttributeModifier(UUID.fromString("95f178e8-97d3-48a5-9390-7187831ba7f1"),"ability.resitance.LP",0,0));

		LISTENABLE = map.build();
	}
}
