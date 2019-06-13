package mods.hinasch.unsaga.villager;

import mods.hinasch.lib.iface.IRequireInitializing;

public interface IUnsagaVillager extends IRequireInitializing{

	public VillagerTypeUnsaga getVillagerType();
	public void setVillagerType(VillagerTypeUnsaga type);
//	public BlackSmithType getBlackSmithType();
//	public void setBlackSmithType(BlackSmithType type);

//	public long getRecentStockedTime();
//	public void setStockedTime(long time);
//	public NonNullList<ItemStack> getMerchandises();
//	public void setMerchandises(NonNullList<ItemStack> list);
//	public NonNullList<ItemStack> getSecretMerchandises();
//	public void setSecretMerchandises(NonNullList<ItemStack> list);
//	public int getDistributionLevel();
//	public void setDistributionLevel(int par1);
//	public int getTransactionPoint();
//	public void setTransactionPoint(int par1);
//	public BarteringShopType getShopType();
//	public void setBarteringShopType(BarteringShopType type);
//	public void setBaseShopLevel(int par1);
//	public int getBaseShopLevel();
//	public boolean hasDisplayedSecretMerchandises();
//	public void setHasDisplayedSecrets(boolean par1);

	public int getCarrierID();
	public void setCarrierID(int par1);


	public IVillagerImplimentation implimentation();
	public void setImplimentation(IVillagerImplimentation impl);

}
