package mods.hinasch.unsaga.ability.specialmove.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.ImmutableList;

import mods.hinasch.lib.sync.SafeUpdateEventByInterval;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.SoundAndSFX;
import mods.hinasch.lib.world.ScannerBuilder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


	public class AsyncSafeBlockCrasher extends SafeUpdateEventByInterval{

		static final String ID = "pulverizer";
		final ScannerBuilder.Ready scanner;
		final World world;
		final EntityLivingBase owner;
		final Iterator<BlockPos> iterator;
		final List<BlockPos> blockPosList;
		final List<String> toolTypes;

		public AsyncSafeBlockCrasher(World world,EntityLivingBase owner,ScannerBuilder.Ready scanner,List<String> types) {
			super(owner,ID);
			this.world = world;
			this.scanner = scanner;
			this.owner = owner;
			Iterator<BlockPos> ite = this.scanner.getIterableInstance().iterator();
			List<BlockPos> list = new ArrayList<>();
			while(ite.hasNext()){
				BlockPos pos = ite.next();
				if(!world.isAirBlock(pos)){
					list.add(pos);
				}
			}
			this.blockPosList = ImmutableList.copyOf(list);
			this.iterator = this.blockPosList.iterator();
			this.toolTypes = types;
		}

		@Override
		public int getIntervalThresold() {
			// TODO 自動生成されたメソッド・スタブ
			return 0;
		}

		@Override
		public void loopByInterval() {

//			BlockPos pos = this.iterator.next();

			if(!this.hasFinished()){
				BlockPos pos = this.iterator.next();

				if(this.loopCheck(pos)){
					IBlockState id = world.getBlockState(pos);
					id.getBlock().dropXpOnBlockBreak(world, pos, id.getBlock().getExpDrop(id, world, pos, 0));
					SoundAndSFX.playBlockBreakSFX(world, pos, id,true);

				}
			}

		}

		public boolean loopCheck(BlockPos pos){
			return this.toolTypes.stream().anyMatch(in -> {
				if(in.equals("shovel")){
					return HSLibs.canBreakAndEffectiveBlock(world,owner,in,pos,new ItemStack(Items.IRON_SHOVEL));
				}else{
					return HSLibs.canBreakAndEffectiveBlock(world,owner,in,pos);
				}
			});

		}
		@Override
		public boolean hasFinished() {
			// TODO 自動生成されたメソッド・スタブ
			return !this.iterator.hasNext();
		}

	}