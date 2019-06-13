package mods.hinasch.unsaga.core.client.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;

import joptsimple.internal.Strings;
import mods.hinasch.lib.client.GuiContainerBase;
import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.misc.XY;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.world.EnvironmentalManager;
import mods.hinasch.lib.world.EnvironmentalManager.EnvironmentalCondition;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.common.IconSkillAssociated;
import mods.hinasch.unsaga.core.advancement.UnsagaTriggers;
import mods.hinasch.unsaga.core.inventory.container.ContainerEquipment;
import mods.hinasch.unsaga.init.UnsagaGui;
import mods.hinasch.unsaga.skillpanel.ISkillPanel;
import mods.hinasch.unsaga.skillpanel.MapAbilityHelper;
import mods.hinasch.unsaga.skillpanel.SkillPanels;
import mods.hinasch.unsaga.status.UnsagaXPCapability;
import mods.hinasch.unsaga.util.HealTimerCalculator;
import mods.hinasch.unsaga.util.UnsagaTextFormatting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class GuiEquipment extends GuiContainerBase{

	public static enum IconType{

		BUTTON_SKILLPANEL(1){
			@Override
			public void useSkill(EntityPlayer player,XYZPos p,ContainerEquipment callback){
				player.closeScreen();
				HSLibs.openGui(player, UnsagaMod.instance, UnsagaGui.Type.SKILLPANEL.getMeta(), player.world, p);
			}
		},
		ROAD_ADVISER(20,SkillPanels.GUIDE_ROAD){
			@Override
			public void useSkill(EntityPlayer player,XYZPos p,ContainerEquipment callback){
				HSLibs.openGui(player, UnsagaMod.instance, UnsagaGui.Type.MAP_FIELD.getMeta(), player.world, p);
			}

		},CAVERN_EXPLORER(21,SkillPanels.GUIDE_CAVE){
			@Override
			public void useSkill(EntityPlayer player,XYZPos p,ContainerEquipment callback){
				HSLibs.openGui(player, UnsagaMod.instance, UnsagaGui.Type.MAP_DUNGEON.getMeta(), player.world, p);
			}
		},BLEND_SPELL(25,SkillPanels.MAGIC_BLEND){
			@Override
			public void useSkill(EntityPlayer player,XYZPos p,ContainerEquipment callback){
				HSLibs.openGui(player, UnsagaMod.instance, UnsagaGui.Type.BLENDING.getMeta(), player.world, p);
			}
		},
		QUICK_FIX(22,SkillPanels.QUICK_FIX){
			@Override
			public void useSkill(EntityPlayer ep,XYZPos p,ContainerEquipment callback){
				MapAbilityHelper.onUseRepair(ep.world, ep, callback);
			}
			@Override
			public void prePacket(GuiEquipment self){
				if(GuiEquipment.isItemStackRepairable(self.player.getHeldItemMainhand(), self.player)){
					self.player.experienceLevel -= 1;
				}
			}
		}
		,WEAPON_CUSTOMIZE(23,SkillPanels.TOOL_CUSTOMIZE){
			@Override
			public void useSkill(EntityPlayer player,XYZPos p,ContainerEquipment callback){
				HSLibs.openGui(player, UnsagaMod.instance, UnsagaGui.Type.SMITH_MINSAGA.getMeta(), player.world, p);
			}
		},EAVESDROP(24,SkillPanels.EAVESDROP){
			@Override
			public void useSkill(EntityPlayer ep,XYZPos p,ContainerEquipment callback){
				MapAbilityHelper.onUseEavesdrop(ep.world, ep, callback);
			}
		},SPEED(27,SkillPanels.SMART_MOVE){
			@Override
			public void useSkill(EntityPlayer ep,XYZPos p,ContainerEquipment callback){
				MapAbilityHelper.onUseSmartMove(ep.world, ep, callback);
			}
		},JUMP(26,SkillPanels.OBSTACLE_CROSSING){
			@Override
			public void useSkill(EntityPlayer ep,XYZPos p,ContainerEquipment callback){
				MapAbilityHelper.onUseObstacleCrossing(ep.world, ep, callback);
			}
		},SWIMMING(28,SkillPanels.SWIMMING){
			@Override
			public void useSkill(EntityPlayer ep,XYZPos p,ContainerEquipment callback){
				MapAbilityHelper.onUseSwimming(ep.world, ep, callback);
			}
		}
		,CONDITION(50),SKILL_POINT(51);

		public static @Nullable IconType fromMeta(int meta){
			return all().stream()
					.filter(in ->in.meta==meta)
					.findFirst()
					.orElse(null);
		}

		public static Collection<IconType> all(){
			return EnumSet.allOf(IconType.class);
		}

		int meta;
		ISkillPanel skill;
		private IconType(int meta){
			this(meta,SkillPanels.DUMMY);
		}

		private IconType(int meta,ISkillPanel p){
			this.meta = meta;
			this.skill = p;
		}
		public void useSkill(EntityPlayer player,XYZPos p,ContainerEquipment callback){

		}
		public void onCotainerCatchPacket(EntityPlayer player,XYZPos p,ContainerEquipment callback){
			if(this.isSkillIcon() && WorldHelper.isServer(player.world)){
				if(WorldHelper.isServer(player.world)){
					UnsagaTriggers.USE_MAP_SKILL.trigger((EntityPlayerMP) player);
				}
				this.useSkill(player, p, callback);
			}
		}
		@SideOnly(Side.CLIENT)
		public void prePacket(GuiEquipment self){

		}
		public List<String> getHoverTexts(GuiEquipment self,boolean isDisabled){
			List<String> list = new ArrayList<>();
			if(this.isSkillIcon()){
				list.add(this.asSkillPanel().getLocalized());
			}
			switch(this){
			case CONDITION:
				EnvironmentalCondition status = self.getCondition();
				list.add(HSLibs.translateKey("gui.unsaga.status."+status.getType().getName()));
				list.add("Heal Interval:"+HealTimerCalculator.calcHealTimer(self.player));
				if(HSLib.configHandler.isDebug()){
					list.add("Temp:"+status.getTemp());
					list.add("Humid:"+status.getHumid());
				}
				break;
			case QUICK_FIX:
				list.add(UnsagaTextFormatting.SIGNIFICANT+HSLibs.translateKey("gui.unsaga.status.quickFix.info1", 10));
				list.add(UnsagaTextFormatting.SIGNIFICANT+HSLibs.translateKey("gui.unsaga.status.quickFix.info2", 1));
				break;
			case EAVESDROP:
				list.add(UnsagaTextFormatting.SIGNIFICANT+HSLibs.translateKey("gui.unsaga.status.eavesdrop.info"));
				break;
			case SKILL_POINT:
				list.add(HSLibs.translateKey("gui.unsaga.status.skillPoint"));
				break;
			default:
				break;
			}
			if(this.isSkillIcon() && isDisabled){
				list.add(UnsagaTextFormatting.PROPERTY_LOCKED+HSLibs.translateKey("gui.unsaga.skill.disabled"));
			}
			return ImmutableList.of();
		}

		public boolean isSkillIcon(){
			return !this.asSkillPanel().isEmpty();
		}
		public Icon createIcon(){
			int x = this.position().getX();
			int y = this.position().getY();
			int ux = this.uv().getX();
			int uy = this.uv().getY();
			if(this.isSkillIcon()){
				return new ButtonMapSkill(this.meta,x,y,ux,uy,true,this.asSkillPanel());
			}
			switch(this){
			case CONDITION:
				return new IconCondition(this.meta,x,y,ux,uy,true);
			case SKILL_POINT:
				return new Icon(this.meta,x,y,ux,uy,true);
			default:
				break;

			}
			return null;
		}
		public ISkillPanel asSkillPanel(){
			return this.skill;
		}

		public XY uv(){
			switch(this){
			case BLEND_SPELL:
				return XY.of(16*2,200);
			case CAVERN_EXPLORER:
				return XY.of(16*1,200);
			case EAVESDROP:
				return XY.of(16*5,200);
			case JUMP:
				return XY.of(16*3,200);
			case QUICK_FIX:
				return XY.of(16*2,200);
			case SPEED:
				return XY.of(16*6,200);
			case SWIMMING:
				return XY.of(16*8,200);
			case WEAPON_CUSTOMIZE:
				return XY.of(16*4,200);
			case SKILL_POINT:
				return XY.of(16,184);
			case CONDITION:
				return XY.of(0, 168);
			default:
				break;

			}
			return XY.of(0, 0);
		}
		public XY position(){
			int sx = -30;
			int sy = 8;
			switch(this){
			case BLEND_SPELL:
				return XY.of(sx,sy+16*2);
			case CAVERN_EXPLORER:
				return XY.of(sx,sy+16*1);
			case EAVESDROP:
				return XY.of(sx,sy+16*5);
			case JUMP:
				return XY.of(sx,sy+16*7);
			case QUICK_FIX:
				return XY.of(sx,sy+16*3);
			case SPEED:
				return XY.of(sx,sy+16*6);
			case SWIMMING:
				return XY.of(sx,sy+16*8);
			case WEAPON_CUSTOMIZE:
				return XY.of(sx,sy+16*4);
			case SKILL_POINT:
				return XY.of(140,58);
			case CONDITION:
				return XY.of(0,8);
			default:
				break;

			}
			return XY.of(0, 0);
		}
	}

	protected final EntityPlayer player;
	protected final World world;
	boolean hasSync = false;
	//	boolean hasUnlockedDechiphering = false;
//
//	public static final int BUTTON_OPEN_SKILLS = 1;
//	public static final int BUTTON_OPEN_BLEND = 2;
//	public static final int BUTTON_USE_MAPSKILL = 3;
//	public static final int DECIPHERING_POINT = 10;
//	public static final int SKILL_POINT = 11;
//	public static final int ROAD_ADVISER = 20;
//	public static final int CAVERN_EXPLORER = 21;
//	public static final int EASY_REPAIR = 22;
//	public static final int WEAPON_FORGE = 23;
//	public static final int EAVESDROP = 24;
//	public static final int SPELL_BLEND = 25;
//	public static final int JUMP = 26;
//	public static final int SPEED = 27;
//	public static final int SWIMMING = 28;

	public GuiEquipment(EntityPlayer player) {
		super(new ContainerEquipment(player.inventory,player));
		this.player = player;
		this.world = player.getEntityWorld();
		// TODO 自動生成されたコンストラクター・スタブ
	}


	@Override
	public void initGui()
	{
		super.initGui();
		int i = width  - xSize >> 1;
		int j = height - ySize >> 1;



		// ボタンを追加
		// GuiButton(ボタンID, ボタンの始点X, ボタンの始点Y, ボタンの幅, ボタンの高さ, ボタンに表示する文字列)
		//buttonList.add(new GuiButton(-2, i + (18*4)+2, j + 16 +(18*2), 30, 20 , "Forging"));
		//this.addButton(BUTTON_OPEN_BLEND,  i + (18*4)+2, j + 16 +(18*0), 30, 20 ,"Blend");
		this.addButton(IconType.BUTTON_SKILLPANEL.meta,  i + (18*5)+20, j + 8 +(18*0), 50, 20 ,"Skill Panels");

		//		//		SkillPanelRegistry reg = SkillPanelRegistry.instance();
		//		this.addIcon(new IconCondition(0,8,49,0,168,true));
		//		int startX = -30;
		//		int startY = 8;
		//		this.addIcon(new ButtonMapSkill(ROAD_ADVISER,startX,startY,0,200,true,SkillPanels.GUIDE_ROAD));
		//		this.addIcon(new ButtonMapSkill(CAVERN_EXPLORER,startX,startY+16*1,16*1,200,true,SkillPanels.GUIDE_CAVE));
		//		this.addIcon(new ButtonMapSkill(SPELL_BLEND,startX,startY+16*2,16*2,200,true,SkillPanels.MAGIC_BLEND));
		//		this.addIcon(new ButtonMapSkill(EASY_REPAIR,startX,startY+16*3,16*3,200,true,SkillPanels.QUICK_FIX));
		//		this.addIcon(new ButtonMapSkill(WEAPON_FORGE,startX,startY+16*4,16*4,200,true,SkillPanels.TOOL_CUSTOMIZE));
		//		this.addIcon(new ButtonMapSkill(EAVESDROP,startX,startY+16*5,16*5,200,true,SkillPanels.EAVESDROP));
		//		this.addIcon(new ButtonMapSkill(SPEED,startX,startY+16*6,16*6,200,true,SkillPanels.SMART_MOVE));
		//		this.addIcon(new ButtonMapSkill(JUMP,startX,startY+16*7,16*7,200,true,SkillPanels.OBSTACLE_CROSSING));
		//		this.addIcon(new ButtonMapSkill(SWIMMING,startX,startY+16*8,16*8,200,true,SkillPanels.SWIMMING));
		//		//		this.addIcon(DECIPHERING_POINT, 140, 38, 0, 184, true);
		//		this.addIcon(SKILL_POINT, 140, 58, 16, 184, true);

		IconType.all()
		.forEach(in ->Optional.of(in.createIcon())
				.ifPresent(icon ->this.addIcon(icon)));
	}



	public String getGuiTextureName(){
		return UnsagaMod.MODID+":textures/gui/container/equipment.png";
	}
	public String getGuiName(){
		return "";
	}

	public static boolean isItemStackRepairable(ItemStack is,EntityPlayer ep){
		if(ItemUtil.isItemStackPresent(is) && is.isItemStackDamageable()){
			return ep.experienceLevel >= 1;
		}
		return false;
	}

	/** 今未使用？*/
	@Override
	protected void drawGuiContainerForegroundLayer(int par1,int par2)
	{
		super.drawGuiContainerForegroundLayer(par1, par2);
		XYZPos pos = XYZPos.createFrom(this.player);

		//		if(SkillPanels.hasPanel(world, this.player, UnsagaMod.skillPanels.roadAdviser)){
		//			fontRendererObj.drawString("Spawn Point:"+this.player.getBedLocation(world.provider.getDimension()),8,165,0xffffff);
		//		}

		//		fontRendererObj.drawString(HSLibs.translateKey("condition.environment")+":"+this.getBiomeEnv(world,pos),8,72,0x404040);


	}

	/** 解読ポイントとスキルXPの描画*/
	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks)
	{
		super.drawScreen(mouseX, mouseY, partialTicks);
		UnsagaXPCapability.displayAdditionalXP(player, fontRenderer,
				EnumSet.of(UnsagaXPCapability.Type.DECIPHER,UnsagaXPCapability.Type.SKILL),
				this.getWindowStartX() + 160,this.getWindowStartY() + 46,0);
		//    	if(HSLib.configHandler.isDebug()){
		//    		this.drawHoveringText(Lists.newArrayList(String.valueOf(mouseX - this.getWindowsStartX()),String.valueOf(mouseY - this.getWindowsStartY())), mouseX, mouseY);
		//    	}


	}
	protected String getBiomeEnv(World world,XYZPos pos){

		EnvironmentalCondition status = EnvironmentalManager.getCondition(world, pos, world.getBiome(pos),player);
		String debug = Strings.EMPTY;
		if(HSLib.configHandler.isDebug()){
			debug = "%s/HealTime:%d/Temp:%.2f/Humid:%.2f";
			debug = String.format(debug, status.getType().getName(),HealTimerCalculator.calcHealTimer(player),status.getTemp(),status.getHumid());
			//			debug = "/HealTimer:"+HealTimerCalculator.calcHealTimer(player)+"/Temp:"+status.getTemp()+"/Humid:"+status.getHumid();
		}
		return HSLibs.translateKey(status.getType().getName())+debug;

	}

	@Override
	public List<String> getIconHoverText(GuiContainerBase.Icon icon){
		boolean isDisabled = Optional.of(icon).filter(in ->in instanceof ButtonMapSkill)
				.map(in ->(ButtonMapSkill)in)
				.map(in -> in.isDisabled(this)).orElse(false);

		return Optional.ofNullable(IconType.fromMeta(icon.id))
				.map(in ->in.getHoverTexts(this, isDisabled))
				.orElse(ImmutableList.of());
		//		if(icon instanceof IconCondition){
		//			EnvironmentalCondition status = this.getCondition();
		//			list.add(HSLibs.translateKey("gui.unsaga.status."+status.getType().getName()));
		//			list.add("Heal Interval:"+HealTimerCalculator.calcHealTimer(player));
		//			if(HSLib.configHandler.isDebug()){
		//				list.add("Temp:"+status.getTemp());
		//				list.add("Humid:"+status.getHumid());
		//
		//			}
		//		}
		//		if(icon instanceof ButtonMapSkill){
		//			list.add(((ButtonMapSkill) icon).getAssociatedSkill().getLocalized());
		//
		//			if(icon.id==EASY_REPAIR){
		//				list.add(UnsagaTextFormatting.SIGNIFICANT+HSLibs.translateKey("gui.unsaga.status.quickFix.info1", 10));
		//				list.add(UnsagaTextFormatting.SIGNIFICANT+HSLibs.translateKey("gui.unsaga.status.quickFix.info2", 1));
		//			}
		//			if(icon.id==EAVESDROP){
		//				list.add(UnsagaTextFormatting.SIGNIFICANT+HSLibs.translateKey("gui.unsaga.status.eavesdrop.info"));
		//			}
		//
		//			if(((ButtonMapSkill)icon).isDisabled(this)){
		//				list.add(UnsagaTextFormatting.PROPERTY_LOCKED+HSLibs.translateKey("gui.unsaga.skill.disabled"));
		//			}
		//		}
		//		if(icon.id==DECIPHERING_POINT){
		//			list.add(HSLibs.translateKey("gui.unsaga.status.decipheringPoint"));
		//			if(!UnsagaUnlockableContentCapability.adapter.getCapability(player).hasUnlockedDeciphering()){
		//				list.add(UnsagaTextFormatting.SIGNIFICANT+"Get Magic Tablet To Unlock This");
		//			}
		//		}
		//		if(icon.id==SKILL_POINT){
		//			list.add(HSLibs.translateKey("gui.unsaga.status.skillPoint"));
		//		}
		//		return list;
	}

	//	@Override
	//	public void onPacketFromServer(NBTTagCompound message){
	//		this.hasUnlockedDechiphering = message.getBoolean("isUnlockDecipher");
	////		super.onPacketFromServer(message);
	//	}
	public EnvironmentalCondition getCondition(){
		XYZPos pos = XYZPos.createFrom(player);
		EnvironmentalCondition status = EnvironmentalManager.getCondition(world, pos, world.getBiome(pos),player);
		return status;
	}

	@Override
	public void prePacket(Icon icon){
		Optional.ofNullable(IconType.fromMeta(icon.id))
		.ifPresent(in ->in.prePacket(this));
	}
	/**
	 *
	 * コンディションアイコン
	 *
	 */
	public static class IconCondition extends GuiContainerBase.Icon{

		public IconCondition(int id,int x, int y, int u, int v, boolean hover) {
			super(id,x, y, u, v, hover);
			// TODO 自動生成されたコンストラクター・スタブ
		}

		@Override
		public XY getUV(GuiContainerBase gui){
			if(gui instanceof GuiEquipment){
				GuiEquipment eqGui = (GuiEquipment) gui;
				EnvironmentalCondition status = eqGui.getCondition();
				//				UnsagaMod.logger.trace("tes", status.getType());
				return new XY(u+16*status.getType().getIconNumber(),v);
			}
			return new XY(0,168);
		}
	}

	public static class ButtonMapSkill extends IconSkillAssociated<GuiEquipment>{

		public ButtonMapSkill(int id, int x, int y, int u, int v, boolean hover, ISkillPanel skill) {
			super(id, x, y, u, v, hover, skill);
			// TODO 自動生成されたコンストラクター・スタブ
		}

		@Override
		public EntityPlayer getPlayer(GuiEquipment gui) {
			// TODO 自動生成されたメソッド・スタブ
			return gui.player;
		}

	}


}
