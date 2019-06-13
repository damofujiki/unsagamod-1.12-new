package mods.hinasch.unsaga.core.client.render.entity;

import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.entity.passive.EntityShadowClone;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

public class RenderShadowGhost extends RenderBiped<EntityShadowClone>{

	public static final ResourceLocation SHADOW_TEXTURE = new ResourceLocation(UnsagaMod.MODID,"textures/entity/shadow.png");
	public static ModelBiped model = new ModelBiped();
	public RenderShadowGhost(RenderManager renderManagerIn) {
		super(renderManagerIn, new ModelBiped(), 0.0F);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
    protected ResourceLocation getEntityTexture(EntityShadowClone entity){
    	return SHADOW_TEXTURE;
    }


    public void renderGhost(EntityShadowClone entity, double x, double y, double z, float entityYaw, float partialTicks)
    {

		GlStateManager.color(1, 1, 1,0.6F);
    	GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
//    	this.mainModel.swingProgress = entity.getSwingProgress(partialTicks);
        super.doRender(entity, x, y, z, entityYaw, partialTicks);

    }

	@Override
    public void doRender(EntityShadowClone entity, double x, double y, double z, float entityYaw, float partialTicks)
    {

        GlStateManager.pushMatrix();
		GlStateManager.enableBlend();
		GlStateManager.enableAlpha();
		if(entity.getMoveType()==EntityShadowClone.MoveType.REVERSE_DELTA){
			Vec3d vec = new Vec3d(-2.0D,0,0);
			float r1 = (entity.getRotation() - entity.getPrevRotation()) * partialTicks;
			Vec3d v1 = vec.rotateYaw(entity.getPrevRotation()+r1);
			Vec3d v2 = v1.rotateYaw((float) Math.toRadians(120.0F));
			Vec3d v3 = v1.rotateYaw((float) Math.toRadians(240.0F));

	        Tessellator tessellator = Tessellator.getInstance();
	        BufferBuilder bufferbuilder = tessellator.getBuffer();

	        bufferbuilder.begin(5, DefaultVertexFormats.POSITION_NORMAL);
	        bufferbuilder.pos(x+v1.x, y+v1.y, z+v1.z).color(255, 255, 255, 100).endVertex();
	        bufferbuilder.pos(x+v2.x, y+v2.y, z+v2.z).color(255, 255, 255, 100).endVertex();
	        bufferbuilder.pos(x+v3.x, y+v3.y, z+v3.z).color(255, 255, 255, 100).endVertex();
	        bufferbuilder.pos(x+v1.x, y+v1.y, z+v1.z).color(255, 255, 255, 100).endVertex();
	        tessellator.draw();


			this.renderGhost(entity, x+v1.x, y+v1.y, z+v1.z, entityYaw, partialTicks);
			this.renderGhost(entity, x+v2.x, y+v2.y, z+v2.z, entityYaw, partialTicks);
			this.renderGhost(entity, x+v3.x, y+v3.y, z+v3.z, entityYaw, partialTicks);


//			this.renderGhost(entity, x+2.0D, y, z, 90, partialTicks);
//			this.renderGhost(entity, x, y, z-2.0D, 180, partialTicks);
		}else{
			this.renderGhost(entity, x, y, z, entityYaw, partialTicks);
		}
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.popMatrix();
    }
}
