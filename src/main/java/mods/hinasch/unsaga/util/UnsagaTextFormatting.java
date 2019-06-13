package mods.hinasch.unsaga.util;

import net.minecraft.util.text.TextFormatting;

public class UnsagaTextFormatting {

	/** 解放されてない固有情報*/
	public static TextFormatting PROPERTY_LOCKED = TextFormatting.DARK_GRAY;
	/** 固有情報*/
	public static TextFormatting PROPERTY = TextFormatting.AQUA;
	/** ポジティブなこと*/
	public static TextFormatting POSITIVE = TextFormatting.BLUE;
	/** ネガティブ*/
	public static TextFormatting NEGATIVE = TextFormatting.RED;
	/** 強調*/
	public static TextFormatting SIGNIFICANT = TextFormatting.YELLOW;

	public static TextFormatting getColorFrom(double d){
		if(d==0){
			return TextFormatting.WHITE;
		}
		if(d>0){
			return UnsagaTextFormatting.POSITIVE;
		}else{
			return UnsagaTextFormatting.NEGATIVE;
		}
	}
	public static String getColored(double d){
		TextFormatting color = null;

		if(d>0){
			color = UnsagaTextFormatting.POSITIVE;
		}else{
			color = UnsagaTextFormatting.NEGATIVE;
		}
		if(d==0){
			color = TextFormatting.WHITE;
		}
		return color.toString()+String.format("%.2f", d);
	}
}
