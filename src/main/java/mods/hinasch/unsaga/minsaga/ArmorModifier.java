package mods.hinasch.unsaga.minsaga;

import com.mojang.realmsclient.util.Pair;

public class ArmorModifier extends Pair<Float,Float>{
	public static final ArmorModifier ZERO = new ArmorModifier(0F,0F);
	public ArmorModifier(Float first, Float second) {
		super(first, second);
		// TODO 自動生成されたコンストラクター・スタブ
	}

	public float melee(){
		return this.first();
	}

	public float magic(){
		return this.second();
	}
}