package mods.hinasch.unsaga.chest;

import java.util.List;

import com.google.common.collect.Lists;

/**
 *
 * マップ表示時のキャッシュ（クライアント用）
 *
 */
public class ClientChestPosCache {

	private static List<ChunkChestStorage> chestPosCache = Lists.newArrayList();
	private static List<ChunkChestStorage> chestPosCache2 = Lists.newArrayList();
	public static List<ChunkChestStorage> getChestPosCache(int bank) {
		return bank ==0 ? chestPosCache : chestPosCache2;
	}

	public static void setChestPosCache(int bank,List<ChunkChestStorage> list) {
		if(bank==0){
			chestPosCache = list;
		}else{
			chestPosCache2 = list;
		}
	}


}
