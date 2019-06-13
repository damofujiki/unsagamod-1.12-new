package mods.hinasch.unsaga.skillpanel;



import com.mojang.realmsclient.util.Pair;

import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;

public class SkillPanelModifier extends SkillPanel{

	final Pair<IAttribute,AttributeModifier> modifierPair;

	public SkillPanelModifier(Builder builder) {
		super(builder);
		this.modifierPair = Pair.of(builder.modifierPair.first(),builder.modifierPair.second());
	}

	public Pair<IAttribute,AttributeModifier> getModifierPair(){
		return this.modifierPair;
	}

	public static class Builder extends SkillPanel.Builder{



		final Pair<IAttribute,AttributeModifier> modifierPair;

		public Builder(String name,IAttribute at,AttributeModifier mod){
			super(name);
			this.modifierPair = Pair.of(at,mod);
		}

		@Override
		public SkillPanelModifier build(){
			return new SkillPanelModifier(this);
		}
	}
}
