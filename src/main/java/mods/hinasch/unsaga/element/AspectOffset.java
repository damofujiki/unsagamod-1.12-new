package mods.hinasch.unsaga.element;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;

import mods.hinasch.lib.iface.INBTWritable;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.core.world.chunk.UnsagaChunkCapability;
import mods.hinasch.unsaga.element.FiveElements.Type;
import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

/** 地相変化値、チャンクごとに付く。基本保存なし*/
public class AspectOffset implements INBTWritable{

	public static final AspectOffset ZERO = new AspectOffset();
	public static final Map<FiveElements.Type,ElementTable> ASPECT_EFFECT_PER_TYPE;

	static{
		ImmutableMap.Builder<Type,ElementTable> builder = ImmutableMap.builder();
		builder.put(Type.FIRE, new ElementTable(2,-2,2,-1,-1,0));
		builder.put(Type.EARTH, new ElementTable(-1,2,-1,1,-1,0));
		builder.put(Type.METAL, new ElementTable(-1,-2,2,-2,2,0));
		builder.put(Type.WATER, new ElementTable(-1,-1,-1,2,-2,-2));
		builder.put(Type.WOOD, new ElementTable(1,-1,-1,-2,2,0));
		builder.put(Type.FORBIDDEN, new ElementTable(-1,-1,-2,-1,-2,2));
		ASPECT_EFFECT_PER_TYPE = builder.build();
	}
	ElementTable aspectOffset = ElementTable.ZERO;
//	NonNullList<FiveElements.Type> aspect = NonNullList.withSize(32, FiveElements.Type.FORBIDDEN);
	long nextEffectTime = -1;

//	boolean hasInitialized = false;

	public static AspectOffset restore(NBTTagCompound args){
		AspectOffset offset = new AspectOffset();
		offset.readFromNBT(args);
		return offset;
	}
	public AspectOffset(){
	}

	public ElementTable getCurrentOffset(){
		return this.aspectOffset;
	}
//	public void init(Biome biome){
//		int index = 0;
//		for(FiveElements.Type type:FiveElements.Type.values()){
//			if(type!=FiveElements.Type.FORBIDDEN){
//				for(int i=0;i<4;i++){
//					this.aspect.set(index, type);
//					index ++;
//				}
//			}
//		}
//		this.hasInitialized = true;
//	}


	public void scheduleUpdate(long current){
		this.nextEffectTime = current + UnsagaConfigHandlerNew.ASPECT.processSpeed;
	}
	public void addOffset(FiveElements.Type type,long currentTime){
		if(!this.isAspectSaturation()){
			ElementTable.Mutable mutable = new ElementTable.Mutable(this.aspectOffset);
			ElementTable additional = ASPECT_EFFECT_PER_TYPE.get(type);
			mutable.add(additional);
			this.aspectOffset = mutable.toImmutable();
			this.scheduleUpdate(currentTime);
		}

	}

	public void processEquilibrium(Random rand,long currentTime){
		List<FiveElements.Type> list = FiveElements.VALUES.stream().filter(in -> Math.abs(this.aspectOffset.get(in))>0)
		.collect(Collectors.toList());
		if(!list.isEmpty()){
			FiveElements.Type elm = HSLibs.randomPick(rand, list);
			int value = this.aspectOffset.getInt(elm)>0 ? -1 : 1;
			this.aspectOffset = this.aspectOffset.add(elm, value);
			this.scheduleUpdate(currentTime);
		}else{
			this.aspectOffset = ElementTable.ZERO;
			this.nextEffectTime = -1;
		}


	}

	public long getNextEffectTime(){
		return this.nextEffectTime;
	}

	/** 飽和状態かどうか*/
	public boolean isAspectSaturation(){
//		for(FiveElements.Type type:FiveElements.VALUES){
//			if(this.aspectOffset.getInt(type)>32){
//				return true;
//			}
//		}
		return FiveElements.VALUES.stream().filter(in -> this.aspectOffset.get(in)>0).mapToInt(in -> this.aspectOffset.getInt(in))
		.sum() > 32 ? true : false;
	}
	public void setOffset(ElementTable offset,long currentTime){
		this.aspectOffset = offset;
		this.scheduleUpdate(currentTime);
	}

	public void addOffset(FiveElements.Type type,int count,long currentTime){
		for(int i=0;i<count;i++){
			this.addOffset(type, currentTime);
		}
	}
//
//	public void decrInterval(){
//		if(this.effectInterval>0){
//			this.effectInterval --;
//		}
//
//	}
//	public void processFlacture(Biome biome){
//		ElementTable.Mutable mutable = new ElementTable.Mutable();
//		ElementTable base = BiomeElements.getBiomeElements(biome);
//		for(FiveElements.Type type:FiveElements.VALUES){
//			int flacture = Integer.compare(base.getInt(type),this.aspectFluctuation.getInt(type));
//			mutable.add(type,flacture);
//		}
//		this.aspectFluctuation = mutable.toImmutable();
//	}
//


	public void writeToNBT(NBTTagCompound nbt){
		this.aspectOffset.writeToNBT(nbt);
		nbt.setLong("time", this.nextEffectTime);
	}


	public void readFromNBT(NBTTagCompound nbt){
		this.aspectOffset = ElementTable.RESTORE.apply(nbt);
		this.nextEffectTime = nbt.getLong("time");
	}

	public static void addOffsetTo(World world,BlockPos position,FiveElements.Type type,int count){
		Chunk chunk = world.getChunkFromBlockCoords(position);
		UnsagaChunkCapability.ADAPTER.getCapabilityOptional(chunk).ifPresent(in ->in.getAspectOffset().addOffset(type,count ,world.getTotalWorldTime()));
	}

	public static void setOffsetTo(World world,BlockPos position,ElementTable offset){
		Chunk chunk = world.getChunkFromBlockCoords(position);
		UnsagaChunkCapability.ADAPTER.getCapabilityOptional(chunk).ifPresent(in ->in.getAspectOffset().setOffset(offset ,world.getTotalWorldTime()));
	}
}
