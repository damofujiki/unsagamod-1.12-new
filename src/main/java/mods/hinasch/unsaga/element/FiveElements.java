package mods.hinasch.unsaga.element;

import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import mods.hinasch.lib.iface.IIntSerializable;
import mods.hinasch.lib.particle.ParticleTypeWrapper;
import mods.hinasch.lib.particle.ParticleTypeWrapper.Particles;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.ability.Abilities;
import mods.hinasch.unsaga.ability.IAbility;
import mods.hinasch.unsaga.skillpanel.ISkillPanel;
import mods.hinasch.unsaga.skillpanel.SkillPanels;
import mods.hinasch.unsaga.status.UnsagaStatus;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.util.EnumParticleTypes;

/** 五行*/
public class FiveElements {

	public static enum Type implements IIntSerializable{
		/**朱雀*/
		FIRE("fire",0),
		/** 黄龍*/
		EARTH("earth",4),
		/** 蒼龍*/
		WOOD("wood",3),
		/** 白虎*/
		METAL("metal",2)
		/**玄武*/
		,WATER("water",1),
		/** 禁術*/
		FORBIDDEN("forbidden",5);

		private int meta;
		private String name;

		private Type(String name,int meta){
			this.name = name;
			this.meta = meta;
		}

		@Override
		public int getMeta(){
			return this.meta;
		}

		public String getUnlocalized(){
			return "element."+this.name;
		}

		public String getLocalized(){
			return HSLibs.translateKey(getUnlocalized());
		}

		public int getElementColor(){
			switch(this){
			case FIRE:return 0xff0000;
			case EARTH:return 0x8b4513;
			case WOOD:return 0x6b8e23;
			case METAL:return 0xffff00;
			case WATER:return 0x4169e1;
			case FORBIDDEN:return 0x800080;
			}
			return 0xffffff;
		}

		public ParticleTypeWrapper getElementParticle(){
			switch(this){
			case FIRE:return new ParticleTypeWrapper(EnumParticleTypes.FLAME);
			case EARTH:return new ParticleTypeWrapper(Particles.STONE);
			case WOOD:return new ParticleTypeWrapper(Particles.LEAVES);
			case METAL:return new ParticleTypeWrapper(EnumParticleTypes.REDSTONE);
			case WATER:return new ParticleTypeWrapper(Particles.BUBBLE);
			case FORBIDDEN:return new ParticleTypeWrapper(EnumParticleTypes.PORTAL);
			}
			return  new ParticleTypeWrapper(EnumParticleTypes.SPELL);
		}

		public String getSpellIconName(){
			return "spellIcon." + this.name;
		}

		public String getSpellBookIconName(){
			return this.name;
		}

		public IAttribute asStatus(){
			return UnsagaStatus.getAttribute(this);
		}

		public IAbility getCastableAbility(){
			switch(this){
			case FIRE:return Abilities.SPELL_FIRE;
			case EARTH:return Abilities.SPELL_EARTH;
			case WOOD:return Abilities.SPELL_WOOD;
			case METAL:return Abilities.SPELL_METAL;
			case WATER:return Abilities.SPELL_WATER;
			case FORBIDDEN:return Abilities.SPELL_FORBIDDEN;
			}
			return null;
		}

		public @Nullable ISkillPanel getCastableFamiliar(){
			switch(this){
			case FIRE:return SkillPanels.FAMILIAR_FIRE;
			case EARTH:return SkillPanels.FAMILIAR_EARTH;
			case WOOD:return SkillPanels.FAMILIAR_WOOD;
			case METAL:return SkillPanels.FAMILIAR_METAL;
			case WATER:return SkillPanels.FAMILIAR_WATER;
			case FORBIDDEN:return null;
			}
			return null;
		}
//		public SkillPanel getFamiliar(){
//			switch(this){
//			case FIRE:return SkillPanels.instance().familiarFire;
//			case EARTH:return SkillPanels.instance().familiarEarth;
//			case WOOD:return SkillPanels.instance().familiarWood;
//			case METAL:return SkillPanels.instance().familiarMetal;
//			case WATER:return SkillPanels.instance().familiarWater;
//			case FORBIDDEN:return null;
//			}
//			return null;
//		}

		public static FiveElements.Type fromMeta(int meta){
			return HSLibs.fromMeta(FiveElements.Type.values(), meta);
		}
	}

	public static final EnumSet<Type> VALUES = EnumSet.of(Type.FIRE,Type.EARTH,Type.WOOD,Type.METAL,Type.WATER,Type.FORBIDDEN);

	public static List<String> getSpellIconJsonNames(){
		return VALUES.stream().map(in -> in.getSpellIconName()).collect(Collectors.toList());
	}

	public static List<String> getSpellBookJsonNames(){
		return VALUES.stream().map(in -> in.getSpellBookIconName()).collect(Collectors.toList());
	}
}
