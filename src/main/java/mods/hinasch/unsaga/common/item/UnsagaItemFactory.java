package mods.hinasch.unsaga.common.item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.mojang.realmsclient.util.Pair;

import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.item.WeightedRandomStack;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.ability.AbilityAPI;
import mods.hinasch.unsaga.ability.AbilityCapability;
import mods.hinasch.unsaga.ability.slot.AbilitySlotList;
import mods.hinasch.unsaga.init.UnsagaItemRegisterer;
import mods.hinasch.unsaga.material.MaterialSuitabilityRegistry;
import mods.hinasch.unsaga.material.UnsagaIngredients;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.material.UnsagaMaterialInitializer;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import mods.hinasch.unsaga.util.ToolCategory;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.WeightedRandom;
import net.minecraft.util.math.MathHelper;

/**
 * チェストの中身を生成するための基本クラス
 */
public class UnsagaItemFactory {
	/** 失敗武器になった場合適合する素材に変わる確率(rand*xx==0)*/
	public static final float FAILED_TRANSFORM = 0.60F;
	/** アビリティ付与確率*/
	public static final int ATTACH_ABILITY = 5;

	public static class WeightedRandomRank extends WeightedRandom.Item{

		public final int number;




		public WeightedRandomRank(int weight,int num) {
			super(weight);
			this.number = num;

		}

	}

	public Random random;
	public UnsagaItemFactory(Random rand){
		this.random = rand;
	}

	/**
	 *
	 * @param amount 生成する数
	 * @param generateLevel 生成レベル
	 * @param orderCategories 作るカテゴリ
	 * @param orderedMaterials 素材の指定
	 * @return
	 */
	public NonNullList<ItemStack> createMerchandises(int amount,int generateLevel,final Collection<ToolCategory> orderCategories,final Set<UnsagaMaterial> orderedMaterials){


		//指定素材から最低レベルを調べる
		int minLevel = orderedMaterials.stream().mapToInt(in -> in.rank()).min().isPresent() ? orderedMaterials.stream().mapToInt(in -> in.rank()).min().getAsInt() : 3;
		//生成レベルより最低レベルのが高ければそっちに合わせる（作れないので）
		int fixedLevel = minLevel>generateLevel ? minLevel : generateLevel;

		NonNullList<ItemStack> merchandises = ItemUtil.createStackList(amount);
		for(int i=0;i<amount;i++){
			//			int rank = random.nextInt(generateLevel);
//			List<WeightedRandomStack> weighted = Lists.newArrayList();


			Supplier<WeightedRandomStack> weightedStackSupplier = () ->{
				List<UnsagaMaterial> materials = UnsagaMaterialInitializer.instance()
						.getMerchandiseMaterials(0,fixedLevel)
						.stream()
						.filter(in -> orderedMaterials.contains(in))
						.collect(Collectors.toList());

				Pair<UnsagaMaterial, ItemStack> ordered = this.orderItemRandom(materials, orderCategories);
				if(!ordered.second().isEmpty()){
					return new WeightedRandomStack(this.calcWeight(ordered.first().rank(),fixedLevel),ordered.second());
				}
				return new WeightedRandomStack(10,ITEMSTACK_FALLBACK);
			};
			List<WeightedRandomStack> weighted =
					Stream.generate(weightedStackSupplier)
					.limit(5)
					.collect(Collectors.toList());

//			//５つのアイテムからなら重みリストを作る
//			for(int j=0;j<5;j++){
//				//取扱素材から選ぶ
//				List<UnsagaMaterial> materials = UnsagaMaterialRegisterer.instance()
//						.getMerchandiseMaterials(0,fixedLevel)
//						.stream()
//						.filter(in -> orderedMaterials.contains(in))
//						.collect(Collectors.toList());
//
//				Pair<UnsagaMaterial, ItemStack> ordered = this.orderItemRandom(materials, orderCategories);
//				if(!ordered.second().isEmpty()){
//					weighted.add(new WeightedRandomStack(this.calcWeight(ordered.first().rank(),fixedLevel),ordered.second()));
//				}
//
//
//			}

			ItemStack stack = this.selectRandomItemSafe(weighted, fixedLevel);
			merchandises.set(i,stack);
		}
		//		UnsagaMod.logger.trace("item", merchandises);
		return merchandises;
	}

	private Pair<UnsagaMaterial, ItemStack> orderItemRandom(Collection<UnsagaMaterial> mate,Collection<ToolCategory> cate){
		if(!mate.isEmpty() && !cate.isEmpty()){
			UnsagaMaterial m = HSLibs.randomPick(random, mate);
			ToolCategory ca = HSLibs.randomPick(random, cate);
			return Pair.of(m,this.orderItem(ca,m));
		}
		return Pair.of(UnsagaMaterials.DUMMY,ItemStack.EMPTY);
	}
	/**
	 * 重みつきリストから一つ選ぶ。もし空のものができてしまった場合廃石を入れておく。
	 * @param weighted
	 * @param fixedLevel 生成レベル（作れるレベルに直されたもの）
	 * @return
	 */
	private @Nonnull ItemStack selectRandomItemSafe(List<WeightedRandomStack> weighted,int fixedLevel){
		return Optional.of(WeightedRandom.getRandomItem(random, weighted).is)
		.filter(in ->!in.isEmpty())
		.orElse(ITEMSTACK_FALLBACK);
	}

	public static final UnsagaMaterial MATERIAL_FALLBACK = UnsagaMaterials.FEATHER;
	public static final ItemStack ITEMSTACK_FALLBACK = new ItemStack(Items.FEATHER);

	/**
	 * カテゴリと素材から実際にアイテムをオーダーする。
	 * 素材が適合しない場合、出来損ない武器になる。
	 *
	 *
	 * @param chosenCategory
	 * @param chosenMaterial
	 * @return
	 */
	private ItemStack orderItem(final ToolCategory chosenCategory,final UnsagaMaterial chosenMaterial){

		if(chosenCategory==null){

		}

		//素材原料の場合、関連つけられた原料を返す。
		if(chosenCategory==ToolCategory.INGREDIENT){
			return Optional.of(UnsagaIngredients.makeStack(chosenMaterial,1))
					.filter(in -> !in.isEmpty())
					.orElse(ITEMSTACK_FALLBACK);
		}else{
			Supplier<UnsagaMaterial> finalMaterialSupplier = ()->{
				if(!chosenCategory.getSuitables().isEmpty()){
					//出来損ない武器になってしまう場合、確率で他の適合する素材になる
					if(!chosenMaterial.isSuitable(chosenCategory) && random.nextFloat()<FAILED_TRANSFORM){
						return Optional.of(MaterialSuitabilityRegistry.instance().getSuitableMerchadises(chosenCategory))
								.map(in -> HSLibs.randomPick(random, in))
								.orElse(MATERIAL_FALLBACK);
					}

				}
				return chosenMaterial;
			};

			ItemStack stack = UnsagaItemRegisterer.createStack(chosenCategory.getAssociatedItem(), finalMaterialSupplier.get(),0);
			this.postOrderedItem(stack);
			return stack;
		}

	}

	/** できたアイテムに何か加える場合*/
	protected void postOrderedItem(ItemStack ordered){
		//ランダムでダメージを与える
		if(this.random.nextInt(10)==0){
			this.applyRandomDamageItemStack(ordered);
		}

		if(random.nextInt(ATTACH_ABILITY)==0){
			this.putRandomAbility(ordered, random);
		}
	}
	private void putRandomAbility(ItemStack stack,Random rand){
		AbilityCapability.adapter.getCapabilityOptional(stack)
		.ifPresent(in ->{
			AbilitySlotList abilityList = in.getAbilitySlots();
			if(abilityList.existLearnableSlot()){
				for(int i=0;i<rand.nextInt(4);i++){
					AbilityAPI.attachRandomAbility(rand, stack,Optional.empty());
				}
			}
		});

	}
	private int calcWeight(int rank,int generateLevel){
		if(rank<=6){
			int c = MathHelper.clamp(generateLevel, 0, 30);
			return (100 - c) - rank*rank;
		}
		int b = generateLevel - 9;
		b = b < 0 ? 0 : b;
		int a = 11 - rank + (b*3);
		//		return a<1 ? 1 : a;
		return a<1 ? 1 : a;
	}

	protected void applyRandomDamageItemStack(ItemStack is){
		if(is.isItemStackDamageable()){
			int maxd = is.getMaxDamage();
			int damage = this.random.nextInt(maxd);
			is.setItemDamage(damage);
		}
	}

	//	private int drawRank(int generateLevel,Random random){
	//		List<WeightedRandomRank> list = this.prepareWeightedList(generateLevel);
	//		WeightedRandomRank w = WeightedRandom.getRandomItem(random, list);
	//		return w.number;
	//	}
	//	private List<WeightedRandomRank> prepareWeightedList(int generateLevel){
	//		List<WeightedRandomRank> list = Lists.newArrayList();
	//		int a = generateLevel <=2 ? 2 : generateLevel;
	//		generateLevel = generateLevel >= 20 ? 20 : generateLevel;
	//		for(int i=0;i<a;i++){
	//			list.add(this.getWeighted(i, generateLevel));
	//		}
	//		return list;
	//	}
	//	private WeightedRandomRank getWeighted(int requireRank,int generateLevel){
	//		if(generateLevel<requireRank){
	//			return new WeightedRandomRank(1,requireRank);
	//		}
	//		if(requireRank<=6){
	//			int a = -10 * requireRank + 100;
	//			return new WeightedRandomRank(a,requireRank);
	//		}
	//		int b = generateLevel - 9;
	//		b = b < 0 ? 0 : b;
	//		int a = - 5 * (requireRank -7) + (10 + b * 2);
	//		a = a < 1 ? 1 : a;
	//		return new WeightedRandomRank(a,requireRank);
	//	}
}
