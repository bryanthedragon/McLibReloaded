package bryanthedragon.mclibreloaded.config.values;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import io.netty.buffer.ByteBuf;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.GuiElement;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.buttons.GuiToggleElement;
import bryanthedragon.mclibreloaded.config.gui.GuiConfigPanel;
import bryanthedragon.mclibreloaded.utils.Interpolation;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

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

    protected Boolean getNullValue()
    {
        return false;
    }

    public void resetServer()
    {
        this.serverValue = null;
    }

    @OnlyIn(Dist.CLIENT)
    public List<GuiElement> getFields(Minecraft mc, GuiConfigPanel gui)
    {
        GuiToggleElement toggle = new GuiToggleElement(mc, this);

        toggle.flex().reset();

        return Arrays.asList(toggle);
    }

    public void valueFromJSON(JsonElement element)
    {
        this.set(element.getAsBoolean());
    }

    public JsonElement valueToJSON()
    {
        return new JsonPrimitive(this.value);
    }

    public void valueFromNBT(Tag tag)
    {
        if (tag instanceof IntTag)
        {
            if (((IntTag) tag).getId() == 1)
            {
                this.set(true);
            }
            else if (((IntTag) tag).getId() == 0)
            {
                this.set(false);
            }
        }
    }

    @SuppressWarnings("removal")
    public Tag valueToNBT()
    {
        return new IntTag(this.value ? 1 : 0);
    }

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

    public void copy(Value value)
    {
        superCopy(value);
        if (value instanceof ValueBoolean)
        {
            this.value = ((ValueBoolean) value).value;
        }
    }

    public void copyServer(Value value)
    {
        super.copyServer(value);
        if (value instanceof ValueBoolean)
        {
            this.serverValue = ((ValueBoolean) value).value;
        }
    }

    public void fromBytes(ByteBuf buffer)
    {
        superFromBytes(buffer);
        this.value = buffer.readBoolean();
        this.defaultValue = buffer.readBoolean();
    }

    public void toBytes(ByteBuf buffer)
    {
        superToBytes(buffer);
        buffer.writeBoolean(this.value);
        buffer.writeBoolean(this.defaultValue);
    }

    public void valueFromBytes(ByteBuf buffer)
    {
        this.value = buffer.readBoolean();
    }

    public void valueToBytes(ByteBuf buffer)
    {
        buffer.writeBoolean(this.value);
    }

    public String toString()
    {
        return Boolean.toString(this.value);
    }

    public ValueBoolean copy()
    {
        ValueBoolean clone = new ValueBoolean(this.id);
        clone.defaultValue = this.defaultValue;
        clone.value = this.value;
        clone.serverValue = this.serverValue;
        return clone;
    }

    public Boolean interpolate(Interpolation interpolation, GenericBaseValue<?> to, float factor)
    {
        if (!(to.value instanceof Boolean)) 
        {
            return this.value;
        }
        return factor == 1F ? (Boolean) to.value : this.value;
    }
}
