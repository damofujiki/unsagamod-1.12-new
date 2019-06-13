package mods.hinasch.unsaga.villager;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

import mods.hinasch.unsaga.UnsagaMod;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

public class VillagerProfessionUnsaga {

	public static final String TEXTURE_PATH = UnsagaMod.MODID+":textures/entity/villager/";
	public static final String VANILLA_TEX_PATH = "minecraft:textures/entity/zombie_villager/zombie_butcher.png";
	/** 商人*/
	public static final VillagerProfession MERCHANT = new VillagerProfession(UnsagaMod.MODID+":merchant"
			,TEXTURE_PATH+"merchant.png",VANILLA_TEX_PATH);
	/** 商人（魔法グッズ）*/
	public static final VillagerProfession MAGIC_MERCHANT = new VillagerProfession(UnsagaMod.MODID+":magicMerchant"
			,TEXTURE_PATH+"magic_merchant.png",VANILLA_TEX_PATH);
	/** 道場*/
	public static final VillagerProfession MENTOR = new VillagerProfession(UnsagaMod.MODID+":mentor"
			,TEXTURE_PATH+"mentor.png",VANILLA_TEX_PATH);
	/** 運び屋*/
	public static final VillagerProfession CARRIER = new VillagerProfession(UnsagaMod.MODID+":carrier"
			,TEXTURE_PATH+"carrier.png",VANILLA_TEX_PATH);
	/** 武器改造屋*/
	public static final VillagerProfession BLACKSMITH = new VillagerProfession(UnsagaMod.MODID+":blackSmith"
			,TEXTURE_PATH+"unsaga_smith.png"
			,"minecraft:textures/entity/zombie_villager/zombie_smith.png");
	private static final Map<String,VillagerProfession> PROFESSIONS;

	static{
		ImmutableMap.Builder<String,VillagerProfession> builder = ImmutableMap.builder();
		builder.put("merchant",MERCHANT);
		builder.put("magicMerchant",MAGIC_MERCHANT);
		builder.put("blacksmith",BLACKSMITH);
		builder.put("mentor",MENTOR);
		builder.put("carrier",CARRIER);
		PROFESSIONS = builder.build();
	}
	public static boolean isUnsagaVillager(EntityVillager villager){
		return PROFESSIONS.values().contains(villager.getProfessionForge());
	}

	@SubscribeEvent
	public void register(RegistryEvent.Register<VillagerProfession> ev){
		for(String key:PROFESSIONS.keySet()){
			ev.getRegistry().register(PROFESSIONS.get(key));
			new VillagerCareer(PROFESSIONS.get(key),key);
		}
	}



}
