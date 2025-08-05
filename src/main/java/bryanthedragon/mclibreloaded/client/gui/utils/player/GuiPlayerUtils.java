package bryanthedragon.mclibreloaded.client.gui.utils.player;

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
public class GuiPlayerUtils
{
    public static void drawPlayerModel(PlayerModel model, Player player, int x, int y, float scale)
    {
        playerModelDrawer(model, player, x, y, scale, 1.0F, null, null);
    }

    /**
     * Draw a {@link ModelBase} without using the {@link EntityRenderDispatcher} (which 
     * adds a lot of useless transformations and stuff to the screen rendering).
     */
    public static void playerModelDrawer(PlayerModel model, Player player, int x, int y, float scale, float alpha, PoseStack matrices, MultiBufferSource buffer) 
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
     * Open web link
     */
    public static void openWebLinkAddress(String address)
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