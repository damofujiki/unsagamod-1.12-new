 package mods.hinasch.unsaga.skillpanel;

import java.util.Collection;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.base.Preconditions;
import com.mojang.realmsclient.util.Pair;

import mods.hinasch.unsaga.core.item.misc.skillpanel.ISkillPanelContainer;
import mods.hinasch.unsaga.core.item.misc.skillpanel.SkillPanelCapability;
import mods.hinasch.unsaga.core.world.UnsagaWorldCapability;
import mods.hinasch.unsaga.minsaga.classes.PlayerClassEntry;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class SkillPanelAPI {


	public static NonNullList<ItemStack> getPanelStacks(EntityPlayer ep){
		Preconditions.checkNotNull(UnsagaWorldCapability.ADAPTER.getCapability(ep.getEntityWorld()));
		return UnsagaWorldCapability.ADAPTER.getCapabilityOptional(ep.getEntityWorld())
				.map(in ->in.skillPanelStorage().getPanels(ep.getGameProfile().getId()))
				.orElse(GrowthPanelStorage.makeEmptyList());
	}

	public static Collection<Pair<ISkillPanel,Integer>> getPanels(EntityPlayer ep){
		return getPanelStacks(ep).stream().filter(in -> SkillPanelCapability.adapter.hasCapability(in))
				.map(in -> SkillPanelCapability.adapter.getCapability(in))
				.map(in -> Pair.of(in.panel(),in.level()))
				.collect(Collectors.toList());
	}
	public static Collection<ISkillPanel> getRegisteredPanels(Predicate<ISkillPanel> filter){
		return SkillPanelInitializer.getAllPanels().stream().filter(filter).collect(Collectors.toList());
	}

	public static Collection<ISkillPanelContainer> getPlayerPanels(EntityPlayer ep,Predicate<ISkillPanelContainer> filter){
		return getPanelStacks(ep).stream().filter(in -> !in.isEmpty()).map(in -> SkillPanelCapability.adapter.getCapability(in)).filter(filter).collect(Collectors.toList());
	}
	public static OptionalInt getLevel(ItemStack panel){
		return SkillPanelCapability.adapter.getCapabilityOptional(panel)
		.map(in -> OptionalInt.of(in.level()))
		.orElse(OptionalInt.empty());
	}

	public static Optional<ISkillPanel> getSkillPanel(ItemStack panel){
		return SkillPanelCapability.adapter.getCapabilityOptional(panel)
		.map(in -> Optional.of(in.panel()))
		.orElse(Optional.empty());
	}


	public static boolean hasFamiliar(EntityPlayer ep){
		return getPlayerPanels(ep,in -> true).stream()
				.anyMatch(in -> in.panel() instanceof SkillPanelFamiliar);
	}
	public static boolean hasPanel(EntityLivingBase ep,ISkillPanel panel,boolean ignoreClass){
		if(ep instanceof EntityPlayer){
			return getHighestPanelLevel((EntityPlayer) ep,panel,ignoreClass).isPresent();
		}
		return false;
	}

	public static boolean hasPanel(EntityLivingBase ep,ISkillPanel panel){
		return hasPanel(ep, panel,false);
	}

	public static Optional<Pair<ISkillPanel,Integer>> getPanelOptional(EntityLivingBase ep,ISkillPanel panel){
		if(hasPanel(ep,panel)){
			return Optional.of(Pair.of(panel,getHighestPanelLevel(ep,panel).getAsInt()));
		}
		return Optional.empty();
	}
	/** レベル(1-5)なので注意*/
	public static OptionalInt getHighestPanelLevel(EntityLivingBase ep,ISkillPanel panel){
		return getHighestPanelLevel(ep, panel, false);
	}

	/** レベル(1-5)なので注意*/
	public static OptionalInt getHighestPanelLevel(EntityLivingBase living,ISkillPanel panel,boolean ignoreClass){
		if(!(living instanceof EntityPlayer)){
			return OptionalInt.empty();
		}

		EntityPlayer ep = (EntityPlayer) living;
		return UnsagaWorldCapability.ADAPTER.getCapabilityOptional(ep.getEntityWorld())
		.map(cap ->{

			Stream<Pair<ISkillPanel,Integer>> normalPanels = getPanels(ep).stream();
			Function<Stream<Pair<ISkillPanel,Integer>>,OptionalInt> maxLevelFinder =
					str -> str.filter(in -> in.first()==panel)
					.mapToInt(in -> in.second())
					.max();

			if(ignoreClass){
				return maxLevelFinder.apply(normalPanels);
			}else{
				PlayerClassEntry entry = cap.playerClassStorage().getEntry(ep);
				Stream<Pair<ISkillPanel,Integer>> conbined =
						Stream.concat(normalPanels
								,entry.playerClass().classSkillsWithLevel(entry.level()).stream());
				return maxLevelFinder.apply(conbined);

			}
		}).orElse(OptionalInt.empty());

	}
}
