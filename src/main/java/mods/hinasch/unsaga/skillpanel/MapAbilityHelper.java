package mods.hinasch.unsaga.skillpanel;

import mods.hinasch.lib.entity.RangedEffect;
import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.util.ChatHandler;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.core.client.gui.GuiEquipment;
import mods.hinasch.unsaga.core.inventory.container.ContainerEquipment;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;

public class MapAbilityHelper{

	private static void prepareUse(World world,EntityPlayer ep,ContainerEquipment container,float exhaust){
		ep.closeScreen();
		container.broadcastSound(XYZPos.createFrom(ep), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, ep);
		ep.getFoodStats().addExhaustion(exhaust);
	}
	public static void onUseSwimming(World world,EntityPlayer ep,ContainerEquipment container){
		prepareUse(world, ep, container, 0.5F);
		if(WorldHelper.isServer(ep.getEntityWorld())){
			ep.addPotionEffect(new PotionEffect(UnsagaPotions.SWIM_WELL,calcPotioinTime(ep, SkillPanels.SWIMMING),0));

		}

	}

	public static void onUseSmartMove(World world,EntityPlayer ep,ContainerEquipment container){
		prepareUse(world, ep, container, 0.5F);
		if(WorldHelper.isServer(ep.getEntityWorld())){
			ep.addPotionEffect(new PotionEffect(MobEffects.SPEED,calcPotioinTime(ep, SkillPanels.SMART_MOVE),0));
			ep.addPotionEffect(new PotionEffect(UnsagaPotions.SILENT_MOVE,calcPotioinTime(ep, SkillPanels.SMART_MOVE),0));
		}

	}
	private static int calcPotioinTime(EntityPlayer ep,ISkillPanel panel){
		int level = SkillPanelAPI.getHighestPanelLevel(ep, panel).getAsInt();
		return ItemUtil.getPotionTime(UnsagaConfigHandlerNew.PANEL_GROWTH.mapSkillBaseTime * level);
	}
	public static void onUseRepair(World world,EntityPlayer ep,ContainerEquipment container){
		ItemStack is = ep.getHeldItemMainhand();
		if(GuiEquipment.isItemStackRepairable(is, ep)){
			ep.experienceLevel -= 1;
			container.broadcastSound(XYZPos.createFrom(ep), SoundEvents.BLOCK_ANVIL_USE, ep);
			int repair = -UnsagaConfigHandlerNew.PANEL_GROWTH.repairBaseDamage - 5 * (SkillPanelAPI.getHighestPanelLevel(ep, SkillPanels.QUICK_FIX).getAsInt()-1);
			is.damageItem(repair, ep);
			ChatHandler.sendChatToPlayer(ep, String.format("%s is repaired %d damage.", is.getDisplayName(),-repair));
			if(is.getItemDamage()<0){
				is.setItemDamage(0);
			}

		}
	}

	public static void onUseEavesdrop(World world,EntityPlayer ep,ContainerEquipment container){
		prepareUse(world, ep, container, 0.5F);
		RangedEffect.builder()
		.owner(ep)
		.entityBoundingBox(ep,30)
		.consumer((self,in)->{
			in.addPotionEffect(new PotionEffect(MobEffects.GLOWING,ItemUtil.getPotionTime(5),0));
		}).build(ep.getEntityWorld()).invoke();
//		RangedHelper.create(ep.getEntityWorld(), ep, 30).setSelector((self,in) -> in!=ep)
//		.setConsumer((self,in)->{
//			in.addPotionEffect(new PotionEffect(MobEffects.GLOWING,ItemUtil.getPotionTime(5),0));
//		}).invoke();
	}

	public static void onUseObstacleCrossing(World world,EntityPlayer ep,ContainerEquipment container){
		prepareUse(world, ep, container, 0.5F);
		if(WorldHelper.isServer(ep.getEntityWorld())){
			ep.addPotionEffect(new PotionEffect(MobEffects.JUMP_BOOST,ItemUtil.getPotionTime(10),SkillPanelAPI.getHighestPanelLevel(ep, SkillPanels.OBSTACLE_CROSSING).getAsInt()));
		}

	}
}
