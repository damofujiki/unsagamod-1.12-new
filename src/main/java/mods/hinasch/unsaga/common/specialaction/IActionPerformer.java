package mods.hinasch.unsaga.common.specialaction;

import java.util.Optional;

import mods.hinasch.lib.iface.IIntSerializable;
import mods.hinasch.lib.particle.ParticleHelper;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.common.specialaction.option.IActionOption;
import mods.hinasch.unsaga.damage.DamageComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IActionPerformer<T, V> {

	//そのうちOptionに置き換える
	enum TargetType implements IIntSerializable{
		UNKNOWN(0),OWNER(1),TARGET(2),POSITION(3);

		private final int meta;

		private TargetType(int meta){
			this.meta = meta;
		}
		@Override
		public int getMeta() {
			// TODO 自動生成されたメソッド・スタブ
			return meta;
		}

		public static TargetType fromMeta(int meta){
			return HSLibs.fromMeta(TargetType.values(), meta);
		}

	}
	public void broadCastMessage(String msg);
	public V getAction();
	public T getActionProperty();
	public Optional<ItemStack> getArtifact();
	public IActionOption getOption();
	public EntityLivingBase getPerformer();
	public DamageComponent getStrength();
	public Optional<EntityLivingBase> getTarget();
	public Optional<BlockPos> getTargetCoordinate();
	public IActionPerformer.TargetType getTargetType();
	public World getWorld();
	public void playSound(XYZPos pos,SoundEvent soundIn,boolean distanceDelay);
	public void playSound(XYZPos pos,SoundEvent soundIn,boolean distanceDelay,float pitch);
	public IActionPerformer setOption(IActionOption option);
	public IActionPerformer setTarget(EntityLivingBase liv);
	public void setTargetCoordinate(BlockPos pos);
	public void spawnParticle(ParticleHelper.MovingType type,Entity target,EnumParticleTypes par,int density,double speedscale);
	public void spawnParticle(ParticleHelper.MovingType type,XYZPos pos,EnumParticleTypes par,int density,double speedscale,int... params);
}
