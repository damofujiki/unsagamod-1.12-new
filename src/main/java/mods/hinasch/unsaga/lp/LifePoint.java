package mods.hinasch.unsaga.lp;

import java.util.Optional;

import mods.hinasch.lib.capability.CapabilityAdapterFactory.CapabilityAdapterPlanImpl;
import mods.hinasch.lib.capability.CapabilityAdapterFrame;
import mods.hinasch.lib.capability.CapabilityStorage;
import mods.hinasch.lib.capability.ComponentCapabilityAdapters;
import mods.hinasch.lib.capability.ISyncCapability;
import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.iface.IRequireInitializing;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew;
import mods.hinasch.unsaga.util.HealTimerCalculator;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LifePoint {

	@CapabilityInject(ILifePoint.class)
	public static Capability<ILifePoint> CAPA;

	public static final String SYNC_ID = "lpsystem";

	public static final int RENDER_DAMAGE_TIME = 30;

	public static CapabilityAdapterFrame<ILifePoint> BUILDER = UnsagaMod.CAPA_ADAPTER_FACTORY.create(new CapabilityAdapterPlanImpl(
			()->CAPA,()->ILifePoint.class,()->DefaultImpl.class,Storage::new));
	public static ComponentCapabilityAdapters.Entity<ILifePoint> adapter = BUILDER.createChildEntity(SYNC_ID);

	static{
		adapter.setPredicate(ev -> ev.getObject() instanceof EntityLivingBase);
		adapter.setRequireSerialize(true);
	}
	public static interface ILifePoint extends IRequireInitializing,ISyncCapability{



		public void init(EntityLivingBase living);
		public int healTimer();
		public void setHealTimer(int timer);
		public float lifeSaturation();
		public void setLifeSaturation(float par1);
		public int lifePoint();
		public void setLifePoint(int par1);
		public void addLifePoint(int par1);
		public int maxLifePoint();
		public void setMaxLifePoint(int par1);
		public void restoreLifePoint();
		public int hurtInterval();
		public void setHurtInterval(int par1);
		public void decrLifePoint(int par1);
//		@Deprecated
//		public void onHealed(EntityLivingBase living,float amount);
//		public void onUpdate(EntityLivingBase living);
		@SideOnly(Side.CLIENT)
		public int renderTicks();
		@SideOnly(Side.CLIENT)
		public void setRenderTicks(int par1);
		@SideOnly(Side.CLIENT)
		public void decrRenderTicks(int par1);
		@SideOnly(Side.CLIENT)
		public void resetRenderTicks();
		@SideOnly(Side.CLIENT)
		public XYZPos renderPosition();
		@SideOnly(Side.CLIENT)
		public void setRenderPosition(XYZPos pos);
		/**
		 * ダメージのレンダーを始める
		 * @param par1
		 */
		@SideOnly(Side.CLIENT)
		public void markStartRenderingDamage(boolean par1);
		@SideOnly(Side.CLIENT)
		public boolean isRendering();
		@SideOnly(Side.CLIENT)
		public void setRenderingDamage(int par1);
		@SideOnly(Side.CLIENT)
		public int getRenderingDamage();
		public void incrHealTimer(int incr);
	}



	public static class DefaultImpl implements ILifePoint{


		protected boolean isInitialized = false;
		protected int MaxLifePoint = 5;
		protected int healTimer = 0;
		protected float lifeSaturation = 0;
		protected int lifeRestoreTimer = 0;
		protected int LifePoint = 5;

		protected int hurtInterval = 5;
		@SideOnly(Side.CLIENT)
		protected boolean marked = false;
		@SideOnly(Side.CLIENT)
		protected int damage;
		@SideOnly(Side.CLIENT)
		protected int renderTick;
		@SideOnly(Side.CLIENT)
		protected XYZPos renderTextPos;
		@Override
		public void addLifePoint(int par1) {
			this.LifePoint += par1;
			if(this.LifePoint<0){
				this.LifePoint = 0;
			}


			if(this.LifePoint>this.MaxLifePoint){
				this.LifePoint = this.MaxLifePoint;
			}

			UnsagaMod.logger.trace(par1+"LP:"+this.LifePoint);
		}
		@Override
		public void decrLifePoint(int par1) {
			// TODO 自動生成されたメソッド・スタブ

			this.addLifePoint(-par1);
		}

		@Override
		public void decrRenderTicks(int par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.renderTick -= par1;
		}

		@Override
		public int hurtInterval() {
			// TODO 自動生成されたメソッド・スタブ
			return this.hurtInterval;
		}

		@Override
		public int lifePoint() {
			// TODO 自動生成されたメソッド・スタブ
			return this.LifePoint;
		}

		@Override
		public int maxLifePoint() {
			// TODO 自動生成されたメソッド・スタブ
			return this.MaxLifePoint;
		}


		@SideOnly(Side.CLIENT)
		@Override
		public int getRenderingDamage() {
			// TODO 自動生成されたメソッド・スタブ
			return this.damage;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public XYZPos renderPosition() {
			return renderTextPos;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public int renderTicks() {
			return renderTick;
		}



		public void init(EntityLivingBase living){

			if(!this.isInitialized){
				this.setMaxLifePoint(LPInitializeHelper.getLPFrom(living));
				this.restoreLifePoint();
				this.isInitialized = true;
			}


			//			if(living instanceof EntityPlayer){
			//				UnsagaMod.packetDispatcher.sendTo(PacketLPNew.create(living, this.getMaxLifePoint()), (EntityPlayerMP) living);
			//				if(living instanceof EntityTameable){
			//					EntityTameable tame = (EntityTameable) living;
			//					if(tame.getOwner() instanceof EntityPlayer){
			//						UnsagaMod.packetDispatcher.sendTo(PacketLPNew.create(living, this.getMaxLifePoint()), (EntityPlayerMP) tame.getOwner());
			//					}
			//				}
			//			}
		}

		@SideOnly(Side.CLIENT)
		@Override
		public boolean isRendering() {
			// TODO 自動生成されたメソッド・スタブ
			return this.marked;
		}


		@SideOnly(Side.CLIENT)
		@Override
		public void markStartRenderingDamage(boolean par1) {
			// TODO 自動生成されたメソッド・スタブ

			this.renderTick = RENDER_DAMAGE_TIME;
			this.marked = par1;
		}

		@Override
		public void resetRenderTicks() {
			// TODO 自動生成されたメソッド・スタブ
			this.renderTick = 0;
			this.markStartRenderingDamage(false);
			this.renderTextPos = null;
		}

		@Override
		public void restoreLifePoint() {
			// TODO 自動生成されたメソッド・スタブ

			this.setLifePoint(maxLifePoint());
		}

		@Override
		public void setHurtInterval(int par1) {
			// TODO 自動生成されたメソッド・スタブ

			this.hurtInterval = par1;
		}


		@Override
		public void setLifePoint(int lp) {
			// TODO 自動生成されたメソッド・スタブ

			this.LifePoint = lp;
		}

		@Override
		public void setMaxLifePoint(int par1) {
			// TODO 自動生成されたメソッド・スタブ

			this.MaxLifePoint = par1;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void setRenderingDamage(int par1) {
			// TODO 自動生成されたメソッド・スタブ

			this.damage = par1;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void setRenderPosition(XYZPos renderTextPos) {
			this.renderTextPos = renderTextPos;
		}

		@SideOnly(Side.CLIENT)
		@Override
		public void setRenderTicks(int renderTick) {
			this.renderTick = renderTick;
		}
		@Override
		public boolean hasInitialized() {
			// TODO 自動生成されたメソッド・スタブ
			return this.isInitialized;
		}
		@Override
		public void setInitialized(boolean par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.isInitialized = par1;
		}

		@Override
		public void incrHealTimer(int incr){
			this.healTimer +=incr;
		}

//		public void onHealed(EntityLivingBase liv,float amount){
//			//			UnsagaMod.logger.trace(this.getClass().getName(), "max",this.getLifePoint(),this.getMaxLifePoint());
//			if(liv.getHealth()>=liv.getMaxHealth() && this.getLifePoint()<this.getMaxLifePoint()){
//				float satu = this.getLifeSaturation();
//				this.setLifeSaturation((float) (satu+amount));
//				//                UnsagaMod.logger.trace(this.getClass().getName(), "saturation",this.getLifeSaturation());
//				if(this.getLifeSaturation()>=20.0F){
//					this.addLifePoint(1);
//					this.setLifeSaturation(0);
//					NBTTagCompound nbt = UtilNBT.compound();
//					nbt.setInteger("entityid", liv.getEntityId());
//					HSLib.getPacketDispatcher().sendToAll(PacketSyncCapability.create(CAPA, this,nbt));
//				}
//			}
//		}
//		@Override
//		public void onUpdate(EntityLivingBase liv){
//			if(WorldHelper.isClient(liv.getEntityWorld())){
//				return;
//			}
//			if(liv instanceof EntityPlayer){
//				this.playerNaturalHeal((EntityPlayer) liv);
//			}else{
//				if(UnsagaMod.configs.isEnabledLifePointSystem()){
//					this.incrHealTimer();
//					int healThreshold = HealTimerCalculator.calcHealTimer(liv);
//					if(this.getHealTimer()>=healThreshold){
//						liv.heal(1.0F);
//						this.setHealTimer(0);
//					}
//				}
//
//			}
//		}

//		private void playerNaturalHeal(EntityPlayer ep){
//			int healThreshold = HealTimerCalculator.calcHealTimer(ep);
//			if(ep.getFoodStats().getSaturationLevel()>0.0F && ep.getFoodStats().getFoodLevel()>=20 && ep.shouldHeal()){
//				this.incrHealTimer(1);
//				if(this.getHealTimer() >=MathHelper.clamp(healThreshold-50, 10, 65535)){
//					float f = Math.min(ep.getFoodStats().getSaturationLevel(), 4.0F);
//					ep.heal(f / 4.0F);
//					ep.getFoodStats().addExhaustion(f);
//					this.setHealTimer(0);
//					UnsagaMod.logger.trace(this.getClass().getName(), "healed");
//				}
//			}else if(ep.getFoodStats().getFoodLevel()>=18 && ep.shouldHeal()){
//				this.incrHealTimer(1);
//				UnsagaMod.logger.trace(this.getClass().getName(), "healed",this.getHealTimer());
//
//				if(this.getHealTimer()>=healThreshold){
//					ep.heal(1.0F);
//					ep.getFoodStats().addExhaustion(4.0F);
//					this.setHealTimer(0);
//
//				}
//			}
//		}
		//		public void onupd(EntityLivingBase living) {
		//			if(living.getHealth()>=living.getMaxHealth() && this.getLifePoint()<this.getMaxLifePoint()){
		//				this.lifeRestoreTimer ++;
		////				UnsagaMod.logger.trace("lifeTimer",this.lifeRestoreTimer);
		//				PotionEffect effect = living.getActivePotionEffect(UnsagaPotions.instance().lifeBoost);
		//				int threshold = effect==null ? 1000 : 1000 - (100*effect.getAmplifier());
		//				if(this.lifeRestoreTimer>threshold){
		//					this.lifeRestoreTimer = 0;
		//					this.addLifePoint(1);
		//					if(living instanceof EntityPlayer){
		//						((EntityPlayer) living).getFoodStats().addExhaustion(1.0F);
		//					}
		//				}
		//			}
		//
		//		}
		@Override
		public float lifeSaturation() {
			// TODO 自動生成されたメソッド・スタブ
			return this.lifeSaturation;
		}
		@Override
		public void setLifeSaturation(float par1) {
			// TODO 自動生成されたメソッド・スタブ
			this.lifeSaturation = par1;
		}
		@Override
		public NBTTagCompound getSendingData() {
			// TODO 自動生成されたメソッド・スタブ
			return (NBTTagCompound) CAPA.writeNBT(this, null);
		}
		@Override
		public void catchSyncData(NBTTagCompound nbt) {
			CAPA.readNBT(this, null, nbt);
		}

		private Entity getTargetEntity(World world,int entityid,PacketSyncCapability message, MessageContext ctx){
			if(message.getArgs().hasKey("isPlayer")){
				if(WorldHelper.isServer(world)){
					return ctx.getServerHandler().player;
				}else{
					return ClientHelper.getPlayer();
				}

			}else{
				return world.getEntityByID(entityid);
			}
		}
		@Override
		public void onPacket(PacketSyncCapability message, MessageContext ctx) {


			if(ctx.side==Side.SERVER){
				EntityPlayerMP serverPlayer = ctx.getServerHandler().player;
				int entityid = message.getArgs().getInteger("entityid");
				Optional.ofNullable(this.getTargetEntity(serverPlayer.world, entityid, message, ctx))
				.filter(in -> adapter.hasCapability(in))
				.map(in -> adapter.getCapability(in))
				.ifPresent(capability ->{
					HSLib.packetDispatcher().sendTo(PacketSyncCapability
							.create(CAPA, capability, message.getArgs()), serverPlayer);
				});


			}
			if(ctx.side==Side.CLIENT){
				boolean isRenderLP = message.getArgs().hasKey("isRenderLP") ? message.getArgs().getBoolean("isRenderLP") : false;
				int id = message.getArgs().getInteger("entityid");
				Optional.ofNullable(this.getTargetEntity(ClientHelper.getWorld(), id, message, ctx))
				.ifPresent(in ->{
					adapter.getCapabilityOptional(in)
					.ifPresent(cap ->{
						cap.catchSyncData(message.getNbt());
						if(isRenderLP){
							int damage = message.getArgs().getInteger("damage");
							cap.setRenderingDamage(damage);
							cap.markStartRenderingDamage(true);
						}
					});

				});

			}




		}
		@Override
		public String getIdentifyName() {
			// TODO 自動生成されたメソッド・スタブ
			return SYNC_ID;
		}
		@Override
		public int healTimer() {
			// TODO 自動生成されたメソッド・スタブ
			return this.healTimer;
		}
		@Override
		public void setHealTimer(int timer) {
			// TODO 自動生成されたメソッド・スタブ
			this.healTimer = timer;
		}

	}

	public static class Storage extends CapabilityStorage<ILifePoint>{



		@Override
		public void writeNBT(NBTTagCompound nbt, Capability<ILifePoint> capability, ILifePoint instance, EnumFacing side) {
			nbt.setInteger("LP", instance.lifePoint());
			nbt.setInteger("maxLP", instance.maxLifePoint());
			nbt.setBoolean("initialized", instance.hasInitialized());
			nbt.setFloat("saturation", instance.lifeSaturation());
		}

		@Override
		public void readNBT(NBTTagCompound comp, Capability<ILifePoint> capability, ILifePoint instance, EnumFacing side) {
			instance.setMaxLifePoint(comp.getInteger("maxLP"));
			//			UnsagaMod.logger.trace("lp read", comp.getInteger("LP"));
			instance.setLifePoint(comp.getInteger("LP"));
			instance.setInitialized(comp.getBoolean("initialized"));
			instance.setLifeSaturation(comp.getFloat("saturation"));
		}

	}

	public static void onUpdate(EntityLivingBase liv){
		LifePoint.adapter.getCapabilityOptional(liv)
		.filter(in ->WorldHelper.isServer(liv.getEntityWorld()))
		.ifPresent(capa ->{
			if(liv instanceof EntityPlayer){
				playerNaturalHeal((EntityPlayer) liv, capa);
			}else{
				//				if(UnsagaMod.configs.isEnabledLifePointSystem()){
				if(UnsagaConfigHandlerNew.GENERAL.enableEnemyAutoHeal){
					if(liv.ticksExisted % 10 == 0){
						capa.incrHealTimer(10);
						int healThreshold = HealTimerCalculator.calcHealTimer(liv);
						if(capa.healTimer()>=healThreshold){
							liv.heal(1.0F);
							capa.setHealTimer(0);
						}
					}
				}


				//				}

			}
		});

	}

	/** バニラの自然回復がオフになってる代わり*/
	private static void playerNaturalHeal(EntityPlayer ep,ILifePoint capability){
		int healThreshold = HealTimerCalculator.calcHealTimer(ep);
		if(ep.getFoodStats().getSaturationLevel()>0.0F && ep.getFoodStats().getFoodLevel()>=20 && ep.shouldHeal()){

			capability.incrHealTimer(1);
			if(capability.healTimer() >=MathHelper.clamp(healThreshold-50, 10, 65535)){
				float f = Math.min(ep.getFoodStats().getSaturationLevel(), 4.0F);
				ep.heal(f / 4.0F);
				ep.getFoodStats().addExhaustion(f);
				capability.setHealTimer(0);
				UnsagaMod.logger.trace(LifePoint.class.getClass().getName(), "healed");
			}
		}else if(ep.getFoodStats().getFoodLevel()>=18 && ep.shouldHeal()){
			capability.incrHealTimer(1);
			UnsagaMod.logger.trace(LifePoint.class.getClass().getName(), "healed",capability.healTimer());

			if(capability.healTimer()>=healThreshold){
				ep.heal(1.0F);
				ep.getFoodStats().addExhaustion(4.0F);
				capability.setHealTimer(0);

			}
		}
	}
	public static class LPHealEvent{

		@SubscribeEvent
		public void onHeal(LivingHealEvent e){
			EntityLivingBase liv = e.getEntityLiving();
			LifePoint.adapter.getCapabilityOptional(liv)
			.ifPresent(cap ->{
				if(liv.getHealth()>=liv.getMaxHealth()){ //体力が最大時
					float satu = cap.lifeSaturation();
					cap.setLifeSaturation((float) (satu+e.getAmount()));
					//                UnsagaMod.logger.trace(this.getClass().getName(), "saturation",this.getLifeSaturation());
					if(cap.lifeSaturation()>=UnsagaConfigHandlerNew.LP_SETTING.thresholdLPSaturation){ //ライフサチュレーションが一定値超えるとLP回復
						cap.addLifePoint(1);
						HSLib.packetDispatcher()
						.sendToAll(PacketSyncCapability.create(CAPA, cap
								,UtilNBT.comp().setInteger("entityid", liv.getEntityId()).get()));
					}
				}
			});
		}

	}

	/** LPの同期*/
	public static void onEntityJoin(EntityJoinWorldEvent in){
		if(WorldHelper.isClient(in.getWorld()) && in.getEntity() instanceof EntityLivingBase){
			EntityLivingBase living = (EntityLivingBase) in.getEntity();
			HSLib.packetDispatcher().sendToServer(PacketSyncCapability.createRequest(CAPA, adapter.getCapability(living)
					,UtilNBT.comp()
					.setBoolean("isPlayer", living instanceof EntityPlayer)
					.setInteger("entityid", living.getEntityId()).get()));
		}
	}
	public static void registerEvents(){
		HSLibs.registerEvent(new LPHealEvent());
		PacketSyncCapability.registerSyncCapability(SYNC_ID, CAPA);

		adapter.registerAttachEvent((inst,capa,facing,ev)->{
			if(!inst.hasInitialized() && ev.getObject() instanceof EntityLivingBase){
				inst.init((EntityLivingBase) ev.getObject());
			}
		});
	}

}
