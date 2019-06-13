package mods.hinasch.unsaga.material;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.google.common.collect.ImmutableMap;

import io.netty.util.internal.StringUtil;
import mods.hinasch.lib.misc.JsonApplier.IJsonApplyTarget;
import mods.hinasch.lib.misc.JsonApplier.IJsonParser;
import mods.hinasch.lib.registry.RegistryUtil.IUnlocalizedName;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.Statics;
import mods.hinasch.unsaga.ability.AbilityPotentialTableProvider;
import mods.hinasch.unsaga.common.UnsagaWeight;
import mods.hinasch.unsaga.core.item.wearable.ItemArmorUnsaga;
import mods.hinasch.unsaga.core.item.wearable.ItemArmorUnsaga.ArmorTexture;
import mods.hinasch.unsaga.core.item.wearable.MaterialArmorTextureSetting;
import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew;
import mods.hinasch.unsaga.material.UnsagaMaterialJsonParser.JsonParserColor;
import mods.hinasch.unsaga.material.UnsagaMaterialJsonParser.JsonParserMaterial;
import mods.hinasch.unsaga.material.UnsagaMaterialJsonParser.JsonParserShieldValue;
import mods.hinasch.unsaga.material.UnsagaMaterialJsonParser.JsonParserSpecialName;
import mods.hinasch.unsaga.util.ToolCategory;
import mods.hinasch.unsaga.villager.bartering.BarteringMaterialCategory;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class UnsagaMaterial extends IForgeRegistryEntry.Impl<UnsagaMaterial> implements Comparable<UnsagaMaterial>,IUnlocalizedName{



	public static class Property{

		final UnsagaWeight weight;
		final int rank;
		final int price;
		final ToolMaterial toolMaterial;
		final ArmorMaterial armorMaterial;

		public Property(int rank,int weight,int price,ToolMaterial t,ArmorMaterial a){
			this.weight = new UnsagaWeight(weight);
			this.rank = rank;
			this.price = price;
			this.toolMaterial = t;
			this.armorMaterial = a;
		}

	}



	public class PropertyGetterImpl implements IItemPropertyGetter{

		UnsagaMaterial m;
		public PropertyGetterImpl(UnsagaMaterial m){

			this.m = m;
		}
		@Override
		public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn) {

			return UnsagaMaterialCapability.adapter.getCapabilityOptional(stack)
					.filter(in -> in.getMaterial()==this.m).isPresent() ? 1.0F : 0;
			//			if(UnsagaMaterialCapability.adapter.hasCapability(stack)){
			//				UnsagaMaterial mat = UnsagaMaterialCapability.adapter.getCapability(stack).getMaterial();
			//
			//				if(mat==this.m){
			//					//					UnsagaMod.logger.trace(this.getClass().getName(),this.m);
			//					return 1.0F;
			//				}
			//			}
			//			return 0;
		}

	}
	public static final ToolMaterial TOOLMATERIAL_DEFAULT = ToolMaterial.STONE;
	public static final ArmorMaterial ARMORMATERIAL_DEFAULT = ArmorMaterial.LEATHER;
	public static final Property PROP_DEF = new Property(0,0,0,TOOLMATERIAL_DEFAULT,ARMORMATERIAL_DEFAULT);
	static Map<UnsagaMaterial,Property> properties = new HashMap<>();

	final String unlname;
	final UnsagaWeight weight;
	final int rank;
	final int price;
	final int MaterialColor;
	final ShieldPower shieldValue;
	final ToolMaterial toolMaterial;
	final ArmorMaterial armorMaterial ;
	final ImmutableMap<ToolCategory,Float> suitableBonus ;
	final ImmutableMap<ToolCategory,String> specialName ;

	public UnsagaMaterial(Builder builder) {
		this.unlname = builder.unlname;
		this.weight = builder.weight;
		this.rank = builder.rank;
		this.price = builder.price;
		this.MaterialColor = builder.MaterialColor;
		this.shieldValue = builder.shieldValue;
		this.toolMaterial = builder.toolMaterial;
		this.armorMaterial = builder.armorMaterial;
		this.specialName = ImmutableMap.copyOf(builder.specialName);
		this.suitableBonus = ImmutableMap.copyOf(builder.suitableBonus);
	}



	//	@Override
	//	public void applyJson(UnsagaMaterialJsonParser.JsonParserMaterial parser) {
	//		ToolMaterial t = TOOLMATERIAL_DEFAULT;
	//		if(parser.harvestLevel>=0){
	//			t = EnumHelper.addToolMaterial("unsaga."+this.name, parser.harvestLevel, parser.maxUses, parser.efficiency, parser.attack, parser.enchantWeapon);
	//		}
	//		ArmorMaterial a = ARMORMATERIAL_DEFAULT;
	//		if(parser.damageFactor>=0){
	//			a = EnumHelper.addArmorMaterial("unsaga."+parser.name, "", parser.damageFactor, parser.reduction, parser.enchantArmor, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, parser.toughness);
	//		}
	//		Property prop = new Property(parser.rank,parser.weight,parser.price,t,a);
	//		//		this.rank = parser.rank;
	//		//		this.weight = parser.weight;
	//		//		this.price = parser.price;
	//
	//		//		if(parser.harvestLevel>0){
	//		//			this.toolMaterial = Optional.of(EnumHelper.addToolMaterial("unsaga."+this.name, parser.harvestLevel, parser.maxUses, parser.efficiency, parser.attack, parser.enchantWeapon));
	//		//		}
	//		//		if(parser.damageFactor>0){
	//		//			this.armorMaterial =Optional.of(EnumHelper.addArmorMaterial("unsaga."+parser.name, "", parser.damageFactor, parser.reduction, parser.enchantArmor, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, parser.toughness));
	//		//		}
	//		properties.put(this, prop);
	////		UnsagaMod.logger.trace("parsing...", this.getPropertyName(),this.rank(),this.weight(),this.price());
	//	}

	@Override
	public int compareTo(UnsagaMaterial o) {
		return Integer.compare(this.rank(), o.rank());
	}
	public Optional<String> getAnotherName(ToolCategory cate){
		return this.specialName.containsKey(cate)?Optional.of( this.specialName.get(cate)) : Optional.empty();
	}
	public ArmorMaterial getArmorMaterial() {
		if(this.armorMaterial!=ARMORMATERIAL_DEFAULT){
			return this.armorMaterial;

		}else{

			return this.getParent().map(in -> in.getDefaultMaterial().getArmorMaterial())
					.orElse(ARMORMATERIAL_DEFAULT);
			//			if(this.getParent().isPresent()){
			//				return this.getParent().get().getDefaultMaterial().getArmorMaterial();
			//			}
		}


		//		return this.ARMORMATERIAL_DEFAULT;
	}

	public float getBowModifier(){
		return this.getToolMaterial().getAttackDamage() - 1.0F;
	}
	//	public void setShieldModifier(int p){
	//		this.shieldPower = p;
	//	}

	public String getLocalized(){
		String additional = StringUtil.EMPTY_STRING;
		if(UnsagaConfigHandlerNew.GENERAL.isDisplayAltMaterial){
			if(this==UnsagaMaterials.DEBRIS2 || this==UnsagaMaterials.STEEL2){
				additional = "(2)";
			}
		}
		return HSLibs.translateKey("material."+this.getUnlocalizedName())+additional;
	}

	//	public boolean isSuitable(ToolCategory cate){
	//		return MaterialSuitabilityRegistry.INSTANCE.getSuitables(cate).contains(this);
	//	}
	//
	//	public boolean isMerchandise(){
	//		return UnsagaMaterialRegistry.getMerchandiseMaterials().contains(this);
	//	}

	public int materialColor(){
		//		if(this.color==Statics.COLOR_NONE && this.getParent().isPresent()){
		//			return this.getParent().get().getDefaultMaterial().getMaterialColor();
		//
		//		}
		return this.MaterialColor;
	}

	//	public void setSubIconGetter(String par1){
	//		subIconGetter.put(this, par1);
	//	}
	//	/**
	//	 * 親カテゴリーを設定している時に親が同じだとtrue.
	//	 * @param in
	//	 * @return
	//	 */
	//	public boolean isSameParent(UnsagaMaterial in){
	//		if(in.getParent().isPresent() && this.getParent().isPresent()){
	//			return in.getParent().get() == this.getParent().get();
	//		}
	//		return false;
	//	}
	public Optional<UnsagaMaterialCategory> getParent(){
		return Optional.ofNullable(UnsagaMaterialCategory.getCategory(this));
	}


	//	public void setMaterialColor(int col){
	//		this.color = col;

	//	public void setToolMaterial(ToolMaterial toolMaterial) {
	//		this.toolMaterial = Optional.of(toolMaterial);
	//	}

	//	public ToolMaterial setToolMaterial(int harvestLevel,int maxUses,float efficiency,float damage,int enchantability){
	//		ToolMaterial tm = UtilUnsagaMaterial.addToolMaterial(this.getPropertyName(), harvestLevel, maxUses, efficiency, damage, enchantability);
	//		return tm;
	//	}
	//
	//	public ArmorMaterial setArmorMaterial(int armor,int[] reduces,int enchant,int... toughIn){
	//		int toughness = toughIn.length>0 ? toughIn[0] : 0;
	//		ResourceLocation res = new ResourceLocation(name);
	//		ArmorMaterial am = EnumHelper.addArmorMaterial(this.getPropertyName(), "testarmortex",armor, reduces, enchant,SoundEvents.ITEM_ARMOR_EQUIP_GENERIC,toughness);
	//		return am;
	//
	//	}

	public int price(){
		return this.price;
	}

	//	private Property getProperty(){
	//		return properties.getOrDefault(this, PROP_DEF);
	//	}

	//	/**
	//	 * 親カテゴリーを設定する（アカシア・樫etc∈木
	//	 * @param c
	//	 */
	//	protected void setParent(UnsagaMaterialCategory c){
	//		this.category = Optional.of(c);
	//	}


	//	public void addCategoryUseOriginalName(ToolCategory category){
	//		this.useOriginalNameForcedList.add(category);
	//	}

	//	public boolean isUseOriginalName(ToolCategory cate){
	//		return this.useOriginalNameForcedList.contains(cate);
	//	}

	//	public void setAnotherName(ToolCategory cate,String name){
	//		this.useAnotherNameMap.put(cate, name);
	//	}

	/**
	 * アイテムアイコン用のプロパティゲッターを返す。
	 * @return
	 */
	public IItemPropertyGetter getPropertyGetter(){
		return new PropertyGetterImpl(this);
	}
	public int rank(){
		return this.rank;
	}


	/** 盾の能力値(バニラの33%+??)*/
	public int shieldValue(){
		return this.shieldValue.value;
	}
	//	public void setArmorMaterial(ArmorMaterial armorMaterial) {
	//		this.armorMaterial = Optional.of(armorMaterial);
	//	}

	public int getArmorValueForAccessory(){
		return AbilityPotentialTableProvider.TABLE_PASSIVE.getArmorModifier(this);
	}
	public ArmorTexture getSpecialArmorTexture(ToolCategory cate){
		return Optional.ofNullable(MaterialArmorTextureSetting.specialArmorTexTabel.get(this, cate))
				.orElse(ItemArmorUnsaga.TEXTURE_DEFAULT);
	}

	public boolean isSameBarteringCategory(UnsagaMaterial other){
		return this.getBarteringMaterialType().getMaterials().contains(other);
	}
	public BarteringMaterialCategory.Type getBarteringMaterialType(){
		return BarteringMaterialCategory.ASSIGNMENTS_MATERIAL.getType(this);
	}
	public float getSuitableBonus(ToolCategory cate){
		return suitableBonus.getOrDefault(cate, 0.0F);
	}

	public boolean isMerchandise(){
		return UnsagaMaterialInitializer.getMerchandiseMaterials().contains(this);
	}

	public boolean isSuitable(ToolCategory cate){
		return MaterialSuitabilityRegistry.getSuitables(cate).contains(this);
	}
	//	public Optional<String> getSubIconGetter(){
	//		if(!UnsagaMaterialRegisterer.instance().getSubIconName(this).isEmpty()){
	//			return Optional.of(UnsagaMaterialRegisterer.instance().getSubIconName(this));
	//		}
	//		return Optional.empty();
	//	}

	/**
	 * EmptyStackで返る場合があるので注意
	 * @return
	 */
	public ItemStack itemStack(){
		return UnsagaIngredients.toStack(this);
	}

	public ItemStack createStack(int amount){
		return this.itemStack().isEmpty() ? ItemStack.EMPTY : new ItemStack(this.itemStack().getItem(),amount);
	}


	public ToolMaterial getToolMaterial() {

		return Optional.of(this.toolMaterial)
				.filter(in -> in==TOOLMATERIAL_DEFAULT)
				.map(in -> this.getParent()
						.map(parent -> parent.getDefaultMaterial().getToolMaterial())
						.orElse(TOOLMATERIAL_DEFAULT))
				.orElse(TOOLMATERIAL_DEFAULT);

	}

	public UnsagaWeight weight() {
		return this.weight;
	}

	public boolean isEmpty(){
		return this.equals(UnsagaMaterials.DUMMY);
	}
	//	/** unlocalizednameで使う。素材の名はカテゴリのものを使うかどうか
	//	 * Categoryのほうで一律設定する。*/
	//	public boolean isUseParentName(ToolCategory cate) {
	//		if(this.getParent().isPresent()){
	//			return this.getParent().get().isUseParentMaterial(cate);
	//		}
	//		return false;
	//	}



	public String getUnlocalizedName(){
		return this.unlname;
	}


	public static class Builder{

		String name;
		String unlname;
		UnsagaWeight weight;
		int rank;
		int price;
		int MaterialColor = Statics.COLOR_NONE;
		ShieldPower shieldValue;
		ToolMaterial toolMaterial = TOOLMATERIAL_DEFAULT;
		ArmorMaterial armorMaterial = ARMORMATERIAL_DEFAULT;
		Map<ToolCategory,Float> suitableBonus = new HashMap<>();
		Map<ToolCategory,String> specialName = new HashMap<>();

		public Builder(String name){
			this(name, name);

		}


		public Builder(String name,String unl){
			this.name = name;
			this.unlname = unl;

		}


		public Builder addSuitableBonus(ToolCategory c,float bonus){
			this.suitableBonus.put(c, bonus);
			return this;
		}

		//		public Builder addSpecialTexture(ToolCategory c,ArmorTexture texture){
		//			this.specialTexture.put(c, texture);
		//			return this;
		//		}
		//
		//		public Builder setMaterialColor(int col){
		//			this.MaterialColor = col;
		//			return this;
		//		}
		//
		//		public Builder setShieldPower(int pow){
		//			this.shieldValue = new ShieldPower(pow);
		//			return this;
		//		}

		public void apply(UnsagaMaterialJsonParser.JsonParserShieldValue p){
			this.shieldValue = new ShieldPower(p.value);
		}
		public void apply(UnsagaMaterialJsonParser.JsonParserColor p){
			this.MaterialColor = p.color;
		}

		public void apply(UnsagaMaterialJsonParser.JsonParserSpecialName p){
			p.ids.forEach(in ->{
				if(in.equals(this.name)){
					this.specialName.put(p.category, p.specialName);
				}
			});
		}
		public void apply(UnsagaMaterialJsonParser.JsonParserMaterial parser){
			ToolMaterial t = TOOLMATERIAL_DEFAULT;
			if(parser.harvestLevel>=0){
				t = EnumHelper.addToolMaterial("unsaga."+this.name, parser.harvestLevel, parser.maxUses, parser.efficiency, parser.attack, parser.enchantWeapon);
			}
			ArmorMaterial a = ARMORMATERIAL_DEFAULT;
			if(parser.damageFactor>=0){
				a = EnumHelper.addArmorMaterial("unsaga."+parser.name, "", parser.damageFactor, parser.reduction, parser.enchantArmor, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, parser.toughness);
			}
			this.rank = parser.rank;
			this.weight = new UnsagaWeight(parser.weight);
			this.price = parser.price;
			this.toolMaterial = t;
			this.armorMaterial = a;
			this.name = parser.name;
		}
	}

	public static class Factory implements IJsonApplyTarget<IJsonParser>{

		Map<String,Builder> map = new HashMap<>();

		public Map<String,Builder> get(){
			return map;
		}

		public void apply(JsonParserMaterial parser) {
			map.get(parser.name).apply(parser);

		}

		public void apply(JsonParserSpecialName parser) {
			parser.ids.forEach(in ->{
				map.get(in).apply(parser);
			});

		}

		public void apply(JsonParserColor parser) {
			map.get(parser.id).apply(parser);

		}

		public void apply(JsonParserShieldValue parser) {
			map.get(parser.id).apply(parser);

		}

		public void register(Builder builder){
			map.put(builder.name, builder);
		}
		@Override
		public void applyJson(IJsonParser parser) {
			// TODO 自動生成されたメソッド・スタブ
			if(parser instanceof JsonParserMaterial){
				this.apply((JsonParserMaterial) parser);
			}
			if(parser instanceof JsonParserSpecialName){
				this.apply((JsonParserSpecialName) parser);
			}
			if(parser instanceof JsonParserShieldValue){
				this.apply((JsonParserShieldValue) parser);
			}
			if(parser instanceof JsonParserColor){
				this.apply((JsonParserColor) parser);
			}
		}

	}

	@Override
	public void setUnlocalizedName(String unl) {
		// TODO 自動生成されたメソッド・スタブ

	}
}
