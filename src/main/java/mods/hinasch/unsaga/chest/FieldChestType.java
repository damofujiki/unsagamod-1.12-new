package mods.hinasch.unsaga.chest;

import java.util.Optional;
import java.util.Random;

import io.netty.util.internal.StringUtil;
import mods.hinasch.lib.iface.IIntSerializable;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.skillpanel.ISkillPanel;
import mods.hinasch.unsaga.skillpanel.SkillPanels;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;

/**
 *
 * マップの財宝タイプ
 *
 */
public enum FieldChestType implements IIntSerializable{

	/** 地上タイプ*/
	FIELD(1){
		@Override
		public String getName(){
			return "Field";
		}
		@Override
		public ISkillPanel getSuitableSkill(){
			return SkillPanels.GUIDE_ROAD;
		}

		@Override
		public BlockPos generateChestPos(World world,int x,int z){
			return world.getHeight(new BlockPos(x,0,z));
		}
	},
	/** 洞窟タイプ（地中）*/
	CAVE(2){
		@Override
		public String getName(){
			return "Cave";
		}
		@Override
		public ISkillPanel getSuitableSkill(){
			return SkillPanels.GUIDE_CAVE;
		}
	},
	/** 指定のないとき*/
	NORMAL(3),
	/** ブロックのチェストの時。デテクトトレジャーで使う*/
	BLOCK(4){
		@Override
		public String getName(){
			return "Block";
		}
	};

	private int meta;

	private FieldChestType(int meta){
		this.meta = meta;
	}
	@Override
	public int getMeta() {
		// TODO 自動生成されたメソッド・スタブ
		return meta;
	}

	public String getName(){
		return StringUtil.EMPTY_STRING;
	}
	public ISkillPanel getSuitableSkill(){
		return SkillPanels.DUMMY;
	}

	public BlockPos generateChestPos(World world,int x,int z){
		Random rand = world.rand;
		return Optional.of(world.getHeight(new BlockPos(x,0,z)))
				.filter(in ->in.getY()>0)
				.map(in ->new BlockPos(x,rand.nextInt(in.getY()) + 1,z))
				.orElse(null);
	}
	public static FieldChestType fromDimension(DimensionType dim){
		if(dim==DimensionType.NETHER){
			return FieldChestType.CAVE;
		}
		if(dim==DimensionType.THE_END){
			return FieldChestType.FIELD;
		}
		return FieldChestType.NORMAL;
	}
	public static FieldChestType fromMeta(int meta){
		return HSLibs.fromMeta(FieldChestType.values(), meta);
	}
}
