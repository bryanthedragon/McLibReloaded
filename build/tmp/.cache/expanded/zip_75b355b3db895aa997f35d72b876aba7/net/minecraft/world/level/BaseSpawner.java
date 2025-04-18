package net.minecraft.world.level;

import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public abstract class BaseSpawner {
    public static final String SPAWN_DATA_TAG = "SpawnData";
    private static final int EVENT_SPAWN = 1;
    private static final int DEFAULT_SPAWN_DELAY = 20;
    private static final int DEFAULT_MIN_SPAWN_DELAY = 200;
    private static final int DEFAULT_MAX_SPAWN_DELAY = 800;
    private static final int DEFAULT_SPAWN_COUNT = 4;
    private static final int DEFAULT_MAX_NEARBY_ENTITIES = 6;
    private static final int DEFAULT_REQUIRED_PLAYER_RANGE = 16;
    private static final int DEFAULT_SPAWN_RANGE = 4;
    private int spawnDelay = 20;
    private WeightedList<SpawnData> spawnPotentials = WeightedList.of();
    @Nullable
    private SpawnData nextSpawnData;
    private double spin;
    private double oSpin;
    private int minSpawnDelay = 200;
    private int maxSpawnDelay = 800;
    private int spawnCount = 4;
    @Nullable
    private Entity displayEntity;
    private int maxNearbyEntities = 6;
    private int requiredPlayerRange = 16;
    private int spawnRange = 4;

    public void setEntityId(EntityType<?> p_253682_, @Nullable Level p_254041_, RandomSource p_254221_, BlockPos p_254050_) {
        this.getOrCreateNextSpawnData(p_254041_, p_254221_, p_254050_).getEntityToSpawn().putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(p_253682_).toString());
    }

    private boolean isNearPlayer(Level p_151344_, BlockPos p_151345_) {
        return p_151344_.hasNearbyAlivePlayer(p_151345_.getX() + 0.5, p_151345_.getY() + 0.5, p_151345_.getZ() + 0.5, this.requiredPlayerRange);
    }

    public void clientTick(Level p_151320_, BlockPos p_151321_) {
        if (!this.isNearPlayer(p_151320_, p_151321_)) {
            this.oSpin = this.spin;
        } else if (this.displayEntity != null) {
            RandomSource randomsource = p_151320_.getRandom();
            double d0 = p_151321_.getX() + randomsource.nextDouble();
            double d1 = p_151321_.getY() + randomsource.nextDouble();
            double d2 = p_151321_.getZ() + randomsource.nextDouble();
            p_151320_.addParticle(ParticleTypes.SMOKE, d0, d1, d2, 0.0, 0.0, 0.0);
            p_151320_.addParticle(ParticleTypes.FLAME, d0, d1, d2, 0.0, 0.0, 0.0);
            if (this.spawnDelay > 0) {
                this.spawnDelay--;
            }

            this.oSpin = this.spin;
            this.spin = (this.spin + 1000.0F / (this.spawnDelay + 200.0F)) % 360.0;
        }
    }

    public void serverTick(ServerLevel p_151312_, BlockPos p_151313_) {
        if (this.isNearPlayer(p_151312_, p_151313_)) {
            if (this.spawnDelay == -1) {
                this.delay(p_151312_, p_151313_);
            }

            if (this.spawnDelay > 0) {
                this.spawnDelay--;
            } else {
                boolean flag = false;
                RandomSource randomsource = p_151312_.getRandom();
                SpawnData spawndata = this.getOrCreateNextSpawnData(p_151312_, randomsource, p_151313_);

                for (int i = 0; i < this.spawnCount; i++) {
                    CompoundTag compoundtag = spawndata.getEntityToSpawn();
                    Optional<EntityType<?>> optional = EntityType.by(compoundtag);
                    if (optional.isEmpty()) {
                        this.delay(p_151312_, p_151313_);
                        return;
                    }

                    Vec3 vec3 = compoundtag.read("Pos", Vec3.CODEC)
                        .orElseGet(
                            () -> new Vec3(
                                p_151313_.getX() + (randomsource.nextDouble() - randomsource.nextDouble()) * this.spawnRange + 0.5,
                                p_151313_.getY() + randomsource.nextInt(3) - 1,
                                p_151313_.getZ() + (randomsource.nextDouble() - randomsource.nextDouble()) * this.spawnRange + 0.5
                            )
                        );
                    if (p_151312_.noCollision(optional.get().getSpawnAABB(vec3.x, vec3.y, vec3.z))) {
                        BlockPos blockpos = BlockPos.containing(vec3);
                        if (spawndata.getCustomSpawnRules().isPresent()) {
                            if (!optional.get().getCategory().isFriendly() && p_151312_.getDifficulty() == Difficulty.PEACEFUL) {
                                continue;
                            }

                            SpawnData.CustomSpawnRules spawndata$customspawnrules = spawndata.getCustomSpawnRules().get();
                            if (!spawndata$customspawnrules.isValidPosition(blockpos, p_151312_)) {
                                continue;
                            }
                        } else if (!SpawnPlacements.checkSpawnRules(optional.get(), p_151312_, EntitySpawnReason.SPAWNER, blockpos, p_151312_.getRandom())) {
                            continue;
                        }

                        Entity entity = EntityType.loadEntityRecursive(compoundtag, p_151312_, EntitySpawnReason.SPAWNER, p_390874_ -> {
                            p_390874_.snapTo(vec3.x, vec3.y, vec3.z, p_390874_.getYRot(), p_390874_.getXRot());
                            return p_390874_;
                        });
                        if (entity == null) {
                            this.delay(p_151312_, p_151313_);
                            return;
                        }

                        int j = p_151312_.getEntities(
                                EntityTypeTest.forExactClass(entity.getClass()),
                                new AABB(
                                        p_151313_.getX(),
                                        p_151313_.getY(),
                                        p_151313_.getZ(),
                                        p_151313_.getX() + 1,
                                        p_151313_.getY() + 1,
                                        p_151313_.getZ() + 1
                                    )
                                    .inflate(this.spawnRange),
                                EntitySelector.NO_SPECTATORS
                            )
                            .size();
                        if (j >= this.maxNearbyEntities) {
                            this.delay(p_151312_, p_151313_);
                            return;
                        }

                        entity.snapTo(entity.getX(), entity.getY(), entity.getZ(), randomsource.nextFloat() * 360.0F, 0.0F);
                        if (entity instanceof Mob mob) {
                            if (!net.minecraftforge.event.ForgeEventFactory.checkSpawnPositionSpawner(mob, p_151312_, EntitySpawnReason.SPAWNER, spawndata, this)) {
                                continue;
                            }

                            boolean flag1 = spawndata.getEntityToSpawn().size() == 1 && spawndata.getEntityToSpawn().getString("id").isPresent();
                            // Forge: Patch in FinalizeSpawn for spawners so it may be fired unconditionally, instead of only when vanilla normally would trigger it.
                            var event = net.minecraftforge.event.ForgeEventFactory.onFinalizeSpawnSpawner(mob, p_151312_, p_151312_.getCurrentDifficultyAt(entity.blockPosition()), null, compoundtag, this);
                            if (!event.isCanceled() && flag1) {
                                ((Mob)entity).finalizeSpawn(p_151312_, event.getDifficulty(), EntitySpawnReason.SPAWNER, null);
                            }

                            spawndata.getEquipment().ifPresent(mob::equip);
                        }

                        if (!p_151312_.tryAddFreshEntityWithPassengers(entity)) {
                            this.delay(p_151312_, p_151313_);
                            return;
                        }

                        p_151312_.levelEvent(2004, p_151313_, 0);
                        p_151312_.gameEvent(entity, GameEvent.ENTITY_PLACE, blockpos);
                        if (entity instanceof Mob) {
                            ((Mob)entity).spawnAnim();
                        }

                        flag = true;
                    }
                }

                if (flag) {
                    this.delay(p_151312_, p_151313_);
                }
            }
        }
    }

    private void delay(Level p_151351_, BlockPos p_151352_) {
        RandomSource randomsource = p_151351_.random;
        if (this.maxSpawnDelay <= this.minSpawnDelay) {
            this.spawnDelay = this.minSpawnDelay;
        } else {
            this.spawnDelay = this.minSpawnDelay + randomsource.nextInt(this.maxSpawnDelay - this.minSpawnDelay);
        }

        this.spawnPotentials.getRandom(randomsource).ifPresent(p_390869_ -> this.setNextSpawnData(p_151351_, p_151352_, p_390869_));
        this.broadcastEvent(p_151351_, p_151352_, 1);
    }

    public void load(@Nullable Level p_151329_, BlockPos p_151330_, CompoundTag p_151331_) {
        this.spawnDelay = p_151331_.getShortOr("Delay", (short)20);
        p_151331_.read("SpawnData", SpawnData.CODEC).ifPresent(p_390872_ -> this.setNextSpawnData(p_151329_, p_151330_, p_390872_));
        this.spawnPotentials = p_151331_.read("SpawnPotentials", SpawnData.LIST_CODEC)
            .orElseGet(() -> WeightedList.of(this.nextSpawnData != null ? this.nextSpawnData : new SpawnData()));
        this.minSpawnDelay = p_151331_.getIntOr("MinSpawnDelay", 200);
        this.maxSpawnDelay = p_151331_.getIntOr("MaxSpawnDelay", 800);
        this.spawnCount = p_151331_.getIntOr("SpawnCount", 4);
        this.maxNearbyEntities = p_151331_.getIntOr("MaxNearbyEntities", 6);
        this.requiredPlayerRange = p_151331_.getIntOr("RequiredPlayerRange", 16);
        this.spawnRange = p_151331_.getIntOr("SpawnRange", 4);
        this.displayEntity = null;
    }

    public CompoundTag save(CompoundTag p_186382_) {
        p_186382_.putShort("Delay", (short)this.spawnDelay);
        p_186382_.putShort("MinSpawnDelay", (short)this.minSpawnDelay);
        p_186382_.putShort("MaxSpawnDelay", (short)this.maxSpawnDelay);
        p_186382_.putShort("SpawnCount", (short)this.spawnCount);
        p_186382_.putShort("MaxNearbyEntities", (short)this.maxNearbyEntities);
        p_186382_.putShort("RequiredPlayerRange", (short)this.requiredPlayerRange);
        p_186382_.putShort("SpawnRange", (short)this.spawnRange);
        p_186382_.storeNullable("SpawnData", SpawnData.CODEC, this.nextSpawnData);
        p_186382_.store("SpawnPotentials", SpawnData.LIST_CODEC, this.spawnPotentials);
        return p_186382_;
    }

    @Nullable
    public Entity getOrCreateDisplayEntity(Level p_254323_, BlockPos p_254313_) {
        if (this.displayEntity == null) {
            CompoundTag compoundtag = this.getOrCreateNextSpawnData(p_254323_, p_254323_.getRandom(), p_254313_).getEntityToSpawn();
            if (compoundtag.getString("id").isEmpty()) {
                return null;
            }

            this.displayEntity = EntityType.loadEntityRecursive(compoundtag, p_254323_, EntitySpawnReason.SPAWNER, Function.identity());
            if (compoundtag.size() == 1 && this.displayEntity instanceof Mob) {
            }
        }

        return this.displayEntity;
    }

    public boolean onEventTriggered(Level p_151317_, int p_151318_) {
        if (p_151318_ == 1) {
            if (p_151317_.isClientSide) {
                this.spawnDelay = this.minSpawnDelay;
            }

            return true;
        } else {
            return false;
        }
    }

    protected void setNextSpawnData(@Nullable Level p_151325_, BlockPos p_151326_, SpawnData p_151327_) {
        this.nextSpawnData = p_151327_;
    }

    private SpawnData getOrCreateNextSpawnData(@Nullable Level p_254503_, RandomSource p_253892_, BlockPos p_254487_) {
        if (this.nextSpawnData != null) {
            return this.nextSpawnData;
        } else {
            this.setNextSpawnData(p_254503_, p_254487_, this.spawnPotentials.getRandom(p_253892_).orElseGet(SpawnData::new));
            return this.nextSpawnData;
        }
    }

    public abstract void broadcastEvent(Level p_151322_, BlockPos p_151323_, int p_151324_);

    public double getSpin() {
        return this.spin;
    }

    public double getoSpin() {
        return this.oSpin;
    }

    @Nullable
    public Entity getSpawnerEntity() {
       return null;
    }

    @Nullable
    public net.minecraft.world.level.block.entity.BlockEntity getSpawnerBlockEntity() {
        return null;
    }
}
