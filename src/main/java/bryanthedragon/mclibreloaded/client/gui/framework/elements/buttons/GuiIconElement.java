package bryanthedragon.mclibreloaded.client.gui.framework.elements.buttons;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiContext;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiDraw;
import bryanthedragon.mclibreloaded.client.gui.utils.Icon;
import bryanthedragon.mclibreloaded.utils.ColorUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import java.util.function.Consumer;

public class GuiIconElement extends GuiClickElement<GuiIconElement>
{
    public Icon icon;
    public int iconColor = 0xffffffff;
    public Icon hoverIcon;
    public int hoverColor = 0xffaaaaaa;

    public int disabledColor = 0x80404040;

    public GuiIconElement(Minecraft mc, Icon icon, Consumer<GuiIconElement> callback)
    {
        super(mc, callback);

        this.icon = icon;
        this.hoverIcon = icon;
        this.flex().wh(20, 20);
    }

    public GuiIconElement both(Icon icon)
    {
        this.icon = this.hoverIcon = icon;

        return this;
    }

    public GuiIconElement icon(Icon icon)
    {
        this.icon = icon;

        return this;
    }

    public GuiIconElement hovered(Icon icon)
    {
        this.hoverIcon = icon;

        return this;
    }

    public GuiIconElement iconColor(int color)
    {
        this.iconColor = color;

        return this;
    }

    public GuiIconElement hoverColor(int color)
    {
        this.hoverColor = color;

        return this;
    }

    public GuiIconElement disabledColor(int color)
    {
        this.disabledColor = color;

        return this;
    }

    @Override
    protected GuiIconElement get()
    {
        return this;
    }

    @Override
    protected void drawSkin(GuiContext context)
    {
        Icon icon = this.hover ? this.hoverIcon : this.icon;
        int color = this.hover ? this.hoverColor : this.iconColor;

        if (this.isEnabled())
        {
            ColorUtils.bindColor(color);
            icon.render(this.area.mx(), this.area.my(), 0.5F, 0.5F);
        }
        else
        {
            ColorUtils.bindColor(this.disabledColor);
            icon.render(this.area.mx(), this.area.my(), 0.5F, 0.5F);
        }
    }
}