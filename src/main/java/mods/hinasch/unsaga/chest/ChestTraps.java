package mods.hinasch.unsaga.chest;

import java.util.Optional;
import java.util.Random;

import mods.hinasch.lib.util.ChatHandler;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.core.entity.mob.EntityTreasureSlimeNew;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.RegistrySimple;

public class ChestTraps {

	public static final RegistrySimple<ResourceLocation,ChestTrap> TRAPS = new RegistrySimple();

	public static final ChestTrap DUMMY = new ChestTrap(-1,"none"){

		@Override
		public void activate(IChestBehavior chest, EntityPlayer ep) {
			// TODO 自動生成されたメソッド・スタブ

		}

	};

	public static final ChestTrap EXPLODE = put(new ChestTrap(0,"explode"){
		@Override
		public void activate(IChestBehavior chest,EntityPlayer ep){
			if(WorldHelper.isServer(ep.getEntityWorld())){

				float explv = ((float)chest.getCapability().level() * 0.06F);
				explv = MathHelper.clamp(explv, 1.0F, 4.0F);
				ChatHandler.sendLocalizedMessageToPlayer(ep, "msg.chest.burst");
				XYZPos pos = chest.getChestPosition();


				WorldHelper.createExplosionSafe(ep.getEntityWorld(),null, pos, 1.5F*explv, true);
//				chest.setStatus(EnumProperty.TRAP_OCCURED,true);

			}
		}
	});

	public static final ChestTrap NEEDLE = new ChestTrap(1,"needle"){
		@Override
		public void activate(IChestBehavior chest,EntityPlayer ep){
			int damage = ep.getEntityWorld().rand.nextInt(MathHelper.clamp(chest.getCapability().level()/15,3,100))+1;
			damage = MathHelper.clamp(damage, 1, 10);
			ep.attackEntityFrom(DamageSource.CACTUS, damage);
			ChatHandler.sendLocalizedMessageToPlayer(ep, "msg.chest.needle");
		}

		@Override
		public boolean canRemove(IChestBehavior chest,EntityPlayer ep){
			return ep.getRNG().nextInt(2)==0;
		}
	};

	public static final ChestTrap POISON = new ChestTrap(2,"poison"){
		@Override
		public void activate(IChestBehavior chest,EntityPlayer ep){
			ep.addPotionEffect(new PotionEffect(MobEffects.POISON,10*(chest.getCapability().level()/2+1),1));
			ChatHandler.sendLocalizedMessageToPlayer(ep, "msg.chest.poison");

//			chest.setStatus(EnumProperty.TRAP_OCCURED,true);
		}
	};

	public static final ChestTrap SLIME = new ChestTrap(3,"slime"){
		private int spawnRange = 2;

		@Override
		public void activate(IChestBehavior chest,EntityPlayer ep){

			Optional.ofNullable(EntityTreasureSlimeNew.makeRandomColoredSlime(ep.getEntityWorld()))
			.ifPresent(slime ->{
				XYZPos pos = chest.getChestPosition();
				double var5 = pos.dx + (ep.getEntityWorld().rand.nextDouble() - ep.getEntityWorld().rand.nextDouble()) * (double)spawnRange;
				double var7 = (pos.dy+ ep.getEntityWorld().rand.nextInt(3) - 1);
				double var9 = pos.dz + (ep.getEntityWorld().rand.nextDouble() - ep.getEntityWorld().rand.nextDouble()) * (double)spawnRange;
//				EntityLiving var11 = slime instanceof EntityLiving ? (EntityLiving)slime : null;
				slime.setLocationAndAngles(var5, var7, var9, ep.getEntityWorld().rand.nextFloat() * 360.0F, 0.0F);
				//if(var11.getCanSpawnHere()){
//				UnsagaMod.logger.trace(this.getPropertyName(), var11);
//				UnsagaMod.logger.trace(this.getPropertyName(), ep.getEntityWorld());
//				UnsagaMod.logger.trace(this.getPropertyName(), ep.getEntityWorld());
				WorldHelper.safeSpawn(ep.world, slime);
			});

		}
	};

	static{
		put(SLIME);
		put(NEEDLE);
		put(EXPLODE);
		put(POISON);
	}
	public static ChestTrap getRandomTraps(Random rand){
		return TRAPS.getRandomObject(rand);
	}
	public static ChestTrap put(ChestTrap trap){
		TRAPS.putObject(trap.res, trap);
		return trap;
	}
	public static ChestTrap getTrap(ResourceLocation num){

			return TRAPS.getObject(num);

	}
}
