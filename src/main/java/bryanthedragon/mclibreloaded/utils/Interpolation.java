package bryanthedragon.mclibreloaded.utils;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public enum Interpolation implements IInterpolation
{
    LINEAR("linear")
    {

        public float interpolateFloat(float a, float b, float x)
        {
            return Interpolations.lerp(a, b, x);
        }


        public double interpolateDouble(double a, double b, double x)
        {
            return Interpolations.lerp(a, b, x);
        }
    },
    QUAD_IN("quad_in")
    {

        public float interpolateFloat(float a, float b, float x)
        {
            return a + (b - a) * x * x;
        }


        public double interpolateDouble(double a, double b, double x)
        {
            return a + (b - a) * x * x;
        }
    },
    QUAD_OUT("quad_out")
    {
        /**
         * {@inheritDoc}
         */

        public float interpolateFloat(float a, float b, float x)
        {
            return a - (b - a) * x * (x - 2);
        }

        /**
         * {@inheritDoc}
         */

        public double interpolateDouble(double a, double b, double x)
        {
            return a - (b - a) * x * (x - 2);
        }
    },
    QUAD_INOUT("quad_inout")
    {
        public float interpolateFloat(float a, float b, float x)
        {
            x *= 2;

            if (x < 1F) 
            {
                return a + (b - a) / 2 * x * x;
            }

            x -= 1;

            return a - (b - a) / 2 * (x * (x - 2) - 1);
        }


        public double interpolateDouble(double a, double b, double x)
        {
            x *= 2;

            if (x < 1F) 
            {
                return a + (b - a) / 2 * x * x;
            }

            x -= 1;

            return a - (b - a) / 2 * (x * (x - 2) - 1);
        }
    },
    CUBIC_IN("cubic_in")
    {

        public float interpolateFloat(float a, float b, float x)
        {
            return a + (b - a) * x * x * x;
        }


        public double interpolateDouble(double a, double b, double x)
        {
            return a + (b - a) * x * x * x;
        }
    },
    CUBIC_OUT("cubic_out")
    {

        public float interpolateFloat(float a, float b, float x)
        {
            x -= 1;
            return a + (b - a) * (x * x * x + 1);
        }


        public double interpolateDouble(double a, double b, double x)
        {
            x -= 1;
            return a + (b - a) * (x * x * x + 1);
        }
    },
    CUBIC_INOUT("cubic_inout")
    {
        public float interpolateFloat(float a, float b, float x)
        {
            x *= 2;

            if (x < 1F) 
            {
                return a + (b - a) / 2 * x * x * x;
            }

            x -= 2;

            return a + (b - a) / 2 * (x * x * x + 2);
        }


        public double interpolateDouble(double a, double b, double x)
        {
            x *= 2;

            if (x < 1F) 
            {
                return a + (b - a) / 2 * x * x * x;
            }

            x -= 2;

            return a + (b - a) / 2 * (x * x * x + 2);
        }
    },
    EXP_IN("exp_in")
    {

        public float interpolateFloat(float a, float b, float x)
        {
            return (float) this.interpolateDouble((double) a, b, x);
        }


        public double interpolateDouble(double a, double b, double x)
        {
            if (x == 0) 
            {
                return a;
            }

            double pow0 = Math.pow(2, -10);

            /*
             * The restrictions f(0) = a and f(1) = b will lead to the following formula instead of plain
             * 2^10*(x-1). This would change the derivative,
             * but with the plain formula the animation jumps when animating big values.
             */
            return (a + (b - a) / (1 - pow0) * (Math.pow(2, 10 * (x - 1))) - pow0);
        }
    },
    EXP_OUT("exp_out")
    {

        public float interpolateFloat(float a, float b, float x)
        {
            return (float) this.interpolateDouble((double) a, b, x);
        }


        public double interpolateDouble(double a, double b, double x)
        {
            if (x == 0)
            {
                return a;
            }

            double pow0 = Math.pow(2, -10);

            return a + (b - a) * (1 - (Math.pow(2, -10 * x) - pow0) * 1 / (1 - pow0));
        }
    },
    EXP_INOUT("exp_inout")
    {

        public float interpolateFloat(float a, float b, float x)
        {
            return (float) this.interpolateDouble((double) a, b, x);
        }


        public double interpolateDouble(double a, double b, double x)
        {
            if (x <= 0.5D) 
            {
                return Interpolation.EXP_IN.interpolateDouble(a, a + (b - a) / 2, x * 2);
            }

            return Interpolation.EXP_OUT.interpolateDouble(a + (b - a) / 2, b, x * 2 - 1);
        }
    },
    /* Following interpolations below were copied from: https://easings.net/ */
    BACK_IN("back_in")
    {

        public float interpolateFloat(float a, float b, float x)
        {
            final float c1 = 1.70158F;
            final float c3 = c1 + 1;

            return Interpolations.lerp(a, b, c3 * x * x * x - c1 * x * x);
        }


        public double interpolateDouble(double a, double b, double x)
        {
            final double c1 = 1.70158D;
            final double c3 = c1 + 1;

            return Interpolations.lerp(a, b, c3 * x * x * x - c1 * x * x);
        }
    },
    BACK_OUT("back_out")
    {

        public float interpolateFloat(float a, float b, float x)
        {
            final float c1 = 1.70158F;
            final float c3 = c1 + 1;

            return Interpolations.lerp(a, b, 1 + c3 * (float) Math.pow(x - 1, 3) + c1 * (float) Math.pow(x - 1, 2));
        }


        public double interpolateDouble(double a, double b, double x)
        {
            final double c1 = 1.70158D;
            final double c3 = c1 + 1;

            return Interpolations.lerp(a, b, 1 + c3 * Math.pow(x - 1, 3) + c1 * Math.pow(x - 1, 2));
        }
    },
    BACK_INOUT("back_inout")
    {

        public float interpolateFloat(float a, float b, float x)
        {
            final float c1 = 1.70158F;
            final float c2 = c1 * 1.525F;

            float factor = x < 0.5 ? ((float) Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2 : ((float) Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;

            return Interpolations.lerp(a, b, factor);
        }


        public double interpolateDouble(double a, double b, double x)
        {
            final double c1 = 1.70158D;
            final double c2 = c1 * 1.525D;

            double factor = x < 0.5 ? (Math.pow(2 * x, 2) * ((c2 + 1) * 2 * x - c2)) / 2 : (Math.pow(2 * x - 2, 2) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2;

            return Interpolations.lerp(a, b, factor);
        }
    },
    ELASTIC_IN("elastic_in")
    {

        public float interpolateFloat(float a, float b, float x)
        {
            final float c4 = (2 * (float) Math.PI) / 3;

            float factor = x == 0 ? 0 : (x == 1 ? 1 : -(float) Math.pow(2, 10 * x - 10) * (float) Math.sin((x * 10 - 10.75) * c4));

            return Interpolations.lerp(a, b, factor);
        }


        public double interpolateDouble(double a, double b, double x)
        {
            final double c4 = (2 * (float) Math.PI) / 3;

            double factor = x == 0 ? 0 : (x == 1 ? 1 : -Math.pow(2, 10 * x - 10) * Math.sin((x * 10 - 10.75) * c4));

            return Interpolations.lerp(a, b, factor);
        }
    },
    ELASTIC_OUT("elastic_out")
    {

        public float interpolateFloat(float a, float b, float x)
        {
            final float c4 = (2 * (float) Math.PI) / 3;

            float factor = x == 0 ? 0 : (x == 1 ? 1 : (float) Math.pow(2, -10 * x) * (float) Math.sin((x * 10 - 0.75) * c4) + 1);

            return Interpolations.lerp(a, b, factor);
        }


        public double interpolateDouble(double a, double b, double x)
        {
            final double c4 = (2 * Math.PI) / 3;

            double factor = x == 0 ? 0 : (x == 1 ? 1 : Math.pow(2, -10 * x) * Math.sin((x * 10 - 0.75) * c4) + 1);

            return Interpolations.lerp(a, b, factor);
        }
    },
    ELASTIC_INOUT("elastic_inout")
    {

        public float interpolateFloat(float a, float b, float x)
        {
            final float c5 = (2 * (float) Math.PI) / 4.5F;

            float factor = x == 0 ? 0 : (x == 1 ? 1 : (x < 0.5 ? -((float) Math.pow(2, 20 * x - 10) * (float) Math.sin((20 * x - 11.125) * c5)) / 2 : ((float) Math.pow(2, -20 * x + 10) * (float) Math.sin((20 * x - 11.125) * c5)) / 2 + 1));

            return Interpolations.lerp(a, b, factor);
        }


        /**
         * Performs an elastic-in-out interpolation between two double values.
         *
         * This method calculates an elastic effect for the interpolation factor `x`,
         * which ranges from 0 to 1. The elastic effect is similar to a spring easing
         * in and out, with the motion overshooting and bouncing back towards the
         * target value.
         *
         * @param a the starting double value
         * @param b the ending double value
         * @param x the interpolation position, where 0 <= x <= 1
         * @return the interpolated double value with an elastic-in-out effect
         */
        public double interpolateDouble(double a, double b, double x)
        {
            final double c5 = (2 * Math.PI) / 4.5;

            double factor = x == 0 ? 0 : (x == 1 ? 1 : (x < 0.5 ? -(Math.pow(2, 20 * x - 10) * Math.sin((20 * x - 11.125) * c5)) / 2 : (Math.pow(2, -20 * x + 10) * Math.sin((20 * x - 11.125) * c5)) / 2 + 1));

            return Interpolations.lerp(a, b, factor);
        }
    },
    BOUNCE_IN("bounce_in")
    {

        /**
         * Performs a bounce-in interpolation between two float values.
         * 
         * @param a the initial value
         * @param b the final value
         * @param x the position between 0 and 1
         * @return the interpolated float value
         * 
         * @see #interpolateDouble(double, double, double)
         */
        public float interpolateFloat(float a, float b, float x)
        {
            return Interpolations.lerp(a, b, 1 - BOUNCE_OUT.interpolateFloat(0, 1, 1 - x));
        }


        /**
         * Performs a bounce-out interpolation between two double values.
         * 
         * @param a the initial value
         * @param b the final value
         * @param x the interpolation position (0 = a, 1 = b)
         * @return the value at the given position
         */
        public double interpolateDouble(double a, double b, double x)
        {
            return Interpolations.lerp(a, b, 1 - BOUNCE_OUT.interpolateDouble(0, 1, 1 - x));
        }
    },
    BOUNCE_OUT("bounce_out")
    {

        /**
         * Performs a bounce-out interpolation between two float values.
         *
         * This method calculates a bounce effect for the interpolation factor `x`,
         * which ranges from 0 to 1. The bounce effect is similar to a ball dropping
         * and bouncing off the ground, with the bounces gradually decreasing in height.
         *
         * @param a the starting float value
         * @param b the ending float value
         * @param x the interpolation factor, where 0 <= x <= 1
         * @return the interpolated float value with a bounce-out effect
         */
        public float interpolateFloat(float a, float b, float x)
        {
            final float n1 = 7.5625F;
            final float d1 = 2.75F;
            float factor;

            if (x < 1 / d1)
            {
                factor = n1 * x * x;
            }
            else if (x < 2 / d1)
            {
                factor = n1 * (x -= 1.5F / d1) * x + 0.75F;
            }
            else if (x < 2.5 / d1)
            {
                factor = n1 * (x -= 2.25F / d1) * x + 0.9375F;
            }
            else
            {
                factor = n1 * (x -= 2.625F / d1) * x + 0.984375F;
            }

            return Interpolations.lerp(a, b, factor);
        }


        /**
         * Interpolates between two double values using a bounce easing in/out function.
         * The provided value, x, is expected to be between 0 and 1, where 0 is the start value and 1 is the end value.
         * This function will return the interpolated value between the two provided values based on the given easing function.
         * The easing function is a bounce function, which is a ease-in, ease-out function that bounces at the end.
         * This function is the same as {@link #interpolateFloat(float, float, float)}, but for doubles.
         * @param a the start value
         * @param b the end value
         * @param x the value to interpolate between 0 and 1
         * @return the interpolated value
         */
        public double interpolateDouble(double a, double b, double x)
        {
            final double n1 = 7.5625;
            final double d1 = 2.75;
            double factor;

            if (x < 1 / d1)
            {
                factor = n1 * x * x;
            }
            else if (x < 2 / d1)
            {
                factor = n1 * (x -= 1.5 / d1) * x + 0.75;
            }
            else if (x < 2.5 / d1)
            {
                factor = n1 * (x -= 2.25 / d1) * x + 0.9375;
            }
            else
            {
                factor = n1 * (x -= 2.625 / d1) * x + 0.984375;
            }

            return Interpolations.lerp(a, b, factor);
        }
    },
    BOUNCE_INOUT("bounce_inout")
    {

        /**
         * Interpolates between two float values using a bounce easing in/out function.
         * This function is the same as {@link #interpolateDouble(double, double, double)}, but for floats.
         *
         * @param a the starting value
         * @param b the ending value
         * @param x the progress between the two values, typically between 0 and 1
         * @return the interpolated value
         */
        public float interpolateFloat(float a, float b, float x)
        {
            float factor = x < 0.5
                ? (1 - BOUNCE_OUT.interpolateFloat(0, 1, 1 - 2 * x)) / 2
                : (1 + BOUNCE_OUT.interpolateFloat(0, 1, 2 * x - 1)) / 2;

            return Interpolations.lerp(a, b, factor);
        }


        /**
         * Interpolates between two double values using a bounce easing in/out function.
         *
         * @param a the starting value
         * @param b the ending value
         * @param x the progress between the two values, typically between 0 and 1
         * @return the interpolated value
         */
        public double interpolateDouble(double a, double b, double x)
        {
            double factor = x < 0.5
                ? (1 - BOUNCE_OUT.interpolateDouble(0, 1, 1 - 2 * x)) / 2
                : (1 + BOUNCE_OUT.interpolateDouble(0, 1, 2 * x - 1)) / 2;

            return Interpolations.lerp(a, b, factor);
        }
    },
    SINE_IN("sine_in")
    {

        /**
         * Interpolates between two float values using a sine easing in function.
         *
         * @param a the starting value
         * @param b the ending value
         * @param x the progress between the two values, typically between 0 and 1
         * @return the interpolated value
         */
        public float interpolateFloat(float a, float b, float x)
        {
            float factor = 1 - (float) Math.cos((x * Math.PI) / 2);

            return Interpolations.lerp(a, b, factor);
        }


        /**
         * @param a the start value
         * @param b the end value
         * @param x the interpolation factor, clamped between 0 and 1
         * @return a value between a and b, interpolated using the sine in function.
         */
        public double interpolateDouble(double a, double b, double x)
        {
            double factor = 1 - (float) Math.cos((x * Math.PI) / 2);

            return Interpolations.lerp(a, b, factor);
        }
    },
    SINE_OUT("sine_out")
    {

        /**
         * Interpolates between two float values using a sine easing out function.
         *
         * @param a the starting value
         * @param b the ending value
         * @param x the progress between the two values, typically between 0 and 1
         * @return the interpolated value
         */
        public float interpolateFloat(float a, float b, float x)
        {
            float factor = (float) Math.sin((x * Math.PI) / 2);

            return Interpolations.lerp(a, b, factor);
        }


        /**
         * Interpolates between two double values using a sine easing in/out function.
         *
         * @param a the starting value
         * @param b the ending value
         * @param x the progress between the two values, typically between 0 and 1
         * @return the interpolated value
         */
        public double interpolateDouble(double a, double b, double x)
        {
            double factor = Math.sin((x * Math.PI) / 2);

            return Interpolations.lerp(a, b, factor);
        }
    },
    SINE_INOUT("sine_inout")
    {

        /**
         * Interpolates between two float values using a sine easing in/out function.
         *
         * @param a the starting value
         * @param b the ending value
         * @param x the progress between the two values, typically between 0 and 1
         * @return the interpolated value
         */
        public float interpolateFloat(float a, float b, float x)
        {
            float factor = (float) (-(Math.cos(Math.PI * x) - 1) / 2);

            return Interpolations.lerp(a, b, factor);
        }


        /**
         * Interpolates between two double values using a quartic easing in function.
         *
         * @param a the starting value
         * @param b the ending value
         * @param x the progress between the two values, typically between 0 and 1
         * @return the interpolated value
         */
        public double interpolateDouble(double a, double b, double x)
        {
            double factor = -(Math.cos(Math.PI * x) - 1) / 2;

            return Interpolations.lerp(a, b, factor);
        }
    },
    QUART_IN("quart_in")
    {

        /**
         * Interpolates between two float values using a quartic easing in function.
         *
         * @param a the starting value
         * @param b the ending value
         * @param x the progress between the two values, typically between 0 and 1
         * @return the interpolated value
         */
        public float interpolateFloat(float a, float b, float x)
        {
            float factor = x * x * x * x;

            return Interpolations.lerp(a, b, factor);
        }


        /**
         * Interpolates between two double values using a quartic easing in function.
         *
         * @param a the starting value
         * @param b the ending value
         * @param x the progress between the two values, typically between 0 and 1
         * @return the interpolated value
         */
        public double interpolateDouble(double a, double b, double x)
        {
            double factor = x * x * x * x;

            return Interpolations.lerp(a, b, factor);
        }
    },
    QUART_OUT("quart_out")
    {

        /**
         * Interpolates between two float values using a quartic easing out function.
         * 
         * @param a the starting value
         * @param b the ending value
         * @param x the progress between the two values, typically between 0 and 1
         * @return the interpolated value
         */
        public float interpolateFloat(float a, float b, float x)
        {
            float factor = 1 - (float) Math.pow(1 - x, 4);

            return Interpolations.lerp(a, b, factor);
        }


        /**
         * Interpolates between two double values using a quartic easing out function.
         * 
         * @param a the starting value
         * @param b the ending value
         * @param x the progress between the two values, typically between 0 and 1
         * @return the interpolated value
         */
        public double interpolateDouble(double a, double b, double x)
        {
            double factor = 1 - Math.pow(1 - x, 4);

            return Interpolations.lerp(a, b, factor);
        }
    },
    QUART_INOUT("quart_inout")
    {

        /**
         * Interpolates between two float values using a quintic easing in/out function.
         * 
         * @param a the starting value
         * @param b the ending value
         * @param x the progress between the two values, typically between 0 and 1
         * @return the interpolated value
         */
        public float interpolateFloat(float a, float b, float x)
        {
            float factor = x < 0.5 ? 8 * x * x * x * x : 1 - (float) Math.pow(-2 * x + 2, 4) / 2;

            return Interpolations.lerp(a, b, factor);
        }


        /**
         * Interpolates between two double values using a quartic easing in/out function.
         * 
         * @param a the starting value
         * @param b the ending value
         * @param x the progress between the two values, typically between 0 and 1
         * @return the interpolated value
         */
        public double interpolateDouble(double a, double b, double x)
        {
            double factor = x < 0.5 ? 8 * x * x * x * x : 1 - Math.pow(-2 * x + 2, 4) / 2;

            return Interpolations.lerp(a, b, factor);
        }
    },
    QUINT_IN("quint_in")
    {

        /**
         * Interpolates between two float values using a quintic easing in function.
         * 
         * @param a the starting value
         * @param b the ending value
         * @param x the progress between the two values, typically between 0 and 1
         * @return the interpolated value
         */
        public float interpolateFloat(float a, float b, float x)
        {
            float factor = x * x * x * x * x;

            return Interpolations.lerp(a, b, factor);
        }


        /**
         * Interpolates between two double values using a quintic easing in function.
         * 
         * @param a the starting value
         * @param b the ending value
         * @param x the progress between the two values, typically between 0 and 1
         * @return the interpolated value
         */
        public double interpolateDouble(double a, double b, double x)
        {
            double factor = x * x * x * x * x;

            return Interpolations.lerp(a, b, factor);
        }
    },
    QUINT_OUT("quint_out")
    {

        /**
         * Interpolates between two float values using a quintic easing out function.
         * 
         * @param a the starting value
         * @param b the ending value
         * @param x the progress between the two values, typically between 0 and 1
         * @return the interpolated value
         */
        public float interpolateFloat(float a, float b, float x)
        {
            float factor = 1 - (float) Math.pow(1 - x, 5);

            return Interpolations.lerp(a, b, factor);
        }


        /**
         * Interpolates between two double values using a quintic easing out function.
         *
         * @param a the start value
         * @param b the end value
         * @param x the interpolation factor, typically between 0 and 1
         * @return the interpolated value
         */
        public double interpolateDouble(double a, double b, double x)
        {
            double factor = 1 - Math.pow(1 - x, 5);

            return Interpolations.lerp(a, b, factor);
        }
    },
    QUINT_INOUT("quint_inout")
    {

        /**
         * Interpolates a float value between the given a and b values by given x factor, using quintic easing in/out function.
         * The function is defined as f(x) = x < 0.5 ? 16*x^5 : 1 - (-2*x + 2)^5 / 2.
         * It is useful for creating smooth, yet sharp movements.
         *
         * @param a the initial value
         * @param b the final value
         * @param x the interpolation factor, ranged from 0 to 1
         * @return the interpolated value
         */
        public float interpolateFloat(float a, float b, float x)
        {
            float factor = x < 0.5 ? 16 * x * x * x * x * x : 1 - (float) Math.pow(-2 * x + 2, 5) / 2;

            return Interpolations.lerp(a, b, factor);
        }


        /**
         * Interpolates a double value between the given a and b values by given x factor, using quintic easing in/out function.
         * The function is defined as f(x) = x < 0.5 ? 16*x^5 : 1 - (-2*x + 2)^5 / 2.
         * It is useful for creating smooth, yet sharp movements.
         *
         * @param a the initial value
         * @param b the final value
         * @param x the interpolation factor, ranged from 0 to 1
         * @return the interpolated value
         */
        public double interpolateDouble(double a, double b, double x)
        {
            double factor = x < 0.5 ? 16 * x * x * x * x * x : 1 - Math.pow(-2 * x + 2, 5) / 2;

            return Interpolations.lerp(a, b, factor);
        }
    },
    CIRCLE_IN("circle_in")
    {

        public float interpolateFloat(float a, float b, float x)
        {
            x = MathUtils.clamperFloat(x, 0, 1);

            float factor = 1 - (float) Math.sqrt(1 - Math.pow(x, 2));

            return Interpolations.lerp(a, b, factor);
        }


        public double interpolateDouble(double a, double b, double x)
        {
            x = MathUtils.clamperDouble(x, 0, 1);

            double factor = 1 - (float) Math.sqrt(1 - Math.pow(x, 2));

            return Interpolations.lerp(a, b, factor);
        }
    },
    CIRCLE_OUT("circle_out")
    {

        public float interpolateFloat(float a, float b, float x)
        {
            x = MathUtils.clamperFloat(x, 0, 1);

            float factor = (float) Math.sqrt(1 - Math.pow(x - 1, 2));

            return Interpolations.lerp(a, b, factor);
        }


        public double interpolateDouble(double a, double b, double x)
        {
            x = MathUtils.clamperDouble(x, 0, 1);

            double factor = Math.sqrt(1 - Math.pow(x - 1, 2));

            return Interpolations.lerp(a, b, factor);
        }
    },
    CIRCLE_INOUT("circle_inout")
    {

        public float interpolateFloat(float a, float b, float x)
        {
            x = MathUtils.clamperFloat(x, 0, 1);

            float factor = x < 0.5 ? (float) (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2 : (float) (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2;

            return Interpolations.lerp(a, b, factor);
        }


        public double interpolateDouble(double a, double b, double x)
        {
            x = MathUtils.clamperDouble(x, 0, 1);

            double factor = x < 0.5 ? (1 - Math.sqrt(1 - Math.pow(2 * x, 2))) / 2 : (Math.sqrt(1 - Math.pow(-2 * x + 2, 2)) + 1) / 2;

            return Interpolations.lerp(a, b, factor);
        }
    };

    public final String key;

    private Interpolation(String key)
    {
        this.key = key;
    }

    /**
     * Returns the key for the interpolation's name, which is used for identifying
     * the interpolation type. This key is combined with a prefix and the internal
     * interpolation key to create a unique identifier for each interpolation type.
     * 
     * @return the key for the interpolation's name
     */
    @OnlyIn(Dist.CLIENT)
    public String getKey()
    {
        return "mclibreloaded.interpolations." + this.key;
    }

    @OnlyIn(Dist.CLIENT)
    public String getTooltipKey()
    {
        return "mclibreloaded.interpolations.tooltips." + this.key;
    }
}