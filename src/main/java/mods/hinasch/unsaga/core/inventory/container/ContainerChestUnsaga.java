package mods.hinasch.unsaga.core.inventory.container;

import java.util.Optional;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.ArrayBlockingQueue;

import com.google.common.collect.Lists;

import joptsimple.internal.Strings;
import mods.hinasch.lib.container.ContainerBase;
import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.network.PacketServerToGui;
import mods.hinasch.lib.util.ChatHandler;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.Abilities;
import mods.hinasch.unsaga.chest.ChestHelper;
import mods.hinasch.unsaga.chest.ChestTrap;
import mods.hinasch.unsaga.chest.IChestBehavior;
import mods.hinasch.unsaga.common.item.UnsagaItemFactory;
import mods.hinasch.unsaga.core.advancement.UnsagaTriggers;
import mods.hinasch.unsaga.core.client.gui.GuiChest;
import mods.hinasch.unsaga.core.net.packet.PacketGuiButtonUnsaga;
import mods.hinasch.unsaga.core.net.packet.PacketSyncSkillPanel;
import mods.hinasch.unsaga.init.UnsagaGui;
import mods.hinasch.unsaga.skillpanel.SkillPanelAPI;
import mods.hinasch.unsaga.skillpanel.SkillPanels;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class ContainerChestUnsaga extends ContainerBase{

	public IChestBehavior chestBehavior;
	//	public ChestInteractionAgent chestBehaviorAgent;
	protected EntityPlayer openPlayer;
	protected boolean setClose;

	protected Abilities abilities;
	//protected int id;

	String message = Strings.EMPTY;
	World world;
	protected InventoryBasic dummy;

	UnsagaItemFactory itemFactory;

	public ContainerChestUnsaga(IChestBehavior chest, EntityPlayer ep) {
		super(ep, null);
//		this.abilities = UnsagaMod.abilities;
		this.openPlayer = ep;
		this.chestBehavior = chest;
		this.chestBehavior.getCapability().setOpeningPlayer(openPlayer);
		this.setClose = false;
		this.dummy = new InventoryBasic("chest",false,10);

		//		this.chestBehaviorAgent = new ChestInteractionAgent(chest,chest.getParent());

		this.world = this.openPlayer.getEntityWorld();
		this.spreadSlotItems = false;

		this.itemFactory = new UnsagaItemFactory(this.world.rand);
		//		this.attachIcons(dummy, openPlayer, ChestSkill.SKILLS,new XY(28,29));


		//		ep.addStat(UnsagaModCore.instance().achievements.firstChest, 1);
		//		ListHelper.stream(ChestSkill.mapSkills).forEach(new Consumer<ChestSkill>(){
		//
		//			@Override
		//			public void accept(ChestSkill input) {
		//				addSlotToContainer(new SlotIcon(dummy, input.getNumber(), 28+(18*input.getNumber()), 29));
		//				ItemStack is = input.getIcon();
		//				if(!input.panelChecker.apply(openPlayer)){
		//					ComponentSelectableIcon.setNegative(is, true);
		//				}
		//				dummy.setInventorySlotContents(input.getNumber(), is);
		//			}}
		//		);

		if(WorldHelper.isServer(this.ep.getEntityWorld())){
			UnsagaMod.PACKET_DISPATCHER.sendTo(PacketSyncSkillPanel.create(openPlayer),(EntityPlayerMP) this.openPlayer);
			UnsagaTriggers.OPEN_GUI_CHEST.trigger((EntityPlayerMP) ep);
		}

//		this.ep.addStat(UnsagaAchievementRegistry.instance().firstChest);


	}


	public void activateTraps(){
		if(WorldHelper.isServer(world)){
			UnsagaTriggers.CHEST_OPEN.trigger((EntityPlayerMP) this.openPlayer);
		}
		this.ep.closeScreen();
		boolean flag = false;
		Queue<ChestTrap> queue = new ArrayBlockingQueue(10);
		this.chestBehavior.getCapability().traps().forEach(in -> queue.offer(in));
		//トラップを順番に起動していく。基本的に起動したトラップは消える
		for(int i=0;i<queue.size();i++){
			ChestTrap trap = queue.poll();
			trap.activate(chestBehavior, openPlayer);
			if(!trap.canRemove(chestBehavior,openPlayer)){
				queue.offer(trap);
			}

		}
		this.chestBehavior.getCapability().updateTraps(Lists.newArrayList(queue));



	}
	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
		if(this.chestBehavior==null){
			return false;
		}
		//		if(this.chest.getStatus(EnumProperty.OPENED)){
		//			return false;
		//		}

		//チェストが死んでたら強制終了
		if(!this.chestBehavior.isChestAlive()){
			return false;
		}
		if(this.openPlayer.openContainer.equals(this)){
			if(this.setClose){
				return false;
			}
			return this.openPlayer.equals(entityplayer);
		}

		if(this.chestBehavior.getCapability().openingPlayer().isPresent()){
			return this.chestBehavior.getCapability().openingPlayer().get().equals(entityplayer);
		}


		return false;

	}


	public void defuse(EnumActionResult result){
		if(this.chestBehavior.getCapability().hasDefused()){
			this.message = "gui.unsaga.chest.success.hasDefused";
			this.syncMessage();
			return;
		}
		switch(result){
		case FAIL:
			this.openPlayer.getFoodStats().addExhaustion(0.025F);
			this.message = "gui.unsaga.chest.failed2";
			break;
		case PASS:
			this.message = "gui.unsaga.chest.failed";
			break;
		case SUCCESS:
			this.broadcastSound(XYZPos.createFrom(openPlayer), SoundEvents.BLOCK_PISTON_CONTRACT, ep);
			this.message = "gui.unsaga.chest.success.defuse";
			this.chestBehavior.getCapability().updateTraps(Lists.newArrayList());
			break;
		default:
			break;

		}
		this.openPlayer.getFoodStats().addExhaustion(0.025F);
		this.syncMessage();
		this.syncChest();
	}

	public void divine(EnumActionResult result,Random rand){
		int up = 0;
		switch(result){
		case FAIL:
			ChatHandler.sendChatToPlayer(ep, HSLibs.translateKey("gui.unsaga.chest.failed2"));
			this.activateTraps();
			break;
		case PASS:
			this.message = "gui.unsaga.chest.failed";
			break;
		case SUCCESS:
			this.broadcastSound(XYZPos.createFrom(openPlayer), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, ep);
			this.message = this.chestBehavior.getCapability().treasureType().getMessage();
			break;
		default:
			break;

		}
		int level = chestBehavior.getCapability().level() + this.getDiviningLevelFix(result, rand);
		level = MathHelper.clamp(level, 1, 100);
		chestBehavior.getCapability().setLevel(level);
		this.openPlayer.getFoodStats().addExhaustion(0.025F);
		this.syncMessage();
		this.syncChest();
	}

	private int getDiviningLevelFix(EnumActionResult result,Random rand){
		if(result==EnumActionResult.FAIL){
			return -100;
		}
		int r = rand.nextInt(10)+1;
		return result==EnumActionResult.SUCCESS ? r : -r;
	}
	@Override
	public int getGuiID() {
		// TODO 自動生成されたメソッド・スタブ
		return UnsagaGui.Type.CHEST.getMeta();
	}

	@Override
	public PacketGuiButtonUnsaga getPacketGuiButton(int guiID, int buttonID,
			NBTTagCompound args) {
		// TODO 自動生成されたメソッド・スタブ
		return PacketGuiButtonUnsaga.create(UnsagaGui.Type.fromMeta(guiID),buttonID,args);
	}
	@Override
	public SimpleNetworkWrapper getPacketPipeline() {
		// TODO 自動生成されたメソッド・スタブ
		return UnsagaMod.PACKET_DISPATCHER;
	}

	@Override
	public PacketServerToGui getSyncPacketToClient(EntityPlayer ep){
		return PacketServerToGui.create(UtilNBT.comp().setString("message", this.message).get());
	}

	//	private void onClickMapSkill(int num) {
	//		if(WorldHelper.isClient(this.openPlayer.worldObj)){
	//			return;
	//		}
	//		openPlayer.closeScreen();
	//		ChestSkill skill = ChestSkill.SKILLS.get(num);
	//		skill.onClicked(openPlayer,this);
	//	}


	//	public IUnsagaChest getOpeningChestCore(){
	//		return this.chest;
	//	}
	//
	//	public ChestInteractionAgent getOpeningChestHelper(){
	//		return this.chestBehaviorAgent;
	//	}

	@Override
	public boolean isShowPlayerInv(){
		return false;
	}


	@Override
	public void onContainerClosed(EntityPlayer par1EntityPlayer)
	{

		super.onContainerClosed(par1EntityPlayer);

		if(WorldHelper.isServer(this.ep.getEntityWorld())){
			//			if(chest.getParent().first!=null){
			//				((EntityUnsagaChest)chest.getParent().first).sync();
			//			}
			//			if(chest.getParent().second!=null){
			//				((TileEntityUnsagaChest)chest.getParent().second).sync();
			//			}
		}
		this.chestBehavior.getCapability().setOpeningPlayer(null);


	}
	@Override
	public void onPacketData() {
		int id = this.buttonID;
		Random rand = this.ep.getRNG();
		Optional.ofNullable(GuiChest.IconType.fromMeta(id))
		.ifPresent(type ->{
			switch(type){
			case DEFUSE:
				SkillPanelAPI.getPanelOptional(ep, SkillPanels.DEFUSE)
				.ifPresent(in ->{
					EnumActionResult result = ChestHelper.tryInteraction(openPlayer, in.first(), rand, chestBehavior.getCapability(), in.second());
					this.defuse(result);
				});
				break;
			case DIVINATION:
				SkillPanelAPI.getPanelOptional(ep, SkillPanels.FORTUNE)
				.ifPresent(in ->{
					EnumActionResult result = ChestHelper.tryInteraction(openPlayer, in.first(), rand, chestBehavior.getCapability(), in.second());
					this.divine(result,rand);
				});
				break;
			case OPEN:
				this.tryOpening();
				break;
			case PENETRATION:
				SkillPanelAPI.getPanelOptional(ep, SkillPanels.SHARP_EYE)
				.ifPresent(in ->{
					EnumActionResult result = ChestHelper.tryInteraction(openPlayer, in.first(), rand, chestBehavior.getCapability(), in.second());
					this.penetrate(result);
				});
				break;
			case UNLOCK:
				SkillPanelAPI.getPanelOptional(ep, SkillPanels.LOCKSMITH)
				.ifPresent(in ->{
					EnumActionResult result = ChestHelper.tryInteraction(openPlayer, in.first(), rand, chestBehavior.getCapability(), in.second());
					this.unlock(result);
				});
				break;
			default:
				break;

			}
		});

	}
//
//	@Override
//	public void onSlotClick(int rawSlotNumber,int containerSlotNumber,int clickButton,ClickType par3,EntityPlayer ep){
//		ItemStack is = this.getSlot(rawSlotNumber).getStack();
//		if(is!=null && is.getItem() instanceof ItemIconMapSkill){
//			this.playClickSound();
//			int num =  is.getItemDamage();
//			if(!ComponentSelectableIcon.isNegative(is)){
//				//				this.onClickMapSkill(num);
//			}
//
//		}
//
//	}

	public void openChest(){
		if(this.chestBehavior.getCapability().hasLocked()){
			ChatHandler.sendChatToPlayer(openPlayer, HSLibs.translateKey("gui.unsaga.chest.locked"));

		}else{
			if(this.chestBehavior.getCapability().hasMagicLocked()){
				ChatHandler.sendChatToPlayer(openPlayer,HSLibs.translateKey("gui.unsaga.chest.magicLocked"));

			}else{
				this.chestBehavior.getCapability().setOpened(true);
				ItemStack treasure = this.chestBehavior.getCapability().treasureType().createTreasure(this.world.rand, this.chestBehavior.getCapability().level(), itemFactory);
				ItemUtil.dropItem(world, treasure, this.chestBehavior.getChestPosition());
				ChatHandler.sendChatToPlayer(openPlayer, HSLibs.translateKey("gui.unsaga.chest.opening"));
				ep.closeScreen();

			}
		}
	}

	public void penetrate(EnumActionResult result){
		if(this.chestBehavior.getCapability().hasAnalyzed()){
			this.message = "gui.unsaga.chest.success.hasAnalyzed";
			this.syncMessage();
			return;
		}
		switch(result){
		case FAIL:
			ChatHandler.sendChatToPlayer(ep, HSLibs.translateKey("gui.unsaga.chest.failed2"));
			this.activateTraps();
			break;
		case PASS:
			this.message = "gui.unsaga.chest.failed";
			break;
		case SUCCESS:
			this.broadcastSound(XYZPos.createFrom(openPlayer), SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, ep);
			this.message = "gui.unsaga.chest.success.penetrate";
			this.chestBehavior.getCapability().setAnalyzed(true);
			break;
		default:
			break;

		}
		this.openPlayer.getFoodStats().addExhaustion(0.025F);
		this.syncMessage();
		this.syncChest();
	}

	public void syncChest(){
		this.chestBehavior.sync(openPlayer);

	}

	public void syncMessage(){
		if(!this.message.isEmpty()){
			if(WorldHelper.isServer(this.openPlayer.getEntityWorld())){
				HSLib.packetDispatcher().sendTo(this.getSyncPacketToClient(openPlayer), (EntityPlayerMP) this.openPlayer);
			}
		}

	}
	public void tryOpening(){
		this.activateTraps();
		this.openChest();

	}
	public void unlock(EnumActionResult result){
		if(!this.chestBehavior.getCapability().hasLocked()){
			this.message = "gui.unsaga.chest.success.hasUnlocked";
			this.syncMessage();
			return;
		}
		switch(result){
		case FAIL:
			ChatHandler.sendChatToPlayer(ep, HSLibs.translateKey("gui.unsaga.chest.failed2"));
			this.activateTraps();
			break;
		case PASS:
			this.message = "gui.unsaga.chest.failed";
			break;
		case SUCCESS:

			this.broadcastSound(XYZPos.createFrom(openPlayer), SoundEvents.BLOCK_PISTON_CONTRACT, ep);
			this.message = "gui.unsaga.chest.success.unlock";
			this.chestBehavior.getCapability().setLocked(false);
			break;
		default:
			break;

		}
		this.openPlayer.getFoodStats().addExhaustion(0.025F);
		this.syncMessage();
		this.syncChest();
	}


}
