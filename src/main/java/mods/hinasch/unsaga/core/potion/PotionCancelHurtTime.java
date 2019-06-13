package mods.hinasch.unsaga.core.potion;

import mods.hinasch.unsaga.damage.AdditionalDamage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class PotionCancelHurtTime extends PotionUnsaga{

	protected PotionCancelHurtTime(String name, int u, int v) {
		super(name, true, 0xff0000, u, v);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public void onDamage(LivingDamageEvent e, AdditionalDamage data, int amplifier) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onTick(World world, EntityLivingBase living, int amp) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
