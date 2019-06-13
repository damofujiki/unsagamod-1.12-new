package mods.hinasch.unsaga.core.inventory.container;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import javax.annotation.Nullable;

import mods.hinasch.lib.container.ContainerBase;
import mods.hinasch.lib.network.PacketGuiButtonBaseNew;
import mods.hinasch.lib.util.ChatHandler;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.Statics;
import mods.hinasch.lib.world.WorldHelper;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.common.item.ComponentUnsagaWeapon;
import mods.hinasch.unsaga.core.client.gui.GuiToolCustomizeMinsaga;
import mods.hinasch.unsaga.core.inventory.InventorySmithMinsaga;
import mods.hinasch.unsaga.core.inventory.slot.SlotMinsagaSmith;
import mods.hinasch.unsaga.core.net.packet.PacketGuiButtonUnsaga;
import mods.hinasch.unsaga.init.UnsagaGui;
import mods.hinasch.unsaga.material.UtilUnsagaMaterial;
import mods.hinasch.unsaga.minsaga.IMinsagaForge;
import mods.hinasch.unsaga.minsaga.MinsagaForgingCapability;
import mods.hinasch.unsaga.minsaga.MinsagaMaterialInitializer;
import net.minecraft.block.BlockAnvil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class ContainerToolCustomizeMinsaga extends ContainerBase{


	public static final int EXP_REQUIRE = 5;
    protected static final UUID UUID_MINSAGA = UUID.fromString("848258b3-a37c-4619-9b2b-b9e94526c906");

	protected InventorySmithMinsaga inventorySmith;
	public ContainerToolCustomizeMinsaga(EntityPlayer ep) {
		super(ep, new InventorySmithMinsaga());
		if(this.getAnvilPositionNear()==null){
			ChatHandler.sendChatToPlayer(ep, HSLibs.translateKey("gui.smith.minsaga.no_anvil"));
		}
		this.inventorySmith = (InventorySmithMinsaga) this.inv;

		UnsagaMod.logger.trace(this.getClass().getName(), "called");
//		this.addSlotToContainer(new SlotSmith(this.inventorySmith, inventorySmith, 28, 53)); //Emerald
		this.addSlotToContainer(new SlotMinsagaSmith.Base(this.inv, inventorySmith.INV_BASE, 28, 53-(18*2))); //Base Material
		this.addSlotToContainer(new SlotMinsagaSmith.Material(this.inv,inventorySmith.INV_SUB, 28+(18*2)-8, 53-(18*2))); //Material2
		this.addSlotToContainer(new SlotMinsagaSmith.Forged(this.inv, inventorySmith.INV_FORGED, 28+(18*6)+1, 52));
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return this.getAnvilPositionNear()!=null;
	}

	@Override
	public PacketGuiButtonBaseNew getPacketGuiButton(int guiID, int buttonID, NBTTagCompound args) {
		// TODO 自動生成されたメソッド・スタブ
		return PacketGuiButtonUnsaga.create(UnsagaGui.Type.SMITH_MINSAGA,buttonID,args);
	}

	@Override
	public SimpleNetworkWrapper getPacketPipeline() {
		// TODO 自動生成されたメソッド・スタブ
		return UnsagaMod.PACKET_DISPATCHER;
	}

	@Override
	public int getGuiID() {
		// TODO 自動生成されたメソッド・スタブ
		return UnsagaGui.Type.SMITH_MINSAGA.getMeta();
	}

	@Override
	public void onPacketData() {
//		UnsagaMod.logger.trace("id", this.buttonID);
		if(this.buttonID==GuiToolCustomizeMinsaga.ID_FORGE){

			Optional.ofNullable(this.getAnvilPositionNear())
			.ifPresent(this::doForge);


		}
	}

	private void doForge(XYZPos pos){
		IBlockState anvil = ep.getEntityWorld().getBlockState(pos);

		if(!this.hasExp(ep)){
			ChatHandler.sendChatToPlayer(ep, HSLibs.translateKey("gui.smith.minsaga.no_xp",EXP_REQUIRE));
			return;
		}
		if(this.isStackInSlots()){
			ItemStack newTool = this.inventorySmith.getBaseItem().copy();
			ItemStack materialStack = this.inventorySmith.getMaterial();
			MinsagaForgingCapability.ADAPTER.getCapabilityOptional(newTool)
			.ifPresent(in ->{
				MinsagaMaterialInitializer.find(materialStack)
				.ifPresent(material ->{
					in.addLayer(material);
					this.inventorySmith.setForged(newTool);
					this.inventorySmith.consumeItems();
					this.consumeExp(ep);
					this.updateWeaponModifier(newTool);
					this.updateAnvilState(ep.world, ep, pos, anvil);
				});

			});

		}
	}
	public boolean isStackInSlots(){
		return this.hasBaseTool() && this.hasMaterialStack();
	}

	public @Nullable XYZPos getAnvilPositionNear(){
		List<XYZPos> list = WorldHelper.findNear(ep.world, XYZPos.createFrom(ep), 5, 3, (w,p,scanner)->scanner.getBlock()==Blocks.ANVIL);
		if(list.isEmpty()){
			return null;
		}

		XYZPos pos = list.get(0);
		return pos;

	}
	public void updateWeaponModifier(ItemStack forged){

		if(MinsagaForgingCapability.ADAPTER.hasCapability(forged)){
			IMinsagaForge capa = MinsagaForgingCapability.ADAPTER.getCapability(forged);
			int weight = (UtilUnsagaMaterial.getWeight(forged).orElse(0)) + capa.weightModifier();;
			ComponentUnsagaWeapon.refleshWeightModifier(forged, weight);
		}
	}

	public AttributeModifier getModifier(String type,float f){
		return new AttributeModifier(UUID_MINSAGA,"Minsaga Modifier "+type,f,Statics.OPERATION_INCREMENT);
	}


	public void updateAnvilState(World worldIn,EntityPlayer playerIn,BlockPos blockPosIn,IBlockState iblockstate){
		float breakChance = 0.13F;
        if (!playerIn.capabilities.isCreativeMode && !worldIn.isRemote && iblockstate.getBlock() == Blocks.ANVIL && playerIn.getRNG().nextFloat() < breakChance)
        {
            int l = ((Integer)iblockstate.getValue(BlockAnvil.DAMAGE)).intValue();
            ++l;

            if (l > 2)
            {
                worldIn.setBlockToAir(blockPosIn);
                worldIn.playEvent(1029, blockPosIn, 0);
            }
            else
            {
                worldIn.setBlockState(blockPosIn, iblockstate.withProperty(BlockAnvil.DAMAGE, Integer.valueOf(l)), 2);
                worldIn.playEvent(1030, blockPosIn, 0);
            }
        }
        else if (!worldIn.isRemote)
        {
            worldIn.playEvent(1030, blockPosIn, 0);
        }
	}
	public boolean hasBaseTool(){

		return !this.inventorySmith.getBaseItem().isEmpty() && this.inventorySmith.getBaseItem().getItem().isRepairable();
	}

	public boolean hasMaterialStack(){
		return !this.inventorySmith.getMaterial().isEmpty() && MinsagaMaterialInitializer.find(this.inventorySmith.getMaterial()).isPresent();
	}

	public void consumeExp(EntityPlayer ep){
		if(!ep.capabilities.isCreativeMode){
			ep.experienceLevel -= EXP_REQUIRE;
		}
	}

	public boolean hasExp(EntityPlayer ep){
		if(ep.capabilities.isCreativeMode){
			return true;
		}
		if(ep.experienceLevel>=EXP_REQUIRE){
			return true;
		}
		return false;
	}
}
