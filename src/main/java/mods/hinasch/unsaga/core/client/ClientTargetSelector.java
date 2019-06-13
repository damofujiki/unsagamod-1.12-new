package mods.hinasch.unsaga.core.client;

import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Optional;
import java.util.TreeMap;

import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew;
import mods.hinasch.unsaga.status.TargetHolderCapability;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.world.World;

public class ClientTargetSelector {

	private int currentIndex = -1;
//	private List<EntityLivingBase> targetList;
	private NavigableMap<Integer,EntityLivingBase> targetMap = new TreeMap<Integer,EntityLivingBase>();
	//	private double defaultRange = UnsagaMod.configs.getDefaultTargettingRange();
	//	private double defaultRangeV = UnsagaMod.configs.getDefaultTargettingRangeVertical();


	public ClientTargetSelector(){
//		this.targetList = new ArrayList();
	}

	public void next(){
		this.gatherLivings();
		if(!this.targetMap.isEmpty()){
			this.findNextIndex();
		}

	}
	public int getCurrentIndex(){
		return this.currentIndex;
	}
	protected void gatherLivings(){
		ClientHelper.getPlayer().playSound(SoundEvents.UI_BUTTON_CLICK, 1.0F,2.0F);
		UnsagaMod.logger.trace("[client target selector]gathering entities...");
		World world = ClientHelper.getWorld();
		final EntityPlayer clientPlayer = ClientHelper.getPlayer();
		double defaultRange = UnsagaConfigHandlerNew.GENERAL.targettingRange[0];
		double defaultRangeV = UnsagaConfigHandlerNew.GENERAL.targettingRange[1];

		List<EntityLivingBase> list = world.getEntitiesWithinAABB(EntityLivingBase.class,clientPlayer.getEntityBoundingBox().grow(defaultRange, defaultRangeV, defaultRange) ,input -> input!=clientPlayer);
		UnsagaMod.logger.trace(this.getClass().getName(), list,defaultRange,defaultRangeV);
		if(!list.isEmpty()){
			this.targetMap.clear();
			list.stream()
			.forEach(input -> targetMap.put(input.getEntityId(), input));

		}

	}

	protected void findNextIndex(){
		//		Entry<Integer,EntityLivingBase> entityEntry = targetMap.higherEntry(this.currentIndex);
		Entry<Integer,EntityLivingBase> entityEntry =
				Optional.ofNullable(targetMap.higherEntry(this.currentIndex))
				.map(in ->{
					if(GuiContainer.isShiftKeyDown()){//シフトを押していると一番近いターゲット
						return targetMap.entrySet().stream()
								.reduce(in,(a,b)->{
									double dis1 = ClientHelper.getPlayer().getDistance(a.getValue());
									double dis2 = ClientHelper.getPlayer().getDistance(b.getValue());
									return dis1>dis2 ? b : a;
								});
					}
					return in;
				}).orElse(null);

		if(entityEntry!=null){
			this.currentIndex = entityEntry.getKey();
			this.updateTargetHolder(currentIndex);
			UnsagaMod.logger.trace("[client target selector]targetting current index:"+this.currentIndex);
		}else{ //マップが空、もしくは現キーより上のターゲットが存在しない場合はリセット
			this.currentIndex = -1;
			if(!this.targetMap.isEmpty()){
				Entry<Integer,EntityLivingBase> resetEntry = targetMap.higherEntry(this.currentIndex);
				this.currentIndex = resetEntry.getKey();
				this.updateTargetHolder(currentIndex);
				UnsagaMod.logger.trace("[client target selector]targetting has reset.:"+this.currentIndex);
			}
		}


	}

	protected void updateTargetHolder(int targetid){

		Optional.ofNullable(ClientHelper.getPlayer())
		.filter(in -> ClientHelper.getWorld()!=null)
		.ifPresent(clientPlayer ->{
			Entity target = clientPlayer.getEntityWorld().getEntityByID(targetid);
			if(target instanceof EntityLivingBase){
				TargetHolderCapability.adapter.getCapabilityOptional(clientPlayer)
				.ifPresent(in ->{
					in.updateTarget((EntityLivingBase) target);
					HSLib.packetDispatcher()
					.sendToServer(PacketSyncCapability
							.create(TargetHolderCapability.CAPA, in
									,UtilNBT.comp()
									.setInteger("entityid", -2)
									.setInteger("targetid", targetid).get()));
				});
			}
		});


	}
}
