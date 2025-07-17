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
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.Tag;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Arrays;
import java.util.List;

public class ValueDouble extends GenericNumberValue<Double> implements IServerValue, IConfigGuiProvider
{
    public ValueDouble(String id)
    {
        super(id, 0D, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public ValueDouble(String id, double defaultValue)
    {
        super(id, defaultValue, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    }

    public ValueDouble(String id, double defaultValue, double min, double max)
    {
        super(id, defaultValue, min, max);
    }

    /**
     * Resets the server-specific value to null, effectively 
     * clearing any value that was set specifically for the server.
     */
    public void resetServer()
    {
        this.serverValue = null;
    }

    /**
     * Returns the default value for this type, which is returned when the value or default value is null.
     * For double, this is 0.0.
     * @return the default value for this type
     */
    protected Double getNullValue()
    {
        return 0D;
    }

    /**
     * Converts a given Number to the type of this value (double in this case).
     * @param number the number to convert
     * @return the converted number
     */
    protected Double numberToValue(Number number) 
    {
        return number.doubleValue();
    }

    /**
     * Returns true if the Value is a whole number, like integer, byte or long.
     * <br>
     * This method is used by the GUI system to determine whether or not to show
     * a slider or a text input element.
     * <br>
     * The default implementation of this method returns false.
     * <br>
     * Subclasses should override this method if the type of the value is a whole number.
     * @return true if the Value is a whole number
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
     * Parses a given JsonElement and sets the value of this ValueDouble to the
     * parsed double value.
     * <br>
     * This method is used by the JsonConfig class to read the value from a
     * configuration file.
     * @param element the JsonElement to parse
     */
    public void valueFromJSON(JsonElement element)
    {
        this.setValue(element.getAsDouble());
    }

    /**
     * Parses a given String and sets the value of this ValueDouble to the parsed double value.
     * <br>
     * This method is used by the CommandConfig class to set the value from a command.
     * <br>
     * If the value cannot be parsed, this method returns false, which will cause the command to fail.
     * @param value the String to parse
     * @return false if the value cannot be parsed, true otherwise
     */
    public boolean parseFromCommand(String value)
    {
        try
        {
            this.setValue(Double.parseDouble(value));
        }
        catch (Exception e)
        {
            // If the value cannot be parsed, we return false
            // This will cause the command to fail
            return false;
        }

        return false;
    }

    /**
     * Copy the value from the specified ValueDouble instance to this ValueDouble instance.
     * @param origin the origin ValueDouble instance to copy from
     */
    public void copy(Value value)
    {
        if (value instanceof ValueDouble)
        {
            this.setValue(((ValueDouble) value).value);
        }
    }

    /**
     * Copies the server value from the specified ValueDouble instance to this ValueDouble instance.
     * If the specified Value is not an instance of ValueDouble, this method does nothing.
     * @param value the ValueDouble instance to copy the server value from
     */
    public void copyServer(Value value)
    {
        if (value instanceof ValueDouble)
        {
            this.serverValue = ((ValueDouble) value).value;
        }
    }

    /**
     * Reads the default, min, max and current values of this ValueDouble from the specified ByteBuffer.
     * The default value is read as a double, the min and max values are read as doubles, and the current value is read
     * by calling {@link #valueFromBytes(ByteBuf)}.
     * @param buffer the ByteBuffer to read from
     */
    public void fromBytes(ByteBuf buffer)
    {
        superFromBytes(buffer);
        this.defaultValue = buffer.readDouble();
        this.min = buffer.readDouble();
        this.max = buffer.readDouble();
        this.valueFromBytes(buffer);
    }

    /**
     * Writes the default, min, max, and current values of this ValueDouble to the specified ByteBuffer.
     * The default, min, and max values are written as doubles, and the current value is written
     * by calling {@link #valueToBytes(ByteBuf)}.
     * @param buffer the ByteBuffer to write to
     */
    public void toBytes(ByteBuf buffer)
    {
        superToBytes(buffer);
        buffer.writeDouble(this.defaultValue);
        buffer.writeDouble(this.min);
        buffer.writeDouble(this.max);
        this.valueToBytes(buffer);
    }

    /**
     * Reads the current value of this ValueDouble from the specified ByteBuffer.
     * The current value is read as a double.
     * @param buffer the ByteBuffer to read from
     */
    public void valueFromBytes(ByteBuf buffer)
    {
        this.setBaseValue(buffer.readDouble());
    }

    /**
     * Writes the current value of this ValueDouble instance to the specified ByteBuffer.
     * The current value is written as a double.
     * 
     * @param buffer the ByteBuffer to write to
     */
    public void valueToBytes(ByteBuf buffer)
    {
        buffer.writeDouble(this.value);
    }

    /**
     * Returns a string representation of the current value of this ValueDouble.
     * @return a string representation of the current value of this ValueDouble
     */
    public String toString()
    {
        return Double.toString(this.value);
    }

    /**
     * Sets the current value of this ValueDouble from the specified NBT tag.
     * The specified tag should be of type DoubleTag, representing the double value.
     * 
     * @param tag the NBT tag to read the double value from
     */
    public void valueFromNBT(Tag tag)
    {
        if (tag instanceof DoubleTag)
        {
            this.setValue(((DoubleTag) tag).asDouble());
        }
    }

    /**
     * Returns a DoubleTag representing the current value of this ValueDouble.
     * @return a DoubleTag representing the current value of this ValueDouble
     */
    @SuppressWarnings("removal")
    public Tag valueToNBT()
    {
        return new DoubleTag(this.value);
    }

    /**
     * Returns a new ValueDouble with the same id, default value, min and max as this instance.
     * The value of the new instance is set to the current value of this instance.
     * @return a new ValueDouble with the same id, default value, min and max as this instance
     */
    public ValueDouble copier()
    {
        ValueDouble clone = new ValueDouble(this.id, this.defaultValue, this.min, this.max);
        clone.value = this.value;

        return clone;
    }

    public Double interpolate(Interpolation interpolation, GenericBaseValue<?> to, float factor)
    {
        if (!(to.value instanceof Double))
        { 
            return this.value;
        }
        return interpolation.interpolateDouble(this.value, (Double) to.value, factor);
    }
}