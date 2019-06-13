package mods.hinasch.unsaga.ability.specialmove.action;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;

public class ComboActionProperty {
	public static final Element DEFAULT_ELEMENT = new Element(false,EnumParticleTypes.SLIME,SoundEvents.ENTITY_IRONGOLEM_HURT,0.5F,35,Blocks.DIRT);
	final NonNullList<Element> elements;
	final int maxCount;

	public static final ComboActionProperty TRIPLE_SUPREMACY = new TripleSupremacy();
	public static final ComboActionProperty BLOODY_MARY = new BloodyMary();
	public static final ComboActionProperty TWIN_BLADE = new TwinBlade();
	public static final ComboActionProperty SCATTERED_PETALS = new ScatteredPetals();

	public ComboActionProperty(int maxCount){
		this.maxCount = maxCount;
		this.elements = NonNullList.withSize(maxCount, DEFAULT_ELEMENT);
	}

	public int getMaxCount(){
		return this.maxCount;
	}

	/** maxCount-1*/
	public Element getElement(int num){
		if(num<this.maxCount){
			return this.elements.get(num);
		}
		return DEFAULT_ELEMENT;
	}

	public static class Element{
		final boolean isRequireSprint;
		final EnumParticleTypes particle;
		final SoundEvent sound;
		final float pitch;
		final int dens;
		final Block block;
		public Element(boolean isRequireSprint,EnumParticleTypes particle,SoundEvent ev,float pitch,int density,Block block){
			this.isRequireSprint = isRequireSprint;
			this.particle = particle;
			if(ev!=null){
				this.sound = ev;
			}else{
				this.sound = SoundEvents.ENTITY_IRONGOLEM_HURT;
			}
			this.block = block;
			this.pitch = pitch;
			this.dens = density;
		}

		public Element(EnumParticleTypes particle,float pitch){
			this(false, particle, pitch);
		}

		public Element(boolean isSprint,EnumParticleTypes particle,float pitch){
			this(isSprint, particle, SoundEvents.ENTITY_IRONGOLEM_HURT, pitch, 35,Blocks.DIRT);
		}

		public Element(boolean isSprint,EnumParticleTypes particle,float pitch,int dens){
			this(isSprint, particle, SoundEvents.ENTITY_IRONGOLEM_HURT, pitch, dens,Blocks.DIRT);
		}
		public Element(boolean isSprint,EnumParticleTypes particle,float pitch,int dens,Block block){
			this(isSprint, particle, SoundEvents.ENTITY_IRONGOLEM_HURT, pitch, dens,block);
		}

		public int getParticleDensity(){
			return this.dens;
		}
		public float getSoundPitch(){
			return this.pitch;
		}

		public SoundEvent getHitSound(){
			return this.sound;
		}
		public boolean isRequireSprint(){
			return this.isRequireSprint;
		}

		public Block getParticleBlock(){
			return this.block;
		}
		public EnumParticleTypes getHitParticle(){
			return this.particle;
		}
	}

	public static class TripleSupremacy extends ComboActionProperty{

		public TripleSupremacy() {
			super(3);
			this.elements.set(0, new Element(EnumParticleTypes.CRIT,0.5F));
			this.elements.set(1, new Element(EnumParticleTypes.CRIT,0.5F));
			this.elements.set(2, new Element(true,EnumParticleTypes.CRIT,1.0F,50));
		}

	}
	public static class ScatteredPetals extends ComboActionProperty{

		public ScatteredPetals() {
			super(3);
			this.elements.set(0, new Element(false,EnumParticleTypes.BLOCK_CRACK,1.0F,35,Blocks.ICE));
			this.elements.set(1, new Element(false,EnumParticleTypes.CRIT,1.0F,35));
			this.elements.set(2, new Element(false,EnumParticleTypes.BLOCK_CRACK,1.0F,35,Blocks.RED_FLOWER));

		}

	}
	public static class TwinBlade extends ComboActionProperty{

		public TwinBlade() {
			super(2);
			this.elements.set(0, new Element(EnumParticleTypes.CRIT,0.5F));
			this.elements.set(1, new Element(EnumParticleTypes.CRIT,1.0F));
		}

	}
	public static class BloodyMary extends ComboActionProperty{

		public BloodyMary() {
			super(5);
			this.elements.set(0, new Element(EnumParticleTypes.REDSTONE,0.5F));
			this.elements.set(1, new Element(EnumParticleTypes.REDSTONE,0.5F));
			this.elements.set(2, new Element(EnumParticleTypes.REDSTONE,0.5F));
			this.elements.set(3, new Element(EnumParticleTypes.REDSTONE,0.5F));
			this.elements.set(4, new Element(false,EnumParticleTypes.REDSTONE,1.0F,50));
		}

	}
}
