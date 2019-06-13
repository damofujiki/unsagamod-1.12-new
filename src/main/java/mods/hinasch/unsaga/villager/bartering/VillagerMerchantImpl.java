package mods.hinasch.unsaga.villager.bartering;

import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.unsaga.villager.IUnsagaVillager;
import mods.hinasch.unsaga.villager.IVillagerImplimentation;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.event.AttachCapabilitiesEvent;

/** 商人の実装*/
public class VillagerMerchantImpl implements IVillagerImplimentation{

	BarteringShopType shopType = BarteringShopType.UNKNOWN;
	boolean hasDisplayedSecretMerchandise = false;

	NonNullList<ItemStack> merchandises = ItemUtil.createStackList(9);
	NonNullList<ItemStack> secrets = ItemUtil.createStackList(9);

	int distributionLevel = 0;


	long purchaseTime = 0;

	int transactionPoint = 0;

	int baseShopLevel = 0;



	public long getRecentStockedTime() {
		// TODO 自動生成されたメソッド・スタブ
		return purchaseTime;
	}

	public void setStockedTime(long time) {
		// TODO 自動生成されたメソッド・スタブ
		this.purchaseTime = time;
	}

	public NonNullList<ItemStack> getMerchandises() {
		// TODO 自動生成されたメソッド・スタブ
		return this.merchandises;
	}

	public void setMerchandises(NonNullList<ItemStack> list) {
		// TODO 自動生成されたメソッド・スタブ
		this.merchandises = list;
	}

	public NonNullList<ItemStack> getSecretMerchandises() {
		// TODO 自動生成されたメソッド・スタブ
		return this.secrets;
	}

	public void setSecretMerchandises(NonNullList<ItemStack> list) {
		// TODO 自動生成されたメソッド・スタブ
		this.secrets = list;
	}

	public int getDistributionLevel() {
		// TODO 自動生成されたメソッド・スタブ
		return this.distributionLevel;
	}


	public void setDistributionLevel(int par1) {
		// TODO 自動生成されたメソッド・スタブ
		this.distributionLevel = par1;
	}


	public int getTransactionPoint() {
		// TODO 自動生成されたメソッド・スタブ
		return this.transactionPoint;
	}


	public void setTransactionPoint(int par1) {
		this.transactionPoint = par1;

	}


	public BarteringShopType getShopType() {
		// TODO 自動生成されたメソッド・スタブ
		return this.shopType;
	}


	public void setBarteringShopType(BarteringShopType type) {
		this.shopType = type;

	}


	public void setBaseShopLevel(int par1) {
		// TODO 自動生成されたメソッド・スタブ
		this.baseShopLevel = par1;
	}


	public int getBaseShopLevel() {
		// TODO 自動生成されたメソッド・スタブ
		return this.baseShopLevel;
	}


	public boolean hasDisplayedSecretMerchandises() {
		// TODO 自動生成されたメソッド・スタブ
		return this.hasDisplayedSecretMerchandise;
	}


	public void setHasDisplayedSecrets(boolean par1) {
		// TODO 自動生成されたメソッド・スタブ
		this.hasDisplayedSecretMerchandise = par1;
	}

	@Override
	public void readNBT(NBTTagCompound comp, Capability<IUnsagaVillager> capability, IUnsagaVillager instance,
			EnumFacing side) {

		UtilNBT.comp(comp)
		.setItemStacksToField("merchandises", 9, (list,key)->this.setMerchandises(list))
		.setItemStacksToField("secrets", 9, (list,key)->this.setSecretMerchandises(list))
		.setToField("purchaseTime", (nbt,key)->this.setStockedTime(nbt.getLong(key)))
		.setToField("distributionLevel", (nbt,key)->this.setDistributionLevel(nbt.getInteger(key)))
		.setToField("transactionPoint", (nbt,key)->this.setTransactionPoint(nbt.getInteger(key)))
		.setToField("shopType", (nbt,key)->this.setBarteringShopType(BarteringShopType.fromMeta((int)nbt.getByte(key))))
		.setToField("baseShopLevel", (nbt,key)->this.setBaseShopLevel(nbt.getInteger(key)))
		.setToField("displayedSercrets", (nbt,key)->this.setHasDisplayedSecrets(nbt.getBoolean(key)));
//		if(comp.hasKey("transactionPoint")){
//			this.setTransactionPoint(comp.getInteger("transactionPoint"));
//		}
//		if(comp.hasKey("shopType")){
//			this.setBarteringShopType(BarteringShopType.fromMeta((int)comp.getByte("shopType")));
//		}
//		if(comp.hasKey("baseShopLevel")){
//			this.setBaseShopLevel(comp.getInteger("baseShopLevel"));
//		}
//		if(comp.hasKey("displayedSercrets")){
//			this.setHasDisplayedSecrets(comp.getBoolean("displayedSercrets"));
//		}
	}

	@Override
	public void writeNBT(NBTTagCompound comp, Capability<IUnsagaVillager> capability, IUnsagaVillager instance,
			EnumFacing side) {
		UtilNBT.comp(comp)
		.setItemStacks("merchandises",  this.getMerchandises())
		.setItemStacks("secrets", this.getSecretMerchandises());

		comp.setInteger("distributionLevel", this.getDistributionLevel());
		comp.setLong("purchaseTime", this.getRecentStockedTime());
		comp.setInteger("transactionPoint", this.getTransactionPoint());
		comp.setByte("shopType", (byte)this.getShopType().getMeta());
		comp.setInteger("baseShopLevel", this.getBaseShopLevel());
		comp.setBoolean("displayedSercrets", this.hasDisplayedSecretMerchandises());
	}


	@Override
	public void init(EntityVillager villager, Capability<IUnsagaVillager> capa, EnumFacing face,
			AttachCapabilitiesEvent ev) {
		if(WorldHelper.isServer(villager.world)){
			BarteringShopType type = BarteringShopType.decideBarteringType(villager.getEntityWorld(), villager);
			this.setBarteringShopType(type);
			this.setBaseShopLevel(villager.getRNG().nextInt(9)+1);
		}

	}
}
