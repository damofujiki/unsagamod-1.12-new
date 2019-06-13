package mods.hinasch.unsaga.core.world;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import mods.hinasch.lib.iface.INBTWritable;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.lib.world.XYZPos;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.ChunkPos;

public class WorldStructureStorage implements INBTWritable{


	public static final ResourceLocation MERCHANT_HOUSE = new ResourceLocation("merchant_house");
	Map<ResourceLocation,XYZPos> structures = new HashMap<>();


	public static WorldStructureStorage restore(NBTTagCompound comp){
		WorldStructureStorage instance = new WorldStructureStorage();
		Map<ResourceLocation,XYZPos> map = Maps.newHashMap();
		UtilNBT.forEachTagList(comp, "structures",in ->{
			XYZPos pos = XYZPos.readFromNBT(in);
			String name = in.getString("name");
			map.put(new ResourceLocation(name), pos);
		});


		instance.setMap(map);
		return instance;
	}
	public boolean isStructureNear(ResourceLocation id, ChunkPos coords) {
		List<XYZPos> list = this.getCoords(id);
		return !list.isEmpty() && list.stream()
				.anyMatch(in -> in.getDistance(coords.x, 0, coords.z)<300.0D);
	}


	public void addCoods(ResourceLocation id, ChunkPos coords) {
		// TODO 自動生成されたメソッド・スタブ
		this.structures.put(id, new XYZPos(coords.x,0,coords.z));
	}


	public List<XYZPos> getCoords(ResourceLocation id) {
		return structures.entrySet().stream()
				.filter(in -> in.getKey()==id)
				.map(in -> in.getValue())
				.collect(Collectors.toList());
	}


	public boolean hasGeneratedFirstMerchantHouse() {
		// TODO 自動生成されたメソッド・スタブ
		return this.getCoords(MERCHANT_HOUSE).size()>=1;
	}


	public Map<ResourceLocation, XYZPos> getMap() {
		// TODO 自動生成されたメソッド・スタブ
		return ImmutableMap.copyOf(this.structures);
	}


	public void setMap(Map<ResourceLocation, XYZPos> map) {
		// TODO 自動生成されたメソッド・スタブ
		this.structures = map;
	}

	@Override
	public void writeToNBT(NBTTagCompound comp) {

		UtilNBT.comp(comp)
		.setTag("structures", tagList ->{
			this.getMap().entrySet().stream().forEach(in ->{
				tagList.appendTag(UtilNBT.comp().write(child -> in.getValue().writeToNBT(child))
						.setString("name", in.getKey().toString()).get());
			});
		});

	}
}
