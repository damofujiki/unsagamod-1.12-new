package mods.hinasch.unsaga.core.potion.state;

import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.network.PacketUtil;
import mods.hinasch.lib.particle.PacketParticle;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.net.packet.PacketSyncActionPerform;
import mods.hinasch.unsaga.core.potion.EntityState;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import mods.hinasch.unsagamagic.spell.action.SpellCastIgniter;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class StateCasting extends EntityState{

	public StateCasting(String name) {
		super(name);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public boolean isValidPotionEffect(PotionEffect effect){
		return effect instanceof Effect;
	}

	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int p_76394_2_)
	{
		super.performEffect(entityLivingBaseIn, p_76394_2_);
		World world = entityLivingBaseIn.world;
		if(StateCapability.isStateActive(entityLivingBaseIn, UnsagaPotions.CASTING)){
			Effect casting = (Effect) StateCapability.getState(entityLivingBaseIn, UnsagaPotions.CASTING);
			if(WorldHelper.isServer(entityLivingBaseIn.getEntityWorld())){
				HSLib.packetDispatcher().sendToAllAround(PacketParticle.create(XYZPos.createFrom(entityLivingBaseIn), EnumParticleTypes.SPELL, 30), PacketUtil.getTargetPointNear(entityLivingBaseIn));
//				ParticleHelper.MovingType.FLOATING.spawnParticle(world, XYZPos.createFrom(entityLivingBaseIn)
//						, EnumParticleTypes.ENCHANTMENT_TABLE, world.rand, 10, 0.02D);
			}
			if(casting.getDuration()<=1 && WorldHelper.isServer(world)){
//				SpellActionBase action = (SpellActionBase) casting.getIgniter().getAction();
//				action.getSpellTargetSelector(10).decideTarget(casting.getIgniter(), entityLivingBaseIn, target);
				casting.getIgniter().cast();
//				int entityid = casting.getIgniter().getPerformer().getEntityId();
//				int targetid = casting.getIgniter().getTarget().isPresent() ? casting.getIgniter().getTarget().get().getEntityId() : -1;
				UnsagaMod.PACKET_DISPATCHER.sendToAllAround(PacketSyncActionPerform.createSyncSpellCastPacket(casting.getIgniter()),PacketUtil.getTargetPointNear(entityLivingBaseIn));

//				casting.getIgniter().cast();
				entityLivingBaseIn.removeActivePotionEffect(this);
			}
//			UnsagaMod.logger.trace("effect6", effect.getDuration());
		}


	}

	public static Effect create(SpellCastIgniter igniter,int casttime){
		return new Effect(casttime,igniter);
	}
	public static class Effect extends PotionEffect{

		final SpellCastIgniter caster;
		public Effect(int durationIn,SpellCastIgniter caster) {
			super(UnsagaPotions.CASTING, durationIn);
			// TODO 自動生成されたコンストラクター・スタブ
			this.caster = caster;
		}

		public SpellCastIgniter getIgniter(){
			return this.caster;
		}
	}
}
