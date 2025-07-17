package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.TriState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;
import org.joml.Matrix4fStack;

@OnlyIn(Dist.CLIENT)
public abstract class RenderStateShard {
    public static final double MAX_ENCHANTMENT_GLINT_SPEED_MILLIS = 8.0;
    protected final String name;
    protected Runnable setupState;
    private final Runnable clearState;
    public static final RenderStateShard.TextureStateShard BLOCK_SHEET_MIPPED = new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, TriState.FALSE, true);
    public static final RenderStateShard.TextureStateShard BLOCK_SHEET = new RenderStateShard.TextureStateShard(TextureAtlas.LOCATION_BLOCKS, TriState.FALSE, false);
    public static final RenderStateShard.EmptyTextureStateShard NO_TEXTURE = new RenderStateShard.EmptyTextureStateShard();
    public static final RenderStateShard.TexturingStateShard DEFAULT_TEXTURING = new RenderStateShard.TexturingStateShard("default_texturing", () -> {}, () -> {});
    public static final RenderStateShard.TexturingStateShard GLINT_TEXTURING = new RenderStateShard.TexturingStateShard(
        "glint_texturing", () -> setupGlintTexturing(8.0F), RenderSystem::resetTextureMatrix
    );
    public static final RenderStateShard.TexturingStateShard ENTITY_GLINT_TEXTURING = new RenderStateShard.TexturingStateShard(
        "entity_glint_texturing", () -> setupGlintTexturing(0.5F), RenderSystem::resetTextureMatrix
    );
    public static final RenderStateShard.TexturingStateShard ARMOR_ENTITY_GLINT_TEXTURING = new RenderStateShard.TexturingStateShard(
        "armor_entity_glint_texturing", () -> setupGlintTexturing(0.16F), RenderSystem::resetTextureMatrix
    );
    public static final RenderStateShard.LightmapStateShard LIGHTMAP = new RenderStateShard.LightmapStateShard(true);
    public static final RenderStateShard.LightmapStateShard NO_LIGHTMAP = new RenderStateShard.LightmapStateShard(false);
    public static final RenderStateShard.OverlayStateShard OVERLAY = new RenderStateShard.OverlayStateShard(true);
    public static final RenderStateShard.OverlayStateShard NO_OVERLAY = new RenderStateShard.OverlayStateShard(false);
    public static final RenderStateShard.LayeringStateShard NO_LAYERING = new RenderStateShard.LayeringStateShard("no_layering", () -> {}, () -> {});
    public static final RenderStateShard.LayeringStateShard VIEW_OFFSET_Z_LAYERING = new RenderStateShard.LayeringStateShard("view_offset_z_layering", () -> {
        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
        matrix4fstack.pushMatrix();
        RenderSystem.getProjectionType().applyLayeringTransform(matrix4fstack, 1.0F);
    }, () -> {
        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
        matrix4fstack.popMatrix();
    });
    public static final RenderStateShard.LayeringStateShard VIEW_OFFSET_Z_LAYERING_FORWARD = new RenderStateShard.LayeringStateShard("view_offset_z_layering_forward", () -> {
        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
        matrix4fstack.pushMatrix();
        RenderSystem.getProjectionType().applyLayeringTransform(matrix4fstack, -1.0F);
    }, () -> {
        Matrix4fStack matrix4fstack = RenderSystem.getModelViewStack();
        matrix4fstack.popMatrix();
    });
    public static final RenderStateShard.OutputStateShard MAIN_TARGET = new RenderStateShard.OutputStateShard(
        "main_target", () -> Minecraft.getInstance().getMainRenderTarget()
    );
    public static final RenderStateShard.OutputStateShard OUTLINE_TARGET = new RenderStateShard.OutputStateShard("outline_target", () -> {
        RenderTarget rendertarget = Minecraft.getInstance().levelRenderer.entityOutlineTarget();
        return rendertarget != null ? rendertarget : Minecraft.getInstance().getMainRenderTarget();
    });
    public static final RenderStateShard.OutputStateShard TRANSLUCENT_TARGET = new RenderStateShard.OutputStateShard("translucent_target", () -> {
        RenderTarget rendertarget = Minecraft.getInstance().levelRenderer.getTranslucentTarget();
        return rendertarget != null ? rendertarget : Minecraft.getInstance().getMainRenderTarget();
    });
    public static final RenderStateShard.OutputStateShard PARTICLES_TARGET = new RenderStateShard.OutputStateShard("particles_target", () -> {
        RenderTarget rendertarget = Minecraft.getInstance().levelRenderer.getParticlesTarget();
        return rendertarget != null ? rendertarget : Minecraft.getInstance().getMainRenderTarget();
    });
    public static final RenderStateShard.OutputStateShard WEATHER_TARGET = new RenderStateShard.OutputStateShard("weather_target", () -> {
        RenderTarget rendertarget = Minecraft.getInstance().levelRenderer.getWeatherTarget();
        return rendertarget != null ? rendertarget : Minecraft.getInstance().getMainRenderTarget();
    });
    public static final RenderStateShard.OutputStateShard ITEM_ENTITY_TARGET = new RenderStateShard.OutputStateShard("item_entity_target", () -> {
        RenderTarget rendertarget = Minecraft.getInstance().levelRenderer.getItemEntityTarget();
        return rendertarget != null ? rendertarget : Minecraft.getInstance().getMainRenderTarget();
    });
    public static final RenderStateShard.LineStateShard DEFAULT_LINE = new RenderStateShard.LineStateShard(OptionalDouble.of(1.0));

    public RenderStateShard(String p_110161_, Runnable p_110162_, Runnable p_110163_) {
        this.name = p_110161_;
        this.setupState = p_110162_;
        this.clearState = p_110163_;
    }

    public void setupRenderState() {
        this.setupState.run();
    }

    public void clearRenderState() {
        this.clearState.run();
    }

    @Override
    public String toString() {
        return this.name;
    }

    public String getName() {
        return this.name;
    }

    private static void setupGlintTexturing(float p_110187_) {
        long i = (long)(Util.getMillis() * Minecraft.getInstance().options.glintSpeed().get() * 8.0);
        float f = (float)(i % 110000L) / 110000.0F;
        float f1 = (float)(i % 30000L) / 30000.0F;
        Matrix4f matrix4f = new Matrix4f().translation(-f, f1, 0.0F);
        matrix4f.rotateZ((float) (Math.PI / 18)).scale(p_110187_);
        RenderSystem.setTextureMatrix(matrix4f);
    }

    @OnlyIn(Dist.CLIENT)
    public static class BooleanStateShard extends RenderStateShard {
        private final boolean enabled;

        public BooleanStateShard(String p_110229_, Runnable p_110230_, Runnable p_110231_, boolean p_110232_) {
            super(p_110229_, p_110230_, p_110231_);
            this.enabled = p_110232_;
        }

        @Override
        public String toString() {
            return this.name + "[" + this.enabled + "]";
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class EmptyTextureStateShard extends RenderStateShard {
        public EmptyTextureStateShard(Runnable p_173117_, Runnable p_173118_) {
            super("texture", p_173117_, p_173118_);
        }

        EmptyTextureStateShard() {
            super("texture", () -> {}, () -> {});
        }

        protected Optional<ResourceLocation> cutoutTexture() {
            return Optional.empty();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class LayeringStateShard extends RenderStateShard {
        public LayeringStateShard(String p_110267_, Runnable p_110268_, Runnable p_110269_) {
            super(p_110267_, p_110268_, p_110269_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class LightmapStateShard extends RenderStateShard.BooleanStateShard {
        public LightmapStateShard(boolean p_110271_) {
            super("lightmap", () -> {
                if (p_110271_) {
                    Minecraft.getInstance().gameRenderer.lightTexture().turnOnLightLayer();
                }
            }, () -> {
                if (p_110271_) {
                    Minecraft.getInstance().gameRenderer.lightTexture().turnOffLightLayer();
                }
            }, p_110271_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    protected static class LineStateShard extends RenderStateShard {
        private final OptionalDouble width;

        public LineStateShard(OptionalDouble p_110278_) {
            super("line_width", () -> {
                if (!Objects.equals(p_110278_, OptionalDouble.of(1.0))) {
                    if (p_110278_.isPresent()) {
                        RenderSystem.lineWidth((float)p_110278_.getAsDouble());
                    } else {
                        RenderSystem.lineWidth(Math.max(2.5F, Minecraft.getInstance().getWindow().getWidth() / 1920.0F * 2.5F));
                    }
                }
            }, () -> {
                if (!Objects.equals(p_110278_, OptionalDouble.of(1.0))) {
                    RenderSystem.lineWidth(1.0F);
                }
            });
            this.width = p_110278_;
        }

        @Override
        public String toString() {
            return this.name + "[" + (this.width.isPresent() ? this.width.getAsDouble() : "window_scale") + "]";
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class MultiTextureStateShard extends RenderStateShard.EmptyTextureStateShard {
        private final Optional<ResourceLocation> cutoutTexture;

        MultiTextureStateShard(List<RenderStateShard.MultiTextureStateShard.Entry> p_376716_) {
            super(() -> {
                for (int i = 0; i < p_376716_.size(); i++) {
                    RenderStateShard.MultiTextureStateShard.Entry renderstateshard$multitexturestateshard$entry = p_376716_.get(i);
                    TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
                    AbstractTexture abstracttexture = texturemanager.getTexture(renderstateshard$multitexturestateshard$entry.id);
                    abstracttexture.setFilter(renderstateshard$multitexturestateshard$entry.blur, renderstateshard$multitexturestateshard$entry.mipmap);
                    RenderSystem.setShaderTexture(i, abstracttexture.getTexture());
                }
            }, () -> {});
            this.cutoutTexture = p_376716_.isEmpty() ? Optional.empty() : Optional.of(p_376716_.getFirst().id);
        }

        @Override
        protected Optional<ResourceLocation> cutoutTexture() {
            return this.cutoutTexture;
        }

        public static RenderStateShard.MultiTextureStateShard.Builder builder() {
            return new RenderStateShard.MultiTextureStateShard.Builder();
        }

        @OnlyIn(Dist.CLIENT)
        public static final class Builder {
            private final ImmutableList.Builder<RenderStateShard.MultiTextureStateShard.Entry> builder = new ImmutableList.Builder<>();

            public RenderStateShard.MultiTextureStateShard.Builder add(ResourceLocation p_173133_, boolean p_173134_, boolean p_173135_) {
                this.builder.add(new RenderStateShard.MultiTextureStateShard.Entry(p_173133_, p_173134_, p_173135_));
                return this;
            }

            public RenderStateShard.MultiTextureStateShard build() {
                return new RenderStateShard.MultiTextureStateShard(this.builder.build());
            }
        }

        @OnlyIn(Dist.CLIENT)
        record Entry(ResourceLocation id, boolean blur, boolean mipmap) {
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static final class OffsetTexturingStateShard extends RenderStateShard.TexturingStateShard {
        public OffsetTexturingStateShard(float p_110290_, float p_110291_) {
            super(
                "offset_texturing",
                () -> RenderSystem.setTextureMatrix(new Matrix4f().translation(p_110290_, p_110291_, 0.0F)),
                () -> RenderSystem.resetTextureMatrix()
            );
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class OutputStateShard extends RenderStateShard {
        private final Supplier<RenderTarget> renderTargetSupplier;

        public OutputStateShard(String p_110300_, Supplier<RenderTarget> p_393533_) {
            super(p_110300_, () -> {}, () -> {});
            this.renderTargetSupplier = p_393533_;
        }

        public RenderTarget getRenderTarget() {
            return this.renderTargetSupplier.get();
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class OverlayStateShard extends RenderStateShard.BooleanStateShard {
        public OverlayStateShard(boolean p_110304_) {
            super("overlay", () -> {
                if (p_110304_) {
                    Minecraft.getInstance().gameRenderer.overlayTexture().setupOverlayColor();
                }
            }, () -> {
                if (p_110304_) {
                    Minecraft.getInstance().gameRenderer.overlayTexture().teardownOverlayColor();
                }
            }, p_110304_);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class TextureStateShard extends RenderStateShard.EmptyTextureStateShard {
        private final Optional<ResourceLocation> texture;
        protected TriState blur;
        protected boolean mipmap;

        public TextureStateShard(ResourceLocation p_110333_, TriState p_369785_, boolean p_110334_) {
            super(() -> {
                TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
                AbstractTexture abstracttexture = texturemanager.getTexture(p_110333_);
                abstracttexture.setFilter(p_369785_, p_110334_);
                RenderSystem.setShaderTexture(0, abstracttexture.getTexture());
            }, () -> {});
            this.texture = Optional.of(p_110333_);
            this.blur = p_369785_;
            this.mipmap = p_110334_;
        }

        @Override
        public String toString() {
            return this.name + "[" + this.texture + "(blur=" + this.blur + ", mipmap=" + this.mipmap + ")]";
        }

        @Override
        protected Optional<ResourceLocation> cutoutTexture() {
            return this.texture;
        }
    }

    @OnlyIn(Dist.CLIENT)
    public static class TexturingStateShard extends RenderStateShard {
        public TexturingStateShard(String p_110349_, Runnable p_110350_, Runnable p_110351_) {
            super(p_110349_, p_110350_, p_110351_);
        }
    }
}