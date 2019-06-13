package mods.hinasch.unsaga.core.potion;

import java.util.function.Predicate;

import mods.hinasch.lib.util.Statics;
import mods.hinasch.lib.util.VecUtil;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.ability.specialmove.action.TechActionMeleeGhost;
import mods.hinasch.unsaga.core.entity.passive.EntityShadowClone;
import mods.hinasch.unsaga.damage.AdditionalDamage;
import mods.hinasch.unsaga.damage.DamageComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class PotionBuff extends PotionUnsaga{

	protected PotionBuff(String name, int u, int v) {
		super(name, false, 0x0000ff, u, v);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	protected PotionBuff(String name, int u,int v,IAttribute attribute,String uuid,double value) {
		super(name, false, 0x0000ff, u, v);
		this.registerPotionAttributeModifier(attribute, uuid, value, Statics.OPERATION_INCREMENT);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public static class Swimming extends PotionBuff{
		protected Swimming(String name, int u, int v) {
			super(name, u, v);
			// TODO 自動生成されたコンストラクター・スタブ
		}

		@Override
		public void onTick(World world,EntityLivingBase entity,int amplifier){
			double scale = 1.2D;
			Vec3d vec = new Vec3d(entity.motionX,entity.motionY,entity.motionZ);
			if(entity.isInWater() && (double)MathHelper.sqrt(entity.motionX* entity.motionX + entity.motionY * entity.motionY + entity.motionZ * entity.motionZ)<2.0D){

				entity.setVelocity(entity.motionX*scale,entity.motionY*scale,entity.motionZ*scale);
			}
		}
	}
	public static class Doubled extends PotionBuff{

		protected Doubled(String name, int u, int v) {
			super(name, u, v);
			// TODO 自動生成されたコンストラクター・スタブ
		}

		@Override
		public void affectOnAttacked(LivingDamageEvent e, AdditionalDamage data, int amplifier) {
			World w = e.getEntityLiving().getEntityWorld();
			EntityLivingBase victim = e.getEntityLiving();
//			UnsagaMod.logger.splitter("Potion");
//			UnsagaMod.logger.trace(this.getName(), data,e.getSource().getImmediateSource(),e.getSource().getTrueSource());
//			UnsagaMod.logger.splitter("");
			if(!(e.getSource().getImmediateSource() instanceof EntityShadowClone) && data!=null){
				if(e.getSource().getTrueSource() instanceof EntityLivingBase){
					EntityLivingBase attacker = (EntityLivingBase) e.getSource().getTrueSource();
					DamageComponent dc = DamageComponent.of(e.getAmount(), data.getLPAttribute());

					EntityShadowClone clone = TechActionMeleeGhost.getGhost(w, attacker, victim, attacker.getHeldItemMainhand(), dc, data.getDamageTypes());
					Vec3d vec = victim.getLookVec().normalize().scale(1.0D).rotateYaw((float) Math.toRadians(180));
					XYZPos pos = XYZPos.createFrom(victim);
					clone.setPosition(pos.dx+vec.x,pos.dy,pos.dz+vec.z);
					clone.setRenderYawOffset(victim.rotationYaw);
					clone.setDelay(2);
					WorldHelper.safeSpawn(w, clone);
					w.playSound(pos.dx, pos.dy, pos.dz, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F, false);
//					Vec3d vec = victim.getLookVec().normalize().scale(1.0D).rotateYaw((float) Math.toRadians(180));
//					XYZPos pos = XYZPos.createFrom(clone);
//					clone.setPosition(pos.dx+vec.x,pos.dy,pos.dz+vec.z);
//					clone.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, attacker.getHeldItemMainhand());
//					clone.setRenderYawOffset(victim.rotationYaw);
				}


			}
		}

		@Override
		public void onHurt(LivingHurtEvent e,int amplifier){

		}
	}
	public static class SpellMagnet extends PotionBuff{

		final Predicate<Entity> filter;
		protected SpellMagnet(String name, int u, int v,Predicate<Entity> filter) {
			super(name, u, v);
			this.filter = filter;
			// TODO 自動生成されたコンストラクター・スタブ
		}

		@Override
		public void onTick(World world,EntityLivingBase living,int amplifier){
			int amp = 1 + amplifier;
			world.getEntitiesWithinAABB(Entity.class, living.getEntityBoundingBox().grow(8.0D * amp)
					,in -> in!=living &&(this.filter.test(in)))
			.forEach(in ->{
				Vec3d vec = VecUtil.getHeadingToEntityVec(in,living).normalize().scale(0.2D);
				in.setVelocity(vec.x, vec.y , vec.z);
			});
		}
	}


	@Override
	public void onTick(World world, EntityLivingBase living, int amp) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void onDamage(LivingDamageEvent e, AdditionalDamage data, int amplifier) {
		// TODO 自動生成されたメソッド・スタブ

	}
}
