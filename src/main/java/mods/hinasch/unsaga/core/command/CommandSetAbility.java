package mods.hinasch.unsaga.core.command;

import java.util.Optional;

import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.AbilityAPI;
import mods.hinasch.unsaga.ability.AbilityCapability;
import mods.hinasch.unsaga.ability.IAbility;
import mods.hinasch.unsaga.ability.IAbilityAttachable;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;

public class CommandSetAbility extends CommandBase{

	@Override
	public String getName() {
		// TODO 自動生成されたメソッド・スタブ
		return "setability";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		// TODO 自動生成されたメソッド・スタブ
		return  "/setability <abilityID>";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(args.length<2){
			return;
		}
		String abilityID = args[0];
		int slotID = Integer.valueOf(args[1]);
		IAbility ability = Optional.of(AbilityAPI.getAbilityByID(abilityID))
				.filter(in ->!in.isAbilityEmpty())
				.orElseThrow(() ->new NumberInvalidException("commands.generic.num.invalid", new Object[] {abilityID}));
		if(sender instanceof EntityPlayer){
			EntityPlayer ep = (EntityPlayer) sender;
			ItemStack stack = Optional.of(ep.getHeldItemMainhand())
					.filter(in ->!in.isEmpty())
					.orElseThrow(() ->new CommandException("command.setability.wrongitem", (Object)null));
			if(AbilityCapability.adapter.hasCapability(stack)){
				IAbilityAttachable instance = AbilityCapability.adapter.getCapability(stack);
				instance.getAbilitySlots().updateSlot(slotID, ability);
				UnsagaMod.PACKET_DISPATCHER.sendTo(PacketSyncCapability.create(AbilityCapability.CAPA, instance), (EntityPlayerMP) sender);
				this.notifyCommandListener(sender, this, "command.setability.success", new Object[]{ability.getUnlocalizedName()});
				sender.setCommandStat(Type.AFFECTED_ITEMS, 1);
			}
		}
		//		String abilityID = args[0];
		//		if(UnsagaMod.abilities.getAbility(abilityID)==null){
		//			throw new NumberInvalidException("commands.generic.num.invalid", new Object[] {abilityID});
		//		}
		//
		//		Ability ability = UnsagaMod.abilities.getAbility(abilityID);
		//		if(sender instanceof EntityPlayer){
		//			EntityPlayer ep = (EntityPlayer) sender;
		//			ItemStack stack = ep.getHeldItemMainhand();
		//			if(stack==null){
		//				throw new CommandException("command.setability.wrongitem", (Object)null);
		//			}
		//			UnsagaMod.logger.trace("trying setability...");
		//
		//			if(AbilityHelper.hasCapability(stack)){
		//				AbilityHelper.getCapability(stack).setAbilityList(Lists.newArrayList(ability));
		//
		////				UnsagaMod.logger.trace("suceeded setability. "+ability.name);
		//				UnsagaMod.packetDispatcher.sendTo(PacketSyncAbiltyHeldItem.create(ability), (EntityPlayerMP) ep);
		//				this.notifyCommandListener(sender, this, "command.setability.success", new Object[]{ability.getName()});
		//				sender.setCommandStat(Type.AFFECTED_ITEMS, 1);
		//			}
		//
		//			UnsagaMod.logger.trace(WorldHelper.isServer(((EntityPlayer) sender).worldObj) ? "sever" : "client");
		//
		//		}

	}

}
