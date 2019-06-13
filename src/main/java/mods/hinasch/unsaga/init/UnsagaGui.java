package mods.hinasch.unsaga.init;

import java.util.Optional;

import javax.annotation.Nullable;

import mods.hinasch.lib.capability.VillagerHelper;
import mods.hinasch.lib.client.GuiTextMenu;
import mods.hinasch.lib.client.IGuiAttribute;
import mods.hinasch.lib.container.ContainerTextMenu;
import mods.hinasch.lib.iface.IModBase;
import mods.hinasch.lib.misc.Triplet;
import mods.hinasch.lib.network.PacketOpenGui;
import mods.hinasch.lib.util.ChatHandler;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.chest.ChestCapability;
import mods.hinasch.unsaga.chest.IChestBehavior;
import mods.hinasch.unsaga.core.client.gui.GuiBartering;
import mods.hinasch.unsaga.core.client.gui.GuiBlacksmithUnsaga;
import mods.hinasch.unsaga.core.client.gui.GuiCarrier;
import mods.hinasch.unsaga.core.client.gui.GuiChest;
import mods.hinasch.unsaga.core.client.gui.GuiEquipment;
import mods.hinasch.unsaga.core.client.gui.GuiMentor;
import mods.hinasch.unsaga.core.client.gui.GuiSkillPanel;
import mods.hinasch.unsaga.core.client.gui.GuiToolCustomizeMinsaga;
import mods.hinasch.unsaga.core.client.gui.GuiUnsagaMap;
import mods.hinasch.unsaga.core.entity.UnsagaActionCapability;
import mods.hinasch.unsaga.core.inventory.container.ContainerBartering;
import mods.hinasch.unsaga.core.inventory.container.ContainerBlacksmithUnsaga;
import mods.hinasch.unsaga.core.inventory.container.ContainerCarrier;
import mods.hinasch.unsaga.core.inventory.container.ContainerChestUnsaga;
import mods.hinasch.unsaga.core.inventory.container.ContainerEquipment;
import mods.hinasch.unsaga.core.inventory.container.ContainerMentor;
import mods.hinasch.unsaga.core.inventory.container.ContainerSkillPanel;
import mods.hinasch.unsaga.core.inventory.container.ContainerToolCustomizeMinsaga;
import mods.hinasch.unsaga.core.inventory.container.ContainerUnsagaMap;
import mods.hinasch.unsaga.villager.IInteractionState;
import mods.hinasch.unsagamagic.client.gui.GuiTabletDeciphering;
import mods.hinasch.unsagamagic.client.gui.blender.GuiBlender;
import mods.hinasch.unsagamagic.inventory.container.ContainerBlender;
import mods.hinasch.unsagamagic.inventory.container.ContainerTabletDeciphering;
import mods.hinasch.unsagamagic.tileentity.TileEntityDecipheringTable;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IMerchant;
import net.minecraft.entity.NpcMerchant;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UnsagaGui {

	public static enum Type implements IGuiAttribute{
		EQUIPMENT(0){

			@Override
			public Container getContainer(World world,EntityPlayer player,XYZPos pos,IInteractionState instance){
				return new ContainerEquipment(player.inventory,player);
			}

			@Override
			public GuiContainer getGui(World world,EntityPlayer player,XYZPos pos){
				return new GuiEquipment(player);
			}
		},SKILLPANEL(1){
			@Override
			public Container getContainer(World world,EntityPlayer player,XYZPos pos,IInteractionState instance){
				return new ContainerSkillPanel(player);
			}

			@Override
			public GuiContainer getGui(World world,EntityPlayer player,XYZPos pos){
				return new GuiSkillPanel(player);
			}
		},BLENDING(2){
			@Override
			public Container getContainer(World world,EntityPlayer player,XYZPos pos,IInteractionState instance){
				return new ContainerBlender(player,world);
			}

			@Override
			public GuiContainer getGui(World world,EntityPlayer player,XYZPos pos){
				return new GuiBlender(player, world);
			}
		}
		,TABLET(3){
			@Override
			public Container getContainer(World world,EntityPlayer player,XYZPos pos,IInteractionState instance){
				return Optional.ofNullable(world.getTileEntity(pos))
						.filter(in -> in instanceof TileEntityDecipheringTable)
						.map(in -> new ContainerTabletDeciphering(player,world,pos,(TileEntityDecipheringTable) in))
						.orElse(null);
			}

			@Override
			public GuiContainer getGui(World world,EntityPlayer player,XYZPos pos){
				return Optional.ofNullable(world.getTileEntity(pos))
						.map(in -> GuiTabletDeciphering.create(player,world,pos,in))
						.orElse(null);
			}
		},SMITH(4){
			@Override
			public Container getContainer(World world,EntityPlayer player,XYZPos pos,IInteractionState instance){
				return instance.getMerchant()
						.map(in -> Triplet.of((IMerchant)in,world,player))
						.map(ContainerBlacksmithUnsaga::new)
						.orElse(null);
			}

			@Override
			public GuiContainer getGui(World world,EntityPlayer player,XYZPos pos){
				return new GuiBlacksmithUnsaga(new NpcMerchant(player,ChatHandler.createChatComponent("smith")),world,player);
			}

			@Override
			public void onGuiOpen(PacketOpenGui pgo,MessageContext ctx,EntityPlayer ep ){
				Entity entity = ep.world.getEntityByID(pgo.getComp().getInteger("villager"));
				Optional.ofNullable(VillagerHelper.getCustomerCapability(ep))
				.filter(in -> entity instanceof EntityVillager)
				.ifPresent(in -> in.setMerchant((EntityVillager) entity));
			}
		},BARTERING(5){
			@Override
			public Container getContainer(World world,EntityPlayer player,XYZPos pos,IInteractionState instance){
				return instance.getMerchant()
						.map(in -> new ContainerBartering(world, player, (IMerchant)in))
						.orElse(null);
			}

			@Override
			public GuiContainer getGui(World world,EntityPlayer player,XYZPos pos){
				return new GuiBartering(new NpcMerchant(player,ChatHandler.createChatComponent("merchant")), world, player);
			}

			@Override
			public void onGuiOpen(PacketOpenGui pgo,MessageContext ctx,EntityPlayer ep ){
				Entity entity = ep.world.getEntityByID(pgo.getComp().getInteger("villager"));
				Optional.ofNullable(VillagerHelper.getCustomerCapability(ep))
				.filter(in -> in instanceof EntityVillager)
				.ifPresent(in -> in.setMerchant((EntityVillager) entity));
			}
		},CHEST(6){
			@Override
			public Container getContainer(World world,EntityPlayer player,XYZPos pos,IInteractionState instance){
				return instance.getChest()
						.filter(in -> ChestCapability.ADAPTER_ENTITY.hasCapability(in))
						.map(in -> new ContainerChestUnsaga((IChestBehavior) in,player))
						.orElse(null);
			}

			@Override
			public GuiContainer getGui(World world,EntityPlayer player,XYZPos pos){
				return UnsagaActionCapability.ADAPTER.getCapabilityOptional(player)
				.map(in -> in.getChest().orElse(null))
				.filter(in -> ChestCapability.ADAPTER_ENTITY.hasCapability(in))
				.map(in -> new GuiChest((IChestBehavior) in,player))
				.orElse(null);
			}
		},BINDER(7){

		}
		,SMITH_MINSAGA(8){
			@Override
			public Container getContainer(World world,EntityPlayer player,XYZPos pos,IInteractionState instance){
				return new ContainerToolCustomizeMinsaga(player);
			}

			@Override
			public GuiContainer getGui(World world,EntityPlayer player,XYZPos pos){
				return new GuiToolCustomizeMinsaga(world,player);
			}
		},MANUAL_ABILITY(9){
			@Override
			public Container getContainer(World world,EntityPlayer player,XYZPos pos,IInteractionState instance){
				return new ContainerTextMenu(player);
			}

			@Override
			public GuiContainer getGui(World world,EntityPlayer player,XYZPos pos){
				return new GuiTextMenu(player, null);
			}
		},MAP_FIELD(10){
			@Override
			public Container getContainer(World world,EntityPlayer player,XYZPos pos,IInteractionState instance){
				return new ContainerUnsagaMap(player);
			}

			@Override
			public GuiContainer getGui(World world,EntityPlayer player,XYZPos pos){
				return new GuiUnsagaMap(player,GuiUnsagaMap.Type.FIELD);
			}
		},MAP_DUNGEON(11){
			@Override
			public Container getContainer(World world,EntityPlayer player,XYZPos pos,IInteractionState instance){
				return new ContainerUnsagaMap(player);
			}

			@Override
			public GuiContainer getGui(World world,EntityPlayer player,XYZPos pos){
				return new GuiUnsagaMap(player,GuiUnsagaMap.Type.DUNGEON);
			}
		}
		,MENTOR(12){
			@Override
			public Container getContainer(World world,EntityPlayer player,XYZPos pos,IInteractionState instance){
				return instance.getMerchant()
						.map(in -> new ContainerMentor(world, player, (IMerchant) instance.getMerchant().get()))
						.orElse(null);
			}

			@Override
			public GuiContainer getGui(World world,EntityPlayer player,XYZPos pos){
				return new GuiMentor(new NpcMerchant(player,ChatHandler.createChatComponent("merchant")), world, player);
			}
		},CARRIER(13){
			@Override
			public Container getContainer(World world,EntityPlayer player,XYZPos pos,IInteractionState instance){
				return instance.getMerchant()
						.map(in ->new ContainerCarrier(world, player, (IMerchant) instance.getMerchant().get()))
						.orElse(null);
			}

			@Override
			public GuiContainer getGui(World world,EntityPlayer player,XYZPos pos){
				return new GuiCarrier(new NpcMerchant(player,ChatHandler.createChatComponent("merchant")),world,player);
			}

		};

		public static Type fromMeta(int meta){
			return HSLibs.fromMeta(Type.values(), meta);
		}

		private int meta;

		private Type(int meta) {
			this.meta = meta;
		}

		@Override
		public @Nullable Container getContainer(World world,EntityPlayer player,XYZPos pos){
			return UnsagaActionCapability.ADAPTER.getCapabilityOptional(player)
					.map(in -> this.getContainer(world, player, pos, in))
					.orElse(null);
		}

		public Container getContainer(World world,EntityPlayer player,XYZPos pos,IInteractionState instance){
			return null;
		}
		@Override
		public GuiContainer getGui(World world,EntityPlayer player,XYZPos pos){
			return null;
		}

		public int getMeta() {
			return meta;
		}



		@Override
		public IModBase getMod() {
			// TODO 自動生成されたメソッド・スタブ
			return UnsagaMod.instance;
		}

	}



}
