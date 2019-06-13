package mods.hinasch.unsaga.skillpanel;

import io.netty.util.internal.StringUtil;
import mods.hinasch.lib.registry.RegistryUtil;
import mods.hinasch.lib.registry.RegistryUtil.IUnlocalizedName;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.skillpanel.SkillPanel.IconType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface ISkillPanel extends IForgeRegistryEntry<ISkillPanel>,Comparable<ISkillPanel>,IUnlocalizedName{
	default ItemStack getItemStack(int level){
		return SkillPanelInitializer.createStack(this, level);
	}
	default String getTranslatedDescription(){
		String key = "skillPanel."+this.getUnlocalizedName()+".desc";
		return HSLibs.canTranslate(key) ? HSLibs.translateKey(key) : StringUtil.EMPTY_STRING;
	}
	public SkillPanelInitializer.Rarity rarity();
	public PanelBonus panelBonus();
	default String getLocalized(){
		return HSLibs.translateKey("skillPanel."+this.getUnlocalizedName()+".name");
	}
	default int getPriority(){
		return this.iconType().getMeta();
	}
	default boolean isEmpty(){
		return this.getRegistryName()==RegistryUtil.EMPTY;
	}
	public IconType iconType();

	default int compareTo(ISkillPanel o) {
		return Integer.compare(this.getPriority(), o.getPriority());
	}
}
