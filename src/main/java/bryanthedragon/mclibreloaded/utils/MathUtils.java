package bryanthedragon.mclibreloaded.utils;

public class MathUtils
{
    /**
     * Clamp an integer value between a given range.
     *
     * @param x the value to clamp
     * @param min the minimum value of the range
     * @param max the maximum value of the range
     * @return the clamped value
     */
    public static int clamp(int x, int min, int max)
    {
        return x < min ? min : (x > max ? max : x);
    }

    /**
     * Clamp a value of type T to the range [min, max]
     *
     * @param x the value to clamp
     * @param min the minimum value of the range
     * @param max the maximum value of the range
     * @return the clamped value
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static<T extends Comparable> T clamper(T x, T min, T max)
    {
        return x.compareTo(min) < 0 ? min : (x.compareTo(max) > 0 ? max : x);
    }

    /**
     * Clamp a float value between a given range.
     *
     * @param x the value to clamp
     * @param min the minimum value of the range
     * @param max the maximum value of the range
     * @return the clamped value
     */
    public static float clamperFloat(float x, float min, float max)
    {
        return x < min ? min : (x > max ? max : x);
    }

    /**
     * Clamp a double value between a given range.
     *
     * @param x the value to clamp
     * @param min the minimum value of the range
     * @param max the maximum value of the range
     * @return the clamped value
     */
    public static double clamperDouble(double x, double min, double max)
    {
        return x < min ? min : (x > max ? max : x);
    }

    /**
     * Clamp a long value between a given range.
     * 
     * @param x the value to clamp
     * @param min the minimum value of the range
     * @param max the maximum value of the range
     * @return the clamped value
     */
    public static long clamperLong(long x, long min, long max)
    {
        return x < min ? min : (x > max ? max : x);
    }

    /**
     * Cycle an integer value within a given range. If the value is out of the range, it is cycled around to the other end of the range.
     * 
     * @param x the value to cycle
     * @param min the minimum value of the range
     * @param max the maximum value of the range
     * @return the cycled value
     */
    public static int cyclerInt(int x, int min, int max)
    {
        return x < min ? max : (x > max ? min : x);
    }

    /**
     * Cycle a float value within a given range. If the value is out of the range, it is cycled around to the other end of the range.
     * 
     * @param x the value to cycle
     * @param min the minimum value of the range
     * @param max the maximum value of the range
     * @return the cycled value
     */
    public static float cyclerFloat(float x, float min, float max)
    {
        return x < min ? max : (x > max ? min : x);
    }

    /**
     * Cycle a double value within a given range. If the value is out of the range, it is cycled around to the other end of the range.
     * 
     * @param x the value to cycle
     * @param min the minimum value of the range
     * @param max the maximum value of the range
     * @return the cycled value
     */
    public static double cyclerDouble(double x, double min, double max)
    {
        return x < min ? max : (x > max ? min : x);
    }

    /**
     * Calculate the index of a grid cell based on the given coordinates and grid dimensions.
     * 
     * @param x the x-coordinate
     * @param y the y-coordinate
     * @param size the size of each grid cell
     * @param width the total width of the grid
     * @return the calculated index of the grid cell
     */
    public static int gridIndex(int x, int y, int size, int width)
    {
        x = x / size;
        y = y / size;

        return x + y * width / size;
    }

    /**
     * Calculate the number of rows needed to accommodate a given number of elements
     * when each element has a specified size and the total width is given.
     * 
     * @param count the number of elements
     * @param size the size of each element
     * @param width the total width available
     * @return the number of rows required
     */
    public static int gridRows(int count, int size, int width)
    {
        double x = count * size / (double) width;

        return count <= 0 ? 1 : (int) Math.ceil(x);
    }

    /**
     * Remove 360 degrees of flips between previous and current angle.
     * A difference greater or equal than 180 degrees will be treated as a flip.
     * @param prev previous angle in radians
     * @param current current angle in radians
     * @return cleaned current in radians
     */
    public static float filterFlips(float prev, float current)
    {
        final float sign = (prev > current) ? 1 : -1;
        final float add = sign * 2.0F * (float) Math.PI;

        while (Math.abs(prev - current) >= Math.PI)
        {
            current += add;
        }

        return current;
    }
}