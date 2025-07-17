package bryanthedragon.mclibreloaded.config.values;

import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.GuiElement;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.input.GuiTrackpadElement;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiLabel;
import bryanthedragon.mclibreloaded.client.gui.utils.Elements;
import bryanthedragon.mclibreloaded.client.gui.utils.keys.IKey;
import bryanthedragon.mclibreloaded.config.gui.GuiConfigPanel;
import bryanthedragon.mclibreloaded.utils.Interpolation;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.Tag;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.List;

public class ValueFloat extends GenericNumberValue<Float> implements IServerValue, IConfigGuiProvider
{
    public ValueFloat(String id)
    {
        super(id, 0F, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
    }

    public ValueFloat(String id, float defaultValue)
    {
        super(id, defaultValue, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
    }

    public ValueFloat(String id, float defaultValue, float min, float max)
    {
        super(id, defaultValue, min, max);
    }

    /**
     * Resets the server-specific value to null, effectively clearing any value that was set specifically for the server.
     */
    public void resetServer()
    {
        this.serverValue = null;
    }

    /**
     * @return the default value that this type produces when not being initialized.
     *         This is used in {@link #set(T)}, for example, to avoid null values for primitive datatype wrappers.
     */
    protected Float getNullValue()
    {
        return 0F;
    }

    /**
     * Converts a given Number to the type of this value (float in this case).
     * @param number the number to convert
     * @return the converted number
     */
    protected Float numberToValue(Number number) 
    {
        return number.floatValue();
    }

    /**
     * @return true when the Value is a whole number, like integer, byte or long.
     */
    public boolean isInteger()
    {
        return false;
    }

    /**
     * Returns a list of GuiElements that represent this value in the configuration GUI.
     * <br>
     * The returned list will contain a single element, which is a GuiElement that
     * contains a label and a trackpad element.
     * <br>
     * The label will display the name of the value, and the trackpad element will
     * allow the user to edit the value.
     * <br>
     * The trackpad element will be configured to have a width of 90 pixels.
     * <br>
     * The element will also have a tooltip that displays the comment of the value.
     * @param mc the minecraft instance
     * @param gui the containing GuiConfigPanel
     * @return a list of GuiElements that represent this value
     */
    @OnlyIn(Dist.CLIENT)
    public List<GuiElement> getFields(Minecraft mc, GuiConfigPanel gui)
    {
        GuiElement element = new GuiElement(mc);
        GuiLabel label = Elements.label(IKey.lang(this.getLabelKey()), 0).anchor(0, 0.5F);
        GuiTrackpadElement trackpad = new GuiTrackpadElement(mc, this);
        trackpad.flex().w(90);
        element.flex().row(0).preferred(0).height(20);
        element.add(label, trackpad.removeTooltip());
        return Arrays.asList(element.tooltip(IKey.lang(this.getCommentKey())));
    }

    /**
     * Sets the current value of this ValueFloat instance from the specified JSON element.
     * The specified JSON element should be a JSON primitive that can be converted to a float.
     * 
     * @param element the JSON element to read from
     */
    public void valueFromJSON(JsonElement element)
    {
        this.setBaseValue(element.getAsFloat());
    }
    /**
     * Sets the current value of this ValueFloat instance from the specified NBT tag.
     * The specified NBT tag should be a FloatTag that contains a float value.
     * If the tag is not a FloatTag, this method will not change the value of this ValueFloat instance.
     * @param tag the NBT tag to read from
     */
    public void valueFromNBT(Tag tag)
    {
        if (tag instanceof FloatTag)
        {
            this.setValue(((FloatTag) tag).asFloat());
        }
    }

    /**
     * Converts the current value of this ValueFloat to an NBT tag.
     * The returned tag is of type FloatTag, representing the float value.
     * @return an NBT tag representing the current value of this ValueFloat
     */
    @SuppressWarnings("removal")
    public Tag valueToNBT()
    {
        return new FloatTag(this.value);
    }

    /**
     * Attempts to parse the specified value as a float, and set it as the current value of this ValueFloat instance.
     * If the parsing fails, the value is not changed.
     * @param value the string to parse
     * @return false, always
     */
    public boolean parseFromCommand(String value)
    {
        try
        {
            this.setBaseValue(Float.parseFloat(value));
        }
        catch (Exception e)
        {

        }
        return false;
    }

    /**
     * Copies the value from the specified ValueFloat instance to this ValueFloat instance.
     * <br>
     * If the specified value is not a ValueFloat, this method does nothing.
     * @param value the ValueFloat to copy from
     */
    public void copy(Value value)
    {
        if (value instanceof ValueFloat)
        {
            this.setBaseValue(((ValueFloat) value).value);
        }
    }

    /**
     * Copies the server value from the specified ValueFloat instance to this ValueFloat instance.
     * <br>
     * If the specified value is not a ValueFloat, this method does nothing.
     * @param value the ValueFloat to copy the server value from
     */
    public void copyServer(Value value)
    {
        if (value instanceof ValueFloat)
        {
            this.serverValue = ((ValueFloat) value).value;
        }
    }

    /**
     * Reads the default, min, max and current values of this ValueFloat from the specified ByteBuffer.
     * The default value is read as a float, the min and max values are read as floats, and the current value is read
     * by calling {@link #valueFromBytes(ByteBuf)}.
     * @param buffer the ByteBuffer to read from
     */
    public void fromBytes(ByteBuf buffer)
    {
        superFromBytes(buffer);
        this.defaultValue = buffer.readFloat();
        this.min = buffer.readFloat();
        this.max = buffer.readFloat();
        this.valueFromBytes(buffer);
    }

    /**
     * Writes the default, min, max, and current values of this ValueFloat to the specified ByteBuffer and
     * calls {@link GenericBaseValue#superToBytes(ByteBuf)}. The default, min, and max values are written
     * as floats, and the current value is written by calling {@link #valueToBytes(ByteBuf)}.
     * @param buffer the ByteBuffer to write to
     */
    public void toBytes(ByteBuf buffer)
    {
        superToBytes(buffer);
        buffer.writeFloat(this.defaultValue);
        buffer.writeFloat(this.min);
        buffer.writeFloat(this.max);
        this.valueToBytes(buffer);
    }

    /**
     * Reads the current value of this ValueFloat from the specified ByteBuffer.
     * The current value is read as a float.
     * @param buffer the ByteBuffer to read from
     */
    public void valueFromBytes(ByteBuf buffer)
    {
        this.setBaseValue(buffer.readFloat());
    }

    /**
     * Writes the current value of this ValueFloat to the specified ByteBuffer.
     * The current value is written as a float.
     * @param buffer the ByteBuffer to write to
     */
    public void valueToBytes(ByteBuf buffer)
    {
        buffer.writeFloat(this.value);
    }

    /**
     * Returns a string representation of the current value of this ValueFloat.
     * @return a string representation of the current value of this ValueFloat
     */
    public String toString()
    {
        return Float.toString(this.value);
    }

    /**
     * Returns a new ValueFloat with the same id, default value, min and max as this instance.
     * The value of the new instance is set to the current value of this instance.
     * @return a new ValueFloat with the same id, default value, min and max as this instance
     */
    public ValueFloat copier()
    {
        ValueFloat clone = new ValueFloat(this.id, this.defaultValue, this.min, this.max);
        clone.value = this.value;
        return clone;
    }

    /**
     * Interpolates between this value and another value, using the specified interpolation
     * and factor.
     * @param interpolation the interpolation to use
     * @param to the value to interpolate to
     * @param factor a value between 0 and 1 indicating how far the interpolation should go towards the target value
     * @return the result of the interpolation
     */
    public Float interpolate(Interpolation interpolation, GenericBaseValue<?> to, float factor)
    {
        if (!(to.value instanceof Float))
        { 
            return this.value;
        }
        return interpolation.interpolateFloat(this.value, (Float) to.value, factor);
    }
}