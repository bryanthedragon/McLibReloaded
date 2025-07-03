package bryanthedragon.mclibreloaded.utils.keyframes;

import bryanthedragon.mclibreloaded.utils.IInterpolation;
import bryanthedragon.mclibreloaded.utils.Interpolations;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


public class KeyframeInterpolations
{
    public static final IInterpolation CONSTANT = new IInterpolation()
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            return a;
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            return a;
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public String getKey()
        {
            return "mclib.interpolations.const";
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public String getTooltipKey()
        {
            return "mclib.interpolations.tooltips.const";
        }
    };

    public static final IInterpolation HERMITE = new IInterpolation()
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            return (float) Interpolations.cubicHermite(a, a, b, b, x);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            return Interpolations.cubicHermite(a, a, b, b, x);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public String getKey()
        {
            return "mclib.interpolations.hermite";
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public String getTooltipKey()
        {
            return "mclib.interpolations.tooltips.hermite";
        }
    };

    public static final IInterpolation BEZIER = new IInterpolation()
    {
        @Override
        public float interpolate(float a, float b, float x)
        {
            return (float) Interpolations.cubicHermite(a, a, b, b, x);
        }

        @Override
        public double interpolate(double a, double b, double x)
        {
            return Interpolations.cubicHermite(a, a, b, b, x);
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public String getKey()
        {
            return "mclib.interpolations.bezier";
        }

        @Override
        @OnlyIn(Dist.CLIENT)
        public String getTooltipKey()
        {
            return "mclib.interpolations.tooltips.bezier";
        }
    };
}