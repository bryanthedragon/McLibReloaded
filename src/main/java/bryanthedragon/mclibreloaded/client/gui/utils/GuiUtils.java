package bryanthedragon.mclibreloaded.client.gui.utils;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

/**
 * GUI utilities
 */
@OnlyIn(Dist.CLIENT)
public class GuiUtils
{
    public static void drawModel(PlayerModel model, Player player, int x, int y, float scale)
    {
        modelDrawer(model, player, x, y, scale, 1.0F, null, null);
    }

    /**
     * Draw a {@link ModelBase} without using the {@link EntityRenderDispatcher} (which 
     * adds a lot of useless transformations and stuff to the screen rendering).
     */
    public static void modelDrawer(PlayerModel model, Player player, int x, int y, float scale, float alpha, PoseStack matrices, MultiBufferSource buffer) 
    {
        float factor = 0.0625F;

        matrices.pushPose();
        matrices.translate(x, y, 50.0F);
        matrices.scale(-scale, scale, scale);
        matrices.mulPose(Axis.XP.rotationDegrees(45));
        matrices.mulPose(Axis.YP.rotationDegrees(45));
        matrices.mulPose(Axis.ZP.rotationDegrees(180));
        matrices.mulPose(Axis.YP.rotationDegrees(180));

        RenderSystem.enableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableCull();

        // Animation
        model.animateModel(player, 0, 0, player.tickCount);
        model.setAngles(player, 0, 0, player.tickCount, 0, 0);

        // Render
        VertexConsumer vertex = buffer.getBuffer(RenderType.entitySolid(player.getSkinTexture()));
        model.renderToBuffer(matrices, vertex, 15728880, OverlayTexture.NO_OVERLAY);

        matrices.popPose();
    }

    /**
     * Draw an entity on the screen.
     *
     * Taken <s>stolen</s> from minecraft's class GuiInventory. I wonder what's
     * the license of minecraft's decompiled code?
     * @param alpha 
     */
    public static void drawEntityOnScreen(int posX, int posY, float scale, LivingEntity entity, float alpha) 
    {
        PoseStack poseStack = new PoseStack();

        poseStack.translate(posX, posY, 100.0F);
        poseStack.scale(-scale, scale, scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(45.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(45.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

        Lighting.setupForEntityInInventory();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, alpha);

        // save old rotations
        float f = entity.yBodyRot;
        float f1 = entity.getYRot();
        float f2 = entity.getXRot();
        float f3 = entity.yHeadRotO;
        float f4 = entity.yHeadRot;

        entity.yBodyRot = 0;
        entity.setYRot(0);
        entity.setXRot(0);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        dispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, poseStack, bufferSource, LightTexture.FULL_BRIGHT);
        bufferSource.endBatch();

        // restore
        entity.yBodyRot = f;
        entity.setYRot(f1);
        entity.setXRot(f2);
        entity.yHeadRotO = f3;
        entity.yHeadRot = f4;

        Lighting.setupFor3DItems();
    }

    /**
     * Draw an entity on the screen.
     *
     * Taken <s>stolen</s> from minecraft's class GuiInventory. I wonder what's
     * the license of minecraft's decompiled code?
     */
    public static void drawLivingEntityOnScreen(int posX, int posY, int scale, float mouseX, float mouseY, LivingEntity entity) 
    {
        PoseStack poseStack = new PoseStack();
        poseStack.translate((double)posX, (double)posY, 50.0D);
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

        float oldBodyRot = entity.yBodyRot;
        float oldYRot = entity.getYRot();
        float oldXRot = entity.getXRot();
        float oldHeadRotO = entity.yHeadRotO;
        float oldHeadRot = entity.yHeadRot;

        entity.yBodyRot = (float)Math.atan(mouseX / 40.0F) * 20.0F;
        entity.setYRot((float)Math.atan(mouseX / 40.0F) * 40.0F);
        entity.setXRot(-((float)Math.atan(mouseY / 40.0F)) * 20.0F);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();

        Lighting.setupForEntityInInventory();

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        dispatcher.overrideCameraOrientation(new Quaternionf().rotateX((float)Math.toRadians(-15))); // optional tilt
        dispatcher.setRenderShadow(false);

        MultiBufferSource.BufferSource buffer = Minecraft.getInstance().renderBuffers().bufferSource();
        dispatcher.render(entity, 0.0D, 0.0D, 0.0D, 0.0F, poseStack, buffer, 15728880);

        buffer.endBatch();

        dispatcher.setRenderShadow(true);

        entity.yBodyRot = oldBodyRot;
        entity.setYRot(oldYRot);
        entity.setXRot(oldXRot);
        entity.yHeadRotO = oldHeadRotO;
        entity.yHeadRot = oldHeadRot;
    }

    /**
     * Open web link
     */
    public static void openWebLink(String address)
    {
        try
        {
            openWebLink(new URI(address));
        }
        catch (Exception e)
        {

        }
    }

    /**
     * Open a URL
     */
    public static void openWebLink(URI uri)
    {
        try
        {
            Class<?> clazz = Class.forName("java.awt.Desktop");
            Object object = clazz.getMethod("getDesktop", new Class[0]).invoke(null);
            clazz.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {uri});
        }
        catch (Throwable t)
        {

        }
    }

    public static void playClick() 
    {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }
    /**
     * Open a Folder<br>
     * Referenced from {@link net.minecraft.client.renderer.OpenGlHelper.openFile(File)}
     */
    public static void openFolder(String url) 
    {
        File file = new File(url);
        String osName = System.getProperty("os.name").toLowerCase();

        try 
        {
            if (osName.contains("win")) 
            {
                // Windows
                Runtime.getRuntime().exec(new String[] {"cmd.exe", "/C", "start", "\"Open file\"", file.getAbsolutePath()});
            } 
            else if (osName.contains("mac")) 
            {
                // macOS
                Runtime.getRuntime().exec(new String[] {"/usr/bin/open", file.getAbsolutePath()});
            } 
            else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) 
            {
                // Linux / Unix
                Runtime.getRuntime().exec(new String[] {"xdg-open", file.getAbsolutePath()});
            }
            else 
            {
                System.out.println("Unsupported OS: " + osName);
            }
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
    }
}