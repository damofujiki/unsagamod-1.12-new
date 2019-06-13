package mods.hinasch.unsaga.core.event;

import mods.hinasch.lib.core.HSLib;
import mods.hinasch.lib.item.CustomDropAdapter;
import mods.hinasch.lib.item.CustomDropAdapter.EntityDropper;
import mods.hinasch.unsaga.core.entity.mob.EntityRuffleTree;
import mods.hinasch.unsaga.core.entity.mob.EntityTreasureSlimeNew;
import mods.hinasch.unsaga.core.entity.passive.EntityUnsagaChest;
import mods.hinasch.unsaga.core.potion.UnsagaPotions;
import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew;
import mods.hinasch.unsaga.material.UnsagaMaterials;
import mods.hinasch.unsagamagic.spell.tablet.TabletInitializer;
import net.minecraft.entity.monster.AbstractIllager;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntitySpider;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class MobDropHandlerUnsaga {

	public void init(){
		//魔道板
		CustomDropAdapter dropTablet = new CustomDropAdapter();
		dropTablet.setDropValueGetter(in ->{
			if(in.getDeadEntity() instanceof EntityWitch){
				return 0.15F;
			}
			if(in.getDeadEntity() instanceof EntityEnderman){
				return 0.10F;
			}
			if(in.getDeadEntity() instanceof EntityTreasureSlimeNew){
				return 0.35F;
			}
			if(in.getDeadEntity() instanceof AbstractIllager){
				return 0.10F;
			}
			return 0F;
		});
		dropTablet.setItemDrop(in ->TabletInitializer.drawRandomTablet(in.rand()).createStack(1));
		//チェスト
		CustomDropAdapter dropChest = new CustomDropAdapter();
		dropChest.setDropValueGetter(in ->{
			int rate = UnsagaConfigHandlerNew.GENERATION.mobDropChestRate;
			float base = 0.01F * (float)rate;
			if(in.getDeadEntity().getMaxHealth()>=20.0F){
				return base * 2.0F;
			}
			if(in.getDeadEntity().getMaxHealth()>=8.0F){
				return base;
			}
			if(in.getDeadEntity().getMaxHealth()>=2.0F){
				return base * 0.5F;
			}
			return 0F;
		});
		dropChest.setDropConsumer(new EntityDropper(in -> new EntityUnsagaChest(in.getDeadEntity().getEntityWorld())));
		//甲板
		CustomDropAdapter dropChitin = new CustomDropAdapter();
		dropChitin.setDropValueGetter(in ->{
			if(in.getDeadEntity() instanceof EntitySpider){
				return 0.2F;
			}
			return 0F;
		});
		dropChitin.setItemDrop(in ->UnsagaMaterials.CHITIN.createStack(1));
		//ゴールドフィンガーの実装（ゴールドフィンガー効果中に敵を倒すと30%+10%*レベルで金ナゲットをドロップ）
		CustomDropAdapter goldFingerImpl = new CustomDropAdapter();
		goldFingerImpl.setPredicate(in -> {
			EntityPlayer ep = (EntityPlayer) in.getDamageSource().getTrueSource();
			return ep.isPotionActive(UnsagaPotions.GOLD_FINGER);
		});
		goldFingerImpl.setDropValueGetter(in ->{
			EntityPlayer ep = (EntityPlayer) in.getDamageSource().getTrueSource();
			return 0.3F+0.1F*(ep.getActivePotionEffect(UnsagaPotions.GOLD_FINGER).getAmplifier()+1);
		});
		goldFingerImpl.setItemDrop(in -> new ItemStack(Items.GOLD_NUGGET,1));
		//ラッフルツリーのドロップおまけ
		CustomDropAdapter dropMush = new CustomDropAdapter();
		dropMush.setDropValueGetter(in ->{
			if(in.getDeadEntity() instanceof EntityRuffleTree){
				return 1.0F;
			}
			return 0F;
		});
		dropMush.setItemDrop(in -> new ItemStack(Blocks.BROWN_MUSHROOM,1));

		HSLib.addMobDrop(goldFingerImpl);
		HSLib.addMobDrop(dropChitin);
		HSLib.addMobDrop(dropChest);
		HSLib.addMobDrop(dropTablet);
		HSLib.addMobDrop(dropMush);


//		HSLib.addMobDrop(dropTablet.setLogger(UnsagaMod.logger,"TabletDrop"));
//		HSLib.addMobDrop(dropChest.setLogger(UnsagaMod.logger,"Chest"));
//		HSLib.addMobDrop(dropChitin.setLogger(UnsagaMod.logger,"Chitin"));
//		HSLib.addMobDrop(dropMush.setLogger(UnsagaMod.logger,"Mush"));
//		HSLib.addMobDrop(dropNugget.setLogger(UnsagaMod.logger,"nugget"));
	}
}
