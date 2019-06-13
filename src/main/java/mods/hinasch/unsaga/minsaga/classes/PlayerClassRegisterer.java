package mods.hinasch.unsaga.minsaga.classes;

import com.mojang.realmsclient.util.Pair;

import mods.hinasch.lib.registry.RegistryUtil;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.skillpanel.SkillPanels;
import mods.hinasch.unsaga.status.UnsagaStatus;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

public class PlayerClassRegisterer{


	private static IForgeRegistry<IPlayerClass> REGISTRY;
	public static final ResourceLocation PLAYER_CLASS = new ResourceLocation(UnsagaMod.MODID,"player_class");


//	private List<IAttribute> watchingAttributes = Lists.newArrayList();


	public static IPlayerClass get(String name){
		return RegistryUtil.getValue(REGISTRY, UnsagaMod.MODID, name);
	}

	@Mod.EventBusSubscriber(modid=UnsagaMod.MODID)
	public static class Registerer{
		@SubscribeEvent
		public void makeRegistry(RegistryEvent.NewRegistry ev){
			REGISTRY = new RegistryBuilder().setName(PLAYER_CLASS).setType(IPlayerClass.class)
			.setIDRange(0, 4096).setDefaultKey(RegistryUtil.EMPTY).create();
		}

		@SubscribeEvent(priority=EventPriority.LOW)
		public void registerClasses(RegistryEvent.Register<IPlayerClass> ev){
			RegistryUtil.Helper<IPlayerClass> reg = RegistryUtil.helper(ev, UnsagaMod.MODID, "unsaga");

			PlayerClassImpl.Builder locksmith = new PlayerClassImpl.Builder("locksmithi");
			PlayerClassImpl.Builder wizard = new PlayerClassImpl.Builder("wizard");
			PlayerClassImpl.Builder heavyFighter = new PlayerClassImpl.Builder("heavy_fighter");
			PlayerClassImpl.Builder cartographer = new PlayerClassImpl.Builder("cartographer");
			PlayerClassImpl.Builder craftsman = new PlayerClassImpl.Builder("craftsman");
			PlayerClassImpl.Builder archer = new PlayerClassImpl.Builder("archer");


			locksmith.setClassSkills(SkillPanels.LOCKSMITH,SkillPanels.DEFUSE,SkillPanels.SHARP_EYE);
			cartographer.setClassSkills(SkillPanels.GUIDE_CAVE,SkillPanels.GUIDE_ROAD);
			craftsman.setClassSkills(SkillPanels.THRIFT_SAVER);
			heavyFighter.setClassSkills(SkillPanels.SHIELD);
			wizard.setClassSkills(SkillPanels.ARCANE_TONGUE,SkillPanels.MAGIC_EXPERT);

			locksmith.buildRequiredSkillsFromClassSkills();
			wizard.buildRequiredSkillsFromClassSkills();
			heavyFighter.setRequireSkills(SkillPanels.SHIELD,SkillPanels.IRON_BODY);
			cartographer.buildRequiredSkillsFromClassSkills();
			craftsman.setRequireSkills(SkillPanels.THRIFT_SAVER,SkillPanels.QUICK_FIX);
			archer.setRequireSkills(SkillPanels.GUN,SkillPanels.SMART_MOVE);

			locksmith.setClassGrowth(Pair.of(SharedMonsterAttributes.ATTACK_DAMAGE, -0.5D),Pair.of(SharedMonsterAttributes.ATTACK_SPEED, 0.15D),Pair.of(UnsagaStatus.DEXTALITY, 0.5D));
			wizard.setClassGrowth(Pair.of(SharedMonsterAttributes.ATTACK_DAMAGE, -1.0D),Pair.of(UnsagaStatus.INTELLIGENCE, 1.0D),Pair.of(UnsagaStatus.MENTAL, 1.0D));
			heavyFighter.setClassGrowth(Pair.of(SharedMonsterAttributes.MAX_HEALTH, 1.0D),Pair.of(SharedMonsterAttributes.ATTACK_SPEED, -0.15D));

			reg.register(locksmith.build());
			reg.register(cartographer.build());
			reg.register(craftsman.build());
			reg.register(heavyFighter.build());
			reg.register(wizard.build());
			reg.register(archer.build());

		}
	}

//
//
//	public List<IAttribute> getWatchingAttributes(){
//		return this.watchingAttributes;
//	}


}
