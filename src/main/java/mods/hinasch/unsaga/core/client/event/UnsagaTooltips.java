package mods.hinasch.unsaga.core.client.event;

import java.util.Collection;
import java.util.List;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import joptsimple.internal.Strings;
import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.ability.AbilityCapability;
import mods.hinasch.unsaga.ability.AbilityPotentialTableProvider;
import mods.hinasch.unsaga.ability.IAbilityAttachable;
import mods.hinasch.unsaga.common.UnsagaWeight;
import mods.hinasch.unsaga.common.item.ItemWeaponUnsaga;
import mods.hinasch.unsaga.common.item.UnsagaToolUtil;
import mods.hinasch.unsaga.core.client.gui.GuiBartering;
import mods.hinasch.unsaga.material.IUnsagaCategoryTool;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.material.UnsagaMaterialCapability;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import mods.hinasch.unsaga.minsaga.IMinsagaForge;
import mods.hinasch.unsaga.minsaga.MaterialLayer;
import mods.hinasch.unsaga.minsaga.MinsagaForgingCapability;
import mods.hinasch.unsaga.minsaga.MinsagaMaterialInitializer;
import mods.hinasch.unsaga.minsaga.MinsagaUtil;
import mods.hinasch.unsaga.util.UnsagaTextFormatting;
import mods.hinasch.unsaga.villager.bartering.BarteringUtil;
import mods.hinasch.unsaga.villager.bartering.MerchandiseCapability;
import mods.hinasch.unsagamagic.enchant.UnsagaEnchantmentCapability;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;

public class UnsagaTooltips {

	public static void addTooltips(ItemStack is, List<String> dispList, ITooltipFlag par4,Type... types){
		for(Type type:types){
			type.getTooltip(is, dispList, par4);
		}
	}
	public static void addTooltips(ItemStack is, List<String> dispList, ITooltipFlag par4,Collection<Type> types){
		for(Type type:types){
			type.getTooltip(is, dispList, par4);
		}
	}
	public static enum Type{
		DEBUG,ABILITY,MATERIAL_AND_WEIGHT,
		@Deprecated
		ABILITY_TO_LEARN
		,BOW,SHIELD,ACCESSORY_ARMOR,ENCHANTMENT_REMAIN,TECH_BOOK
		,MINSAGA_MATERIALS,MINSAGA_MODIFIERS,DURABILITY,MERCHANT_PRICE;


		public void getTooltip(ItemStack is, List dispList, ITooltipFlag par4) {
			boolean isShowSplitter = true;
			//Worldがnullの場合もある(==ClientPlayerがnull)ので注意
			UnsagaMaterial m = UnsagaMaterialCapability.adapter.hasCapability(is) ?UnsagaMaterialCapability.adapter.getCapability(is).getMaterial() : UnsagaMaterials.DUMMY;
			switch(this){

			case ABILITY_TO_LEARN:
//				if(AbilityCapability.adapter.hasCapability(is) && ClientHelper.getPlayer()!=null &&ClientHelper.getPlayer().isCreative()){
//					String abilityNamesToLearn  = AbilityAPI.getAbilityTable(is,ClientHelper.getPlayer()).stream().map(in -> Joiner.on(",").join(in)).collect(Collectors.joining("/"));
//					dispList.add(UnsagaTextFormatting.PROPERTY_LOCKED+HSLibs.translateKey("tooltip.unsaga.ability.learnable")+":"+abilityNamesToLearn);
//				}
				break;
			case ABILITY:
				if(AbilityCapability.adapter.hasCapability(is)){
					IAbilityAttachable instance = AbilityCapability.adapter.getCapability(is);
					instance.getAbilitySlots().addTooltips(dispList, ClientHelper.getPlayer(), is);




				}
				break;
			case SHIELD:
				if(m.isEmpty())return;
				dispList.add(UnsagaTextFormatting.POSITIVE+HSLibs.translateKey("tooltip.unsaga.shield_desc"));
				dispList.add(UnsagaTextFormatting.POSITIVE+HSLibs.translateKey("tooltip.unsaga.shield_avoid", m.shieldValue()));
				dispList.add(UnsagaTextFormatting.POSITIVE+HSLibs.translateKey("tooltip.unsaga.shield_block", m.shieldValue()+33));
				break;
			case DEBUG:

				if(HSLib.isDebug() && m!=null){
					dispList.add("Tool Material:"+m.getToolMaterial().name());
					dispList.add("Armor Material:"+m.getArmorMaterial());
					float multiply = 1.0F;
					if(is.getItem() instanceof ItemWeaponUnsaga){
						multiply = ((ItemWeaponUnsaga)is.getItem()).getMaxDamageMultiply();
					}
					if(is.getItem() instanceof IUnsagaCategoryTool){
						IUnsagaCategoryTool unsagaweapon = (IUnsagaCategoryTool) is.getItem();

						dispList.add("Max Uses:"+unsagaweapon.getComponent().getMaxDamage(is) * multiply);
					}
				}

				break;
			case BOW:
				dispList.add(UnsagaTextFormatting.POSITIVE+"Bow Power +"+UnsagaToolUtil.getMaterial(is).getBowModifier());
				break;
			case MATERIAL_AND_WEIGHT:
				if(m.isEmpty())return;
				UnsagaWeight weight = UnsagaMaterialCapability.adapter.getCapability(is).getWeight();
//				UnsagaWeightType weightType = UnsagaWeightType.fromWeight(weight);
				String str = HSLibs.translateKey("tooltip.unsaga.material")+":"+HSLibs.translateKey("material."+m.getUnlocalizedName());
				dispList.add(str);

				String weightString = HSLibs.translateKey("word.weight") + ":" + weight.getValue();
				weightString += "(" + weight.type().getName() + ")";

				if(weightString!=null){
					dispList.add(weightString);
				}
				break;
			case ACCESSORY_ARMOR:
				if(m!=UnsagaMaterials.DUMMY){
					int modifier = AbilityPotentialTableProvider.TABLE_PASSIVE.getArmorModifier(m);
					if(modifier>0){
						dispList.add(UnsagaTextFormatting.POSITIVE+HSLibs.translateKey("tooltip.unsaga.accessory_modifier", modifier));
					}
				}
				break;
			case ENCHANTMENT_REMAIN:
				if(ClientHelper.getWorld()!=null){
//					UnsagaEnchantmentCapability.adapter.getCapability(is).getEntries().stream()
//					.sorted((o1,o2)->o1.getKey().compareTo(o2.getKey()))
//					.forEach(in ->{
//						EnchantmentProperty p = in.getKey();
//						EnchantmentState state = in.getValue();
//						String s = p.getLocalized() + " " + (state.getLevel() == 1 ? "" : HSLibs.translateKey("enchantment.level."+state.getLevel()));
//						long remain = in.getValue().getExpireTime() - ClientHelper.getWorld().getTotalWorldTime();
//						if(remain>0){
//							s += "/"+String.format("Time Remaining:%d", (HSLibs.exceptZero((int) remain))/30);
//						}
//
//						dispList.add(UnsagaTextFormatting.POSITIVE+s);
//					});

					UnsagaEnchantmentCapability.adapter.getCapability(is).getInstantEnchantments().entrySet()
					.stream().forEach(in ->{
						String s = HSLibs.translateKey(in.getKey().getName());
						long remainings = in.getValue() - ClientHelper.getWorld().getTotalWorldTime();
						if(remainings>0){
							s += "/"+String.format("Time Remaining:%d", (HSLibs.exceptZero((int) remainings))/30);
							dispList.add(UnsagaTextFormatting.POSITIVE+s);
						}

					});
				}
				break;
			case MINSAGA_MATERIALS: //ミンサガ補強後のツール表示
				IMinsagaForge instance = MinsagaForgingCapability.ADAPTER.getCapability(is);
				String forgedMaterials = instance.layers().stream().map((in)->{

					if(!in.isEmptyLayer()){
						String color = Strings.EMPTY;
						if(in.hasFinishedFitting()){
							color = UnsagaTextFormatting.POSITIVE.toString();
						}
						return color + in.getMaterial().getLocalized() + TextFormatting.GRAY;
					}
					return "--";
				}).collect(Collectors.joining("/"));
				dispList.add(forgedMaterials);
				MaterialLayer current = instance.getCurrentFittingLayer();
				if(GuiScreen.isShiftKeyDown()){
					if(!current.isEmptyLayer()){
						dispList.add(current.getFittingProgress()+"/"+current.maxFittingProgress());
					}
					if(instance.hasForged()){
						dispList.addAll(MinsagaUtil.buildModifierTips(instance,OptionalInt.empty()));
						dispList.add(UnsagaTextFormatting.PROPERTY+instance.getAbilities().stream().map(in -> in.getLocalized()).collect(Collectors.joining("/")));
					}
				}else{
					dispList.add("[Shift Key]Customized Info");
				}
				break;
			case MINSAGA_MODIFIERS: //ミンサガ補強の素材表示
				MinsagaMaterialInitializer.find(is)
				.ifPresent(material ->{
					dispList.add(HSLibs.translateKey("word.minsaga.material")+":"+material.getLocalized());
					List<String> strings = MinsagaUtil.buildModifierTips(material,OptionalInt.of(material.getRepairDamage()));
					dispList.addAll(strings);
					if(material.hasAbilities()){
						//						String ab = material.get().getAbilities().stream().map(in -> in.getName()).collect(Collectors.joining("/"));
						dispList.add(UnsagaTextFormatting.PROPERTY+material.abilities().stream().map(in -> in.getLocalized()).collect(Collectors.joining("/")));
					}
				});
				break;
			case DURABILITY:
				int du = is.getMaxDamage() - is.getItemDamage();
				dispList.add("Durability:"+du+"/"+is.getMaxDamage());

				break;
			case MERCHANT_PRICE:
				if(MerchandiseCapability.ADAPTER.getCapability(is).canSell(is)){
					GuiBartering gui = (GuiBartering) ClientHelper.getCurrentGui();
					OptionalInt originalPrice = OptionalInt.empty();
					gui.setMerchandiseTag();
					int price = MerchandiseCapability.ADAPTER.getCapability(is).getPrice(is);
					if(MerchandiseCapability.ADAPTER.getCapability(is).isMerchadise()){
						price *= BarteringUtil.MERCHANDISE_PRICE;
					}

					originalPrice = OptionalInt.of(price);
					price = BarteringUtil.getDiscountPrice(price, gui.getDiscount());
					if(price!=originalPrice.getAsInt() && MerchandiseCapability.ADAPTER.getCapability(is).isMerchadise()){
						dispList.add(UnsagaTextFormatting.SIGNIFICANT+String.format("Price:%d(%d)",price,originalPrice.getAsInt()));
					}else{
						dispList.add(UnsagaTextFormatting.SIGNIFICANT+"Price:"+price);
					}

				}
			default:
				break;

			}
		}
	}

}
