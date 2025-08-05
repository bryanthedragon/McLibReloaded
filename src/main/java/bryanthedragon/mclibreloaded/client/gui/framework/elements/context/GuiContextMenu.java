package bryanthedragon.mclibreloaded.client.gui.framework.elements.context;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiContext;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.GuiElement;
import net.minecraft.client.Minecraft;

public abstract class GuiContextMenu extends GuiElement
{
    public GuiContextMenu(Minecraft mc)
    {
        super(mc);

        this.hideTooltip();
    }

    /**
     * Set mouse coordinate
     *
     * In this method for subclasses, you should setup the resizer
     */
    public abstract void setMouse(GuiContext context);

    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseGetsClicked(context))
        {
            return true;
        }

        if (!this.area.isInside(context))
        {
            this.removeFromParent();
        }

        return false;
    }

    public void draw(GuiContext context)
    {
        this.area.draw(0xff000000);

        super.draw(context);
    }
}