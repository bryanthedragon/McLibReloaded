package net.minecraft.world;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public interface RandomizableContainer extends Container {
    String LOOT_TABLE_TAG = "LootTable";
    String LOOT_TABLE_SEED_TAG = "LootTableSeed";

    @Nullable
    ResourceKey<LootTable> getLootTable();

    void setLootTable(@Nullable ResourceKey<LootTable> p_332603_);

    default void setLootTable(ResourceKey<LootTable> p_328843_, long p_312787_) {
        this.setLootTable(p_328843_);
        this.setLootTableSeed(p_312787_);
    }

    long getLootTableSeed();

    void setLootTableSeed(long p_309671_);

    BlockPos getBlockPos();

    @Nullable
    Level getLevel();

    static void setBlockEntityLootTable(BlockGetter p_312806_, RandomSource p_311284_, BlockPos p_311567_, ResourceKey<LootTable> p_330092_) {
        if (p_312806_.getBlockEntity(p_311567_) instanceof RandomizableContainer randomizablecontainer) {
            randomizablecontainer.setLootTable(p_330092_, p_311284_.nextLong());
        }
    }

    default boolean tryLoadLootTable(CompoundTag p_310316_) {
        ResourceKey<LootTable> resourcekey = p_310316_.read("LootTable", LootTable.KEY_CODEC).orElse(null);
        this.setLootTable(resourcekey);
        this.setLootTableSeed(p_310316_.getLongOr("LootTableSeed", 0L));
        return resourcekey != null;
    }

    default boolean trySaveLootTable(CompoundTag p_311616_) {
        ResourceKey<LootTable> resourcekey = this.getLootTable();
        if (resourcekey == null) {
            return false;
        } else {
            p_311616_.store("LootTable", LootTable.KEY_CODEC, resourcekey);
            long i = this.getLootTableSeed();
            if (i != 0L) {
                p_311616_.putLong("LootTableSeed", i);
            }

            return true;
        }
    }

    default void unpackLootTable(@Nullable Player p_309552_) {
        Level level = this.getLevel();
        BlockPos blockpos = this.getBlockPos();
        ResourceKey<LootTable> resourcekey = this.getLootTable();
        if (resourcekey != null && level != null && level.getServer() != null) {
            LootTable loottable = level.getServer().reloadableRegistries().getLootTable(resourcekey);
            if (p_309552_ instanceof ServerPlayer) {
                CriteriaTriggers.GENERATE_LOOT.trigger((ServerPlayer)p_309552_, resourcekey);
            }

            this.setLootTable(null);
            LootParams.Builder lootparams$builder = new LootParams.Builder((ServerLevel)level).withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(blockpos));
            if (p_309552_ != null) {
                lootparams$builder.withLuck(p_309552_.getLuck()).withParameter(LootContextParams.THIS_ENTITY, p_309552_);
            }

            loottable.fill(this, lootparams$builder.create(LootContextParamSets.CHEST), this.getLootTableSeed());
        }
    }
}