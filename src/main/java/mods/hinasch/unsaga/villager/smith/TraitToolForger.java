package mods.hinasch.unsaga.villager.smith;

import java.util.Random;

import mods.hinasch.unsaga.core.inventory.container.ContainerBlacksmithUnsaga;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.util.ToolCategory;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;

/**
 *
 * コンテナから呼び出される村人の鍛冶機能
 *
 */
public class TraitToolForger {

	ContainerBlacksmithUnsaga parentContainer;

	public static enum ForgeResultType{
		/** アビリティを忘れる*/
		BAD,
		/** 普通*/
		GOOD,
		/** アビリティを引き出す*/
		VERY_GOOD;

		public SoundEvent getResultSound(){
			switch(this){
			case BAD:
				return SoundEvents.BLOCK_ANVIL_DESTROY;
			case GOOD:
				return SoundEvents.BLOCK_ANVIL_USE;
			case VERY_GOOD:
				return SoundEvents.BLOCK_ANVIL_PLACE;
			default:
				break;

			}
			return SoundEvents.BLOCK_ANVIL_DESTROY;
		}
	}
	Random rand;
	public TraitToolForger(Random rand,ContainerBlacksmithUnsaga parent){
		this.rand = rand;
		this.parentContainer = parent;
	}

	public ValidPayments.Value getPaymentValue(){
		return ValidPayments.findValue(this.parentContainer.getPaymentStack()).get();

	}

	public BlacksmithType smithType(){
		return this.parentContainer.getSmithType();
	}

	public ToolCategory getToolCategory(){
		return this.parentContainer.getCurrentCategory();
	}
	public BlacksmithToolBaker toolBuilder(UnsagaMaterial base,UnsagaMaterial sub,ItemStack baseStack,ItemStack subStack){
		return new BlacksmithToolBaker(this, base, baseStack, sub, subStack);
	}

	public static class ForgeResult{

		final ForgeResultType type;
		final ItemStack result;

		public ForgeResult(ForgeResultType type,ItemStack result){
			this.type = type;
			this.result = result;
		}

		public ForgeResultType type(){
			return this.type;
		}

		public ItemStack getResult(){
			return this.result;
		}

	}
}
