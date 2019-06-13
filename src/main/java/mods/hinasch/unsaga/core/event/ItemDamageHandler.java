package mods.hinasch.unsaga.core.event;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import mods.hinasch.lib.item.ItemDamageEvent;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.world.UnsagaWorldCapability;
import mods.hinasch.unsaga.minsaga.MinsagaForgingCapability;
import mods.hinasch.unsaga.minsaga.classes.PlayerClasses;
import mods.hinasch.unsaga.skillpanel.SkillPanelAPI;
import mods.hinasch.unsaga.skillpanel.SkillPanels;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ItemDamageHandler {

	@SubscribeEvent
	public void onItemDamage(ItemDamageEvent e){

		UnsagaMod.logger.trace("onItemDamage","called");
		if(e.getDamage()<=0){
			return;
		}

		if(e.getPlayer()==null){
			return;
		}
		World world = e.getPlayer().getEntityWorld();

		if(world==null){
			return;
		}
		//		int negateAmount = 0;
		List<Integer> negates = new ArrayList<>();

		if(UnsagaWorldCapability.playerClassStorage(world).getClass(e.getPlayer())==PlayerClasses.CRAFTSMAN){
			for(int i=0;i < e.getDamage();i++){
				int level = UnsagaWorldCapability.playerClassStorage(world).getEntry(e.getPlayer()).level();
				if(e.rand().nextFloat()<(0.15F+0.05F*level)){
					negates.add(1);
				}
			}
		}


		SkillPanelAPI.getHighestPanelLevel(e.getPlayer(), SkillPanels.THRIFT_SAVER)
		.ifPresent(lv ->{
			for(int i=0;i < e.getDamage();i++){
				if(e.rand().nextFloat()<(0.15F+0.05F*lv)){
					negates.add(1);
				}
			}
		});




		MinsagaForgingCapability.ADAPTER.getCapabilityOptional(e.getStack())
		.ifPresent(in ->{
			double d = in.durabilityModifier();
			if(d!=0){
				IntStream.range(0, e.getDamage())
				.forEach(num ->{
					if(e.rand().nextFloat()<(0.15F+0.05F*Math.abs(d))){
						negates.add(d>0 ? 1 : -1);
					}
				});

			}
		});




		UnsagaMod.logger.trace(this.getClass().getName(), e.getDamage(),"negate:"+negates.stream().mapToInt(in -> in).sum());
		int amount = e.getDamage() - negates.stream().mapToInt(in -> in).sum();

		if(amount<=0){
			amount = 0;
		}

		e.setDamage(amount);

	}
}
