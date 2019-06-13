package mods.hinasch.unsaga.core.entity;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.collect.Sets;
import com.mojang.realmsclient.util.Pair;

import mods.hinasch.lib.registry.PropertyElementWithID;
import mods.hinasch.lib.registry.PropertyRegistry;
import mods.hinasch.lib.util.HSLibs;
import mods.hinasch.unsaga.UnsagaMod;
import mods.hinasch.unsaga.core.entity.mob.EntityCastableZombie;
import mods.hinasch.unsaga.core.entity.mob.EntityGelatinous;
import mods.hinasch.unsaga.core.entity.mob.EntityPoisonEater;
import mods.hinasch.unsaga.core.entity.mob.EntityRuffleTree;
import mods.hinasch.unsaga.core.entity.mob.EntityShadowServant;
import mods.hinasch.unsaga.core.entity.mob.EntitySignalTree;
import mods.hinasch.unsaga.core.entity.mob.EntityStormEater;
import mods.hinasch.unsaga.core.entity.mob.EntitySwarm;
import mods.hinasch.unsaga.core.entity.mob.EntityTreasureSlimeNew;
import mods.hinasch.unsaga.core.entity.passive.EntityBeam;
import mods.hinasch.unsaga.core.entity.passive.EntityEffectSpawner;
import mods.hinasch.unsaga.core.entity.passive.EntityShadowClone;
import mods.hinasch.unsaga.core.entity.passive.EntitySnowFall;
import mods.hinasch.unsaga.core.entity.passive.EntityUnsagaChest;
import mods.hinasch.unsaga.core.entity.projectile.EntityBullet;
import mods.hinasch.unsaga.core.entity.projectile.EntityCustomArrow;
import mods.hinasch.unsaga.core.entity.projectile.EntityFlyingAxe;
import mods.hinasch.unsaga.core.entity.projectile.EntityIceNeedle;
import mods.hinasch.unsaga.core.entity.projectile.EntityJavelin;
import mods.hinasch.unsaga.core.entity.projectile.EntityMagicBall;
import mods.hinasch.unsaga.core.entity.projectile.EntitySolutionLiquid;
import mods.hinasch.unsaga.core.entity.projectile.EntityThrowingKnife;
import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew;
import mods.hinasch.unsaga.init.UnsagaConfigHandlerNew.Enemy;
import net.minecraft.block.material.MapColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityEntryBuilder;
import net.minecraftforge.registries.IForgeRegistry;

public class UnsagaEntityRegistry extends PropertyRegistry<UnsagaEntityRegistry.EntityProperty>{

	private static UnsagaEntityRegistry INSTANCE;

	public static UnsagaEntityRegistry instance(){
		if(INSTANCE == null){
			INSTANCE = new UnsagaEntityRegistry();

		}
		return INSTANCE;
	}
//	public Property flyingAxe = new Property(1,"flyingAxe",EntityFlyingAxe.class);
	public static final EntityProperty FLYING_AXE = new EntityProperty(10,"flyingAxeNew",EntityFlyingAxe.class).setUpdateFreq(3);
//	public Property throwingKnife = new Property(2,"throwingKnife",EntityThrowingKnife.class);
	public static final EntityProperty THROWING_KNIFE = new EntityProperty(2,"throwingKnifeNew",EntityThrowingKnife.class);
//	public Property boulder = new Property(3,"boulder",EntityBoulder.class);
//	public Property bubbleBlow = new Property(4,"bubbleBlow",EntityBubbleBlow.class);
//	public Property shadow = new Property(5,"shadow",EntityShadow.class);
	public static final EntityProperty ARROW = new EntityProperty(3,"arrow",EntityCustomArrow.class);
	public static final EntityProperty TREASURE_BOX = new SpawnableProperty(4,"chest",EntityUnsagaChest.class).setEggColor(0x5b3e22, 0);
	public static final EntityProperty TREASURE_SLIME = new SpawnableProperty(5,"treasureSlime",EntityTreasureSlimeNew.class).setEggColor(0xdb2266, 0);
	public static final EntityProperty LIQUID_BALL = new EntityProperty(6,"liquidBall",EntitySolutionLiquid.class);
	public static final MonsterProperty RUFFLE_TREE = (MonsterProperty) new MonsterProperty(7,"ruffleTree",EntityRuffleTree.class).setEggColor(0x967f5e, 0);
	public static final EntityProperty BULLET = new EntityProperty(8,"bullet",EntityBullet.class);
	public static final EntityProperty BEAM = new EntityProperty(9,"beam",EntityBeam.class);
//	public Property blaster = new Property(13,"blaster",EntityBlaster.class);
//	public Property fireWall = new Property(10,"fireWall",EntityFireWall.class);
	public static final MonsterProperty SIGNAL_TREE = (MonsterProperty) new MonsterProperty(11,"signalTree",EntitySignalTree.class).setUpdateFreq(3).setEggColor(0x353dad, 0xd11919);
//	public Property iceNeedle = new Property(16,"iceNeedle",EntityIceNeedle.class);
//	public Property fireArrow = new Property(17,"fireArrow",EntityFireArrow.class);
	public static final MonsterProperty STORM_EATER = (MonsterProperty) new MonsterProperty(12,"stormEater",EntityStormEater.class).setUpdateFreq(3).setEggColor(0x6c6f9d, 0x999999);
	public static final MonsterProperty POISON_EATER = (MonsterProperty) new MonsterProperty(13,"poisonEater",EntityPoisonEater.class).setUpdateFreq(3).setEggColor(0xad134e, 0x0a2375);
	public static final EntityProperty VOID = new EntityProperty(14,"void",EntityEffectSpawner.class);
	public static final EntityProperty MAGIC_BALL = new EntityProperty(15,"magicBall",EntityMagicBall.class).setUpdateFreq(2);
	public static final EntityProperty ICE_NEEDLE = new EntityProperty(16,"iceNeedleNew",EntityIceNeedle.class).setUpdateFreq(2);
	public static final EntityProperty SHADOW_SERVANT = new EntityProperty(17,"shadowServant",EntityShadowServant.class);
	public static final EntityProperty JAVELIN = new EntityProperty(18,"javelin",EntityJavelin.class);
	public static final EntityProperty GHOST = new EntityProperty(19,"ghost",EntityShadowClone.class).setUpdateFreq(2);
	public static final EntityProperty SNOWFALL = new EntityProperty(20,"snowfall",EntitySnowFall.class);
	public static final MonsterProperty REVENANT = (MonsterProperty) new MonsterProperty(21,"revenant",EntityCastableZombie.Revenant.class).setUpdateFreq(3).setEggColor(0x222222, MapColor.BROWN.colorValue);
	public static final MonsterProperty GELATINOUS_MATTER = (MonsterProperty) new MonsterProperty(22,"gelatinousMatter",EntityGelatinous.GelatinousMatter.class).setUpdateFreq(3).setEggColor(0xb6b0e0,0xab5fe0);
	public static final MonsterProperty GOLDENBAUM = (MonsterProperty) new MonsterProperty(23,"goldenbaum",EntityGelatinous.GoldenBaum.class).setUpdateFreq(3).setEggColor(0xdec650, MapColor.RED.colorValue);
	public static final EntityProperty SWARM = (MonsterProperty) new MonsterProperty(24,"swarm",EntitySwarm.class).setUpdateFreq(3).setEggColor(0xdec650, MapColor.GRAY.colorValue);


	public static class EntityProperty extends PropertyElementWithID{

		EntityEntry entry;

		public Class getEntityClass() {
			return entityClass;
		}

		public EntityProperty setEntityClass(Class entityClass) {
			this.entityClass = entityClass;
			return this;
		}

		public boolean isSendsVelocityUpdates() {
			return sendsVelocityUpdates;
		}

		public EntityProperty setSendsVelocityUpdates(boolean sendsVelocityUpdates) {
			this.sendsVelocityUpdates = sendsVelocityUpdates;
			return this;
		}

		public int getUpdateFreq() {
			return updateFreq;
		}

		public EntityProperty setUpdateFreq(int updateFreq) {
			this.updateFreq = updateFreq;
			return this;
		}

		public int getTrackingRange() {
			return trackingRange;
		}

		public EntityProperty setTrackingRange(int trackingRange) {
			this.trackingRange = trackingRange;
			return this;
		}

		public Class entityClass;
		public boolean sendsVelocityUpdates = true;
		public int updateFreq = 5;
		public int trackingRange = 128;

		public EntityProperty(int id,String name,Class<? extends Entity> clazz){
			super(new ResourceLocation(UnsagaMod.MODID,name),name,id);
			this.entityClass = clazz;
			//			UnsagaEntityRegistry.map.putObject(new ResourceLocation(name), this);
		}



		@Override
		public Class getParentClass() {
			// TODO 自動生成されたメソッド・スタブ
			return this.getClass();
		}


		public EntityEntry createEntry(){
			this.entry = EntityEntryBuilder.create()
			.id(getKey(), this.getId())
			.entity(getEntityClass())
			.name(this.getPropertyName())
			.tracker(this.trackingRange, this.updateFreq, this.sendsVelocityUpdates)
			.build();
			return this.entry;
		}

		public EntityEntry getEntityEntry(){
			return this.entry;
		}

		public EntityProperty setEggColor(int c1,int c2){
			return this;
		}
	}

	public static class SpawnableProperty extends EntityProperty{

		public Pair<Integer,Integer> eggColor = Pair.of(0, MapColor.WHITE_STAINED_HARDENED_CLAY.colorValue);

		public SpawnableProperty(int id, String name, Class<? extends Entity> clazz) {
			super(id, name, clazz);
			// TODO 自動生成されたコンストラクター・スタブ
		}

		@Override
		public EntityEntry createEntry(){
			this.entry = EntityEntryBuilder.create()
			.id(getKey(), this.getId())
			.entity(getEntityClass())
			.name(this.getPropertyName())
			.egg(eggColor.first(),eggColor.second())
			.tracker(this.trackingRange, this.updateFreq, this.sendsVelocityUpdates)
			.build();
			return this.entry;
		}

		@Override
		public EntityProperty setEggColor(int c1,int c2){
			this.eggColor = Pair.of(c1,c2);
			return this;
		}
	}
	public static class MonsterProperty extends SpawnableProperty{

		int spawnWeight = 1;
		Pair<Integer,Integer> spawnAmount = Pair.of(1, 1);
		Set<BiomeDictionary.Type> spawnBiomes = new HashSet<>();

		public MonsterProperty(int id, String name, Class<? extends Entity> clazz) {
			super(id, name, clazz);
			// TODO 自動生成されたコンストラクター・スタブ
		}

		public MonsterProperty setSpawn(int w,int min,int max){
			this.spawnWeight = w;
			this.spawnAmount = Pair.of(min,max);
			return this;
		}

		public MonsterProperty setSpawnBiomeTypes(BiomeDictionary.Type... types){
			this.spawnBiomes = Sets.newHashSet(types);
			return this;
		}

		@Override
		public EntityEntry createEntry(){
			Set<Biome> biomes = spawnBiomes.stream()
					.flatMap(in ->HSLibs.getBiomesFromType(in).stream()).collect(Collectors.toSet());
			if(this.spawnWeight>0){
				this.entry = EntityEntryBuilder.create()
						.id(getKey(), this.getId())
						.entity(getEntityClass())
						.name(this.getPropertyName())
						.egg(eggColor.first(),eggColor.second())
						.tracker(this.trackingRange, this.updateFreq, this.sendsVelocityUpdates)
						.spawn(EnumCreatureType.MONSTER, spawnWeight, spawnAmount.first(), spawnAmount.second(), biomes)
						.build();
						return this.getEntityEntry();
			}else{
				this.entry = EntityEntryBuilder.create()
						.id(getKey(), this.getId())
						.entity(getEntityClass())
						.name(this.getPropertyName())
						.egg(eggColor.first(),eggColor.second())
						.tracker(this.trackingRange, this.updateFreq, this.sendsVelocityUpdates)
						.build();
						return this.getEntityEntry();
			}

		}
	}

	@Override
	public void init() {

	}

	@Override
	public void preInit() {
		// TODO 自動生成されたメソッド・スタブ
		this.registerObjects();

//		for(UnsagaEntityRegistry.EntityProperty prop:this.getProperties()){
//			EntityRegistry.registerModEntity(prop.getKey(),prop.getEntityClass(), prop.getPropertyName(), prop.getId(), UnsagaMod.instance, prop.getTrackingRange(), prop.getUpdateFreq(), prop.isSendsVelocityUpdates());
//		}
		this.initSpawn();
		HSLibs.registerEvent(new RegisteringHandler());
	}

	private void initSpawn(){
		Enemy config = UnsagaConfigHandlerNew.ENEMY;
		int dens = config.spawnBase;
		SIGNAL_TREE.setSpawn(dens*config.spawnSignalTree, 1, 3).setSpawnBiomeTypes(BiomeDictionary.Type.JUNGLE,BiomeDictionary.Type.FOREST);
		RUFFLE_TREE.setSpawn(dens*config.spawnRuffleTree, 1, 1).setSpawnBiomeTypes(BiomeDictionary.Type.JUNGLE,BiomeDictionary.Type.FOREST);
		STORM_EATER.setSpawn(dens*config.spawnStormEater, 1, 3).setSpawnBiomeTypes(BiomeDictionary.Type.HILLS,BiomeDictionary.Type.MOUNTAIN);
		POISON_EATER.setSpawn(dens*config.spawnPoisonEater, 1, 3).setSpawnBiomeTypes(BiomeDictionary.Type.SWAMP,BiomeDictionary.Type.SPOOKY,BiomeDictionary.Type.MESA);
		REVENANT.setSpawn(dens*config.spawnRevenant, 1, 2).setSpawnBiomeTypes(BiomeDictionary.Type.PLAINS,BiomeDictionary.Type.FOREST);
		GELATINOUS_MATTER.setSpawn(dens*3, 1, 3).setSpawnBiomeTypes(BiomeDictionary.Type.SWAMP,BiomeDictionary.Type.WET);
		GOLDENBAUM.setSpawn(dens*3, 1, 3).setSpawnBiomeTypes(BiomeDictionary.Type.SWAMP,BiomeDictionary.Type.WET,BiomeDictionary.Type.MESA);
//		List<Biome> forests = Lists.newArrayList();
//		Biome.REGISTRY.forEach(in ->{
//			List<BiomeDictionary.Type> types = Lists.newArrayList(BiomeDictionary.getTypes(in));
//			if(types.contains(BiomeDictionary.Type.FOREST) || types.contains(BiomeDictionary.Type.JUNGLE)){
//				if(!types.contains(BiomeDictionary.Type.DRY)){
//					forests.add(in);
//				}
//
//			}
//		});
//
//		Biome[] biomes = forests.toArray(new Biome[forests.size()]);
//
//		List<Biome> hills = Lists.newArrayList();
//		Biome.REGISTRY.forEach(in ->{
//			List<BiomeDictionary.Type> types = Lists.newArrayList(BiomeDictionary.getTypes(in));
//			if(types.contains(BiomeDictionary.Type.MOUNTAIN) || types.contains(BiomeDictionary.Type.OCEAN)){
//				hills.add(in);
//			}
//		});
//
//		Biome[] biomes2 = hills.toArray(new Biome[hills.size()]);
//		int dens = UnsagaMod.configs.getDensityMonsterSpawn();
//		dens = MathHelper.clamp(dens, 1, 100);
//		if(biomes.length>0){
//			EntityRegistry.addSpawn(EntitySignalTree.class, dens*4, 1, 3, EnumCreatureType.MONSTER, biomes);
//			EntityRegistry.addSpawn(EntityRuffleTree.class, dens, 1, 1, EnumCreatureType.MONSTER, biomes);
//		}
//		if(biomes2.length>0){
//			EntityRegistry.addSpawn(EntityStormEater.class, dens*3, 1, 3, EnumCreatureType.MONSTER, biomes2);
//			EntityRegistry.addSpawn(EntityPoisonEater.class, dens*3, 1, 3, EnumCreatureType.MONSTER, biomes2);
//		}

//		Biome.REGISTRY.forEach(in ->{
//			EntityRegistry.addSpawn(EntityUnsagaChestNew.class, 50, 1, 3, EnumCreatureType.CREATURE, in);
//		});
//		EntityRegistry.addSpawn(EntityStormEater.class, 40, 1, 3, EnumCreatureType.MONSTER, biomes);
//		EntityRegistry.addSpawn(EntityPoisonEater.class, 40, 1, 3, EnumCreatureType.MONSTER, biomes);



//		EntityRegistry.registerEgg(this.TREASURE_BOX.getKey(), 0x5b3e22, 0);
//		EntityRegistry.registerEgg(this.TREASURE_SLIME.getKey(), 0xdb2266, 0);
//
//		EntityRegistry.registerEgg(this.RUFFLE_TREE.getKey(), 0x967f5e, 0);
//		EntityRegistry.registerEgg(this.SIGNAL_TREE.getKey(), 0x353dad, 0xd11919);
//		EntityRegistry.registerEgg(this.STORM_EATER.getKey(), 0x6c6f9d, 0x999999);
//		EntityRegistry.registerEgg(this.POISON_EATER.getKey(), 0xad134e, 0x0a2375);

	}

	@Override
	protected void registerObjects() {
//		this.put(boulder);
//		this.put(bubbleBlow);
//		this.put(shadow);
//		this.put(flyingAxe);
//		this.put(throwingKnife);
		this.put(THROWING_KNIFE);
		this.put(ARROW);
		this.put(TREASURE_BOX);
		this.put(TREASURE_SLIME);
		this.put(LIQUID_BALL);
		this.put(RUFFLE_TREE);
		this.put(BULLET);
		this.put(BEAM);
//		this.put(blaster);
//		this.put(fireWall);
		this.put(SIGNAL_TREE);
//		this.put(iceNeedle);
//		this.put(fireArrow);
		this.put(STORM_EATER);
		this.put(POISON_EATER);
		this.put(FLYING_AXE);
		this.put(VOID);
		this.put(MAGIC_BALL);
		this.put(ICE_NEEDLE);
		this.put(SHADOW_SERVANT);
		this.put(JAVELIN);
		this.put(GHOST);
		this.put(SNOWFALL);
		this.put(REVENANT);
		this.put(GOLDENBAUM);
		this.put(GELATINOUS_MATTER);
		this.put(SWARM);
	}

	public static class RegisteringHandler{

		@SubscribeEvent
		public void onRegisterEntity(RegistryEvent.Register<EntityEntry> ev){

			IForgeRegistry<EntityEntry> reg = ev.getRegistry();

			UnsagaEntityRegistry.instance().getProperties().stream()
			.forEach(in ->{
				in.createEntry();
				reg.register(in.getEntityEntry());
			});
		}
	}
}
