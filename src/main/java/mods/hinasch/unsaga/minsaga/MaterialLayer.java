package mods.hinasch.unsaga.minsaga;

import mods.hinasch.lib.iface.INBTWritable;
import net.minecraft.nbt.NBTTagCompound;

public class MaterialLayer implements INBTWritable{

	public static final MaterialLayer EMPTY = new MaterialLayer();

	public static MaterialLayer restore(NBTTagCompound input){
		MinsagaMaterial m = MinsagaMaterialInitializer.get(input.getString("material"));
		int progress = input.getInteger("progress");
		int max = input.getInteger("max");

		return new MaterialLayer(m,max,progress);
	}
	final private MinsagaMaterial forgedMaterial;
	final private int maxFitCount ;
	final private int fittingCount ;

	public static MaterialLayer newLayer(MinsagaMaterial material,int maxcount){
		return new MaterialLayer(material,maxcount,0);
	}
	public MaterialLayer(){
		this(MinsagaMaterials.EMPTY,0,0);
	}
	public MaterialLayer(MinsagaMaterial material,int maxFitCount,int fittingCount){
		this.forgedMaterial = material;
		this.maxFitCount = maxFitCount;
		this.fittingCount = fittingCount;
	}
	public int getFittingProgress() {
		return fittingCount;
	}

	public boolean isEmptyLayer(){
		return this.getMaterial()==MinsagaMaterials.EMPTY;
	}
	public boolean hasFinishedFitting(){
		return this.getFittingProgress() >= this.maxFittingProgress() && !this.isEmptyLayer();
	}
	public MinsagaMaterial getMaterial() {
		return forgedMaterial;
	}
	public int maxFittingProgress() {
		return maxFitCount;
	}

	public MaterialLayer update(int progress){
		return new MaterialLayer(this.forgedMaterial,this.maxFitCount,progress);
	}

//	public void setFittingProgress(int suitCount) {
//		this.fittingCount = suitCount;
//	}
//	public void setMaterial(MinsagaMaterial forgedMaterial) {
//		this.forgedMaterial = forgedMaterial;
//	}
//	public void setMaxFittingProgress(int maxSuitCount) {
//		this.maxFitCount = maxSuitCount;
//	}



	@Override
	public void writeToNBT(NBTTagCompound stream) {
		stream.setString("material", this.forgedMaterial.getRegistryName().toString());
		stream.setInteger("progress", this.getFittingProgress());
		stream.setInteger("max", this.maxFittingProgress());

	}

}