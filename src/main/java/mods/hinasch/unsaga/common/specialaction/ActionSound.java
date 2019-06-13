package mods.hinasch.unsaga.common.specialaction;

import java.util.function.Function;

import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.common.specialaction.ActionBase.IAction;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.SoundEvent;

public class ActionSound<T extends IActionPerformer> implements IAction<T> {

	final Function<T,XYZPos> pos;
	final SoundEvent event;
	final boolean distanceDelay;



	public ActionSound(Function<T,XYZPos> pos, SoundEvent event, boolean distanceDelay) {
		super();
		this.pos = pos;
		this.event = event;
		this.distanceDelay = distanceDelay;
	}



	@Override
	public EnumActionResult apply(T t) {
		t.playSound(pos.apply(t), event, distanceDelay);
		return EnumActionResult.PASS;
	}

}
