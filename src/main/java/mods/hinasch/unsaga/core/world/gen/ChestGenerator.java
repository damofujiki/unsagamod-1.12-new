package mods.hinasch.unsaga.core.world.gen;

import java.util.Optional;
import java.util.Random;

import javax.annotation.Nullable;

import mods.hinasch.lib.world.WorldGeneratorBase;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.chest.FieldChestType;
import mods.hinasch.unsaga.core.world.chunk.UnsagaChunkCapability;
import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class ChestGenerator extends WorldGeneratorBase{

	@Override
	public void generateNether(World world, Random random, int i, int j) {
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void generateOverworld(World world, Random rand, int gx, int gz) {
		Chunk chunk = world.getChunkFromChunkCoords(gx >> 4 , gz >> 4);
		//		UnsagaMod.logger.trace(this.getClass().getName(), chunk);
		rand.nextFloat();
		UnsagaChunkCapability.ADAPTER.getCapabilityOptional(chunk)
		.ifPresent(cap ->{
			DimensionType dimType = world.provider.getDimensionType();
			if(cap.chunkChestStorage().hasInitialized()){
				return;
			}else{
				this.generateChest(world, rand, gx, gz, dimType, chunk);
			}

		});

	}

	private  void generateChest(World world, Random rand, int gx, int gz,DimensionType dimType,Chunk chunk) {
		UnsagaChunkCapability.ADAPTER.getCapabilityOptional(chunk)
		.ifPresent(cap ->{
			cap.chunkChestStorage().markInitialized(true);
			if(rand.nextInt(30)<=UnsagaConfigHandlerNew.GENERATION.bonusChestGenerationRate){
				FieldChestType type = this.decideChestType(dimType, rand);
				cap.chunkChestStorage().initializeChestType(type);
				int x = gx + rand.nextInt(16);
				int z = gz + rand.nextInt(16);
				this.generateChestPosition(type, world, x, z)
				.ifPresent(pos ->{
					cap.chunkChestStorage().setChestPos(pos);
					UnsagaMod.logger.trace(this.getClass().getName(), "Chest Generated:",pos);
				});


			}
		});

	}
	private Optional<BlockPos> generateChestPosition(FieldChestType type,World world,int x,int z){
		return Optional.ofNullable(type.generateChestPos(world, x, z));
	}
	private @Nullable FieldChestType decideChestType(DimensionType dimType,Random rand){
		FieldChestType type = Optional.ofNullable(dimType)
				.filter(dim ->dim!=DimensionType.OVERWORLD)
				.map(dim ->FieldChestType.fromDimension(dim))
				.orElse(rand.nextInt(2)==0 ? FieldChestType.CAVE : FieldChestType.FIELD);
		return type;
	}

	@Override
	public void generateEnd(World world, Random random, int i, int j) {
		// TODO 自動生成されたメソッド・スタブ

	}

}
