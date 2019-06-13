package mods.hinasch.unsaga.ability;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import com.mojang.realmsclient.util.Pair;

import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.network.PacketSound;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.lib.util.ChatHandler;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.slot.AbilitySlotList;
import mods.hinasch.unsaga.ability.specialmove.LearningFacilityHelper;
import mods.hinasch.unsaga.core.advancement.UnsagaTriggers;
import mods.hinasch.unsaga.core.item.weapon.ItemGloveUnsaga;
import mods.hinasch.unsaga.skillpanel.SkillPanelAPI;
import mods.hinasch.unsaga.skillpanel.SkillPanels;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

/**
 *
 * 技をひらめくイベント。パッシブアビリティ（撃破時）、技（アタック時）
 *
 */
public class LearningAbilityEventHandler {

	public static void onLivingAttack(final LivingAttackEvent e){
		if(e.getSource().getTrueSource() instanceof EntityPlayer){
			if(e.getEntityLiving() instanceof IMob){
				onPlayerAttackMob(e);
			}
		}
	}


	private static double calcSparkingTechModifier(final ItemStack weapon,final AbilitySlotList abilityList,final EntityPlayer player){
		float modifier = 0;
		if(weapon.getItem() instanceof ItemGloveUnsaga){
			final int size = abilityList.getUnlockedAbilities().size();
			modifier += (-0.01F*(size*2));
		}
		//弓はちょっとひらめきやすく
		if(weapon.getItem() instanceof ItemBow){
			modifier += 0.10F;
		}
		return modifier;
	}

	/**
	 * アビリティ引き出し率の基本値を取得。目利きがあると引き出し率が上がる。
	 * @param player
	 * @param d
	 * @return
	 */
	private static double calcInvokingAbilityModifier(final EntityPlayer player){
		double base = 0.0D;
		if(SkillPanelAPI.getHighestPanelLevel(player,SkillPanels.ARTISTE).isPresent()){
			base += 0.05D * (double)SkillPanelAPI.getHighestPanelLevel(player, SkillPanels.ARTISTE).getAsInt();
		}
		return base;
	}
	/**
	 * 敵を攻撃した時（ひらめきイベント）
	 * @param e
	 */
	private static void onPlayerAttackMob(final LivingAttackEvent e){
		if(WorldHelper.isClient(e.getEntityLiving().getEntityWorld())){
			return;
		}
		final EntityPlayer player = (EntityPlayer) e.getSource().getTrueSource();
		final EntityLivingBase mob = e.getEntityLiving();
		final Random rand = player.getEntityWorld().rand;
		final double sparkling = LearningFacilityHelper.getBaseValue(mob);
		//		sparkling += calcInvokingAbilityModifier(player, 0.02D);
		final ItemStack weapon = player.getHeldItem(EnumHand.MAIN_HAND);
		if(weapon.isEmpty()){
			return;
		}

		if(!AbilityCapability.adapter.hasCapability(weapon)){
			return;
		}
		final AbilitySlotList abilityList = AbilityCapability.adapter.getCapability(weapon).getAbilitySlots();


		final float f = rand.nextFloat();
		if(f<(sparkling+calcSparkingTechModifier(weapon, abilityList, player)) ){








			if(abilityList.existLearnableSlot()){
				final Optional<IAbility> learned = AbilityAPI.attachRandomAbility(rand, weapon, Optional.of(player));
				if(learned.isPresent()){
					HSLib.packetDispatcher().sendTo(PacketSyncCapability.create(AbilityCapability.CAPA, AbilityCapability.adapter.getCapability(weapon)),(EntityPlayerMP) player );
					HSLib.packetDispatcher().sendTo(PacketSound.atEntity(SoundEvents.BLOCK_ANVIL_PLACE, player), (EntityPlayerMP) player);
					final String msg = HSLibs.translateKey("ability.unsaga.sparkling.specialMove", learned.get().getLocalized());
					ChatHandler.sendChatToPlayer(player, msg);
					UnsagaTriggers.FIRST_SPARK.trigger((EntityPlayerMP) player);
					//					player.addStat(UnsagaAchievementRegistry.instance().learnSkillFirst);
				}
			}
			//			UnsagaMod.logger.trace(EventAbilityLearning.class.getClass().getName(), AbilityAPI.existLearnableAbility(weapon,player));
			//			if(AbilityAPI.existLearnableAbility(weapon,player)){
			//				Optional<IAbility> learned = AbilityAPI.attachRandomAbility(player.getRNG(), weapon,player);
			//				if(learned.isPresent()){
			//					HSLib.getPacketDispatcher().sendTo(PacketSyncCapability.create(AbilityCapability.CAPA, AbilityCapability.adapter.getCapability(weapon)),(EntityPlayerMP) player );
			//					HSLib.getPacketDispatcher().sendTo(PacketSound.atEntity(SoundEvents.BLOCK_ANVIL_PLACE, player), (EntityPlayerMP) player);
			//					String msg = HSLibs.translateKey("ability.unsaga.sparkling.specialMove", learned.get().getLocalized());
			//					ChatHandler.sendChatToPlayer(player, msg);
			//					UnsagaTriggers.LEARN_SPECIALMOVE.trigger((EntityPlayerMP) player);
			////					player.addStat(UnsagaAchievementRegistry.instance().learnSkillFirst);
			//				}
			//
			//			}
		}
	}

	public static void onLivingDeath(final LivingDeathEvent e){
		if(e.getSource().getTrueSource() instanceof EntityPlayer){
			if(e.getEntityLiving() instanceof IMob && e.getEntityLiving().getMaxHealth()>=4.0F){
				onPlayerKilledMob(e);
			}
		}
	}

	/**
	 * 敵を撃破した時（引き出しイベント）
	 * @param e
	 */
	private static void onPlayerKilledMob(final LivingDeathEvent e){
		if(WorldHelper.isClient(e.getEntityLiving().getEntityWorld())){
			return;
		}
		final EntityPlayer player = (EntityPlayer) e.getSource().getTrueSource();
		final EntityLivingBase mob = e.getEntityLiving();
		final Random rand = player.getEntityWorld().rand;
		double sparkling = LearningFacilityHelper.getBaseValue(mob);
		sparkling += calcInvokingAbilityModifier(player);
		sparkling += 0.1D; //技より少し引き出しやすく
		final float f = rand.nextFloat();
		UnsagaMod.logger.trace("sparkling", f,sparkling);
		if(f<sparkling){
			//		if(true){

			//クライアントへアビリティを引き出した部位の情報を送るために装備スロットの情報が必要
			final List<Pair<AbilityAPI.EquipmentSlot,ItemStack>> learnableItems = AbilityAPI.getAllEquippedArmors(player).stream()
					.filter(in -> AbilityCapability.adapter.hasCapability(in.second()) && AbilityCapability.adapter.getCapability(in.second()).getAbilitySlots().existLearnableSlot())
					.collect(Collectors.toList());
			//			List<ItemStack> list2 = list.stream().filter(in -> AbilityCapability.adapter.hasCapability(in)).collect(Collectors.toList());
			UnsagaMod.logger.trace("spkarling", learnableItems.stream().map(in -> in.second()).collect(Collectors.toList()));
			if(!learnableItems.isEmpty()){
				final Pair<AbilityAPI.EquipmentSlot,ItemStack> sparkItem = HSLibs.randomPick(rand, learnableItems);
				final Optional<IAbility> learned = AbilityAPI.attachRandomAbility(player.getRNG(), sparkItem.second(),Optional.empty());
				if(learned.isPresent()){
					HSLib.packetDispatcher()
					.sendTo(PacketSyncCapability.create(AbilityCapability.CAPA
							,AbilityCapability.adapter.getCapability(sparkItem.second())
							,UtilNBT.comp().setString("slot", sparkItem.first().getName()).get())
							,(EntityPlayerMP) player);
					HSLib.packetDispatcher().sendTo(PacketSound.atEntity(SoundEvents.BLOCK_ANVIL_PLACE, player), (EntityPlayerMP) player);
					final String msg = HSLibs.translateKey("ability.unsaga.sparkling.passive", sparkItem.second().getDisplayName(),learned.get().getLocalized());
					ChatHandler.sendChatToPlayer(player, msg);
					UnsagaTriggers.FIRST_ABILITY.trigger((EntityPlayerMP) player);
					//					player.addStat(UnsagaAchievementRegistry.instance().gainAbilityFirst);
				}

			}


		}
	}
}
