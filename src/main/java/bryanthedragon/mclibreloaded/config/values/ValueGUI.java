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

    public Object getValue()
    {
        return null;
    }


    public void setValue(Object value)
    {
        if (value instanceof RotationOrder)
        {
            this.rotationOrder.set((RotationOrder) value);
        }
        else if (value instanceof String)
        {
            try
            {
                this.rotationOrder.set(RotationOrder.valueOf((String) value));
            }
            catch (IllegalArgumentException e)
            {
                this.rotationOrder.set(rotationOrder.getNullValue());
            }
        }
        else
        {
            throw new IllegalArgumentException("Cannot set value of type " + value.getClass().getName() + " to ValueGUI " + this.getClass().getName());
        }
    }


    public void reset()
    {
        this.rotationOrder.set(this.defaultValue);
    }


    public void valueFromJSON(JsonElement element)
    {
        if (element.isJsonNull())
        {
            this.rotationOrder.set(rotationOrder.getNullValue());
        }
        else if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString())
        {
            try
            {
                this.rotationOrder.set(RotationOrder.valueOf(element.getAsString()));
            }
            catch (IllegalArgumentException e)
            {
                this.rotationOrder.set(rotationOrder.getNullValue());
            }
        }
        else
        {
            throw new IllegalArgumentException("Cannot parse JSON element of type " + element.getClass().getName() + " to ValueGUI " + this.getClass().getName());
        }
    }


    public void copy(Value value)
    {
        super.copy(value);
        if (value instanceof ValueGUI)
        {
            ValueGUI guiValue = (ValueGUI) value;
            this.rotationOrder.set(guiValue.rotationOrder.get());
            this.defaultValue = guiValue.defaultValue;
        }
        else
        {
            throw new IllegalArgumentException("Cannot copy value of type " + value.getClass().getName() + " to ValueGUI " + this.getClass().getName());
        }
    }


    public JsonElement valueToJSON()
    {
        return JsonNull.INSTANCE;
    }


    public void fromBytes(ByteBuf buffer)
    {
        superFromBytes(buffer);
        this.defaultValue = rotationOrder.getNullValue();
        rotationOrder.valueFromBytes(buffer);
    }


    public void toBytes(ByteBuf buffer)
    {
        superToBytes(buffer);
        // buffer.writeFloat(this.defaultValue);
        rotationOrder.valueToBytes(buffer);
    }
}