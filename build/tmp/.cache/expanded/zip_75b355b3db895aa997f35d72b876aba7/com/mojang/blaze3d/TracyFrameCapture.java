package com.mojang.blaze3d;

import com.mojang.blaze3d.buffers.BufferType;
import com.mojang.blaze3d.buffers.BufferUsage;
import com.mojang.blaze3d.buffers.GpuBuffer;
import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.systems.CommandEncoder;
import com.mojang.blaze3d.systems.RenderPass;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;
import com.mojang.blaze3d.textures.TextureFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.mojang.jtracy.TracyClient;
import java.util.OptionalInt;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TracyFrameCapture implements AutoCloseable {
    private static final int MAX_WIDTH = 320;
    private static final int MAX_HEIGHT = 180;
    private static final int BYTES_PER_PIXEL = 4;
    private int targetWidth;
    private int targetHeight;
    private int width;
    private int height;
    @Nullable
    private GpuTexture frameBuffer;
    @Nullable
    private GpuBuffer pixelbuffer;
    private int lastCaptureDelay;
    private boolean capturedThisFrame;
    private TracyFrameCapture.Status status = TracyFrameCapture.Status.WAITING_FOR_CAPTURE;

    private void resize(int p_361808_, int p_365044_) {
        float f = (float)p_361808_ / p_365044_;
        if (p_361808_ > 320) {
            p_361808_ = 320;
            p_365044_ = (int)(320.0F / f);
        }

        if (p_365044_ > 180) {
            p_361808_ = (int)(180.0F * f);
            p_365044_ = 180;
        }

        p_361808_ = p_361808_ / 4 * 4;
        p_365044_ = p_365044_ / 4 * 4;
        if (this.width != p_361808_ || this.height != p_365044_) {
            this.width = p_361808_;
            this.height = p_365044_;
            if (this.frameBuffer != null) {
                this.frameBuffer.close();
            }

            this.frameBuffer = RenderSystem.getDevice().createTexture("Tracy Frame Capture", TextureFormat.RGBA8, p_361808_, p_365044_, 1);
            if (this.pixelbuffer != null) {
                this.pixelbuffer.close();
            }

            this.pixelbuffer = RenderSystem.getDevice()
                .createBuffer(() -> "Tracy Frame Capture buffer", BufferType.PIXEL_PACK, BufferUsage.STREAM_READ, p_361808_ * p_365044_ * 4);
        }
    }

    public void capture(RenderTarget p_367460_) {
        if (this.status == TracyFrameCapture.Status.WAITING_FOR_CAPTURE
            && !this.capturedThisFrame
            && p_367460_.getColorTexture() != null
            && this.pixelbuffer != null
            && this.frameBuffer != null) {
            this.capturedThisFrame = true;
            if (p_367460_.width != this.targetWidth || p_367460_.height != this.targetHeight) {
                this.targetWidth = p_367460_.width;
                this.targetHeight = p_367460_.height;
                this.resize(this.targetWidth, this.targetHeight);
            }

            this.status = TracyFrameCapture.Status.WAITING_FOR_COPY;
            CommandEncoder commandencoder = RenderSystem.getDevice().createCommandEncoder();
            RenderSystem.AutoStorageIndexBuffer rendersystem$autostorageindexbuffer = RenderSystem.getSequentialBuffer(VertexFormat.Mode.QUADS);
            GpuBuffer gpubuffer = rendersystem$autostorageindexbuffer.getBuffer(6);

            try (RenderPass renderpass = RenderSystem.getDevice().createCommandEncoder().createRenderPass(this.frameBuffer, OptionalInt.empty())) {
                renderpass.setPipeline(RenderPipelines.TRACY_BLIT);
                renderpass.setVertexBuffer(0, RenderSystem.getQuadVertexBuffer());
                renderpass.setIndexBuffer(gpubuffer, rendersystem$autostorageindexbuffer.type());
                renderpass.bindSampler("InSampler", p_367460_.getColorTexture());
                renderpass.drawIndexed(0, 6);
            }

            commandencoder.copyTextureToBuffer(this.frameBuffer, this.pixelbuffer, 0, () -> this.status = TracyFrameCapture.Status.WAITING_FOR_UPLOAD, 0);
            this.lastCaptureDelay = 0;
        }
    }

    public void upload() {
        if (this.status == TracyFrameCapture.Status.WAITING_FOR_UPLOAD && this.pixelbuffer != null) {
            this.status = TracyFrameCapture.Status.WAITING_FOR_CAPTURE;

            try (GpuBuffer.ReadView gpubuffer$readview = RenderSystem.getDevice().createCommandEncoder().readBuffer(this.pixelbuffer)) {
                TracyClient.frameImage(gpubuffer$readview.data(), this.width, this.height, this.lastCaptureDelay, true);
            }
        }
    }

    public void endFrame() {
        this.lastCaptureDelay++;
        this.capturedThisFrame = false;
        TracyClient.markFrame();
    }

    @Override
    public void close() {
        if (this.frameBuffer != null) {
            this.frameBuffer.close();
        }

        if (this.pixelbuffer != null) {
            this.pixelbuffer.close();
        }
    }

    @OnlyIn(Dist.CLIENT)
    static enum Status {
        WAITING_FOR_CAPTURE,
        WAITING_FOR_COPY,
        WAITING_FOR_UPLOAD;
    }
}