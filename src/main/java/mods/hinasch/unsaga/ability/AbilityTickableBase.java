package mods.hinasch.unsaga.ability;

import java.util.List;
import java.util.stream.Collectors;

import mods.hinasch.lib.util.HSLibs;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

/**
 *
 * Tickで呼び出されるアビリティのベース。
 *
 */
public abstract class AbilityTickableBase extends Ability{

	/** アビリティの発動する間隔、全デバフ抑制とかは長めに*/
	public static final int SUPERHEALING_INTERVAL = 10;

	public static final int ANTI_DEBUFF_ALL_INTERVAL = 20;
	public static final int ANTI_DEBUFF_INTERVAL = 8;

	public AbilityTickableBase(final String name) {
		super(name);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public abstract void onLivingUpdate(LivingUpdateEvent e);

	public static class Factory{

		public static AbilityTickableBase superHealing(final String name){
			return new SuperHealing(name);
		}

		public static AbilityTickableBase antiDebuff(final String name){
			return new AntiDebuff(name);
		}

	}

	public static class SuperHealing extends AbilityTickableBase{

		public SuperHealing(final String name) {
			super(name);
			// TODO 自動生成されたコンストラクター・スタブ
		}

		@Override
		public void onLivingUpdate(final LivingUpdateEvent e) {
			if(e.getEntityLiving().ticksExisted % AbilityTickableBase.SUPERHEALING_INTERVAL ==0){
				e.getEntityLiving().heal(0.5F);
			}
		}

	}

	public static class AntiDebuff extends AbilityTickableBase{

		public AntiDebuff(final String name) {
			super(name);
			// TODO 自動生成されたコンストラクター・スタブ
		}

		@Override
		public void onLivingUpdate(final LivingUpdateEvent e) {
			if(e.getEntityLiving().ticksExisted % AbilityTickableBase.ANTI_DEBUFF_ALL_INTERVAL ==0){
				final List<Potion> antis = e.getEntityLiving().getActivePotionEffects().stream().map(in -> in.getPotion()).filter(in -> in.isBadEffect()).collect(Collectors.toList());
				final Potion randomPick = HSLibs.randomPick(e.getEntityLiving().getRNG(), antis);
				e.getEntityLiving().removeActivePotionEffect(randomPick);
			}
		}

	}
}
