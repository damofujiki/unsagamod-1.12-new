package mods.hinasch.unsaga.ability.specialmove;

import java.util.Map;
import java.util.function.Predicate;

import com.google.common.collect.Maps;

import mods.hinasch.unsaga.status.LibraryLivingStatus;
import mods.hinasch.unsaga.status.LibraryLivingStatus.LivingStatus;
import mods.hinasch.unsaga.status.UnsagaStatus;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySlime;

public class LearningFacilityHelper {


	protected static Map<Predicate<EntityLivingBase>,Float> map = Maps.newHashMap();


	/** 敵モブのひらめき度を取得。存在しない場合は一律15%*/
	public static double getBaseValue(EntityLivingBase entity){

		if(LibraryLivingStatus.find(entity).isPresent()){
			LivingStatus st = LibraryLivingStatus.find(entity).get();
			if(st.getStatusMap().containsKey(UnsagaStatus.SPARK_LV)){
				return st.getStatusMap().get(UnsagaStatus.SPARK_LV);
			}
		}
		if(entity instanceof EntitySlime){
			EntitySlime slime = (EntitySlime) entity;
			return slime.isSmallSlime() ? 0.01F : 0.15F;
		}
		//		OptionalDouble rt = map.entrySet().stream().filter(in -> in.getKey().test(entity))
		//				.mapToDouble(in -> (double)in.getValue()).findFirst();
		//		return rt.isPresent() ? rt.getAsDouble() : 0.15D;
		return 0.15D;
	}
	public static void register(){
//		//高いほどよくひらめく
//		map.put(in -> in instanceof EntityEnderman, 0.35F);
//		map.put(in -> in instanceof EntityPigZombie, 0.35F);
//		map.put(in -> in instanceof EntityWitch, 0.25F);
//		map.put(in -> in instanceof EntityShulker, 0.25F);
//		map.put(in -> in instanceof EntityGuardian, 0.25F);
//		map.put(in -> in instanceof EntityBlaze, 0.25F);
//		map.put(in -> in instanceof EntityGhast, 0.25F);
//		map.put(in -> {
//			if(in instanceof EntitySlime){
//				EntitySlime slime = (EntitySlime) in;
//				return slime.isSmallSlime();
//			}
//			return false;
//		}, 0.01F);
//		map.put(in -> in instanceof EntityWitherSkeleton, 0.15F);
//		map.put(in -> in instanceof EntityStray, 0.15F);
	}
}
