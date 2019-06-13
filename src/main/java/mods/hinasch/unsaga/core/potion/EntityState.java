package mods.hinasch.unsaga.core.potion;

import java.util.Set;

import com.google.common.collect.Sets;

import mods.hinasch.lib.entity.IEntityState;
import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.particle.ParticleHelper;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.damage.AdditionalDamage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class EntityState extends PotionUnsaga implements IEntityState{

	public static final Set<Potion> SPAWN_PARTICLE = Sets.newHashSet(UnsagaPotions.CANCEL_FALL,UnsagaPotions.THROWN);

	protected EntityState(String name) {
		super(name, false, 0, 0,0);
		// TODO 自動生成されたコンストラクター・スタブ
	}


	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int p_76394_2_)
	{
		super.performEffect(entityLivingBaseIn, p_76394_2_);
//		UnsagaMod.logger.trace(this.getName(), "update"+LivingCapability.getCapability(entityLivingBaseIn).getState(this).getDuration());
		World world = entityLivingBaseIn.world;
		if(StateCapability.isStateActive(entityLivingBaseIn, UnsagaPotions.CANCEL_FALL)){
			entityLivingBaseIn.fallDistance = 0;
			if(entityLivingBaseIn.onGround){
				StateCapability.getCapability(entityLivingBaseIn).removeState(UnsagaPotions.CANCEL_FALL);
			}

		}

		if(SPAWN_PARTICLE.stream().anyMatch(in -> in==this)){
			if(WorldHelper.isClient(world)){
				ParticleHelper.MovingType.FLOATING.spawnParticle(world, XYZPos.createFrom(entityLivingBaseIn), EnumParticleTypes.CRIT, world.rand, 10, 0.05D);
			}
		}
//		if(entityLivingBaseIn.isPotionActive(UnsagaPotions.CANCEL_HURT)){
//			if(!entityLivingBaseIn.isPotionActive(UnsagaPotions.MOVING_STATE)){
//				entityLivingBaseIn.removeActivePotionEffect(UnsagaPotions.CANCEL_HURT);
//			}
//		}
	}
	@Override
	public boolean hasStatusIcon() {
		return false;
	}


	@Override
	public void onHurt(LivingHurtEvent e,int amplifier){
//		UnsagaMod.logger.trace(this.getClass().getName(), "called3");
		if(!StateCapability.ADAPTER.hasCapability(e.getEntityLiving())){
			return;
		}
//		UnsagaMod.logger.trace(this.getClass().getName(), "called");
		if(this.equals(UnsagaPotions.CANCEL_HURT)){
//			UnsagaMod.logger.trace(this.getClass().getName(), "called2");
			e.setAmount(0);
			e.setCanceled(true);
		}
	}


	public static void onFall(LivingFallEvent e){
		if(!StateCapability.ADAPTER.hasCapability(e.getEntityLiving())){
			return;
		}
		if(StateCapability.isStateActive(e.getEntityLiving(),UnsagaPotions.CANCEL_FALL)){
			e.setDistance(0);
			e.setCanceled(true);
		}
	}

	public static void setCancelHurt(EntityLivingBase living,int time){
		StateCapability.getCapability(living).addState(new PotionEffect(UnsagaPotions.CANCEL_HURT,time,0,false,false));
	}

	public static void setCancelFall(EntityLivingBase living,int time){
		StateCapability.getCapability(living).addState(new PotionEffect(UnsagaPotions.CANCEL_FALL,time,0,false,false));
	}


	@Override
	public void onTick(World world, EntityLivingBase living, int amp) {
		// TODO 自動生成されたメソッド・スタブ

	}


	@Override
	public void onDamage(LivingDamageEvent e, AdditionalDamage data, int amplifier) {
		// TODO 自動生成されたメソッド・スタブ

	}


	@Override
	public void onClientPacketEvent(EntityLivingBase living, NBTTagCompound tag) {
		// TODO 自動生成されたメソッド・スタブ

	}


	@Override
	public boolean isValidPotionEffect(PotionEffect effect) {
		return true;
	}
}
