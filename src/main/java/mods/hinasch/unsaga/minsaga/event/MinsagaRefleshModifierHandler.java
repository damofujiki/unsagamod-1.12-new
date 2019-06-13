package mods.hinasch.unsaga.minsaga.event;

import java.util.List;
import java.util.UUID;

import mods.hinasch.lib.entity.ModifierHelper;
import mods.hinasch.lib.util.Statics;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.minsaga.ArmorModifier;
import mods.hinasch.unsaga.minsaga.MinsagaForgingCapability;
import mods.hinasch.unsaga.minsaga.MinsagaUtil;
import mods.hinasch.unsaga.status.UnsagaStatus;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class MinsagaRefleshModifierHandler {

	public static final UUID MINSAGA = UUID.fromString("46b2abe7-06fd-462c-871e-8a2cde17c2d1");
	public static final UUID MINSAGA2 = UUID.fromString("a4b3fffd-4e10-4c3a-bc39-0cbf8b7a49b3");
	public static final UUID MINSAGA3 = UUID.fromString("1b43a63e-710f-4986-aad3-19ff856f8c12");
	public static final UUID MINSAGA4 = UUID.fromString("b1d1240c-d90e-422c-9a04-2480bf71bd60");
	boolean isSuspend = true;
	@SubscribeEvent
	public void onEquipChanged(LivingEquipmentChangeEvent ev){
		//		if(isSuspend){ //ちょっとおかしくなるので切り離し中
		//			return;
		//		}
		EntityLivingBase el = ev.getEntityLiving();
		if(!el.getHeldItemMainhand().isEmpty()){
			ItemStack held = el.getHeldItemMainhand();
			if(MinsagaForgingCapability.ADAPTER.hasCapability(held)){
				double attack = MinsagaForgingCapability.ADAPTER.getCapability(held).attackModifier();
				AttributeModifier attackModfier = new AttributeModifier(MINSAGA, "minsaga.forging", attack, Statics.OPERATION_INCREMENT);
				UnsagaMod.logger.trace(this.getClass().getName(), attack);
				ModifierHelper.refleshModifier(el, SharedMonsterAttributes.ATTACK_DAMAGE, attackModfier);
			}
		}

		List<ItemStack> list = MinsagaUtil.getForgedArmors(ev.getEntityLiving());

		ArmorModifier armor = MinsagaUtil.getArmorModifierAmount(ev.getEntityLiving());
		AttributeModifier mod_melee = new AttributeModifier(MINSAGA, "minsaga.forging", armor.melee(), Statics.OPERATION_INCREMENT);
		AttributeModifier mod_magic = new AttributeModifier(MINSAGA, "minsaga.forging", armor.magic(), Statics.OPERATION_INCREMENT);
		ModifierHelper.refleshModifier(el, SharedMonsterAttributes.ARMOR, mod_melee);
		ModifierHelper.refleshModifier(el,UnsagaStatus.MENTAL, mod_magic);


		//		double amountMelee = MinsagaUtil.getForgedArmors(el).stream().mapToDouble(in -> MinsagaForgeCapability.ADAPTER.getCapability(in).getArmorModifier().melee()).sum();
		//		double amountMagic  = MinsagaUtil.getForgedArmors(el).stream().mapToDouble(in -> MinsagaForgeCapability.ADAPTER.getCapability(in).getArmorModifier().magic()).sum();
		//		AttributeModifier meleeArmorModifier = new AttributeModifier(MINSAGA, "minsaga.forging", amountMelee, Statics.OPERATION_INCREMENT);
		//		ModifierHelper.refleshModifier(el, SharedMonsterAttributes.ARMOR, meleeArmorModifier);
		//		AttributeModifier magicArmorModifier = new AttributeModifier(MINSAGA, "minsaga.forging", amountMagic, Statics.OPERATION_INCREMENT);
		//		ModifierHelper.refleshModifier(el, UnsagaStatus.MENTAL, magicArmorModifier);
		//
		//		List<MinsagaForging.Ability> abilities = MinsagaUtil.getAbilities(el);
		//		ObjectCounter<MinsagaForging.Ability> counter = new ObjectCounter();
		//		abilities.stream().forEach(in ->{
		//			counter.add(in);
		//		});
		//		if(el instanceof EntityPlayer){
		//			double amountLuck = counter.get(MinsagaForging.Ability.LOOT) * 1.0D;
		//			AttributeModifier luckModifier = new AttributeModifier(MINSAGA2, "minsaga.forging", amountLuck, Statics.OPERATION_INCREMENT);
		//			ModifierHelper.refleshModifier(el, SharedMonsterAttributes.LUCK, luckModifier);
		//		}
		//
		//
		////		double amountAbyss = counter.get(MinsagaForging.Ability.ABYSS) * 1.0D;
		////		AttributeModifier abyssModifier = new AttributeModifier(MINSAGA3, "minsaga.forging", amountAbyss, Statics.OPERATION_INCREMENT);
		////		ModifierHelper.refleshModifier(el, AdditionalStatus.ENTITY_ELEMENTS.get(FiveElements.Type.FORBIDDEN), abyssModifier);
		//
		//		double amountQuickSilver = counter.get(MinsagaForging.Ability.QUICKSILVER) * 0.5D;
		//		AttributeModifier quickSilverModifier = new AttributeModifier(MINSAGA4, "minsaga.forging", amountQuickSilver, Statics.OPERATION_INCREMENT);
		//		ModifierHelper.refleshModifier(el, UnsagaStatus.INTELLIGENCE, quickSilverModifier);
	}
}
