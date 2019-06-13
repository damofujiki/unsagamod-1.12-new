package mods.hinasch.unsaga.core.entity.projectile.custom_arrow;

import javax.annotation.Nullable;

import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.iface.IIntSerializable;
import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.network.PacketSound;
import mods.hinasch.lib.network.PacketUtil;
import mods.hinasch.lib.particle.PacketParticle;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.VecUtil;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.specialmove.Techs;
import mods.hinasch.unsaga.core.net.packet.PacketClientThunder;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import mods.hinasch.unsaga.damage.AdditionalDamage;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.Sub;
import mods.hinasch.unsaga.lp.LPAttribute;
import mods.hinasch.unsagamagic.spell.UnsagaSpells;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public enum SpecialArrowType implements IIntSerializable{
	NONE(0)
	/** 未使用*/
	,MAGIC_ARROW(1){
		public LPAttribute getLPStrength(){
			return UnsagaSpells.FIRE_ARROW.strength().lp();
		}
		public @Nullable EnumParticleTypes getParticle(){
			return EnumParticleTypes.FLAME;
		}
	},EXORCIST(2){
		public void onImpactLiving(EntityLivingBase shooter,EntityLivingBase living){
			if(living.getCreatureAttribute()==EnumCreatureAttribute.UNDEAD){
				living.setFire(4);
				HSLib.packetDispatcher().sendToAllAround(PacketSound.atEntity(SoundEvents.ENTITY_ZOMBIE_VILLAGER_CURE, living), PacketUtil.getTargetPointNear(living));
				HSLib.packetDispatcher().sendToAllAround(PacketParticle.create(XYZPos.createFrom(living), EnumParticleTypes.VILLAGER_HAPPY	, 10), PacketUtil.getTargetPointNear(living));
			}

		}

		public void onLivingHurt(LivingHurtEvent e){
			if(e.getEntityLiving().getCreatureAttribute()==EnumCreatureAttribute.UNDEAD){
				e.setAmount(e.getAmount() * 1.5F);
			}
			return;
		}

		public AdditionalDamage getDamageSource(EntityLivingBase attacker,Entity arrow){
			return super.getDamageSource(attacker, arrow).setSubTypes(Sub.SHOCK);
		}

		public LPAttribute getLPStrength(){
			return Techs.EXORCIST.getStrength().lp();
		}

		public @Nullable EnumParticleTypes getParticle(){
			return EnumParticleTypes.CRIT;
		}
	},SHADOW_STITCH(3){
		public void onImpact(ProjectileImpactEvent.Arrow e){
			if(e.getRayTraceResult().typeOfHit==RayTraceResult.Type.BLOCK){
				World world = e.getArrow().getEntityWorld();
				world.getEntitiesWithinAABB(EntityLivingBase.class,e.getArrow().getEntityBoundingBox().grow(3D, 2D, 3D),in ->in != e.getArrow().shootingEntity)
				.forEach(in ->{
					in.addPotionEffect(new PotionEffect(UnsagaPotions.STUN,ItemUtil.getPotionTime(8),0));
				});
			}
		}

		public LPAttribute getLPStrength(){
			return LPAttribute.ZERO;
		}

		public @Nullable EnumParticleTypes getParticle(){
			return EnumParticleTypes.PORTAL;
		}
	},ZAPPER(4){
		public void onImpact(ProjectileImpactEvent.Arrow e){
			HSLib.packetDispatcher().sendToAllAround(PacketSound.atEntity(SoundEvents.ENTITY_LIGHTNING_THUNDER, e.getArrow()), PacketUtil.getTargetPointNear(e.getArrow()));
			UnsagaMod.PACKET_DISPATCHER.sendToAllAround(PacketClientThunder.create(XYZPos.createFrom(e.getArrow())), PacketUtil.getTargetPointNear(e.getArrow()));

		}

		public LPAttribute getLPStrength(){
			return Techs.ZAPPER.getStrength().lp();
		}

		public @Nullable EnumParticleTypes getParticle(){
			return EnumParticleTypes.CRIT;
		}
	}
	,PHOENIX(5){
		public void onImpactLiving(EntityLivingBase shooter,EntityLivingBase hitEntity){
			HSLib.packetDispatcher().sendToAllAround(PacketSound.atEntity(SoundEvents.ENTITY_PLAYER_LEVELUP, shooter), PacketUtil.getTargetPointNear(shooter));
			HSLib.packetDispatcher().sendToAllAround(PacketParticle.create(XYZPos.createFrom(shooter), EnumParticleTypes.VILLAGER_HAPPY	, 10), PacketUtil.getTargetPointNear(shooter));
			HSLib.packetDispatcher().sendToAllAround(PacketParticle.create(XYZPos.createFrom(hitEntity), EnumParticleTypes.FLAME	, 10), PacketUtil.getTargetPointNear(hitEntity));
			shooter.heal(1.0F);
		}

		public LPAttribute getLPStrength(){
			return Techs.PHOENIX.getStrength().lp();

		}
		public @Nullable EnumParticleTypes getParticle(){
			return EnumParticleTypes.FLAME;
		}
	},ARROW_RAIN(6){

	},POWER(7){
		public void onImpactLiving(EntityLivingBase shooter,EntityLivingBase hitEntity){
			HSLib.packetDispatcher().sendToAllAround(PacketSound.atEntity(SoundEvents.ENTITY_IRONGOLEM_HURT, shooter), PacketUtil.getTargetPointNear(shooter));
			VecUtil.knockback(shooter, hitEntity, 3.0D, 0.1D);
		}

		public LPAttribute getLPStrength(){
			return Techs.POWER_SHOT.getStrength().lp();
		}
		public @Nullable EnumParticleTypes getParticle(){
			return EnumParticleTypes.CRIT;
		}
	};

	final int meta;
	private SpecialArrowType(int meta){
		this.meta = meta;
	}
	@Override
	public int getMeta() {
		// TODO 自動生成されたメソッド・スタブ
		return this.meta;
	}

	public void onImpactLiving(EntityLivingBase shooter,EntityLivingBase hitEntity){

	}
	public void onImpact(ProjectileImpactEvent.Arrow e){
		Entity shooter = e.getArrow().shootingEntity;
		if(e.getRayTraceResult().typeOfHit==RayTraceResult.Type.ENTITY){

			Entity hitEntity = e.getRayTraceResult().entityHit;
			//フェニックスアローの自己回復
			if(shooter instanceof EntityLivingBase && hitEntity instanceof EntityLivingBase){
				this.onImpactLiving((EntityLivingBase) shooter, (EntityLivingBase)hitEntity);
			}

		}


	}
	public void onLivingHurt(LivingHurtEvent e){
		return;
	}
	//	public float getDamage(EntityLivingBase victim,float amount){
	//		if(this==EXORCIST && victim.getCreatureAttribute()==EnumCreatureAttribute.UNDEAD){
	//			return amount * 2.0F;
	//		}
	//		return amount;
	//	}
	public AdditionalDamage getDamageSource(EntityLivingBase attacker,Entity arrow){
		AdditionalDamage data = new AdditionalDamage(DamageSource.causeArrowDamage((EntityArrow) arrow, attacker),this.getLPStrength(),General.SPEAR);
		return data;
	}

	public LPAttribute getLPStrength(){
		return new LPAttribute(0.3F,1);
	}
	public @Nullable EnumParticleTypes getParticle(){
		return null;
	}
	public static SpecialArrowType fromMeta(int meta){
		return HSLibs.fromMeta(SpecialArrowType.values(), meta);
	}
}