package mods.hinasch.unsaga.villager;

import mods.hinasch.lib.iface.IIntSerializable;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.villager.bartering.VillagerMerchantImpl;
import mods.hinasch.unsaga.villager.smith.VillagerBlacksmithImpl;

public enum VillagerTypeUnsaga implements IIntSerializable{

	CARRIER(0,"carrier"),
	BARTERING(1,"bartering"){
		@Override
		public IVillagerImplimentation createImpl(){
			return new VillagerMerchantImpl();
		}
	},SMITH(2,"smith"){
		@Override
		public IVillagerImplimentation createImpl(){
			return new VillagerBlacksmithImpl();
		}
	},UNKNOWN(10,"unknown");

	final int meta;
	final String name;

	private VillagerTypeUnsaga(int meta,String name){
		this.meta = meta;
		this.name = name;
	}
	@Override
	public int getMeta() {
		// TODO 自動生成されたメソッド・スタブ
		return meta;
	}

	public IVillagerImplimentation createImpl(){
		return new IVillagerImplimentation.Empty();
	}
	public static VillagerTypeUnsaga fromMeta(int meta){
		return HSLibs.fromMeta(VillagerTypeUnsaga.values(),meta);
	}

}
