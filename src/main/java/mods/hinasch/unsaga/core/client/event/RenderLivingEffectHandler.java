package mods.hinasch.unsaga.core.client.event;

import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.unsaga.core.client.IRenderLivingEffect;
import mods.hinasch.unsaga.skillpanel.SkillPanelAPI;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelGhast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


/** 生物のポーションエフェクトやら*/
public class RenderLivingEffectHandler {

	public ModelGhast ghastModel = new ModelGhast();


	public RenderLivingEffectHandler(){

	}
	@SubscribeEvent
	public void onRenderLiving(RenderLivingEvent.Post e){

		EntityLivingBase renderLiving = e.getEntity();
		World world = renderLiving.getEntityWorld();


		/** ポーションの専用エフェクト描画*/
		for(PotionEffect effect:e.getEntity().getActivePotionEffects()){
			if(effect.getPotion() instanceof IRenderLivingEffect){
				IRenderLivingEffect render = (IRenderLivingEffect)effect.getPotion();
				render.renderEffect(e);
			}
		}
		StateCapability.ADAPTER.getCapabilityOptional(renderLiving)
		.ifPresent(in ->{
			for(PotionEffect effect:in.getStateMap().values()){
				if(effect.getPotion() instanceof IRenderLivingEffect){
					IRenderLivingEffect render = (IRenderLivingEffect)effect.getPotion();
					render.renderEffect(e);
				}
			}
		});


		/** ファミリア（？）の表示。途中*/
		if(renderLiving instanceof EntityPlayer && SkillPanelAPI.hasFamiliar((EntityPlayer) renderLiving)){
			GlStateManager.pushMatrix();
			GlStateManager.enableRescaleNormal();
			GlStateManager.disableLighting();
			GlStateManager.disableCull();
			float scala = 0.025F;

			GlStateManager.translate(1.0F, 1.0F, 0.0F);
			GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
			GlStateManager.scale(scala, scala, scala);
			GlStateManager.rotate(180F, 1.0F, 0F, 0.0F);

			Minecraft.getMinecraft().renderEngine.bindTexture(new ResourceLocation("textures/entity/ghast/ghast.png"));
			ghastModel.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
			GlStateManager.disableRescaleNormal();
			GlStateManager.enableCull();
			GlStateManager.enableLighting();
			GlStateManager.popMatrix();
		}


	}

}
