package mods.hinasch.unsaga.ability;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Joiner;
import com.google.common.collect.Sets;

import net.minecraft.util.NonNullList;

/**
 *
 * {@code NonNullList<Set<IAbility>>}のラップクラス。
 *
 */
@Deprecated
public class LearnableAbilityTable{


	NonNullList<Set<IAbility>> table;

	public static final LearnableAbilityTable EMPTY = new LearnableAbilityTable();

	public LearnableAbilityTable(){
		this.table = NonNullList.create();
	}
	public LearnableAbilityTable(int size){
		this.table = NonNullList.withSize(size, Sets.newHashSet());
	}
	public LearnableAbilityTable(NonNullList<IAbility> list){
		this.table = NonNullList.withSize(list.size(), Sets.newHashSet());
		for(int i=0;i<list.size();i++){
			if(!list.get(i).isAbilityEmpty()){
				this.table.set(i, Sets.newHashSet(list.get(i)));
			}

		}
	}


	public Set<IAbility> get(int index){
		return this.table.get(index);
	}

	public boolean isEmpty(){
		if(this==EMPTY){
			return true;
		}
		if(this.table.stream().allMatch(in -> in.isEmpty())){
			return true;
		}
		return false;
	}

	public void set(int index,Collection<IAbility> abilities){
		this.table.set(index, Sets.newHashSet(abilities));
	}
	public void set(int index,IAbility ability){
		this.table.set(index, Sets.newHashSet(ability));
	}
	public int size(){
		return this.table.size();
	}
	public Stream<Set<IAbility>> stream(){
		return this.table.stream();
	}

	/** indexの位置に覚えられる技は存在するか*/
	public boolean hasLearnableAbilityAt(int index){
		if(index<this.table.size()){
			return !this.get(index).isEmpty();
		}
		return false;
	}

	public String getTooltipString(){
		return stream().map(in -> in.stream().map(i -> i.getLocalized()).collect(Collectors.joining(",")))
				.map(in ->"["+in+"]").collect(Collectors.joining());
	}
//
//	public OptionalInt getRandomLearnableIndex(Random rand){
//		List<Integer> list = Lists.newArrayList();
//		for(int i=0;i<this.table.size();i++){
//			if(!this.table.get(i).isEmpty()){
//				UnsagaMod.logger.trace(this.getClass().getName(),this.table.get(i));
//				list.add(i);
//			}
//		}
//		if(!list.isEmpty()){
//			int rt = HSLibs.randomPick(rand, list);
//			UnsagaMod.logger.trace(this.getClass().getName(),this.table.get(rt));
//			return OptionalInt.of(rt);
//		}
//		return OptionalInt.empty();
//	}

	public String toString(){
		StringBuilder builder = new StringBuilder();
		for(int i=0;i<table.size();i++){
			builder.append("/");
			builder.append(Joiner.on(",").join(table.get(i)));
		}
		return builder.toString();
	}

}
