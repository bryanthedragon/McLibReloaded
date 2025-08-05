package bryanthedragon.mclibreloaded.utils.resources;

import bryanthedragon.mclibreloaded.utils.Color;

public enum PixelAccessor
{
    BYTE()
    {
        /**
         * Retrieves the color at the specified index in the pixel array.
         *
         * @param pixels the pixels object
         * @param index the index of the pixel in the array
         * @param color the color object to store the color in
         */
        public void getPixelColor(Pixels pixels, int index, Color color)
        {
            index *= pixels.pixelLength;
            int offset = 0;
            if (pixels.hasAlpha())
            {
                color.a = ((int) pixels.pixelBytes[index + offset++] & 0xff) / 255F;
            }
            else
            {
                color.a = 1;
            }
            color.b = ((int) pixels.pixelBytes[index + offset++] & 0xff) / 255F;
            color.g = ((int) pixels.pixelBytes[index + offset++] & 0xff) / 255F;
            color.r = ((int) pixels.pixelBytes[index + offset] & 0xff) / 255F;
        }

        /**
         * Sets the color at the specified index in the pixel array.
         *
         * @param pixels the pixels object containing the pixel data
         * @param index the index of the pixel in the array
         * @param color the color object containing the color values to set
         */
        public void setPixelColor(Pixels pixels, int index, Color color)
        {
            index *= pixels.pixelLength;
            int offset = 0;
            if (pixels.hasAlpha())
            {
                pixels.pixelBytes[index + offset++] = (byte) (color.a * 0xff);
            }
            pixels.pixelBytes[index + offset++] = (byte) (color.b * 0xff);
            pixels.pixelBytes[index + offset++] = (byte) (color.g * 0xff);
            pixels.pixelBytes[index + offset] = (byte) (color.r * 0xff);
        }
    },
    INT()
    {
        /**
         * Retrieves the color at the specified index in the pixel array.
         *
         * @param pixels the pixels object
         * @param index the index of the pixel in the array
         * @param color the color object to store the color in
         */
        public void getPixelColor(Pixels pixels, int index, Color color)
        {
            int c = pixels.pixelInts[index];
            int a = c >> 24 & 0xff;
            int b = c >> 16 & 0xff;
            int g = c >> 8 & 0xff;
            int r = c & 0xff;
            color.r = r / 255F;
            color.g = g / 255F;
            color.b = b / 255F;
            color.a = a / 255F;
        }

        /**
         * Sets the color at the specified index in the pixel array.
         *
         * @param pixels the pixels object containing the pixel data
         * @param index the index of the pixel in the array
         * @param color the color object containing the color values to set
         */
        public void setPixelColor(Pixels pixels, int index, Color color)
        {
            pixels.pixelInts[index] = ((int) (color.a * 0xff) << 24) + ((int) (color.b * 0xff) << 16) + ((int) (color.g * 0xff) << 8) + (int) (color.r * 0xff);
        }
    };

    public abstract void getPixelColor(Pixels pixels, int index, Color color);

    public abstract void setPixelColor(Pixels pixels, int index, Color color);
}