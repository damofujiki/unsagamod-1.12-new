package mods.hinasch.unsaga.ability.slot;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import mods.hinasch.lib.iface.INBTWritable;
import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.unsaga.ability.Abilities;
import mods.hinasch.unsaga.ability.Ability;
import mods.hinasch.unsaga.ability.AbilityCapability;
import mods.hinasch.unsaga.ability.IAbility;
import mods.hinasch.unsaga.ability.specialmove.Tech;
import mods.hinasch.unsaga.ability.specialmove.TechInvoker.InvokeType;
import mods.hinasch.unsaga.ability.specialmove.action.TechActionBase;
import mods.hinasch.unsaga.common.UnsagaWeightType;
import mods.hinasch.unsaga.core.item.misc.ItemTechBook;
import mods.hinasch.unsaga.material.UnsagaMaterial;
import mods.hinasch.unsaga.util.ToolCategory;
import mods.hinasch.unsaga.util.UnsagaTextFormatting;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;

/**
 *
 * AbilitySlotのリストを持つラッパー。
 * 選択中の技・置き換え可能なスロットの情報も持つ。
 *
 */
public class AbilitySlotList implements INBTWritable{

	final NonNullList<IAbilitySlot> abilitySlots;
	final int replaceableSlot;
	final boolean isSelectable;

	public AbilitySlotList(Builder builder){
		this.abilitySlots = NonNullList.withSize(builder.size(), AbilitySlot.EMPTY);
		int index = 0;
		for(AbilitySlotType type:builder.list){
			this.abilitySlots.set(index, new AbilitySlot(type,index));
			index ++;
		}
		this.replaceableSlot = builder.replace;
		this.isSelectable = builder.selectable;
	}

	public AbilitySlotList(NBTTagCompound nbt){
		int size = nbt.getInteger("size");
		this.isSelectable = nbt.getBoolean("isSelectable");
		List<AbilitySlotType> types = UtilNBT.readListFromNBT(nbt, "types", AbilitySlotType.RESTORE);
		this.abilitySlots = NonNullList.withSize(size, AbilitySlot.EMPTY);
		for(int i=0;i<types.size();i++){
			this.abilitySlots.set(i, new AbilitySlot(types.get(i),i));
		}
		List<IAbility> list = UtilNBT.readListFromNBT(nbt, "abilities", Ability.FUNC_RESTORE);
		this.replaceableSlot = nbt.getInteger("replaceable");
		this.apply(list);
	}


	/**
	 * 技スロットの選択を次に送る。
	 */
	public void nextTechSlot(ItemStack stack){
		AbilityCapability.adapter.getCapabilityOptional(stack)
		.ifPresent(in ->{
			int index = in.getSelectedIndex();
			index ++;
			if(index>=this.getSize()){
				index = 0;
			}
			in.setSelectedIndex(index);
		});

	}

	public boolean isSelectable(){
		return this.isSelectable;
	}
	protected void addTechBookToolTips(List<String> list,EntityPlayer el,ItemStack is){

		String abs = this.abilitySlots.stream()
				.map(in -> in.ability().getLocalizedInAbilityList())
				.collect(Collectors.joining("/"));
		list.add(HSLibs.translateKey("tooltip.unsaga.ability")+":"+abs);
		if(this.abilitySlots.get(0).ability().isTech()){
			Tech tech = (Tech) this.abilitySlots.get(0).ability();
			TechActionBase action = tech.getAction();
			list.add("Category:"+tech.getRestrictions().stream()
					.map(in -> in.getLocalized())
					.collect(Collectors.joining("/")));
			if(!action.isEmpty()){
				if(GuiContainer.isAltKeyDown()){
					for(InvokeType type:action.getInvokeTypes()){
						list.add(HSLibs.translateKey("tech.tips.invoke_type."+type.getName()));
					}
					if(!tech.getTranslatedDescription().isEmpty()){
						list.add(tech.getTranslatedDescription());
					}
				}else{
					list.add(UnsagaTextFormatting.PROPERTY_LOCKED+"Press [ALT] To Show Description");
				}

			}
		}

	}


	public void addTooltips(List<String> list,EntityPlayer el,ItemStack is){
		if(el==null)return;

		boolean isTechBook = is.getItem() instanceof ItemTechBook;

		if(isTechBook){
			this.addTechBookToolTips(list, el, is);
		}else{
			if(GuiContainer.isShiftKeyDown()){
				if(GuiContainer.isAltKeyDown()){
					for(int i=0;i<this.getSize();i++){
						this.getSlot(i).addTooltipPotentialAbility(list,el,is);
					}
				}else{
					for(int i=0;i<this.getSize();i++){
						if(this.isSelectable()){
							this.getSlot(i).addTooltip(list,i == this.getSelectedSlotIndex(is));
						}else{
							this.getSlot(i).addTooltip(list,false);
						}

					}
					list.add(UnsagaTextFormatting.PROPERTY_LOCKED+"[ALT] to Show Potential Abilities");
				}
			}else{
				String abs = this.abilitySlots.stream().map(in -> in.ability().getLocalized()).collect(Collectors.joining("/"));
				list.add(HSLibs.translateKey("tooltip.unsaga.ability")+":"+abs);
				list.add(UnsagaTextFormatting.PROPERTY_LOCKED+"[SHIFT] to Expand Ability Info");
			}
		}

	}

	public void apply(List<IAbility> list){
		for(int i=0;i<this.abilitySlots.size();i++){
			this.updateSlot(i, list.get(i));
		}
	}

	/**
	 * 全スロットからスロット位置に対応したそのままのアビリティリスト(NonNullList)を得る。
	 * スロット位置の情報も欲しい時はこっち
	 * @return
	 */
	public NonNullList<IAbility> toNonNullList(){
		List<IAbility> list = this.slotStream(null)
				.map(in -> in.ability())
				.collect(Collectors.toList());
		return ItemUtil.toNonNull(list, Abilities.EMPTY);
	}

	/**
	 * 選択中の技
	 * @return
	 */
	public IAbilitySlot getCurrentTechSlot(ItemStack is){
		return this.getSlot(this.getSelectedSlotIndex(is));
	}

	public int getSelectedSlotIndex(ItemStack is){
		return AbilityCapability.adapter.getCapabilityOptional(is)
		.filter(in -> this.isSelectable)
		.filter(in -> in.getSelectedIndex() < this.getSize())
		.map(in ->in.getSelectedIndex())
		.orElse(0);

	}

	/**
	 * 覚える事のできるアビリティを返す（リストの番号はスロット番号に対応）
	 * @param category
	 * @param m
	 * @param weight
	 * @return
	 */
	public NonNullList<Set<IAbility>> getLearnableAbilities(ToolCategory category,UnsagaMaterial m,UnsagaWeightType weight){
		List<Set<IAbility>> list = this.slotStream(in ->in.isSlotLearnable())
				.map(in -> in.getLearnableAbilities(category, m, weight)).collect(Collectors.toList());
		return ItemUtil.toNonNull(list, Sets.newHashSet());
	}

	private Stream<IAbilitySlot> slotStream(Predicate<IAbilitySlot> predicate){
		return this.getSlots().stream().filter(predicate == null ?(in -> true) : predicate);
	}


	/**
	 * 空きがあり、アビリティ解放の可能性があるスロットを取得。
	 * @param category
	 * @param m
	 * @param weight
	 * @return
	 */
	public List<IAbilitySlot> getLearnableSlots(ToolCategory category,UnsagaMaterial m,UnsagaWeightType weight){
		return this.slotStream(in ->in.ability().isAbilityEmpty() && in.isSlotLearnable())
				.filter(in ->!in.getLearnableAbilities(category, m, weight).isEmpty())
				.collect(Collectors.toList());
	}

	public List<IAbility> getUnlockedAbilities(){
		return this.getUnlockedAbilities(in -> true);
	}
	/**
	 * 覚えているアビリティだけをリストで取得。スロットの位置は保証されない、空は無視。
	 * @param predicate
	 * @return
	 */
	public List<IAbility> getUnlockedAbilities(Predicate<IAbilitySlot> predicate){
		return this.slotStream(in -> !in.ability().isAbilityEmpty()).filter(predicate).map(in -> in.ability()).collect(Collectors.toList());
	}

	public List<IAbilitySlot> getUnlockedSlots(){
		return this.slotStream(in -> !in.ability().isAbilityEmpty()).collect(Collectors.toList());
	}

	public int getReplaceableSlot(){
		return this.replaceableSlot;
	}

	/**
	 * スロットのリストサイズを取得
	 * @return
	 */
	public int getSize(){
		return this.abilitySlots.size();
	}
	protected IAbilitySlot getSlot(int index){
		return this.getSlots().get(index);
	}

	public IAbility getAbility(int slot){
		return this.abilitySlots.get(slot).ability();
	}

	public List<IAbilitySlot> getSlots(){
		return ImmutableList.copyOf(this.abilitySlots);
	}

	/**
	 * 全スロットのタイプをリストで取得
	 * @return
	 */
	private List<AbilitySlotType> getTypes(){
		return this.abilitySlots.stream().map(in -> in.getType()).collect(Collectors.toList());
	}

	public boolean hasAbility(IAbility ab){
		return this.abilitySlots.contains(ab);
	}


	public boolean existLearnableSlot(){
		return this.slotStream(in -> in.ability().isAbilityEmpty()).anyMatch(in -> in.isSlotLearnable());
	}

	/**
	 * スロットが全て空か
	 * @return
	 */
	public boolean isAllEmpty(){
		return this.abilitySlots.stream().allMatch(in -> in.ability().isAbilityEmpty());
	}


	public void load(NBTTagCompound nbt){
//		int size = nbt.getInteger("size");
//		List<AbilitySlotType> types = UtilNBT.readListFromNBT(nbt, "types", AbilitySlotType.RESTORE);
//		this.abilitySlots = NonNullList.withSize(size, AbilitySlot.EMPTY);
//		for(int i=0;i<types.size();i++){
//			this.updateSlot(i, types.get(i));
//			this.abilitySlots.set(i, new AbilitySlot(types.get(i),i));
//		}
		List<IAbility> list = UtilNBT.readListFromNBT(nbt, "abilities", Ability.FUNC_RESTORE);
//		this.replaceable = nbt.getInteger("replaceable");
		this.apply(list);
	}




	public void removeAbility(int index){
		this.updateSlot(index, Abilities.EMPTY);
	}
	public String toString(){
		String str = this.getSlots().stream()
				.map(in -> in.ability().getLocalized())
				.collect(Collectors.joining("/"));
		return str.isEmpty() ? Abilities.EMPTY.getLocalized() : str;
	}

	//abilityList唯一のセットメソッド、スロットの中身を入れ替えるだけ
	//（スロットごと入れ替えてた名残）
	public void updateSlot(int index,IAbility ab){
		IAbilitySlot slot = this.getSlot(index);
//		this.getSlot(index).setAbility(ab);
		this.abilitySlots.set(index, slot.update(ab));
	}

	@Override
	public void writeToNBT(NBTTagCompound stream) {
		stream.setInteger("size", this.getSize());
		UtilNBT.writeListToNBT(this.getTypes(),stream, "types");
		UtilNBT.writeListToNBT(this.toNonNullList(), stream, "abilities");
		stream.setInteger("replaceable", this.replaceableSlot);
		stream.setBoolean("isSelectable", isSelectable);
	}

	public static Builder builder(){
		return new Builder();
	}

	public static class Builder{
		int replace = -1;
		boolean selectable = false;
		NonNullList<AbilitySlotType> list = NonNullList.withSize(1, AbilitySlotType.NO_FUNCTION);

		public Builder replaceable(int re){
			this.replace = re;
			return this;
		}

		public Builder selectable(boolean se){
			this.selectable = se;
			return this;
		}

		public Builder slot(AbilitySlotType... types){
			this.list = NonNullList.withSize(types.length, AbilitySlotType.NO_FUNCTION);
			int index = 0;
			for(AbilitySlotType type:types){
				list.set(index,type);
				index ++;
			}
			return this;
		}

		public Builder slot(int size,AbilitySlotType type){
			this.list = NonNullList.withSize(size,type);
			return this;
		}

		public int size(){
			return this.list.size();
		}

		public AbilitySlotList build(){
			return new AbilitySlotList(this);
		}
	}
}
