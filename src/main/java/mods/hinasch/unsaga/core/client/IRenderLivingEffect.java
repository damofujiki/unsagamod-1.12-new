package mods.hinasch.unsaga.core.client;

import net.minecraftforge.client.event.RenderLivingEvent;

/** ポーションエフェクト表示に使う*/
public interface IRenderLivingEffect {

	public void renderEffect(RenderLivingEvent.Post e);
}
