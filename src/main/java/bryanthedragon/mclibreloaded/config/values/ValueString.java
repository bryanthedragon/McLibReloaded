package bryanthedragon.mclibreloaded.config.values;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiContext;
import bryanthedragon.mclibreloaded.client.gui.framework.tooltips.ITooltip;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.GuiElement;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.input.GuiTextElement;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiLabel;
import bryanthedragon.mclibreloaded.client.gui.utils.keys.IKey;
import bryanthedragon.mclibreloaded.config.gui.GuiConfigPanel;
import bryanthedragon.mclibreloaded.utils.Interpolation;
import bryanthedragon.mclibreloaded.utils.MatrixUtils;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.Tag;
import org.spongepowered.asm.mixin.MixinEnvironment;
import java.util.Arrays;
import java.util.List;

public abstract class ValueString extends GenericValue<String> implements IServerValue, IConfigGuiProvider
{
    public ValueString(String id)
    {
        super(id, "");
    }

    public ValueString(String id, String defaultValue)
    {
        super(id, defaultValue);
    }

    @Override
    public void resetServer()
    {
        this.serverValue = null;
    }

    @Override
    @SideOnly(MixinEnvironment.Side.CLIENT)
    public List<GuiElement> getFields(Minecraft mc, GuiConfigPanel gui)
    {
        GuiElement element = new GuiElement(mc) {
            @Override
            public void mouseReleased(GuiContext context) {

            }

            @Override
            public void OnmouseReleased(GuiContext context) {

            }
        };
        GuiLabel label = label(IKey.lang(this.getConfig().getValueLabelKey(this)), 0).anchor(0, 0.5F);
        GuiTextElement textbox = new GuiTextElement(mc, this);

        textbox.flex().w(90);

        element.flex().row(0).preferred(0).height(20);
        element.add(label, textbox.removeTooltip());

        return Arrays.asList(element.tooltip(IKey.lang(this.getConfig().getValueCommentKey(this))));
    }

    private GuiLabel label(ITooltip lang, int i) {
    }

    @Override
    public void valueFromJSON(JsonElement element)
    {
        this.set(element.getAsString());
    }

    @Override
    public JsonElement valueToJSON()
    {
        return new JsonPrimitive(this.value);
    }

    @Override
    public void valueFromNBT(Tag tag)
    {
        if (tag != null)
        {
            this.set(((NBTTagString) tag).getString());
        }
    }

    @Override
    public Tag valueToNBT()
    {
        return new NBTTagString(this.value == null ? "" : this.value);
    }

    @Override
    public boolean parseFromCommand(String value)
    {
        this.set(value);

        return true;
    }

    @Override
    public void copy(Value value)
    {
        if (value instanceof ValueString)
        {
            this.value = ((ValueString) value).value;
        }
    }

    @Override
    public void copyServer(Value value)
    {
        if (value instanceof ValueString)
        {
            this.serverValue = ((ValueString) value).value;
        }
    }

    @Override
    public void fromBytes(ByteBuf buffer)
    {
        superFromBytes(buffer);

        this.value = ByteBufUtils.readUTF8String(buffer);
        this.defaultValue = ByteBufUtils.readUTF8String(buffer);
    }

    @Override
    public void toBytes(ByteBuf buffer)
    {
        superToBytes(buffer);

        ByteBufUtils.writeUTF8String(buffer, this.value == null ? "" : this.value);
        ByteBufUtils.writeUTF8String(buffer, this.defaultValue == null ? "" : this.defaultValue);
    }

    @Override
    public void valueFromBytes(ByteBuf buffer)
    {
        this.value = ByteBufUtils.readUTF8String(buffer);
    }

    @Override
    public void valueToBytes(ByteBuf buffer)
    {
        ByteBufUtils.writeUTF8String(buffer, this.value == null ? "" : this.value);
    }

    @Override
    public String toString()
    {
        return this.value;
    }

    @Override
    public ValueString copy()
    {
        ValueString clone = new ValueString(this.id) {
            @Override
            public List<GuiElement> getFields(Minecraft mc, GuiConfigPanel gui) {
                return List.of();
            }

            @Override
            public MatrixUtils.RotationOrder interpolate(Interpolation interpolation, GenericBaseValue<?> to, float factor) {
                return "";
            }
        };
        clone.defaultValue = this.defaultValue;
        clone.value = this.value;
        clone.serverValue = this.serverValue;

        return clone;
    }

    @Override
    public MatrixUtils.RotationOrder interpolate(Interpolation interpolation, GenericBaseValue<?> to, float factor)
    {
        if (!(to.value instanceof String)) return this.value;

        return factor == 1F ? (String) to.value : this.value;
    }
}