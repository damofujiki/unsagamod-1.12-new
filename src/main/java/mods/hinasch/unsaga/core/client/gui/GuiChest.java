package mods.hinasch.unsaga.core.client.gui;

import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;

import joptsimple.internal.Strings;
import mods.hinasch.lib.client.GuiContainerBase;
import mods.hinasch.lib.misc.XY;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.chest.ChestHelper;
import mods.hinasch.unsaga.chest.ChestHelper.BaseDifficulty;
import mods.hinasch.unsaga.chest.ChestTrap;
import mods.hinasch.unsaga.chest.ChestTraps;
import mods.hinasch.unsaga.chest.IChestBehavior;
import mods.hinasch.unsaga.chest.IChestCapability;
import mods.hinasch.unsaga.common.IconSkillAssociated;
import mods.hinasch.unsaga.core.inventory.container.ContainerChestUnsaga;
import mods.hinasch.unsaga.skillpanel.ISkillPanel;
import mods.hinasch.unsaga.skillpanel.SkillPanelAPI;
import mods.hinasch.unsaga.skillpanel.SkillPanels;
import mods.hinasch.unsaga.util.UnsagaTextFormatting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class GuiChest extends GuiContainerBase{


	//protected ResourceLocation guiPanel = new ResourceLocation(Unsaga.DOMAIN+":textures/gui/box.png");

	public static enum IconType{
		OPEN(1,"open"),UNLOCK(2,"unlock"),DEFUSE(3,"defuse")
		,DIVINATION(4,"divine")
		,PENETRATION(5,"penetrate")
		,UNKNOWN_CHEST(10,"unknown"),NEEDLE(20,"trap.needle")
		,POISON(21,"trap.poison"),EXPLODE(22,"trap.explode"),SLIME(23,"trap.slime");

		private int meta;
		private String name;
		private IconType(int meta,String name){
			this.meta = meta;
			this.name = name;
		}

		public boolean isViewHover(){
			return true;
		}

		public XY uv(){

			switch(this){
			case DEFUSE:
				return XY.of(16,168);
			case DIVINATION:
				return XY.of(48,168);
			case EXPLODE:
				return XY.of(32, 184);
			case NEEDLE:
				return XY.of(0, 184);
			case OPEN:
				return XY.of(0, 168);
			case PENETRATION:
				return XY.of(32,168);
			case POISON:
				return XY.of(16, 184);
			case SLIME:
				return XY.of(48, 184);
			case UNKNOWN_CHEST:
				return XY.of(64, 184);
			case UNLOCK:
				return XY.of(64,168);
			default:
				break;


			}
			return XY.of(0, 0);
		}

		public XY iconPosition(){
			int skillY = 80;
			int trapY = 40;
			int fix = 8;
			switch(this){
			case DEFUSE:
				return XY.of(24*1+fix, skillY);
			case DIVINATION:
				return XY.of(24*3+fix, skillY);
			case EXPLODE:
				return XY.of(84+16*2,trapY);
			case NEEDLE:
				return XY.of(84,trapY);
			case OPEN:
				return XY.of(8, skillY);
			case PENETRATION:
				return XY.of(24*2+fix, skillY);
			case POISON:
				return XY.of(84+16*1,trapY);
			case SLIME:
				return XY.of(84+16*3,trapY);
			case UNKNOWN_CHEST:
				return XY.of(84, 40);
			case UNLOCK:
				return XY.of(24*4+fix, skillY);
			default:
				break;


			}
			return XY.of(0, 0);
		}

		public ChestTrap asTrap(){
			switch(this){
			case EXPLODE:
				return ChestTraps.EXPLODE;
			case NEEDLE:
				return ChestTraps.NEEDLE;
			case POISON:
				return ChestTraps.POISON;
			case SLIME:
				return ChestTraps.SLIME;
			default:
				break;

			}
			return ChestTraps.DUMMY;
		}
		public ISkillPanel asSkillPanel(){
			switch(this){
			case DEFUSE:
				return SkillPanels.DEFUSE;
			case DIVINATION:
				return SkillPanels.FORTUNE;
			case PENETRATION:
				return SkillPanels.SHARP_EYE;
			case UNLOCK:
				return SkillPanels.LOCKSMITH;
			default:
				break;

			}
			return SkillPanels.DUMMY;
		}
		public int meta(){
			return this.meta;
		}

		public String hoverTextUnlocalized(){
			return "gui.unsaga.chest."+this.name;
		}

		public Icon createIcon(){
			XY pos = this.iconPosition();
			XY uv = this.uv();
			switch(this){
			case DEFUSE:
			case DIVINATION:
			case PENETRATION:
			case UNLOCK:
				return new IconSkill(this.meta(),pos.getX(),pos.getY(),uv.getX(),uv.getY(),this.isViewHover(),this.asSkillPanel());
			case EXPLODE:
			case NEEDLE:
			case SLIME:
			case POISON:
				return new IconTrap(this.meta(),pos.getX(),pos.getY(),uv.getX(),uv.getY(),this.isViewHover(),this.asTrap());
			case OPEN:
				return new IconButton(this.meta(),pos.getX(),pos.getY(),uv.getX(),uv.getY(),this.isViewHover());
			case UNKNOWN_CHEST:
				Icon iconUnknownChest = new Icon(this.meta(),pos.getX(),pos.getY(),uv.getX(),uv.getY(),this.isViewHover());
				iconUnknownChest.setVisibleDelegate((self,gui)->{
					if(gui instanceof GuiChest){

						GuiChest guiChest = (GuiChest) gui;
						return !guiChest.theChest.hasAnalyzed() && !guiChest.openPlayer.isCreative();
					}
					return false;
				});
				return iconUnknownChest;
			default:
				break;

			}
			return null;
		}


		public static Collection<IconType> all(){
			return EnumSet.allOf(IconType.class);
		}

		public static IconType fromMeta(int meta){
			return all().stream().filter(in -> in.meta()==meta).findFirst().orElse(null);
		}
	}

	public final EntityPlayer openPlayer;
	public IChestCapability theChest;
	public String message = Strings.EMPTY;

	protected ContainerChestUnsaga container;

	public GuiChest(IChestBehavior chest,EntityPlayer ep) {
		super(new ContainerChestUnsaga(chest,ep));
		this.container = (ContainerChestUnsaga) this.inventorySlots;
		this.theChest = chest.getCapability();
		this.openPlayer = ep;
		// TODO 自動生成されたコンストラクター・スタブ
	}


	@Override
	public void initGui()
	{
		super.initGui();
		int i = width  - xSize >> 1;
		int j = height - ySize >> 1;


		IconType.all()
		.forEach(in ->Optional.ofNullable(in.createIcon())
				.ifPresent(icon ->this.addIcon(icon)));

		//鍵かかっているかどうかも追加する
	}

	public void updateCapability(IChestCapability capa){

		UnsagaMod.logger.trace(this.getClass().getName(), capa.traps());
		this.theChest = capa;
	}
	@Override
	protected void drawGuiContainerForegroundLayer(int par1,int par2)
	{
		super.drawGuiContainerForegroundLayer(par1, par2);

		this.fontRenderer.drawString(HSLibs.translateKey("word.chest")+" LV:"+this.theChest.level(), 84, 20, 0x404040);
		this.fontRenderer.drawString(HSLibs.translateKey(this.message), 16, 140, 0x404040);

		//fontRenderer.drawString("Result:"+getSpellStr(), 8, (ySize - 96) + 2, 0x404040);
	}

	@Override
	public String getGuiTextureName(){
		return UnsagaMod.MODID+":textures/gui/container/chest.png";
	}

	@Override
	public String getGuiName(){
		return "Chest";
	}

	@Override
	public void onGuiClosed(){
		super.onGuiClosed();
		//    	ExtendedPlayerData.getData(openPlayer).setInteractingChest(null);
	}


	@Override
	public List<String> getIconHoverText(Icon id){
		List<String> list = Lists.newArrayList();
		float prob = 0;


		Optional.ofNullable(IconType.fromMeta(id.id))
		.ifPresent(type ->{
			list.add(type.hoverTextUnlocalized());

			if(id instanceof IconSkill){
				IconSkill iconSkill = (IconSkill) id;
				if(iconSkill.isDisabled(this)){
					list.add(UnsagaTextFormatting.PROPERTY_LOCKED+HSLibs.translateKey("gui.unsaga.chest.skill.disabled"));
				}else{
					SkillPanelAPI.getHighestPanelLevel(openPlayer, ((IconSkill) id).getAssociatedSkill())
					.ifPresent(in ->{
						list.add(ChestHelper.getInteractionSuccessProb(((IconSkill) id).getBaseDifficulty(), this.theChest,in)*100+"%");
					});
				}
			}

		});

		return list;
	}

	@Override
	public void onPacketFromServer(NBTTagCompound message){
		String str = message.getString("message");
		this.message = str;

	}

	public static class IconSkill extends IconSkillAssociated<GuiChest>{

		public IconSkill(int id, int x, int y, int u, int v, boolean hover, ISkillPanel skill) {
			super(id, x, y, u, v, hover, skill);
			// TODO 自動生成されたコンストラクター・スタブ
		}

		public BaseDifficulty getBaseDifficulty(){
			return ChestHelper.getBaseDifficulty(this.getAssociatedSkill());
		}

		@Override
		public EntityPlayer getPlayer(GuiChest gui) {
			// TODO 自動生成されたメソッド・スタブ
			return gui.openPlayer;
		}

	}

	public static class IconTrap extends Icon<GuiChest>{

		final ChestTrap trap;
		public IconTrap(int id, int x, int y, int u, int v, boolean hover,ChestTrap trap) {
			super(id, x, y, u, v, hover);
			this.trap = trap;
			// TODO 自動生成されたコンストラクター・スタブ
		}

		public boolean isVisible(GuiChest gui){
			if(gui instanceof GuiChest){

				IChestCapability capa = gui.theChest;
				if(capa.hasAnalyzed() || gui.openPlayer.isCreative())
				return capa.traps().contains(trap);
			}
			return false;
		}


	}
}
