package mods.hinasch.unsaga.core.client.event;

import io.netty.util.internal.StringUtil;
import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.misc.XY;
import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew;
import mods.hinasch.unsaga.lp.LifePoint;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** プレイヤーGUIまわり*/
	public class RenderUnsagaHudHandler extends Gui{

		public static final XY LP = XY.of(10, 10);
		public static final XY COMBO = XY.of(10, 20);

		public static final Alert NO_MESSAGE = new Alert(StringUtil.EMPTY_STRING,0);
		public static Alert message = NO_MESSAGE;

		protected FontRenderer fontRenderer;



		public RenderUnsagaHudHandler(){

		}

		@SubscribeEvent
		public void onRenderStatus(RenderGameOverlayEvent e){


//			Tessellator tesselater = Tessellator.getInstance();



			if(this.fontRenderer==null){
				this.fontRenderer = ClientHelper.fontRenderer();
			}


			//デバフの描画
			if(e.getType()==RenderGameOverlayEvent.ElementType.TEXT){
//				this.renderPlayerDebuffs();
				this.renderPlayerLP();
				this.renderAlert();
			}

			if(message.hasExpired()){
				message = NO_MESSAGE;
			}else{
				if(ClientHelper.getPlayer().ticksExisted % 10 == 0){
					message.decrTime();
				}
			}


		}

		public void renderPlayerLP(){
//			RangedHelper r = RangedHelper.create(ClientHelper.getWorld(), ClientHelper.getPlayer(), 20);
//			r.setSelector((self,in)->{
//				return ((EntityLivingBase)in).isPotionActive(UnsagaPotions.instance().DETECTED);
//			});
			if(!UnsagaConfigHandlerNew.GENERAL.enableLPSystem){
				return;
			}

			EntityPlayer clientPlayer = ClientHelper.getPlayer();
			int[] offset = UnsagaConfigHandlerNew.DISPLAY.offsetLPDisplay;
			if(UnsagaConfigHandlerNew.GENERAL.enableLPSystem && LifePoint.adapter.hasCapability(clientPlayer)){
				String str = String.format("LP:%d / %d", LifePoint.adapter.getCapability(clientPlayer).lifePoint(),LifePoint.adapter.getCapability(clientPlayer).maxLifePoint());
				fontRenderer.drawString(str, offset[0]+LP.getX(), offset[1]+LP.getY(), 0xffffff);



			}

		}

		public void renderAlert(){
			EntityPlayer clientPlayer = ClientHelper.getPlayer();
			int[] offset = UnsagaConfigHandlerNew.DISPLAY.offsetLPDisplay;
			if(!message.hasExpired()){
				fontRenderer.drawString(message.str, offset[0]+COMBO.getX(), offset[1]+COMBO.getY(), 0xffffff);
			}
		}

		public static void createAlert(String str){
			message = new Alert(str,100);
		}

		public static class Alert{

			public final String str;
			public int time;

			public Alert(String str,int time){
				this.str = str;
				this.time = time;
			}

			public void decrTime(){
				this.time --;
				if(this.time<=0){
					this.time = 0;
				}
			}

			public boolean hasExpired(){
				return this.time <=0;
			}
		}
	}