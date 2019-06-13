package mods.hinasch.unsaga.ability;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import mods.hinasch.lib.registry.RegistryUtil;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.specialmove.TechInitializer;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.Sub;
import mods.hinasch.unsaga.element.FiveElements;
import mods.hinasch.unsaga.status.UnsagaStatus;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

public class AbilityInitializer {

	public static final ResourceLocation ABILITIES = new ResourceLocation(UnsagaMod.MODID,"abilities");


	private static IForgeRegistry<IAbility> REGISTRY;

//	public Techs specialArts;
//	private AbilityPotentialTableProvider association;
	private static void registerModifiers(){

//		Abilities.HEAL_DOWN1.registerAttributeModifier(UnsagaStatus.HEAL_THRESHOLD, 5D);
//		Abilities.HEAL_DOWN2.registerAttributeModifier(UnsagaStatus.HEAL_THRESHOLD, 10D);
//		Abilities.HEAL_DOWN3.registerAttributeModifier(UnsagaStatus.HEAL_THRESHOLD, 15D);
//		Abilities.HEAL_DOWN4.registerAttributeModifier(UnsagaStatus.HEAL_THRESHOLD, 20D);
//		Abilities.HEAL_DOWN5.registerAttributeModifier(UnsagaStatus.HEAL_THRESHOLD, 25D);
//		Abilities.HEAL_UP1.registerAttributeModifier(UnsagaStatus.HEAL_THRESHOLD, -5D);
//		Abilities.HEAL_UP2.registerAttributeModifier(UnsagaStatus.HEAL_THRESHOLD, -10D);
//		Abilities.SUPPORT_FIRE.registerAttributeModifier(UnsagaStatus.ENTITY_ELEMENTS.get(FiveElements.Type.FIRE), 5D);
//		Abilities.SUPPORT_EARTH.registerAttributeModifier(UnsagaStatus.ENTITY_ELEMENTS.get(FiveElements.Type.EARTH), 5D);
//		Abilities.SUPPORT_METAL.registerAttributeModifier(UnsagaStatus.ENTITY_ELEMENTS.get(FiveElements.Type.METAL), 5D);
//		Abilities.SUPPORT_WATER.registerAttributeModifier(UnsagaStatus.ENTITY_ELEMENTS.get(FiveElements.Type.WATER), 5D);
//		Abilities.SUPPORT_WOOD.registerAttributeModifier(UnsagaStatus.ENTITY_ELEMENTS.get(FiveElements.Type.WOOD), 5D);
//		Abilities.SUPPORT_FORBIDDEN.registerAttributeModifier(UnsagaStatus.ENTITY_ELEMENTS.get(FiveElements.Type.FORBIDDEN), 5D);
//		Abilities.ARMOR_BRUISE_EX.registerAttributeModifier(UnsagaStatus.GENERALS.get(General.PUNCH), 0.5D);
//		Abilities.ARMOR_FIRE_EX.registerAttributeModifier(UnsagaStatus.SUBS.get(Sub.FIRE), 0.5D);
//		Abilities.ARMOR_COLD_EX.registerAttributeModifier(UnsagaStatus.SUBS.get(Sub.FREEZE), 0.5D);
//		Abilities.ARMOR_ELECTRIC_EX.registerAttributeModifier(UnsagaStatus.SUBS.get(Sub.ELECTRIC), 0.5D);
//		Abilities.ARMOR_SLASH.registerAttributeModifier(UnsagaStatus.GENERALS.get(General.SWORD), 0.3D);
//		Abilities.ARMOR_BRUISE.registerAttributeModifier(UnsagaStatus.GENERALS.get(General.PUNCH), 0.3D);
//		Abilities.ARMOR_PIERCE.registerAttributeModifier(UnsagaStatus.GENERALS.get(General.SPEAR), 0.3D);
//		Abilities.ARMOR_FIRE.registerAttributeModifier(UnsagaStatus.SUBS.get(Sub.FIRE), 0.25D);
//		Abilities.ARMOR_COLD.registerAttributeModifier(UnsagaStatus.SUBS.get(Sub.FREEZE), 0.25D);
//		Abilities.ARMOR_ELECTRIC.registerAttributeModifier(UnsagaStatus.SUBS.get(Sub.ELECTRIC), 0.25D);
//		Abilities.ARMOR_LIGHT.registerAttributeModifier(UnsagaStatus.SUBS.get(Sub.SHOCK), 0.25D);
//		Abilities.ARMOR_PIERCE_WEAK.registerAttributeModifier(UnsagaStatus.GENERALS.get(General.SPEAR), -0.125D);
//		Abilities.PROTECTION_LIFE.registerAttributeModifier(UnsagaStatus.RESISTANCE_LP_HURT, 0.1D);
	}
	public static void init() {
		AbilityPotentialTableProvider.preinit();
		TechInitializer.init();
		registerModifiers();
		HSLibs.registerEvent(new RefleshAbilityObserver());
		AbilitySpell.registerBimap();
	}

	public static IAbility get(String name){
		return RegistryUtil.getValue(REGISTRY, UnsagaMod.MODID, name);
	}

	public static Collection<IAbility> all(){
		return RegistryUtil.getValuesExceptEmpty(REGISTRY).stream().sorted().collect(Collectors.toList());
	}
	public static void preInit() {

//		HSLibs.registerEvent(new Registerer());
//		HSLibs.registerEvent(new TechInitializer());


	}

	public static void onLivingUpdate(LivingUpdateEvent e){
		if(e.getEntityLiving() instanceof EntityPlayer){
			List<Ability> abilities = AbilityAPI.getEffectiveAllPassiveAbilities(e.getEntityLiving());
			abilities.stream().filter(in -> in instanceof AbilityTickableBase).map(in ->(AbilityTickableBase)in).forEach(in -> in.onLivingUpdate(e));


		}

	}


	@EventBusSubscriber(modid=UnsagaMod.MODID)
	public static class Registerer{

		@SubscribeEvent
		public void abilityRegister(RegistryEvent.Register<IAbility> ev){
			UnsagaMod.logger.trace("registering objects...");
			RegistryUtil.Helper<IAbility> reg = RegistryUtil.helper(ev, UnsagaMod.MODID, "unsaga");
			reg.register(new Ability("empty"));
			reg.register(new AbilityStatusModifier("heal_down1", "heal_down",UnsagaStatus.HEAL_THRESHOLD,5.0D));
			reg.register(new AbilityStatusModifier("heal_down2", "heal_down",UnsagaStatus.HEAL_THRESHOLD,10.0D));
			reg.register(new AbilityStatusModifier("heal_down3", "heal_down",UnsagaStatus.HEAL_THRESHOLD,15.0D));
			reg.register(new AbilityStatusModifier("heal_down4", "heal_down",UnsagaStatus.HEAL_THRESHOLD,20.0D));
			reg.register(new AbilityStatusModifier("heal_down5", "heal_down",UnsagaStatus.HEAL_THRESHOLD,25.0D));
			reg.register(new AbilityStatusModifier("heal_up1","heal_up1",UnsagaStatus.HEAL_THRESHOLD,-5.0D));
			reg.register(new AbilityStatusModifier("heal_up2","heal_up2",UnsagaStatus.HEAL_THRESHOLD,-10.0D));
			reg.register(new AbilityStatusModifier("support_fire",FiveElements.Type.FIRE.asStatus(),5.0D));
			reg.register(new AbilityStatusModifier("support_wood",FiveElements.Type.FIRE.asStatus(),5.0D));
			reg.register(new AbilityStatusModifier("support_water",FiveElements.Type.WATER.asStatus(),5.0D));
			reg.register(new AbilityStatusModifier("support_earth",FiveElements.Type.EARTH.asStatus(),5.0D));
			reg.register(new AbilityStatusModifier("support_metal",FiveElements.Type.METAL.asStatus(),5.0D));
			reg.register(new AbilityStatusModifier("support_forbidden",FiveElements.Type.FORBIDDEN.asStatus(),5.0D));
			reg.register(new AbilitySpell("spell_fire"));
			reg.register(new AbilitySpell("spell_earth"));
			reg.register(new AbilitySpell("spell_wood"));
			reg.register(new AbilitySpell("spell_water"));
			reg.register(new AbilitySpell("spell_metal"));
			reg.register(new AbilitySpell("spell_forbidden"));
			reg.register(new AbilityBlocking("knife_guard"));
			reg.register(new AbilityBlocking("deflect"));
			reg.register(new AbilityBlocking("blocking"));
			reg.register(new AbilityShield("block_fire",1.5F,Sub.FIRE));
			reg.register(new AbilityShield("block_melee",General.PUNCH,General.SPEAR,General.SWORD));
			reg.register(new AbilityShield("block_slash_bruise",General.SWORD,General.PUNCH));
			reg.register(new AbilityShield("block_spear",General.SPEAR));
			reg.register(new AbilityShield("block_electric",1.5F,Sub.ELECTRIC));
			reg.register(new AbilityShield("block_fire_freeze_electric",1.0F,Sub.FIRE,Sub.ELECTRIC,Sub.FREEZE));
			reg.register(new AbilityStatusModifier("resist_bruise_ex",General.PUNCH.asStatus(),0.5D));
			reg.register(new AbilityStatusModifier("resist_fire_ex",Sub.FIRE.asStatus(),0.5D));
			reg.register(new AbilityStatusModifier("resist_cold_ex",Sub.FREEZE.asStatus(),0.5D));
			reg.register(new AbilityStatusModifier("resist_electric_ex",Sub.ELECTRIC.asStatus(),0.5D));
			reg.register(new AbilityStatusModifier("resist_slash",General.SWORD.asStatus(),0.3D));
			reg.register(new AbilityStatusModifier("resist_bruise",General.PUNCH.asStatus(),0.3D));
			reg.register(new AbilityStatusModifier("resist_pierce",General.SPEAR.asStatus(),0.3D));
			reg.register(new AbilityStatusModifier("resist_fire",Sub.FIRE.asStatus(),0.25D));
			reg.register(new AbilityStatusModifier("resist_cold",Sub.FREEZE.asStatus(),0.25D));
			reg.register(new AbilityStatusModifier("resist_electric",Sub.ELECTRIC.asStatus(),0.25D));
			reg.register(new AbilityStatusModifier("resist_light",Sub.SHOCK.asStatus(),0.25D));
			reg.register(AbilityTickableBase.Factory.antiDebuff("resist_debuff"));
			reg.register(new AbilityStatusModifier("resist_pierce_weak",General.SPEAR.asStatus(),-0.125D));
			reg.register(new AbilityAntiDebuff("anti_poison",MobEffects.POISON));
			reg.register(new AbilityAntiDebuff("anti_blind",MobEffects.BLINDNESS));
			reg.register(new AbilityAntiDebuff("anti_stun",UnsagaPotions.STUN));
			reg.register(new AbilityAntiDebuff("anti_levitation",MobEffects.LEVITATION));
			reg.register(new AbilityAntiDebuff("anti_sleep",UnsagaPotions.STUN));
			reg.register(new AbilityAntiDebuff("anti_wither",MobEffects.WITHER));
			reg.register(new AbilityAntiDebuff("anti_hunger",MobEffects.HUNGER));
			reg.register(new AbilityAntiDebuff("anti_fatigue",MobEffects.MINING_FATIGUE));
			reg.register(new AbilityStatusModifier("life_protection",UnsagaStatus.RESISTANCE_LP_HURT,0.1D));
			reg.register(new AbilityAntiDebuff("str_protection",MobEffects.WEAKNESS));
			reg.register(new AbilityAntiDebuff("dex_protection",UnsagaPotions.AWKWARD_DEX));
			reg.register(new AbilityAntiDebuff("mind_protection",UnsagaPotions.DEPRESSED_MENTAL));
			reg.register(new AbilityAntiDebuff("int_protection",UnsagaPotions.POOR_INT));
			reg.register(new AbilityAntiDebuff("vit_protection",UnsagaPotions.POOR_VITALITY));
			reg.register(AbilityTickableBase.Factory.superHealing("super_healing"));
		}
		@SubscribeEvent
		public void makeRegistry(RegistryEvent.NewRegistry ev){

			UnsagaMod.logger.trace("building registry...");
			AbilityInitializer.REGISTRY=new RegistryBuilder<IAbility>().setName(ABILITIES).setType(IAbility.class)
			.setIDRange(0, 4096).setDefaultKey(new ResourceLocation(UnsagaMod.MODID,"empty")).create();
		}
	}
}
