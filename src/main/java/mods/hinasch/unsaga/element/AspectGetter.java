package mods.hinasch.unsaga.element;

import mods.hinasch.unsaga.core.world.chunk.UnsagaChunkCapability;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public class AspectGetter {

	/** 天候時のフィルターも加えた最終的な現在地の相を得る。*/
	public static ElementTable getCurrentAspect(World world,BlockPos pos){
		ElementTable staticElements = getStaticAspect(world,pos);
		return staticElements.add(getAspectOffsetFromChunk(world,pos).getCurrentOffset());
	}

	/** チャンクの相変化を抜いた静的な五行相を得る。要はバイオーム独自相＋α
	 * 術の解読時はこちらを見る*/
	public static ElementTable getStaticAspect(World world,BlockPos pos){
		ElementTable biomeElements = BiomeElementsRegistry.getBiomeElements(world.getBiome(pos));
		return biomeElements.add(getAspectFilter(world,pos));
	}
	/** 五行相に加えるフィルター。今の所雨の時の水相のみ。*/
	private static ElementTable getAspectFilter(World world,BlockPos pos){

		if(world.isRaining()){
			return new ElementTable(-3,0,0,3,0,0);
		}
		return ElementTable.ZERO;
	}

	/** 五行相（変化）をチャンクから得る。*/
	public static AspectOffset getAspectOffsetFromChunk(World world,BlockPos pos){
		Chunk chunk = world.getChunkFromBlockCoords(pos);
		if(UnsagaChunkCapability.ADAPTER.hasCapability(chunk)){
			return UnsagaChunkCapability.ADAPTER.getCapability(chunk).getAspectOffset();
		}
		return AspectOffset.ZERO;
	}
}
