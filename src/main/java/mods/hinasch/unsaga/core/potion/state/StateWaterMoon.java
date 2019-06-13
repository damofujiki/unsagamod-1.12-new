package mods.hinasch.unsaga.core.potion.state;

import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.client.IRenderLivingEffect;
import mods.hinasch.unsaga.core.client.model.ModelThinBox;
import mods.hinasch.unsaga.core.potion.EntityState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderLivingEvent.Post;

public class StateWaterMoon extends EntityState implements IRenderLivingEffect{

	public static final ResourceLocation CIRCLE = new ResourceLocation(UnsagaMod.MODID,"textures/entity/moon.png");
	public static ModelThinBox modelShield = new ModelThinBox();

	public StateWaterMoon(String name) {
		super(name);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public void renderEffect(Post e) {
		GlStateManager.pushMatrix();
		GlStateManager.enableRescaleNormal();
		GlStateManager.translate(e.getX(),e.getY()+0.15D,e.getZ());
		GlStateManager.scale(0.3F, 0.3F, 0.3F);
		GlStateManager.rotate(180F, 1.0F, 0F, 0.0F);
		GlStateManager.color(0.5F, 0.5F, 0.5F, 1.0F);
		GlStateManager.disableCull();
		GlStateManager.disableLighting();
		GlStateManager.enableBlend();
		GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

		ClientHelper.bindTextureToTextureManager(CIRCLE);


		this.modelShield.render(null, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.625F);
		GlStateManager.disableBlend();
		GlStateManager.enableLighting();
		GlStateManager.enableCull();
		GlStateManager.popMatrix();
	}

}
