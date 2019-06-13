package mods.hinasch.unsaga.core.entity.passive;

import java.util.Optional;

import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class EntitySnowFall extends Entity{

	int animationTick = 0;

	Optional<EntityLivingBase> target = Optional.empty();

	public EntitySnowFall(World worldIn) {
		super(worldIn);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public EntitySnowFall(World worldIn,EntityLivingBase target) {
		super(worldIn);
		this.target = Optional.of(target);
	}

	@Override
	protected void entityInit() {

	}

	public int getAnimationTick(){
		return this.animationTick;
	}
	@Override
    public void onUpdate()
    {
		this.animationTick += 1;

		this.target.ifPresent(in ->this.setPosition(in.posX, in.posY, in.posZ));


		if(WorldHelper.isServer(world)){
			this.target.ifPresent(in ->{
				StateCapability.ADAPTER.getCapabilityOptional(in)
				.filter(cap -> !cap.isStateActive(UnsagaPotions.SNOWFALL)|| in.isDead)
				.ifPresent(cap ->this.setDead());
			});
			if(this.animationTick>200){
				this.setDead();
			}
		}

    }
	@Override
	protected void readEntityFromNBT(NBTTagCompound compound) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound compound) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
