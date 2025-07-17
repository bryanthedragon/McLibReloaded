package bryanthedragon.mclibreloaded.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import io.netty.buffer.ByteBuf;

import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.Tag;

import bryanthedragon.mclibreloaded.utils.Interpolation;
import bryanthedragon.mclibreloaded.utils.MatrixUtils.RotationOrder;

import javax.annotation.Nonnull;

/**
 * The value of this container will never be null. Null values will be replaced with XYZ RotationOrder
 */
public class ValueRotationOrder extends GenericValue<RotationOrder>
{
    public ValueRotationOrder(String id, @Nonnull RotationOrder order)
    {
        super(id, order);
    }

    protected RotationOrder getNullValue()
    {
        return RotationOrder.XYZ;
    }

    public void fromBytes(ByteBuf buffer)
    {
        superFromBytes(buffer);
        this.value = RotationOrder.values()[buffer.readByte()];
        this.defaultValue = RotationOrder.values()[buffer.readByte()];
    }

    public void toBytes(ByteBuf buffer)
    {
        superToBytes(buffer);
        buffer.writeByte((byte) this.value.ordinal());
        buffer.writeByte((byte) this.defaultValue.ordinal());
    }

    public void valueFromBytes(ByteBuf buffer)
    {
        this.value = RotationOrder.values()[buffer.readByte()];
    }

    public void valueToBytes(ByteBuf buffer)
    {
        buffer.writeByte((byte) this.value.ordinal());
    }

    public void valueFromJSON(JsonElement element)
    {
        this.setBaseValue(RotationOrder.values()[element.getAsByte()]);
    }

    public JsonElement valueToJSON()
    {
        return new JsonPrimitive(this.value.ordinal());
    }

    public void valueFromNBT(Tag tag)
    {
        if (tag instanceof ByteTag)
        {
            this.setBaseValue(RotationOrder.values()[((ByteTag) tag).asByte()]);
        }
    }

    @SuppressWarnings("removal")
    public Tag valueToNBT()
    {
        return new ByteTag((byte) this.value.ordinal());
    }

    public ValueRotationOrder copier()
    {
        ValueRotationOrder clone = new ValueRotationOrder(this.id, this.defaultValue);
        clone.value = this.value;
        clone.serverValue = this.serverValue;
        return clone;
    }

    public void copy(Value origin)
    {
        superCopy(origin);
        if (origin instanceof ValueRotationOrder)
        {
            this.value = ((ValueRotationOrder) origin).value;
        }
    }

    public RotationOrder interpolate(Interpolation interpolation, GenericBaseValue<?> to, float factor)
    {
        if (!(to.value instanceof RotationOrder)) 
        {
            return this.value;
        }
        return factor == 1F ? (RotationOrder) to.value : this.value;
    }
}
