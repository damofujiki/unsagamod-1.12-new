package mods.hinasch.unsaga.damage;

import java.util.Collection;
import java.util.EnumSet;

import com.google.common.collect.Sets;

import mods.hinasch.unsaga.ability.specialmove.TechInvoker;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.General;
import mods.hinasch.unsaga.damage.DamageTypeUnsaga.Sub;
import mods.hinasch.unsaga.lp.LPAttribute;
import mods.hinasch.unsagamagic.spell.action.SpellCastIgniter;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

/**
 *
 * このmod用の追加ダメージデータ。LP攻撃力と元のダメージソース元の参照を持つ
 *
 * @param <T>
 */
public class AdditionalDamage<T> extends AdditionalDamageTypes{

	private LPAttribute lpStrength;
	private final DamageSource from;

	public AdditionalDamage(DamageSource from,General general,float lpstr,int numAttack){
		super(general);
		this.from = from;
		this.lpStrength = new LPAttribute(lpstr,numAttack);
	}
	public AdditionalDamage(DamageSource from,float lpstr,int numAttack,General... generals){
		super(generals);
		this.from = from;
		this.lpStrength = new LPAttribute(lpstr,numAttack);
	}
	public AdditionalDamage(DamageSource from,LPAttribute lpd,General... generals){
		super(generals);
		this.from = from;
		this.lpStrength = lpd;
	}
	public AdditionalDamage(DamageSource from,float lpstr,int numAttack,Collection<General> generals){
		this(from, lpstr, numAttack, generals.toArray(new General[generals.size()]));

	}
	public AdditionalDamage(DamageSource from,LPAttribute lp,Collection<General> generals){
		this(from, lp, generals.toArray(new General[generals.size()]));

	}
	public AdditionalDamage(TechInvoker invoker,General... generals){
		this(getSource(invoker.getPerformer()), invoker.getStrength().lp().amount(),invoker.getStrength().lp().chances(),generals);
	}

	public AdditionalDamage(SpellCastIgniter invoker,General... generals){
		this(getSource(invoker.getPerformer()), invoker.getStrength().lp().amount(),invoker.getStrength().lp().chances(),generals);
	}
	public AdditionalDamage(DamageSource from,General general,float lpstr){
		this(from, general, lpstr, 1);
	}
	public DamageSource getParent(){
		return this.from;
	}


	public static DamageSource getSource(EntityLivingBase living){
		if(living instanceof EntityPlayer){
			return DamageSource.causePlayerDamage((EntityPlayer) living);
		}
		return DamageSource.causeMobDamage(living);
	}

	public AdditionalDamage setSubTypes(Sub... subs){
		this.subType = EnumSet.copyOf(Sets.newHashSet(subs));
		return this;
	}

	public void setLP(LPAttribute lp){
		this.lpStrength = lp;
	}

	public AdditionalDamage setSubTypes(EnumSet<Sub> subs){
		this.subType = subs;
		return this;
	}

	public LPAttribute getLPAttribute(){
		return this.lpStrength;
	}


	public static DamageSource register(AdditionalDamage data){
		return DamageHelper.register(data);
	}
}
