package bryanthedragon.mclibreloaded.config.values;

import bryanthedragon.mclibreloaded.utils.MatrixUtils;
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

    @Override
    public void resetServer()
    {
        this.serverValue = null;
    }

    @Override
    protected Float getNullValue()
    {
        return 0F;
    }

    @Override
    protected Float numberToValue(Number number) {
        return number.floatValue();
    }

    public boolean isInteger()
    {
        return false;
    }

    @Override
    @SideOnly(Dist.CLIENT)
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

    @Override
    public void valueFromJSON(JsonElement element)
    {
        this.set(element.getAsFloat());
    }

    @Override
    public void valueFromNBT(NBTBase tag)
    {
        if (tag instanceof NBTPrimitive)
        {
            this.set(((NBTPrimitive) tag).getFloat());
        }
    }

    @Override
    public NBTBase valueToNBT()
    {
        return new NBTTagFloat(this.value);
    }

    @Override
    public boolean parseFromCommand(String value)
    {
        try
        {
            this.set(Float.parseFloat(value));
        }
        catch (Exception e)
        {}

        return false;
    }

    @Override
    public void copy(Value value)
    {
        if (value instanceof ValueFloat)
        {
            this.set(((ValueFloat) value).value);
        }
    }

    @Override
    public void copyServer(Value value)
    {
        if (value instanceof ValueFloat)
        {
            this.serverValue = ((ValueFloat) value).value;
        }
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        superFromBytes(buffer);

        this.defaultValue = buffer.readFloat();
        this.min = buffer.readFloat();
        this.max = buffer.readFloat();
        this.valueFromBytes(buffer);
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        superToBytes(buffer);

        buffer.writeFloat(this.defaultValue);
        buffer.writeFloat(this.min);
        buffer.writeFloat(this.max);
        this.valueToBytes(buffer);
    }

    @Override
    public void valueFromBytes(ByteBuf buffer)
    {
        this.set(buffer.readFloat());
    }

    @Override
    public void valueToBytes(ByteBuf buffer)
    {
        buffer.writeFloat(this.value);
    }

    @Override
    public String toString()
    {
        return Float.toString(this.value);
    }

    @Override
    public ValueFloat copy()
    {
        ValueFloat clone = new ValueFloat(this.id, this.defaultValue, this.min, this.max);
        clone.value = this.value;

        return clone;
    }

    public MatrixUtils.RotationOrder interpolate(Interpolation interpolation, GenericBaseValue<?> to, float factor)
    {
        if (!(to.value instanceof Float)) return this.value;

        return interpolation.interpolate(this.value, (Float) to.value, factor);
    }
}