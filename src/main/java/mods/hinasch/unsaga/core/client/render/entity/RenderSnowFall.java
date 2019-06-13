package mods.hinasch.unsaga.core.client.render.entity;

import mods.hinasch.unsaga.core.client.model.ModelSnowFall;
import mods.hinasch.unsaga.core.entity.passive.EntitySnowFall;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class RenderSnowFall extends Render<EntitySnowFall>{
//	public static final ResourceLocation SNOW = new ResourceLocation(UnsagaMod.MODID,"textures/entity/snow.png");
	public static final ResourceLocation SNOW = new ResourceLocation("textures/environment/snow.png");
	ModelSnowFall modelSnowFall = new ModelSnowFall();

	public RenderSnowFall(RenderManager renderManager) {
		super(renderManager);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
    public void doRender(EntitySnowFall entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
		this.bindEntityTexture(entity);
		GlStateManager.pushMatrix();
		GlStateManager.disableCull();
		GlStateManager.translate(x, y + 16.0F, z);
		GlStateManager.scale(0.0625D,0.0625D,0.0625D);
		GlStateManager.rotate(entityYaw, 0, 1.0F, 0);
		GlStateManager.rotate(180F, 1.0F, 0, 0);

//		this.modelSnowFall.setTextureOffset(0,128 -( entity.getAnimationTick() % 128));
//        this.modelSnowFall.render(entity, 0, 0, 0, 0,0,0.0625F);
//		GL11.glTexParameterf(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, -10497.0F);

		Tessellator tesselator = Tessellator.getInstance();
		BufferBuilder builder = tesselator.getBuffer();
		double dx = 32D;
		double dy = 256D;
		double d1 = 1.0D;
		double d2 = 0.5D;
		int tick = entity.getAnimationTick();
		double speed = 100.0D;
		double d3 = d2 / speed * tick + (d2 / speed * partialTicks);
		builder.begin(7, DefaultVertexFormats.POSITION_TEX);
		builder.pos(dx, dy, 0).tex(d1,d1-d3).endVertex();
		builder.pos(dx, 0, 0).tex(d1,-d3).endVertex();
		builder.pos(-dx, 0, 0).tex(0,-d3).endVertex();
		builder.pos(-dx, dy, 0).tex(0,d1-d3).endVertex();
		tesselator.draw();
        GlStateManager.popMatrix();


    }
	@Override
	protected ResourceLocation getEntityTexture(EntitySnowFall entity) {
		// TODO 自動生成されたメソッド・スタブ
		return SNOW;
	}

}
