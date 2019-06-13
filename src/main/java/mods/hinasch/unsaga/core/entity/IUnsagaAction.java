package mods.hinasch.unsaga.core.entity;

import mods.hinasch.lib.capability.ISyncCapability;
import mods.hinasch.unsaga.util.ToolCategory;
import mods.hinasch.unsaga.villager.IInteractionState;

public interface IUnsagaAction extends IInteractionState,ISyncCapability {

	public int getWeaponGuardCooling();
	public void decrWeaponGuardProgress();
	public void resetWeaponGuardProgress(ToolCategory cate);
	public void decrWeaponGuardCooling();
	public int getWeaponGuardProgress();
	public int getSprintTimer();
	public void setSprintTimer(int timer);
}
