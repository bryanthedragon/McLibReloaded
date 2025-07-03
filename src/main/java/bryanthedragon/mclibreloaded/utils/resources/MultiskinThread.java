package bryanthedragon.mclibreloaded.utils.resources;

import bryanthedragon.mclibreloaded.utils.ReflectionUtils;
import bryanthedragon.mclibreloaded.utils.resources.location.MultiResourceLocation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL12;
import org.lwjgl.opengl.GL11;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
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
    public static synchronized void add(MultiResourceLocation location)
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

    public static void clear()
    {
        instance = null;
    }

    /**
     * Create a byte buffer from buffered image
     */
    public static ByteBuffer bytesFromBuffer(BufferedImage image)
    {
        int w = image.getWidth();
        int h = image.getHeight();

        ByteBuffer buffer = GLAllocation.createDirectByteBuffer(w * h * 4);
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

    @Override
    public void run()
    {
        while (!this.locations.isEmpty() && instance != null)
        {
            MultiResourceLocation location = this.locations.peek();
            ITextureObject texture = ReflectionUtils.getTextures(Minecraft.getInstance().renderEngine).get(location);

            try
            {
                if (texture != null)
                {
                    this.locations.pop();

                    BufferedImage image = TextureProcessor.postProcess(location);
                    int w = image.getWidth();
                    int h = image.getHeight();
                    ByteBuffer buffer = bytesFromBuffer(image);

                    Minecraft.getInstance().addScheduledTask(() ->
                    {
                        TextureUtil.allocateTexture(texture.getGlTextureId(), w, h);

                        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.getGlTextureId());
                        RenderSystem.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
                        RenderSystem.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
                        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA8, w, h, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
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