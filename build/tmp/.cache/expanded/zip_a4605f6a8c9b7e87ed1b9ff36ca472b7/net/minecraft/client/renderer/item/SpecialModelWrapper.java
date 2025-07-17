package net.minecraft.client.renderer.item;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.TextureSlots;
import net.minecraft.client.renderer.special.SpecialModelRenderer;
import net.minecraft.client.renderer.special.SpecialModelRenderers;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvableModel;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class SpecialModelWrapper<T> implements ItemModel {
    private static final Vector3f[] EXTENTS = new Vector3f[]{
        new Vector3f(0.0F, 0.0F, 0.0F),
        new Vector3f(0.0F, 0.0F, 1.0F),
        new Vector3f(0.0F, 1.0F, 1.0F),
        new Vector3f(0.0F, 1.0F, 0.0F),
        new Vector3f(1.0F, 1.0F, 0.0F),
        new Vector3f(1.0F, 1.0F, 1.0F),
        new Vector3f(1.0F, 0.0F, 1.0F),
        new Vector3f(1.0F, 0.0F, 0.0F)
    };
    private final SpecialModelRenderer<T> specialRenderer;
    private final ModelRenderProperties properties;

    public SpecialModelWrapper(SpecialModelRenderer<T> p_375554_, ModelRenderProperties p_393945_) {
        this.specialRenderer = p_375554_;
        this.properties = p_393945_;
    }

    @Override
    public void update(
        ItemStackRenderState p_376096_,
        ItemStack p_376294_,
        ItemModelResolver p_377226_,
        ItemDisplayContext p_377206_,
        @Nullable ClientLevel p_375445_,
        @Nullable LivingEntity p_375829_,
        int p_375847_
    ) {
        ItemStackRenderState.LayerRenderState itemstackrenderstate$layerrenderstate = p_376096_.newLayer();
        if (p_376294_.hasFoil()) {
            itemstackrenderstate$layerrenderstate.setFoilType(ItemStackRenderState.FoilType.STANDARD);
        }

        itemstackrenderstate$layerrenderstate.setExtents(() -> EXTENTS);
        itemstackrenderstate$layerrenderstate.setupSpecialModel(this.specialRenderer, this.specialRenderer.extractArgument(p_376294_));
        this.properties.applyToLayer(itemstackrenderstate$layerrenderstate, p_377206_);
    }

    @OnlyIn(Dist.CLIENT)
    public record Unbaked(ResourceLocation base, SpecialModelRenderer.Unbaked specialModel) implements ItemModel.Unbaked {
        public static final MapCodec<SpecialModelWrapper.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
            p_375865_ -> p_375865_.group(
                    ResourceLocation.CODEC.fieldOf("base").forGetter(SpecialModelWrapper.Unbaked::base),
                    SpecialModelRenderers.CODEC.fieldOf("model").forGetter(SpecialModelWrapper.Unbaked::specialModel)
                )
                .apply(p_375865_, SpecialModelWrapper.Unbaked::new)
        );

        @Override
        public void resolveDependencies(ResolvableModel.Resolver p_377714_) {
            p_377714_.markDependency(this.base);
        }

        @Override
        public ItemModel bake(ItemModel.BakingContext p_378066_) {
            SpecialModelRenderer<?> specialmodelrenderer = this.specialModel.bake(p_378066_.entityModelSet());
            if (specialmodelrenderer == null) {
                return p_378066_.missingItemModel();
            } else {
                ModelRenderProperties modelrenderproperties = this.getProperties(p_378066_);
                return new SpecialModelWrapper<>(specialmodelrenderer, modelrenderproperties);
            }
        }

        private ModelRenderProperties getProperties(ItemModel.BakingContext p_393172_) {
            ModelBaker modelbaker = p_393172_.blockModelBaker();
            ResolvedModel resolvedmodel = modelbaker.getModel(this.base);
            TextureSlots textureslots = resolvedmodel.getTopTextureSlots();
            return ModelRenderProperties.fromResolvedModel(modelbaker, resolvedmodel, textureslots);
        }

        @Override
        public MapCodec<SpecialModelWrapper.Unbaked> type() {
            return MAP_CODEC;
        }
    }
}