package bryanthedragon.mclibreloaded.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import bryanthedragon.mclibreloaded.utils.MathUtils;

import javax.annotation.Nonnull;

/**
 * GenericNumberValue can be used for the primitive datatype wrappers
 * that extend the {@link Number} class and implement {@link Comparable}.
 *
 * <br><br>
 * Subclasses need to override {@link #getNullValue()} to ensure
 * that the internal values never take the null state!
 * <br>
 * This means {@link #value}, {@link #defaultValue}, {@link #min} and {@link #max} shall never be null!
 * <br>
 * The variable {@link #serverValue} can be null.
 * @param <T>
 */
public abstract class GenericNumberValue<T extends Number & Comparable<T>> extends GenericValue<T>
{
    protected T min;
    protected T max;

    /**
     * If defaultValue, min or max are null, the value of {@link #getNullValue()} will be set for the variable with null.
     * @param id
     * @param defaultValue
     * @param min
     * @param max
     */
    public GenericNumberValue(String id, @Nonnull T defaultValue, @Nonnull T min, @Nonnull T max)
    {
        super(id);

        this.min = (min == null) ? this.getNullValue() : min;
        this.max = (max == null) ? this.getNullValue() : max;

        this.defaultValue = (defaultValue == null) ? this.getNullValue() : defaultValue;

        this.reset();
    }

    @Override
    public void setValue(Object value)
    {
        if (value == null)
        {
            return;
        }

        if (value instanceof Number) {
            this.set(this.numberToValue((Number) value));
        }
    }

    protected abstract T numberToValue(Number number);

    /**
     * The value will be clamped between {@link #min} and {@link #max}
     * @param value
     */
    @Override
    public void set(T value)
    {
        this.value = MathUtils.clamp((value == null) ? this.getNullValue() : value, this.min, this.max);

        this.saveLater();
    }

    public T getMin()
    {
        return this.min;
    }

    public T getMax()
    {
        return this.max;
    }

    @Override
    protected abstract T getNullValue();

    /**
     * @return true when the Value is a whole number, like integer, byte or long.
     */
    public abstract boolean isInteger();

    @Override
    public JsonElement valueToJSON()
    {
        return new JsonPrimitive(this.value);
    }
}
