package mods.hinasch.unsaga.lp;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import mods.hinasch.lib.iface.INBTWritable;
import mods.hinasch.lib.util.UtilNBT.RestoreFunc;
import net.minecraft.nbt.NBTTagCompound;

public class LPAttribute implements INBTWritable{




	public static final LPAttribute ZERO = new LPAttribute(0,0);

	public static final RestoreFunc<LPAttribute> RESTORE = in ->{
		if(in.hasKey("lpstr") && in.hasKey("chances")){
			float lpstr = in.getFloat("lpstr");
			int chances = in.getInteger("chances");
			return new LPAttribute(lpstr,chances);
		}
		return ZERO;
	};

	final float lpstr;

	/** LP減少率*/
	public float amount() {
		return lpstr;
	}

	/** LP減少の試行回数*/
	public int chances() {
		return attackCount;
	}
	final int attackCount;
	public LPAttribute(float lpstr, int attackCount) {
		super();
		this.lpstr = lpstr;
		this.attackCount = attackCount;
	}

	public LPAttribute(float lpstr) {
		this(lpstr,1);
	}

	public static LPAttribute of(String serializedData){
		List<Float> floats = Lists.newArrayList(Splitter.on(",").split(serializedData)).stream().map(in -> Float.valueOf(in)).collect(Collectors.toList());
		if(floats.size()<=1){
			floats.add(1.0F);
		}
		return new LPAttribute(floats.get(0),floats.get(1).intValue());
	}

	@Override
	public void writeToNBT(NBTTagCompound stream) {
		// TODO 自動生成されたメソッド・スタブ
		stream.setFloat("lpstr", lpstr);
		stream.setInteger("chances", attackCount);
	}
}