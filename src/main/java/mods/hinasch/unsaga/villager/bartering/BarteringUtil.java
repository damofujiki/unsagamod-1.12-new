package mods.hinasch.unsaga.villager.bartering;

import com.mojang.realmsclient.util.Pair;

import mods.hinasch.unsaga.skillpanel.SkillPanelAPI;
import mods.hinasch.unsaga.skillpanel.SkillPanels;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.MathHelper;

public class BarteringUtil {

	/** 売値にこれを掛けたものが商品価格*/
	public static final float MERCHANDISE_PRICE = 1.6F;
	/** 基本価格に掛かる値*/
	public static final float BASE_PRICE = 0.2F;
	/** 原料の場合の掛け値（ツールでは無い場合にこの掛け率が使われる）*/
	public static final float RAW_MATERIAL_PRICE = 0.3F;
//	public static final float BUY_PRICE_MULTIPLY = 1.5F;
	public static int getDiscountPrice(int basePrice,DiscountPair discounts){
		int price = basePrice;
		price -= (int)((float)basePrice*0.05F*(float)discounts.priceDown());
		price += (int)((float)basePrice*0.05F*(float)discounts.priceUp());
		price = MathHelper.clamp(price, 1, Integer.MAX_VALUE);
		return price;
	}


	/** 流通レベルから次の流通XPを計算*/
	public static int calcNextTransactionThreshold(int distLV){
		return (distLV+1) * (distLV>10 ? 2000 : distLV>5 ? 1000 : 500);
	}
	public static DiscountPair applyDiscount(EntityPlayer ep){

		int priceDown = SkillPanelAPI.getHighestPanelLevel(ep, SkillPanels.MONGER)
				.orElse(0);
		int additional = SkillPanelAPI.hasPanel(ep, SkillPanels.FASHIONABLE) ? 1 : 0;
		int priceUp = 0;
		return new DiscountPair(priceDown+additional,priceUp);
	}

	public static DiscountPair applyGratuity(EntityPlayer ep){

		int priceUp = SkillPanelAPI.getHighestPanelLevel(ep, SkillPanels.MAHARAJA)
				.orElse(0);
		int additional = SkillPanelAPI.hasPanel(ep, SkillPanels.FASHIONABLE) ? 1 : 0;

		int priceDown = 0;
		return new DiscountPair(priceDown,priceUp+additional);
	}

	public static class DiscountPair extends Pair<Integer,Integer>{

		public DiscountPair(Integer priceDown, Integer priceUp) {
			super(priceUp, priceDown);
			// TODO 自動生成されたコンストラクター・スタブ
		}

		public int priceUp(){
			return this.first();
		}

		public int priceDown(){
			return this.second();
		}
	}
}
