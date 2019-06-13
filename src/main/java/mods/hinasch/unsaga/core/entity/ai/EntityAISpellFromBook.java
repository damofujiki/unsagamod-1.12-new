//package mods.hinasch.unsaga.core.entity.ai;
//
//import java.util.List;
//import java.util.stream.Collectors;
//
//import com.google.common.collect.Lists;
//
//import mods.hinasch.lib.item.ItemUtil;
//import mods.hinasch.unsagamagic.item.ItemSpellBook;
//import mods.hinasch.unsagamagic.spell.Spell;
//import mods.hinasch.unsagamagic.spell.UnsagaSpells;
//import mods.hinasch.unsagamagic.spell.spellbook.SpellBookCapability;
//import net.minecraft.item.ItemStack;
//
//@Deprecated
//public class EntityAISpellFromBook extends EntityAISpell{
//
//
//	public static final List<Spell> blackList = Lists.newArrayList(UnsagaSpells.SHADOW_SERVANT,UnsagaSpells.SIMULACRUM);
//	public static final List<Spell> castBonus = Lists.newArrayList(UnsagaSpells.FIRE_ARROW,UnsagaSpells.BUBBLE_BLOW,UnsagaSpells.STONE_SHOWER);
//	public EntityAISpellFromBook(ISpellAI host,double moveSpeed, int interval,
//			float search, int chance) {
//		super(host, Lists.newArrayList(), moveSpeed, interval, search, chance);
//		// TODO 自動生成されたコンストラクター・スタブ
//	}
//
//
//	@Override
//    public List<SpellAIData> getSpellList(){
//    	ItemStack held = this.entityHost.getHeldItemMainhand();
//    	if(ItemUtil.isItemStackPresent(held)){
//    		if(held.getItem() instanceof ItemSpellBook && SpellBookCapability.ADAPTER.hasCapability(held)){
//    			return SpellBookCapability.ADAPTER.getCapability(held).getRawSpells()
//    					.stream().filter(in -> !blackList.contains(in)).map(in -> new SpellAIData(in, 30.0D, 0.0D, castBonus.contains(in)? in.castingTime()/2:in.castingTime())).collect(Collectors.toList());
//
//    		}
//    	}
//    	return Lists.newArrayList();
//    }
//
//}
