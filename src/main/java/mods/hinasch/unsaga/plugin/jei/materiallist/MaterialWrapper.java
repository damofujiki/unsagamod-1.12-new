package mods.hinasch.unsaga.plugin.jei.materiallist;

import java.util.List;
import java.util.Optional;

import com.google.common.collect.Lists;

import mods.hinasch.unsaga.init.UnsagaLibrary;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import net.minecraft.item.ItemStack;

public class MaterialWrapper<T> implements Comparable<MaterialWrapper>{


	final public UnsagaMaterial material;
	//	final public MinsagaForging.Material materialMinsaga;
	public List<ItemStack> itemStacks;

	public MaterialWrapper(UnsagaMaterial mate){
		this.material = mate;

		this.itemStacks = Lists.newArrayList();


		if(!material.itemStack().isEmpty()){
			this.itemStacks.add(material.itemStack());
		}

		Optional.of(UnsagaLibrary.CATALOG_MATERIAL.findItemStacksByMaterial(material))
		.filter(in -> !in.isEmpty())
		.map(in -> this.itemStacks.addAll(in));

	}

	public UnsagaMaterial getMaterial(){
		return this.material;

	}


	public List<ItemStack> getStacks(){
		return this.itemStacks;
	}

	@Override
	public int compareTo(MaterialWrapper o) {
		return Integer.compare(this.material.rank(), o.getMaterial().rank());
	}
}
