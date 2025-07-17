package bryanthedragon.mclibreloaded.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

import bryanthedragon.mclibreloaded.utils.MatrixUtils.RotationOrder;

import io.netty.buffer.ByteBuf;

public abstract class ValueGUI extends Value implements IConfigGuiProvider
{
    private ValueRotationOrder rotationOrder;
    protected RotationOrder defaultValue = RotationOrder.XYZ;

    public ValueGUI(String id)
    {
        super(id);
    }

    /**
     * Gets the value of this ValueGUI.
     * <br>
     * This default implementation always returns null.
     * <br>
     * Subclasses should override this with their own implementation.
     * @return the value of this ValueGUI
     */
    public Object getValue()
    {
        return null;
    }


    /**
     * Sets the value of the ValueGUI from the given object.
     * If the given object is a RotationOrder, it is set directly.
     * If the given object is a String, it is converted to a RotationOrder and set.
     * If the given object is neither a RotationOrder nor a String, it throws an IllegalArgumentException.
     * If the given object is null, it throws an IllegalArgumentException.
     * @param value the value to set
     * @throws IllegalArgumentException if the given object is not a RotationOrder or a String
     */
    public void setValue(Object value)
    {
        if (value instanceof RotationOrder)
        {
            this.rotationOrder.setValue((RotationOrder) value);
        }
        else if (value instanceof String)
        {
            try
            {
                this.rotationOrder.setValue(RotationOrder.valueOf((String) value));
            }
            catch (IllegalArgumentException e)
            {
                this.rotationOrder.setValue(rotationOrder.getNullValue());
            }
        }
        else
        {
            throw new IllegalArgumentException("Cannot set value of type " + value.getClass().getName() + " to ValueGUI " + this.getClass().getName());
        }
    }


    /**
     * Resets the value of this ValueGUI to its default value.
     * This calls {@link #setValue(Object)} with the default value.
     */
    public void reset()
    {
        this.rotationOrder.setValue(this.defaultValue);
    }


    /**
     * Sets the value of the ValueGUI from the given JsonElement.
     * If the given JsonElement is a JsonNull, it sets the value to null.
     * If the given JsonElement is a JsonPrimitive and a String, it converts the String to a RotationOrder and sets the value.
     * If the given JsonElement is neither a JsonNull nor a JsonPrimitive and a String, it throws an IllegalArgumentException.
     * @param element the JsonElement to parse
     * @throws IllegalArgumentException if the given JsonElement is not a JsonNull or a JsonPrimitive and a String
     */
    public void valueFromJSON(JsonElement element)
    {
        if (element.isJsonNull())
        {
            this.rotationOrder.setValue(rotationOrder.getNullValue());
        }
        else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString())
        {
            try
            {
                this.rotationOrder.setValue(RotationOrder.valueOf(element.getAsString()));
            }
            catch (IllegalArgumentException e)
            {
                this.rotationOrder.setValue(rotationOrder.getNullValue());
            }
        }
        else
        {
            throw new IllegalArgumentException("Cannot parse JSON element of type " + element.getClass().getName() + " to ValueGUI " + this.getClass().getName());
        }
    }


    /**
     * Copies the value from the specified ValueGUI instance to this ValueGUI instance.
     * If the specified value is not a ValueGUI, this method throws an IllegalArgumentException.
     * @param value the ValueGUI to copy from
     * @throws IllegalArgumentException if the specified value is not a ValueGUI
     */
    public void copy(Value value)
    {
        super.copy(value);
        if (value instanceof ValueGUI)
        {
            ValueGUI guiValue = (ValueGUI) value;
            this.rotationOrder.setValue(guiValue.rotationOrder.get());
            this.defaultValue = guiValue.defaultValue;
        }
        else
        {
            throw new IllegalArgumentException("Cannot copy value of type " + value.getClass().getName() + " to ValueGUI " + this.getClass().getName());
        }
    }


    /**
     * Converts the current value of this ValueGUI instance to a JSON element.
     * Since the ValueGUI does not have a specific JSON representation, it returns a JSON null instance.
     *
     * @return a JsonElement representing the current value, which is JsonNull.INSTANCE in this case
     */
    public JsonElement valueToJSON()
    {
        return JsonNull.INSTANCE;
    }


    /**
     * Reads the ValueGUI from the specified ByteBuffer, and calls {@link GenericBaseValue#superFromBytes(ByteBuf)}.
     * The default value is set to the null value of the rotationOrder.
     * The current value of the rotationOrder is read from the ByteBuffer.
     * @param buffer
     */
    public void fromBytes(ByteBuf buffer)
    {
        superFromBytes(buffer);
        this.defaultValue = rotationOrder.getNullValue();
        rotationOrder.valueFromBytes(buffer);
    }


    /**
     * Writes the default and current values of this ValueGUI to the specified ByteBuffer.
     * The default value is written as a float, and the current value is written by calling
     * {@link #valueToBytes(ByteBuf)}.
     * @param buffer the ByteBuffer to write to
     */
    public void toBytes(ByteBuf buffer)
    {
        superToBytes(buffer);
        // buffer.writeFloat(this.defaultValue);
        rotationOrder.valueToBytes(buffer);
    }
}