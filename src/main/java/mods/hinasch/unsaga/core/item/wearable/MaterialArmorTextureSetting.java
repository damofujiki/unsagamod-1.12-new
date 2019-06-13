package mods.hinasch.unsaga.core.item.wearable;

import java.util.Collection;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

import mods.hinasch.unsaga.core.item.wearable.ItemArmorUnsaga.ArmorTexture;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.material.UnsagaMaterialCategory;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import mods.hinasch.unsaga.util.ToolCategory;

public class MaterialArmorTextureSetting {


	public static enum RenderSize{
		NORMAL,THIN;
	}


	public static final ArmorTexture THIN_ARMOR = new ArmorTexture("armor","armor2").setRenderSize(RenderSize.THIN);
	public static final ArmorTexture FEATHER = new ArmorTexture("feather","armor2").setRenderSize(RenderSize.THIN);
	public static final ArmorTexture FUR = new ArmorTexture("fur","armor2");
	public static final ArmorTexture WOOD = new ArmorTexture("wood","armor2");
	public static final ArmorTexture BOOTS = new ArmorTexture("fur","armor2").setRenderSize(RenderSize.THIN);
	public static final ArmorTexture CIRCLET = new ArmorTexture("circlet","armor2").setRenderSize(RenderSize.THIN);
	public static final ArmorTexture HEADBAND = new ArmorTexture("headband","armor2").setRenderSize(RenderSize.THIN);
	public static final ArmorTexture MASK = new ArmorTexture("mask","armor2");
	public static final ArmorTexture TRANSPARENT = new ArmorTexture("nothing","nothing");


	public static Table<UnsagaMaterial, ToolCategory, ArmorTexture> specialArmorTexTabel = HashBasedTable.create();
	public static void register(){
		add(UnsagaMaterials.SILK,ToolCategory.HELMET,CIRCLET);
		add(UnsagaMaterials.VELVET,ToolCategory.HELMET,CIRCLET);
		add(UnsagaMaterials.COTTON,ToolCategory.HELMET,CIRCLET);
		add(UnsagaMaterials.LIVE_SILK,ToolCategory.HELMET,CIRCLET);
		add(UnsagaMaterials.SILVER,ToolCategory.HELMET,CIRCLET);
		add(UnsagaMaterials.DIAMOND,ToolCategory.HELMET,CIRCLET);
		add(UnsagaMaterials.SAPPHIRE,ToolCategory.HELMET,CIRCLET);
		add(UnsagaMaterials.RUBY,ToolCategory.HELMET,CIRCLET);
		add(UnsagaMaterials.OBSIDIAN,ToolCategory.HELMET,MASK);
		add(UnsagaMaterials.SILK,ToolCategory.LEGGINS,TRANSPARENT);
		add(UnsagaMaterials.VELVET,ToolCategory.LEGGINS,TRANSPARENT);
		add(UnsagaMaterials.COTTON,ToolCategory.LEGGINS,TRANSPARENT);
		add(UnsagaMaterials.LIVE_SILK,ToolCategory.LEGGINS,TRANSPARENT);
		add(UnsagaMaterials.HYDRA_LEATHER,ToolCategory.HELMET,CIRCLET);
		add(UnsagaMaterials.CROCODILE_LEATHER,ToolCategory.HELMET,HEADBAND);
		add(UnsagaMaterials.SNAKE_LEATHER,ToolCategory.HELMET,HEADBAND);
		add(UnsagaMaterials.FUR,ToolCategory.HELMET, FUR);
		add(UnsagaMaterials.FEATHER,ToolCategory.HELMET, FEATHER);
		add(UnsagaMaterials.CROCODILE_LEATHER,ToolCategory.BOOTS,BOOTS);
		add(UnsagaMaterials.SNAKE_LEATHER,ToolCategory.BOOTS,BOOTS);
		add(UnsagaMaterials.FUR,ToolCategory.BOOTS,BOOTS);
		add(UnsagaMaterials.HYDRA_LEATHER,ToolCategory.BOOTS,BOOTS);
		addToCategory(UnsagaMaterialCategory.CLOTHES.getChildMaterials(),ToolCategory.ARMOR,THIN_ARMOR);
		addToCategory(UnsagaMaterialCategory.WOODS.getChildMaterials(),ToolCategory.ARMOR,WOOD);
		addToCategory(UnsagaMaterialCategory.WOODS.getChildMaterials(),ToolCategory.ARMOR,WOOD);
		addToCategory(UnsagaMaterialCategory.WOODS.getChildMaterials(),ToolCategory.HELMET,WOOD);
		addToCategory(UnsagaMaterialCategory.WOODS.getChildMaterials(),ToolCategory.BOOTS,WOOD);
	}

	public static void add(UnsagaMaterial m,ToolCategory cate,ArmorTexture tex){
		specialArmorTexTabel.put(m, cate, tex);
	}
	public static void addToCategory(Collection<UnsagaMaterial> cate,ToolCategory tool,ArmorTexture tex){
		for(UnsagaMaterial m:cate){
			specialArmorTexTabel.put(m, tool, tex);
		}
	}
}
