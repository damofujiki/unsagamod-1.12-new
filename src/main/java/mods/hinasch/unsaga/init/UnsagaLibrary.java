package mods.hinasch.unsaga.init;

import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.damage.LibraryItemDamageAttribute;
import mods.hinasch.unsaga.status.LibraryLivingStatus;
import mods.hinasch.unsaga.villager.bartering.LibraryItemPrice;
import mods.hinasch.unsaga.villager.smith.LibraryBlacksmith;

public class UnsagaLibrary {

	protected static UnsagaLibrary instance;
//	ElementPointLibrary elementPointLibrary;
	public static final LibraryBlacksmith CATALOG_MATERIAL = new LibraryBlacksmith();
	public static final LibraryItemPrice CATALOG_PRICE = LibraryItemPrice.instance();
	public static final LibraryLivingStatus MOB_STATUS = new LibraryLivingStatus();
	public static final LibraryItemDamageAttribute ITEM_ATTRIBUTE = new LibraryItemDamageAttribute();
//	ValidPayments validPayments;
//	ForgingLibrary forgingLibrary;
//	MerchandisePriceLibrary merchandisePriseLibrary;

//	ElementAssociationLibrary elementsNew;

//	public MerchandisePriceLibrary getMerchandisePriseLibrary() {
//		return merchandisePriseLibrary;
//	}
	public static void initCatalogues(){
		UnsagaMod.logger.get().info("Initialising Libraies...");
//		elementPointLibrary = new ElementPointLibrary();
//		this.elementsNew = ElementAssociationLibrary.instance();
//		this.elementsNew.register();
		CATALOG_MATERIAL.register();
		CATALOG_PRICE.register();
		MOB_STATUS.register();
		ITEM_ATTRIBUTE.register();
//		validPayments = new ValidPayments();
//		forgingLibrary = new ForgingLibrary();
//		merchandisePriseLibrary = new MerchandisePriceLibrary();
	}
//	public ForgingLibrary getForgingLibrary() {
//		return forgingLibrary;
//	}

//	public ElementAssociationLibrary elementLib(){
//		return this.elementsNew;
//	}

//	public ElementPointLibrary getElementPointLib() {
//		return elementPointLibrary;
//	}

//	public ValidPayments getValidPayments(){
//		return validPayments;
//	}
}
