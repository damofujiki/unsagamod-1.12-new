package mods.hinasch.unsaga.core.potion;

import mods.hinasch.unsaga.skillpanel.ISkillPanel;
import mods.hinasch.unsaga.skillpanel.SkillPanelAPI;
import mods.hinasch.unsaga.skillpanel.SkillPanels;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerVisibilityHandlerUnsaga {

	@SubscribeEvent
	public void modifierVisibility(PlayerEvent.Visibility ev){
		EntityPlayer ep = ev.getEntityPlayer();
		ISkillPanel incospicious = SkillPanels.INCONSPICIOUS;
		double modifier = 1.0D;

		if(ep.isPotionActive(UnsagaPotions.SILENT_MOVE)){

			int amp = ep.getActivePotionEffect(UnsagaPotions.SILENT_MOVE).getAmplifier();
			final double base = amp == 0 ? 0.2D : 0D;
			//地味があるとさらに敵の視認性が下がる
			if(SkillPanelAPI.hasPanel(ep, incospicious)){
				int level = SkillPanelAPI.getHighestPanelLevel(ep, incospicious).getAsInt();
				modifier = base - (0.04D*level);
			}else{
				modifier = base;
			}


		}else{
			//地味だけでも効果あり
			if(SkillPanelAPI.hasPanel(ep, SkillPanels.INCONSPICIOUS)){
				modifier = 0.8D;
			}
		}

		ev.modifyVisibility(MathHelper.clamp(modifier, 0.0D, 2.0D));
	}
}
