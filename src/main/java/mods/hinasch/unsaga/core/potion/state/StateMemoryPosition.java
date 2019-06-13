package mods.hinasch.unsaga.core.potion.state;

import mods.hinasch.unsaga.core.potion.EntityState;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;

public class StateMemoryPosition extends EntityState{

	public StateMemoryPosition(String name) {
		super(name);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public boolean isValidPotionEffect(PotionEffect effect){
		return effect instanceof Effect;
	}

	public static class Effect extends PotionEffect{

		final BlockPos pos;
		public Effect(int durationIn,BlockPos pos) {
			super(UnsagaPotions.MEMORY_POSITION, durationIn);
			// TODO 自動生成されたコンストラクター・スタ
			this.pos = pos;
		}

		public BlockPos getPos(){
			return this.pos;
		}
	}
}
