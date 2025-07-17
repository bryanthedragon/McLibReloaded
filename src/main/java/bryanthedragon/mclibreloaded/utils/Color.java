package bryanthedragon.mclibreloaded.utils;

import org.apache.commons.lang3.StringUtils;

public class Color implements ICopy<Color>
{
    public float r;
    public float g;
    public float b;
    public float a = 1;

    public Color()
    {}

    public Color(float r, float g, float b)
    {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public Color(float r, float g, float b, float a)
    {
        this(r, g, b);

        this.a = a;
    }

    public Color(int color)
    {
        this(color, true);
    }

    public Color(int color, boolean alpha)
    {
        this.alphaSetter(color, alpha);
    }

    /**
     * Set the color components
     *
     * @param r red component, ranges from 0 to 1
     * @param g green component, ranges from 0 to 1
     * @param b blue component, ranges from 0 to 1
     * @param a alpha component, ranges from 0 to 1
     * @return this
     */
    public Color set(float r, float g, float b, float a)
    {
        this.r = r;
        this.g = g;
        this.b = b;
        this.a = a;

        return this;
    }

    /**
     * Set the specified component of the color to the given value.
     *
     * @param value the value to set the component to, ranges from 0 to 1
     * @param component the component to set: 1 for red, 2 for green, 3 for blue, any other value for alpha
     * @return this
     */
    public Color floatSetter(float value, int component)
    {
        switch (component)
        {
            case 1:
                this.r = value;
            break;

            case 2:
                this.g = value;
            break;

            case 3:
                this.b = value;
            break;

            default:
                this.a = value;
            break;
        }

        return this;
    }

    /**
     * Sets the color to the given int value in the format of 0xAARRGGBB
     *
     * @param color the color to set, format of 0xAARRGGBB
     * @return this
     */
    public Color setInt(int color)
    {
        return this.alphaSetter(color, true);
    }

    /**
     * Sets the color components based on the given int value in the format 0xAARRGGBB.
     * 
     * @param color the color to set, in the format of 0xAARRGGBB
     * @param alpha if true, sets the alpha channel based on the color's alpha value; 
     *              if false, sets alpha to 1
     * @return this
     */
    public Color alphaSetter(int color, boolean alpha)
    {
        this.set((color >> 16 & 0xff) / 255F, (color >> 8 & 0xff) / 255F, (color & 0xff)  / 255F, alpha ? (color >> 24 & 0xff)  / 255F : 1F);
        return this;
    }

    /**
     * Returns a new copy of this color.
     *
     * @return a new copy of this color
     */
    public Color copier()
    {
        Color copy = new Color();
        copy.copy(this);
        return copy;
    }

    /**
     * Copies the RGBA components from the specified color to this color.
     *
     * @param color the color object from which to copy the RGBA values
     */
    public void copy(Color color)
    {
        this.set(color.r, color.g, color.b, color.a);
    }

    /**
     * Returns the RGBA components of the color as an integer.
     * 
     * @return the RGBA components of the color in the format 0xAARRGGBB, 
     *         where the alpha channel is set to the value of the alpha component,
     *         and the red, green, and blue components are set to the values
     *         of the red, green, and blue components, respectively.
     */
    public int getRGBAColor()
    {
        float r = MathUtils.clamperFloat(this.r, 0, 1);
        float g = MathUtils.clamperFloat(this.g, 0, 1);
        float b = MathUtils.clamperFloat(this.b, 0, 1);
        float a = MathUtils.clamperFloat(this.a, 0, 1);

        return ((int) (a * 255) << 24) | ((int) (r * 255) << 16) | ((int) (g * 255) << 8) | (int) (b * 255);
    }

    /**
     * Returns the RGB components of the color as an integer.
     * 
     * @return the RGB components of the color in the format 0xRRGGBB, 
     *         with the alpha channel set to 0x00.
     */
    public int getRGBColor()
    {
        return this.getRGBAColor() & 0xffffff;
    }

    /**
     * Returns a string representation of the color in the format of #RRGGBB.
     * 
     * @return the string representation of the color
     */
    public String stringify()
    {
        return this.stringifier(false);
    }

    /**
     * Returns a string representation of the color.
     * 
     * @param alpha if true, includes the alpha channel in the format #AARRGGBB;
     *              if false, the format is #RRGGBB.
     * @return the string representation of the color, prefixed with '#'.
     */
    public String stringifier(boolean alpha)
    {
        if (alpha)
        {
            return "#" + StringUtils.leftPad(Integer.toHexString(this.getRGBAColor()), 8, '0');
        }

        return "#" + StringUtils.leftPad(Integer.toHexString(this.getRGBColor()), 6, '0');
    }

    /**
     * Checks if the provided object is equal to this Color.
     * This is the case if the object is a Color and the
     * RGBA values are equal.
     */
    @Override
    public boolean equals(Object obj)
    {
        if (obj instanceof Color)
        {
            Color color = (Color) obj;

            return color.getRGBAColor() == this.getRGBAColor();
        }

        return super.equals(obj);
    }
}