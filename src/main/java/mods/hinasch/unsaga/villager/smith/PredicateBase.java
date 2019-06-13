package mods.hinasch.unsaga.villager.smith;

import java.util.function.Predicate;

import mods.hinasch.unsaga.villager.smith.LibraryBlacksmith.IGetItemStack;
import net.minecraft.item.ItemStack;

public abstract class PredicateBase<T> implements Predicate<ItemStack>,IGetItemStack{

	public final T object;

	public PredicateBase(T obj){
		this.object = obj;
	}



}