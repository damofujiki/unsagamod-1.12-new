package mods.hinasch.unsaga.core.item.wearable;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.realmsclient.util.Pair;

import mods.hinasch.lib.iface.ICustomModel;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.ability.IAbilitySelector;
import mods.hinasch.unsaga.ability.slot.AbilitySlotList;
import mods.hinasch.unsaga.ability.slot.AbilitySlotType;
import mods.hinasch.unsaga.common.item.ComponentUnsagaWeapon;
import mods.hinasch.unsaga.core.client.model.ModelArmorColored;
import mods.hinasch.unsaga.core.item.wearable.MaterialArmorTextureSetting.RenderSize;
import mods.hinasch.unsaga.material.IUnsagaCategoryTool;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.material.UnsagaMaterialCapability;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import mods.hinasch.unsaga.util.ToolCategory;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemArmorUnsaga extends ItemArmor implements IItemColor,IAbilitySelector,IUnsagaCategoryTool,ICustomModel{

	private static final int[] MAX_DAMAGE_ARRAY = new int[] {13, 15, 16, 11};
	protected static final String PATH = UnsagaMod.MODID+":textures/models/armor/";
	public static ArmorTexture TEXTURE_DEFAULT = new ArmorTexture("armor", "armor2");
	public static final ArmorMaterial FAILED = EnumHelper.addArmorMaterial("failed", "", 2, new int[]{1,1,1,1}, 1, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0);
	private static final UUID[] ARMOR_MODIFIERS = new UUID[] {UUID.fromString("845DB27C-C624-495F-8C9F-6020A9A58B6B"), UUID.fromString("D8499B04-0E66-4726-AB29-64469D734E0D"), UUID.fromString("9F3D476D-C118-4544-8365-64846904B48E"), UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150")};
	final ComponentUnsagaWeapon component;
	final ToolCategory category;
	//	String categoryName;
	//	boolean isFailed = false;
	final public ModelArmorColored modelArmor = new ModelArmorColored(1.0F);
	final public ModelArmorColored modelArmor2 = new ModelArmorColored(0.65F);

	public ItemArmorUnsaga(ToolCategory category) {
		super(ArmorMaterial.IRON, 0, category.getEquipmentSlot());
		this.component = new ComponentUnsagaWeapon(category);
		this.component.registerPropertyOverrides(this);
		this.category = category;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public net.minecraft.client.model.ModelBiped getArmorModel(EntityLivingBase entityLiving, ItemStack itemStack, EntityEquipmentSlot armorSlot, net.minecraft.client.model.ModelBiped _default)
	{

		if(!UnsagaMaterialCapability.adapter.hasCapability(itemStack)){
			return _default;
		}
		ArmorTexture textures = UnsagaMaterialCapability.adapter.getCapability(itemStack).getMaterial().getSpecialArmorTexture(getCategory());
//		if(textures.getRenderSize()==RenderSize.THIN){
//			model = modelArmor2;
//		}
		ModelArmorColored model = textures.getRenderSize()==RenderSize.THIN ? new ModelArmorColored(0.65F) : new ModelArmorColored(1.0F);
		model.setStack(itemStack);

		//		modelArmor.setParent(this.prepareArmorModel(_default,entityLiving, itemStack, armorSlot));
		//		modelArmor2.setStack(itemStack);
		//		modelArmor2.setParent(_default);

		return this.prepareArmorModel(model, entityLiving, itemStack, armorSlot);
	}
	//	@Override
	//	public net.minecraftforge.common.capabilities.ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound nbt)
	//	{
	//		return this.component.initCapabilities(stack, nbt);
	//	}

	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn)
	{
		this.component.addInformations(stack, tooltip, flagIn);
	}

	@Override
	public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot equipmentSlot, ItemStack stack)
	{
		Multimap<String, AttributeModifier> multimap = HashMultimap.create();

		double armorPoint = this.component.isFailed(stack) ? FAILED.getDamageReductionAmount(equipmentSlot) : this.getDamageReductionAmount(stack);
		double toughness = this.component.isFailed(stack) ? FAILED.getToughness() : this.getToughness(stack);
		if (equipmentSlot == this.armorType)
		{
			multimap.put(SharedMonsterAttributes.ARMOR.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor modifier", (double)armorPoint, 0));
			multimap.put(SharedMonsterAttributes.ARMOR_TOUGHNESS.getName(), new AttributeModifier(ARMOR_MODIFIERS[equipmentSlot.getIndex()], "Armor toughness", (double)toughness, 0));
		}

		return multimap;
	}

	public int getDamageReductionAmount(ItemStack is){
		return this.component.getDamageReductionAmount(is, armorType);
	}

	@Override
	public int colorMultiplier(ItemStack stack, int tintIndex) {
		return this.component.getColorFromItemstack(stack, tintIndex);
	}

	@Override
	public boolean getIsRepairable(ItemStack toRepair, ItemStack repair)
	{
		return false;
	}

	@Override
	public int getItemEnchantability(ItemStack is)
	{
		return this.component.getItemEnchantability(is);
	}

	@Override
	public int getMaxDamage(ItemStack stack)
	{

		return (int)this.component.getMaxDamage(stack);
	}
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if(!this.isInCreativeTab(tab)){
			return;
		}
		this.component.getSubItems(this, tab, items);


	}

	@Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, EntityPlayer playerIn, EnumHand handIn)
    {
        ItemStack held = playerIn.getHeldItem(handIn);
        EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(held);
        if(playerIn.isSneaking()){
        	for(EntityEquipmentSlot slot:EntityEquipmentSlot.values()){
        		if(playerIn.getItemStackFromSlot(slot).isEmpty()){
        			entityequipmentslot = slot;
        		}
        	}
        }
        ItemStack stackInSlot = playerIn.getItemStackFromSlot(entityequipmentslot);

        if (stackInSlot.isEmpty())
        {
            playerIn.setItemStackToSlot(entityequipmentslot, held.copy());
            held.setCount(0);
            return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, held);
        }
        else
        {
            return new ActionResult<ItemStack>(EnumActionResult.FAIL, held);
        }
    }
	@Override
	public boolean isValidArmor(ItemStack stack, EntityEquipmentSlot armorType, Entity entity)
	{
		//TODO:ここコンフィグで全身鎧OKにするとどこでも装備できるように
		return net.minecraft.entity.EntityLiving.getSlotForItemStack(stack) == armorType;
	}

	public float getToughness(ItemStack is){
		return this.component.getToughness(is);
	}

	//	@Override
	//	public ActionResult<ItemStack> onItemRightClick(ItemStack armorStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand)
	//	{
	//		InventoryHandler inv = new InventoryHandler(playerIn.inventory);
	//
	//		EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(armorStackIn);
	//		if(playerIn.isSneaking()){
	//			if(inv.getFirstEmptyArmorSlot().isPresent()){
	//				entityequipmentslot = inv.getFirstEmptyArmorSlot().get().getEquipmentSlot();
	//			}
	//		}
	//		ItemStack itemstack = playerIn.getItemStackFromSlot(entityequipmentslot);
	//
	//		if (itemstack != null)
	//		{
	//			playerIn.setItemStackToSlot(entityequipmentslot, armorStackIn.copy());
	//			ItemUtil.setStackSize(itemstack,0);
	//			return new ActionResult(EnumActionResult.SUCCESS, armorStackIn);
	//		}
	//		else
	//		{
	//			return new ActionResult(EnumActionResult.FAIL, armorStackIn);
	//		}
	//	}

	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack)
	{
		return this.component.getUnlocalizedName(par1ItemStack);
	}

	private ArmorTexture getTexturePair(UnsagaMaterial mate,ToolCategory unsagatype){
		return mate.getSpecialArmorTexture(unsagatype);
	}
	@Override
	public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type)
	{
		String suffix = ".png";//type!= null ? "_overlay.png" : ".png";
		ItemArmorUnsaga armorunsaga = (ItemArmorUnsaga)stack.getItem();
		final UnsagaMaterial mate = UnsagaMaterialCapability.adapter.hasCapability(stack) ? UnsagaMaterialCapability.adapter.getCapability(stack).getMaterial() : UnsagaMaterials.DUMMY;
		final ArmorTexture pairTexture = this.getTexturePair(mate, this.getCategory());

		return PATH + pairTexture.getTextureFromCategory(this.getCategory()) + suffix;
	}
	@Override
	public int getMaxAbilitySize() {
		// TODO 自動生成されたメソッド・スタブ
		return 4;
	}

	@Override
	public ToolCategory getCategory() {
		// TODO 自動生成されたメソッド・スタブ
		return this.category;
	}

	public static class ArmorTexture extends Pair<String,String>{



		RenderSize size = RenderSize.NORMAL;
		protected ArmorTexture(String first, String second) {
			super(first, second);
			// TODO 自動生成されたコンストラクター・スタブ
		}

		public String getTextureFromCategory(ToolCategory cate){
			if(cate==ToolCategory.HELMET || cate==ToolCategory.BOOTS || cate==ToolCategory.ARMOR)
			{
				return this.first();
			}
			if(cate==ToolCategory.LEGGINS)
			{
				return this.second();
			}
			UnsagaMod.logger.trace("Unknown ArmorType???");
			return this.first();
		}


		public String upper(){
			return this.first();
		}


		public String lower(){
			return this.second();
		}

		public RenderSize getRenderSize(){
			return this.size;
		}

		public ArmorTexture setRenderSize(RenderSize size){
			this.size = size;
			return this;
		}
	}

	@Override
	public ComponentUnsagaWeapon getComponent() {
		// TODO 自動生成されたメソッド・スタブ
		return this.component;
	}


	@Override
	public AbilitySlotList createAbilityList() {
		// TODO 自動生成されたメソッド・スタブ
		return AbilitySlotList.builder().replaceable(4).slot(4, AbilitySlotType.PASSIVE).build();
	}
}
