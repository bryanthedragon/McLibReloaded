package bryanthedragon.mclibreloaded.client.gui.framework.elements;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiContext;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiDraw;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.IViewportStack;
import bryanthedragon.mclibreloaded.client.gui.utils.ScrollArea;
import bryanthedragon.mclibreloaded.client.gui.utils.ScrollDirection;

import net.minecraft.client.Minecraft;


/**
 * Scroll area GUI class
 * 
 * This bad boy allows to scroll stuff
 */
public class GuiScrollElement extends GuiElement implements IViewport
{
    public ScrollArea scroll;

    public GuiScrollElement(Minecraft mc)
    {
        this(mc, ScrollDirection.VERTICAL);
    }

    public GuiScrollElement(Minecraft mc, ScrollDirection direction)
    {
        super(mc);

        this.area = this.scroll = new ScrollArea(0);
        this.scroll.direction = direction;
        this.scroll.scrollSpeed = 20;
    }

    public GuiScrollElement cancelScrollEdge()
    {
        this.scroll.cancelScrollEdge = true;

        return this;
    }

    @Override
    public void apply(IViewportStack stack)
    {
        stack.pushViewport(this.area);

        if (this.scroll.direction == ScrollDirection.VERTICAL)
        {
            stack.shiftY(this.scroll.scroll);
        }
        else
        {
            stack.shiftX(this.scroll.scroll);
        }
    }

    @Override
    public void unapply(IViewportStack stack)
    {
        if (this.scroll.direction == ScrollDirection.VERTICAL)
        {
            stack.shiftY(-this.scroll.scroll);
        }
        else
        {
            stack.shiftX(-this.scroll.scroll);
        }

        stack.popViewport();
    }

    @Override
    public void resize()
    {
        super.resize();

        this.scroll.clamp();
    }

    @Override
    public boolean mouseClicked(GuiContext context)
    {
        if (context.awaitsRightClick && context.mouseButton == 1)
        {
            return super.mouseClicked(context);
        }

        if (!this.area.isInside(context))
        {
            if (context.isFocused() && this.isDescendant((GuiElement) context.activeElement))
            {
                context.unfocus();
            }

            return false;
        }

        if (this.scroll.mouseClicked(context))
        {
            return true;
        }

        this.apply(context);
        boolean result = super.mouseClicked(context);
        this.unapply(context);

        return result;
    }

    @Override
    public boolean mouseScrolled(GuiContext context)
    {
        if (!this.area.isInside(context))
        {
            if (context.isFocused() && this.isDescendant((GuiElement) context.activeElement))
            {
                context.unfocus();
            }

            return false;
        }

        this.apply(context);
        boolean result = super.mouseScrolled(context);
        this.unapply(context);

        if (result)
        {
            return true;
        }

        return this.scroll.mouseScroll(context);
    }

    @Override
    public void mouseReleased(GuiContext context)
    {
        this.scroll.mouseReleased(context);

        this.apply(context);
        super.mouseReleased(context);
        this.unapply(context);
    }

    @Override
    public void draw(GuiContext context)
    {
        GuiElement lastTooltip = context.tooltip.element;

        this.scroll.drag(context.mouseX, context.mouseY);

        GuiDraw.scissor(this.scroll.x, this.scroll.y, this.scroll.w, this.scroll.h, context);

        RenderSystem.pushMatrix();

        /* Translate the contents using OpenGL (scroll) */
        if (this.scroll.direction == ScrollDirection.VERTICAL)
        {
            RenderSystem.translate(0, -this.scroll.scroll, 0);
        }
        else
        {
            RenderSystem.translate(-this.scroll.scroll, 0, 0);
        }

        this.apply(context);
        this.preDraw(context);

        super.draw(context);

        this.postDraw(context);
        this.unapply(context);

        RenderSystem.popMatrix();

        this.scroll.drawScrollbar();

        GuiDraw.unscissor(context);

        /* Clear tooltip in case if it was set outside of scroll area within the scroll */
        if (!this.area.isInside(context) && context.tooltip.element != lastTooltip)
        {
            context.tooltip.set(context, null);
        }
    }

    protected void preDraw(GuiContext context)
    {}

    protected void postDraw(GuiContext context)
    {}
}