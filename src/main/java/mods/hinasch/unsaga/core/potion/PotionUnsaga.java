package mods.hinasch.unsaga.core.potion;

import mods.hinasch.lib.client.ClientHelper;
import mods.hinasch.lib.item.PotionUtil;
import mods.hinasch.lib.util.Statics;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.damage.AdditionalDamage;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public abstract class PotionUnsaga extends Potion{


	public static final ResourceLocation TEXTURE = new ResourceLocation(UnsagaMod.MODID,"textures/gui/container/status_effects.png");


	public static PotionUnsaga badPotion(String name,int u,int v){
		return new PotionDebuff(name,u,v);
	}
	public static PotionUnsaga badPotion(String name,int u,int v,IAttribute attribute,String uuid,double value){
		return (PotionUnsaga) new PotionDebuff(name,u,v).registerPotionAttributeModifier(attribute, uuid, value, Statics.OPERATION_INCREMENT);
	}
	public static PotionUnsaga buff(String name,int u,int v){
		return new PotionBuff(name,u,v);
	}
//	String name;
	ResourceLocation key;
//	PotionType type;
	boolean hasStatusIcon = true;
	protected PotionUnsaga(String name,boolean isBadEffectIn, int liquidColorIn,int u,int v) {
		super(isBadEffectIn, 250);
		this.setIconIndex(u, v);
		//		this.setRegistryName(new ResourceLocation(UnsagaMod.MODID,"potion."+name));
		this.setPotionName("unsaga.potion."+name);
		this.key = new ResourceLocation(UnsagaMod.MODID,name);
		this.setRegistryName(this.key);
	}
	public void affectOnAttacked(LivingDamageEvent e, AdditionalDamage data, int amplifier){}
	public void onHurt(LivingHurtEvent e,int amplifier){
	}


	public PotionEffect createEffect(int time,int amp){
		return new PotionEffect(this,time,amp);
	}

	public PotionEffect getPotionEffect(EntityLivingBase living){
		return PotionUtil.getEffect(living, this);
	}
	@Override
	public int getStatusIconIndex() {
		ClientHelper.bindTextureToTextureManager(TEXTURE);
		return super.getStatusIconIndex();
	}

	@Override
	public boolean hasStatusIcon() {
		return this.hasStatusIcon;
	}

	@Override
	public boolean isInstant() {
		return false;
	}

	@Override
	public boolean isReady(int duration, int amplifier) {
		return true;
	}


	public abstract void onDamage(LivingDamageEvent e, AdditionalDamage data, int amplifier);

	public abstract void onTick(World world,EntityLivingBase living,int amp);

	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int p_76394_2_)
	{
		super.performEffect(entityLivingBaseIn, p_76394_2_);
		this.onTick(entityLivingBaseIn.getEntityWorld(), entityLivingBaseIn, p_76394_2_);
	};

}
