package mods.hinasch.unsaga;

import mods.hinasch.lib.config.EventConfigChanged;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.core.advancement.AdvancementHandlerUnsaga;
import mods.hinasch.unsaga.core.client.event.EventUnsagaTooltip;
import mods.hinasch.unsaga.core.entity.projectile.custom_arrow.CustomArrowEventHandler;
import mods.hinasch.unsaga.core.event.CancelFallingEventHandler;
import mods.hinasch.unsaga.core.event.DamageEventHandlerUnsaga;
import mods.hinasch.unsaga.core.event.EntityJoinWorldEventHandler;
import mods.hinasch.unsaga.core.event.ItemDamageHandler;
import mods.hinasch.unsaga.core.event.LivingUpdateEventHandler;
import mods.hinasch.unsaga.core.event.MobDropHandlerUnsaga;
import mods.hinasch.unsaga.core.event.ReplaceFoodStatsHandler;
import mods.hinasch.unsaga.core.item.misc.ItemTechBook;
import mods.hinasch.unsaga.status.UnsagaStatus;
import mods.hinasch.unsaga.villager.VillagerInteractionUnsaga;

public class UnsagaModEvents {

	private static UnsagaModEvents INSTANCE;
	public static UnsagaModEvents instance(){
		if(INSTANCE==null){
			INSTANCE = new UnsagaModEvents();
		}
		return INSTANCE;
	}

	public void regiser(){



//		HSLibs.registerEvent(new UnsagaConfigHandlerNew.EventHandler());
		CustomArrowEventHandler.register();
		HSLibs.registerEvent(new DamageEventHandlerUnsaga());
		HSLibs.registerEvent(new LivingUpdateEventHandler());
		HSLibs.registerEvent(new EntityJoinWorldEventHandler());
		HSLibs.registerEvent(new CancelFallingEventHandler());
		HSLibs.registerEvent(new AdvancementHandlerUnsaga());
		HSLibs.registerEvent(new ReplaceFoodStatsHandler());
		HSLibs.registerEvent(new ItemDamageHandler());
//		LPEvents.register();
		UnsagaStatus.register();
//		AdditionalStatusEvents.register();
		(new MobDropHandlerUnsaga()).init();
//		StatePropertySpecialMove.register();
		HSLibs.registerEvent(new ItemTechBook.EventHandler());
		EventConfigChanged.instance().addConfigHandler(UnsagaMod.MODID, UnsagaMod.configs);
//		HSLib.core().events.livingHurt.getEventsMiddle().add(new ILivingHurtEvent(){
//
//			@Override
//			public boolean apply(LivingHurtEvent e, DamageSource dsu) {
//				UnsagaMod.logger.trace(this.getName(), "shsfhhsd1",e.getSource().getSourceOfDamage(),e.getSource().getEntity());
//				return e.getSource().getSourceOfDamage() instanceof EntityArrow;
//			}
//
//			@Override
//			public String getName() {
//				// TODO 自動生成されたメソッド・スタブ
//				return "arrow fix";
//			}
//
//			@Override
//			public DamageSource process(LivingHurtEvent e, DamageSource dsu) {
//				UnsagaMod.logger.trace(this.getName(), "shsfhhsd2");
//				EntityArrow arrow = (EntityArrow) e.getSource().getSourceOfDamage();
//				if(EntityStateCapability.adapter.hasCapability(arrow)){
//					UnsagaMod.logger.trace(this.getName(), "shsfhhsd3");
//					StateArrow state = (StateArrow) EntityStateCapability.adapter.getCapability(arrow).getState(StateRegistry.instance().stateArrow);
//					if(state.isCancelHurtRegistance()){
//						e.getEntityLiving().hurtResistantTime = 0;
//						e.getEntityLiving().hurtTime=0;
//					}
//				}
//				return dsu;
//			}}
//		);
//		HSLibs.registerEvent(new EventOnBed());
//		HSLibs.registerEvent(new EventToolTipUnsaga());
//		HSLibs.registerEvent(new EventAbilityLearning());
//		HSLibs.registerEvent(new EventReplaceFoodStats());
		HSLibs.registerEvent(new VillagerInteractionUnsaga());
//		HSLibs.registerEvent(new EventMartialArtsOnInteract());
//		HSLibs.registerEvent(new WorldSaveDataSkillPanel.SkillPanelSyncEvent());
//		HSLibEvents.livingUpdate.getEvents().add(new LivingUpdateLPRestoreEvent());

		HSLibs.registerEvent(new EventUnsagaTooltip());

	}
}
