package mods.hinasch.unsaga.common;

public class UnsagaWeight {

	final int value;

	public UnsagaWeight(int weight){
		this.value = weight;
	}

	public int getValue(){
		return this.value;
	}

	public UnsagaWeightType type(){
		return UnsagaWeightType.fromWeight(value);
	}

	public boolean isHeavy(){
		return this.type().isHeavy();
	}

	@Override
	public String toString(){
		return "weight value:"+this.value+"/"+this.type().toString();
	}
}
