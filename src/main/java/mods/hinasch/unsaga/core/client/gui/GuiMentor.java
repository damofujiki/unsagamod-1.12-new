package mods.hinasch.unsaga.core.client.gui;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;

import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.client.GuiContainerBase;
import mods.hinasch.lib.misc.XY;
import mods.hinasch.lib.registry.RegistryUtil;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.inventory.container.ContainerMentor;
import mods.hinasch.unsaga.core.world.UnsagaWorldCapability;
import mods.hinasch.unsaga.init.UnsagaRegistries;
import mods.hinasch.unsaga.minsaga.classes.IPlayerClass;
import mods.hinasch.unsaga.minsaga.classes.PlayerClasses;
import mods.hinasch.unsaga.skillpanel.SkillPanelAPI;
import mods.hinasch.unsaga.util.UnsagaTextFormatting;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class GuiMentor extends GuiContainerBase{

	public static final int BUTTON_ACCEPT  = 100;

	IPlayerClass selected = PlayerClasses.NO_CLASS;

	public GuiMentor(IMerchant npcMerchant, World world, EntityPlayer player) {
		super(new ContainerMentor(world, player, npcMerchant));

		this.selected = UnsagaWorldCapability.playerClassStorage(world).getClass(player);
	}
	@Override
	public String getGuiName(){
		return "";
	}

	@Override
	public void initGui(){
		super.initGui();


		int index = 0;
		int offsetX = 0;
		int offsetY = 0;
		for(IPlayerClass clazz:RegistryUtil.getSortedValues(UnsagaRegistries.playerClass(), in -> in!=PlayerClasses.NO_CLASS)){
			this.addIcon(new IconClass(index, 8+offsetX, 8+offsetY, 64, 168, clazz));
			index ++;
			offsetX += 16;
			if(index>8){
				offsetX = 0;
				offsetY += 16;
			}
		}
		this.addIcon(new IconButton(BUTTON_ACCEPT,26,60,48,168,true));
	}

	List<String> hoverText = Lists.newArrayList();
	@Override
	public List<String> getIconHoverText(Icon icon){
		hoverText.clear();
		if(icon.id==BUTTON_ACCEPT){
			hoverText.add(HSLibs.translateKey("gui.mentor.accept"));
			hoverText.add(UnsagaTextFormatting.SIGNIFICANT+HSLibs.translateKey("gui.mentor.accept.desc"));
		}
		if(icon instanceof IconClass){
			IconClass icc = (IconClass)icon;
			hoverText.add(UnsagaTextFormatting.PROPERTY+icc.getPlayerClass().getLocalized());
			hoverText.add(HSLibs.translateKey(icc.getPlayerClass().getUnlocalizedName()+".desc"));
			if(!icc.getPlayerClass().requiredSkills().isEmpty()){
				if(this.isShiftKeyDown()){
					hoverText.add(UnsagaTextFormatting.SIGNIFICANT+icc.getPlayerClass().requiredSkills().stream().map(in -> in.getLocalized()).collect(Collectors.joining("/")));

				}else{
					hoverText.add(UnsagaTextFormatting.SIGNIFICANT+"Push [Shift] To Display Require Skills");
				}
			}

			if(icc.isDisabled(this)){
				hoverText.add(UnsagaTextFormatting.PROPERTY_LOCKED+HSLibs.translateKey("gui.mentor.disabled"));
			}else{
				hoverText.add(UnsagaTextFormatting.PROPERTY+HSLibs.translateKey("gui.mentor.enabled"));
			}
		}
		return hoverText;
	}

	@Override
	public NBTTagCompound getSendingArgs(){
		NBTTagCompound comp = UtilNBT.compound();
		comp.setString("selected", this.selected.getRegistryName().toString());
		return comp;
	}
	@Override
	public String getGuiTextureName(){
		return UnsagaMod.MODID + ":textures/gui/container/mentor.png";
	}

	@Override
	public void prePacket(Icon icon){
		this.selected = Optional.ofNullable(icon).filter(in -> in instanceof IconClass)
				.map(in -> (IconClass)in).map(in -> in.getPlayerClass()).orElse(this.selected);;
	}

	/** クラス＜＞アイコン*/
	public static class IconClass extends GuiContainerBase.IconButtonDisableable<GuiMentor>{

		IPlayerClass clazz;
		public IconClass(int id, int x, int y, int u, int v, IPlayerClass clazz) {
			super(id, x, y, u, v, true);
			this.clazz = clazz;
			// TODO 自動生成されたコンストラクター・スタブ
		}
		@Override
		public XY getUV(GuiMentor gui){
			if(gui.selected.equals(clazz)){
				return new XY(u+16,v);
			}
			return super.getUV(gui);
		}
		public IPlayerClass getPlayerClass(){
			return this.clazz;
		}
		@Override
		public boolean isDisabled(GuiMentor gui){
			if(this.clazz.equals(PlayerClasses.NO_CLASS)){
				return false;
			}
			if(this.clazz.requiredSkills().stream().allMatch(in -> SkillPanelAPI.hasPanel(ClientHelper.getPlayer(), in,true))){
				return false;
			}
			return true;
		}
	}
}
