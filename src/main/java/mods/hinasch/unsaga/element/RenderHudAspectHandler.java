package mods.hinasch.unsaga.element;


import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.item.misc.ItemElementChecker;
import mods.hinasch.unsaga.core.world.chunk.UnsagaChunkCapability;
import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.Gui;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/** 地相チェッカーの描画部分*/
public class RenderHudAspectHandler extends Gui{


	public static final ResourceLocation ELEMENT_ICONS = new ResourceLocation(UnsagaMod.MODID,"textures/gui/hud_elements.png");

//	UnsagaLibrary lib = UnsagaLibrary.instance();
	@SubscribeEvent
	public void onTextDraw(RenderGameOverlayEvent.Post e){
        int top = 2;
    	int offsetx = UnsagaConfigHandlerNew.DISPLAY.offsetAspect[0];
    	int offsety = UnsagaConfigHandlerNew.DISPLAY.offsetAspect[1];
        if(ClientHelper.getPlayer().ticksExisted % 30 == 0){
        	Chunk chunk = ClientHelper.getWorld().getChunkFromBlockCoords(ClientHelper.getPlayer().getPosition());
        	HSLib.packetDispatcher().sendToServer(PacketSyncCapability.createRequest(UnsagaChunkCapability.CAPA, UnsagaChunkCapability.ADAPTER.getCapability(chunk)));
        }
        if(e.getType()==ElementType.TEXT){
        	ItemStack is = ClientHelper.getPlayer().getHeldItemMainhand();
        	if(is!=null && is.getItem() instanceof ItemElementChecker){
        		RayTraceResult ray = ClientHelper.getMouseOver();
        		if(ray!=null && ray.typeOfHit == RayTraceResult.Type.BLOCK){
        			BlockPos pos = ray.getBlockPos();
        			IBlockState state = ClientHelper.getWorld().getBlockState(pos);
        			ElementTable tableOpt = AspectGetter.getCurrentAspect(ClientHelper.getWorld(), ClientHelper.getPlayer().getPosition());

        			if(state!=null && tableOpt!=null){
        				ElementTable table = tableOpt;
                		ClientHelper.bindTextureToTextureManager(ELEMENT_ICONS);
            			int margin = 3;
                		for(FiveElements.Type type:FiveElements.VALUES){
                			int x =offsetx+ (16+margin)*type.getMeta();
                			this.drawTexturedModalRect(x, offsety, type.getMeta()*16, 0, 16,16);
                		}
                		for(FiveElements.Type type:FiveElements.VALUES){
                			int x =offsetx+ (16+margin)*type.getMeta();
                			float elm = table.get(type);
                			String str = String.valueOf(elm);
                			this.drawString(ClientHelper.fontRenderer(), str, x, offsety + 16 + 2, 0xffffff);
                		}
        			}
        		}


        	}
        }

	}
}
