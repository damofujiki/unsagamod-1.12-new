package mods.hinasch.unsaga.common;

import mods.hinasch.lib.entity.RangedEffect;
import mods.hinasch.unsaga.common.specialaction.IActionPerformer;
import net.minecraft.world.World;

public class RangedEffectTech<T extends IActionPerformer> extends RangedEffect{

	final T invoker;

	public RangedEffectTech(T invoker,Builder builder) {
		super(builder);
		this.invoker = invoker;
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public RangedEffectTech(T invoker,RangedEffect.Builder builder) {
		super(builder);
		this.invoker = invoker;
		// TODO 自動生成されたコンストラクター・スタブ
	}
	public T context(){
		return this.invoker;
	}


	public static <K extends IActionPerformer> Builder builder(K invoker){
		return new Builder(invoker);
	}

	public static <Y extends IActionPerformer> RangedEffectTech<Y> convert(Y invoker,RangedEffect.Builder builder){
		return new RangedEffectTech(invoker,builder.world(invoker.getWorld()));
	}
	public static class Builder<T extends IActionPerformer> extends RangedEffect.Builder{

		final T invoker;
		public Builder(T invoker){
			this.invoker = invoker;
		}

		@Override
		public RangedEffectTech build(World world){
			this.world(world);
			return new RangedEffectTech(invoker, this);
		}
	}
}
