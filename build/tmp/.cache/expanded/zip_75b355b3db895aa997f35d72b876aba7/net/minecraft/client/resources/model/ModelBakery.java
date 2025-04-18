package net.minecraft.client.resources.model;

import com.mojang.logging.LogUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.block.model.BlockStateModel;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.block.model.SimpleModelWrapper;
import net.minecraft.client.renderer.block.model.SingleVariant;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.item.ClientItem;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.MissingItemModel;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.thread.ParallelMapTransform;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class ModelBakery {
    public static final Material FIRE_0 = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/fire_0"));
    public static final Material FIRE_1 = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/fire_1"));
    public static final Material LAVA_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/lava_flow"));
    public static final Material WATER_FLOW = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/water_flow"));
    public static final Material WATER_OVERLAY = new Material(TextureAtlas.LOCATION_BLOCKS, ResourceLocation.withDefaultNamespace("block/water_overlay"));
    public static final Material BANNER_BASE = new Material(Sheets.BANNER_SHEET, ResourceLocation.withDefaultNamespace("entity/banner_base"));
    public static final Material SHIELD_BASE = new Material(Sheets.SHIELD_SHEET, ResourceLocation.withDefaultNamespace("entity/shield_base"));
    public static final Material NO_PATTERN_SHIELD = new Material(Sheets.SHIELD_SHEET, ResourceLocation.withDefaultNamespace("entity/shield_base_nopattern"));
    public static final int DESTROY_STAGE_COUNT = 10;
    public static final List<ResourceLocation> DESTROY_STAGES = IntStream.range(0, 10)
        .mapToObj(p_340955_ -> ResourceLocation.withDefaultNamespace("block/destroy_stage_" + p_340955_))
        .collect(Collectors.toList());
    public static final List<ResourceLocation> BREAKING_LOCATIONS = DESTROY_STAGES.stream()
        .map(p_340960_ -> p_340960_.withPath(p_340956_ -> "textures/" + p_340956_ + ".png"))
        .collect(Collectors.toList());
    public static final List<RenderType> DESTROY_TYPES = BREAKING_LOCATIONS.stream().map(RenderType::crumbling).collect(Collectors.toList());
    static final Logger LOGGER = LogUtils.getLogger();
    private final EntityModelSet entityModelSet;
    private final Map<BlockState, BlockStateModel.UnbakedRoot> unbakedBlockStateModels;
    private final Map<ResourceLocation, ClientItem> clientInfos;
    final Map<ResourceLocation, ResolvedModel> resolvedModels;
    final ResolvedModel missingModel;

    public ModelBakery(
        EntityModelSet p_376026_,
        Map<BlockState, BlockStateModel.UnbakedRoot> p_251087_,
        Map<ResourceLocation, ClientItem> p_250416_,
        Map<ResourceLocation, ResolvedModel> p_375852_,
        ResolvedModel p_393546_
    ) {
        this.entityModelSet = p_376026_;
        this.unbakedBlockStateModels = p_251087_;
        this.clientInfos = p_250416_;
        this.resolvedModels = p_375852_;
        this.missingModel = p_393546_;
    }

    public CompletableFuture<ModelBakery.BakingResult> bakeModels(SpriteGetter p_393789_, Executor p_392289_) {
        ModelBakery.MissingModels modelbakery$missingmodels = ModelBakery.MissingModels.bake(this.missingModel, p_393789_);
        ModelBakery.ModelBakerImpl modelbakery$modelbakerimpl = new ModelBakery.ModelBakerImpl(p_393789_);
        CompletableFuture<Map<BlockState, BlockStateModel>> completablefuture = ParallelMapTransform.schedule(this.unbakedBlockStateModels, (p_389589_, p_389590_) -> {
            try {
                return p_389590_.bake(p_389589_, modelbakery$modelbakerimpl);
            } catch (Exception exception) {
                LOGGER.warn("Unable to bake model: '{}': {}", p_389589_, exception);
                return null;
            }
        }, p_392289_);
        CompletableFuture<Map<ResourceLocation, ItemModel>> completablefuture1 = ParallelMapTransform.schedule(
            this.clientInfos,
            (p_389600_, p_389601_) -> {
                try {
                    return p_389601_.model()
                        .bake(
                            new ItemModel.BakingContext(modelbakery$modelbakerimpl, this.entityModelSet, modelbakery$missingmodels.item, p_389601_.registrySwapper())
                        );
                } catch (Exception exception) {
                    LOGGER.warn("Unable to bake item model: '{}'", p_389600_, exception);
                    return null;
                }
            },
            p_392289_
        );
        Map<ResourceLocation, ClientItem.Properties> map = new HashMap<>(this.clientInfos.size());
        this.clientInfos.forEach((p_389592_, p_389593_) -> {
            ClientItem.Properties clientitem$properties = p_389593_.properties();
            if (!clientitem$properties.equals(ClientItem.Properties.DEFAULT)) {
                map.put(p_389592_, clientitem$properties);
            }
        });
        return completablefuture.thenCombine(
            completablefuture1,
            (p_389596_, p_389597_) -> new ModelBakery.BakingResult(
                modelbakery$missingmodels, (Map<BlockState, BlockStateModel>)p_389596_, (Map<ResourceLocation, ItemModel>)p_389597_, map
            )
        );
    }

    @OnlyIn(Dist.CLIENT)
    public record BakingResult(
        ModelBakery.MissingModels missingModels,
        Map<BlockState, BlockStateModel> blockStateModels,
        Map<ResourceLocation, ItemModel> itemStackModels,
        Map<ResourceLocation, ClientItem.Properties> itemProperties
    ) {
    }

    @OnlyIn(Dist.CLIENT)
    public record MissingModels(BlockStateModel block, ItemModel item) {
        public static ModelBakery.MissingModels bake(ResolvedModel p_395506_, final SpriteGetter p_393671_) {
            ModelBaker modelbaker = new ModelBaker() {
                @Override
                public ResolvedModel getModel(ResourceLocation p_394244_) {
                    throw new IllegalStateException("Missing model can't have dependencies, but asked for " + p_394244_);
                }

                @Override
                public <T> T compute(ModelBaker.SharedOperationKey<T> p_396793_) {
                    return p_396793_.compute(this);
                }

                @Override
                public SpriteGetter sprites() {
                    return p_393671_;
                }
            };
            TextureSlots textureslots = p_395506_.getTopTextureSlots();
            boolean flag = p_395506_.getTopAmbientOcclusion();
            boolean flag1 = p_395506_.getTopGuiLight().lightLikeBlock();
            ItemTransforms itemtransforms = p_395506_.getTopTransforms();
            QuadCollection quadcollection = p_395506_.bakeTopGeometry(textureslots, modelbaker, BlockModelRotation.X0_Y0);
            TextureAtlasSprite textureatlassprite = p_395506_.resolveParticleSprite(textureslots, modelbaker);
            BlockStateModel blockstatemodel = new SingleVariant(new SimpleModelWrapper(quadcollection, flag, textureatlassprite));
            ItemModel itemmodel = new MissingItemModel(quadcollection.getAll(), new ModelRenderProperties(flag1, textureatlassprite, itemtransforms));
            return new ModelBakery.MissingModels(blockstatemodel, itemmodel);
        }
    }

    @OnlyIn(Dist.CLIENT)
    class ModelBakerImpl implements ModelBaker {
        private final SpriteGetter sprites;
        private final Map<ModelBaker.SharedOperationKey<Object>, Object> operationCache = new ConcurrentHashMap<>();
        private final Function<ModelBaker.SharedOperationKey<Object>, Object> cacheComputeFunction = p_395291_ -> p_395291_.compute(this);

        ModelBakerImpl(final SpriteGetter p_393058_) {
            this.sprites = p_393058_;
        }

        @Override
        public SpriteGetter sprites() {
            return this.sprites;
        }

        @Override
        public ResolvedModel getModel(ResourceLocation p_248568_) {
            ResolvedModel resolvedmodel = ModelBakery.this.resolvedModels.get(p_248568_);
            if (resolvedmodel == null) {
                ModelBakery.LOGGER.warn("Requested a model that was not discovered previously: {}", p_248568_);
                return ModelBakery.this.missingModel;
            } else {
                return resolvedmodel;
            }
        }

        @Override
        public <T> T compute(ModelBaker.SharedOperationKey<T> p_393371_) {
            return (T)this.operationCache.computeIfAbsent((ModelBaker.SharedOperationKey)p_393371_, this.cacheComputeFunction);
        }
    }
}