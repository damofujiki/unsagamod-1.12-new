package mods.hinasch.unsaga.core.world.gen;

import java.util.Random;

import mods.hinasch.lib.world.WorldGeneratorBase;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.world.UnsagaWorldCapability;
import mods.hinasch.unsaga.core.world.WorldStructureStorage;
import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

/** 鍛冶屋と商人の小屋のGenerator*/
public class MerchantHouseGenerator extends WorldGeneratorBase{

	Random rand2;

	public static final int PROB_GEN = 5;
//	 public static final Set<BiomeDictionary.Type> biomeTypesSpawn = Sets.newHashSet();
	@Override
	public void generateNether(World world, Random random, int i, int j) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void generateOverworld(World world, Random rand, int x, int z) {
		if(!UnsagaConfigHandlerNew.GENERAL.enableGenerationMerchantHouse){
			return;
		}



		UnsagaWorldCapability.ADAPTER.getCapabilityOptional(world)
		.ifPresent(cap ->{
			int chunkX = x >> 4;
			int chunkZ = z >> 4;


			boolean isForce = UnsagaConfigHandlerNew.GENERATION.enableForceMerchantHouseFirst && !UnsagaWorldCapability.ADAPTER.getCapability(world).structureStorage().hasGeneratedFirstMerchantHouse();


			rand2 = new Random(world.getSeed() + chunkX + chunkZ * 31);
			rand2.nextFloat();
			int r = (int) (rand2.nextFloat() * 1000);
//			UnsagaMod.logger.trace("rand",r );

			boolean isHouseNear = cap.structureStorage().isStructureNear(WorldStructureStorage.MERCHANT_HOUSE,new ChunkPos(chunkX,chunkZ));


//			UnsagaMod.logger.trace("isForce", isForce,isHouseNear);
			if((r<PROB_GEN || isForce) && !isHouseNear){
				int px = x + rand.nextInt(8);
				int pz = z + rand.nextInt(8);
				UnsagaMod.logger.splitter("Merchant Houser");
				UnsagaMod.logger.trace("2", this.getClass().getName());
				BlockPos pos = world.getHeight(new BlockPos(px,0,pz));

				if(this.canGenerateHere(world, pos)){
					UnsagaMod.logger.trace("3", this.getClass().getName());
					StructureMerchantHouse.create().build(world, pos);
					cap.structureStorage().addCoods(WorldStructureStorage.MERCHANT_HOUSE, new ChunkPos(x,z));
//					WorldSaveDataStructure.get(world).addCoods(chunkX, chunkZ);
//					WorldSaveDataStructure.get(world).markDirty();
//					this.generated = true;
				}

			}
		});

	}

	private boolean canGenerateHere(World world,BlockPos pos){
		Biome biome = world.getBiome(pos);
		if(world.getVillageCollection().getNearestVillage(pos, 6)!=null){
			return false;
		}
		return BiomeDictionary.hasType(biome, BiomeDictionary.Type.PLAINS) || BiomeDictionary.hasType(biome, BiomeDictionary.Type.SANDY);
	}
	@Override
	public void generateEnd(World world, Random random, int i, int j) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
