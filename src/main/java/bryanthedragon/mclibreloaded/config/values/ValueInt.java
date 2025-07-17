package bryanthedragon.mclibreloaded.config.values;

import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.GuiElement;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.buttons.GuiCirculateElement;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.input.GuiColorElement;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.input.GuiKeybindElement;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.input.GuiTrackpadElement;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiLabel;
import bryanthedragon.mclibreloaded.client.gui.utils.Elements;
import bryanthedragon.mclibreloaded.client.gui.utils.keys.IKey;
import bryanthedragon.mclibreloaded.client.gui.utils.keys.KeyParser;
import bryanthedragon.mclibreloaded.config.gui.GuiConfigPanel;
import bryanthedragon.mclibreloaded.utils.ColorUtils;
import bryanthedragon.mclibreloaded.utils.Interpolation;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ValueInt extends GenericNumberValue<Integer> implements IServerValue, IConfigGuiProvider
{
    private Subtype subtype = Subtype.INTEGER;
    private List<IKey> labels;

    public ValueInt(String id)
    {
        super(id, 0, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public ValueInt(String id, int defaultValue)
    {
        super(id, defaultValue, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public ValueInt(String id, int defaultValue, int min, int max)
    {
        super(id, defaultValue, min, max);
    }

    public void setColorValue(String value)
    {
        this.setBaseValue(ColorUtils.parseColor(value));
    }

    protected Integer numberToValue(Number number) 
    {
        return number.intValue();
    }

    public Subtype getSubtype()
    {
        return this.subtype;
    }

    public ValueInt subtype(Subtype subtype)
    {
        this.subtype = subtype;

        return this;
    }

    public ValueInt color()
    {
        return this.subtype(Subtype.COLOR);
    }

    public ValueInt colorAlpha()
    {
        return this.subtype(Subtype.COLOR_ALPHA);
    }

    public ValueInt keybind()
    {
        return this.subtype(Subtype.KEYBIND);
    }

    public ValueInt comboKey()
    {
        return this.subtype(Subtype.COMBOKEY);
    }

    public ValueInt modes(IKey... labels)
    {
        this.labels = new ArrayList<IKey>();
        Collections.addAll(this.labels, labels);

        return this.subtype(Subtype.MODES);
    }

    public boolean isInteger()
    {
        return true;
    }


    public void resetServer()
    {
        this.serverValue = null;
    }

    protected Integer getNullValue()
    {
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public List<GuiElement> getFields(Minecraft mc, GuiConfigPanel gui)
    {
        GuiElement element = new GuiElement(mc);
        GuiLabel label = Elements.label(IKey.lang(this.getLabelKey()), 0).anchor(0, 0.5F);
        element.flex().row(0).preferred(0).height(20);
        element.add(label);
        if (this.subtype == Subtype.COLOR || this.subtype == Subtype.COLOR_ALPHA)
        {
            GuiColorElement color = new GuiColorElement(mc, this);
            color.flex().w(90);
            element.add(color.removeTooltip());
        }
        else if (this.subtype == Subtype.KEYBIND || this.subtype == Subtype.COMBOKEY)
        {
            GuiKeybindElement keybind = new GuiKeybindElement(mc, this);
            keybind.flex().w(90);
            element.add(keybind.removeTooltip());
        }
        else if (this.subtype == Subtype.MODES)
        {
            GuiCirculateElement button = new GuiCirculateElement(mc, null);
            for (IKey key : this.labels)
            {
                button.addLabel(key);
            }
            button.callback = (b) -> this.setBaseValue(button.getValue());
            button.setValue(this.get());
            button.flex().w(90);
            element.add(button);
        }
        else
        {
            GuiTrackpadElement trackpad = new GuiTrackpadElement(mc, this);
            trackpad.flex().w(90);
            element.add(trackpad.removeTooltip());
        }
        return Arrays.asList(element.tooltip(IKey.lang(this.getCommentKey())));
    }

    public void valueFromJSON(JsonElement element)
    {
        this.setBaseValue(element.getAsInt());
    }

    public void valueFromNBT(Tag tag)
    {
        if (tag instanceof IntTag)
        {
            this.setValue(((IntTag) tag).getId());
        }
    }

    @SuppressWarnings("removal")
    public Tag valueToNBT()
    {
        return new IntTag(this.value);
    }

    public boolean parseFromCommand(String value)
    {
        try
        {
            if (this.subtype == Subtype.COLOR || this.subtype == Subtype.COLOR_ALPHA)
            {
                this.setBaseValue(ColorUtils.parseColorWithException(value));
            }
            else
            {
                this.setBaseValue(Integer.parseInt(value));
            }
            return true;
        }
        catch (Exception e)
        {

        }
        return false;
    }

    public void copy(Value value)
    {
        if (value instanceof ValueInt)
        {
            this.setBaseValue(((ValueInt) value).value);
        }
    }

    public void copyServer(Value value)
    {
        if (value instanceof ValueInt)
        {
            this.serverValue = ((ValueInt) value).value;
        }
    }

    public void fromBytes(ByteBuf buffer)
    {
        superFromBytes(buffer);
        this.defaultValue = buffer.readInt();
        this.min = buffer.readInt();
        this.max = buffer.readInt();
        this.valueFromBytes(buffer);
        this.subtype = Subtype.values()[buffer.readInt()];
        if (buffer.readBoolean())
        {
            this.labels = new ArrayList<IKey>();
            for (int i = 0, c = buffer.readInt(); i < c; i++)
            {
                IKey key = KeyParser.keyFromBytes(buffer);
                if (key != null)
                {
                    this.labels.add(key);
                }
            }
        }
    }

    public void toBytes(ByteBuf buffer)
    {
        superToBytes(buffer);
        buffer.writeInt(this.defaultValue);
        buffer.writeInt(this.min);
        buffer.writeInt(this.max);
        this.valueToBytes(buffer);
        buffer.writeInt(this.subtype.ordinal());
        buffer.writeBoolean(this.labels != null);
        if (this.labels != null)
        {
            buffer.writeInt(this.labels.size());
            for (IKey key : this.labels)
            {
                KeyParser.keyToBytes(buffer, key);
            }
        }
    }

    public void valueFromBytes(ByteBuf buffer)
    {
        this.setBaseValue(buffer.readInt());
    }

    public void valueToBytes(ByteBuf buffer)
    {
        buffer.writeInt(this.value);
    }

    public String toString()
    {
        if (this.subtype == Subtype.COLOR || this.subtype == Subtype.COLOR_ALPHA)
        {
            return "#" + Integer.toHexString(this.value);
        }
        return Integer.toString(this.value);
    }

    public ValueInt copy()
    {
        ValueInt clone = new ValueInt(this.id, this.defaultValue, this.min, this.max);
        clone.value = this.value;
        return clone;
    }

    public static enum Subtype
    {
        INTEGER,
        COLOR,
        COLOR_ALPHA,
        KEYBIND,
        COMBOKEY,
        MODES
    }

    public Integer interpolate(Interpolation interpolation, GenericBaseValue<?> to, float factor)
    {
        if (!(to.value instanceof Integer)) 
        {
            return this.value;
        }
        return (int) interpolation.interpolateFloat(this.value, (Integer) to.value, factor);
    }
}