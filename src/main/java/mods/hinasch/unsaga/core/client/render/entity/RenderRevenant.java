package mods.hinasch.unsaga.core.client.render.entity;

import mods.hinasch.unsaga.UnsagaMod;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderZombie;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.ResourceLocation;

public class RenderRevenant extends RenderZombie{

	public static final ResourceLocation REVENANT_TEX = new ResourceLocation(UnsagaMod.MODID,"textures/entity/mob/revenant.png");
	public RenderRevenant(RenderManager renderManagerIn) {
		super(renderManagerIn);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
    protected ResourceLocation getEntityTexture(EntityZombie entity)
    {
        return REVENANT_TEX;
    }
}
