package mods.hinasch.unsaga.core.entity.projectile.custom_arrow;

import java.util.Optional;

import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.entity.EntityArrowUpdateEvent;
import mods.hinasch.lib.network.PacketUtil;
import mods.hinasch.lib.particle.PacketParticle;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.lib.world.WorldHelper;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CustomArrowEventHandler {

	public static void register(){
		HSLibs.registerEvent(new CustomArrowEventHandler());
	}
	@SubscribeEvent
	public void onArrowImpact(ProjectileImpactEvent.Arrow e){

		CustomArrowCapability.ADAPTER.getCapabilityOptional(e.getArrow())
		.ifPresent(in ->in.getArrowType().onImpact(e));

	}

	@SubscribeEvent
	public void onArrowUpdate(EntityArrowUpdateEvent e){
		EntityArrow arrow = e.getArrow();
		CustomArrowCapability.ADAPTER.getCapabilityOptional(arrow)
		.filter(in -> WorldHelper.isServer(e.getArrow().getEntityWorld()))
		.filter(in ->arrow.ticksExisted % 3 == 0)
		.ifPresent(in ->{
			NBTTagCompound nbt = UtilNBT.compound();
			arrow.writeEntityToNBT(nbt);
			boolean inGround = nbt.getBoolean("inGround");
			if(in.getArrowType()==SpecialArrowType.ARROW_RAIN && inGround){
				arrow.setDead();
			}
			Optional.ofNullable(in.getArrowType().getParticle())
			.filter(type -> !inGround)
			.ifPresent(particle ->{
				HSLib.packetDispatcher().sendToAllAround(PacketParticle.toEntity(particle, arrow, 15), PacketUtil.getTargetPointNear(arrow,300D));
			});

		});

	}


	public static void onEntityJoin(EntityJoinWorldEvent e){
		//		if(WorldHelper.isServer(e.getWorld())){
		//			return;
		//		}
		//		if(e.getEntity() instanceof EntityArrow){
		//			if(CustomArrowCapability.ADAPTER.hasCapability(e.getEntity())){
		//				ICustomArrow instance = CustomArrowCapability.ADAPTER.getCapability(e.getEntity());
		//				if(instance.getArrowType()!=Type.NONE){
		//					NBTTagCompound args = UtilNBT.compound();
		//					args.setInteger("entityid", e.getEntity().getEntityId());
		//					HSLib.getPacketDispatcher().sendToServer(PacketSyncCapability.createRequest(CustomArrowCapability.CAPA, instance,args));
		//				}
		//
		//			}
		//		}
	}
	//
	//	@SubscribeEvent
	//	public void arrowParticleEvent(LivingUpdateEvent e){
	//		World world = e.getEntityLiving().getEntityWorld();
	//		if(WorldHelper.isServer(world)){
	//			return;
	//		}
	//		world.getEntitiesWithinAABB(EntityArrow.class, e.getEntityLiving().getEntityBoundingBox().grow(50D))
	//		.forEach(in ->{
	//			if(in.ticksExisted % 12 == 0){
	//				StateArrow.Type type = CustomArrowCapability.ADAPTER.getCapability(in).getArrowType();
	//				EnumParticleTypes particle = type.getParticle();
	//				ParticleHelper.MovingType.DIVERGE.spawnParticle(world, XYZPos.createFrom(in), particle, world.rand, 10, 0.2D);
	//			}
	//		});
	//	}
}
