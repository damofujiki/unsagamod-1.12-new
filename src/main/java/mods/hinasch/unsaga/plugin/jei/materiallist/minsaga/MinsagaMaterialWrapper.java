package mods.hinasch.unsaga.plugin.jei.materiallist.minsaga;

import java.util.List;

import com.google.common.collect.ImmutableList;

import mods.hinasch.unsaga.minsaga.MinsagaMaterial;
import mods.hinasch.unsaga.minsaga.MinsagaMaterialInitializer;
import mods.hinasch.unsaga.villager.smith.LibraryBlacksmith.IGetItemStack;
import net.minecraft.item.ItemStack;

public class MinsagaMaterialWrapper implements Comparable<MinsagaMaterialWrapper>{

	final public MinsagaMaterial material;
	final public List<ItemStack> itemStack;

	public MinsagaMaterialWrapper(MinsagaMaterial mate){
		this.material = mate;
		this.itemStack = MinsagaMaterialInitializer.fromMaterial(mate)
				.filter(in ->in instanceof IGetItemStack)
				.map(in ->(IGetItemStack)in)
				.map(in ->in.getItemStack())
				.orElse(ImmutableList.of());



	}


	public MinsagaMaterial getMaterial(){
		return this.material;

	}


	public List<ItemStack> getStacks(){
		return this.itemStack;
	}

	@Override
	public int compareTo(MinsagaMaterialWrapper o) {
		return this.material.compareTo(o.getMaterial());
	}
}
