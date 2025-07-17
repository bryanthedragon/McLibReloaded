package bryanthedragon.mclibreloaded.config.values;

import com.google.gson.JsonElement;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import bryanthedragon.mclibreloaded.forge.fml.common.network.ForgeByteBufUtils;
import bryanthedragon.mclibreloaded.utils.ByteBufUtils;
import bryanthedragon.mclibreloaded.utils.Color;
import bryanthedragon.mclibreloaded.utils.Interpolation;
import bryanthedragon.mclibreloaded.utils.JsonUtils;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;


/**
 * The Array of ItemStacks will not contain null values. Null values will be replaced with {@link ItemStack#EMPTY}
 */
public class ValueItemSlots extends GenericValue<ItemStack[]>
{
    private int size;
    private ForgeByteBufUtils fbb = new ForgeByteBufUtils();

    public ValueItemSlots(String id, int size)
    {
        super(id);
        this.size = size;
        this.defaultValue = this.getNullValue();
        this.reset();
    }

    /**
     * Sets the defaultValue to a copy of the provided defaultValue array.
     * @param id
     * @param defaultValue
     */
    public ValueItemSlots(String id, ItemStack[] defaultValue)
    {
        super(id);
        this.size = defaultValue.length;
        this.defaultValue = new ItemStack[defaultValue.length];
        for (int i = 0; i < this.defaultValue.length; i++)
        {
            this.defaultValue[i] = (defaultValue[i] == null) ? this.getNullElementValue() : defaultValue[i].copy();
        }
        this.reset();
    }

    /**
     * Set this value to a copy of the provided array
     * @param value
     */
    @SuppressWarnings({ "null", "unused" })
    public void setItemStackArray(@Nonnull ItemStack[] value)
    {
        if (value == null) return;
        for (int i = 0; i < value.length && i < this.value.length; i++)
        {
            this.value[i] = (value[i] == null) ? this.getNullElementValue() : value[i].copy();
        }
        this.saveLater();
    }

    /**
     * Set the provided index to a copy of the provided ItemStack.<br>
     * If the provided ItemStack is null, {@link ItemStack#EMPTY} will be set.<br>
     * Do nothing if the provided index is out of bounds.
     * @param itemStack
     * @param index
     */
    public void setItemStackIndex(ItemStack itemStack, int index)
    {
        if (index < this.value.length)
        {
            this.value[index] = (itemStack == null) ? this.getNullElementValue() : itemStack.copy();
            this.saveLater();
        }
    }

    public void reset()
    {
        this.value = new ItemStack[this.defaultValue.length];
        for (int i = 0; i < this.value.length; i++)
        {
            this.setItemStackIndex(this.defaultValue[i], i);
        }
    }

    /**
     * @return a deep copy of the array
     */
    public ItemStack[] getItemStack()
    {
        ItemStack[] copy = new ItemStack[this.value.length];
        for (int i = 0; i < this.value.length; i++)
        {
            copy[i] = this.value[i].copy();
        }
        return copy;
    }

    /**
     * @param index
     * @return a copy of the ItemStack at the provided index
     * @throws IndexOutOfBoundsException
     */
    public ItemStack getIndex(int index) throws IndexOutOfBoundsException
    {
        return this.value[index].copy();
    }

    public int getIndexSize()
    {
        return this.size;
    }

    /**
     * @return {@link ItemStack#EMPTY}, the default value that the type of the array should produce instead of null.
     */
    protected ItemStack getNullElementValue()
    {
        return ItemStack.EMPTY;
    }

    /**
     * @return an ItemStack array with the {@link #size} of this object filled with {@link ItemStack#EMPTY}
     */
    protected ItemStack[] getNullValue()
    {
        ItemStack[] nullValue = new ItemStack[this.size];
        Arrays.fill(nullValue, this.getNullElementValue());
        return nullValue;
    }

    /**
     *
     * @param obj
     * @return false if the provided object is not instance of ValueItemSlots or if the length of the arrays are different.
     *         Returns true if every ItemStack in the array returns true for {@link ItemStack#isItemEqual(ItemStack)} or {@link ItemStack#isEmpty()}.
     */
    public boolean equalsTo(Object obj)
    {
        if (!(obj instanceof ValueItemSlots))
        {
            return false;
        }
        ValueItemSlots valueObj = (ValueItemSlots) obj;
        if (this.value.length != valueObj.value.length)
        {
            return false;
        }
        for (int i = 0; i < this.value.length; i++)
        {
            if (!ItemStack.areItemStacksEqual(this.value[i], this.defaultValue[i]))
            {
                return false;
            }
        }
        return true;
    }

    /**
     * @return false if every ItemStack in {@link #value} and {@link #defaultValue} returns true for {@link ItemStack#isItemEqual(ItemStack)} or {@link ItemStack#isEmpty()}.
     */
    public boolean hasChanged()
    {
        for (int i = 0; i < this.value.length; i++)
        {
            if (!ItemStack.areItemStacksEqual(this.value[i], this.defaultValue[i]))
            {
                return true;
            }
        }
        return false;
    }

    public ValueItemSlots copier()
    {
        ValueItemSlots copy = new ValueItemSlots(this.id, this.defaultValue);
        copy.setItemStackArray(this.value);
        return copy;
    }

    public void copy(Value origin)
    {
        superCopy(origin);
        if (origin instanceof ValueItemSlots)
        {
            ValueItemSlots valueItemSlots = (ValueItemSlots) origin;
            for (int i = 0; i < valueItemSlots.value.length && i < this.value.length; i++)
            {
                this.value[i] = valueItemSlots.value[i].copy();
            }
        }
    }

    public void fromBytes(ByteBuf buffer)
    {
        this.size = buffer.readInt();
        this.value = new ItemStack[this.size];
        this.defaultValue = new ItemStack[this.size];
        for (int i = 0; i < this.value.length; i++)
        {
            this.value[i] = buffer.readBoolean() ? fbb.readItemStack(buffer) : this.getNullElementValue();
        }
        for (int i = 0; i < this.defaultValue.length; i++)
        {
            this.defaultValue[i] = buffer.readBoolean() ? fbb.readItemStack(buffer) : this.getNullElementValue();
        }
    }

    public void toBytes(ByteBuf buffer)
    {
        buffer.writeInt(this.size);
        for (int i = 0; i < this.value.length; i++)
        {
            buffer.writeBoolean(this.value[i] != null);
            if (this.value[i] != null)
            {
                fbb.writeItemStack(buffer, this.value[i]);
            }
        }
        for (int i = 0; i < this.defaultValue.length; i++)
        {
            buffer.writeBoolean(this.defaultValue[i] != null);
            if (this.defaultValue[i] != null)
            {
                fbb.writeItemStack(buffer, this.defaultValue[i]);
            }
        }
    }

    public void valueFromBytes(ByteBuf buffer)
    {
        this.size = buffer.readInt();
        this.value = new ItemStack[this.size];
        for (int i = 0; i < this.value.length; i++)
        {
            this.value[i] = buffer.readBoolean() ? fbb.readItemStack(buffer) : this.getNullElementValue();
        }
    }

    public void valueToBytes(ByteBuf buffer)
    {
        buffer.writeInt(this.size);
        for (int i = 0; i < this.value.length; i++)
        {
            buffer.writeBoolean(this.value[i] != null);
            if (this.value[i] != null)
            {
                fbb.writeItemStack(buffer, this.value[i]);
            }
        }
    }
    /*TODO*/
    public void valueFromJSON(JsonElement element)
    {

    }

    /*TODO*/
    @Nullable
    public JsonElement valueToJSON()
    {
        return null;
    }

    public void valueFromNBT(Tag tag)
    {
        if (tag instanceof ListTag)
        {
            ListTag items = (ListTag) tag;
            for (int i = 0; i < items.tagCount() && i < this.value.length; i++)
            {
                this.value[i] = new ItemStack(items.getCompoundTagAt(i));
            }
        }
    }

    public Tag valueToNBT()
    {
        ListTag list = new ListTag();
        for (int i = 0; i < this.value.length; i++)
        {
            CompoundTag tag = new CompoundTag();
            ItemStack stack = this.value[i];
            if (!stack.isEmpty())
            {
                stack.writeToNBT(tag);
            }
            list.appendTag(tag);
        }
        return list;
    }

    public String toString()
    {
        String str = "";
        for (int i = 0; i < this.value.length; i++)
        {
            str += this.value[i].toString() + ((i + 1 == this.value.length) ? "" : ", ");
        }
        return str;
    }

    public ItemStack[] interpolate(Interpolation interpolation, GenericBaseValue<?> to, float factor)
    {
        if (!(to.value instanceof ItemStack[])) 
        {
            return this.copier().value;
        }
        return factor == 1F ? (ItemStack[]) to.copier().getValue() : this.copier().value;
    }
}
