package mods.hinasch.unsaga.common.panel_bonus;

import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collector.Characteristics;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import net.minecraft.inventory.IInventory;

public class PanelJointList<T extends IPanelJoint>{

	public static class Builder<T extends IPanelJoint>{

		List<T> joints = Lists.newArrayList();
		public void addAll(PanelJointList<T> joint){
			for(T elm:joint.joints){
				this.addJoint(elm);
			}
		}
		/** ジョイントを追加（同等のジョイントがあれば追加しない）*/
		public void addJoint(T joint){
			if(!this.hasSameJoint(joint)){
				joints.add(joint);
			}
		}

		public PanelJointList<T> build(){
			return new PanelJointList(joints);
		}

		/** 同じジョイントを持つかどうか*/
		private boolean hasSameJoint(T other){
			//			List<IPanelJoint> rt = Lists.newArrayList(target);
			for(T joint:this.joints){
				if(other.isSame(joint)){
					return true;
				}
			}
			return false;
		}
	}

	public static final Collector<IPanelJoint,?,Builder<IPanelJoint>> COLLECTOR;
	static{
		Supplier<Builder<IPanelJoint>>           supplier    = ()       -> new Builder<>();
		BiConsumer<Builder<IPanelJoint>, IPanelJoint> accumulator = (l, t)   -> l.addJoint(t);
		BinaryOperator<Builder<IPanelJoint>>     combiner    = (l1, l2) -> {
			l1.addAll(l2.build());
			return l1;
		};
		COLLECTOR = Collector.of(supplier, accumulator, combiner, Characteristics.IDENTITY_FINISH);
	}
	final ImmutableList<T> joints;

	public PanelJointList(List<T> list){
		this.joints = ImmutableList.copyOf(list);
	}

	/** ボーナスレイアウトをリストで得る。*/
	public ImmutableList<T> asList(){
		return this.joints;
	}

	public PanelJointList<T> cleanCollisions(){
		PanelJointList<T> rt = new PanelJointList(this.joints);
		for(T joint:this.joints){
			rt = this.removeCollisionJoint(joint);
		}
		return rt;
	}

	public String dump(IInventory inv){
		return Joiner.on("/").join(joints.stream().map(in -> in.getClass().getSimpleName()+"@"+in.dump(inv)).collect(Collectors.toList()));
	}


	private PanelJointList<T> removeCollisionJoint(T base){
		List<T> copyList = Lists.newArrayList(this.joints);
		List<T> removeList = Lists.newArrayList();
		for(T joint:this.joints){
			if(joint.containsAny(base) && !joint.isSame(base)){
				if(base.compareTo(joint)>0){
					removeList.add(joint);
				}

			}
		}
		copyList.removeAll(removeList);
		return new PanelJointList(copyList);
	}

	//	public void addJoint(T joint){
//		if(!this.hasSameJoint(joint)){
//			joints.add(joint);
//		}
//	}
//	public void addAll(PanelBonusLayoutList<T> joint){
//		for(T elm:joint.joints){
//			this.addJoint(elm);
//		}
//	}
//	private boolean hasSameJoint(T other){
//		//			List<IPanelJoint> rt = Lists.newArrayList(target);
//		for(T joint:this.joints){
//			if(other.isSame(joint)){
//				return true;
//			}
//		}
//		return false;
//	}
//	private boolean hasCollisionJoint(T other){
//		for(T joint:this.joints){
//			if(other.containsAny(joint)){
//				return true;
//			}
//		}
//		return false;
//	}
	public PanelJointList<T> sort(){
		List<T> copyList = Lists.newArrayList(this.joints);
		Collections.sort(copyList);
		return new PanelJointList(copyList);
	}



}