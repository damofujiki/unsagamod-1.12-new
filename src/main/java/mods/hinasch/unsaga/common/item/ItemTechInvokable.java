package mods.hinasch.unsaga.common.item;

import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.AbilityAPI;
import mods.hinasch.unsaga.ability.IAbilitySelector;
import mods.hinasch.unsaga.ability.slot.AbilitySlotList;
import mods.hinasch.unsaga.ability.slot.AbilitySlotType;
import mods.hinasch.unsaga.ability.specialmove.Tech;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker.InvokeType;
import mods.hinasch.unsaga.common.specialaction.IActionPerformer.TargetType;
import mods.hinasch.unsaga.common.specialaction.IRightClickToCharge;
import mods.hinasch.unsaga.core.entity.UnsagaActionCapability;
import mods.hinasch.unsaga.core.event.SprintTimerHandler;
import mods.hinasch.unsaga.core.net.packet.PacketSyncActionPerform;
import mods.hinasch.unsaga.material.IUnsagaCategoryTool;
import mods.hinasch.unsaga.status.TargetHolderCapability;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemTechInvokable extends ItemSword implements IAbilitySelector{
	public ItemTechInvokable(ToolMaterial material) {
		super(material);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	protected boolean hasTechType(ItemStack is,InvokeType type){
		return AbilityAPI.getLearnedSpecialMove(is).filter(in -> in.getAction().getInvokeTypes().contains(type)).isPresent();
	}

	protected boolean canInvoke(ItemStack is,InvokeType type){
		if(!AbilityAPI.getCurrentTech(is).isAbilityEmpty()){
			Tech tech = (Tech) AbilityAPI.getCurrentTech(is);
			return tech.getAction().getInvokeTypes().contains(type);
		}
		return false;
	}
	@Override
	public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand hand)
	{
		ItemStack itemStackIn = playerIn.getHeldItem(hand);
		//ディフレクト（オフハンドに持って右クリ）
		if(hand==EnumHand.OFF_HAND && AbilityAPI.hasBlockingAbility(itemStackIn)){
			if(StateCapability.ADAPTER.hasCapability(playerIn) && itemStackIn.getItem() instanceof IUnsagaCategoryTool){
				//				ChatHandler.sendChatToPlayer(playerIn, "kiteru1");
				if(UnsagaActionCapability.ADAPTER.getCapability(playerIn).getWeaponGuardCooling()<=0){
					//					ChatHandler.sendChatToPlayer(playerIn, "kiteru2");
					playerIn.swingArm(EnumHand.OFF_HAND);
					IUnsagaCategoryTool instance = (IUnsagaCategoryTool) itemStackIn.getItem();
					UnsagaActionCapability.ADAPTER.getCapability(playerIn).resetWeaponGuardProgress(instance.getCategory());
					return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
				}
			}
		}
		//技（利き手に持ってSneak右クリ）
		if(playerIn.isSneaking() && hand==EnumHand.MAIN_HAND){

			//乱れ雪月花のみで使う、ちょっと無理やりねじこんだ処理
			if(this.canInvoke(itemStackIn, InvokeType.RIGHTCLICK_TO_CHARGE)){
				Tech move = (Tech) AbilityAPI.getCurrentTech(itemStackIn);
				if(move.getAction() instanceof IRightClickToCharge){
					TechInvoker invoker = new TechInvoker(worldIn, playerIn, move);
					invoker.setArtifact(itemStackIn);
					invoker.setInvokeType(InvokeType.RIGHTCLICK);
					invoker.setTargetType(TargetType.TARGET);
					TargetHolderCapability.getTarget(playerIn).ifPresent(in -> invoker.setTarget(in));

					if(((IRightClickToCharge)move.getAction()).canCharge(invoker)){
						playerIn.setActiveHand(hand);
					}else{
						invoker.invoke();
					}


				}

				return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
			}
			if(this.canInvoke(itemStackIn, InvokeType.CHARGE)){
				Tech move = (Tech) AbilityAPI.getCurrentTech(itemStackIn);
				playerIn.setActiveHand(hand);
				return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
			}
			//			ChatHandler.sendChatToPlayer(playerIn, String.valueOf(EventSprintTimer.getSprintTimer(playerIn)));

			//走ってる状態でクリック
			if(this.canInvoke(itemStackIn, InvokeType.SPRINT_RIGHTCLICK) && SprintTimerHandler.getSprintTimer(playerIn)>10){
				if(WorldHelper.isClient(worldIn))HSLib.packetDispatcher().sendToServer(PacketSyncCapability.create(StateCapability.CAPA	, StateCapability.ADAPTER.getCapability(playerIn)));
				SprintTimerHandler.resetTimer(playerIn);
				Tech move = (Tech) AbilityAPI.getCurrentTech(itemStackIn);
				TechInvoker invoker = new TechInvoker(worldIn, playerIn, move);
				invoker.setArtifact(itemStackIn);
				invoker.setInvokeType(InvokeType.SPRINT_RIGHTCLICK);
				invoker.invoke();
				return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
			}

			//普通にクリック
			if(this.canInvoke(itemStackIn, InvokeType.RIGHTCLICK)){
				Tech move = (Tech) AbilityAPI.getCurrentTech(itemStackIn);
				TechInvoker invoker = new TechInvoker(worldIn, playerIn, move);
				invoker.setArtifact(itemStackIn);
				invoker.setInvokeType(InvokeType.RIGHTCLICK);
				invoker.setTargetType(TargetType.TARGET);
				invoker.invoke();
				return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
			}
		}

		return new ActionResult(EnumActionResult.PASS, itemStackIn);
	}
	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World worldIn, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		ItemStack stack = player.getHeldItem(hand);
		if(this.canInvoke(stack, InvokeType.USE) && player.isSneaking()){
			Tech move = (Tech) AbilityAPI.getCurrentTech(stack);
			TechInvoker invoker = new TechInvoker(worldIn, player, move);
			invoker.setArtifact(stack);
			invoker.setTargetType(TargetType.POSITION);
			invoker.setInvokeType(InvokeType.USE);
			invoker.setUseFacing(facing);
			invoker.setTargetCoordinate(pos);
			invoker.invoke();
			return EnumActionResult.SUCCESS;

		}
		return EnumActionResult.PASS;
	}

	@Override
	public void onPlayerStoppedUsing(ItemStack itemStackIn, World worldIn, EntityLivingBase playerIn, int timeLeft)
	{
		if(WorldHelper.isClient(worldIn)){
			return;
		}

		this.invokeChargedAttack(itemStackIn, worldIn, playerIn, timeLeft);
		UnsagaMod.PACKET_DISPATCHER.sendToAll(PacketSyncActionPerform.createSyncChargedAttackPacket(timeLeft));
	}

	public void invokeChargedAttack(ItemStack itemStackIn, World worldIn, EntityLivingBase playerIn, int timeLeft)
	{
		//		ChatHandler.sendChatToPlayer((EntityPlayer) playerIn, WorldHelper.isServer(worldIn) ? "server" : "client");
		Tech move = (Tech) AbilityAPI.getCurrentTech(itemStackIn);
		TechInvoker invoker = new TechInvoker(worldIn, playerIn, move);
		if(move.isRequireTarget()){
			invoker.setTargetType(TargetType.TARGET);
		}
		invoker.setArtifact(itemStackIn);
		invoker.setInvokeType(InvokeType.CHARGE);
		invoker.setChargedTime(this.getMaxItemUseDuration(itemStackIn)-timeLeft);
		invoker.invoke();


	}
	@Override
	public int getMaxAbilitySize() {
		// TODO 自動生成されたメソッド・スタブ
		return 0;
	}

	@Override
	public AbilitySlotList createAbilityList() {
		// TODO 自動生成されたメソッド・スタブ
		return AbilitySlotList.builder().replaceable(1).selectable(true).slot(AbilitySlotType.TECH,AbilitySlotType.BLOCKING,AbilitySlotType.TECH_MATERIAL,AbilitySlotType.NO_FUNCTION).build();
	}
}
