package net.minecraft.world.entity.animal.frog;

import java.util.Optional;
import net.minecraft.core.ClientAsset;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.TemperatureVariants;
import net.minecraft.world.entity.variant.BiomeCheck;
import net.minecraft.world.entity.variant.PriorityProvider;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;
import net.minecraft.world.level.biome.Biome;

public interface FrogVariants {
    ResourceKey<FrogVariant> TEMPERATE = createKey(TemperatureVariants.TEMPERATE);
    ResourceKey<FrogVariant> WARM = createKey(TemperatureVariants.WARM);
    ResourceKey<FrogVariant> COLD = createKey(TemperatureVariants.COLD);

    private static ResourceKey<FrogVariant> createKey(ResourceLocation p_395998_) {
        return ResourceKey.create(Registries.FROG_VARIANT, p_395998_);
    }

    static void bootstrap(BootstrapContext<FrogVariant> p_395413_) {
        register(p_395413_, TEMPERATE, "entity/frog/temperate_frog", SpawnPrioritySelectors.fallback(0));
        register(p_395413_, WARM, "entity/frog/warm_frog", BiomeTags.SPAWNS_WARM_VARIANT_FROGS);
        register(p_395413_, COLD, "entity/frog/cold_frog", BiomeTags.SPAWNS_COLD_VARIANT_FROGS);
    }

    private static void register(BootstrapContext<FrogVariant> p_394280_, ResourceKey<FrogVariant> p_394919_, String p_395531_, TagKey<Biome> p_396514_) {
        HolderSet<Biome> holderset = p_394280_.lookup(Registries.BIOME).getOrThrow(p_396514_);
        register(p_394280_, p_394919_, p_395531_, SpawnPrioritySelectors.single(new BiomeCheck(holderset), 1));
    }

    private static void register(
        BootstrapContext<FrogVariant> p_394711_, ResourceKey<FrogVariant> p_397529_, String p_395128_, SpawnPrioritySelectors p_396085_
    ) {
        p_394711_.register(p_397529_, new FrogVariant(new ClientAsset(ResourceLocation.withDefaultNamespace(p_395128_)), p_396085_));
    }

    static Optional<Holder.Reference<FrogVariant>> selectVariantToSpawn(RandomSource p_397112_, RegistryAccess p_391237_, SpawnContext p_393557_) {
        return PriorityProvider.pick(p_391237_.lookupOrThrow(Registries.FROG_VARIANT).listElements(), Holder::value, p_397112_, p_393557_);
    }
}