package mods.hinasch.unsaga.status;

import mods.hinasch.lib.capability.ISyncCapability;

public interface IUnsagaXP extends ISyncCapability{

	/** 現在のスキルポイントを得る*/
	public int getSkillPoint();
	public void setSkillPoint(int par1);
	public void addSkillPoint(int par1);
	/** スキルポイントの端数を得る*/
	public int getSkillPointPiece();
	public void setSkillPointPiece(int par1);
	public void addSkillPointPiece(int par1);
	@Deprecated
	public int getDecipheringPoint();
	@Deprecated
	public void setDecipheringPoint(int par1);
	@Deprecated
	public void addDecipheringPoint(int par1);
}