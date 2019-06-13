package mods.hinasch.unsaga.common.specialaction;

import java.util.function.Consumer;

import net.minecraft.util.EnumActionResult;

/**
 *
 * IActionのListを持ち、performで順に起動。
 * 術や技の動きの基礎になり、技や術の固定情報のPropertyとこの動きで一対になる。
 *
 * @param <T>
 */
public interface IActionBase<T extends IActionPerformer> {

	public EnumActionResult perform(T context);
	/** 基本的にAIで使う*/
	public boolean isBenefical();
	public Consumer<T> getPrePerform();
}
