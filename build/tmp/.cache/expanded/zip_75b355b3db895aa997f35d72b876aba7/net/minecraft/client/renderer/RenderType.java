package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.vertex.MeshData;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.Util;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.LoadingOverlay;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class RenderType extends RenderStateShard {
    private static final int MEGABYTE = 1048576;
    public static final int BIG_BUFFER_SIZE = 4194304;
    public static final int SMALL_BUFFER_SIZE = 786432;
    public static final int TRANSIENT_BUFFER_SIZE = 1536;
    private static final RenderType SOLID = create(
        "solid",
        4194304,
        true,
        false,
        RenderPipelines.SOLID,
        RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).createCompositeState(true)
    );
    private static final RenderType CUTOUT_MIPPED = create(
        "cutout_mipped",
        4194304,
        true,
        false,
        RenderPipelines.CUTOUT_MIPPED,
        RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).createCompositeState(true)
    );
    private static final RenderType CUTOUT = create(
        "cutout",
        786432,
        true,
        false,
        RenderPipelines.CUTOUT,
        RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET).createCompositeState(true)
    );
    private static final RenderType TRANSLUCENT = create(
        "translucent",
        786432,
        true,
        true,
        RenderPipelines.TRANSLUCENT,
        RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setOutputState(TRANSLUCENT_TARGET).createCompositeState(true)
    );
    private static final RenderType TRANSLUCENT_MOVING_BLOCK = create(
        "translucent_moving_block",
        786432,
        false,
        true,
        RenderPipelines.TRANSLUCENT_MOVING_BLOCK,
        RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setOutputState(ITEM_ENTITY_TARGET).createCompositeState(true)
    );
    private static final Function<ResourceLocation, RenderType> ARMOR_CUTOUT_NO_CULL = Util.memoize(
        p_389448_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389448_, TriState.FALSE, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                .createCompositeState(true);
            return create("armor_cutout_no_cull", 1536, true, false, RenderPipelines.ARMOR_CUTOUT_NO_CULL, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> ARMOR_TRANSLUCENT = Util.memoize(
        p_389426_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389426_, TriState.FALSE, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                .createCompositeState(true);
            return create("armor_translucent", 1536, true, true, RenderPipelines.ARMOR_TRANSLUCENT, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> ENTITY_SOLID = Util.memoize(
        p_389438_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389438_, TriState.FALSE, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
            return create("entity_solid", 1536, true, false, RenderPipelines.ENTITY_SOLID, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> ENTITY_SOLID_Z_OFFSET_FORWARD = Util.memoize(
        p_389425_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389425_, TriState.FALSE, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING_FORWARD)
                .createCompositeState(true);
            return create("entity_solid_z_offset_forward", 1536, true, false, RenderPipelines.ENTITY_SOLID_Z_OFFSET_FORWARD, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> ENTITY_CUTOUT = Util.memoize(
        p_389450_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389450_, TriState.FALSE, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
            return create("entity_cutout", 1536, true, false, RenderPipelines.ENTITY_CUTOUT, rendertype$compositestate);
        }
    );
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_CUTOUT_NO_CULL = Util.memoize(
        (p_389414_, p_389415_) -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389414_, TriState.FALSE, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(p_389415_);
            return create("entity_cutout_no_cull", 1536, true, false, RenderPipelines.ENTITY_CUTOUT_NO_CULL, rendertype$compositestate);
        }
    );
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_CUTOUT_NO_CULL_Z_OFFSET = Util.memoize(
        (p_389422_, p_389423_) -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389422_, TriState.FALSE, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                .createCompositeState(p_389423_);
            return create("entity_cutout_no_cull_z_offset", 1536, true, false, RenderPipelines.ENTITY_CUTOUT_NO_CULL_Z_OFFSET, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> ITEM_ENTITY_TRANSLUCENT_CULL = Util.memoize(
        p_389444_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389444_, TriState.FALSE, false))
                .setOutputState(ITEM_ENTITY_TARGET)
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
            return create("item_entity_translucent_cull", 1536, true, true, RenderPipelines.ITEM_ENTITY_TRANSLUCENT_CULL, rendertype$compositestate);
        }
    );
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT = Util.memoize(
        (p_389446_, p_389447_) -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389446_, TriState.FALSE, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(p_389447_);
            return create("entity_translucent", 1536, true, true, RenderPipelines.ENTITY_TRANSLUCENT, rendertype$compositestate);
        }
    );
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize(
        (p_389418_, p_389419_) -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389418_, TriState.FALSE, false))
                .setOverlayState(OVERLAY)
                .createCompositeState(p_389419_);
            return create("entity_translucent_emissive", 1536, true, true, RenderPipelines.ENTITY_TRANSLUCENT_EMISSIVE, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> ENTITY_SMOOTH_CUTOUT = Util.memoize(
        p_389452_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389452_, TriState.FALSE, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(true);
            return create("entity_smooth_cutout", 1536, RenderPipelines.ENTITY_SMOOTH_CUTOUT, rendertype$compositestate);
        }
    );
    private static final BiFunction<ResourceLocation, Boolean, RenderType> BEACON_BEAM = Util.memoize(
        (p_389429_, p_389430_) -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389429_, TriState.FALSE, false))
                .createCompositeState(false);
            return create("beacon_beam", 1536, false, true, p_389430_ ? RenderPipelines.BEACON_BEAM_TRANSLUCENT : RenderPipelines.BEACON_BEAM_OPAQUE, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> ENTITY_DECAL = Util.memoize(
        p_389437_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389437_, TriState.FALSE, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(false);
            return create("entity_decal", 1536, RenderPipelines.ENTITY_DECAL, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> ENTITY_NO_OUTLINE = Util.memoize(
        p_389436_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389436_, TriState.FALSE, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(false);
            return create("entity_no_outline", 1536, false, true, RenderPipelines.ENTITY_NO_OUTLINE, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> ENTITY_SHADOW = Util.memoize(
        p_389412_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389412_, TriState.FALSE, false))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .setLayeringState(VIEW_OFFSET_Z_LAYERING)
                .createCompositeState(false);
            return create("entity_shadow", 1536, false, false, RenderPipelines.ENTITY_SHADOW, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> DRAGON_EXPLOSION_ALPHA = Util.memoize(
        p_389454_ -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389454_, TriState.FALSE, false))
                .createCompositeState(true);
            return create("entity_alpha", 1536, RenderPipelines.DRAGON_EXPLOSION_ALPHA, rendertype$compositestate);
        }
    );
    private static final Function<ResourceLocation, RenderType> EYES = Util.memoize(
        p_389420_ -> {
            RenderStateShard.TextureStateShard renderstateshard$texturestateshard = new RenderStateShard.TextureStateShard(p_389420_, TriState.FALSE, false);
            return create(
                "eyes",
                1536,
                false,
                true,
                RenderPipelines.EYES,
                RenderType.CompositeState.builder().setTextureState(renderstateshard$texturestateshard).createCompositeState(false)
            );
        }
    );
    private static final RenderType LEASH = create(
        "leash", 1536, RenderPipelines.LEASH, RenderType.CompositeState.builder().setTextureState(NO_TEXTURE).setLightmapState(LIGHTMAP).createCompositeState(false)
    );
    private static final RenderType WATER_MASK = create(
        "water_mask", 1536, RenderPipelines.WATER_MASK, RenderType.CompositeState.builder().setTextureState(NO_TEXTURE).createCompositeState(false)
    );
    private static final RenderType ARMOR_ENTITY_GLINT = create(
        "armor_entity_glint",
        1536,
        RenderPipelines.GLINT,
        RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ARMOR, TriState.DEFAULT, false))
            .setTexturingState(ARMOR_ENTITY_GLINT_TEXTURING)
            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
            .createCompositeState(false)
    );
    private static final RenderType GLINT_TRANSLUCENT = create(
        "glint_translucent",
        1536,
        RenderPipelines.GLINT,
        RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, TriState.DEFAULT, false))
            .setTexturingState(GLINT_TEXTURING)
            .setOutputState(ITEM_ENTITY_TARGET)
            .createCompositeState(false)
    );
    private static final RenderType GLINT = create(
        "glint",
        1536,
        RenderPipelines.GLINT,
        RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, TriState.DEFAULT, false))
            .setTexturingState(GLINT_TEXTURING)
            .createCompositeState(false)
    );
    private static final RenderType ENTITY_GLINT = create(
        "entity_glint",
        1536,
        RenderPipelines.GLINT,
        RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(ItemRenderer.ENCHANTED_GLINT_ITEM, TriState.DEFAULT, false))
            .setTexturingState(ENTITY_GLINT_TEXTURING)
            .createCompositeState(false)
    );
    private static final Function<ResourceLocation, RenderType> CRUMBLING = Util.memoize(
        p_389416_ -> {
            RenderStateShard.TextureStateShard renderstateshard$texturestateshard = new RenderStateShard.TextureStateShard(p_389416_, TriState.FALSE, false);
            return create(
                "crumbling",
                1536,
                false,
                true,
                RenderPipelines.CRUMBLING,
                RenderType.CompositeState.builder().setTextureState(renderstateshard$texturestateshard).createCompositeState(false)
            );
        }
    );
    private static final Function<ResourceLocation, RenderType> TEXT = Util.memoize(
        p_389424_ -> create(
            "text",
            786432,
            false,
            false,
            RenderPipelines.TEXT,
            RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389424_, TriState.FALSE, false))
                .setLightmapState(LIGHTMAP)
                .createCompositeState(false)
        )
    );
    private static final RenderType TEXT_BACKGROUND = create(
        "text_background",
        1536,
        false,
        true,
        RenderPipelines.TEXT_BACKGROUND,
        RenderType.CompositeState.builder().setTextureState(NO_TEXTURE).setLightmapState(LIGHTMAP).createCompositeState(false)
    );
    private static final Function<ResourceLocation, RenderType> TEXT_INTENSITY = Util.memoize(
        p_389421_ -> create(
            "text_intensity",
            786432,
            false,
            false,
            RenderPipelines.TEXT_INTENSITY,
            RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389421_, TriState.FALSE, false))
                .setLightmapState(LIGHTMAP)
                .createCompositeState(false)
        )
    );
    private static final Function<ResourceLocation, RenderType> TEXT_POLYGON_OFFSET = Util.memoize(
        p_389439_ -> create(
            "text_polygon_offset",
            1536,
            false,
            true,
            RenderPipelines.TEXT_POLYGON_OFFSET,
            RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389439_, TriState.FALSE, false))
                .setLightmapState(LIGHTMAP)
                .createCompositeState(false)
        )
    );
    private static final Function<ResourceLocation, RenderType> TEXT_INTENSITY_POLYGON_OFFSET = Util.memoize(
        p_389453_ -> create(
            "text_intensity_polygon_offset",
            1536,
            false,
            true,
            RenderPipelines.TEXT_INTENSITY,
            RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389453_, TriState.FALSE, false))
                .setLightmapState(LIGHTMAP)
                .createCompositeState(false)
        )
    );
    private static final Function<ResourceLocation, RenderType> TEXT_SEE_THROUGH = Util.memoize(
        p_389413_ -> create(
            "text_see_through",
            1536,
            false,
            false,
            RenderPipelines.TEXT_SEE_THROUGH,
            RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389413_, TriState.FALSE, false))
                .setLightmapState(LIGHTMAP)
                .createCompositeState(false)
        )
    );
    private static final RenderType TEXT_BACKGROUND_SEE_THROUGH = create(
        "text_background_see_through",
        1536,
        false,
        true,
        RenderPipelines.TEXT_BACKGROUND_SEE_THROUGH,
        RenderType.CompositeState.builder().setTextureState(NO_TEXTURE).setLightmapState(LIGHTMAP).createCompositeState(false)
    );
    private static final Function<ResourceLocation, RenderType> TEXT_INTENSITY_SEE_THROUGH = Util.memoize(
        p_389427_ -> create(
            "text_intensity_see_through",
            1536,
            false,
            true,
            RenderPipelines.TEXT_INTENSITY_SEE_THROUGH,
            RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389427_, TriState.FALSE, false))
                .setLightmapState(LIGHTMAP)
                .createCompositeState(false)
        )
    );
    private static final RenderType LIGHTNING = create(
        "lightning", 1536, false, true, RenderPipelines.LIGHTNING, RenderType.CompositeState.builder().setOutputState(WEATHER_TARGET).createCompositeState(false)
    );
    private static final RenderType DRAGON_RAYS = create(
        "dragon_rays", 1536, false, false, RenderPipelines.DRAGON_RAYS, RenderType.CompositeState.builder().createCompositeState(false)
    );
    private static final RenderType DRAGON_RAYS_DEPTH = create(
        "dragon_rays_depth", 1536, false, false, RenderPipelines.DRAGON_RAYS_DEPTH, RenderType.CompositeState.builder().createCompositeState(false)
    );
    private static final RenderType TRIPWIRE = create(
        "tripwire",
        1536,
        true,
        true,
        RenderPipelines.TRIPWIRE,
        RenderType.CompositeState.builder().setLightmapState(LIGHTMAP).setTextureState(BLOCK_SHEET_MIPPED).setOutputState(WEATHER_TARGET).createCompositeState(true)
    );
    private static final RenderType END_PORTAL = create(
        "end_portal",
        1536,
        false,
        false,
        RenderPipelines.END_PORTAL,
        RenderType.CompositeState.builder()
            .setTextureState(
                RenderStateShard.MultiTextureStateShard.builder()
                    .add(TheEndPortalRenderer.END_SKY_LOCATION, false, false)
                    .add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false)
                    .build()
            )
            .createCompositeState(false)
    );
    private static final RenderType END_GATEWAY = create(
        "end_gateway",
        1536,
        false,
        false,
        RenderPipelines.END_GATEWAY,
        RenderType.CompositeState.builder()
            .setTextureState(
                RenderStateShard.MultiTextureStateShard.builder()
                    .add(TheEndPortalRenderer.END_SKY_LOCATION, false, false)
                    .add(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false)
                    .build()
            )
            .createCompositeState(false)
    );
    public static final RenderType.CompositeRenderType LINES = create(
        "lines",
        1536,
        RenderPipelines.LINES,
        RenderType.CompositeState.builder()
            .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
            .setOutputState(ITEM_ENTITY_TARGET)
            .createCompositeState(false)
    );
    public static final RenderType.CompositeRenderType SECONDARY_BLOCK_OUTLINE = create(
        "secondary_block_outline",
        1536,
        RenderPipelines.SECONDARY_BLOCK_OUTLINE,
        RenderType.CompositeState.builder()
            .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(7.0)))
            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
            .setOutputState(ITEM_ENTITY_TARGET)
            .createCompositeState(false)
    );
    public static final RenderType.CompositeRenderType LINE_STRIP = create(
        "line_strip",
        1536,
        RenderPipelines.LINE_STRIP,
        RenderType.CompositeState.builder()
            .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
            .setOutputState(ITEM_ENTITY_TARGET)
            .createCompositeState(false)
    );
    private static final Function<Double, RenderType.CompositeRenderType> DEBUG_LINE_STRIP = Util.memoize(
        p_389417_ -> create(
            "debug_line_strip",
            1536,
            RenderPipelines.DEBUG_LINE_STRIP,
            RenderType.CompositeState.builder().setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(p_389417_))).createCompositeState(false)
        )
    );
    private static final Function<Double, RenderType.CompositeRenderType> DEBUG_LINE = Util.memoize(
        p_389443_ -> create(
            "debug_line",
            1536,
            RenderPipelines.LINES,
            RenderType.CompositeState.builder().setLineState(new RenderStateShard.LineStateShard(OptionalDouble.of(p_389443_))).createCompositeState(false)
        )
    );
    private static final RenderType.CompositeRenderType DEBUG_FILLED_BOX = create(
        "debug_filled_box", 1536, false, true, RenderPipelines.DEBUG_FILLED_BOX, RenderType.CompositeState.builder().setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false)
    );
    private static final RenderType.CompositeRenderType DEBUG_QUADS = create(
        "debug_quads", 1536, false, true, RenderPipelines.DEBUG_QUADS, RenderType.CompositeState.builder().createCompositeState(false)
    );
    private static final RenderType.CompositeRenderType DEBUG_TRIANGLE_FAN = create(
        "debug_triangle_fan", 1536, false, true, RenderPipelines.DEBUG_TRIANGLE_FAN, RenderType.CompositeState.builder().createCompositeState(false)
    );
    private static final RenderType.CompositeRenderType DEBUG_STRUCTURE_QUADS = create(
        "debug_structure_quads", 1536, false, true, RenderPipelines.DEBUG_STRUCTURE_QUADS, RenderType.CompositeState.builder().createCompositeState(false)
    );
    private static final RenderType.CompositeRenderType DEBUG_SECTION_QUADS = create(
        "debug_section_quads", 1536, false, true, RenderPipelines.DEBUG_SECTION_QUADS, RenderType.CompositeState.builder().setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(false)
    );
    private static final Function<ResourceLocation, RenderType> OPAQUE_PARTICLE = Util.memoize(
        p_389433_ -> create(
            "opaque_particle",
            1536,
            false,
            false,
            RenderPipelines.OPAQUE_PARTICLE,
            RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389433_, TriState.FALSE, false))
                .setLightmapState(LIGHTMAP)
                .createCompositeState(false)
        )
    );
    private static final Function<ResourceLocation, RenderType> TRANSLUCENT_PARTICLE = Util.memoize(
        p_389431_ -> create(
            "translucent_particle",
            1536,
            false,
            false,
            RenderPipelines.TRANSLUCENT_PARTICLE,
            RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_389431_, TriState.FALSE, false))
                .setOutputState(PARTICLES_TARGET)
                .setLightmapState(LIGHTMAP)
                .createCompositeState(false)
        )
    );
    private static final Function<ResourceLocation, RenderType> WEATHER_DEPTH_WRITE = createWeather(RenderPipelines.WEATHER_DEPTH_WRITE);
    private static final Function<ResourceLocation, RenderType> WEATHER_NO_DEPTH_WRITE = createWeather(RenderPipelines.WEATHER_NO_DEPTH_WRITE);
    private static final RenderType SUNRISE_SUNSET = create(
        "sunrise_sunset", 1536, false, false, RenderPipelines.SUNRISE_SUNSET, RenderType.CompositeState.builder().createCompositeState(false)
    );
    private static final Function<ResourceLocation, RenderType> CELESTIAL = Util.memoize(
        p_389435_ -> create(
            "celestial",
            1536,
            false,
            false,
            RenderPipelines.CELESTIAL,
            RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(p_389435_, TriState.FALSE, false)).createCompositeState(false)
        )
    );
    private static final Function<ResourceLocation, RenderType> BLOCK_SCREEN_EFFECT = Util.memoize(
        p_389445_ -> create(
            "block_screen_effect",
            1536,
            false,
            false,
            RenderPipelines.BLOCK_SCREEN_EFFECT,
            RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(p_389445_, TriState.FALSE, false)).createCompositeState(false)
        )
    );
    private static final Function<ResourceLocation, RenderType> FIRE_SCREEN_EFFECT = Util.memoize(
        p_389451_ -> create(
            "fire_screen_effect",
            1536,
            false,
            false,
            RenderPipelines.FIRE_SCREEN_EFFECT,
            RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(p_389451_, TriState.FALSE, false)).createCompositeState(false)
        )
    );
    private static final RenderType.CompositeRenderType GUI = create(
        "gui", 786432, RenderPipelines.GUI, RenderType.CompositeState.builder().createCompositeState(false)
    );
    private static final RenderType.CompositeRenderType GUI_OVERLAY = create(
        "gui_overlay", 1536, RenderPipelines.GUI_OVERLAY, RenderType.CompositeState.builder().createCompositeState(false)
    );
    private static final Function<ResourceLocation, RenderType> GUI_TEXTURED_OVERLAY = Util.memoize(
        p_389442_ -> create(
            "gui_textured_overlay",
            1536,
            RenderPipelines.GUI_TEXTURED_OVERLAY,
            RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(p_389442_, TriState.DEFAULT, false)).createCompositeState(false)
        )
    );
    private static final Function<ResourceLocation, RenderType> GUI_OPAQUE_TEXTURED_BACKGROUND = Util.memoize(
        p_389434_ -> create(
            "gui_opaque_textured_background",
            786432,
            RenderPipelines.GUI_OPAQUE_TEXTURED_BACKGROUND,
            RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(p_389434_, TriState.FALSE, false)).createCompositeState(false)
        )
    );
    private static final RenderType.CompositeRenderType GUI_NAUSEA_OVERLAY = create(
        "gui_nausea_overlay",
        1536,
        RenderPipelines.GUI_NAUSEA_OVERLAY,
        RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(Gui.NAUSEA_LOCATION, TriState.DEFAULT, false)).createCompositeState(false)
    );
    private static final RenderType.CompositeRenderType GUI_TEXT_HIGHLIGHT = create(
        "gui_text_highlight", 1536, RenderPipelines.GUI_TEXT_HIGHLIGHT, RenderType.CompositeState.builder().createCompositeState(false)
    );
    private static final RenderType.CompositeRenderType GUI_GHOST_RECIPE_OVERLAY = create(
        "gui_ghost_recipe_overlay", 1536, RenderPipelines.GUI_GHOST_RECIPE_OVERLAY, RenderType.CompositeState.builder().createCompositeState(false)
    );
    private static final Function<ResourceLocation, RenderType> GUI_TEXTURED = Util.memoize(
        p_389432_ -> create(
            "gui_textured",
            786432,
            RenderPipelines.GUI_TEXTURED,
            RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(p_389432_, TriState.FALSE, false)).createCompositeState(false)
        )
    );
    private static final Function<ResourceLocation, RenderType> VIGNETTE = Util.memoize(
        p_389449_ -> create(
            "vignette",
            786432,
            RenderPipelines.VIGNETTE,
            RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(p_389449_, TriState.DEFAULT, false)).createCompositeState(false)
        )
    );
    private static final Function<ResourceLocation, RenderType> CROSSHAIR = Util.memoize(
        p_389428_ -> create(
            "crosshair",
            786432,
            RenderPipelines.CROSSHAIR,
            RenderType.CompositeState.builder().setTextureState(new RenderStateShard.TextureStateShard(p_389428_, TriState.FALSE, false)).createCompositeState(false)
        )
    );
    private static final RenderType.CompositeRenderType MOJANG_LOGO = create(
        "mojang_logo",
        786432,
        RenderPipelines.MOJANG_LOGO,
        RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(LoadingOverlay.MOJANG_STUDIOS_LOGO_LOCATION, TriState.DEFAULT, false))
            .createCompositeState(false)
    );
    private static final ImmutableList<RenderType> CHUNK_BUFFER_LAYERS = ImmutableList.of(solid(), cutoutMipped(), cutout(), translucent(), tripwire());
    static {
        int i = 0;
        for (var layer : CHUNK_BUFFER_LAYERS)
            layer.chunkLayerId = i++;
    }

    private final int bufferSize;
    private final boolean affectsCrumbling;
    private final boolean sortOnUpload;
    private int chunkLayerId = -1;
    /** {@return the unique ID of this {@link RenderType} for chunk rendering purposes, or {@literal -1} if this is not a chunk {@link RenderType}} */
    public final int getChunkLayerId() {
        return chunkLayerId;
    }

    public static RenderType solid() {
        return SOLID;
    }

    public static RenderType cutoutMipped() {
        return CUTOUT_MIPPED;
    }

    public static RenderType cutout() {
        return CUTOUT;
    }

    public static RenderType translucent() {
        return TRANSLUCENT;
    }

    public static RenderType translucentMovingBlock() {
        return TRANSLUCENT_MOVING_BLOCK;
    }

    public static RenderType armorCutoutNoCull(ResourceLocation p_110432_) {
        return ARMOR_CUTOUT_NO_CULL.apply(p_110432_);
    }

    public static RenderType createArmorDecalCutoutNoCull(ResourceLocation p_298982_) {
        RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.builder()
            .setTextureState(new RenderStateShard.TextureStateShard(p_298982_, TriState.FALSE, false))
            .setLightmapState(LIGHTMAP)
            .setOverlayState(OVERLAY)
            .setLayeringState(VIEW_OFFSET_Z_LAYERING)
            .createCompositeState(true);
        return create("armor_decal_cutout_no_cull", 1536, true, false, RenderPipelines.ARMOR_DECAL_CUTOUT_NO_CULL, rendertype$compositestate);
    }

    public static RenderType armorTranslucent(ResourceLocation p_368218_) {
        return ARMOR_TRANSLUCENT.apply(p_368218_);
    }

    public static RenderType entitySolid(ResourceLocation p_110447_) {
        return ENTITY_SOLID.apply(p_110447_);
    }

    public static RenderType entitySolidZOffsetForward(ResourceLocation p_364403_) {
        return ENTITY_SOLID_Z_OFFSET_FORWARD.apply(p_364403_);
    }

    public static RenderType entityCutout(ResourceLocation p_110453_) {
        return ENTITY_CUTOUT.apply(p_110453_);
    }

    public static RenderType entityCutoutNoCull(ResourceLocation p_110444_, boolean p_110445_) {
        return ENTITY_CUTOUT_NO_CULL.apply(p_110444_, p_110445_);
    }

    public static RenderType entityCutoutNoCull(ResourceLocation p_110459_) {
        return entityCutoutNoCull(p_110459_, true);
    }

    public static RenderType entityCutoutNoCullZOffset(ResourceLocation p_110449_, boolean p_110450_) {
        return ENTITY_CUTOUT_NO_CULL_Z_OFFSET.apply(p_110449_, p_110450_);
    }

    public static RenderType entityCutoutNoCullZOffset(ResourceLocation p_110465_) {
        return entityCutoutNoCullZOffset(p_110465_, true);
    }

    public static RenderType itemEntityTranslucentCull(ResourceLocation p_110468_) {
        return ITEM_ENTITY_TRANSLUCENT_CULL.apply(p_110468_);
    }

    public static RenderType entityTranslucent(ResourceLocation p_110455_, boolean p_110456_) {
        return ENTITY_TRANSLUCENT.apply(p_110455_, p_110456_);
    }

    public static RenderType entityTranslucent(ResourceLocation p_110474_) {
        return entityTranslucent(p_110474_, true);
    }

    public static RenderType entityTranslucentEmissive(ResourceLocation p_234336_, boolean p_234337_) {
        return ENTITY_TRANSLUCENT_EMISSIVE.apply(p_234336_, p_234337_);
    }

    public static RenderType entityTranslucentEmissive(ResourceLocation p_234339_) {
        return entityTranslucentEmissive(p_234339_, true);
    }

    public static RenderType entitySmoothCutout(ResourceLocation p_110477_) {
        return ENTITY_SMOOTH_CUTOUT.apply(p_110477_);
    }

    public static RenderType beaconBeam(ResourceLocation p_110461_, boolean p_110462_) {
        return BEACON_BEAM.apply(p_110461_, p_110462_);
    }

    public static RenderType entityDecal(ResourceLocation p_110480_) {
        return ENTITY_DECAL.apply(p_110480_);
    }

    public static RenderType entityNoOutline(ResourceLocation p_110483_) {
        return ENTITY_NO_OUTLINE.apply(p_110483_);
    }

    public static RenderType entityShadow(ResourceLocation p_110486_) {
        return ENTITY_SHADOW.apply(p_110486_);
    }

    public static RenderType dragonExplosionAlpha(ResourceLocation p_173236_) {
        return DRAGON_EXPLOSION_ALPHA.apply(p_173236_);
    }

    public static RenderType eyes(ResourceLocation p_110489_) {
        return EYES.apply(p_110489_);
    }

    public static RenderType breezeEyes(ResourceLocation p_311465_) {
        return ENTITY_TRANSLUCENT_EMISSIVE.apply(p_311465_, false);
    }

    public static RenderType breezeWind(ResourceLocation p_311543_, float p_312161_, float p_310801_) {
        return create(
            "breeze_wind",
            1536,
            false,
            true,
            RenderPipelines.BREEZE_WIND,
            RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_311543_, TriState.FALSE, false))
                .setTexturingState(new RenderStateShard.OffsetTexturingStateShard(p_312161_, p_310801_))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(NO_OVERLAY)
                .createCompositeState(false)
        );
    }

    public static RenderType energySwirl(ResourceLocation p_110437_, float p_110438_, float p_110439_) {
        return create(
            "energy_swirl",
            1536,
            false,
            true,
            RenderPipelines.ENERGY_SWIRL,
            RenderType.CompositeState.builder()
                .setTextureState(new RenderStateShard.TextureStateShard(p_110437_, TriState.FALSE, false))
                .setTexturingState(new RenderStateShard.OffsetTexturingStateShard(p_110438_, p_110439_))
                .setLightmapState(LIGHTMAP)
                .setOverlayState(OVERLAY)
                .createCompositeState(false)
        );
    }

    public static RenderType leash() {
        return LEASH;
    }

    public static RenderType waterMask() {
        return WATER_MASK;
    }

    public static RenderType outline(ResourceLocation p_110492_) {
        return RenderType.CompositeRenderType.OUTLINE.apply(p_110492_, false);
    }

    public static RenderType armorEntityGlint() {
        return ARMOR_ENTITY_GLINT;
    }

    public static RenderType glintTranslucent() {
        return GLINT_TRANSLUCENT;
    }

    public static RenderType glint() {
        return GLINT;
    }

    public static RenderType entityGlint() {
        return ENTITY_GLINT;
    }

    public static RenderType crumbling(ResourceLocation p_110495_) {
        return CRUMBLING.apply(p_110495_);
    }

    public static RenderType text(ResourceLocation p_110498_) {
        return net.minecraftforge.client.ForgeRenderTypes.getText(p_110498_);
    }

    public static RenderType textBackground() {
        return TEXT_BACKGROUND;
    }

    public static RenderType textIntensity(ResourceLocation p_173238_) {
        return net.minecraftforge.client.ForgeRenderTypes.getTextIntensity(p_173238_);
    }

    public static RenderType textPolygonOffset(ResourceLocation p_181445_) {
        return net.minecraftforge.client.ForgeRenderTypes.getTextPolygonOffset(p_181445_);
    }

    public static RenderType textIntensityPolygonOffset(ResourceLocation p_181447_) {
        return net.minecraftforge.client.ForgeRenderTypes.getTextIntensityPolygonOffset(p_181447_);
    }

    public static RenderType textSeeThrough(ResourceLocation p_110501_) {
        return net.minecraftforge.client.ForgeRenderTypes.getTextSeeThrough(p_110501_);
    }

    public static RenderType textBackgroundSeeThrough() {
        return TEXT_BACKGROUND_SEE_THROUGH;
    }

    public static RenderType textIntensitySeeThrough(ResourceLocation p_173241_) {
        return net.minecraftforge.client.ForgeRenderTypes.getTextIntensitySeeThrough(p_173241_);
    }

    public static RenderType lightning() {
        return LIGHTNING;
    }

    public static RenderType dragonRays() {
        return DRAGON_RAYS;
    }

    public static RenderType dragonRaysDepth() {
        return DRAGON_RAYS_DEPTH;
    }

    public static RenderType tripwire() {
        return TRIPWIRE;
    }

    public static RenderType endPortal() {
        return END_PORTAL;
    }

    public static RenderType endGateway() {
        return END_GATEWAY;
    }

    public static RenderType lines() {
        return LINES;
    }

    public static RenderType secondaryBlockOutline() {
        return SECONDARY_BLOCK_OUTLINE;
    }

    public static RenderType lineStrip() {
        return LINE_STRIP;
    }

    public static RenderType debugLineStrip(double p_270166_) {
        return DEBUG_LINE_STRIP.apply(p_270166_);
    }

    public static RenderType debugLine(double p_396315_) {
        return DEBUG_LINE.apply(p_396315_);
    }

    public static RenderType debugFilledBox() {
        return DEBUG_FILLED_BOX;
    }

    public static RenderType debugQuads() {
        return DEBUG_QUADS;
    }

    public static RenderType debugTriangleFan() {
        return DEBUG_TRIANGLE_FAN;
    }

    public static RenderType debugStructureQuads() {
        return DEBUG_STRUCTURE_QUADS;
    }

    public static RenderType debugSectionQuads() {
        return DEBUG_SECTION_QUADS;
    }

    public static RenderType opaqueParticle(ResourceLocation p_377146_) {
        return OPAQUE_PARTICLE.apply(p_377146_);
    }

    public static RenderType translucentParticle(ResourceLocation p_375670_) {
        return TRANSLUCENT_PARTICLE.apply(p_375670_);
    }

    private static Function<ResourceLocation, RenderType> createWeather(RenderPipeline p_395516_) {
        return Util.memoize(
            p_389441_ -> create(
                "weather",
                1536,
                false,
                false,
                p_395516_,
                RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(p_389441_, TriState.FALSE, false))
                    .setOutputState(WEATHER_TARGET)
                    .setLightmapState(LIGHTMAP)
                    .createCompositeState(false)
            )
        );
    }

    public static RenderType weather(ResourceLocation p_376873_, boolean p_375456_) {
        return (p_375456_ ? WEATHER_DEPTH_WRITE : WEATHER_NO_DEPTH_WRITE).apply(p_376873_);
    }

    public static RenderType sunriseSunset() {
        return SUNRISE_SUNSET;
    }

    public static RenderType celestial(ResourceLocation p_376189_) {
        return CELESTIAL.apply(p_376189_);
    }

    public static RenderType blockScreenEffect(ResourceLocation p_378425_) {
        return BLOCK_SCREEN_EFFECT.apply(p_378425_);
    }

    public static RenderType fireScreenEffect(ResourceLocation p_376662_) {
        return FIRE_SCREEN_EFFECT.apply(p_376662_);
    }

    public static RenderType gui() {
        return GUI;
    }

    public static RenderType guiOverlay() {
        return GUI_OVERLAY;
    }

    public static RenderType guiTexturedOverlay(ResourceLocation p_363958_) {
        return GUI_TEXTURED_OVERLAY.apply(p_363958_);
    }

    public static RenderType guiOpaqueTexturedBackground(ResourceLocation p_366506_) {
        return GUI_OPAQUE_TEXTURED_BACKGROUND.apply(p_366506_);
    }

    public static RenderType guiNauseaOverlay() {
        return GUI_NAUSEA_OVERLAY;
    }

    public static RenderType guiTextHighlight() {
        return GUI_TEXT_HIGHLIGHT;
    }

    public static RenderType guiGhostRecipeOverlay() {
        return GUI_GHOST_RECIPE_OVERLAY;
    }

    public static RenderType guiTextured(ResourceLocation p_364490_) {
        return GUI_TEXTURED.apply(p_364490_);
    }

    public static RenderType vignette(ResourceLocation p_369905_) {
        return VIGNETTE.apply(p_369905_);
    }

    public static RenderType crosshair(ResourceLocation p_361108_) {
        return CROSSHAIR.apply(p_361108_);
    }

    public static RenderType mojangLogo() {
        return MOJANG_LOGO;
    }

    public RenderType(String p_173178_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
        super(p_173178_, p_173184_, p_173185_);
        this.bufferSize = p_173181_;
        this.affectsCrumbling = p_173182_;
        this.sortOnUpload = p_173183_;
    }

    public static RenderType.CompositeRenderType create(String p_173210_, int p_173213_, RenderPipeline p_394966_, RenderType.CompositeState p_173214_) {
        return create(p_173210_, p_173213_, false, false, p_394966_, p_173214_);
    }

    public static RenderType.CompositeRenderType create(
        String p_173216_, int p_173219_, boolean p_173220_, boolean p_173221_, RenderPipeline p_393788_, RenderType.CompositeState p_173222_
    ) {
        return new RenderType.CompositeRenderType(p_173216_, p_173219_, p_173220_, p_173221_, p_393788_, p_173222_);
    }

    public abstract void draw(MeshData p_343145_);

    public abstract RenderTarget getRenderTarget();

    public abstract RenderPipeline getRenderPipeline();

    public static List<RenderType> chunkBufferLayers() {
        return CHUNK_BUFFER_LAYERS;
    }

    public int bufferSize() {
        return this.bufferSize;
    }

    public abstract VertexFormat format();

    public abstract VertexFormat.Mode mode();

    public Optional<RenderType> outline() {
        return Optional.empty();
    }

    public boolean isOutline() {
        return false;
    }

    public boolean affectsCrumbling() {
        return this.affectsCrumbling;
    }

    public boolean canConsolidateConsecutiveGeometry() {
        return !this.mode().connectedPrimitives;
    }

    public boolean sortOnUpload() {
        return this.sortOnUpload;
    }

    @OnlyIn(Dist.CLIENT)
    public static final class CompositeRenderType extends RenderType {
        static final BiFunction<ResourceLocation, Boolean, RenderType> OUTLINE = Util.memoize(
            (p_389457_, p_389458_) -> RenderType.create(
                "outline",
                1536,
                p_389458_ ? RenderPipelines.OUTLINE_CULL : RenderPipelines.OUTLINE_NO_CULL,
                RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(p_389457_, TriState.FALSE, false))
                    .setOutputState(OUTLINE_TARGET)
                    .createCompositeState(RenderType.OutlineProperty.IS_OUTLINE)
            )
        );
        private final RenderType.CompositeState state;
        private final RenderPipeline renderPipeline;
        private final Optional<RenderType> outline;
        private final boolean isOutline;

        CompositeRenderType(
            String p_173258_, int p_173261_, boolean p_173262_, boolean p_173263_, RenderPipeline p_394121_, RenderType.CompositeState p_173264_
        ) {
            super(
                p_173258_,
                p_173261_,
                p_173262_,
                p_173263_,
                () -> p_173264_.states.forEach(RenderStateShard::setupRenderState),
                () -> p_173264_.states.forEach(RenderStateShard::clearRenderState)
            );
            this.state = p_173264_;
            this.renderPipeline = p_394121_;
            this.outline = p_173264_.outlineProperty == RenderType.OutlineProperty.AFFECTS_OUTLINE
                ? p_173264_.textureState.cutoutTexture().map(p_389456_ -> OUTLINE.apply(p_389456_, p_394121_.isCull()))
                : Optional.empty();
            this.isOutline = p_173264_.outlineProperty == RenderType.OutlineProperty.IS_OUTLINE;
        }

        @Override
        public Optional<RenderType> outline() {
            return this.outline;
        }

        @Override
        public boolean isOutline() {
            return this.isOutline;
        }

        @Override
        public RenderPipeline getRenderPipeline() {
            return this.renderPipeline;
        }

        @Override
        public VertexFormat format() {
            return this.renderPipeline.getVertexFormat();
        }

        @Override
        public VertexFormat.Mode mode() {
            return this.renderPipeline.getVertexFormatMode();
        }

        @Override
        public void draw(MeshData p_397523_) {
            RenderPipeline renderpipeline = this.getRenderPipeline();
            this.setupRenderState();
            MeshData meshdata = p_397523_;

            try {
                GpuBuffer gpubuffer = renderpipeline.getVertexFormat().uploadImmediateVertexBuffer(p_397523_.vertexBuffer());
                GpuBuffer gpubuffer1;
                VertexFormat.IndexType vertexformat$indextype;
                if (p_397523_.indexBuffer() == null) {
                    RenderSystem.AutoStorageIndexBuffer rendersystem$autostorageindexbuffer = RenderSystem.getSequentialBuffer(
                        p_397523_.drawState().mode()
                    );
                    gpubuffer1 = rendersystem$autostorageindexbuffer.getBuffer(p_397523_.drawState().indexCount());
                    vertexformat$indextype = rendersystem$autostorageindexbuffer.type();
                } else {
                    gpubuffer1 = renderpipeline.getVertexFormat().uploadImmediateIndexBuffer(p_397523_.indexBuffer());
                    vertexformat$indextype = p_397523_.drawState().indexType();
                }

                RenderTarget rendertarget = this.state.outputState.getRenderTarget();

                try (RenderPass renderpass = RenderSystem.getDevice()
                        .createCommandEncoder()
                        .createRenderPass(
                            rendertarget.getColorTexture(), OptionalInt.empty(), rendertarget.useDepth ? rendertarget.getDepthTexture() : null, OptionalDouble.empty()
                        )) {
                    renderpass.setPipeline(renderpipeline);
                    renderpass.setVertexBuffer(0, gpubuffer);
                    if (RenderSystem.SCISSOR_STATE.isEnabled()) {
                        renderpass.enableScissor(RenderSystem.SCISSOR_STATE);
                    }

                    for (int i = 0; i < 12; i++) {
                        GpuTexture gputexture = RenderSystem.getShaderTexture(i);
                        if (gputexture != null) {
                            renderpass.bindSampler("Sampler" + i, gputexture);
                        }
                    }

                    renderpass.setIndexBuffer(gpubuffer1, vertexformat$indextype);
                    renderpass.drawIndexed(0, p_397523_.drawState().indexCount());
                }
            } catch (Throwable throwable2) {
                if (p_397523_ != null) {
                    try {
                        meshdata.close();
                    } catch (Throwable throwable) {
                        throwable2.addSuppressed(throwable);
                    }
                }

                throw throwable2;
            }

            if (p_397523_ != null) {
                p_397523_.close();
            }

            this.clearRenderState();
        }

        @Override
        public RenderTarget getRenderTarget() {
            return this.state.outputState.getRenderTarget();
        }

        @Override
        public String toString() {
            return "RenderType[" + this.name + ":" + this.state + "]";
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static final class CompositeState {
        final RenderStateShard.EmptyTextureStateShard textureState;
        final RenderStateShard.OutputStateShard outputState;
        final RenderType.OutlineProperty outlineProperty;
        final ImmutableList<RenderStateShard> states;

        CompositeState(
            RenderStateShard.EmptyTextureStateShard p_286632_,
            RenderStateShard.LightmapStateShard p_286744_,
            RenderStateShard.OverlayStateShard p_286754_,
            RenderStateShard.LayeringStateShard p_286895_,
            RenderStateShard.OutputStateShard p_286435_,
            RenderStateShard.TexturingStateShard p_286893_,
            RenderStateShard.LineStateShard p_286768_,
            RenderType.OutlineProperty p_286290_
        ) {
            this.textureState = p_286632_;
            this.outputState = p_286435_;
            this.outlineProperty = p_286290_;
            this.states = ImmutableList.of(p_286632_, p_286744_, p_286754_, p_286895_, p_286435_, p_286893_, p_286768_);
        }

        @Override
        public String toString() {
            return "CompositeState[" + this.states + ", outlineProperty=" + this.outlineProperty + "]";
        }

        public static RenderType.CompositeState.CompositeStateBuilder builder() {
            return new RenderType.CompositeState.CompositeStateBuilder();
        }

        @OnlyIn(Dist.CLIENT)
        public static class CompositeStateBuilder {
            private RenderStateShard.EmptyTextureStateShard textureState = RenderStateShard.NO_TEXTURE;
            private RenderStateShard.LightmapStateShard lightmapState = RenderStateShard.NO_LIGHTMAP;
            private RenderStateShard.OverlayStateShard overlayState = RenderStateShard.NO_OVERLAY;
            private RenderStateShard.LayeringStateShard layeringState = RenderStateShard.NO_LAYERING;
            private RenderStateShard.OutputStateShard outputState = RenderStateShard.MAIN_TARGET;
            private RenderStateShard.TexturingStateShard texturingState = RenderStateShard.DEFAULT_TEXTURING;
            private RenderStateShard.LineStateShard lineState = RenderStateShard.DEFAULT_LINE;

            public RenderType.CompositeState.CompositeStateBuilder setTextureState(RenderStateShard.EmptyTextureStateShard p_173291_) {
                this.textureState = p_173291_;
                return this;
            }

            public RenderType.CompositeState.CompositeStateBuilder setLightmapState(RenderStateShard.LightmapStateShard p_110672_) {
                this.lightmapState = p_110672_;
                return this;
            }

            public RenderType.CompositeState.CompositeStateBuilder setOverlayState(RenderStateShard.OverlayStateShard p_110678_) {
                this.overlayState = p_110678_;
                return this;
            }

            public RenderType.CompositeState.CompositeStateBuilder setLayeringState(RenderStateShard.LayeringStateShard p_110670_) {
                this.layeringState = p_110670_;
                return this;
            }

            public RenderType.CompositeState.CompositeStateBuilder setOutputState(RenderStateShard.OutputStateShard p_110676_) {
                this.outputState = p_110676_;
                return this;
            }

            public RenderType.CompositeState.CompositeStateBuilder setTexturingState(RenderStateShard.TexturingStateShard p_110684_) {
                this.texturingState = p_110684_;
                return this;
            }

            public RenderType.CompositeState.CompositeStateBuilder setLineState(RenderStateShard.LineStateShard p_110674_) {
                this.lineState = p_110674_;
                return this;
            }

            public RenderType.CompositeState createCompositeState(boolean p_110692_) {
                return this.createCompositeState(p_110692_ ? RenderType.OutlineProperty.AFFECTS_OUTLINE : RenderType.OutlineProperty.NONE);
            }

            public RenderType.CompositeState createCompositeState(RenderType.OutlineProperty p_110690_) {
                return new RenderType.CompositeState(
                    this.textureState, this.lightmapState, this.overlayState, this.layeringState, this.outputState, this.texturingState, this.lineState, p_110690_
                );
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected static enum OutlineProperty {
        NONE("none"),
        IS_OUTLINE("is_outline"),
        AFFECTS_OUTLINE("affects_outline");

        private final String name;

        private OutlineProperty(final String p_110702_) {
            this.name = p_110702_;
        }

        @Override
        public String toString() {
            return this.name;
        }
    }
}
