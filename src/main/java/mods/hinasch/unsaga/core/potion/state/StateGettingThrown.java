package mods.hinasch.unsaga.core.potion.state;

import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.particle.ParticleHelper;
import mods.hinasch.lib.util.VecUtil;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.potion.EntityState;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import mods.hinasch.unsaga.damage.AdditionalDamage;
import mods.hinasch.unsaga.damage.DamageHelper;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 *
 * 投げられている状態
 *
 */
public class StateGettingThrown extends EntityState{

	public StateGettingThrown(String name) {
		super(name);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public boolean isValidPotionEffect(PotionEffect effect){
		return effect instanceof Effect;
	}
	@Override
	public void performEffect(EntityLivingBase victim, int amplifier)
	{
		super.performEffect(victim, amplifier);

		if(StateCapability.getState(victim, this) instanceof Effect){
			UnsagaMod.logger.trace(this.getName(), victim);
			Effect ef = (Effect) StateCapability.getState(victim,this);
			if(ef.getThrower()!=null){
				UnsagaMod.logger.trace(this.getName(), ef);
				World world = victim.getEntityWorld();
				world.getEntitiesWithinAABB(EntityLivingBase.class, victim.getEntityBoundingBox().grow(0.3D),in -> in!=ef.getThrower() && in!=victim)
				.forEach( in -> {
					DamageSource ds = DamageSource.causeThrownDamage(ef.getThrower(), null);
					in.attackEntityFrom(DamageHelper.register(new AdditionalDamage(ds,0.2F,1,General.PUNCH)), 2.0F);
					VecUtil.knockback(victim, in, 1.0D, 0.3D);
				});

				if(VecUtil.getSurfaceFrom(victim, 20).isPresent() && world.rand.nextInt(5)==0){
					BlockPos surface = VecUtil.getSurfaceFrom(victim, 20).get();
					IBlockState surfaceBlock = world.getBlockState(surface);
					ParticleHelper.MovingType.WAVE.spawnParticle(world, new XYZPos(surface.add(0,1,0)), EnumParticleTypes.BLOCK_DUST, world.rand, 10, 0.2D, Block.getIdFromBlock(surfaceBlock.getBlock()));
				}
			}

			if(StateCapability.isStateActive(victim, UnsagaPotions.THROWN)){
				if(victim.onGround && ef.getDuration()<(ef.startDuration-5)){
					StateCapability.getCapability(victim).removeState(UnsagaPotions.THROWN);
				}

			}

		}


	}

	public static class Effect extends PotionEffect{

		final EntityLivingBase thrower;
		final int startDuration;
		final boolean isCyclone;
		public Effect(int durationIn,EntityLivingBase thrower,boolean isCyclone) {
			super(UnsagaPotions.THROWN, durationIn);
			// TODO 自動生成されたコンストラクター・スタブ
			this.thrower = thrower;
			this.startDuration = durationIn;
			this.isCyclone = isCyclone;
		}

		public EntityLivingBase getThrower(){
			return this.thrower;
		}
	}
}
