package bryanthedragon.mclibreloaded.utils.resources;

import bryanthedragon.mclibreloaded.utils.ReflectionUtils;
import bryanthedragon.mclibreloaded.utils.resources.location.MultiResourceLocation;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;

import com.mojang.blaze3d.platform.NativeImage;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Stack;

public class MultiskinThread implements Runnable
{
    private static MultiskinThread instance;
    private static Thread thread;

    public Stack<MultiResourceLocation> locations = new Stack<MultiResourceLocation>();

    /**
     * Adds a MultiResourceLocation to be processed by the MultiskinThread.
     * 
     * If the thread is not alive or the instance is null, a new instance of
     * MultiskinThread is created, and the provided location is added to it. 
     * If the instance already exists, the location is added to the existing 
     * instance.
     *
     * @param location the MultiResourceLocation to be added
     */
    public static synchronized void addMultiResourceLocation(MultiResourceLocation location)
    {
        if (instance != null && !thread.isAlive())
        {
            instance = null;
        }
        if (instance == null)
        {
            instance = new MultiskinThread();
            instance.addLocation(location);
            thread = new Thread(instance);
            thread.start();
        }
        else
        {
            instance.addLocation(location);
        }
    }

    /**
     * Clears the MultiskinThread instance, stopping any existing
     * thread and preventing any new threads from being created.
     */
    public static void clear()
    {
        instance = null;
    }

    /**
     * Converts a BufferedImage to a direct ByteBuffer.
     * The buffer is in RGBA format, with the red, green, and blue channels
     * represented by bytes, and the alpha channel represented by a single byte.
     * The buffer is flipped to begin at index 0.
     *
     * @param image the BufferedImage to convert
     * @return the direct ByteBuffer representation of the image
     */
    public static ByteBuffer bytesFromBuffer(BufferedImage image)
    {
        int w = image.getWidth();
        int h = image.getHeight();
        ByteBuffer buffer = ByteBuffer.allocateDirect(w * h * 4).order(ByteOrder.nativeOrder());
        int[] pixels = new int[w * h];
        image.getRGB(0, 0, w, h, pixels, 0, w);
        for (int y = 0; y < h; y++)
        {
            for (int x = 0; x < w; x++)
            {
                int pixel = pixels[y * w + x];
                buffer.put((byte) ((pixel >> 16) & 0xFF));
                buffer.put((byte) ((pixel >> 8) & 0xFF));
                buffer.put((byte) (pixel & 0xFF));
                buffer.put((byte) ((pixel >> 24) & 0xFF));
            }
        }
        buffer.flip();
        return buffer;
    }

    /**
     * Add a location to be processed by the thread. If the location already exists in the stack, it is not added again.
     * @param location the location to add
     */
    public synchronized void addLocation(MultiResourceLocation location)
    {
        if (this.locations.contains(location))
        {
            return;
        }
        this.locations.add(location);
    }

    @SuppressWarnings("unlikely-arg-type")
    public void run()
    {
        while (!this.locations.isEmpty() && instance != null)
        {
            MultiResourceLocation location = this.locations.peek();
            AbstractTexture texture = ReflectionUtils.getTextures(Minecraft.getInstance().textureManager).get(location);
            try
            {
                if (texture != null)
                {
                    this.locations.pop();
                    BufferedImage image = TextureProcessor.postProcess(location);
                    int w = image.getWidth();
                    int h = image.getHeight();
                    ByteBuffer buffer = bytesFromBuffer(image);
                    Minecraft.getInstance().execute(() -> {
                        try (NativeImage nativeImg = new NativeImage(w, h, false)) 
                        {
                            buffer.rewind();
                            for (int y = 0; y < h; y++) 
                            {
                                for (int x = 0; x < w; x++) 
                                {
                                    int i = (y * w + x) * 4;
                                    int r = buffer.get(i) & 0xFF;
                                    int g = buffer.get(i + 1) & 0xFF;
                                    int b = buffer.get(i + 2) & 0xFF;
                                    int a = buffer.get(i + 3) & 0xFF;
                                    int argb = (a << 24) | (r << 16) | (g << 8) | b;
                                    nativeImg.setPixel(x, y, argb);
                                }
                            }
                            DynamicTexture dynTex = new DynamicTexture("multiskin", w, h, false);
                            dynTex.setPixels(nativeImg);
                            dynTex.upload();
                            Minecraft.getInstance().getTextureManager().register(location.toResourceLocation(), dynTex);
                        }
                    });
                }
                Thread.sleep(100);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        instance = null;
        thread = null;
    }
}