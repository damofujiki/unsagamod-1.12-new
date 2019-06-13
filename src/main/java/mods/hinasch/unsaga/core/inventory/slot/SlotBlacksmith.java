package mods.hinasch.unsaga.core.inventory.slot;

import mods.hinasch.unsaga.core.inventory.container.ContainerBlacksmithUnsaga;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class SlotBlacksmith{


	public static class Material extends Slot{

		public Material(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
			// TODO 自動生成されたコンストラクター・スタブ
		}

		@Override
	    public int getSlotStackLimit()
	    {
	        return ContainerBlacksmithUnsaga.STACKLIMIT_MATERIAL;
	    }
	}
	public static class Payment extends Slot{

		public Payment(IInventory inventoryIn, int index, int xPosition, int yPosition) {
			super(inventoryIn, index, xPosition, yPosition);
			// TODO 自動生成されたコンストラクター・スタブ
		}

		@Override
	    public int getSlotStackLimit()
	    {
	        return 64;
	    }
	}
}
