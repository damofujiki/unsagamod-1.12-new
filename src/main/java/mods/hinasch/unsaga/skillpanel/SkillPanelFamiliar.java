package mods.hinasch.unsaga.skillpanel;

import mods.hinasch.unsaga.element.FiveElements;

public class SkillPanelFamiliar extends SkillPanel{

	final FiveElements.Type element;


	public SkillPanelFamiliar(Builder builder) {
		super(builder);
		this.element = builder.element;
	}



	public FiveElements.Type getElement(){
		return this.element;
	}

	public static class Builder extends SkillPanel.Builder{

		FiveElements.Type element;

		public Builder(String name,FiveElements.Type element){
			super(name);
			this.element = element;
		}

		@Override
		public SkillPanelFamiliar build(){
			return new SkillPanelFamiliar(this);
		}
	}
}
