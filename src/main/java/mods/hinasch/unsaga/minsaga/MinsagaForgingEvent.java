package mods.hinasch.unsaga.minsaga;

import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import mods.hinasch.unsaga.minsaga.event.ImproveHarvestHandler;
import mods.hinasch.unsaga.minsaga.event.ItemUseFinishedHandler;
import mods.hinasch.unsaga.minsaga.event.MinsagaAnvilHandler;
import mods.hinasch.unsaga.minsaga.event.MinsagaRefleshModifierHandler;
import mods.hinasch.unsaga.minsaga.event.MinsagaTooltipHandler;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import net.minecraftforge.oredict.OreDictionary;



public class MinsagaForgingEvent {


	public static enum Type{
		WEAPON,ARMOR;
	}

	public static void registerEvents(){

		OreDictionary.registerOre("hardShell", UnsagaMaterials.CHITIN.itemStack());
		OreDictionary.registerOre("hardShell", new ItemStack(Items.SHULKER_SHELL));
		HSLibs.registerEvent(new MinsagaTooltipHandler());
		HSLibs.registerEvent(new MinsagaRefleshModifierHandler());

//		HSLib.events.livingHurt.getEventsMiddle().add(new EventFittingProgressMinsaga());

//		HSLibs.registerEvent(new EventApplyForgedAbility());
		HSLibs.registerEvent(new ImproveHarvestHandler());
		HSLibs.registerEvent(new ItemUseFinishedHandler());
		/**
		 * かなとこでの修理
		 */
		HSLibs.registerEvent(new MinsagaAnvilHandler());

		/**
		 * 採掘速度の実装
		 */
		HSLibs.registerEvent(new BreakSpeedHandler());

//		/**
//		 * 強度の実装
//		 */
//		EventSaveDamage.events.add(living ->{
//			if(living instanceof EntityPlayer){
//				EntityPlayer ep = (EntityPlayer) living;
//				World world = ep.getEntityWorld();
//				ItemStack is = living.getHeldItemMainhand();
//				if(is!=null && MinsagaForgeCapability.ADAPTER.hasCapability(is)){
//					MinsagaUtil.damageToolProcess(ep,is,MinsagaForgeCapability.ADAPTER.getCapability(is).getDurabilityModifier(),ep.getRNG());
//				}
//
//			}
//		});
		//		LivingHelper.registerEquipmentsChangedEvent(living -> {
		//
		//		});
	}

	public static class BreakSpeedHandler{

		public void modifyBreakSpeed(BreakSpeed ev){
			ItemStack is = ev.getEntityPlayer().getHeldItemMainhand();
			if(!is.isEmpty() && MinsagaForgingCapability.ADAPTER.hasCapability(is)){
				float speed = ev.getOriginalSpeed() + MinsagaForgingCapability.ADAPTER.getCapability(is).efficiencyModifier();
				if(ev.getEntityPlayer().isInWater() && MinsagaUtil.getAbilities(ev.getEntityPlayer()).contains(MinsagaMaterialInitializer.Ability.SEA)){
					speed *= 2.0F;
				}
				ev.setNewSpeed(speed);
			}
		}
	}
}
