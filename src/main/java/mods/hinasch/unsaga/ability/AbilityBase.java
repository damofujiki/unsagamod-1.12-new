package mods.hinasch.unsaga.ability;

import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.UtilNBT.RestoreFunc;
import mods.hinasch.unsaga.UnsagaMod;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public abstract class AbilityBase extends IForgeRegistryEntry.Impl<IAbility> implements IAbility{


	String unlName;

	public AbilityBase(String name) {
//		super(new ResourceLocation(name), name);
//		this.setRegistryName(new ResoureceLocation(UnsagaMod.MODID))
		this.setRegistryName(new ResourceLocation(UnsagaMod.MODID,name));
		this.setUnlocalizedName(name);
	}

	@Override
	public void writeToNBT(NBTTagCompound stream) {
		stream.setString("id",this.getRegistryName().toString());

	}

	@Override
	public String toString(){
		return this.getRegistryName().toString();
	}

	@Override
	public String getUnlocalizedName() {
		// TODO 自動生成されたメソッド・スタブ
		return this.unlName;
	}

	@Override
	public String getLocalized() {
		// TODO 自動生成されたメソッド・スタブ
		return HSLibs.translateKey(this.getUnlocalizedName());
	}


	@Override
	public void setUnlocalizedName(String unl) {
		// TODO 自動生成されたメソッド・スタブ
		this.unlName = unl;
	}
//	public String getUnlocalizedName(){
//		return "ability."+this.getPropertyName();
//	}
	public static final RestoreFunc<IAbility> FUNC_RESTORE = new RestoreFunc<IAbility>(){

		@Override
		public IAbility apply(NBTTagCompound input) {
			String id = input.getString("id");
			return AbilityAPI.getAbilityByID(id);
		}
	};
}
