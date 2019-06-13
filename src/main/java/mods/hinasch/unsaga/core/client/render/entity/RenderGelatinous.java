package mods.hinasch.unsaga.core.client.render.entity;

import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.client.model.ModelGelatinous;
import mods.hinasch.unsaga.core.entity.mob.EntityGelatinous;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderGelatinous extends RenderLiving<EntityGelatinous>{


	public static final ResourceLocation GELATINOUS_MATTER_TEX = new ResourceLocation(UnsagaMod.MODID,"textures/entity/mob/gelatinous_matter.png");
	public static final ResourceLocation GOLDENBAUM_TEX = new ResourceLocation(UnsagaMod.MODID,"textures/entity/mob/goldenbaum.png");


	public RenderGelatinous(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelGelatinous(false), 1.0F);
		this.addLayer(new LayerGelatinousGel(this));
	}

	@Override
	protected ResourceLocation getEntityTexture(EntityGelatinous entity) {
		// TODO 自動生成されたメソッド・スタブ
		return entity.getTextureName();
	}

	@Override
    public void doRender(EntityGelatinous entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.shadowSize = 0.25F * (float)entity.getSlimeSize();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }


    @Override
    protected void preRenderCallback(EntityGelatinous entitylivingbaseIn, float partialTickTime)
    {
        float f = 0.999F;
        GlStateManager.scale(0.999F, 0.999F, 0.999F);
        float f1 = (float)entitylivingbaseIn.getSlimeSize();
        float f2 = (entitylivingbaseIn.prevSquishFactor + (entitylivingbaseIn.squishFactor - entitylivingbaseIn.prevSquishFactor) * partialTickTime) / (f1 * 0.5F + 1.0F);
        float f3 = 1.0F / (f2 + 1.0F);
        GlStateManager.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
    }

}
