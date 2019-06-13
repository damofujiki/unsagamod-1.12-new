package mods.hinasch.unsaga.chest;

import java.util.Optional;

import javax.annotation.Nullable;

import mods.hinasch.lib.iface.INBTWritable;
import mods.hinasch.lib.util.VecUtil;
import mods.hinasch.lib.world.ScannerBuilder;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.core.entity.passive.EntityUnsagaChest;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ChunkChestStorage implements INBTWritable{

	public static final double CHEST_DETECT_RANGE = 5.0D;

	FieldChestType fieldChestType = FieldChestType.NORMAL;
	Optional<BlockPos> blockPos = Optional.empty();
	boolean hasInitialized = false;

	public static @Nullable ChunkChestStorage restore(NBTTagCompound nbt){
		return Optional.ofNullable(nbt)
				.map(in ->{
					ChunkChestStorage info = new ChunkChestStorage();
					BlockPos pos = XYZPos.RESTORE_TO_BLOCKPOS.apply(in);
					if(pos!=null){
						info.setChestPos(pos);
					}
					int type = in.getInteger("chestType");
					info.initializeChestType(FieldChestType.fromMeta(type));
					info.markInitialized(in.getBoolean("initialized"));
					return info;
				}).orElse(null);

	}
	public ChunkChestStorage(){

	}
	public ChunkChestStorage(BlockPos pos){
		this.setChestPos(pos);
	}

	public boolean hasDetectableChest(BlockPos pos){
		return this.getChestPos().map(in ->{
			double distance = in.getDistance(pos.getX(), pos.getY(), pos.getZ());
			return CHEST_DETECT_RANGE>distance;
		}).orElse(false);
	}

	/** チャンクのチェストをスポーンさせる。空間がなければスルー。
	 * あればスポーンさせてチャンクのチェストをemptyにする。*/
	public void spawnChest(World world,BlockPos playerPos){
		ScannerBuilder.create()
		.base(playerPos)
		.range(5)
		.ready()
		.stream()
		.filter(in -> world.isAirBlock(in) && world.isAirBlock(in.up()))
		.findFirst().ifPresent(pos ->{
			this.setChestPos(null);
			EntityUnsagaChest chest = new EntityUnsagaChest(world);
			VecUtil.setEntityPositionTo(chest, pos);
			world.spawnEntity(chest);
		});

	}
	public FieldChestType chestType() {
		return fieldChestType;
	}
	public void initializeChestType(FieldChestType fieldChestType) {
		this.fieldChestType = fieldChestType;
	}
	public Optional<BlockPos> getChestPos() {
		return blockPos;
	}
	public void setChestPos(@Nullable BlockPos blockPos) {
		this.blockPos = Optional.ofNullable(blockPos);
	}
	public boolean hasInitialized() {
		return hasInitialized;
	}
	public void markInitialized(boolean hasInitialized) {
		this.hasInitialized = hasInitialized;
	}
	@Override
	public void writeToNBT(NBTTagCompound stream) {
		// TODO 自動生成されたメソッド・スタブ
		stream.setInteger("chestType", this.chestType().getMeta());
		this.getChestPos().ifPresent(in ->(new XYZPos(in)).writeToNBT(stream));
		stream.setBoolean("initialized", hasInitialized);
	}


}
