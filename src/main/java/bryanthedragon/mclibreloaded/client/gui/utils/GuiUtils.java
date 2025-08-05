package bryanthedragon.mclibreloaded.client.gui.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.api.distmarker.Dist;

import java.io.File;
import java.io.IOException;
import java.net.URI;

/**
 * GUI utilities
 */
@OnlyIn(Dist.CLIENT)
public class GuiUtils
{
    public static void drawModel(ModelBase model, Player player, int x, int y, float scale)
    {
        drawModel(model, player, x, y, scale, 1.0F);
    }

    /**
     * Draw a {@link ModelBase} without using the {@link EntityRenderDispatcher} (which 
     * adds a lot of useless transformations and stuff to the screen rendering).
     */
    public static void drawModel(ModelBase model, Player player, int x, int y, float scale, float alpha)
    {
        float factor = 0.0625F;

        RenderSystem.enableColorMaterial();
        RenderSystem.pushMatrix();
        RenderSystem.translate(x, y, 50.0F);
        RenderSystem.scale((-scale), scale, scale);
        RenderSystem.rotate(45.0F, -1.0F, 0.0F, 0.0F);
        RenderSystem.rotate(45.0F, 0.0F, -1.0F, 0.0F);
        RenderSystem.rotate(180.0F, 0.0F, 0.0F, 1.0F);
        RenderSystem.rotate(180.0F, 0.0F, 1.0F, 0.0F);

        RenderHelper.enableStandardItemLighting();

        RenderSystem.pushMatrix();
        RenderSystem.disableCull();

        RenderSystem.enableRescaleNormal();
        RenderSystem.scale(-1.0F, -1.0F, 1.0F);
        RenderSystem.translate(0.0F, -1.501F, 0.0F);

        RenderSystem.enableAlpha();

        model.setLivingAnimations(player, 0, 0, 0);
        model.setRotationAngles(0, 0, player.ticksExisted, 0, 0, factor, player);

        RenderSystem.enableDepth();
        RenderSystem.color(1.0F, 1.0F, 1.0F, alpha);

        model.render(player, 0, 0, 0, 0, 0, factor);

        RenderSystem.disableDepth();

        RenderSystem.disableRescaleNormal();
        RenderSystem.disableAlpha();
        RenderSystem.popMatrix();

        RenderSystem.popMatrix();

        RenderHelper.disableStandardItemLighting();

        RenderSystem.disableRescaleNormal();
        RenderSystem.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        RenderSystem.disableTexture2D();
        RenderSystem.setActiveTexture(OpenGlHelper.defaultTexUnit);
    }

    /**
     * Draw an entity on the screen.
     *
     * Taken <s>stolen</s> from minecraft's class GuiInventory. I wonder what's
     * the license of minecraft's decompiled code?
     * @param alpha 
     */
    public static void drawEntityOnScreen(int posX, int posY, float scale, EntityLivingBase ent, float alpha)
    {
        RenderSystem.enableDepth();
        RenderSystem.disableBlend();
        RenderSystem.enableColorMaterial();
        RenderSystem.pushMatrix();
        RenderSystem.translate(posX, posY, 100.0F);
        RenderSystem.scale((-scale), scale, scale);
        RenderSystem.rotate(45.0F, -1.0F, 0.0F, 0.0F);
        RenderSystem.rotate(45.0F, 0.0F, -1.0F, 0.0F);
        RenderSystem.rotate(180.0F, 0.0F, 0.0F, 1.0F);

        boolean render = ent.getAlwaysRenderNameTag();

        if (ent instanceof EntityDragon)
        {
            RenderSystem.rotate(180.0F, 0.0F, 1.0F, 0.0F);
        }

        RenderHelper.enableStandardItemLighting();

        RenderSystem.enableRescaleNormal();
        RenderSystem.color(1.0F, 1.0F, 1.0F, alpha);

        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;

        ent.renderYawOffset = 0;
        ent.rotationYaw = 0;
        ent.rotationPitch = 0;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;
        ent.setAlwaysRenderNameTag(false);

        RenderSystem.translate(0.0F, 0.0F, 0.0F);

        EntityRenderDispatcher EntityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderDispatcher.setPlayerViewY(180.0F);
        EntityRenderDispatcher.setRenderShadow(false);
        EntityRenderDispatcher.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        EntityRenderDispatcher.setRenderShadow(true);

        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;

        ent.setAlwaysRenderNameTag(render);

        RenderSystem.popMatrix();

        RenderHelper.disableStandardItemLighting();

        RenderSystem.disableRescaleNormal();

        RenderSystem.disableBlend();
        RenderSystem.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        RenderSystem.disableTexture2D();
        RenderSystem.setActiveTexture(OpenGlHelper.defaultTexUnit);
        RenderSystem.disableDepth();
    }

    /**
     * Draw an entity on the screen.
     *
     * Taken <s>stolen</s> from minecraft's class GuiInventory. I wonder what's
     * the license of minecraft's decompiled code?
     */
    public static void drawEntityOnScreen(int posX, int posY, int scale, int mouseX, int mouseY, EntityLivingBase ent)
    {
        RenderSystem.enableColorMaterial();
        RenderSystem.pushMatrix();
        RenderSystem.translate(posX, posY, 100.0F);
        RenderSystem.scale((-scale), scale, scale);
        RenderSystem.rotate(180.0F, 0.0F, 0.0F, 1.0F);

        float f = ent.renderYawOffset;
        float f1 = ent.rotationYaw;
        float f2 = ent.rotationPitch;
        float f3 = ent.prevRotationYawHead;
        float f4 = ent.rotationYawHead;

        ent.renderYawOffset = (float) Math.atan(mouseX / 40.0F) * 20.0F;
        ent.rotationYaw = (float) Math.atan(mouseX / 40.0F) * 40.0F;
        ent.rotationPitch = -((float) Math.atan(mouseY / 40.0F)) * 20.0F;
        ent.rotationYawHead = ent.rotationYaw;
        ent.prevRotationYawHead = ent.rotationYaw;

        RenderSystem.translate(0.0F, 0.0F, 0.0F);

        EntityRenderDispatcher EntityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderDispatcher.setPlayerViewY(180.0F);
        EntityRenderDispatcher.setRenderShadow(false);
        EntityRenderDispatcher.renderEntity(ent, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F, false);
        EntityRenderDispatcher.setRenderShadow(true);

        ent.renderYawOffset = f;
        ent.rotationYaw = f1;
        ent.rotationPitch = f2;
        ent.prevRotationYawHead = f3;
        ent.rotationYawHead = f4;

        RenderSystem.popMatrix();
        RenderHelper.disableStandardItemLighting();
        RenderSystem.disableRescaleNormal();
        RenderSystem.setActiveTexture(OpenGlHelper.lightmapTexUnit);
        RenderSystem.disableTexture2D();
        RenderSystem.setActiveTexture(OpenGlHelper.defaultTexUnit);
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
        {}
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
        {}
    }

    public static void playClick()
    {
        Minecraft.getInstance().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
    }

    /**
     * Open a Folder<br>
     * Referenced from {@link net.minecraft.client.renderer.OpenGlHelper.openFile(File)}
     */
    public static void openFolder(String url)
    {
        File file = new File(url);

        switch (Util.getOSType())
        {
            case WINDOWS:
                try
                {
                    Runtime.getRuntime().exec(new String[]
                    {
                        "cmd.exe", "/C", "start", "\"Open file\"", file.getAbsolutePath()
                    });

                    return;
                }
                catch (IOException ioexception)
                {
                    ioexception.printStackTrace();

                    break;
                }

            case OSX:
                try
                {
                    Runtime.getRuntime().exec(new String[]
                    {
                            "/usr/bin/open", file.getAbsolutePath()
                    });

                    return;
                }
                catch (IOException ioexception1)
                {
                    ioexception1.printStackTrace();
                }

            default:
                break;
        }

        boolean failed = false;

        try
        {
            Class<?> clazz = Class.forName("java.awt.Desktop");
            Object object = clazz.getMethod("getDesktop", new Class[0]).invoke(null);

            clazz.getMethod("browse", new Class[] {URI.class}).invoke(object, new Object[] {file.toURI()});
        }
        catch (Throwable throwable1)
        {
            throwable1.printStackTrace();
            failed = true;
        }

        if (failed)
        {
            Sys.openURL("file://" + file.getAbsolutePath());
        }
    }
}