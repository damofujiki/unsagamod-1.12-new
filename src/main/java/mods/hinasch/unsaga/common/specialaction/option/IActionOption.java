package mods.hinasch.unsaga.common.specialaction.option;


public interface IActionOption {

	public String getName();
	public String getLocalized();
	public mods.hinasch.unsaga.common.specialaction.IActionPerformer.TargetType getTargetType();
}
