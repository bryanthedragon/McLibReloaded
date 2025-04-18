package com.mojang.blaze3d.opengl;

import com.mojang.blaze3d.textures.AddressMode;
import com.mojang.blaze3d.textures.FilterMode;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import javax.annotation.Nullable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GlTexture extends GpuTexture {
    protected final int id;
    private final Int2IntMap fboCache = new Int2IntOpenHashMap();
    protected boolean closed;
    protected boolean modesDirty = true;

    protected GlTexture(String p_393950_, TextureFormat p_392837_, int p_394590_, int p_391379_, int p_391947_, int p_396659_) {
        this(p_393950_, p_392837_, p_394590_, p_391379_, p_391947_, p_396659_, false);
    }

    protected GlTexture(String p_393950_, TextureFormat p_392837_, int p_394590_, int p_391379_, int p_391947_, int p_396659_, boolean stencil) {
        super(p_393950_, p_392837_, p_394590_, p_391379_, p_391947_);
        this.id = p_396659_;
        this.stencilEnabled = stencil;
    }

    @Override
    public void close() {
        if (!this.closed) {
            this.closed = true;
            GlStateManager._deleteTexture(this.id);

            for (int i : this.fboCache.values()) {
                GlStateManager._glDeleteFramebuffers(i);
            }
        }
    }

    @Override
    public boolean isClosed() {
        return this.closed;
    }

    public int getFbo(DirectStateAccess p_393100_, @Nullable GpuTexture p_394451_) {
        int i = p_394451_ == null ? 0 : ((GlTexture)p_394451_).id;
        return this.fboCache.computeIfAbsent(i, p_393965_ -> {
            int j = p_393100_.createFrameBufferObject();
            p_393100_.bindFrameBufferTextures(j, this.id, i, 0, 0);
            return j;
        });
    }

    public void flushModeChanges() {
        if (this.modesDirty) {
            GlStateManager._texParameter(3553, 10242, GlConst.toGl(this.addressModeU));
            GlStateManager._texParameter(3553, 10243, GlConst.toGl(this.addressModeV));
            switch (this.minFilter) {
                case NEAREST:
                    GlStateManager._texParameter(3553, 10241, this.useMipmaps ? 9986 : 9728);
                    break;
                case LINEAR:
                    GlStateManager._texParameter(3553, 10241, this.useMipmaps ? 9987 : 9729);
            }

            switch (this.magFilter) {
                case NEAREST:
                    GlStateManager._texParameter(3553, 10240, 9728);
                    break;
                case LINEAR:
                    GlStateManager._texParameter(3553, 10240, 9729);
            }

            this.modesDirty = false;
        }
    }

    public int glId() {
        return this.id;
    }

    @Override
    public void setAddressMode(AddressMode p_393262_, AddressMode p_392605_) {
        super.setAddressMode(p_393262_, p_392605_);
        this.modesDirty = true;
    }

    @Override
    public void setTextureFilter(FilterMode p_394977_, FilterMode p_397175_, boolean p_393558_) {
        super.setTextureFilter(p_394977_, p_397175_, p_393558_);
        this.modesDirty = true;
    }


    /* FORGE START */

    private final boolean stencilEnabled;

    @Override
    public boolean isStencilEnabled() {
        return this.stencilEnabled;
    }
}
