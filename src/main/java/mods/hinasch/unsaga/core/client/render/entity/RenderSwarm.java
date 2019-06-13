package mods.hinasch.unsaga.core.client.render.entity;

import mods.hinasch.unsaga.core.entity.mob.EntitySwarm;
import net.minecraft.client.model.ModelSilverfish;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class RenderSwarm extends RenderLiving<EntitySwarm>{

    private static final ResourceLocation SILVERFISH_TEXTURES = new ResourceLocation("textures/entity/silverfish.png");

	public RenderSwarm(RenderManager rendermanagerIn) {
		super(rendermanagerIn, new ModelSilverfish(), 0.3F);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
    protected float getDeathMaxRotation(EntitySwarm entityLivingBaseIn)
    {
        return 180.0F;
    }

	@Override
	protected ResourceLocation getEntityTexture(EntitySwarm entity) {
		// TODO 自動生成されたメソッド・スタブ
		return SILVERFISH_TEXTURES;
	}

}
