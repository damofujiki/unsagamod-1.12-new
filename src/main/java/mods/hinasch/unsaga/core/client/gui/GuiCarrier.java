package mods.hinasch.unsaga.core.client.gui;

import mods.hinasch.lib.client.GuiContainerBase;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.inventory.container.ContainerCarrier;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

public class GuiCarrier extends GuiContainerBase{


	public GuiCarrier(IMerchant npcMerchant, World world, EntityPlayer player) {
		super(new ContainerCarrier(world, player, npcMerchant));
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public String getGuiName(){
		return "Carrier";
	}

	@Override
	public String getGuiTextureName(){
		return UnsagaMod.MODID+":textures/gui/container/carrier.png";
	}
}
