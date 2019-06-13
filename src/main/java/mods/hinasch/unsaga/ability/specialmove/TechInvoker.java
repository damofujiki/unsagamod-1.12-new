package mods.hinasch.unsaga.ability.specialmove;

import java.util.Optional;

import mods.hinasch.lib.container.inventory.InventoryHandler;
import mods.hinasch.lib.container.inventory.InventoryHandler.InventoryStatus;
import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.iface.IExtendedReach;
import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.ability.AbilityCapability;
import mods.hinasch.unsaga.ability.specialmove.action.TechActionBase;
import mods.hinasch.unsaga.ability.specialmove.action.TechArrow;
import mods.hinasch.unsaga.common.specialaction.ActionPerformerBase;
import mods.hinasch.unsaga.core.item.misc.ItemTechBook;
import mods.hinasch.unsaga.core.item.weapon.ItemBowUnsaga;
import mods.hinasch.unsaga.core.potion.UnsagaPotionInitializer;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import mods.hinasch.unsaga.damage.DamageComponent;
import mods.hinasch.unsaga.skillpanel.SkillPanelAPI;
import mods.hinasch.unsaga.skillpanel.SkillPanels;
import mods.hinasch.unsaga.status.AdditionalMPAdaptUtil;
import mods.hinasch.unsaga.util.ToolCategory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class TechInvoker extends ActionPerformerBase<Tech,TechActionBase>{

	/**技の起動方法	 */
	public static enum InvokeType{
		NONE("none"),USE("use"),CHARGE("charge"),RIGHTCLICK("right_click")
		,BOW("bow"),SPRINT_RIGHTCLICK("sprint_right_click"),
		/** 一定の条件で右クリ→溜めに変わる、今の所乱れ雪月花のみ使用*/
		RIGHTCLICK_TO_CHARGE("right_click_to_charge");

		private final String name;
		private InvokeType(String name){
			this.name = name;
		}
		public String getName(){
			return this.name;
		}
	}

	public static final float PROB_MASTER_TECH = 0.2F;

	int usingTime = 0;
	InvokeType invokeType = InvokeType.NONE;
	EnumFacing facing = EnumFacing.DOWN;
	Optional<ComponentArrow> arrowEntity = Optional.empty();
	public static enum ArtifactType {NON_PLAYER,MARTIAL_ARTS,WEAPON_ARTS,ARTIFACT_NOT_FOUND};

	public TechInvoker(World world, EntityLivingBase performer,Tech move) {
		super(world, performer,move);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public InvokeType getInvokeType(){
		return this.invokeType;
	}

	public void setArrowComponent(EntityArrow arrow,ItemStack arrowStack){
		this.arrowEntity = Optional.of(new ComponentArrow(arrowStack,arrow));
	}

	public Optional<ComponentArrow> getArrowComponent(){
		return this.arrowEntity;
	}
	public void swingMainHand(boolean swingSound,boolean sweepParticle){
		this.getPerformer().swingArm(EnumHand.MAIN_HAND);
		if(swingSound){
			this.playSound(XYZPos.createFrom(getPerformer()), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, false);

		}
		if(sweepParticle){
			this.spawnSweepParticle();
		}
	}
	public void spawnSweepParticle(){
		if(this.getPerformer() instanceof EntityPlayer){
			((EntityPlayer)this.getPerformer()).spawnSweepParticles();
		}
	}

	public TechInvoker setUseFacing(EnumFacing facing){
		this.facing = facing;
		return this;
	}

	public EnumFacing getUseFacing(){
		return this.facing;
	}

	public TechInvoker setInvokeType(InvokeType type){
		this.invokeType = type;
		return this;
	}

	public float getReach(){
		if(this.article.isPresent()){
			if(this.article.get().getItem() instanceof IExtendedReach){
				return ((IExtendedReach)this.article.get().getItem()).getReach();
			}
		}
		return 4.0F;
	}

	public int getChargedTime(){
		return this.usingTime;
	}

	public DamageComponent getActionStrength(){
		return this.getActionProperty().getStrength();
	}
	public TechInvoker setChargedTime(int time){
		this.usingTime = time;
		return this;
	}
	@Override
	public TechActionBase getAction() {
		// TODO 自動生成されたメソッド・スタブ
		return this.getActionProperty().getAction();
	}


	public ArtifactType getArtifactType(){
		if(this.getPerformer() instanceof EntityPlayer){
			if(this.getArtifact().isPresent()){
				if(ToolCategory.fromItem(this.getArtifact().get().getItem())==ToolCategory.GLOVES){
					return ArtifactType.MARTIAL_ARTS;
				}else{
					return ArtifactType.WEAPON_ARTS;
				}

			}else{
				return ArtifactType.ARTIFACT_NOT_FOUND;
			}
		}
		return ArtifactType.NON_PLAYER;
	}
	public void invoke(){
		if(this.canInvoke()){
			//技発動中状態にする（ロック）
			StateCapability.ADAPTER.getCapabilityOptional(getPerformer()).ifPresent(in -> in.addState(new PotionEffect(UnsagaPotions.ACTION_PROGRESS,3000,0)));
			//			this.getAction().getPrePerform().accept(this);
			EnumActionResult result = this.getAction().perform(this);

			if(result!=EnumActionResult.PASS){ //リサルトがPASS以外の場合（成功時）
				//クールタイムの付与
				if(this.getActionProperty().getCoolingTime()>0){
					int cooling = this.getActionProperty().getCoolingTime();
					UnsagaPotionInitializer.setCooling(this.getPerformer(), cooling);
				}
				this.consumeCost();
				if(WorldHelper.isServer(getWorld()) && this.getWorld().rand.nextFloat()<=PROB_MASTER_TECH){
					this.masterTech();
				}
			}
		}

		StateCapability.ADAPTER.getCapabilityOptional(getPerformer()).ifPresent(in -> in.removeState(UnsagaPotions.ACTION_PROGRESS));
	}

	/** 攻撃者の生の攻撃力（entityattributeから）を得る。弓技の場合は弓の固有の攻撃力*/
	public float getRawInvokerStrength(){
		if(this.getAction() instanceof TechArrow && this.getArtifact().isPresent()){
			return ItemBowUnsaga.getDamageModifier(this.getArtifact().get());
		}
		if(this.getPerformer().getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE)!=null){
			return (float) this.getPerformer().getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
		}
		return 1.0F;
	}

	/**
	 * 攻撃者のRawな攻撃力＋技の攻撃力を足した値を返す。
	 * {@link #getStrength()}から参照される
	 * @return
	 */
	public float getStrengthHP(){
		float at = this.getRawInvokerStrength() + this.getActionProperty().getStrength().hp();
		return MathHelper.clamp(at, 0.1F, at);
	}
	public boolean canInvoke(){
		//
		//		if(this.getTargetType()==TargetType.TARGET){
		//			if(TargetHolderCapability.adapter.hasCapability(getPerformer())){
		//				if(!TargetHolderCapability.adapter.getCapability(getPerformer()).getTarget().isPresent()){
		//					return false;
		//				}
		//			}
		//		}

		//クールダウン中なら使えない
		if(this.getPerformer().isPotionActive(UnsagaPotions.COOLDOWN)){
			return false;
		}

		if(this.getPerformer() instanceof EntityPlayer){
			if(this.getArtifact().isPresent()){ //マジックアイテムが存在する場合
				return this.checkItemCost();
			}else{ //マジックアイテムが無い場合、本人の力を消費
				return AdditionalMPAdaptUtil.canConsume((EntityPlayer) this.getPerformer(), getCost());
			}

		}



		return false;
	}

	public float getCostReduction(){
		if(SkillPanelAPI.hasPanel(getPerformer(), SkillPanels.WEAPON_MASTER)){
			return 0.13F * SkillPanelAPI.getHighestPanelLevel(getPerformer(), SkillPanels.WEAPON_MASTER).getAsInt();
		}
		return 0.0F;
	}

	@Override
	public int getCost(){
		int cost = this.getActionProperty().getCost();
		cost  -= (int) ((float)this.getActionProperty().getCost() * this.getCostReduction());
		return MathHelper.clamp(cost, 1, cost);

	}

	/** 技の極意を得る*/
	private void masterTech(){
		if(!(this.getPerformer() instanceof EntityPlayer)){
			return;
		}
		EntityPlayer ep = (EntityPlayer) this.getPerformer();
		if(SkillPanelAPI.hasPanel(ep, SkillPanels.WEAPON_MASTER)){
			this.findEmptyWazaBook(ep).ifPresent(in ->{
				ItemStack book = in.getStack();
				AbilityCapability.adapter.getCapability(book).getAbilitySlots().updateSlot(0, getActionProperty());
				this.playSound(XYZPos.createFrom(ep), SoundEvents.BLOCK_ANVIL_PLACE, false);
				this.broadCastMessage(HSLibs.translateKey("msg.mastered.specialMove",this.getActionProperty().getLocalized()));
			});

		}


	}

	private Optional<InventoryStatus> findEmptyWazaBook(EntityPlayer ep){
		return InventoryHandler.of(ep.inventory).toStream(0, ep.inventory.getSizeInventory())
				.filter(in -> ItemUtil.isItemStackPresent(in.getStack())).filter(in -> in.getStack().getItem() instanceof ItemTechBook)
				.filter(in -> AbilityCapability.adapter.getCapability(in.getStack()).getAbilitySlots().isAllEmpty()).findFirst();
	}
	private void consumeCost(){
		switch(this.getArtifactType()){
		case MARTIAL_ARTS:
		case ARTIFACT_NOT_FOUND:
			AdditionalMPAdaptUtil.consumeMPByExhaustion((EntityPlayer) this.getPerformer(), getCost());
			break;
		case NON_PLAYER:
			break;
		case WEAPON_ARTS:

			int cost = this.getActionProperty()==Techs.FINAL_STRIKING ? this.getArtifact().get().getMaxDamage() : this.getCost();
			this.getArtifact().get().damageItem(cost, this.getPerformer());
			break;
		default:
			break;

		}
	}


	@Override
	public DamageComponent getStrength() {
		// TODO 自動生成されたメソッド・スタブ
		return DamageComponent.of(this.getStrengthHP(), this.getActionStrength().lp());
	}

	public static class ComponentArrow{
		public final ItemStack arrowStack;
		public final EntityArrow arrowEntity;

		public ComponentArrow(ItemStack stack,EntityArrow e){
			this.arrowEntity = e;
			this.arrowStack = stack;
		}
	}
}
