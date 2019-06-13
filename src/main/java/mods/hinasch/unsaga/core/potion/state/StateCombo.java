package mods.hinasch.unsaga.core.potion.state;

import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.network.PacketSound;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.lib.network.PacketUtil;
import mods.hinasch.lib.particle.PacketParticle;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.lib.util.VecUtil;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.specialmove.action.ComboActionProperty;
import mods.hinasch.unsaga.core.client.event.RenderUnsagaHudHandler;
import mods.hinasch.unsaga.core.potion.EntityState;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import mods.hinasch.unsaga.damage.AdditionalDamage;
import mods.hinasch.unsaga.damage.AdditionalDamageCache;
import mods.hinasch.unsaga.damage.DamageComponent;
import mods.hinasch.unsaga.lp.LifePoint;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class StateCombo extends EntityState{

	//TODO:ターゲットの同一性も観るようにする
	public StateCombo(String name) {
		super(name);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	@Override
	public boolean isValidPotionEffect(PotionEffect effect){
		return effect instanceof Effect;
	}


	@Override
	public void onClientPacketEvent(EntityLivingBase living,NBTTagCompound tag){
		String message = tag.getString("message");
		RenderUnsagaHudHandler.createAlert(message);

	}
	public static void onDamage(LivingHurtEvent e){
		if(!(e.getSource().getTrueSource() instanceof EntityLivingBase)){
			return;
		}
		EntityLivingBase attacker = (EntityLivingBase) e.getSource().getTrueSource();
		if(StateCapability.isStateActive(attacker, UnsagaPotions.COMBO)){
			World world = e.getEntity().getEntityWorld();
			EntityLivingBase victim = e.getEntityLiving();
			Effect comboState = (Effect) StateCapability.getState(attacker, UnsagaPotions.COMBO);
			int attackCount = comboState.getAmplifier();
			if(comboState.getWeapon()!=attacker.getHeldItemMainhand()){
				return;
			} //コンボの武器が同じか
			if(comboState.getHitTick()==attacker.ticksExisted){
				return;
			} //スイープの同時ヒットによるカウントを防ぐ（同じティック内のヒットはコンボに加算しない）
			ComboActionProperty prop = comboState.getProperty();
			ComboActionProperty.Element element = prop.getElement(attackCount);
			if(!isAllowedAttack(element, attacker)){
				return;
			}
			if(attackCount+1>=comboState.getMaxCount()){
				processFinishAttack(e, attacker, victim, comboState, element);
			}else{
				processComboAttack(e, attacker, victim, comboState, element);
//				UnsagaMod.logger.trace(this.getName(), "combo:",e.getEntityLiving().getActivePotionEffect(this).getAmplifier());
			}
		}
	}

	private static boolean isAllowedAttack(ComboActionProperty.Element element,EntityLivingBase attacker){
		if(element.isRequireSprint()){
			return attacker.isSprinting();
		}
		return true;
	}
	private static void processComboAttack(LivingHurtEvent e,EntityLivingBase attacker,EntityLivingBase victim,Effect effect,ComboActionProperty.Element elm){
		int attackCount = effect.getAmplifier();
		HSLib.packetDispatcher().sendToAllAround(PacketParticle.create(new XYZPos(victim.getPosition().up()),elm.getHitParticle(),elm.getParticleDensity(),elm.getParticleBlock()), PacketUtil.getTargetPointNear(victim));

		HSLib.packetDispatcher().sendToAllAround(PacketSound.atEntity(elm.getHitSound(), e.getEntityLiving()).setPitch(elm.getSoundPitch()), PacketUtil.getTargetPointNear(victim));
//		UnsagaPotions.addRemoveQueue(e.getEntityLiving(), this);
		StateCapability.getCapability(attacker).addState(effect.copy(attackCount+1).setHitTick(attacker.ticksExisted));


		sendAlertMessage(attacker,String.valueOf(attackCount+1)+" Hit Combo!");
//		if(attacker instanceof EntityPlayer){
//			ChatHandler.sendChatToPlayer((EntityPlayer) attacker, (attackCount)+" Hit Combo!");
//		}
	}

	private static void sendAlertMessage(EntityLivingBase attacker,String message){
		UnsagaMod.PACKET_DISPATCHER.sendToAll(PacketSyncCapability.create(StateCapability.CAPA, StateCapability.ADAPTER.getCapability(attacker)
				, UtilNBT.comp().setEntity(attacker).setString("potion", UnsagaPotions.COMBO.getRegistryName().toString())
				.setString("message", message).setOperation(StateCapability.OPERATION_POTION_EVENT).get()));
	}
	private static void processFinishAttack(LivingHurtEvent e,EntityLivingBase attacker,EntityLivingBase victim,Effect effect,ComboActionProperty.Element elm){
		int attackCount = effect.getAmplifier();
		LifePoint.adapter.getCapability(victim).setHurtInterval(0);
		HSLib.packetDispatcher().sendToAllAround(PacketParticle.create(new XYZPos(victim.getPosition().up()),elm.getHitParticle(),elm.getParticleDensity(),elm.getParticleBlock()), PacketUtil.getTargetPointNear(victim));
		StateCapability.getCapability(attacker).removeStateWithSync(attacker,UnsagaPotions.COMBO);
		HSLib.packetDispatcher().sendToAllAround(PacketSound.atEntity(elm.getHitSound(), e.getEntityLiving()).setPitch(elm.getSoundPitch()), PacketUtil.getTargetPointNear(victim));
		e.setAmount(e.getAmount()+effect.getFinishDamage().hp());
		if(AdditionalDamageCache.getData(e.getSource())!=null){
			AdditionalDamage data = AdditionalDamageCache.getData(e.getSource());
			data.setLP(effect.getFinishDamage().lp());
		}

		sendAlertMessage(attacker,String.valueOf(attackCount+1)+" Hit Combo Finish!");
		VecUtil.knockback(attacker, victim, 1.0D, 0.4D);

//		if(attacker instanceof EntityPlayer){
//			ChatHandler.sendChatToPlayer((EntityPlayer) attacker, (attackCount)+" Hit Combo Finish!");
//		}
	}
	public static class Effect extends PotionEffect{

		final DamageComponent finishDamage;
		final ItemStack weapon;
		final ComboActionProperty prop;

		int hitTick = 0;
		public Effect(int current,DamageComponent lastDamage,ComboActionProperty prop,ItemStack weapon) {
			super(UnsagaPotions.COMBO, ItemUtil.getPotionTime(2),current,false,false);
			this.finishDamage = lastDamage;
			this.weapon = weapon;
			this.prop = prop;
			// TODO 自動生成されたコンストラクター・スタブ
		}

		public DamageComponent getFinishDamage(){
			return this.finishDamage;
		}

		public EnumParticleTypes getParticle(){
			return this.getProperty().getElement(0).getHitParticle();
		}

		public ItemStack getWeapon(){
			return this.weapon;
		}

		public int getHitTick(){
			return this.hitTick;
		}

		public Effect setHitTick(int tick){
			this.hitTick = tick;
			return this;
		}
		public int getMaxCount(){
			return this.prop.getMaxCount();
		}

		public ComboActionProperty getProperty(){
			return this.prop;
		}
		public Effect copy(int hit){
			return new Effect(hit,this.finishDamage,prop, this.weapon);
		}


	}


}
