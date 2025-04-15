package net.minecraftforge.client.extensions;

import com.mojang.blaze3d.textures.GpuTexture;

public interface IForgeGpuTexture {
    @SuppressWarnings("unused")
    private GpuTexture self() {
        return (GpuTexture) this;
    }

    default boolean isStencilEnabled() {
        return false;
    }
}
