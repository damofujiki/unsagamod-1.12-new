package mods.hinasch.unsaga.damage;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.util.DamageSource;

public class AdditionalDamageCache {


	private static Map<DamageSource,AdditionalDamage> map = new HashMap<>();


	public static void addCache(DamageSource anker,AdditionalDamage ds){

		if(map.size()>1000){
			map.clear();
		}
		map.put(anker, ds);
	}

	public static void removeCache(DamageSource anker){
		map.remove(anker);
	}

	public static @Nullable AdditionalDamage  getData(DamageSource source){
		return map.get(source);
	}
}
