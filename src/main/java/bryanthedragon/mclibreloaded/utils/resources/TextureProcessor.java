package bryanthedragon.mclibreloaded.utils.resources;

import bryanthedragon.mclibreloaded.McLib;
import bryanthedragon.mclibreloaded.events.MultiskinProcessedEvent;
import bryanthedragon.mclibreloaded.utils.Color;
import bryanthedragon.mclibreloaded.utils.resources.location.MultiResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.Resource;


import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.awt.Graphics;


@OnlyIn(Dist.CLIENT)
public class TextureProcessor
{
    public static Pixels pixels = new Pixels();
    public static Pixels target = new Pixels();

    /**
     * Processes a multi-resource location to produce a BufferedImage and posts
     * a MultiskinProcessedEvent to the event bus.
     *
     * @param multi the MultiResourceLocation to process
     * @return a BufferedImage generated from the given MultiResourceLocation
     */
    public static BufferedImage postProcess(MultiResourceLocation multi)
    {
        BufferedImage image = process(multi);

        Minecraft.getInstance().execute(() -> {McLib.EVENT_BUS.post(new MultiskinProcessedEvent(multi, image));});

        return image;
    }

    /**
     * Process a MultiResourceLocation and combine all the textures into a single one.
     * The order of the textures in the list is the order in which they are drawn on top of each other.
     * If a texture is null, it is skipped.
     * The resulting image is then scaled to the maximum of the widths and heights of all the textures.
     * If any of the FilteredResourceLocation objects in the list have scaleToLargest set to true, then that texture is scaled to the maximum of the widths and heights of all the textures.
     * If any of the FilteredResourceLocation objects in the list have scale set to a non-zero number, then that texture is scaled by that amount.
     * If any of the FilteredResourceLocation objects in the list have erase set to true, then that texture is applied as a mask to the resulting image, erasing any pixels that are not fully opaque.
     * If any of the FilteredResourceLocation objects in the list have color set to a non-zero number, then that texture is tinted with that color.
     * If any of the FilteredResourceLocation objects in the list have pixelate set to a number greater than 1, then that texture is pixelated by that amount.
     * All of the above steps are done in the order in which they appear in the list.
     */
    @SuppressWarnings("null")
    public static BufferedImage process(MultiResourceLocation multi)
    {
        ResourceManager manager = Minecraft.getInstance().getResourceManager();
        List<BufferedImage> images = new ArrayList<BufferedImage>();

        int w = 0;
        int h = 0;

        for (int i = 0; i < multi.children.size(); i++)
        {
            FilteredResourceLocation child = multi.children.get(i);
            BufferedImage image = null;

            try
            {
                Optional<Resource> opt = manager.getResource(child.path);
                if (opt.isPresent()) 
                {
                    Resource resource = opt.get();
                    InputStream stream = resource.open();  // Not getInputStream()
                    image = ImageIO.read(stream);
                }

                w = Math.max(w, child.getWidth(image.getWidth()));
                h = Math.max(h, child.getHeight(image.getHeight()));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            images.add(image);
        }

        BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics g = image.getGraphics();

        for (int i = 0; i < multi.children.size(); i++)
        {
            BufferedImage child = images.get(i);

            if (child == null)
            {
                continue;
            }

            FilteredResourceLocation filter = multi.children.get(i);
            int iw = child.getWidth();
            int ih = child.getHeight();

            if (filter.scaleToLargest)
            {
                iw = w;
                ih = h;
            }
            else if (filter.scale != 0 && filter.scale > 0)
            {
                iw = (int) (iw * filter.scale);
                ih = (int) (ih * filter.scale);
            }

            if (iw > 0 && ih > 0)
            {
                if (filter.erase)
                {
                    processErase(image, child, filter, iw, ih);
                }
                else
                {
                    if (filter.color != 0xffffff || filter.pixelate > 1)
                    {
                        processImage(child, filter);
                    }

                    g.drawImage(child, filter.shiftX, filter.shiftY, iw, ih, null);
                }
            }
        }

        g.dispose();

        return image;
    }

    
    /**
     * Process a mask image and apply it to a destination image, erasing any pixels
     * that are not fully opaque in the mask.
     *
     * @param image the destination image to erase pixels from
     * @param child the mask image to apply
     * @param filter the FilteredResourceLocation containing the shift, scale, and other
     *               parameters for the mask image
     * @param iw the width of the mask image after scaling
     * @param ih the height of the mask image after scaling
     */
    private static void processErase(BufferedImage image, BufferedImage child, FilteredResourceLocation filter, int iw, int ih)
    {
        BufferedImage mask = new BufferedImage(image.getWidth(), image.getHeight(), image.getType());
        Graphics g2 = mask.getGraphics();

        g2.drawImage(child, filter.shiftX, filter.shiftY, iw, ih, null);
        g2.dispose();

        target.set(mask);
        pixels.set(image);

        for (int p = 0, c = target.getCount(); p < c; p++)
        {
            Color pixel = target.getColor(p);

            if (pixel.a > 0.999F)
            {
                pixel = pixels.getColor(p);
                pixel.a = 0;
                pixels.setColor(p, pixel);
            }
        }
    }

    /**
     * Processes an image by applying color filtering and pixelation based on the given
     * {@link FilteredResourceLocation} parameters.
     *
     * This method sets the pixels of the given image and applies the specified color filter.
     * If the image has an alpha channel, it skips fully transparent pixels. Additionally,
     * if pixelation is enabled, it modifies the image by reducing the resolution based on
     * the pixelate factor from the {@link FilteredResourceLocation}.
     *
     * @param child the image to process and modify
     * @param frl the {@link FilteredResourceLocation} containing the color and pixelation
     *            parameters
     */
    private static void processImage(BufferedImage child, FilteredResourceLocation frl)
    {
        pixels.set(child);

        Color filter = new Color().set(frl.color);
        Color pixel = new Color();

        for (int i = 0, c = pixels.getCount(); i < c; i++)
        {
            pixel.copy(pixels.getColor(i));

            if (pixels.hasAlpha())
            {
                if (pixel.a <= 0)
                {
                    continue;
                }
            }

            if (frl.pixelate > 1)
            {
                int x = pixels.toX(i);
                int y = pixels.toY(i);
                boolean origin = x % frl.pixelate == 0 && y % frl.pixelate == 0;

                x -= x % frl.pixelate;
                y -= y % frl.pixelate;

                pixel.copy(pixels.getColor(x, y));
                pixels.setColor(i, pixel);

                if (!origin)
                {
                    continue;
                }
            }

            pixel.r *= filter.r;
            pixel.g *= filter.g;
            pixel.b *= filter.b;
            pixel.a *= filter.a;
            pixels.setColor(i, pixel);
        }
    }
}