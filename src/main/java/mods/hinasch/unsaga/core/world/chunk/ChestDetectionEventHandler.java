package mods.hinasch.unsaga.core.world.chunk;

import java.util.Optional;

import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.network.PacketSound;
import mods.hinasch.lib.util.ChatHandler;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.unsaga.core.advancement.UnsagaTriggers;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;

public class ChestDetectionEventHandler {

	public static final int CHEST_CHECK_INTERVAL = 100;
	public static void onLivingUpdate(LivingUpdateEvent e){

		Optional.of(e.getEntityLiving())
		.filter(in -> in instanceof EntityPlayer)
		.filter(in -> in.ticksExisted % CHEST_CHECK_INTERVAL == 0)
		.filter(in -> in.isSneaking())
		.filter(in -> WorldHelper.isServer(in.getEntityWorld()))
		.map(in ->(EntityPlayerMP)in)
		.ifPresent(ep ->{
			World world = ep.getEntityWorld();
			Chunk chunk = world.getChunkFromBlockCoords(ep.getPosition());
			UnsagaChunkCapability.ADAPTER.getCapabilityOptional(chunk)
			.ifPresent(in ->{
				if(in.chunkChestStorage().hasDetectableChest(ep.getPosition())){
					HSLib.packetDispatcher().sendTo(PacketSound.atEntity(SoundEvents.BLOCK_PISTON_EXTEND, ep), ep);
					ChatHandler.sendChatToPlayer(ep, HSLibs.translateKey("msg.chest.found"));
					if(WorldHelper.isServer(world)){
						UnsagaTriggers.CHEST_FIND.trigger(ep);
					}
					in.chunkChestStorage().spawnChest(world, ep.getPosition());
				}
			});
		});

	}
}
