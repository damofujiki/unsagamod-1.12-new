package mods.hinasch.unsaga.core.event;

import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import com.google.common.collect.Maps;

import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.entity.StateCapability;
import mods.hinasch.lib.network.PacketSound;
import mods.hinasch.lib.particle.ParticleSender;
import mods.hinasch.lib.util.ChatHandler;
import mods.hinasch.lib.util.VecUtil;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.AbilityAPI;
import mods.hinasch.unsaga.ability.AbilityShield;
import mods.hinasch.unsaga.ability.LearningAbilityEventHandler;
import mods.hinasch.unsaga.chest.EventUnlockMagic;
import mods.hinasch.unsaga.core.entity.UnsagaActionCapability;
import mods.hinasch.unsaga.core.entity.projectile.custom_arrow.CustomArrowCapability;
import mods.hinasch.unsaga.core.entity.projectile.custom_arrow.ICustomArrow;
import mods.hinasch.unsaga.core.entity.projectile.custom_arrow.SpecialArrowType;
import mods.hinasch.unsaga.core.inventory.AccessorySlotCapability;
import mods.hinasch.unsaga.core.item.weapon.ItemAxeUnsaga;
import mods.hinasch.unsaga.core.item.weapon.ItemGloveUnsaga;
import mods.hinasch.unsaga.core.item.weapon.ItemKnifeUnsaga;
import mods.hinasch.unsaga.core.item.weapon.ItemSpearUnsaga;
import mods.hinasch.unsaga.core.item.weapon.ItemStaffUnsaga;
import mods.hinasch.unsaga.core.item.wearable.ItemShieldUnsaga;
import mods.hinasch.unsaga.core.potion.PotionUnsaga;
import mods.hinasch.unsaga.core.potion.UnsagaPotionInitializer;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import mods.hinasch.unsaga.core.potion.state.StateCombo;
import mods.hinasch.unsaga.core.world.UnsagaWorldCapability;
import mods.hinasch.unsaga.damage.AdditionalDamage;
import mods.hinasch.unsaga.damage.AdditionalDamageCache;
import mods.hinasch.unsaga.damage.DamageHelper;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.Sub;
import mods.hinasch.unsaga.damage.LibraryItemDamageAttribute;
import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew;
import mods.hinasch.unsaga.lp.LPDecrCalculator;
import mods.hinasch.unsaga.lp.LifePoint;
import mods.hinasch.unsaga.lp.event.LPInvulnerabilityHandler;
import mods.hinasch.unsaga.material.UnsagaMaterialCapability;
import mods.hinasch.unsaga.minsaga.classes.PlayerClasses;
import mods.hinasch.unsaga.minsaga.event.ApplyAbilityHandler;
import mods.hinasch.unsaga.minsaga.event.FittingProgressHandler;
import mods.hinasch.unsaga.plugin.hac.UnsagaClimateDamageEvent;
import mods.hinasch.unsaga.skillpanel.SavingDamageHandler;
import mods.hinasch.unsaga.skillpanel.SkillPanelAPI;
import mods.hinasch.unsaga.skillpanel.SkillPanelEventHandler;
import mods.hinasch.unsaga.skillpanel.SkillPanels;
import mods.hinasch.unsaga.status.UnsagaStatus;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityEvokerFangs;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 *
 * Attack->Hurt->Damage->Deathの順番
 *
 */
public class DamageEventHandlerUnsaga {


	@SubscribeEvent
	public void onKnockback(LivingKnockBackEvent e){
		if(e.getAttacker() instanceof EntityLivingBase){ //短剣はノックバックを小さく
			ItemStack held = ((EntityLivingBase)e.getAttacker()).getHeldItemMainhand();
			Predicate<LivingKnockBackEvent> knifeKnockback = in ->{
				return !held.isEmpty() && held.getItem() instanceof ItemKnifeUnsaga;
			};
			Predicate<LivingKnockBackEvent> comboKnockback = in ->{
				return e.getEntityLiving().isPotionActive(UnsagaPotions.COMBO);
			};
			if(knifeKnockback.or(comboKnockback).test(e)){
				e.setStrength(e.getOriginalStrength()*0.5F);
			}
		}
	}
	@SubscribeEvent
	public void onLivingDeath(LivingDeathEvent e){
		if(LPInvulnerabilityHandler.onLivingDeath(e)){
			AccessorySlotCapability.onLivingDeath(e); //アクセサリ死亡時ばらまき
			LearningAbilityEventHandler.onLivingDeath(e); //アビリティの引き出し
			SkillPanelEventHandler.onLivingDeath(e); //主にネガティブスキル
		}

	}
	@SubscribeEvent
	public void onLivingAttack(LivingAttackEvent e){
		//		UnsagaMod.logger.trace("livingattack", "called");
		//バニラの盾メソッドを呼び出す
		if(!e.getSource().isUnblockable() && e.getEntityLiving().isActiveItemStackBlocking() && e.getEntityLiving() instanceof EntityPlayer){
			this.shieldProcess(e);
		}
		LearningAbilityEventHandler.onLivingAttack(e); //ひらめきイベント
		SavingDamageHandler.onAttack(e); //節約スキルの実装 TODO:ItemDamageEventに移動
		EventUnlockMagic.INSTANCE.onAttack(e); //魔法鍵のアンロック
	}
	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent e){
		//		UnsagaMod.logger.trace("livinghurt", "called");

		ItemGloveUnsaga.onLivingAttack(e);


		if(AdditionalDamageCache.getData(e.getSource())==null){
			AdditionalDamage data = figureBaseAdditionalDamage(e);
			figureDamageSubType(e.getSource(), data);
			AdditionalDamageCache.addCache(e.getSource(), data);
		}
		//		if(AdditionalDamageAttributeCapability.adapter.getCapability(e.getEntityLiving()).getData()==null){
		//			AdditionalDamageData data = new AdditionalDamageData(e.getSource(),General.PUNCH,1.0F);
		//			AdditionalDamageAttributeCapability.adapter.getCapability(e.getEntityLiving()).setData(data);
		//		}

		if(FittingProgressHandler.apply(e)){
			FittingProgressHandler.process(e); //武器補強改造の定着プロセス
		}


		UnsagaPotionInitializer.onEntityHurt(e); // 被ダメ時ポーション反応
		StateCapability.onEntityHurt(e); //ステイトの実装
		StateCombo.onDamage(e);
		ApplyAbilityHandler.onHurt(e); //ミンサガ補強アビリティの実装
	}

	@SubscribeEvent
	public void onLivingDamage(LivingDamageEvent e){
		//		UnsagaMod.logger.trace("livingdamage", "called");
		double rawDamage = e.getAmount();
		AdditionalDamage data = AdditionalDamageCache.getData(e.getSource());
		if(data!=null){

			//			if(UnsagaMod.configs.getDifficulty().enableDamageMultiplyToWounded() && !this.isVictimPlayerTeam(e.getEntityLiving())){
			//				if(e.getEntityLiving().getHealth()<=0.1F || LifePoint.adapter.getCapability(e.getEntityLiving()).getLifePoint()<=0){
			//					e.setAmount(e.getAmount()*1.5F);
			//				}
			//			}

			//			EntityMob
			//スキルパネルの影響
			SkillPanelEventHandler.onLivingDamage(e);
			//攻撃が複合属性の場合、防御力は最低の値を選ぶ
			double armor = DamageHelper.getEntityMinArmorValue(data, e.getEntityLiving());
			//攻撃が複合属性の時、防御力に差がある時は、最小と最大の間をとる
			if(armor<DamageHelper.getEntityMaxArmorValue(data, e.getEntityLiving())){
				armor = (armor + DamageHelper.getEntityMaxArmorValue(data, e.getEntityLiving()))/2;
			}
			//追加ダメージを計算
			float additional = getAdditionalDamageByStatus(1.0F,(float) armor,e.getAmount(),true);
			//反映されたダメージ
			float appliedDamage = MathHelper.clamp(e.getAmount() + additional, 0, 65535F);


			//精神による魔法攻撃の特防
			float mentalGuard = e.getSource().isMagicDamage() ? getAdditionalDamageByStatus(1.0F,(float)e.getEntityLiving().getEntityAttribute(UnsagaStatus.MENTAL).getAttributeValue(),e.getAmount(),true) : 0;
			//攻撃者の知性による特攻
			float intelligencePower = 0;
			if(e.getSource().getTrueSource() instanceof EntityLivingBase && e.getSource().isMagicDamage()){
				EntityLivingBase attacker = (EntityLivingBase) e.getSource().getTrueSource();
				intelligencePower = getAdditionalDamageByStatus(1.0F,(float)attacker.getEntityAttribute(UnsagaStatus.INTELLIGENCE).getAttributeValue(),e.getAmount(),false);
			}



			e.setAmount(appliedDamage+mentalGuard+intelligencePower);


			//ブロッキング
			if(UnsagaActionCapability.ADAPTER.getCapability(e.getEntityLiving()).getWeaponGuardProgress()>0 && !e.getSource().isUnblockable()){
				if(e.getSource().getImmediateSource() instanceof EntityLivingBase){
					EntityLivingBase enemy = (EntityLivingBase) e.getSource().getImmediateSource();
					VecUtil.knockback(e.getEntityLiving(), enemy, 0.8D, 0.1D);
				}
				HSLib.packetDispatcher().sendTo(PacketSound.atEntity(SoundEvents.BLOCK_ANVIL_LAND, e.getEntityLiving()), (EntityPlayerMP) e.getEntityLiving());
				e.setAmount(0);
				XYZPos p = XYZPos.createFrom(e.getEntityLiving());
				EntityXPOrb orb = new EntityXPOrb(e.getEntityLiving().getEntityWorld(), p.dx, p.dy,p.dz, 2);
				WorldHelper.safeSpawn(e.getEntityLiving().getEntityWorld(), orb);
			}

			//盾アビリティの発動。盾の向きは問わない
			if(e.getEntityLiving().isActiveItemStackBlocking() && e.getEntityLiving() instanceof EntityPlayer){
				this.shieldAbilityProcess(e, data);
			}

			if(!e.getEntityLiving().getActivePotionEffects().isEmpty()){
				e.getEntityLiving().getActivePotionEffects().stream()
				.filter(in ->in.getPotion() instanceof PotionUnsaga)
				.forEach(in ->{
					PotionUnsaga pu = (PotionUnsaga) in.getPotion();
					pu.onDamage(e,data,in.getAmplifier());
				});
			}

			Optional.of(e.getSource().getTrueSource())
			.filter( in -> in instanceof EntityLivingBase)
			.map(in ->(EntityLivingBase)in)
			.filter(in -> !in.getActivePotionEffects().isEmpty())
			.ifPresent(attacker ->{
				attacker.getActivePotionEffects().stream()
				.filter(ef -> ef.getPotion() instanceof PotionUnsaga)
				.forEach(effect ->{
					PotionUnsaga pu = (PotionUnsaga) effect.getPotion();
					pu.affectOnAttacked(e,data,effect.getAmplifier());
				});

			});


			//LPシステム不採用時の仕様。ミスヒット＆クリティカル
			if(!UnsagaConfigHandlerNew.GENERAL.enableLPSystem && UnsagaConfigHandlerNew.GENERAL.enableCritical){
				if(!e.getSource().isMagicDamage() && e.getSource().getTrueSource() instanceof EntityLivingBase){
					EntityLivingBase attacker = (EntityLivingBase) e.getSource().getTrueSource();
					double dex = attacker.getEntityAttribute(UnsagaStatus.DEXTALITY).getAttributeValue();
					//					double res = attacker.getEntityAttribute(UnsagaStatus.RESISTANCE_LP_HURT).getAttributeValue();
					Random rand = attacker.getRNG();
					UnsagaMod.logger.trace("crit", dex);
					if(dex>=1.0D){ //技が1以上->クリティカルが発生(15%)
						float crit = 0;
						double critModifier = Stream.generate(()->rand.nextFloat())
								.limit(data.getLPAttribute().chances())
								.filter(chance -> chance<0.15F)
								.mapToDouble(in ->e.getAmount() * data.getLPAttribute().amount())
								.sum();

						if(crit>0){
							ParticleSender.sendParticle(attacker.world, XYZPos.createFrom(e.getEntityLiving()), EnumParticleTypes.CRIT, 15);
							e.setAmount(e.getAmount() + (float)MathHelper.clamp(critModifier, 0, 255.0D));
						}

					}else{ //技が1未満->ミスヒットが発生(40%)
						if(attacker.getRNG().nextFloat()<0.4F){
							ParticleSender.sendParticle(attacker.world, XYZPos.createFrom(e.getEntityLiving()), EnumParticleTypes.VILLAGER_ANGRY, 15);
							e.setAmount(e.getAmount() * 0.5F);
						}
					}

				}

			}



			String mes1 = "Attacker:%s Source:%s Class:%s Victim Name:%s DamageSource:%s";
			String mes2 = "Raw:%.2f Amount:%.2f LP攻撃力:%.2f LP攻撃回数:%d,Type:%s(%s)";
			String mes3 = "残りHealth:%.2f 残りLP:%d 防御力:%.2f ";
			String mes4 = "能力:斬%.2f 殴%.2f 突%.2f 魔%.2f 精神特防:%.2f 攻撃者知性特攻:%.2f";
			//デバッグ用
			EntityPlayer ep = Optional.ofNullable(e.getSource().getTrueSource())
					.filter(in ->in instanceof EntityPlayer)
					.map(in ->(EntityPlayer)in)
					.orElse(e.getEntityLiving() instanceof EntityPlayer ? (EntityPlayer)e.getEntityLiving() : null);

			if(ep!=null && HSLib.isDebug()){
				String itemclass = "unknown";
				UnsagaMod.logger.splitter("attacker");
				UnsagaMod.logger.trace("source", e.getSource().getTrueSource(),e.getSource().getImmediateSource());
				if(e.getSource().getTrueSource() instanceof EntityLivingBase){
					EntityLivingBase attacker = (EntityLivingBase)e.getSource().getTrueSource();
					itemclass = attacker.getHeldItemMainhand().isEmpty() ? "empty" : attacker.getHeldItemMainhand().getItem().getClass().getSimpleName().toString();
				}

				ChatHandler.sendChatToPlayer(ep, String.format(mes1, getEntityNameSafe(e.getSource().getTrueSource()),getEntityNameSafe(e.getSource().getImmediateSource()),itemclass,EntityList.getEntityString(e.getEntityLiving()),e.getSource().toString()));
				ChatHandler.sendChatToPlayer(ep, String.format(mes2,rawDamage, e.getAmount(),data.getLPAttribute().amount(),data.getLPAttribute().chances(),data.getDamageTypes(),data.getSubDamageTypes()));
				ChatHandler.sendChatToPlayer(ep, String.format(mes3, e.getEntityLiving().getHealth(),LifePoint.adapter.getCapability(e.getEntityLiving()).lifePoint(),armor));
				EntityLivingBase el = e.getEntityLiving();
				ChatHandler.sendChatToPlayer(ep, String.format(mes4, el.getEntityAttribute(UnsagaStatus.GENERALS.get(General.SWORD)).getAttributeValue(),el.getEntityAttribute(UnsagaStatus.GENERALS.get(General.PUNCH)).getAttributeValue()
						,el.getEntityAttribute(UnsagaStatus.GENERALS.get(General.SPEAR)).getAttributeValue(),el.getEntityAttribute(UnsagaStatus.GENERALS.get(General.MAGIC)).getAttributeValue()
						,mentalGuard,intelligencePower));
			}




			//LP操作
			if(UnsagaConfigHandlerNew.GENERAL.enableLPSystem){
				LPDecrCalculator.tryDecrLP(e.getEntityLiving().getRNG(), e.getEntityLiving(), e.getSource(), e.getAmount(),data);
			}

			//追加属性のキャッシュ消去
			AdditionalDamageCache.removeCache(e.getSource());
		}

		//		AdditionalDamageData data = AdditionalDamageAttributeCapability.adapter.getCapability(e.getEntityLiving()).getData();
		//		AdditionalDamageAttributeCapability.adapter.getCapability(e.getEntityLiving()).setData(null);
	}

	//	private Optional<EntityLivingBase> findAttacker(DamageSource ds){
	//		if(ds.getTrueSource()!=null){
	//			if(ds.getTrueSource() instanceof EntityLivingBase){
	//				return Optional.of((EntityLivingBase)ds.getTrueSource());
	//			}
	//		}else{
	//			if(ds.getImmediateSource() instanceof EntityLivingBase){
	//				return Optional.of((EntityLivingBase)ds.getImmediateSource());
	//			}else{
	//				return Optional.empty();
	//
	//			}
	//		}
	//		return Optional.empty();
	//	}
	/**
	 * 盾スキルがあるとスキルが発動
	 * @param e
	 * @param data
	 */
	private void shieldAbilityProcess(LivingDamageEvent e,AdditionalDamage data){
		//		UnsagaMod.logger.trace("cllaed", "call");
		EntityPlayer ep = (EntityPlayer) e.getEntityLiving();
		ItemStack shield = ep.getActiveItemStack();
		Random rand = ep.getRNG();
		//盾アビリティを持っているか

		//			UnsagaMod.logger.trace("cllaed", "call2");

		UnsagaMaterialCapability.adapter.getCapabilityOptional(shield)
		.filter(in ->SkillPanelAPI.hasPanel(ep, SkillPanels.SHIELD))
		.filter(in ->shield.getItem() instanceof ItemShieldUnsaga)
		.map(in ->in.getMaterial())
		.ifPresent(material ->{
			//発動率は盾の基礎能力+5*スキルレベル
			int chance = material.shieldValue() +(5 * SkillPanelAPI.getHighestPanelLevel(ep, SkillPanels.SHIELD).orElse(0));
			this.reduceDamage(e, chance);

			AbilityAPI.getMatchedShieldAbility(shield, data).stream()
			.reduce((a,b)->a.getBlockableValue()>b.getBlockableValue() ? a : b)
			.ifPresent(ability ->{
				this.tryBlockDamage(e, shield, ability, data, ep, chance);
			});
		});



	}

	private void tryBlockDamage(LivingDamageEvent e,ItemStack shield,AbilityShield ability,AdditionalDamage data,EntityPlayer ep,int prob){


		Random rand = ep.getRNG();
		UnsagaWorldCapability.ADAPTER.getCapabilityOptional(ep.world)
		.map(in ->in.playerClassStorage())
		.ifPresent(classManager ->{
			int r = classManager.getClass(ep)==PlayerClasses.HEAVY_FIGHTER ? 80 : 100;
			//マッチするアビリティの中から一番発動率の高いアビリティが発動したかどうか
			if(prob*ability.getBlockableValue()>rand.nextInt(r)){
				HSLib.packetDispatcher().sendTo(PacketSound.atEntity(SoundEvents.ITEM_SHIELD_BLOCK, ep), (EntityPlayerMP) ep);
				ItemShieldUnsaga.damageShield(ep, e.getAmount());
				e.setAmount(0);
			}
		});


	}
	private void reduceDamage(LivingDamageEvent e,int shieldValue){
		//軽減率はバニラの33%+基礎盾能力の1%
		float reduce = (AbilityShield.VANILLA_BLOCK_VALUE + shieldValue) * AbilityShield.SHIELD_RATIO;
		if(!e.getSource().isUnblockable()){
			e.setAmount(e.getAmount()*reduce);
		}
	}
	private void shieldProcess(LivingAttackEvent e){


		EntityPlayer ep = (EntityPlayer) e.getEntityLiving();
		ItemStack stack = e.getEntityLiving().getActiveItemStack();
		//		if(stack.getItem() instanceof ItemShieldUnsaga && ItemShieldUnsaga.canBlockDamageSource(ep, e.getSource())){
		//			if(UnsagaMaterialCapability.adapter.hasCapability(stack)){
		//				//					int shieldPower = UnsagaMaterialCapability.adapter.getCapability(stack).getMaterial().getShieldPower();
		//				//					float reduce = (1+shieldPower*shieldPower)/5000;
		//				//					reduce = (float) MathHelper.clamp(reduce, 0.1D, 0.9D);
		//				//					float shieldDamage = e.getAmount() - (e.getAmount()*reduce);
		//				if(e.getAmount()>0.0D){
		//
		//					Method method = ReflectionHelper.findMethod(EntityPlayer.class, "damageShield", "func_184590_k", Float.class);
		////					ItemShieldUnsaga.damageShield(ep, e.getAmount());
		//					try {
		//						method.invoke(e.getAmount());
		//					} catch (IllegalAccessException e1) {
		//						// TODO 自動生成された catch ブロック
		//						e1.printStackTrace();
		//					} catch (IllegalArgumentException e1) {
		//						// TODO 自動生成された catch ブロック
		//						e1.printStackTrace();
		//					} catch (InvocationTargetException e1) {
		//						// TODO 自動生成された catch ブロック
		//						e1.printStackTrace();
		//					}
		//				}
		//
		//			}
		//
		//
		//		}
	}

	/**
	 * 元ダメージから追加ダメージを計算する。ダメージx(しきい値-value)として帰ってくる
	 * @param threshold 初期値・しきい値、これを堺にネガティブ・ポジティブになる値
	 * @param value ここにアーマー値などを入れる
	 * @param damage 元のダメージ値
	 * @param isArmorStatus trueにすると負の値で帰ってくる（アーマー値は上がるほど減るので）
	 * @return
	 */
	private float getAdditionalDamageByStatus(float threshold,float value,float damage,boolean isArmorStatus){
		float f = threshold - value;
		if(!isArmorStatus){
			return -(damage*f);
		}
		return damage * f;

	}
	private boolean isVictimPlayerTeam(EntityLivingBase victim){
		if(victim instanceof EntityPlayer){
			return true;
		}
		if(victim instanceof EntityTameable){
			if(((EntityTameable) victim).getOwner() instanceof EntityPlayer){
				return true;
			}
		}
		return false;
	}

	//デバグ用
	private String getEntityNameSafe(Entity el){
		return el!=null ? el.getName() : "NULL";
	}

	//バニラデータ・他modからの推測
	public static AdditionalDamage figureBaseAdditionalDamage(LivingHurtEvent e){
		final DamageSource ds = e.getSource();
		//		UnsagaMod.logger.trace("damagesource", e.getSource());
		if(HSLib.plugin().isLoadedHAC()){
			//			UnsagaMod.logger.trace("damagesource", "called");
			if(UnsagaClimateDamageEvent.onfigureBaseAdditionalDamage(e)!=null){
				return UnsagaClimateDamageEvent.onfigureBaseAdditionalDamage(e);
			}
		}

		if(ds==DamageSource.CACTUS){
			return new AdditionalDamage(ds,General.SPEAR, 0.1F,1);
		}
		if(ds==DamageSource.ON_FIRE){
			return new AdditionalDamage(ds,General.MAGIC, 0F).setSubTypes(Sub.FIRE);
		}
		if(ds==DamageSource.IN_FIRE){
			return new AdditionalDamage(ds,General.MAGIC, 0F).setSubTypes(Sub.FIRE);
		}
		if(ds==DamageSource.LAVA){
			return new AdditionalDamage(ds,General.MAGIC, 0.1F).setSubTypes(Sub.FIRE);
		}
		if(ds==DamageSource.FIREWORKS){
			return new AdditionalDamage(ds,General.MAGIC, 0.1F).setSubTypes(Sub.FIRE);
		}
		if(ds==DamageSource.HOT_FLOOR){
			return new AdditionalDamage(ds,General.MAGIC, 0.1F).setSubTypes(Sub.FIRE);
		}
		if(ds==DamageSource.DRAGON_BREATH){
			return new AdditionalDamage(ds,General.MAGIC, 0.1F).setSubTypes(Sub.FIRE,Sub.SHOCK);
		}
		if(ds==DamageSource.LIGHTNING_BOLT){
			return new AdditionalDamage(ds,General.MAGIC, 0.1F).setSubTypes(Sub.ELECTRIC);
		}
		if(ds==DamageSource.WITHER){
			return new AdditionalDamage(ds,General.MAGIC, 0F).setSubTypes(Sub.SHOCK);
		}
		if(ds==DamageSource.STARVE){
			return new AdditionalDamage(ds,General.MAGIC, 0F);
		}
		if(ds==DamageSource.FALL){
			return new AdditionalDamage(ds,General.PUNCH, 3.0F,2);
		}
		if(ds==DamageSource.ANVIL){
			return new AdditionalDamage(ds,General.PUNCH, 3.0F,2);
		}
		if(ds.getTrueSource()==null && ds.isMagicDamage()){
			return new AdditionalDamage(ds,General.MAGIC, 0.0F,1);
		}
		if(ds.getImmediateSource() instanceof EntityArrow){
			if(CustomArrowCapability.ADAPTER.hasCapability(ds.getImmediateSource())){
				EntityArrow arrow = (EntityArrow) ds.getImmediateSource();
				ICustomArrow instance = CustomArrowCapability.ADAPTER.getCapability(arrow);
				if(instance.getArrowType()!=SpecialArrowType.NONE && ds.getTrueSource() instanceof EntityLivingBase){
					instance.getArrowType().onLivingHurt(e);
					return instance.getArrowType().getDamageSource((EntityLivingBase) ds.getTrueSource(), arrow);
				}
			}
			return new AdditionalDamage(ds,General.SPEAR,0.65F);
		}
		if(ds.getImmediateSource() instanceof IProjectile){
			Entity throwable = ds.getImmediateSource();
			if(LibraryItemDamageAttribute.instance().find(throwable).isPresent()){
				return LibraryItemDamageAttribute.instance().find(throwable).get().toAdditionalDamage(ds);
			}
			return new AdditionalDamage(ds,0.5F,1,General.PUNCH,General.SPEAR);
		}
		if(ds.isExplosion()){
			return new AdditionalDamage(ds,General.MAGIC,1.5F,2).setSubTypes(Sub.FIRE,Sub.SHOCK);
		}
		if(ds.getImmediateSource() instanceof EntityPotion){
			return new AdditionalDamage(ds,General.MAGIC,0.5F,1);
		}
		if(ds.getDamageType().equals("thorns")){
			return new AdditionalDamage(ds,0.4F,1,General.SPEAR);
		}
		if(ds.getImmediateSource() instanceof EntityEvokerFangs){
			return new AdditionalDamage(ds,0.5F,1,General.SWORD,General.PUNCH);
		}
		if(ds.getTrueSource() instanceof EntitySpider){
			return new AdditionalDamage(ds,0.5F,1,General.SWORD,General.PUNCH);
		}
		if(ds.getTrueSource() instanceof EntityGuardian){
			return new AdditionalDamage(ds,General.MAGIC,0.5F).setSubTypes(Sub.SHOCK,Sub.ELECTRIC);
		}
		if(ds.getTrueSource() instanceof EntityLivingBase){
			EntityLivingBase living = (EntityLivingBase) ds.getTrueSource();
			ItemStack held = living.getHeldItemMainhand();
			if(held.isEmpty()){ //素手のとき
				return new AdditionalDamage(ds,General.PUNCH,0F);
			}

			//			ResourceLocation clazz = new ResourceLocation(held.getItem().getClass().getSimpleName());
			if(LibraryItemDamageAttribute.instance().find(held).isPresent()){
				LibraryItemDamageAttribute.Attributes data = LibraryItemDamageAttribute.instance().find(held).get();
				return data.toAdditionalDamage(ds);
			}
			//			if(map.containsKey(clazz)){
			//				return map.get(clazz).apply(ds);
			//			}

			if(held.getItem() instanceof ItemGloveUnsaga){
				return new AdditionalDamage(ds,0.3F,1,General.PUNCH);
			}
			if(held.getItem() instanceof ItemAxe){
				return new AdditionalDamage(ds,0.6F,1,General.PUNCH,General.SWORD);
			}
			if(held.getItem() instanceof ItemStaffUnsaga){
				return new AdditionalDamage(ds,0.4F,1,General.PUNCH);
			}
			if(held.getItem() instanceof ItemSpearUnsaga){
				return new AdditionalDamage(ds,0.65F,1,General.SPEAR);
			}
			if(held.getItem() instanceof ItemKnifeUnsaga){
				return new AdditionalDamage(ds,0.7F,1,General.SPEAR);
			}
			if(held.getItem() instanceof ItemAxeUnsaga){
				return new AdditionalDamage(ds,0.6F,1,General.PUNCH,General.SWORD);
			}
			if(held.getItem() instanceof ItemSword){
				return new AdditionalDamage(ds,0.5F,1,General.SWORD);
			}
			if(held.getItem() instanceof ItemPickaxe){
				return new AdditionalDamage(ds,0.2F,1,General.SPEAR,General.PUNCH);
			}
			if(held.getItem() instanceof Item){
				if(ds.isMagicDamage()){
					return new AdditionalDamage(ds,0.5F,1,General.MAGIC);
				}
				return new AdditionalDamage(ds,0.5F,1,General.SWORD);
			}
		}

		if(ds.isMagicDamage()){
			return new AdditionalDamage(ds,0.5F,1,General.MAGIC);
		}
		return new AdditionalDamage(ds,0.5F,1,General.SWORD);
	}

	public static void figureDamageSubType(DamageSource ds,AdditionalDamage ad){
		if(ds.isFireDamage()){
			ad.setSubTypes(Sub.FIRE);
		}
		if(ds.getTrueSource() instanceof EntityLivingBase){
			EntityLivingBase el = (EntityLivingBase) ds.getTrueSource();
			ItemStack held = el.getHeldItemMainhand();
			if(!held.isEmpty()){
				if(EnchantmentHelper.getFireAspectModifier(el)>0){
					ad.setSubTypes(Sub.FIRE);
				}
			}
		}
	}

	public static Map<ResourceLocation,Function<DamageSource,AdditionalDamage>> map = Maps.newHashMap();
	static{
		put("BattleAxe", ds -> new AdditionalDamage(ds,0.5F,1,General.PUNCH,General.SWORD));
		put("BattleSign", ds -> new AdditionalDamage(ds,0.3F,1,General.PUNCH));
		put("BroadSword", ds -> new AdditionalDamage(ds,0.5F,1,General.SWORD));
		put("Cleaver", ds -> new AdditionalDamage(ds,0.5F,1,General.PUNCH,General.SWORD));
		put("FryPan", ds -> new AdditionalDamage(ds,0.3F,1,General.PUNCH));
		put("LongSword", ds -> new AdditionalDamage(ds,0.5F,1,General.SWORD));
		put("Rapier", ds -> new AdditionalDamage(ds,0.6F,1,General.SPEAR));
		put("Excavotor", ds -> new AdditionalDamage(ds,0.5F,1,General.SWORD));
		put("Hammer", ds -> new AdditionalDamage(ds,0.3F,1,General.PUNCH));
		put("Hatchet", ds -> new AdditionalDamage(ds,0.6F,1,General.SPEAR));
		put("Kama", ds -> new AdditionalDamage(ds,0.5F,1,General.SWORD));
		put("LumberAxe", ds -> new AdditionalDamage(ds,0.5F,1,General.PUNCH,General.SWORD));
		put("Mattock", ds -> new AdditionalDamage(ds,0.5F,1,General.PUNCH,General.SPEAR));
		put("Pickaxe", ds -> new AdditionalDamage(ds,0.5F,1,General.PUNCH,General.SPEAR));
		put("Scythe", ds -> new AdditionalDamage(ds,0.5F,1,General.SWORD));
		put("Shovel", ds -> new AdditionalDamage(ds,0.3F,1,General.PUNCH));
		put("Arrow", ds -> new AdditionalDamage(ds,0.6F,1,General.SPEAR));
		put("Bolt", ds -> new AdditionalDamage(ds,0.6F,1,General.SPEAR));
		put("Shuriken", ds -> new AdditionalDamage(ds,0.6F,1,General.SPEAR));
	}

	public static void put(String str,Function<DamageSource,AdditionalDamage> func){
		map.put(new ResourceLocation(str), func);
	}
}
