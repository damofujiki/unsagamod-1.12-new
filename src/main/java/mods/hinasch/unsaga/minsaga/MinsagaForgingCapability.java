package mods.hinasch.unsaga.minsaga;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;

import mods.hinasch.lib.capability.CapabilityAdapterFactory.CapabilityAdapterPlanImpl;
import mods.hinasch.lib.capability.CapabilityAdapterFrame;
import mods.hinasch.lib.capability.CapabilityStorage;
import mods.hinasch.lib.capability.ComponentCapabilityAdapters;
import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.network.PacketSyncCapability;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.lib.util.UtilNBT;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.minsaga.MinsagaMaterialInitializer.Ability;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class MinsagaForgingCapability {

	public static class DefaultImpl implements IMinsagaForge{

//		NonNullList<MaterialLayer> layers = NonNullList.withSize(MAX_LAYER_COUNT, MaterialLayer.EMPTY);

		List<MaterialLayer> newLayers = new ArrayList<>();



		@Override
		public void addLayer(MinsagaMaterial material) {

			//			int index = this.getCurrentEmptyLayer();
			//			/** フィッティングが終わってないなら現在の改造中のレイヤー、
			//			 * でなければ次のレイヤー*/
			//			if(this.getCurrentFittingLayer().hasFinishedFitting()){
			//				index ++;
			//			}
			//			/** 改造限界の場合はMAX-1番目（一番最後）を上書き*/
			//			if(this.getLayerCount()>=MAX_LAYER_COUNT){
			//				index = this.getLayerCount() - 1;
			//			}
			////			MaterialLayer newForge = new MaterialLayer();
			////			newForge.setMaterial(material);
			////			newForge.setFittingProgress(0);
			////			newForge.setMaxFittingProgress(PROGRESS_BASE * (this.getLayerCount()+1));
			//			this.layers.set(index, new MaterialLayer(material,PROGRESS_BASE * (this.getLayerCount()+1),0));
			//

			int maxFit = PROGRESS_BASE * (this.getLayerCount()+1);
			int lastIndex = this.getLastLayerIndex();
			MaterialLayer lastLayer = this.newLayers.get(lastIndex);
			//レイヤーがフルじゃない、なおかつ最後のレイヤーフィットが終わってる時だけ重ねる
			if(!this.isLayerFull() && lastLayer.hasFinishedFitting()){
				this.newLayers.add(MaterialLayer.newLayer(material, maxFit));
			}else{ //それ以外は最後のレイヤーに上書き
				this.newLayers.set(lastIndex, MaterialLayer.newLayer(material, maxFit));
			}

			//			MaterialLayer lastLayer = Iterables.getLast(this.newLayers,MaterialLayer.EMPTY);
			//
			//			if(this.getLayerCount()>=MAX_LAYER_COUNT){ //レイヤーが最大の場合
			//				index = this.getLayerCount() - 1;
			//			}else{//レイヤーがまだ追加できる場合
			//				if(lastLayer.hasFinishedFitting() || this.newLayers.isEmpty()){  //最後のレイヤーがフィット完了もしくはまだ何もない時
			//					this.newLayers.add(MaterialLayer.newLayer(material,PROGRESS_BASE * (this.getLayerCount()+1)));
			//				}else{
			//					int lastIndex = this.newLayers.lastIndexOf(lastLayer);
			//					this.newLayers.set(lastIndex, MaterialLayer.newLayer(material, PROGRESS_BASE * (this.getLayerCount()+1)));
			////					this.newLayers.set(index, element)
			//				}
			//			}


		}


		private boolean isLayerFull(){
			return this.getLayerCount()>=MAX_LAYER_COUNT;
		}
		/** 最後のレイヤーのインデックスを返す。何もない場合0
		 * MAX_LAYER-1より超えないよう調整される*/
		private int getLastLayerIndex(){
			int index = this.newLayers.isEmpty() ? 0 : this.getLayerCount() -1;
			return MathHelper.clamp(index, 0, MAX_LAYER_COUNT-1);
		}


		@Override
		public void catchSyncData(NBTTagCompound nbt) {
			// TODO 自動生成されたメソッド・スタブ
			CAPA.getStorage().readNBT(CAPA, this, null, nbt);
		}

		@Override
		public List<Ability> getAbilities() {
			// TODO 自動生成されたメソッド・スタブ
			return this.layers().stream().flatMap(in -> {
				List<Ability> list = Lists.newArrayList();
				if(in.getMaterial().hasAbilities()){
					list.addAll(in.getMaterial().abilities());
				}
				return list.stream();
			}).collect(Collectors.toList());
		}

		@Override
		public ArmorModifier armorModifier() {
			float melee = (float) this.layers().stream().mapToDouble(in -> in.getMaterial().armorModifier().melee()).sum();
			float magic = (float) this.layers().stream().mapToDouble(in -> in.getMaterial().armorModifier().magic()).sum();
			return this.hasForged() ? new ArmorModifier(melee,magic) : ArmorModifier.ZERO;
		}

		@Override
		public float attackModifier() {
			return (float)this.layers().stream()
					.filter(in -> this.hasForged())
					.filter(in -> !in.isEmptyLayer())
					.mapToDouble(in -> in.getMaterial().attackModifier())
					.sum();
		}

		@Override
		public int costModifier() {
			return this.layers().stream()
					.filter(in -> this.hasForged())
					.filter(in -> !in.isEmptyLayer())
					.mapToInt(in -> in.getMaterial().costModifier())
					.sum();
		}

		@Override
		public @Nonnull MaterialLayer getCurrentFittingLayer() {

			int lastIndex = this.getLastLayerIndex();
			return this.layers().isEmpty() ? MaterialLayer.EMPTY : this.newLayers.get(lastIndex);
		}
//		/** 改造されたレイヤーの一番最後（のインデックス）を返す。
//		 * (鉄・鉄・なし・なし)なら1*/
//		public int getCurrentEmptyLayer(){
//			int index = 0;
//
//
//			for(int i=0;i<this.layers.size();i++){
//				if(!this.layers.get(i).isEmptyLayer()){
//					index = i;
//				}
//			}
//			return index;
//		}

		@Override
		public int durabilityModifier() {
			return this.layers().stream()
					.filter(in -> this.hasForged())
					.filter(in -> !in.isEmptyLayer())
					.mapToInt(in -> in.getMaterial().durabilityModifier())
					.sum();
		}

		@Override
		public float efficiencyModifier() {
			return (float)this.layers().stream()
					.filter(in -> this.hasForged())
					.filter(in -> !in.isEmptyLayer())
					.mapToDouble(in -> in.getMaterial().efficiencyModifier())
					.sum();
		}

		@Override
		public String getIdentifyName() {
			// TODO 自動生成されたメソッド・スタブ
			return ID_SYNC;
		}

		/** 改造されたレイヤー数を返す。*/
		@Override
		public int getLayerCount() {
			return (int) this.layers().stream().filter(in ->!in.isEmptyLayer()).count();
		}

		@Override
		public List<MaterialLayer> layers() {
			// TODO 自動生成されたメソッド・スタブ
			return ImmutableList.copyOf(this.newLayers);
		}

		@Override
		public NBTTagCompound getSendingData() {
			return (NBTTagCompound) CAPA.getStorage().writeNBT(CAPA, this, null);
		}

		@Override
		public int weightModifier() {
			return this.layers().stream()
					.filter(in -> this.hasForged())
					.filter(in -> !in.isEmptyLayer())
					.mapToInt(in -> in.getMaterial().weightModifier())
					.sum();
		}


		@Override
		public boolean hasForged() {
			//			UnsagaMod.logger.trace(this.getClass().getName(), this.getLayerCount());
			return this.getLayerCount() > 0;
		}


		@Override
		public void onPacket(PacketSyncCapability message, MessageContext ctx) {
			// TODO 自動生成されたメソッド・スタブ
			if(ctx.side.isClient()){
				ItemStack is = ClientHelper.getPlayer().getHeldItemMainhand();
				if(!is.isEmpty()){
					ADAPTER.getCapabilityOptional(is).ifPresent(in ->in.catchSyncData(message.getNbt()));
				}

			}
		}


		@Deprecated
		@Override
		public void removeUnfinished() {
//			if(this.layers!=null && !this.layers.isEmpty()){
//				this.layers.removeIf((forge)->!forge.hasFinishedFitting());
//			}

		}


		@Override
		public void setLayerList(List<MaterialLayer> list) {
			this.newLayers = list;
		}


		@Override
		public void updateCurrentFitting(int value) {
			// TODO 自動生成されたメソッド・スタブ
			int lastIndex = this.getLastLayerIndex();
			if(!this.layers().isEmpty()){
				MaterialLayer layer = this.layers().get(lastIndex);
				this.newLayers.set(lastIndex, layer.update(value));
			}

		}






	}
	public static class Storage extends CapabilityStorage<IMinsagaForge>{

		@Override
		public void readNBT(NBTTagCompound comp, Capability<IMinsagaForge> capability, IMinsagaForge instance,
				EnumFacing side) {
			UtilNBT.comp(comp)
			.setToField("forgeAttribute", (in,key)->
			instance.setLayerList(UtilNBT.readListFromNBT(in, key, MaterialLayer::restore)));


		}

		@Override
		public void writeNBT(NBTTagCompound comp, Capability<IMinsagaForge> capability, IMinsagaForge instance,
				EnumFacing side) {
			UtilNBT.comp(comp)
			.writeList("forgeAttribute", instance.layers());

		}

	}

	public static void registerEvents(){
		ADAPTER.registerAttachEvent();
		PacketSyncCapability.registerSyncCapability(ID_SYNC, CAPA);
	}
	/** 最大改造回数*/
	public static final int MAX_LAYER_COUNT = 4;
	public static final String ID_SYNC = "minsaga.forging";
	/**改造を重ねるごとに50*levelする */
	public static final int PROGRESS_BASE = 50;
	public static final int EXP_BASE = 5;

	@CapabilityInject(IMinsagaForge.class)
	public static Capability<IMinsagaForge> CAPA;

	public static CapabilityAdapterFrame<IMinsagaForge> BUILDER = UnsagaMod.CAPA_ADAPTER_FACTORY.create(
			new CapabilityAdapterPlanImpl(()->CAPA,()->IMinsagaForge.class,()->DefaultImpl.class,Storage::new));

	public static ComponentCapabilityAdapters.ItemStack<IMinsagaForge> ADAPTER = BUILDER.createChildItem("minsaga_forge");
	static{
		ADAPTER.setPredicate((ev)->HSLibs.itemStackPredicate(ev, in -> in.getItem().isRepairable()));
		ADAPTER.setRequireSerialize(true);
	}


}
