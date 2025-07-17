package net.minecraft.client.renderer;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.google.common.collect.ImmutableList.Builder;
import com.mojang.blaze3d.framegraph.FrameGraphBuilder;
import com.mojang.blaze3d.pipeline.CompiledRenderPipeline;
import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.resource.GraphicsResourceAllocator;
import com.mojang.blaze3d.resource.RenderTargetDescriptor;
import com.mojang.blaze3d.resource.ResourceHandle;
import com.mojang.blaze3d.shaders.UniformType;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class PostChain {
    public static final ResourceLocation MAIN_TARGET_ID = ResourceLocation.withDefaultNamespace("main");
    private final List<PostPass> passes;
    private final Map<ResourceLocation, PostChainConfig.InternalTarget> internalTargets;
    private final Set<ResourceLocation> externalTargets;

    private PostChain(List<PostPass> p_368125_, Map<ResourceLocation, PostChainConfig.InternalTarget> p_365904_, Set<ResourceLocation> p_364283_) {
        this.passes = p_368125_;
        this.internalTargets = p_365904_;
        this.externalTargets = p_364283_;
    }

    public static PostChain load(PostChainConfig p_361031_, TextureManager p_110034_, Set<ResourceLocation> p_370027_, ResourceLocation p_395867_) throws ShaderManager.CompilationException {
        Stream<ResourceLocation> stream = p_361031_.passes().stream().flatMap(PostChainConfig.Pass::referencedTargets);
        Set<ResourceLocation> set = stream.filter(p_357871_ -> !p_361031_.internalTargets().containsKey(p_357871_)).collect(Collectors.toSet());
        Set<ResourceLocation> set1 = Sets.difference(set, p_370027_);
        if (!set1.isEmpty()) {
            throw new ShaderManager.CompilationException("Referenced external targets are not available in this context: " + set1);
        } else {
            Builder<PostPass> builder = ImmutableList.builder();

            for (int i = 0; i < p_361031_.passes().size(); i++) {
                PostChainConfig.Pass postchainconfig$pass = p_361031_.passes().get(i);
                builder.add(createPass(p_110034_, postchainconfig$pass, p_395867_.withSuffix("/" + i)));
            }

            return new PostChain(builder.build(), p_361031_.internalTargets(), set);
        }
    }

    private static PostPass createPass(TextureManager p_366006_, PostChainConfig.Pass p_368358_, ResourceLocation p_396862_) throws ShaderManager.CompilationException {
        RenderPipeline.Builder renderpipeline$builder = RenderPipeline.builder(RenderPipelines.POST_PROCESSING_SNIPPET)
            .withFragmentShader(p_368358_.fragmentShaderId())
            .withVertexShader(p_368358_.vertexShaderId())
            .withLocation(p_396862_);

        for (PostChainConfig.Input postchainconfig$input : p_368358_.inputs()) {
            renderpipeline$builder.withSampler(postchainconfig$input.samplerName() + "Sampler");
            renderpipeline$builder.withUniform(postchainconfig$input.samplerName() + "Size", UniformType.VEC2);
        }

        for (PostChainConfig.Uniform postchainconfig$uniform1 : p_368358_.uniforms()) {
            renderpipeline$builder.withUniform(
                postchainconfig$uniform1.name(), Objects.requireNonNull(UniformType.CODEC.byName(postchainconfig$uniform1.type()))
            );
        }

        RenderPipeline renderpipeline = renderpipeline$builder.build();
        CompiledRenderPipeline compiledrenderpipeline = RenderSystem.getDevice().precompilePipeline(renderpipeline);

        for (PostChainConfig.Uniform postchainconfig$uniform : p_368358_.uniforms()) {
            String s = postchainconfig$uniform.name();
            if (!compiledrenderpipeline.containsUniform(s)) {
                throw new ShaderManager.CompilationException("Uniform '" + s + "' does not exist for " + p_396862_);
            }
        }

        PostPass postpass = new PostPass(renderpipeline, p_368358_.outputTarget(), p_368358_.uniforms());

        for (PostChainConfig.Input postchainconfig$input1 : p_368358_.inputs()) {
            switch (postchainconfig$input1) {
                case PostChainConfig.TextureInput(String s2, ResourceLocation resourcelocation, int i, int j, boolean flag):
                    AbstractTexture abstracttexture = p_366006_.getTexture(resourcelocation.withPath(p_357869_ -> "textures/effect/" + p_357869_ + ".png"));
                    abstracttexture.setFilter(flag, false);
                    postpass.addInput(new PostPass.TextureInput(s2, abstracttexture, i, j));
                    break;
                case PostChainConfig.TargetInput(String s1, ResourceLocation resourcelocation1, boolean flag1, boolean flag2):
                    postpass.addInput(new PostPass.TargetInput(s1, resourcelocation1, flag1, flag2));
                    break;
                default:
                    throw new MatchException(null, null);
            }
        }

        return postpass;
    }

    public void addToFrame(FrameGraphBuilder p_362816_, int p_365028_, int p_368108_, PostChain.TargetBundle p_366403_, @Nullable Consumer<RenderPass> p_392831_) {
        Matrix4f matrix4f = new Matrix4f().setOrtho(0.0F, p_365028_, 0.0F, p_368108_, 0.1F, 1000.0F);
        Map<ResourceLocation, ResourceHandle<RenderTarget>> map = new HashMap<>(this.internalTargets.size() + this.externalTargets.size());

        for (ResourceLocation resourcelocation : this.externalTargets) {
            map.put(resourcelocation, p_366403_.getOrThrow(resourcelocation));
        }

        for (Entry<ResourceLocation, PostChainConfig.InternalTarget> entry : this.internalTargets.entrySet()) {
            ResourceLocation resourcelocation1 = entry.getKey();

            RenderTargetDescriptor rendertargetdescriptor = switch ((PostChainConfig.InternalTarget)entry.getValue()) {
                case PostChainConfig.FixedSizedTarget(int i, int j) -> new RenderTargetDescriptor(i, j, true, 0);
                case PostChainConfig.FullScreenTarget postchainconfig$fullscreentarget -> new RenderTargetDescriptor(p_365028_, p_368108_, true, 0);
                default -> throw new MatchException(null, null);
            };
            map.put(resourcelocation1, p_362816_.createInternal(resourcelocation1.toString(), rendertargetdescriptor));
        }

        for (PostPass postpass : this.passes) {
            postpass.addToFrame(p_362816_, map, matrix4f, p_392831_);
        }

        for (ResourceLocation resourcelocation2 : this.externalTargets) {
            p_366403_.replace(resourcelocation2, map.get(resourcelocation2));
        }
    }

    @Deprecated
    public void process(RenderTarget p_367570_, GraphicsResourceAllocator p_362918_, @Nullable Consumer<RenderPass> p_394757_) {
        FrameGraphBuilder framegraphbuilder = new FrameGraphBuilder();
        PostChain.TargetBundle postchain$targetbundle = PostChain.TargetBundle.of(MAIN_TARGET_ID, framegraphbuilder.importExternal("main", p_367570_));
        this.addToFrame(framegraphbuilder, p_367570_.width, p_367570_.height, postchain$targetbundle, p_394757_);
        framegraphbuilder.execute(p_362918_);
    }

    @OnlyIn(Dist.CLIENT)
    public interface TargetBundle {
        static PostChain.TargetBundle of(final ResourceLocation p_366117_, final ResourceHandle<RenderTarget> p_367685_) {
            return new PostChain.TargetBundle() {
                private ResourceHandle<RenderTarget> handle = p_367685_;

                @Override
                public void replace(ResourceLocation p_368607_, ResourceHandle<RenderTarget> p_369595_) {
                    if (p_368607_.equals(p_366117_)) {
                        this.handle = p_369595_;
                    } else {
                        throw new IllegalArgumentException("No target with id " + p_368607_);
                    }
                }

                @Nullable
                @Override
                public ResourceHandle<RenderTarget> get(ResourceLocation p_364302_) {
                    return p_364302_.equals(p_366117_) ? this.handle : null;
                }
            };
        }

        void replace(ResourceLocation p_369680_, ResourceHandle<RenderTarget> p_364990_);

        @Nullable
        ResourceHandle<RenderTarget> get(ResourceLocation p_365511_);

        default ResourceHandle<RenderTarget> getOrThrow(ResourceLocation p_364229_) {
            ResourceHandle<RenderTarget> resourcehandle = this.get(p_364229_);
            if (resourcehandle == null) {
                throw new IllegalArgumentException("Missing target with id " + p_364229_);
            } else {
                return resourcehandle;
            }
        }
    }
}