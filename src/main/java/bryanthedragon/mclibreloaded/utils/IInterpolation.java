package bryanthedragon.mclibreloaded.utils;


import net.minecraft.client.resources.language.I18n;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IInterpolation
{
    public float interpolateFloat(float a, float b, float x);

    public double interpolateDouble(double a, double b, double x);

    /**
     * Returns the translated name of the interpolation using the key provided by getKey().
     * This method utilizes the I18n format to convert the key into a human-readable string.
     * It is intended to be used on the client side only.
     * 
     * @return the translated name of the interpolation
     */
    @OnlyIn(Dist.CLIENT)
    public default String getName()
    {
        return I18n.format(this.getKey());
    }

    /**
     * Returns the key for the interpolation's name, which is translated to human-readable
     * string using I18n.
     * @return the key for the interpolation's name
     */
    @OnlyIn(Dist.CLIENT)
    public String getKey();

    /**
     * Returns the tooltip for the interpolation, which is the human-readable string
     * translated from the key returned by getTooltipKey() using I18n.
     * @return the tooltip for the interpolation
     */
    @OnlyIn(Dist.CLIENT)
    public default String getTooltip()
    {
        return I18n.format(this.getTooltipKey());
    }

    /**
     * Returns the key for the interpolation's tooltip, which is translated to human-readable
     * string using I18n. The tooltip is a longer description of the interpolation's behavior
     * and can be used to display more detailed information about the interpolation to users.
     * @return the key for the interpolation's tooltip
     */
    @OnlyIn(Dist.CLIENT)
    public String getTooltipKey();
}
