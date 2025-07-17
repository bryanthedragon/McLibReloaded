package bryanthedragon.mclibreloaded.config.values;

import com.google.gson.JsonElement;

import io.netty.buffer.ByteBuf;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.GuiElement;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.buttons.GuiButtonElement;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.input.GuiTexturePicker;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiLabel;
import bryanthedragon.mclibreloaded.client.gui.utils.Elements;
import bryanthedragon.mclibreloaded.client.gui.utils.keys.IKey;
import bryanthedragon.mclibreloaded.config.gui.GuiConfigPanel;
import bryanthedragon.mclibreloaded.forge.fml.common.network.ForgeByteBufUtils;
import bryanthedragon.mclibreloaded.utils.ByteBufUtils;
import bryanthedragon.mclibreloaded.utils.Interpolation;
import bryanthedragon.mclibreloaded.utils.resources.RLUtils;
import bryanthedragon.mclibreloaded.utils.resources.location.ResourceLocations;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

import java.util.Arrays;
import java.util.List;

public class ValueRL extends GenericValue<ResourceLocation> implements IServerValue, IConfigGuiProvider
{
    @OnlyIn(Dist.CLIENT)
    public static GuiTexturePicker picker;

    private boolean useServer;

    public ValueRL(String id)
    {
        super(id);
    }

    public ValueRL(String id, ResourceLocation defaultValue)
    {
        super(id);
        this.defaultValue = defaultValue;
    }

    /**
     * @return the reference to {@link #value} or {@link #serverValue}.
     */
    public ResourceLocation get()
    {
        return !this.useServer ? this.value : this.serverValue;
    }

    /**
     * Set this {@link #value} to the reference of the provided value.
     * <br>Note: This is how it was implemented before Chryfi did rewrites
     * and it has been used throughout McLib etc., so to avoid any problems, the old implementation is kept
     * @param value
     */
    public void setResourceLocation(ResourceLocation value)
    {
        this.value = value;
        this.saveLater();
    }

    public void setString(String value)
    {
        this.setValue(RLUtils.createTextureTransformer(value));
    }

    public void resetServer()
    {
        this.useServer = false;
        this.serverValue = null;
    }

    public void reset()
    {
        this.setValue(RLUtils.clone(this.defaultValue));
    }

    @OnlyIn(Dist.CLIENT)
    public List<GuiElement> getFields(Minecraft mc, GuiConfigPanel gui)
    {
        GuiElement element = new GuiElement(mc);
        GuiLabel label = Elements.label(IKey.lang(this.getLabelKey()), 0).anchor(0, 0.5F);
        GuiButtonElement pick = new GuiButtonElement(mc, IKey.lang("mclib.gui.pick_texture"),  (button) ->
        {
            if (picker == null)
            {
                picker = new GuiTexturePicker(mc, null);
            }
            picker.callback = this::setValue;
            picker.fill(this.value);
            picker.flex().relative(gui).wh(1F, 1F);
            picker.resize();
            if (picker.hasParent())
            {
                picker.removeFromParent();
            }
            gui.add(picker);
        });
        pick.flex().w(90);
        element.flex().row(0).preferred(0).height(20);
        element.add(label, pick);
        return Arrays.asList(element.tooltip(IKey.lang(this.getCommentKey())));
    }

    public void valueFromJSON(JsonElement element)
    {
        this.value = (ResourceLocation) ResourceLocations.fromJson(element);
    }

    public JsonElement valueToJSON()
    {
        return ResourceLocations.toJson(this.value);
    }

    public void valueFromNBT(Tag tag)
    {
        this.setValue(RLUtils.createNBTTag(tag));
    }

    @Nullable
    public Tag valueToNBT()
    {
        return ResourceLocations.toNBT(this.value);
    }

    public boolean parseFromCommand(String value)
    {
        this.setValue(RLUtils.createTextureTransformer(value));
        return true;
    }

    public void copy(Value value)
    {
        if (value instanceof ValueRL)
        {
            this.value = (ResourceLocation) RLUtils.clone(((ValueRL) value).value);
        }
    }

    public void copyServer(Value value)
    {
        if (value instanceof ValueRL)
        {
            this.useServer = true;
            this.serverValue = (ResourceLocation) RLUtils.clone(((ValueRL) value).value);
        }
    }

    public void fromBytes(ByteBuf buffer)
    {
        superFromBytes(buffer);
        this.value = this.readRL(buffer);
        this.defaultValue = this.readRL(buffer);
    }

    @SuppressWarnings("null")
    private ResourceLocation readRL(ByteBuf buffer)
    {
        if (buffer.readBoolean())
        {
            CompoundTag tag = ForgeByteBufUtils.readTag(buffer);
            return (ResourceLocation) RLUtils.createNBTTag(tag.get("RL"));
        }
        return null;
    }

    public void toBytes(ByteBuf buffer)
    {
        superToBytes(buffer);
        this.writeRL(buffer, this.value);
        this.writeRL(buffer, this.defaultValue);
    }

    public void valueFromBytes(ByteBuf buffer)
    {
        this.value = this.readRL(buffer);
    }

    public void valueToBytes(ByteBuf buffer)
    {
        this.writeRL(buffer, this.value);
    }

    private void writeRL(ByteBuf buffer, ResourceLocation rl)
    {
        buffer.writeBoolean(rl != null);
        if (rl != null)
        {
            CompoundTag tag = new CompoundTag();
            tag.setTag("RL", ResourceLocations.toNBT(rl));
            ForgeByteBufUtils.writeTag(buffer, tag);
        }
    }

    public String toString()
    {
        return this.value == null ? "" : this.value.toString();
    }

    public ValueRL copy()
    {
        ValueRL clone = new ValueRL(this.id);
        clone.value = (ResourceLocation) RLUtils.clone(this.value);
        clone.defaultValue = (ResourceLocation) RLUtils.clone(this.defaultValue);
        clone.serverValue = (ResourceLocation) RLUtils.clone(this.serverValue);
        clone.useServer = this.useServer;
        return clone;
    }

    public ResourceLocation interpolate(Interpolation interpolation, GenericBaseValue<?> to, float factor)
    {
        if (!(to.value instanceof ResourceLocation)) 
        {
            return (ResourceLocation) RLUtils.clone(this.value);
        }
        return factor == 1F ? (ResourceLocation) RLUtils.clone((ResourceLocation) to.value) : (ResourceLocation) RLUtils.clone(this.value);
    }
}