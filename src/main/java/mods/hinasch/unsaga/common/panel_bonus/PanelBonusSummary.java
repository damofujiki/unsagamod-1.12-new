package mods.hinasch.unsaga.common.panel_bonus;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.collect.Maps;
import com.mojang.realmsclient.util.Pair;

import mods.hinasch.lib.entity.ModifierHelper;
import mods.hinasch.lib.util.Statics;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.skillpanel.PanelBonus;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;

/** パネルボーナスのまとめ*/
public class PanelBonusSummary {

	public static final String NAME = UnsagaMod.MODID+".panelBonus";
	public static final UUID PANEL_UUID = UUID.fromString("7f4c3c3c-c447-4c55-b941-66ccee700ce3");

	Map<Hex,Pair<IPanelJoint,PanelBonus>> map = Maps.newHashMap();

	public void add(Hex base,IPanelJoint type,PanelBonus bonus){
		map.put(base, Pair.of(type,bonus));
	}

	public PanelBonus getTotalBonusAmount(){
		return map.values().stream().map(in -> in.second().applyMultiply()).reduce(PanelBonus.EMPTY, (a,b)->a.combine(b));
//		Map<IAttribute,Integer> amount = Maps.newHashMap();
//		for(Pair<IPanelBonusLayout,PanelBonus> pair:map.values()){
//			amount.putAll(pair.second().multiplied().getMap());
//		}
//		return new PanelBonus(amount);
	}

	public void applyBonus(EntityLivingBase living){
		PanelBonus bonus = this.getTotalBonusAmount();
		bonus.buildActualAttributeMap().entrySet().forEach(in ->{
			ModifierHelper.refleshModifier(living, in.getKey(), new AttributeModifier(PANEL_UUID, NAME, in.getValue(), Statics.OPERATION_INCREMENT));
		});
	}
	public void addSummaryTips(List<String> tip){
		map.keySet().stream().sorted().forEach(in ->{
			String bonustype = map.get(in).first().getLayoutName();
			PanelBonus bonus = map.get(in).second();
			tip.add(String.format("[%s]", bonustype));
			bonus.addTips(tip);
		});
	}
}
