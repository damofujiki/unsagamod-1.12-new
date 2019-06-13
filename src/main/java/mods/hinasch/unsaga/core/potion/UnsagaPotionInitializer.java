package mods.hinasch.unsaga.core.potion;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import com.google.common.collect.ImmutableList;

import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.particle.ParticleHelper;
import mods.hinasch.lib.registry.RegistryUtil;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.potion.state.StateAsyncSpell;
import mods.hinasch.unsaga.core.potion.state.StateCasting;
import mods.hinasch.unsaga.core.potion.state.StateCombo;
import mods.hinasch.unsaga.core.potion.state.StateDelayedExplode;
import mods.hinasch.unsaga.core.potion.state.StateGettingThrown;
import mods.hinasch.unsaga.core.potion.state.StateMemoryPosition;
import mods.hinasch.unsaga.core.potion.state.StateMovingTech;
import mods.hinasch.unsaga.core.potion.state.StateRestoringAIListener;
import mods.hinasch.unsaga.core.potion.state.StateUpdateEvent;
import mods.hinasch.unsaga.core.potion.state.StateWaterMoon;
import mods.hinasch.unsaga.damage.AdditionalDamageTypes;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.Sub;
import mods.hinasch.unsaga.element.FiveElements;
import mods.hinasch.unsaga.status.UnsagaStatus;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Tuple;
import net.minecraft.world.World;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class UnsagaPotionInitializer {

	public static interface IPotionBottle{
		public PotionType getPotionType();
	}


	public static final ImmutableList<Potion> DEBUFFS_BODY= ImmutableList.of(MobEffects.POISON,MobEffects.BLINDNESS,MobEffects.NAUSEA);

	public static Queue<Tuple<EntityLivingBase,Potion>> removePotionQueue = new ArrayBlockingQueue(20);

	public static void addRemoveQueue(EntityLivingBase living,Potion p){
		removePotionQueue.offer(new Tuple(living,p));
	}
	public static List<Potion> getDebuffEffects(){
		return  ImmutableList.of(MobEffects.SLOWNESS,MobEffects.WEAKNESS,MobEffects.UNLUCK,MobEffects.MINING_FATIGUE
				,UnsagaPotions.AWKWARD_DEX,UnsagaPotions.POOR_VITALITY,UnsagaPotions.POOR_INT);
	}

	public static List<Potion> getMentalBadEffects(){
		return  ImmutableList.of(MobEffects.BLINDNESS,MobEffects.WITHER,UnsagaPotions.FEAR);
	}

	public static List<Potion> getBodyBadEffects(){
		return  ImmutableList.of(MobEffects.POISON,MobEffects.BLINDNESS,MobEffects.NAUSEA);
	}


	public static void onEntityHurt(LivingHurtEvent e){
		e.getEntityLiving().getActivePotionEffects().stream()
		.filter(in -> in.getPotion() instanceof PotionUnsaga)
		.forEach(in ->{
			PotionUnsaga pu = (PotionUnsaga) in.getPotion();
			pu.onHurt(e,in.getAmplifier());
		});
	}

	public static void onFall(LivingFallEvent e){
		if(e.getEntityLiving().isPotionActive(UnsagaPotions.GRAVITY)){
			e.setDamageMultiplier(1.5F);
		}
	}

	public static void onLivingUpdate(LivingUpdateEvent e){
		if(e.getEntityLiving() instanceof EntityLivingBase){

			EntityLivingBase living = (EntityLivingBase) e.getEntityLiving();
			World world = living.getEntityWorld();
//			if(e.getEntityLiving() instanceof EntityCreature && !e.getEntityLiving().getActivePotionMap().isEmpty()){
//				PotionDisturbAI.checkExpired(e);
//			}


			if(living.isPotionActive(UnsagaPotions.STUN)){
				if(living.getActivePotionEffect(UnsagaPotions.STUN).getAmplifier()>0){
					e.setCanceled(true);
				}
			}

			StateCapability.ADAPTER.getCapabilityOptional(living)
			.filter(in ->in.isStateActive(UnsagaPotions.PARTICLE))
			.filter(in ->WorldHelper.isClient(living.getEntityWorld()))
			.ifPresent(in ->{
				ParticleHelper.MovingType.FLOATING.spawnParticle(world, XYZPos.createFrom(living)
						, EnumParticleTypes.CRIT, world.rand, 15, 0.1D);
			});


//			if(WorldHelper.isClient(e.getEntityLiving().getEntityWorld())){
//				e.getEntityLiving().getActivePotionEffects().forEach(in ->{
//					if(in.getDuration()<=0){
//						addRemoveQueue(e.getEntityLiving(),in.getPotion());
//					}
//
//				});
//			}
//			if(removePotionQueue.isEmpty()){
//				Tuple<EntityLivingBase,Potion> tuple = removePotionQueue.poll();
//				if(tuple!=null){
//					if(tuple.getFirst()==e.getEntityLiving()){
//						e.getEntityLiving().removeActivePotionEffect(tuple.getSecond());
//					}else{
//						removePotionQueue.offer(tuple);
//					}
//
//				}
//			}


		}

	}

	public static void registerEvent(){
//		for(ShieldProperty shield:instance().shieldProperties.shields){
//			instance().shieldEvents.add(new ShieldProperty.ShieldEvent(shield));
//		}
		HSLibs.registerEvent(new PlayerVisibilityHandlerUnsaga());
//		HSLibs.registerEvent(UnsagaPotions.instance());
	}
	public static void setCooling(EntityLivingBase target,int time){
		target.addPotionEffect(new PotionEffect(UnsagaPotions.COOLDOWN,ItemUtil.getPotionTime(time),0));
	}
	public static void setStunned(EntityLivingBase target,int time,int level){
		target.addPotionEffect(new PotionEffect(UnsagaPotions.STUN,time,level));
	}
	@SubscribeEvent
	public void registerPotions(RegistryEvent.Register<Potion> ev){
		RegistryUtil.Helper<Potion> reg = RegistryUtil.helper(ev, UnsagaMod.MODID, "unsaga");
		reg.register(new PotionRepelling("darkSeal",4,2,in -> in.getCreatureAttribute()!=EnumCreatureAttribute.UNDEAD));
		reg.register(new PotionRepelling("holySeal",4,2,in -> in.getCreatureAttribute()==EnumCreatureAttribute.UNDEAD));
		reg.register(new PotionBuff("shadowServant",5,1));
		reg.register(new PotionBuff("goldFinger",1,2));
		reg.register(new PotionBuff.SpellMagnet("spellMagnet",3,2,in ->in instanceof EntityLivingBase || in instanceof EntityThrowable || in instanceof EntityArrow ));
		reg.register(new PotionBuff.SpellMagnet("spellMagnet2",3,2,in -> in instanceof EntityXPOrb || in instanceof EntityItem));
		reg.register(new PotionDebuff.Stun("sleep",5,1));
		reg.register(new PotionDisturbAI("fear",0,0,c -> new EntityAIAvoidEntity(c,EntityPlayer.class, 10.0F, 1.0D, 1.2D)));
		reg.register(new PotionDebuff.Gravity("gravity",8,0));
		reg.register((PotionUnsaga) PotionUnsaga.badPotion("downMental",1,1,UnsagaStatus.MENTAL, "0fd2e08e-2b55-4254-8eb1-2629e68bbd3a", -0.10D));
		reg.register((PotionUnsaga) PotionUnsaga.badPotion("downDex",1,1,UnsagaStatus.DEXTALITY, "9eb8e2f7-9f67-4e7e-b55f-62cb060b8599", -0.10D));
		reg.register((PotionUnsaga) PotionUnsaga.badPotion("downVit",2,1,SharedMonsterAttributes.KNOCKBACK_RESISTANCE, "b1726066-1f04-45c9-8b01-8baa39bd9005", -1.0D));
		reg.register((PotionUnsaga) PotionUnsaga.badPotion("downInt",3,1,UnsagaStatus.INTELLIGENCE, "7ce9b1ce-5b19-427a-9281-4db7c90c041b", -0.10D));
		reg.register(PotionUnsaga.badPotion("lockSlime",5,0));
		reg.register((PotionUnsaga) PotionUnsaga.badPotion("detected",8,1,SharedMonsterAttributes.ARMOR, "ec8a8e69-80a1-4c22-ba72-94c790e5c7d5", -0.1D));
		reg.register(PotionUnsaga.badPotion("coolDown",0,1));
		reg.register(new PotionMagicShield("waterShield",6,1,UnsagaStatus.getAttribute(Sub.FIRE), "16cc3856-1abc-4cf6-927d-adbb2aad4a23", 0.1D));
		reg.register(new PotionMagicShield("selfBurning",6,1,UnsagaStatus.getAttribute(Sub.FREEZE), "4219c6c6-21ed-4bbe-ab0f-ea8caf5b8701", 0.1D));
		reg.register(new PotionBlockableShield("missileGuard",6,1,new AdditionalDamageTypes(General.SPEAR),0.9F,false));
		reg.register(new PotionBlockableShield("leafShield",6,1,new AdditionalDamageTypes(General.SWORD,General.PUNCH,General.SPEAR),0.5F,false));
		reg.register(new PotionBlockableShield("aegisShield",6,1,new AdditionalDamageTypes(General.SWORD,General.PUNCH,General.SPEAR,General.MAGIC),0.35F,true));
		reg.register(new PotionBuff("detectPlant",8,1));
		reg.register(new PotionOreDetector("detectGold",8,1));
		reg.register(new PotionOreDetector("detectTreasure",8,1));
		reg.register(new PotionBuff("lifeBoost",7,2,UnsagaStatus.HEAL_THRESHOLD, "7ce9b1ce-5b19-427a-9281-4db7c90c041b", -10D));
		reg.register(new PotionBuff("upVit",2,0,SharedMonsterAttributes.KNOCKBACK_RESISTANCE, "7e1e210e-fa95-4bb0-8649-962426819a4b", 1.0D));
		reg.register(new PotionBuff("upInt",3,0,UnsagaStatus.INTELLIGENCE, "7ce9b1ce-5b19-427a-9281-4db7c90c041e", 0.10D));
		reg.register(new PotionBuff("upMental",3,0,UnsagaStatus.MENTAL, "7ce9b1ce-5b19-427a-9281-4fb7c90c041e", 0.10D));
		reg.register(new PotionBuff("veilFire",6,0,UnsagaStatus.getAttribute(FiveElements.Type.FIRE), "1e3b0dc5-ddec-4a94-ab07-ed4d47b7b412", 5D));
		reg.register(new PotionBuff("veilWater",6,0,UnsagaStatus.getAttribute(FiveElements.Type.WATER), "6e6097e8-0cbe-402e-8fb8-5095be33ae4e", 5D));
		reg.register(new PotionBuff("veilEarth",6,0,UnsagaStatus.getAttribute(FiveElements.Type.EARTH), "129b9b6a-addd-4578-a9dd-5ce8c50254af", 5D));
		reg.register(new PotionBuff("veilWood",6,0,UnsagaStatus.getAttribute(FiveElements.Type.WOOD), "f000b94d-f0fe-4ca8-9bfd-7171b367bd45", 5D));
		reg.register(new PotionBuff("veilMetal",6,0,UnsagaStatus.getAttribute(FiveElements.Type.METAL), "613cbb37-2c3f-4e57-be01-72c8bbac5ad1", 5D));
		reg.register((PotionUnsaga) PotionUnsaga.buff("silentMove",5,0));
		reg.register(PotionUnsaga.badPotion("hurt",0,1));
		reg.register(new PotionBuff.Doubled("shadowServant",0,0));
		//状態
		reg.register(PotionUnsaga.badPotion("allowMultipleAttack",5,0));
		reg.register(new StateDelayedExplode("delayedExplode"));
		reg.register(new EntityState("cancelFall"));
		reg.register(new EntityState("cancelHurt"));
		reg.register(new EntityState("snowFall"));
		reg.register(new StateMovingTech("movingState"));
		reg.register(new StateAsyncSpell("asyncSpell"));
		reg.register(new StateCasting("casting"));
		reg.register(new StateMemoryPosition("set_pos"));
		reg.register(new StateCombo("combo"));
		reg.register(new EntityState("spawn_particle"));
		reg.register(new EntityState("action_progress"));
		reg.register(new PotionBuff.Swimming("swimming", 6, 0));
		reg.register(new StateGettingThrown("gettingThrown"));
		reg.register(new StateUpdateEvent("asyncEvent"));
		reg.register(new StateRestoringAIListener("restoringAI"));
		reg.register(new StateWaterMoon("water_moon"));
	}


	@SubscribeEvent
	public void registerPotionTypes(RegistryEvent.Register<PotionType> ev){
		RegistryUtil.Helper<PotionType> reg = RegistryUtil.helper(ev, UnsagaMod.MODID, "unsaga");
		reg.register(new PotionType(new PotionEffect(UnsagaPotions.FEAR,4800)));
	}
}
