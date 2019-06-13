package mods.hinasch.unsaga.villager.bartering;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.misc.Triplet;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.unsaga.core.advancement.UnsagaTriggers;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.util.ToolCategory;
import mods.hinasch.unsaga.villager.VillagerCapabilityUnsaga;
import mods.hinasch.unsaga.villager.village.VillageCapabilityUnsaga;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.Village;
import net.minecraft.world.World;

/**
 *
 * 店の商品を陳列する、流通レベルの管理、
 *要は店に関するもの全部
 */
public class TraitShopOwner {

	World world;
	EntityVillager villager;
	@Nullable Village villageObj;
	public final VillagerMerchantImpl merchant;
	MerchandiseFactory factory;

	public static final int MAX_DIST_LEVEL = 30;

	public TraitShopOwner(World world,EntityVillager villager){
		this.world = world;
		this.villager = villager;
		this.villageObj = this.world.villageCollection.getNearestVillage(villager.getPosition(), 30);
		this.merchant = (VillagerMerchantImpl) VillagerCapabilityUnsaga.ADAPTER.getCapability(villager).implimentation();
		this.factory = merchant.getShopType()==BarteringShopType.MAGIC ? new MerchandiseFactory.Magic(world.rand) : new MerchandiseFactory(world.rand);
	}

	/** 商品入れ替え時間かどうか*/
	public boolean hasComeUpdateTime(){
		if(merchant.getRecentStockedTime()<=0){
			return true;
		}
		if(world.getTotalWorldTime() - merchant.getRecentStockedTime()>24000){
			return true;
		}
//		if(UnsagaVillagerCapability.ADAPTER.hasCapability(villager)){
//			if(UnsagaVillagerCapability.ADAPTER.getCapability(villager).getRecentStockedTime()<=0){
//				return true;
//			}
//			if(world.getTotalWorldTime() - UnsagaVillagerCapability.ADAPTER.getCapability(villager).getRecentStockedTime()>24000){
//				return true;
//			}
//
//		}

		return false;
	}


	public void addTransactionPoint(int point){
		int prev = merchant.getTransactionPoint();
		merchant.setTransactionPoint(prev + point);
//		if(UnsagaVillagerCapability.ADAPTER.hasCapability(villager)){
//			int prev = UnsagaVillagerCapability.ADAPTER.getCapability(villager).getTransactionPoint();
//			UnsagaVillagerCapability.ADAPTER.getCapability(villager).setTransactionPoint(prev + point);
//		}
	}

	/** 流通レベルの同期（村と村人の）*/
	private void syncDistributionLevel(){
		//村がある場合村の流通レベルを取得、なければゼロ
		int villageDistribution = this.villageObj!=null ? VillageCapabilityUnsaga.ADAPTER.getCapability(this.villageObj).getDistributionPoint() : 0;
		int current = merchant.getDistributionLevel(); //村人の流通レベル

		//		if(optMaxDistLevel.isPresent()){
		if(current<villageDistribution){ //村人より村の流通レベルが大きければ、村人の流通度はそれに準ずる
			merchant.setDistributionLevel(villageDistribution);
		}else{ //村より村人の流通レベルが大きければ、村の流通レベルはそれに同期
			if(this.villageObj!=null){
				VillageCapabilityUnsaga.ADAPTER.getCapability(villageObj).setDistributionPoint(current);
			}
		}
		//		OptionalInt optMaxDistLevel = this.world.getEntitiesWithinAABB(EntityVillager.class, this.villager.getEntityBoundingBox().grow(30.0D)).stream().filter(in-> UnsagaVillagerCapability.ADAPTER.hasCapability(in))
		//				.mapToInt(in -> UnsagaVillagerCapability.ADAPTER.getCapability(in).getDistributionLevel()).max();
//		int villageDistribution = this.villageObj!=null ? VillageDistributionCapability.ADAPTER.getCapability(villageObj).getDistributionPoint() : 0;
//		int current = UnsagaVillagerCapability.ADAPTER.getCapability(villager).getDistributionLevel();
//
//		//		if(optMaxDistLevel.isPresent()){
//		if(current<villageDistribution){
//			UnsagaVillagerCapability.ADAPTER.getCapability(villager).setDistributionLevel(villageDistribution);
//		}else{
//			if(this.villageObj!=null){
//				VillageDistributionCapability.ADAPTER.getCapability(villageObj).setDistributionPoint(current);
//			}
//		}
		//		}
	}
	private void checkDistributionLevelUp(EntityPlayer ep){
//		if(UnsagaVillagerCapability.ADAPTER.hasCapability(villager)){
			if(merchant.getDistributionLevel()>MAX_DIST_LEVEL){
				return;
			}
			int threshold = BarteringUtil.calcNextTransactionThreshold(merchant.getDistributionLevel());
			if(merchant.getTransactionPoint()>=threshold){ //取引XPがしきい値を上回れば、流通度のレベルアップ
				int base = merchant.getDistributionLevel();
				merchant.setDistributionLevel(base+1);
				merchant.setTransactionPoint(0);

				//進捗関連
				if(WorldHelper.isServer(ep.getEntityWorld())){
					if(merchant.getDistributionLevel()>=5){
						//						ep.addStat(UnsagaMod.core.achievements.bartering2);
						UnsagaTriggers.BARTERING_TIER1.trigger((EntityPlayerMP) ep);
					}
					if(merchant.getDistributionLevel()>=10){
						//						ep.addStat(UnsagaMod.core.achievements.bartering3);
						UnsagaTriggers.BARTERING_TIER2.trigger((EntityPlayerMP) ep);
					}
				}

			}

			this.syncDistributionLevel();
//		}
//		if(UnsagaVillagerCapability.ADAPTER.hasCapability(villager)){
//			VillagerBarteringImpl impl = (VillagerBarteringImpl) UnsagaVillagerCapability.ADAPTER.getCapability(villager).getImpl();
//			if(impl.getDistributionLevel()>MAX_DIST_LEVEL){
//				return;
//			}
//			int threshold = BarteringUtil.calcNextTransactionThreshold(UnsagaVillagerCapability.ADAPTER.getCapability(villager).getDistributionLevel());
//			if(UnsagaVillagerCapability.ADAPTER.getCapability(villager).getTransactionPoint()>=threshold){
//				int base = UnsagaVillagerCapability.ADAPTER.getCapability(villager).getDistributionLevel();
//				UnsagaVillagerCapability.ADAPTER.getCapability(villager).setDistributionLevel(base+1);
//				UnsagaVillagerCapability.ADAPTER.getCapability(villager).setTransactionPoint(0);
//
//				if(WorldHelper.isServer(ep.getEntityWorld())){
//					if(UnsagaVillagerCapability.ADAPTER.getCapability(villager).getDistributionLevel()>=5){
//						//						ep.addStat(UnsagaMod.core.achievements.bartering2);
//						UnsagaTriggers.BARTERING_TIER2.trigger((EntityPlayerMP) ep);
//					}
//					if(UnsagaVillagerCapability.ADAPTER.getCapability(villager).getDistributionLevel()>=10){
//						//						ep.addStat(UnsagaMod.core.achievements.bartering3);
//						UnsagaTriggers.BARTERING_TIER3.trigger((EntityPlayerMP) ep);
//					}
//				}
//
//			}
//
//			this.syncDistributionLevel();
//		}
	}

	/** 掘り出し物は陳列されたかどうか*/
	public boolean hasDisplayedSecrets(){

//		if(UnsagaVillagerCapability.ADAPTER.hasCapability(villager)){
//			return UnsagaVillagerCapability.ADAPTER.getCapability(villager).hasDisplayedSecretMerchandises();
//		}
		return merchant.hasDisplayedSecretMerchandises();
	}


	/** 掘り出し物の陳列したかどうか…をリセット*/
	public void resetDisplayedSecretMerchandise(){
//		if(UnsagaVillagerCapability.ADAPTER.hasCapability(villager)){
//			UnsagaVillagerCapability.ADAPTER.getCapability(villager).setHasDisplayedSecrets(false);
//		}
		merchant.setHasDisplayedSecrets(false);
	}

	public void updateMerchandises(EntityPlayer ep){

		//品物を入れ替える前はレベルアップチェック
		this.checkDistributionLevelUp(ep);
		//仕入れ時間の記憶
		merchant.setStockedTime(this.world.getTotalWorldTime());
		int distLV = merchant.getDistributionLevel();
		Triplet<Integer,Integer,Integer> generateLevel = this.getGenerateLevels(distLV);
		//ショップタイプに準じて品物の素材・カテゴリを決定
		Set<ToolCategory> category = merchant.getShopType().getAvailableToolCategory();
		Set<UnsagaMaterial> stricts = merchant.getShopType().getAvailableMerchandiseMaterials();
		//生成レベル別に４つと５つ生成
		NonNullList<ItemStack> merchandises1 = factory.createMerchandises(4, generateLevel.first, category,stricts);
		NonNullList<ItemStack> merchandises2 = factory.createMerchandises(5, generateLevel.second, category,stricts);
		//			NonNullList<ItemStack> list = ItemUtil.createStackList(9);


		List<ItemStack> combined = Lists.newArrayList();
		combined.addAll(merchandises1);
		combined.addAll(merchandises2);

//		//魔法アイテム屋なら術アビリティを付与
//		if(impl.getShopType()==BarteringShopType.MAGIC){
//			combined.forEach(in ->{
//				if(AbilityCapability.adapter.hasCapability(in)){
//					IAbility spellAbility = AbilityRegistry.getSpellAbilityFromMaterial(UnsagaMaterialCapability.adapter.getCapability(in).getMaterial());
//					AbilityCapability.adapter.getCapability(in).setAbility(1, spellAbility);
//				}
//			});
//		}
		//			list.set(merchandises1);
		//生成したら村人にセット
		merchant.setMerchandises(ItemUtil.toNonNullList(combined));
		merchant.setSecretMerchandises(ItemUtil.createStackList(9)); //入れ替え時点では掘り出し物は決定されない


//		if(UnsagaVillagerCapability.ADAPTER.hasCapability(villager)){
//
//			this.checkDistributionLevelUp(ep);
//			UnsagaVillagerCapability.ADAPTER.getCapability(villager).setStockedTime(this.world.getTotalWorldTime());
//			int distLV = UnsagaVillagerCapability.ADAPTER.getCapability(villager).getDistributionLevel();
//			Triplet<Integer,Integer,Integer> generateLevel = this.getGenerateLevels(distLV);
//			Set<ToolCategory> category = UnsagaVillagerCapability.ADAPTER.getCapability(villager).getShopType().getAvailableMerchandiseCategory();
//			Set<UnsagaMaterial> stricts = UnsagaVillagerCapability.ADAPTER.getCapability(villager).getShopType().getAvailableMerchandiseMaterials();
//			NonNullList<ItemStack> merchandises1 = factory.createMerchandises(4, generateLevel.first, category,stricts);
//			NonNullList<ItemStack> merchandises2 = factory.createMerchandises(5, generateLevel.second, category,stricts);
//			//			NonNullList<ItemStack> list = ItemUtil.createStackList(9);
//
//
//			List<ItemStack> combined = Lists.newArrayList();
//			combined.addAll(merchandises1);
//			combined.addAll(merchandises2);
//			//			list.set(merchandises1);
//			UnsagaVillagerCapability.ADAPTER.getCapability(villager).setMerchandises(ItemUtil.toNonNullList(combined));
//			UnsagaVillagerCapability.ADAPTER.getCapability(villager).setSecretMerchandises(ItemUtil.createStackList(9));
//		}
	}

	public NonNullList<ItemStack> createSecretMerchandises(int num){
		int distLV = merchant.getDistributionLevel();
		Triplet<Integer,Integer,Integer> generateLevel = this.getGenerateLevels(distLV);
		Set<ToolCategory> category = merchant.getShopType().getAvailableToolCategory();
		Set<UnsagaMaterial> stricts = merchant.getShopType().getAvailableMerchandiseMaterials();
//		NonNullList<ItemStack> merchandises1 = factory.createMerchandises(4, generateLevel.first, category,stricts);
//		NonNullList<ItemStack> merchandises2 = factory.createMerchandises(5, generateLevel.second, category,stricts);
		//生成レベルは３つめを選択してnum個生成
		NonNullList<ItemStack> merchandises3 = factory.createMerchandises(num, generateLevel.third(), category,stricts);
		//			NonNullList<ItemStack> list = ItemUtil.createStackList(9);
		//			list.set(merchandises3);
		//			UnsagaVillagerCapability.adapter.getCapability(villager).setSecretMerchandises(list);

		merchant.setHasDisplayedSecrets(true);

//		if(UnsagaVillagerCapability.ADAPTER.hasCapability(villager) && !UnsagaVillagerCapability.ADAPTER.getCapability(villager).hasDisplayedSecretMerchandises()){
//			int distLV = UnsagaVillagerCapability.ADAPTER.getCapability(villager).getDistributionLevel();
//			Triplet<Integer,Integer,Integer> generateLevel = this.getGenerateLevels(distLV);
//			Set<ToolCategory> category = UnsagaVillagerCapability.ADAPTER.getCapability(villager).getShopType().getAvailableMerchandiseCategory();
//			Set<UnsagaMaterial> stricts = UnsagaVillagerCapability.ADAPTER.getCapability(villager).getShopType().getAvailableMerchandiseMaterials();
//			NonNullList<ItemStack> merchandises1 = factory.createMerchandises(4, generateLevel.first, category,stricts);
//			NonNullList<ItemStack> merchandises2 = factory.createMerchandises(5, generateLevel.second, category,stricts);
//			NonNullList<ItemStack> merchandises3 = factory.createMerchandises(num, generateLevel.third(), category,stricts);
//			//			NonNullList<ItemStack> list = ItemUtil.createStackList(9);
//			//			list.set(merchandises3);
//			//			UnsagaVillagerCapability.adapter.getCapability(villager).setSecretMerchandises(list);
//			UnsagaVillagerCapability.ADAPTER.getCapability(villager).setHasDisplayedSecrets(true);
//			return merchandises3;
//		}
		return merchandises3;
	}
	/**
	 * 三種の生成レベルを得る。
	 * 生成レベル１：店レベル/10・・・商品の半分<br>
	 * 生成レベル２：(店レベル +流通レベル*4)/10・・・商品の半分<br>
	 * 生成レベル３：生成レベル２に＋２したもの・・・目利き<br>
	 */
	private Triplet<Integer,Integer,Integer> getGenerateLevels(int distLV){
		int generateLevel1 = MathHelper.clamp(this.calcShopLevel(villager)/10,2,20);
		int generateLevel2 = MathHelper.clamp((this.calcShopLevel(villager) + distLV * 4)/10,2,20);
		int generateLevel3 = MathHelper.clamp(generateLevel2  + 2, 2,20);
		return Triplet.of(generateLevel1, generateLevel2, generateLevel3);
	}
	/**
	 * 店のレベルを計算。
	 * ベース店レベル＋村の半径/3+村人数/3
	 * @param villager
	 * @return
	 */
	public int calcShopLevel(EntityVillager villager){
		Village village = villager.getEntityWorld().getVillageCollection().getNearestVillage(villager.getPosition(), 32);

		if(village!=null){
			return (int)(village.getVillageRadius() / 3 + village.getNumVillagers() / 3) + merchant.getBaseShopLevel();
		}else{
			return merchant.getBaseShopLevel();
		}

//		if(UnsagaVillagerCapability.ADAPTER.hasCapability(villager)){
//			if(village!=null){
//				return (int)(village.getVillageRadius() / 3 + village.getNumVillagers() / 3) + UnsagaVillagerCapability.ADAPTER.getCapability(villager).getBaseShopLevel();
//			}else{
//				return UnsagaVillagerCapability.ADAPTER.getCapability(villager).getBaseShopLevel();
//			}
//		}

//		return 1;
	}
}
