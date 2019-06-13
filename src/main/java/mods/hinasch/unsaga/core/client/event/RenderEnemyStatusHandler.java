package mods.hinasch.unsaga.core.client.event;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;

import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.client.RenderHelperHS;
import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.item.PotionUtil;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import mods.hinasch.unsaga.core.potion.state.StateCombo;
import mods.hinasch.unsaga.core.potion.state.StateCombo.Effect;
import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew;
import mods.hinasch.unsaga.lp.LifePoint;
import mods.hinasch.unsaga.lp.LifePoint.ILifePoint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderLivingEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class RenderEnemyStatusHandler extends Gui{


	public static final int INTERVAL = 20;


	public RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

	List<StatusRenderer> statusRenderers = new ArrayList<>();
	public RenderEnemyStatusHandler(){
		List<StatusRenderer> list = new ArrayList<>();

		list.add(new Target());
		list.add(new Casting());
		list.add(new LPDamage());
		list.add(new Detected());
		list.add(new MobLP());
//		list.add(new Combo());

		this.statusRenderers = ImmutableList.copyOf(list);
	}


	@SubscribeEvent
	public void onRenderLivingPost(RenderLivingEvent.Post e){
		XYZPos pos = new XYZPos(e.getX(),e.getY(),e.getZ());
		FontRenderer fontRenderer = e.getRenderer().getFontRendererFromRenderManager();
		RenderHelperHS renderHelper = RenderHelperHS.create(fontRenderer, renderManager);

		List<StatusRenderer> list = this.statusRenderers.stream().filter(in -> in.test(e)).collect(Collectors.toList());

		if(!list.isEmpty()){
			int size = list.size();
			int par = e.getEntity().ticksExisted %( size * INTERVAL);
			int index = (par+1) / INTERVAL;

			if(list.size()>index){
				list.get(index).render(e, pos, renderHelper);
			}
		}

		//			this.renderShadowLife(e, pos);

	}


	public abstract static class StatusRenderer implements Predicate<RenderLivingEvent.Post>{

		public abstract void render(RenderLivingEvent.Post e,XYZPos pos,RenderHelperHS helper);
	}

	public static class Target extends StatusRenderer{

		@Override
		public boolean test(Post e) {
			if(e.getEntity().getEntityId()==UnsagaMod.proxy.getKeyBindings().getClientTargetSelector().getCurrentIndex()){
				return true;
			}
			return false;
		}

		@Override
		public void render(Post e, XYZPos pos, RenderHelperHS helper) {
			String str = "Target";
			helper.renderStringAt(e.getEntity(), str, pos, 100, 0xffffff);
		}

	}
	public static class Casting extends StatusRenderer{

		@Override
		public boolean test(Post e) {
			return StateCapability.isStateActive(e.getEntity(),UnsagaPotions.CASTING);
		}

		@Override
		public void render(Post e, XYZPos pos, RenderHelperHS helper) {
			int duration = StateCapability.getState(e.getEntity(),UnsagaPotions.CASTING).getDuration();
			String str = "Casting:%d";
			String str2 = String.format(str,duration );
			helper.renderStringAt(e.getEntity(), str2, pos, 100, 0xffffff);
		}

	}
	public static class Detected extends StatusRenderer{

		@Override
		public boolean test(Post e) {
			PotionEffect effect = PotionUtil.getEffect(e.getEntity(), UnsagaPotions.DETECTED);
			if(!PotionUtil.isNullEffect(effect)){
				return effect.getDuration()>0;
			}
			return this.canDetectEntityWithSkill(e.getEntity());
		}

		public boolean canDetectEntityWithSkill(EntityLivingBase e){
//
//			if(SkillPanelAPI.hasPanel(ClientHelper.getPlayer(), SkillPanels.WATCHING_OUT)){
//				if(e.getDistance(ClientHelper.getPlayer())<10.0D){
//					return true;
//				}
//			}
			return false;
		}

		@Override
		public void render(Post e, XYZPos pos, RenderHelperHS helper) {
			String str = "Health:%d / %d";
			int hp = (int)e.getEntity().getHealth();
			int maxhp = (int)e.getEntity().getMaxHealth();
			String str2 = String.format(str, hp,maxhp);
			//Unsaga.debug(this.getClass(),"kiteru");
			helper.renderStringAt(e.getEntity(), str2, pos, 100, 0xffffff);

		}

	}
	public static class LPDamage extends StatusRenderer{

		@Override
		public boolean test(Post e) {
			EntityLivingBase living = e.getEntity();
			ILifePoint lpCapability = LifePoint.adapter.getCapability(living);
			if(LifePoint.adapter.hasCapability(living) && UnsagaConfigHandlerNew.GENERAL.enableLPSystem){
				return lpCapability.isRendering();
			}
			return false;
		}



		@Override
		public void render(Post e, XYZPos pos, RenderHelperHS helper) {
			EntityLivingBase living = e.getEntity();
			ILifePoint lpCapability = LifePoint.adapter.getCapability(living);
			if(lpCapability.renderPosition()!=null){
				lpCapability.setRenderPosition(pos);
			}
			XYZPos p = lpCapability.renderPosition();
			lpCapability.decrRenderTicks(1);
			String str = "-"+String.valueOf(lpCapability.getRenderingDamage())+" LP";
			helper.renderStringAt(living, str, pos, 100, 0xff0000);
			if(lpCapability.renderTicks()<0){
				lpCapability.resetRenderTicks();
			}
		}

	}
	public static class MobLP extends StatusRenderer{
		@Override
		public boolean test(Post e) {
			EntityLivingBase living = e.getEntity();
			ILifePoint lpCapability = LifePoint.adapter.getCapability(living);
			boolean isNearPlayer = ClientHelper.getPlayer().getDistance(e.getEntity())<8.0D;

			if(LifePoint.adapter.hasCapability(living) && UnsagaConfigHandlerNew.GENERAL.enableLPSystem){
				if(HSLibs.isSameTeam(ClientHelper.getPlayer(), living)){
					return true;
				}
				if((UnsagaConfigHandlerNew.DISPLAY.isRenderNearLP && isNearPlayer)){
					return living != ClientHelper.getPlayer();
				}
			}
			return false;
		}

		@Override
		public void render(Post e, XYZPos pos, RenderHelperHS helper) {
			EntityLivingBase living = e.getEntity();
			ILifePoint lpCapability = LifePoint.adapter.getCapability(living);
			boolean isNearPlayer = ClientHelper.getPlayer().getDistance(e.getEntity())<8.0D;

			String str = String.format("LP:%d / %d", lpCapability.lifePoint(),lpCapability.maxLifePoint());


			helper.renderStringAt(living, str, pos, 100, 0xffffff);


		}


	}
	public static class Combo extends StatusRenderer{

		@Override
		public boolean test(Post ev) {
			// TODO 自動生成されたメソッド・スタブ
			return StateCapability.isStateActive(ev.getEntity(), UnsagaPotions.COMBO);
		}

		@Override
		public void render(Post e, XYZPos pos, RenderHelperHS helper) {
			// TODO 自動生成されたメソッド・スタブ
			StateCombo.Effect effect = (Effect) StateCapability.getState(e.getEntity(), UnsagaPotions.COMBO);
			int count = effect.getAmplifier();
			String str = String.format("%d Hit Combo", count+1);
			helper.renderStringAt(e.getEntity(), str, pos, 100, 0xffffff);
		}

	}
}