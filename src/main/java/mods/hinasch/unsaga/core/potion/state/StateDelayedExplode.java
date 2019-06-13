package mods.hinasch.unsaga.core.potion.state;

import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.particle.ParticleHelper;
import mods.hinasch.lib.util.VecUtil;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.potion.EntityState;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class StateDelayedExplode extends EntityState{

	public StateDelayedExplode(String name) {
		super(name);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int p_76394_2_)
	{
		super.performEffect(entityLivingBaseIn, p_76394_2_);
		World world = entityLivingBaseIn.getEntityWorld();
		UnsagaMod.logger.trace("remain1", "called");
		if(StateCapability.isStateActive(entityLivingBaseIn,this)){

			PotionEffect ef = StateCapability.getState(entityLivingBaseIn,this);
			UnsagaMod.logger.trace("remain2", ef.getDuration());
			if(VecUtil.getSurfaceFrom(entityLivingBaseIn, 5).isPresent() && ef.getDuration()<=3){
				BlockPos pos = VecUtil.getSurfaceFrom(entityLivingBaseIn, 5).get();
				IBlockState state = world.getBlockState(pos);
				ParticleHelper.MovingType.WAVE.spawnParticle(world, new XYZPos(pos.add(0, 1, 0)), EnumParticleTypes.BLOCK_DUST, world.rand, 10, 0.2D, Block.getIdFromBlock(state.getBlock()));
			}
			if(ef.getDuration()<=1){
				StateCapability.ADAPTER.getCapability(entityLivingBaseIn).removeState(this);



				if(entityLivingBaseIn.onGround){
					XYZPos pos = XYZPos.createFrom(entityLivingBaseIn);
					WorldHelper.createExplosionSafe(world,null,pos, 3, true);
				}else{
					BlockPos pos = entityLivingBaseIn.getEntityWorld().getHeight(entityLivingBaseIn.getPosition());
					if(pos.getY()>0){
						WorldHelper.createExplosionSafe(world,null,new XYZPos(pos), 3, true);
					}

				}
			}
		}
	}
}
