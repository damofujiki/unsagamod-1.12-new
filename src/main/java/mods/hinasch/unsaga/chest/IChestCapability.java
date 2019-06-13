package mods.hinasch.unsaga.chest;

import java.util.List;
import java.util.Optional;

import mods.hinasch.lib.capability.ISyncCapability;
import mods.hinasch.lib.iface.IRequireInitializing;
import net.minecraft.entity.player.EntityPlayer;

/**
 * 宝箱のキャパビリティの中身
 */
public interface IChestCapability extends ISyncCapability,IRequireInitializing{

	public int level();
	public void setLevel(int par1);
	public List<ChestTrap> traps();
	public void updateTraps(List<ChestTrap> traps);
	public boolean hasLocked();
	public void setLocked(boolean par1);
	public boolean hasDefused();
	public void setDefused();
	public boolean hasAnalyzed();
	public void setAnalyzed(boolean par1);
	public Class getChestType();
	public void setChestType(Class clazz);
	public void setOpeningPlayer(EntityPlayer ep);
	public Optional<EntityPlayer> openingPlayer();
	public boolean hasOpened();
	public void setOpened(boolean par1);
	public boolean hasMagicLocked();
	public void setMagicLocked(boolean par1);
	public ChestTreasureType treasureType();
	public void setTreasureType(ChestTreasureType par1);
}
