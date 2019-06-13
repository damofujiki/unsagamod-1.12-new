package mods.hinasch.unsaga.minsaga;

public interface IStatusModifier{
	public float attackModifier();
	public ArmorModifier armorModifier();
	public float efficiencyModifier();
	public int costModifier();
	public int durabilityModifier();
	public int weightModifier();
}