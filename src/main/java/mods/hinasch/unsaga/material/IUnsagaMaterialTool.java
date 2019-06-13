package mods.hinasch.unsaga.material;

import mods.hinasch.lib.capability.ISyncCapability;
import mods.hinasch.lib.iface.IRequireInitializing;
import mods.hinasch.unsaga.common.UnsagaWeight;
import mods.hinasch.unsaga.common.UnsagaWeightType;

public interface IUnsagaMaterialTool extends IRequireInitializing,ISyncCapability{


	public UnsagaWeight getWeight();
	public UnsagaWeightType getToolWeightType();
	public void setWeight(int weight);
	public UnsagaMaterial getMaterial();
	public void setMaterial(UnsagaMaterial m);


}
