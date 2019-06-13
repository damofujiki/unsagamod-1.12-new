package mods.hinasch.unsaga.init;

import com.google.common.collect.ImmutableList;

import mods.hinasch.unsaga.init.UnsagaOreRegistry.OreUnsaga;

public class UnsagaOres {

	public static final UnsagaOreRegistry.OreUnsaga LEAD = new OreUnsaga(0,"ore_lead","oreLead",0.7F,1);
	public static final UnsagaOreRegistry.OreUnsaga RUBY = new OreUnsaga(1,"ore_ruby","oreRuby",1.0F,2);
	public static final UnsagaOreRegistry.OreUnsaga SAPPHIRE = new OreUnsaga(2,"ore_sapphire","oreSapphire",1.0F,2);
	public static final UnsagaOreRegistry.OreUnsaga SILVER = new OreUnsaga(3,"ore_silver","oreSilver",0.7F,1);
	public static final UnsagaOreRegistry.OreUnsaga COPPER = new OreUnsaga(4,"ore_copper","oreCopper",0.7F,0);
	public static final UnsagaOreRegistry.OreUnsaga ANGELITE = new OreUnsaga(5,"ore_angelite","oreAngelite",1.0F,1);
	public static final UnsagaOreRegistry.OreUnsaga DEMONITE = new OreUnsaga(6,"ore_demonite","oreDemonite",1.0F,1);

	public static final ImmutableList<OreUnsaga> ALL_ORES = ImmutableList.of(LEAD,RUBY,SAPPHIRE,SILVER,COPPER,ANGELITE,DEMONITE);

}
