package mods.hinasch.unsaga.core.client.gui;

import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;

import io.netty.util.internal.StringUtil;
import mods.hinasch.lib.client.GuiContainerBase;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.common.panel_bonus.IPanelJoint;
import mods.hinasch.unsaga.common.panel_bonus.PanelBonusSummary;
import mods.hinasch.unsaga.common.panel_bonus.PanelJointList;
import mods.hinasch.unsaga.core.inventory.container.ContainerSkillPanel;
import mods.hinasch.unsaga.core.item.misc.skillpanel.SkillPanelCapability;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class GuiSkillPanel extends GuiContainerBase{

	public static enum IconType{
		DRAW_PANELS(1),CHANGE_EXP(2),UNDO(3);


		private int meta;

		private IconType(final int meta){
			this.meta = meta;
		}

		public int meta(){
			return this.meta;
		}
		public Icon createIcon(){
			switch(this){
			case CHANGE_EXP:
				return null;
			case DRAW_PANELS:
				return new IconButton(meta(), 8, 64, 16, 168, true);
			case UNDO:
				return new IconButton(meta(), 28, 64, 0, 168, true);
			default:
				break;

			}
			return null;
		}

		public String getHoverText(){
			switch(this){
			case CHANGE_EXP:
				break;
			case DRAW_PANELS:
				return "Consume XP to Draw Panels";
			case UNDO:
				return "Undo";
			default:
				break;

			}
			return StringUtil.EMPTY_STRING;
		}

		public static IconType fromMeta(final int meta){
			return EnumSet.allOf(IconType.class).stream()
					.filter(in ->in.meta()==meta).findFirst().orElse(null);
		}
	}

	protected GuiButton buttonInstance;
	protected EntityPlayer ep;

	public GuiSkillPanel(final EntityPlayer ep) {
		super(new ContainerSkillPanel(ep));
		this.ep = ep;
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public void initGui(){
		super.initGui();
		final int i = width  - xSize >> 1;
		final int j = height - ySize >> 1;
		buttonInstance = this.addButton(IconType.CHANGE_EXP.meta(),  this.getWindowStartX() + 10,this.getWindowStartY() + 20, 30, 20 ,String.valueOf(getExpToConsume())+"XP");
		//		this.addButton(BUTTON_DRAW_PANELS,  i + (18*7)+2, j + 30 +(18*0), 30, 20 ,"draw");
		//		this.addButton(BUTTON_UNDO,  i + (18*1)+2, j + 30 +(18*0), 30, 20 ,"undo");

		this.addIcon(IconType.DRAW_PANELS.createIcon());
		this.addIcon(IconType.UNDO.createIcon());
	}
	@Override
	public String getGuiTextureName(){
		return UnsagaMod.MODID+":textures/gui/container/skillpanel.png";
	}

	public List<String> getIconHoverText(final Icon icon){
		final List<String> list = Lists.newArrayList();
		Optional.ofNullable(IconType.fromMeta(icon.id))
		.ifPresent(in ->list.add(in.getHoverText()));
		return list;
	}

	protected ContainerSkillPanel getContainer(){
		return (ContainerSkillPanel) this.inventorySlots;
	}
	@Override
	protected void drawGuiContainerForegroundLayer(final int par1,final int par2)
	{
		super.drawGuiContainerForegroundLayer(par1, par2);
		final ContainerSkillPanel containerSkill = (ContainerSkillPanel) this.container;
		//		HexMatrixAdapter<ItemStack> matrix = containerSkill.getPanelMatrix();
		//		for(XY xy:matrix.getJointed()){
		//			ItemStack is = matrix.getMatrix(xy);
		//			if(SkillPanelCapability.adapter.hasCapability(is)){
		//				SkillPanelCapability.adapter.getCapability(is).setJointed(true);
		//			}
		//		}
		final PanelJointList<IPanelJoint> layouts = containerSkill.adapter.buildJointList();
		layouts.asList().forEach(joint ->
		joint.hexes().stream()
		.map(in -> containerSkill.adapter.getStackFrom(in))
		.filter(in -> !in.isEmpty())
		.forEach(in -> SkillPanelCapability.adapter.getCapability(in).setJointed(true)));


		//		boolean isLineBonus = false;
		//		if(!matrix.checkLine().isEmpty()){
		////			UnsagaMod.logger.trace("matrix", "line");
		//			isLineBonus = true;
		//		}
		//		float str = HelperSkillPanelBonus.calcAppliedBonus(matrix,HelperSkillPanelBonus.Type.STR,isLineBonus);
		//		float luck = HelperSkillPanelBonus.calcAppliedBonus(matrix,HelperSkillPanelBonus.Type.LUCK,isLineBonus);
		//		float health = HelperSkillPanelBonus.calcAppliedBonus(matrix,HelperSkillPanelBonus.Type.HEALTH,isLineBonus);
		//		float lpstr = HelperSkillPanelBonus.calcAppliedBonus(matrix,HelperSkillPanelBonus.Type.LPSTR,isLineBonus);
		//		float magic = HelperSkillPanelBonus.calcAppliedBonus(matrix,HelperSkillPanelBonus.Type.MAGIC,isLineBonus);
		//		float elements = HelperSkillPanelBonus.calcAppliedBonus(matrix,HelperSkillPanelBonus.Type.ELEMENTS,isLineBonus);


		//		for(XY xy:matrix.getAll()){
		//			ItemStack panel = matrix.getMatrix(xy);
		//			if(panel!=null){
		//				for(ItemStack aroundStack:matrix.getAroundElements(xy.getX(), xy.getY())){
		//					if(aroundStack!=null){
		//						if(skillPanels.getData(panel.getItemDamage())==skillPanels.getData(aroundStack.getItemDamage())){
		//							if(ItemSkillPanel.adapter.hasCapability(panel) && !ItemSkillPanel.adapter.getCapability(panel).isJointed()){
		//								ItemSkillPanel.adapter.getCapability(panel).setJointed(true);
		//							}
		//						}
		//					}
		//
		//				}
		//			}
		//
		//		}
		//		UnsagaMod.logger.trace("bonus", str,luck,health,lpstr,magic);
		//		UnsagaXPCapability.displayAdditionalXP(ep, fontRendererObj, EnumSet.of(Type.SKILL));
		//		String bonusMes = "STR:%.2f HP:%.2f LUCK:%.2f LP.STR:%.2f INT:%.2f ELM:%.2f";
		//		String formatted = String.format(bonusMes, str,health,luck,lpstr,magic,elements);
		//		fontRenderer.drawString("Panel Bonus", -60,0,0xffffff);
		//		fontRenderer.drawString(String.format("STR:%.2f", str), -60,16,0xffffff);
		//		fontRenderer.drawString(String.format("HP:%.2f", health), -60,16*2,0xffffff);
		//		fontRenderer.drawString(String.format("LUCK:%.2f", luck), -60,16*3,0xffffff);
		//		fontRenderer.drawString(String.format("DEX:%.2f", lpstr), -60,16*4,0xffffff);
		//		fontRenderer.drawString(String.format("INT:%.2f", magic), -60,16*5,0xffffff);
		//		fontRenderer.drawString(String.format("ELM:%.2f", elements), -60,16*6,0xffffff);

		final PanelBonusSummary summary = containerSkill.adapter.createSummary();
		final List<String> tips = Lists.newArrayList();
		tips.add("[Panel Bonus]");

		summary.getTotalBonusAmount().addTips(tips);
		for(int i=0;i<tips.size();i++){
			fontRenderer.drawString(tips.get(i), -70, 12*i, 0xffffff);
		}

	}
	public int getExpToConsume(){
		return this.getContainer().getXPToConsume();
	}
	@Override
	public String getGuiName(){
		return "Skill Panel";
	}

	public void setExpToConsume(final int var1){
		this.getContainer().setXPToConsume(var1);
	}
	@Override
	public void prePacket(final GuiButton par1GuiButton){
		if(par1GuiButton.id==IconType.CHANGE_EXP.meta()){
			this.setExpToConsume(this.getExpToConsume() + 5);
			//			Unsaga.debug(this.expToConsume,this.getClass());


			if(this.getExpToConsume()>30){
				this.setExpToConsume(5);
			}
			this.buttonInstance.displayString = String.valueOf(getExpToConsume())+"XP";
		}
	}
	@Override
	public NBTTagCompound getSendingArgs(){
		return UtilNBT.comp().setInteger("exp", this.getExpToConsume()).get();
	}
}
