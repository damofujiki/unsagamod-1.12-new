package mods.hinasch.unsaga.core.client;

import org.lwjgl.input.Keyboard;

import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.core.event.KeyBindingHandler;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.lib.util.ChatHandler;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.SoundAndSFX;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.lib.world.XYZPos;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.AbilityCapability;
import mods.hinasch.unsaga.ability.slot.AbilitySlotList;
import mods.hinasch.unsaga.init.UnsagaGui;
import mods.hinasch.unsagamagic.item.ItemSpellBook;
import mods.hinasch.unsagamagic.spell.spellbook.SpellBookCapability;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundCategory;

public class KeyBindingUnsaga {
	public static final KeyBinding OPENGUI  = new KeyBinding("Open Gui", Keyboard.KEY_COMMA, UnsagaMod.MODID);
	public static final KeyBinding TARGETTING  = new KeyBinding("Targetting", Keyboard.KEY_H, UnsagaMod.MODID);
	public static final KeyBinding CHANGE_SPELL  = new KeyBinding("Change Spell", Keyboard.KEY_G, UnsagaMod.MODID);

	private final ClientTargetSelector targetter = new ClientTargetSelector();

	public KeyBindingUnsaga(){
		KeyBindingHandler.addKeyBindings(OPENGUI,TARGETTING,CHANGE_SPELL);
		KeyBindingHandler.addEvent(e -> {
			boolean press_opengui = KeyBindingUnsaga.OPENGUI.isPressed();
			boolean press_targetting = KeyBindingUnsaga.TARGETTING.isPressed();
			boolean press_change_spell = KeyBindingUnsaga.CHANGE_SPELL.isPressed();
			if(press_targetting){
				this.targetter.next();
			}
			if(press_change_spell){
				this.changeSpell();
			}
			if(press_opengui){
//				UnsagaMod.logger.trace(this.getClass().getName(),"pressed");
//				if(ClientHelper.getMouseOver()!=null && ClientHelper.getMouseOver().entityHit!=null){
//					if(ClientHelper.getMouseOver().entityHit instanceof EntityVillager){
//						EntityVillager villager = (EntityVillager) ClientHelper.getMouseOver().entityHit;
//						NBTTagCompound args = UtilNBT.createCompound();
//						args.setInteger("villager", villager.getEntityId());
//						if(Statics.checkProfession(villager.getProfessionForge(), Statics.VILLAGER_BLACKSMITH)){
//							UnsagaMod.packetDispatcher.sendToServer(PacketOpenGui.create(UnsagaGui.Type.SMITH,XYZPos.createFrom(ClientHelper.getPlayer()),args));
//							return;
//						}else{
//							UnsagaMod.packetDispatcher.sendToServer(PacketOpenGui.create(UnsagaGui.Type.BARTERING,XYZPos.createFrom(ClientHelper.getPlayer()),args));
//							return;
//						}
//
//					}
//				}
				HSLibs.openGuiFromClient(UnsagaGui.Type.EQUIPMENT, XYZPos.createFrom(ClientHelper.getPlayer()));

			}
		});
	}

	public void changeSpell(){
		ItemStack held = ClientHelper.getPlayer().getHeldItemMainhand();
		if(held.isEmpty()){
			return;
		}

		AbilityCapability.adapter.getCapabilityOptional(held)
		.ifPresent(in ->{
			AbilitySlotList abilityList = in.getAbilitySlots();
			if(abilityList.isSelectable()){
				SoundAndSFX.playPlayerSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, 1.0F, false);
				abilityList.nextTechSlot(held);
				ChatHandler.sendChatToPlayer(ClientHelper.getPlayer(), "Changed To:"+abilityList.getCurrentTechSlot(held).ability().getLocalized());
				HSLib.packetDispatcher().sendToServer(PacketSyncCapability.create(AbilityCapability.CAPA, in));
			}

		});

		if(held.getItem() instanceof ItemSpellBook){
			SoundAndSFX.playPlayerSound(SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, SoundCategory.PLAYERS, 1.0F, 1.0F, false);
			SpellBookCapability.ADAPTER.getCapabilityOptional(held)
			.ifPresent(capa ->{
				SpellBookCapability.changeSpell(held);
				String spellName = capa.getCurrentSpell()
						.map(in -> in.rawSpell().getLocalized())
						.orElse("---");
				ChatHandler.sendChatToPlayer(ClientHelper.getPlayer(), "Changed To:"+spellName);
				HSLib.packetDispatcher().sendToServer(PacketSyncCapability.create(SpellBookCapability.CAPA, capa
						, UtilNBT.comp().setInteger("order", 1).get()));
			});


		}
	}
	public ClientTargetSelector getClientTargetSelector(){
		return this.targetter;
	}

}
