package bryanthedragon.mclibreloaded.client.gui.framework.elements;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiContext;
import bryanthedragon.mclibreloaded.client.gui.utils.Area;

import java.util.ArrayList;
import java.util.List;

/**
 * GUI elements collection
 * 
 * This class is responsible for handling a collection of elements
 */
public class GuiElements<T extends IGuiElement> implements IGuiElement
{
    /**
     * List of elements 
     */
    public List<T> elements = new ArrayList<T>();

    /**
     * Whether this element is enabled (can handle any input) 
     */
    protected boolean enabled = true;

    /**
     * Whether this element is visible 
     */
    protected boolean visible = true;

    /**
     * Parent of this elements collection
     */
    @SuppressWarnings("unused")
    private GuiElement parent;

    public GuiElements(GuiElement parent)
    {
        this.parent = parent;
    }

    public void clear()
    {
        this.elements.clear();
    }

    public void prepend(T element)
    {
        if (element != null)
        {
            this.elements.add(0, element);
        }
    }

    public void add(T element)
    {
        if (element != null)
        {
            this.elements.add(element);
        }
    }

    public boolean addAfter(T target, T element)
    {
        int index = this.elements.indexOf(target);
        if (index != -1 && element != null)
        {
            if (index + 1 >= this.elements.size())
            {
                this.elements.add(element);
            }
            else
            {
                this.elements.add(index + 1, element);
            }
            return true;
        }
        return false;
    }

    public boolean addBefore(T target, T element)
    {
        int index = this.elements.indexOf(target);
        if (index != -1 && element != null)
        {
            this.elements.add(index, element);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    public void add(T... elements)
    {
        for (T element : elements)
        {
            if (element != null) this.elements.add(element);
        }
    }

    public void resize()
    {
        for (T element : this.elements)
        {
            element.resize();
        }
    }

    public boolean isEnabled()
    {
        return this.enabled && this.visible;
    }

    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Returns whether this element and all its children are visible.
     * 
     * @return true if this element and all its children are visible, false otherwise
     */
    public boolean isVisible()
    {
        return this.visible;
    }

    /**
     * Sets the visibility of this element and all its children.
     * 
     * @param visible whether this element and its children should be visible
     */
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    /**
     * Handles a mouse click event.
     * If the element has children, it passes the event to the children.
     * If the event is not a right-click and the mouse is inside the element and the mouse button is 1,
     * it checks if there is a context menu needed. If there is, it creates it and sets it in the context.
     *
     * @param context the context of the mouse event
     * @return true if the event was handled, false otherwise
     */
    public boolean mouseGetsClicked(GuiContext context)
    {
        for (int i = this.elements.size() - 1; i >= 0; i--)
        {
            T element = this.elements.get(i);
            if (element.isEnabled() && element.mouseGetsClicked(context))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Mouse wheel was scrolled.
     *
     * @param context the context of the mouse scroll
     * @return true if the mouse scroll was consumed by an enabled element, false otherwise
     */
    public boolean mouseScrolled(GuiContext context)
    {
        for (int i = this.elements.size() - 1; i >= 0; i--)
        {
            T element = this.elements.get(i);
            if (element.isEnabled() && element.mouseScrolled(context))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Mouse was released.
     *
     * Calls the mouseGetsReleased method of each child element, starting from the last child element and going backwards.
     * If an element is enabled, calls the mouseGetsReleased method of that element.
     *
     * @param context the context of the mouse released event
     */
    public void mouseGetsReleased(GuiContext context)
    {
        for (int i = this.elements.size() - 1; i >= 0; i--)
        {
            T element = this.elements.get(i);

            if (element.isEnabled())
            {
                element.mouseGetsReleased(context);
            }
        }
    }

    /**
     * Key was typed.
     *
     * If the children are not null and the children's keyTyped method returns true, returns true.
     * Otherwise, returns false.
     *
     * @param context the context of the key typed
     * @return true if the children's keyTyped method returns true, otherwise false
     */
    public boolean keyTyped(GuiContext context)
    {
        for (int i = this.elements.size() - 1; i >= 0; i--)
        {
            T element = this.elements.get(i);

            if (element.isEnabled() && element.keyTyped(context))
            {
                return true;
            }
        }

        return false;
    }

    /**
     * Determines whether this element can be drawn on the screen
     * @param viewport the viewport to check against
     * @return true if the element can be drawn, false otherwise
     */
    public boolean canBeDrawn(Area viewport)
    {
        return true;
    }

    public void draw(GuiContext context)
    {
        for (T element : this.elements)
        {
            if (element.isVisible() && element.canBeDrawn(context.getViewport()))
            {
                element.draw(context);
            }
        }
    }
}