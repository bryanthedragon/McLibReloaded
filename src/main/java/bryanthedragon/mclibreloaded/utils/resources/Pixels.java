package bryanthedragon.mclibreloaded.utils.resources;

import bryanthedragon.mclibreloaded.utils.Color;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;

public class Pixels
{
    public byte[] pixelBytes;
    public int[] pixelInts;
    public int pixelLength;
    public int width;
    public int height;
    public Color color = new Color();
    private PixelAccessor accessor;

    /**
     * Set the pixels of the image to be the ones from the given image. This will
     * store the pixels in either the byte or int array depending on whether the
     * image has an alpha channel or not. The pixel length is also set to either
     * 3 or 4 depending on whether the image has an alpha channel.
     *
     * @param image the image to copy the pixels from
     */
    public void set(BufferedImage image)
    {
        if (image.getRaster().getDataBuffer() instanceof DataBufferByte)
        {
            this.pixelBytes = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
            this.pixelInts = null;
            this.accessor = PixelAccessor.BYTE;
        }
        else
        {
            this.pixelInts = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
            this.pixelBytes = null;
            this.accessor = PixelAccessor.INT;
        }
        this.pixelLength = image.getAlphaRaster() != null ? 4 : 3;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    /**
     * Check if the image has an alpha channel.
     *
     * @return true if the image has an alpha channel, false otherwise.
     */
    public boolean hasAlpha()
    {
        return this.pixelLength == 4;
    }

    /**
     * Converts 2D coordinates (x, y) to a 1D index.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the index in the pixel array corresponding to the (x, y) position
     */
    public int toIndex(int x, int y)
    {
        return x + y * this.width;
    }

    /**
     * Converts a 1D index to an x-coordinate.
     *
     * @param index the index in the pixel array
     * @return the x-coordinate of the corresponding pixel
     */
    public int toX(int index)
    {
        return index % this.width;
    }

    /**
     * Converts a 1D index to a y-coordinate.
     *
     * @param index the index in the pixel array
     * @return the y-coordinate of the corresponding pixel
     */
    public int toY(int index)
    {
        return index / this.width;
    }

    /**
     * Returns the number of pixels in the image.
     *
     * @return the number of pixels in the image
     */
    public int getCount()
    {
        return this.accessor == PixelAccessor.BYTE ? this.pixelBytes.length / this.pixelLength : this.pixelInts.length;
    }

    /**
     * Retrieves the color at the specified index in the pixel array.
     *
     * @param index the index of the pixel in the array
     * @return the color at the specified index
     */
    public Color getColorIndex(int index)
    {
        this.accessor.getPixelColor(this, index, this.color);
        return this.color;
    }

    /**
     * Retrieves the color at the specified (x, y) coordinates in the image.
     *
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @return the color at the specified (x, y) coordinates
     */
    public Color getColor(int x, int y)
    {
        return this.getColorIndex(this.toIndex(x, y));
    }

    /**
     * Sets the color of the pixel at the specified index in the pixel array.
     *
     * @param index the index of the pixel in the array
     * @param color the color to set at the specified index
     */
    public void setColor(int index, Color color)
    {
        this.accessor.setPixelColor(this, index, color);
    }

    /**
     * Sets the color of the pixel at the specified (x, y) coordinates in the image.
     *
     * @param x the x-coordinate of the pixel
     * @param y the y-coordinate of the pixel
     * @param color the color to set at the specified coordinates
     */
    public void setColor(int x, int y, Color color)
    {
        this.setColor(this.toIndex(x, y), color);
    }
}