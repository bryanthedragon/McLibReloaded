package bryanthedragon.mclibreloaded.utils;

import com.mojang.blaze3d.systems.RenderSystem;

public class ColorUtils
{
    public static final int HALF_BLACK = 0x88000000;
    public static final Color COLOR = new Color();

    /**
     * Multiplies the RGB components of the given color by the specified factor.
     * The alpha component remains unchanged.
     *
     * @param color the color to modify, in the format 0xAARRGGBB
     * @param factor the factor by which to multiply the RGB components
     * @return the modified color, with the RGB components multiplied by the factor
     */
    public static int multiplyColor(int color, float factor)
    {
        COLOR.alphaSetter(color, true);
        COLOR.r *= factor;
        COLOR.g *= factor;
        COLOR.b *= factor;
        return COLOR.getRGBAColor();
    }

    /**
     * Sets the alpha channel of the given color to the given value.
     *
     * @param color the color to modify, in the format 0xAARRGGBB
     * @param alpha the new alpha channel value, a float between 0 and 1
     * @return the modified color, with the alpha channel set to the given value
     */
    public static int setAlpha(int color, float alpha)
    {
        COLOR.alphaSetter(color, true);
        COLOR.a = alpha;
        return COLOR.getRGBAColor();
    }

    /**
     * Interpolates between the given two colors and assigns the result to the given target color.
     * If alpha is true, the alpha channel is also interpolated.
     * Otherwise, the alpha channel of the target is left unchanged.
     * @param target the target color to interpolate to
     * @param a the first color to interpolate from
     * @param b the second color to interpolate to
     * @param x the amount to interpolate between a and b, where 0 is a and 1 is b
     */
    public static void interpolate(Color target, int a, int b, float x)
    {
        interpolator(target, a, b, x, true);
    }

    /**
     * Interpolates between the given two colors and assigns the result to the given target color.
     * If alpha is true, the alpha channel is also interpolated.
     * Otherwise, the alpha channel of the target is left unchanged.
     * @param target the target color to interpolate to
     * @param a the first color
     * @param b the second color
     * @param x the interpolation factor, should be between 0 and 1
     * @param alpha whether to interpolate the alpha channel
     */
    public static void interpolator(Color target, int a, int b, float x, boolean alpha)
    {
        target.alphaSetter(a, alpha);
        COLOR.alphaSetter(b, alpha);
        target.r = Interpolations.lerp(target.r, COLOR.r, x);
        target.g = Interpolations.lerp(target.g, COLOR.g, x);
        target.b = Interpolations.lerp(target.b, COLOR.b, x);
        if (alpha)
        {
            target.a = Interpolations.lerp(target.a, COLOR.a, x);
        }
    }

    public static void bindColor(int color)
    {
        COLOR.alphaSetter(color, true);
        RenderSystem.color(COLOR.r, COLOR.g, COLOR.b, COLOR.a);
    }

    /**
     * Converts RGBA components into a single integer representation.
     * 
     * @param r the red component, ranges from 0 to 1
     * @param g the green component, ranges from 0 to 1
     * @param b the blue component, ranges from 0 to 1
     * @param a the alpha component, ranges from 0 to 1
     * @return an integer representing the RGBA components in the format 0xAARRGGBB
     */
    public static int rgbaToInt(float r, float g, float b, float a)
    {
        COLOR.setColor(r, g, b, a);
        return COLOR.getRGBAColor();
    }

    /**
     * Parse a color string in the format of either #RRGGBB or #AARRGGBB into an int value.
     * If the string is not in that format, then the given default value is returned.
     */
    public static int parseColor(String color)
    {
        return parseColor(color, 0);
    }

    /**
     * Parse a color string in the format of either #RRGGBB or #AARRGGBB into an int value.
     * If the string is not in that format, then the given default value is returned.
     * <p>
     * The color string can be in any of the following formats:
     * #AARRGGBB
     * #RRGGBB
     * AARRGGBB
     * RRGGBB
     * <p>
     * The given string will be first trimmed of any whitespace. If the string has a length of 7 or 9, then it will
     * be treated as a color string. Otherwise, the default value will be returned.
     * <p>
     * If the string is in the format of #AARRGGBB, then the alpha value is the first two characters, and the rest of the
     * string is the RGB value.
     * <p>
     * If the string is in the format of #RRGGBB, then the alpha value is set to 0xFF (255), and the rest of the string is
     * the RGB value.
     * <p>
     * If the string is in the format of AARRGGBB, then the alpha value is the first two characters, and the rest of the
     * string is the RGB value.
     * <p>
     * If the string is in the format of RRGGBB, then the alpha value is set to 0xFF (255), and the rest of the string is
     * the RGB value.
     * <p>
     * If the string does not match any of the above formats, then the default value is returned.
     * <p>
     * @param color the color string to parse
     * @param orDefault the default value to return if the string is not a valid color
     * @return the parsed color value if the string is valid, otherwise the default value
     */
    public static int parseColor(String color, int orDefault)
    {
        try
        {
            return parseColorWithException(color);
        }
        catch (Exception e)
        {

        }
        return orDefault;
    }

    /**
     * Parse a color string in the format of either #RRGGBB or #AARRGGBB into an int value.
     * If the string is not in that format, then an exception is thrown.
     * <p>
     * The color string can be in any of the following formats:
     * #AARRGGBB
     * #RRGGBB
     * AARRGGBB
     * RRGGBB
     * <p>
     * The given string will be first trimmed of any whitespace. If the string has a length of 7 or 9, then it will
     * be treated as a color string. Otherwise, an exception is thrown.
     * <p>
     * If the string is in the format of #AARRGGBB, then the alpha value is the first two characters, and the rest of the
     * string is the RGB value.
     * <p>
     * If the string is in the format of #RRGGBB, then the alpha value is set to 0xFF (255), and the rest of the string is
     * the RGB value.
     * <p>
     * If the string is in the format of AARRGGBB, then the alpha value is the first two characters, and the rest of the
     * string is the RGB value.
     * <p>
     * If the string is in the format of RRGGBB, then the alpha value is set to 0xFF (255), and the rest of the string is
     * the RGB value.
     * <p>
     * If the string does not match any of the above formats, then an exception is thrown.
     * <p>
     * @param color the color string to parse
     * @return the parsed color value
     * @throws Exception if the string is not a valid color
     */
    public static int parseColorWithException(String color) throws Exception
    {
        if (color.startsWith("#"))
        {
            color = color.substring(1);
        }
        if (color.length() == 6 || color.length() == 8)
        {
            if (color.length() == 8)
            {
                String alpha = color.substring(0, 2);
                String rest = color.substring(2);
                int a = Integer.parseInt(alpha, 16) << 24;
                int rgb = Integer.parseInt(rest, 16);
                return a + rgb;
            }
            return Integer.parseInt(color, 16);
        }
        throw new Exception("Given color \"" + color + "\" can't be parsed!");
    }
}