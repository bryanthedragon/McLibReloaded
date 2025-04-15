package bryanthedragon.mclibreloaded.utils;

public interface IInterpolation
{
    public float interpolate(float a, float b, float x);

    public double interpolate(double a, double b, double x);


    public default String getName()
    {
        return I18n.format(this.getKey());
    }

    public String getKey();


    public default String getTooltip()
    {
        return I18n.format(this.getTooltipKey());
    }


    public default String getTooltipKey()
    {
        return "mclib.interpolations.tooltips." + this.getKey();
    };
}
