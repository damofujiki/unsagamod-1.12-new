package mods.hinasch.unsaga.villager.smith;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

import mods.hinasch.lib.item.ItemUtil;
import mods.hinasch.unsaga.villager.smith.TraitToolForger.ForgeResultType;
import net.minecraft.item.ItemStack;

public class ValidPayments {

	public static enum Value{
		LOW("Low/25% Fail"){
			@Override
			public ForgeResultType decideForgeResult(Random rand,float fix){
				if(rand.nextFloat()<0.25F-fix){
					return ForgeResultType.BAD;
				}
				return super.decideForgeResult(rand, fix);
			}

			@Override
			public int decideForgedDurability(int base,boolean additional){
				float fix = additional ? 0.3F : 0;
				return (int)((float)base * (0.8F+fix));
			}
		},MID("Middle/10% Fail"){
			@Override
			public ForgeResultType decideForgeResult(Random rand,float fix){
				if(rand.nextFloat()<0.10F-fix){
					return ForgeResultType.BAD;
				}
				return super.decideForgeResult(rand, fix);
			}

			@Override
			public int decideForgedDurability(int base,boolean additional){
				float fix = additional ? 0.3F : 0;
				return (int)((float)base * (1.0F+fix));
			}
		},HIGH("High/No Fail"){
			@Override
			public ForgeResultType decideForgeResult(Random rand,float fix){
				if(rand.nextFloat()<0.15F+fix){
					return ForgeResultType.VERY_GOOD;
				}
				return super.decideForgeResult(rand, fix);
			}

			@Override
			public int decideForgedDurability(int base,boolean additional){
				float fix = additional ? 0.3F : 0;
				return (int)((float)base * (1.2F+fix));
			}
		},RICH("Rich/+Ability"){
			@Override
			public ForgeResultType decideForgeResult(Random rand,float fix){
				return ForgeResultType.VERY_GOOD;
			}

			@Override
			public int decideForgedDurability(int base,boolean additional){
				float fix = additional ? 0.3F : 0;
				return (int)((float)base * (1.5F+fix));
			}
		};

		private final String name;
		private Value(String name){
			this.name = name;
		}

		public String getName(){
			return this.name;
		}
		public ForgeResultType decideForgeResult(Random rand,float fix){
			return ForgeResultType.GOOD;
		}

		public int decideForgedDurability(int base,boolean additional){
			float fix = additional ? 0.3F : 0;
			return (int)((float)base * (1.0F+fix));
		}
	}


	public static Optional<Value> findValue(ItemStack is){
		return ValidPayments.LOOK_UP.entrySet().stream().filter(in -> ItemUtil.isOreDict(is, in.getKey()))
				.map(in -> in.getValue()).findFirst();
	}
	static final Map<String,Value> LOOK_UP = new HashMap<>();


	static{
		LOOK_UP.put("nuggetGold", Value.LOW);
		LOOK_UP.put("ingotGold", Value.MID);
		LOOK_UP.put("ingotSilver", Value.LOW);
		LOOK_UP.put("gemEmerald", Value.HIGH);
		LOOK_UP.put("gemDiamond", Value.RICH);
		LOOK_UP.put("ingotElectrum", Value.MID);
		LOOK_UP.put("ingotPlatinum ", Value.HIGH);
		LOOK_UP.put("blockGold ", Value.HIGH);
		LOOK_UP.put("blockSilver ", Value.MID);
	}

}
