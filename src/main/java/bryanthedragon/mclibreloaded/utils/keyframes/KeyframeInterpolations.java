package bryanthedragon.mclibreloaded.utils.keyframes;

import bryanthedragon.mclibreloaded.utils.IInterpolation;
import bryanthedragon.mclibreloaded.utils.Interpolations;

public class KeyframeInterpolations
{
    public static final IInterpolation CONSTANT = new IInterpolation()
    {
        public float interpolate(float a, float b, float x)
        {
            return a;
        }

        public double interpolate(double a, double b, double x)
        {
            return a;
        }

        public String getKey()
        {
            return "mclib.interpolations.const";
        }

        public String getTooltipKey()
        {
            return "mclib.interpolations.tooltips.const";
        }
    };

    public static final IInterpolation HERMITE = new IInterpolation()
    {
        public float interpolate(float a, float b, float x)
        {
            return (float) Interpolations.cubicHermite(a, a, b, b, x);
        }

        public double interpolate(double a, double b, double x)
        {
            return Interpolations.cubicHermite(a, a, b, b, x);
        }

        public String getKey()
        {
            return "mclib.interpolations.hermite";
        }

        public String getTooltipKey()
        {
            return "mclib.interpolations.tooltips.hermite";
        }
    };

    public static final IInterpolation BEZIER = new IInterpolation()
    {
        public float interpolate(float a, float b, float x)
        {
            return (float) Interpolations.cubicHermite(a, a, b, b, x);
        }

        public double interpolate(double a, double b, double x)
        {
            return Interpolations.cubicHermite(a, a, b, b, x);
        }

        public String getKey()
        {
            return "mclib.interpolations.bezier";
        }

        public String getTooltipKey()
        {
            return "mclib.interpolations.tooltips.bezier";
        }
    };
}
