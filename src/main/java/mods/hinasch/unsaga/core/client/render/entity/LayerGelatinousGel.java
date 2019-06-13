package mods.hinasch.unsaga.core.client.render.entity;

import mods.hinasch.unsaga.core.client.model.ModelGelatinous;
import mods.hinasch.unsaga.core.entity.mob.EntityGelatinous;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;

public class LayerGelatinousGel implements LayerRenderer<EntityGelatinous>{


	final RenderGelatinous parent;
	final ModelGelatinous model;

	public LayerGelatinousGel(RenderGelatinous parent){
		this.parent = parent;
		this.model = new ModelGelatinous(true);
	}
	@Override
	public boolean shouldCombineTextures() {
		// TODO 自動生成されたメソッド・スタブ
		return true;
	}

	@Override
	public void doRenderLayer(EntityGelatinous entitylivingbaseIn, float limbSwing, float limbSwingAmount,
			float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
		// TODO 自動生成されたメソッド・スタブ
        if (!entitylivingbaseIn.isInvisible())
        {
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.enableNormalize();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.model.setModelAttributes(this.parent.getMainModel());
            this.model.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.disableBlend();
            GlStateManager.disableNormalize();
        }
	}

}
