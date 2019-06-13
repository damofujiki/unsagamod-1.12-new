package mods.hinasch.unsaga.skillpanel;

import java.util.function.Predicate;

import net.minecraft.entity.Entity;

public class SkillPanelNegative extends SkillPanel{

	enum Type{WEAKNESS,DAMAGE};
	final Predicate<Entity> entityPredicate;
	final Type type;

	public SkillPanelNegative(Builder builder) {
		super(builder);
		this.entityPredicate = builder.entityPredicate;
		this.type = builder.type;
	}

	public Type getNegativeType(){
		return this.type;
	}

	public Predicate<Entity> getTargetEntityFilter(){
		return this.entityPredicate;
	}


	public static class Builder extends SkillPanel.Builder{

		final Predicate<Entity> entityPredicate;
		final Type type;
		public Builder(String string, Predicate<Entity> pre,SkillPanelNegative.Type weakness) {
			super(string);
			this.entityPredicate = pre;
			this.type = weakness;
		}

		@Override
		public SkillPanelNegative build(){
			return new SkillPanelNegative(this);
		}
	}
}
