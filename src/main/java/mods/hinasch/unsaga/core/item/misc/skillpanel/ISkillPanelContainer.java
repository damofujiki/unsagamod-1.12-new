package mods.hinasch.unsaga.core.item.misc.skillpanel;

import mods.hinasch.unsaga.skillpanel.ISkillPanel;

public interface ISkillPanelContainer {

	public ISkillPanel panel();
	/** (1-5)*/
	public int level();
	public void setPanel(ISkillPanel panel);
	public void setLevel(int level);
	public boolean hasJointed();
	public boolean hasLocked();
	public void setJointed(boolean par1);
	public void setLocked(boolean par1);

	/**
	 * パネルとレベルが一致
	 * @param other
	 * @param otherLevel
	 * @return
	 */
	default boolean equalPanel(ISkillPanel other,int otherLevel){
		return this.panel()==other && this.level() == otherLevel;
	}
}
