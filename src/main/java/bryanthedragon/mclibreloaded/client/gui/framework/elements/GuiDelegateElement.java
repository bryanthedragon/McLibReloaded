package bryanthedragon.mclibreloaded.client.gui.framework.elements;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.context.GuiContextMenu;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiContext;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Delegated {@link IGuiElement}
 */
@OnlyIn(Dist.CLIENT)
public class GuiDelegateElement<T extends GuiElement> extends GuiElement
{
    public T delegate;

    public GuiDelegateElement(Minecraft mc, T element)
    {
        super(mc);
        this.delegate = element;
        if (this.delegate != null)
        {
            this.delegate.parent = this;
        }
    }

    /**
     * Sets the delegate element for this delegate element.
     * The delegate element will be automatically added as a child of this element.
     *
     * @param element the delegate element to set
     */
    public void setDelegate(T element)
    {
        this.delegate = element;
        if (this.delegate != null)
        {
            this.delegate.parent = this;
        }
        this.resize();
    }

    /**
     * Throws an {@link IllegalStateException} indicating that the following method is unsupported by the delegate element.
     *
     * @throws IllegalStateException always
     */
    private void unsupported()
    {
        throw new IllegalStateException("Following method is unsupported by delegate element!");
    }

    /**
     * Returns a list of children of this delegate element.
     * 
     * @return a list containing the delegate element, or an empty list if there is no delegate element
     */
    public List<IGuiElement> getChildren()
    {
        return this.delegate == null ? Collections.emptyList() : Arrays.asList(this.delegate);
    }

    /**
     * Throws an {@link IllegalStateException} indicating that the {@link #removeAll()} method is unsupported by the delegate element.
     *
     * @throws IllegalStateException always
     */
    public void removeAll()
    {
        this.unsupported();
    }

    /**
     * Throws an {@link IllegalStateException} indicating that the {@link #add(IGuiElement)} method is unsupported by the delegate element.
     *
     * @param element the element to add
     * @throws IllegalStateException always
     */
    public void add(IGuiElement element)
    {
        this.unsupported();
    }

    /**
     * Throws an {@link IllegalStateException} indicating that the {@link #add(IGuiElement...)} method is unsupported by the delegate element.
     *
     * @param elements the elements to add
     * @throws IllegalStateException always
     */
    public void add(IGuiElement... elements)
    {
        this.unsupported();
    }

    /**
     * Removes the given delegate element from this delegate element.
     *
     * @param element the delegate element to remove
     * @throws NullPointerException if element is null
     */
    public void remove(GuiElement element)
    {
        if (this.delegate != null && this.delegate == element)
        {
            this.delegate.parent = null;
            this.delegate = null;
        }
    }

    /**
     * Whether this delegate element is enabled (and can accept any input).
     * If the delegate element is null, this returns false. Otherwise, it returns the result of calling {@link IGuiElement#isEnabled()} on the delegate element.
     *
     * @return true if enabled, false otherwise
     */
    public boolean isEnabled()
    {
        return this.delegate != null && this.delegate.isEnabled();
    }

    /**
     * Whether this delegate element is visible on the screen.
     * If the delegate element is null, this returns false. Otherwise, it returns the result of calling {@link IGuiElement#isVisible()} on the delegate element.
     *
     * @return true if visible, false otherwise
     */
    public boolean isVisible()
    {
        return this.delegate == null || this.delegate.isVisible();
    }

    /**
     * Resizes the delegate element, if it exists.
     * <ul>
     * <li>If the delegate element is not null, it is removed from its current parent and re-added to this delegate element.</li>
     * <li>The delegate element's resizer is applied to the area of this delegate element.</li>
     * <li>The delegate element's resizer is set to this delegate element's resizer.</li>
     * <li>The delegate element's flex is linked to this delegate element's flex.</li>
     * <li>The delegate element's resize method is called.</li>
     * <li>The delegate element's resizer's postApply method is called on the area of this delegate element.</li>
     * </ul>
     */
    public void resize()
    {
        /* In case in another GUI */
        if (this.delegate != null && this.delegate.parent != this)
        {
            this.delegate.removeFromParent();
            this.delegate.parent = this;
        }
        if (this.resizer != null)
        {
            this.resizer.apply(this.area);
        }
        if (this.delegate != null)
        {
            this.delegate.resizer = this.resizer;
            this.delegate.flex().link(this.flex());
            this.delegate.resize();
        }
        if (this.resizer != null)
        {
            this.resizer.postApply(this.area);
        }
    }

    /**
     * Calls the delegate element's mouseClicked method if it exists, returning the result. Otherwise, returns false.
     *
     * @param context the context of the mouse click
     * @return the result of calling the delegate element's mouseClicked method, or false if the delegate element is null
     */
    public boolean mouseClicked(GuiContext context)
    {
        return this.delegate != null && this.delegate.mouseGetsClicked(context);
    }

    /**
     * Create a context menu instance. If the delegate element is null, call the superclass's method. Otherwise, call the
     * delegate element's createContextMenu method.
     *
     * @param context the context of the mouse click
     * @return the created context menu
     */
    public GuiContextMenu createContextMenu(GuiContext context)
    {
        return this.delegate == null ? super.createContextMenu(context) : this.delegate.createContextMenu(context);
    }

    /**
     * Calls the delegate element's mouseScrolled method if it exists, returning the result. Otherwise, returns false.
     *
     * @param context the context of the mouse scroll
     * @return the result of calling the delegate element's mouseScrolled method, or false if the delegate element is null
     */
    public boolean mouseScrolled(GuiContext context)
    {
        return this.delegate != null && this.delegate.mouseScrolled(context);
    }

    /**
     * Calls the delegate element's mouseReleased method if it exists.
     *
     * @param context the context of the mouse release
     */
    public void mouseReleased(GuiContext context)
    {
        if (this.delegate != null)
        {
            this.delegate.mouseGetsReleased(context);
        }
    }

    /**
     * Calls the delegate element's keyTyped method if it exists, returning the result. Otherwise, returns false.
     *
     * @param context the context of the key typed
     * @return the result of calling the delegate element's keyTyped method, or false if the delegate element is null
     */
    public boolean keyTyped(GuiContext context)
    {
        return this.delegate != null && this.delegate.keyTyped(context);
    }

    /**
     * Draws the delegate element, if it exists.
     *
     * @param context the context of the draw
     */
    public void draw(GuiContext context)
    {
        if (this.delegate != null)
        {
            this.delegate.draw(context);
        }
    }
}