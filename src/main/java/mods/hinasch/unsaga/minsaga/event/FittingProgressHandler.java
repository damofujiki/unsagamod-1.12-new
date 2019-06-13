package mods.hinasch.unsaga.minsaga.event;

import java.util.Optional;

import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew;
import mods.hinasch.unsaga.minsaga.IMinsagaForge;
import mods.hinasch.unsaga.minsaga.MaterialLayer;
import mods.hinasch.unsaga.minsaga.MinsagaForgingCapability;
import mods.hinasch.unsaga.minsaga.MinsagaUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class FittingProgressHandler{



	public static boolean apply(LivingHurtEvent e) {
		if(e.getSource().getTrueSource() instanceof EntityLivingBase){ //Attacker
			EntityLivingBase el = (EntityLivingBase) e.getSource().getTrueSource();
			return !el.getHeldItemMainhand().isEmpty()
					&& MinsagaForgingCapability.ADAPTER.hasCapability(el.getHeldItemMainhand());
		}

		return false;
	}


	public static void process(LivingHurtEvent e) {


		EntityLivingBase attacker = (EntityLivingBase) e.getSource().getTrueSource();

		MinsagaForgingCapability.ADAPTER.getCapabilityOptional(attacker.getHeldItemMainhand())
		.ifPresent(cap ->{
			if(cap.hasForged()){
				Optional.of(cap.getCurrentFittingLayer())
				.filter(in -> !in.isEmptyLayer())
				.filter(in -> in.getFittingProgress()<in.maxFittingProgress())
				.ifPresent(in ->{
					int added = in.getFittingProgress() + (UnsagaConfigHandlerNew.DEBUG.enableMinsagaQuickFitting ? 50 : 1);
					cap.updateCurrentFitting(added);
					if(attacker instanceof EntityPlayerMP){
						HSLib.packetDispatcher().sendTo(PacketSyncCapability.create(MinsagaForgingCapability.CAPA, cap), (EntityPlayerMP) attacker);
					}
				});
			}
		});


		//防具のフィッティング（なくす）
		if(e.getEntityLiving() instanceof EntityLivingBase){
			MinsagaUtil.getForgedArmors(e.getEntityLiving()).forEach(in ->{
				IMinsagaForge capability = MinsagaForgingCapability.ADAPTER.getCapability(in);
				if(capability.hasForged() && !capability.getCurrentFittingLayer().isEmptyLayer()){
					MaterialLayer layer = capability.getCurrentFittingLayer();
//					UnsagaMod.logger.trace("layer", layer.getMaterial().getPropertyName());
					int progress = (int) MathHelper.clamp(e.getAmount(), 0, 10);
					if(layer.getFittingProgress()<layer.maxFittingProgress() && progress>0){
						int added = layer.getFittingProgress()+ (UnsagaConfigHandlerNew.DEBUG.enableMinsagaQuickFitting ? 50 : progress);
						capability.updateCurrentFitting(added);
//						layer.setFittingProgress(layer.getFittingProgress()+ (UnsagaConfigHandlerNew.DEBUG.enableMinsagaQuickFitting ? 50 : progress));
						if(attacker instanceof EntityPlayerMP){
							HSLib.packetDispatcher().sendTo(PacketSyncCapability.create(MinsagaForgingCapability.CAPA, MinsagaForgingCapability.ADAPTER.getCapability(in)), (EntityPlayerMP) attacker);
						}

					}

//					MinsagaUtil.damageToolProcess(el,stack,capa.getDurabilityModifier(),el.getRNG());
				}
			});
		}
	}



}
