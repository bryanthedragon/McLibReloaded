package bryanthedragon.mclibreloaded.config.values;

import bryanthedragon.mclibreloaded.utils.MatrixUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.GuiElement;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.buttons.GuiToggleElement;
import bryanthedragon.mclibreloaded.config.gui.GuiConfigPanel;
import bryanthedragon.mclibreloaded.utils.Interpolation;
import net.minecraft.client.Minecraft;
import java.util.Arrays;
import java.util.List;

public class ValueBoolean extends GenericValue<Boolean> implements IServerValue, IConfigGuiProvider
{
    public ValueBoolean(String id)
    {
        super(id, false);
    }

    public ValueBoolean(String id, boolean defaultValue)
    {
        super(id, defaultValue);
    }

    @Override
    protected Boolean getNullValue()
    {
        return false;
    }

    @Override
    public void resetServer()
    {
        this.serverValue = null;
    }

    @Override
    @SideOnly(Dist.CLIENT)
    public List<GuiElement> getFields(Minecraft mc, GuiConfigPanel gui)
    {
        GuiToggleElement toggle = new GuiToggleElement(mc, this);

        toggle.flex().reset();

        return Arrays.asList(toggle);
    }

    @Override
    public void valueFromJSON(JsonElement element)
    {
        this.set(element.getAsBoolean());
    }

    @Override
    public JsonElement valueToJSON()
    {
        return new JsonPrimitive(this.value);
    }

    @Override
    public void valueFromNBT(NBTBase tag)
    {
        if (tag instanceof NBTPrimitive)
        {
            if (((NBTPrimitive) tag).getInt() == 1)
            {
                this.set(true);
            }
            else if (((NBTPrimitive) tag).getInt() == 0)
            {
                this.set(false);
            }
        }
    }

    @Override
    public NBTBase valueToNBT()
    {
        return new NBTTagInt(this.value ? 1 : 0);
    }

    @Override
    public boolean parseFromCommand(String value)
    {
        if (value.equals("1"))
        {
            this.set(true);
        }
        else if (value.equals("0"))
        {
            this.set(false);
        }
        else
        {
            this.set(Boolean.parseBoolean(value));
        }

        return true;
    }

    @Override
    public void copy(Value value)
    {
        superCopy(value);

        if (value instanceof ValueBoolean)
        {
            this.value = ((ValueBoolean) value).value;
        }
    }

    @Override
    public void copyServer(Value value)
    {
        super.copyServer(value);

        if (value instanceof ValueBoolean)
        {
            this.serverValue = ((ValueBoolean) value).value;
        }
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        superFromBytes(buffer);

        this.value = buffer.readBoolean();
        this.defaultValue = buffer.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        superToBytes(buffer);

        buffer.writeBoolean(this.value);
        buffer.writeBoolean(this.defaultValue);
    }

    @Override
    public void valueFromBytes(ByteBuf buffer)
    {
        this.value = buffer.readBoolean();
    }

    @Override
    public void valueToBytes(ByteBuf buffer)
    {
        buffer.writeBoolean(this.value);
    }

    @Override
    public String toString()
    {
        return Boolean.toString(this.value);
    }

    @Override
    public ValueBoolean copy()
    {
        ValueBoolean clone = new ValueBoolean(this.id);
        clone.defaultValue = this.defaultValue;
        clone.value = this.value;
        clone.serverValue = this.serverValue;

        return clone;
    }

    public MatrixUtils.RotationOrder interpolate(Interpolation interpolation, GenericBaseValue<?> to, float factor)
    {
        if (!(to.value instanceof Boolean)) return this.value;

        return factor == 1F ? (Boolean) to.value : this.value;
    }
}
