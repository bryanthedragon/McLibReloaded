package bryanthedragon.mclibreloaded.client.gui.framework.elements.buttons;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiContext;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.GuiElement;
import bryanthedragon.mclibreloaded.client.gui.utils.GuiUtils;

import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public abstract class GuiClickElement<T> extends GuiElement
{
    public Consumer<T> callback;

    protected boolean hover;
    protected boolean pressed;

    public GuiClickElement(Minecraft mc, Consumer<T> callback)
    {
        super(mc);

        this.callback = callback;
    }

    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseGetsClicked(context))
        {
            return true;
        }

        if (context.mouseButton == 1 && context.awaitsRightClick)
        {
            return false;
        }

        if (this.isAllowed(context.mouseButton) && this.area.isInside(context))
        {
            this.pressed = true;
            GuiUtils.playClick();
            this.click(context.mouseButton);

            return true;
        }

        return false;
    }

    protected boolean isAllowed(int mouseButton)
    {
        return mouseButton == 0;
    }

    protected void click(int mouseButton)
    {
        if (this.callback != null)
        {
            this.callback.accept(this.get());
        }
    }

    protected abstract T get();

    public void mouseReleased(GuiContext context)
    {
        super.mouseGetsReleased(context);
        this.pressed = false;
    }

    public void draw(GuiContext context)
    {
        this.hover = this.area.isInside(context);

        this.drawSkin(context);
        super.draw(context);
    }

    protected abstract void drawSkin(GuiContext context);
}