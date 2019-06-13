package mods.hinasch.unsaga.core.potion;

import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.unsaga.damage.AdditionalDamage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;

public class PotionDebuff extends PotionUnsaga{

	public static class Gravity extends PotionDebuff{

		protected Gravity(String name, int u, int v) {
			super(name, u, v);
			// TODO 自動生成されたコンストラクター・スタブ
		}


		@Override
		public void onTick(World world,EntityLivingBase living,int amplifier){
			if(living.posY>0){
				living.motionY -= 1.0D;
			}
		}
	}


	public static class Stun extends PotionDebuff{

		protected Stun(String name, int u, int v) {
			super(name, u, v);
			// TODO 自動生成されたコンストラクター・スタブ
		}
		@Override
		public void onTick(World world,EntityLivingBase living,int amplifier){
			if(!living.isPotionActive(MobEffects.SLOWNESS)){
				living.addPotionEffect(new PotionEffect(MobEffects.SLOWNESS,ItemUtil.getPotionTime(3),10));
			}
		}
	}

	protected PotionDebuff(String name, int u, int v) {
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
