package mods.hinasch.unsaga.minsaga;

import java.util.List;

import javax.annotation.Nonnull;

import mods.hinasch.lib.capability.ISyncCapability;

public interface IMinsagaForge extends IStatusModifier,ISyncCapability{

	public List<MinsagaMaterialInitializer.Ability> getAbilities();
	public float attackModifier();
	public ArmorModifier armorModifier();
	public float efficiencyModifier();
	public @Nonnull MaterialLayer getCurrentFittingLayer();
	public void updateCurrentFitting(int value);
	public void addLayer(MinsagaMaterial material);
	public int getLayerCount();
	public List<MaterialLayer> layers();
	public boolean hasForged();
	public void removeUnfinished();
	public void setLayerList(List<MaterialLayer> list);
}