package mods.hinasch.unsaga.core.client.model;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;

public class ModelGelatinous extends ModelBase{
    public ModelRenderer body;
    public ModelRenderer organ1;
    public ModelRenderer organ_big;
    public ModelRenderer organ2;

    boolean isGel = false;

    public ModelGelatinous(final boolean isGel) {
        this.textureWidth = 64;
        this.textureHeight = 32;

        this.isGel = isGel;


        if(this.isGel){

            this.body = new ModelRenderer(this, 0, 0);
            this.body.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.body.addBox(-3.0F, 16.0F, -3.0F, 8, 8, 8);
        }else{
            this.organ1 = new ModelRenderer(this, 0, 22);
            this.organ1.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.organ1.addBox(1.0F, 20.7F, -1.7F, 2, 2, 2);
            this.organ_big = new ModelRenderer(this, 0, 16);
            this.organ_big.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.organ_big.addBox(-1.8F, 17.0F, 0.0F, 3, 3, 3);
            this.organ2 = new ModelRenderer(this, 0, 22);
            this.organ2.setRotationPoint(0.0F, 0.0F, 0.0F);
            this.organ2.addBox(1.4F, 19.9F, 2.3F, 2, 2, 2);
        }

    }

    @Override
    public void render(Entity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float rotationYaw, float rotationPitch, float scale) {

    	if(this.isGel){
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 0.5F);
            this.body.render(scale);
            GlStateManager.disableBlend();
    	}else{
            GlStateManager.disableBlend();
            GlStateManager.enableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.organ1.render(scale);
            GlStateManager.disableBlend();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F,1.0F);
            this.organ_big.render(scale);
            GlStateManager.disableBlend();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            this.organ2.render(scale);
//            GlStateManager.enableNormalize();

    	}


//        GlStateManager.disableBlend();
    }

    public void setRotationAngles(ModelRenderer modelRenderer, float x, float y, float z) {
        modelRenderer.rotateAngleX = x;
        modelRenderer.rotateAngleY = y;
        modelRenderer.rotateAngleZ = z;
    }
}
