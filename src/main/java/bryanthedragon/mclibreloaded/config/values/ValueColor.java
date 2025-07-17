package bryanthedragon.mclibreloaded.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;

import bryanthedragon.mclibreloaded.utils.Color;
import bryanthedragon.mclibreloaded.utils.Interpolation;

import javax.annotation.Nullable;

public class ValueColor extends GenericValue<Color>
{
    public ValueColor(String id)
    {
        super(id);
    }

    public ValueColor(String id, Color defaultValue)
    {
        super(id, defaultValue);
    }

    /**
     * Creates a copy of this ValueColor instance.
     *
     * @return a new ValueColor instance with the same id and defaultValue, and the current value.
     */
    public GenericBaseValue<Color> copier()
    {
        ValueColor clone = new ValueColor(this.id, this.defaultValue);
        clone.value = this.value;
        return clone;
    }

    /**
     * Copy the value from the specified ValueColor instance to this ValueColor instance.
     *
     * @param origin the origin ValueColor instance to copy from
     */
    public void copy(Value origin)
    {
        if (origin instanceof ValueColor)
        {
            this.set(((ValueColor) origin).value);
        }
    }

    /**
     * Reads the ValueColor from the specified ByteBuffer, and calls {@link GenericBaseValue#superFromBytes(ByteBuf)}.
     * The default value is read as an integer (rgba color) and the current value as an integer (rgba color).
     * @param buffer
     */
    public void fromBytes(ByteBuf buffer)
    {
        superFromBytes(buffer);
        this.defaultValue = new Color(buffer.readInt());
        this.valueFromBytes(buffer);
    }

    /**
     * Writes the current state of this ValueColor instance to the specified ByteBuffer and
     * calls {@link GenericBaseValue#superToBytes(ByteBuf)}. The default value is written
     * as an integer (rgba color) and the current value as an integer (rgba color).
     * @param buffer the ByteBuffer to write to
     */
    public void toBytes(ByteBuf buffer)
    {
        superToBytes(buffer);
        buffer.writeInt(this.defaultValue.getRGBAColor());
        this.valueToBytes(buffer);
    }

    /**
     * Reads the current value of this ValueColor instance from the specified ByteBuffer.
     * The current value is read as an integer (rgba color).
     * @param buffer the ByteBuffer to read from
     */
    public void valueFromBytes(ByteBuf buffer)
    {
        this.value = new Color(buffer.readInt());
    }

    /**
     * Writes the current value of this ValueColor instance to the specified ByteBuffer.
     * The current value is written as an integer (rgba color).
     * @param buffer the ByteBuffer to write to
     */
    public void valueToBytes(ByteBuf buffer)
    {
        buffer.writeInt(this.value.getRGBAColor());
    }

    /**
     * Sets the current value of this ValueColor instance from the specified JSON element.
     * The specified JSON element should be an integer (rgba color) or a string in the
     * format "0xAARRGGBB".
     * @param element the JSON element to read from
     */
    public void valueFromJSON(JsonElement element)
    {
        this.set(new Color(element.getAsInt()));
    }

    /**
     * Converts the current value of this ValueColor instance to a JSON element.
     * The returned JSON element is an integer (rgba color) representing the current color value.
     *
     * @return a JsonElement representing the current color value as an integer
     */
    public JsonElement valueToJSON()
    {
        return new JsonPrimitive(this.value.getRGBAColor());
    }

    /**
     * Sets the current value of this ValueColor instance from the specified NBT tag.
     * The specified tag should be of type IntTag, representing the color as an RGBA integer.
     * 
     * @param tag the NBT tag to read the color value from
     */
    public void valueFromNBT(Tag tag)
    {
        if (tag instanceof IntTag)
        {
            this.set(new Color(((IntTag) tag).getId()));
        }
    }

    /**
     * Converts the current value of this ValueColor instance to an NBT tag.
     * The returned tag is of type IntTag, representing the color as an RGBA integer.
     *
     * @return an NBT tag representing the current color value as an integer
     */
    @Nullable
    @SuppressWarnings("removal")
    public Tag valueToNBT()
    {
        return new IntTag(this.value.getRGBAColor());
    }

    /**
     * Interpolates between this color value and another color value, using the specified interpolation
     * and factor.
     * @param interpolation the interpolation to use
     * @param to the color value to interpolate to
     * @param factor a value between 0 and 1 indicating how far the interpolation should go towards the target value
     * @return a new color value that is the result of the interpolation
     */
    public Color interpolate(Interpolation interpolation, GenericBaseValue<?> to, float factor)
    {
        if (!(to.value instanceof Color)) 
        {
            return this.value.copier();
        }

        Color toC = (Color) to.value;
        Color interpolated = new Color();
        interpolated.r = interpolation.interpolateFloat(this.value.r, toC.r, factor);
        interpolated.g = interpolation.interpolateFloat(this.value.g, toC.g, factor);
        interpolated.b = interpolation.interpolateFloat(this.value.b, toC.b, factor);
        interpolated.a = interpolation.interpolateFloat(this.value.a, toC.a, factor);

        return interpolated;
    }
}
