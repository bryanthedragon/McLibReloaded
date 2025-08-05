package bryanthedragon.mclibreloaded.utils.wav;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiDraw;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.textures.GpuTexture;

import java.util.ArrayList;
import java.util.List;

/**
 * Waveform class
 *
 * This class allows to
 */
@OnlyIn(Dist.CLIENT)
public class Waveform
{
    public float[] average;
    public float[] maximum;
    private final List<DynamicTexture> dynamicTextures = new ArrayList<>();
    private List<WaveformSprite> sprites = new ArrayList<WaveformSprite>();
    private int w;
    private int h;
    private int pixelsPerSecond;

    /**
     * Generates a waveform for the given audio data and renders it as a texture.
     * 
     * @param data the audio data to generate a waveform for
     * @param pixelsPerSecond the number of pixels per second to generate the waveform for
     * @param height the height of the waveform
     * @throws IllegalStateException if the audio data is not 16-bit
     */
    public void generate(Wave data, int pixelsPerSecond, int height)
    {
        if (data.getBytesPerSample() != 2)
        {
            throw new IllegalStateException("Waveform generation doesn't support non 16-bit audio data!");
        }
        this.populateWaveform(data, pixelsPerSecond, height);
        this.renderWaveform();
    }

    private final List<ResourceLocation> textureLocations = new ArrayList<>();

    /**
     * Generates a waveform for the given audio data and renders it as a texture.
     * 
     * This method is expensive and should be called sparingly. It will delete all
     * existing sprites and textures.
     * 
     * @since 1.0
     */
    public void renderWaveform() 
    {
        this.deleteWaveform();
        int maxTextureSize = 16384;  // Hard-coded or query via caps if needed
        int count = (int) Math.ceil(this.w / (double) maxTextureSize);
        int offset = 0;
        for (int t = 0; t < count; t++) 
        {
            int width = Math.min(this.w - offset, maxTextureSize);
            NativeImage image = new NativeImage(NativeImage.Format.RGBA, width, this.h, true);
            for (int i = offset, j = 0, c = Math.min(offset + width, this.average.length); i < c; i++, j++) 
            {
                float average = this.average[i];
                float maximum = this.maximum[i];
                int maxHeight = (int) (maximum * this.h);
                int avgHeight = (int) (average * (this.h - 1)) + 1;
                if (avgHeight > 0) 
                {
                    int center = this.h / 2;
                    int yStartMax = center - maxHeight / 2;
                    int yEndMax = yStartMax + maxHeight;
                    for (int y = yStartMax; y < yEndMax; y++) 
                    {
                        if (y >= 0 && y < this.h) 
                        {
                            image.setPixelABGR(j, y, 0xFFFFFFFF);
                        }
                    }
                    int yStartAvg = center - avgHeight / 2;
                    int yEndAvg = yStartAvg + avgHeight;
                    for (int y = yStartAvg; y < yEndAvg; y++) 
                    {
                        if (y >= 0 && y < this.h) 
                        {
                            image.setPixelABGR(j, y, 0xFFD3D3D3);
                        }
                    }
                }
            }
            // Wrap in DynamicTexture
            String namespace = "mclibreloaded";
            String path = "waveform/" + System.nanoTime();
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, path);
            DynamicTexture dynTex = new DynamicTexture(() -> "waveform", image);
            this.dynamicTextures.add(dynTex);
            
            Minecraft.getInstance().getTextureManager().register(id, dynTex);
            this.sprites.add(new WaveformSprite(id, width));
            this.textureLocations.add(id);
        }
    }

    /**
     * Populates the waveform data by calculating the average and maximum amplitude
     * values for each pixel column based on the provided audio data.
     *
     * @param data the audio data to process
     * @param pixelsPerSecond the number of pixels representing each second of audio
     * @param height the height of the waveform to generate
     */
    public void populateWaveform(Wave data, int pixelsPerSecond, int height)
    {
        this.pixelsPerSecond = pixelsPerSecond;
        this.w = (int) (data.getDuration() * pixelsPerSecond);
        this.h = height;
        this.average = new float[this.w];
        this.maximum = new float[this.w];
        int region = data.getScanRegion(pixelsPerSecond);
        for (int i = 0; i < this.w; i ++)
        {
            int offset = i * region;
            int count = 0;
            float average = 0;
            float maximum = 0;
            for (int j = 0; j < region; j += 2 * data.numChannels)
            {
                if (offset + j + 1 >= data.data.length)
                {
                    break;
                }
                byte a = data.data[offset + j];
                byte b = data.data[offset + j + 1];
                float sample = a + (b << 8);
                maximum = Math.max(maximum, Math.abs(sample));
                average += Math.abs(sample);
                count++;
            }
            average /= count;
            average /= 0xffff / 2;
            maximum /= 0xffff / 2;
            this.average[i] = average;
            this.maximum[i] = maximum;
        }
    }

    /**
     * Deletes all of the textures and sprites created by this waveform generator.
     * This should be called when the waveform is no longer needed to free up
     * OpenGL resources.
     */
    public void deleteWaveform() 
    {
        for (DynamicTexture tex : this.dynamicTextures) 
        {
            tex.close(); // This safely deletes the OpenGL texture
        }
    this.dynamicTextures.clear();
    this.textureLocations.clear();
    this.sprites.clear();
}

    /**
     * Returns true if this waveform generator has been populated with audio data
     * and false otherwise.
     * 
     * @return true if this waveform generator has been populated with audio data
     *         and false otherwise
     */
    public boolean isCreated()
    {
        return !this.sprites.isEmpty();
    }

    /**
     * Returns the number of pixels per second of audio data that this waveform generator
     * will render. This is the number of pixels that will be generated for each second of
     * audio data. A higher value will result in a more detailed waveform, but will also
     * result in a larger texture.
     * 
     * @return the number of pixels per second of audio data that this waveform generator
     *         will render
     */
    public int getPixelsPerSecond()
    {
        return this.pixelsPerSecond;
    }

    /**
     * Returns the width of this waveform in pixels.
     * 
     * @return the width of this waveform in pixels
     */
    public int getWidth()
    {
        return this.w;
    }

    /**
     * Returns the height of this waveform in pixels.
     * 
     * @return the height of this waveform in pixels
     */
    public int getHeight()
    {
        return this.h;
    }

    /**
     * Returns a list of all the sprites generated by this waveform generator.
     * 
     * @return a list of all the sprites generated by this waveform generator
     */
    public List<WaveformSprite> getSprites()
    {
        return this.sprites;
    }

    /**
     * Draws the waveform at the specified position with the given UV coordinates, width,
     * and height using the default height of the waveform.
     * 
     * @param x the x-coordinate where the waveform will be drawn
     * @param y the y-coordinate where the waveform will be drawn
     * @param u the U texture coordinate
     * @param v the V texture coordinate
     * @param w the width of the drawn waveform
     * @param h the height of the drawn waveform
     */
    public void draw(int x, int y, int u, int v, int w, int h)
    {
        drawer(x, y, u, v, w, h, this.h);
    }


    /**
     * Draws the waveform at the specified position with the given UV coordinates, width,
     * and height with the specified height of the waveform.
     * 
     * @param x the x-coordinate where the waveform will be drawn
     * @param y the y-coordinate where the waveform will be drawn
     * @param u the U texture coordinate
     * @param v the V texture coordinate
     * @param w the width of the drawn waveform
     * @param h the height of the drawn waveform
     * @param height the height of the waveform
     */
    public void drawer(int x, int y, int u, int v, int w, int h, int height)
    {
        int offset = 0;
        for (WaveformSprite sprite : this.sprites)
        {
            int sw = sprite.width;
            offset += sw;
            if (w <= 0)
            {
                break;
            }
            if (u >= offset)
            {
                continue;
            }
            int so = offset - u;
            GpuTexture gpuTex = getGpuTexture(sprite.texture);
            if (gpuTex != null) 
            {
                RenderSystem.setShaderTexture(0, gpuTex);
            }
            GuiDraw.drawBillboard(x, y, u, v, Math.min(w, so), h, sw, height);
            x += so;
            u += so;
            w -= so;
        }
    }
    
    /**
     * Retrieves a GpuTexture from a TextureManager by a given ResourceLocation
     * 
     * @param location the ResourceLocation of the texture to retrieve
     * @return the GpuTexture at the given location, or null if it couldn't be found
     */
    private GpuTexture getGpuTexture(ResourceLocation location) 
    {
        TextureManager textureManager = Minecraft.getInstance().getTextureManager();
        AbstractTexture tex = textureManager.getTexture(location);
        if (tex == null) 
        {
            // handle error or fallback
            return null;
        }
        // AbstractTexture has a protected field 'texture' of type GpuTexture; 
        // If it’s not accessible, you may need reflection or a public getter
        return tex.getTexture(); // <-- in Mojang mappings, getTexture() returns GpuTexture
    }


    @OnlyIn(Dist.CLIENT)
    public static class WaveformSprite 
    {
        public final ResourceLocation texture;
        public final int width;
        public WaveformSprite(ResourceLocation texture, int width) 
        {
            this.texture = texture;
            this.width = width;
        }
    }
}
