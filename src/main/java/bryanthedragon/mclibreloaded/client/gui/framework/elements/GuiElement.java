package bryanthedragon.mclibreloaded.client.gui.framework.elements;

import bryanthedragon.mclibreloaded.client.gui.framework.GuiBase;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.context.GuiContextMenu;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiContext;
import bryanthedragon.mclibreloaded.client.gui.framework.tooltips.ITooltip;
import bryanthedragon.mclibreloaded.client.gui.framework.tooltips.LabelTooltip;
import bryanthedragon.mclibreloaded.client.gui.utils.KeybindManager;
import bryanthedragon.mclibreloaded.client.gui.utils.Area;
import bryanthedragon.mclibreloaded.client.gui.utils.keys.IKey;
import bryanthedragon.mclibreloaded.client.gui.utils.resizers.IResizer;
import bryanthedragon.mclibreloaded.client.gui.utils.resizers.Flex;
import bryanthedragon.mclibreloaded.client.gui.utils.resizers.Margin;
import bryanthedragon.mclibreloaded.utils.Direction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class GuiElement extends Gui implements IGuiElement
{
    /**
     * Area of this element (i.e. position and size) 
     */
    public Area area = new Area();

    /**
     * Element's margin (it's used only by layout resizers)
     */
    public final Margin margin = new Margin();

    /**
     * Resizer of this class
     */
    protected IResizer resizer;

    /**
     * Flex resizer of this class
     */
    protected Flex flex;

    /**
     * Tooltip instance
     */
    public ITooltip tooltip;

    /**
     * Keybind manager
     */
    public KeybindManager keybinds;

    /**
     * Context menu supplier
     */
    public Supplier<GuiContextMenu> contextMenu;

    /**
     * Hide tooltip
     */
    public boolean hideTooltip;

    /**
     * Whether this element should be ignored by post resizers
     */
    public boolean ignored;

    /**
     * Whether this element can be culled if it's out of viewport
     */
    public boolean culled = true;

    /**
     * Whether this element is a container
     */
    protected boolean container;

    /**
     * Parent GUI element
     */
    protected GuiElement parent;

    /**
     * Children elements
     */
    private GuiElements<IGuiElement> children;

    /**
     * Whether this element is enabled (can handle any input) 
     */
    protected boolean enabled = true;

    /**
     * Whether this element is visible 
     */
    protected boolean visible = true;

    /* Useful references */
    protected Minecraft mc;
    protected FontRenderer font;

    /**
     * Initiate GUI element with Minecraft's instance 
     */
    public GuiElement(Minecraft mc)
    {
        super(mc);
        this.mc = mc;
        this.font = mc.fontRenderer;
    }

    /* Hierarchy management */

    /**
     * Get the root {@link GuiBase.GuiRootElement} from this element.
     * 
     * @return the root element, or null if not found
     */
    public GuiBase.GuiRootElement getRoot()
    {
        GuiElement element = this;
        while (element.getParent() != null)
        {
            element = element.getParent();
        }
        return element instanceof GuiBase.GuiRootElement ? (GuiBase.GuiRootElement) element : null;
    }

    /**
     * Get the parent element of this element.
     *
     * @return the parent element, or null if this element is a root element
     */
    public GuiElement getParent()
    {
        return this.parent;
    }

    /**
     * Whether this element has a parent.
     *
     * @return whether this element has a parent
     */
    public boolean hasParent()
    {
        return this.parent != null;
    }

    /**
     * Whether this element is a descendant of the given element.
     *
     * @param element the element to check
     * @return whether this element is a descendant of the given element
     */
    public boolean isDescendant(GuiElement element)
    {
        if (this == element)
        {
            return false;
        }
        while (element != null)
        {
            if (element.parent == this)
            {
                return true;
            }
            element = element.parent;
        }
        return false;
    }

    /**
     * Returns a list of children of this element.
     *
     * @return an immutable list of children, or an empty list if there are no children
     */
    public List<IGuiElement> getChildren()
    {
        if (this.children == null)
        {
            return Collections.emptyList();
        }
        return this.children.elements;
    }

    /**
     * Returns a list of children of this element, that are instances of the given class.
     *
     * @param clazz the class to search for in the children of this element
     * @param <T> the type of the children to search for
     * @return an immutable list of children, or an empty list if there are no children matching the given class
     */
    public <T> List<T> getChildren(Class<T> clazz)
    {
        return getChildrenList(clazz, new ArrayList<T>());
    }

    /**
     * Returns a list of children of this element, that are instances of the given class.
     *
     * @param clazz the class to search for in the children of this element
     * @param list the list to which the children will be added; if null, a new list will be created
     * @param <T> the type parameter of the class
     * @return the list of children of this element that are instances of the given class
     */
    public <T> List<T> getChildrenList(Class<T> clazz, List<T> list)
    {
        return getAllChildren(clazz, list, false);
    }

    /**
     * Returns a list of children of this element, and optionally this element itself,
     * that are instances of the given class.
     *
     * @param clazz the class to search for in the children of this element
     * @param list the list to which the children will be added; if null, a new list will be created
     * @param includeItself whether or not to include this element itself in the list
     * @param <T> the type parameter of the class
     * @return the list of children of this element that are instances of the given class
     */
    public <T> List<T> getAllChildren(Class<T> clazz, List<T> list, boolean includeItself)
    {
        if (includeItself && clazz.isAssignableFrom(this.getClass()))
        {
            list.add(clazz.cast(this));
        }
        for (IGuiElement element : this.getChildren())
        {
            if (clazz.isAssignableFrom(element.getClass()))
            {
                list.add(clazz.cast(element));
            }
            if (element instanceof GuiElement)
            {
                ((GuiElement) element).getAllChildren(clazz, list, includeItself);
            }
        }
        return list;
    }

    /**
     * Adds the given GuiElement to the beginning of this element's list of children.
     *
     * @param element the GuiElement to add
     * @throws NullPointerException if element is null
     */
    public void prepend(IGuiElement element)
    {
        if (this.children == null)
        {
            this.children = new GuiElements<IGuiElement>(this);
        }
        this.markChild(element);
        this.children.prepend(element);
    }

    /**
     * Adds the given GuiElement to the end of this element's list of children.
     *
     * @param element the GuiElement to add
     * @throws NullPointerException if element is null
     */
    public void add(IGuiElement element)
    {
        if (this.children == null)
        {
            this.children = new GuiElements<IGuiElement>(this);
        }
        this.markChild(element);
        this.children.add(element);
    }

    /**
     * Adds multiple GuiElements to the end of this element's list of children.
     *
     * @param elements the elements to add
     * @throws NullPointerException if any element in the array is null
     */
    public void addArray(IGuiElement... elements)
    {
        if (this.children == null)
        {
            this.children = new GuiElements<IGuiElement>(this);
        }
        for (IGuiElement element : elements)
        {
            this.markChild(element);
            this.children.add(element);
        }
    }

    /**
     * Adds a GuiElement after the specified target element.
     *
     * @param target the target element
     * @param element the element to add after the target
     * @throws NullPointerException if the target or element is null
     */
    public void addAfter(IGuiElement target, IGuiElement element)
    {
        if (this.children == null)
        {
            return;
        }
        if (this.children.addAfter(target, element))
        {
            this.markChild(element);
        }
    }

    /**
     * Adds a GuiElement before the specified target element.
     *
     * @param target the target element
     * @param element the GuiElement to be added before the target
     */
    public void addBefore(IGuiElement target, IGuiElement element)
    {
        if (this.children == null)
        {
            return;
        }
        if (this.children.addBefore(target, element))
        {
            this.markChild(element);
        }
    }

    /**
     * Marks the given element as a child of this GuiElement. This method sets the
     * parent of the element to this GuiElement and adds the element to the resizer
     * if one exists.
     *
     * @param element The element to be marked as a child.
     */
    private void markChild(IGuiElement element)
    {
        if (element instanceof GuiElement)
        {
            GuiElement child = (GuiElement) element;
            child.parent = this;
            if (this.resizer != null)
            {
                this.resizer.add(this, child);
            }
        }
    }

    /**
     * Removes all child elements from this GuiElement. This method calls the
     * {@link IResizer#remove(GuiElement, GuiElement)} method on the resizer
     * associated with this GuiElement for each child element, sets the parent of
     * the child element to null, and finally clears the list of child elements.
     * <p>
     * This method does nothing if the list of child elements is null.
     */
    public void removeAll()
    {
        if (this.children == null)
        {
            return;
        }
        for (IGuiElement element : this.children.elements)
        {
            if (element instanceof GuiElement)
            {
                if (this.resizer != null)
                {
                    this.resizer.remove(this, (GuiElement) element);
                }
                ((GuiElement) element).parent = null;
            }
        }
        this.children.clear();
    }

    /**
     * Removes this element from its parent. If the element has a parent, the parent's {@code remove} method is called
     * with this element as an argument.
     */
    public void removeFromParent()
    {
        if (this.hasParent())
        {
            this.parent.remove(this);
        }
    }

    /**
     * Removes a child element from this element. If the child is a {@link GuiElement}, it is also removed from its
     * resizer.
     *
     * @param element the child element to remove
     */
    public void remove(GuiElement element)
    {
        if (this.children.elements.remove(element))
        {
            if (this.resizer != null)
            {
                this.resizer.remove(this, element);
            }

            element.parent = null;
        }
    }

    /* Setters */

    /**
     * Removes the tooltip from this element.
     *
     * @return the element itself for chaining.
     */
    public GuiElement removeTooltip()
    {
        this.tooltip = null;
        return this;
    }

    /**
     * Sets the tooltip for this element.
     *
     * @param tooltip the tooltip to set.
     * @return this GuiElement.
     */
    public GuiElement tooltipGetter(ITooltip tooltip)
    {
        this.tooltip = tooltip;

        return this;
    }

    /**
     * Sets the tooltip for this element to a {@link LabelTooltip} with the specified label and a default direction
     * of {@link Direction#BOTTOM}.
     *
     * @param label the label for the tooltip
     * @return this element
     */
    public GuiElement tooltipLabel(IKey label)
    {
        this.tooltip = new LabelTooltip(label, Direction.BOTTOM);
        return this;
    }

    /**
     * Sets the tooltip for this element to a {@link LabelTooltip} with the specified label and direction.
     *
     * @param label the label for the tooltip
     * @param direction the direction the tooltip should be displayed in
     * @return this element
     */
    public GuiElement tooltipDirection(IKey label, Direction direction)
    {
        this.tooltip = new LabelTooltip(label, direction);
        return this;
    }

    /**
     * Sets the tooltip for this element to a {@link LabelTooltip} with the specified label, width, and direction.
     *
     * @param label the label for the tooltip
     * @param width the width of the tooltip (in pixels)
     * @param direction the direction of the tooltip from the element
     * @return this element for chaining
     */
    public GuiElement tooltip(IKey label, int width, Direction direction)
    {
        this.tooltip = new LabelTooltip(label, width, direction);
        return this;
    }

    /**
     * Hides the tooltip for this element.
     *
     * @return this element for chaining
     */
    public GuiElement hideTooltip()
    {
        this.hideTooltip = true;
        return this;
    }

    /**
     * Disables culling for this element, making it always rendered.
     *
     * @return this element for chaining
     */
    public GuiElement noCulling()
    {
        this.culled = false;
        return this;
    }

    /* Keybind manager */

    /**
     * Returns the keybind manager for this element.
     *
     * @return the keybind manager for this element
     */
    public KeybindManager keys()
    {
        if (this.keybinds == null)
        {
            this.keybinds = new KeybindManager();
        }
        return this.keybinds;
    }

    /* Container stuff */

    /**
     * Marks this element as a container.
     *
     * When this element is marked as a container, it will be treated as a container
     * by the framework. This means that the element will be able to contain other
     * elements inside of it.
     *
     * @return this gui element
     */
    public GuiElement markContainer()
    {
        this.container = true;
        return this;
    }

    /**
     * Marks this element as ignored.
     *
     * @return this gui element
     */
    public GuiElement markIgnored()
    {
        this.ignored = true;
        return this;
    }

    /**
     * Returns whether this element is a container.
     *
     * @return true if this element is a container, false otherwise
     */
    public boolean isContainer()
    {
        return this.container;
    }

    /**
     * Returns the parent element that is a container. If none is found, null is returned.
     *
     * @return the parent element that is a container, or null if none is found
     */
    public GuiElement getParentContainer()
    {
        GuiElement element = this.getParent();
        while (element != null && !element.isContainer())
        {
            element = element.getParent();
        }
        return element;
    }


    public GuiElement context(Supplier<GuiContextMenu> supplier)
    {
        this.contextMenu = supplier;
        return this;
    }

    /* Resizer methods */

    /**
     * This method assigns a {@link Flex} to this element and sets it as the main resizer.
     *
     * Flex isn't necessary when you place this element into an element with column, row or
     * grid layouts. Use this to avoid manually calculating the position and scale.
     *
     * @return the {@link Flex} object that is assigned to the main resizer.
     */
    public Flex flex()
    {
        if (this.flex == null)
        {
            this.flex = new Flex(this);
            if (this.resizer == null)
            {
                this.resizer = this.flex;
            }
        }
        return this.flex;
    }

    /**
     * Sets the main resizer for this {@link GuiElement}. The main resizer is responsible for
     * calculating the position and scale of this element based on the constraints set in it.
     *
     * @param flex the {@link Flex} object that will be assigned to the main resizer, or null if
     *             it should be removed
     */
    public void flexGetter(Flex flex)
    {
        if (flex != null)
        {
            this.flex = flex;
        }
    }

    /**
     * Returns the main resizer for this {@link GuiElement}. The main resizer is responsible for
     * calculating the position and scale of this element based on the constraints set in it.
     *
     * @return the main {@link IResizer} for this element, or null if none is set
     */
    public IResizer resizerGetter()
    {
        return this.resizer;
    }

    /**
     * Sets the main resizer for this {@link GuiElement}. If the given resizer is null, the main
     * resizer is cleared.
     *
     * @param resizer the new resizer for this element, or null to clear the resizer
     * @return this {@link GuiElement} for chaining
     */
    public GuiElement resizer(IResizer resizer)
    {
        this.resizer = resizer;
        return this;
    }

    /* Margin */

    /**
     * Sets the horizontal and vertical margins of this {@link GuiElement} to the same value.
     *
     * @param all the margin on the left, right, top and bottom
     * @return this {@link GuiElement}
     */
    public GuiElement marginAll(int all)
    {
        return this.marginAxis(all, all);
    }

    /**
     * Sets the horizontal and vertical margins of this {@link GuiElement}.
     *
     * @param horizontal the margin on the left and right
     * @param vertical the margin on the top and bottom
     * @return this {@link GuiElement} for chaining
     */
    public GuiElement marginAxis(int horizontal, int vertical)
    {
        return this.marginGetter(horizontal, vertical, horizontal, vertical);
    }

    /**
     * Sets the margin on all sides of this {@link GuiElement}.
     *
     * @param left the margin on the left
     * @param top the margin on the top
     * @param right the margin on the right
     * @param bottom the margin on the bottom
     * @return this {@link GuiElement} for chaining
     */
    public GuiElement marginGetter(int left, int top, int right, int bottom)
    {
        this.margin.all(left, top, right, bottom);
        return this;
    }

    /**
     * Sets the margin on the left of this {@link GuiElement}.
     *
     * @param left the margin on the left
     * @return this {@link GuiElement} for chaining
     */
    public GuiElement marginLeft(int left)
    {
        this.margin.left(left);
        return this;
    }

    /**
     * Sets the margin on the top of this {@link GuiElement}.
     *
     * @param top the margin on the top
     * @return this {@link GuiElement} for chaining
     */
    public GuiElement marginTop(int top)
    {
        this.margin.top(top);
        return this;
    }

    /**
     * Sets the margin on the right of this {@link GuiElement}.
     *
     * @param right the margin on the right
     * @return this {@link GuiElement} for chaining
     */
    public GuiElement marginRight(int right)
    {
        this.margin.right(right);
        return this;
    }

    /**
     * Sets the margin on the bottom of this {@link GuiElement}.
     *
     * @param bottom the margin on the bottom
     * @return this {@link GuiElement} for chaining
     */
    public GuiElement marginBottom(int bottom)
    {
        this.margin.bottom(bottom);
        return this;
    }

    /* Enabled methods */

    /**
     * Whether this element is enabled (and can accept any input).
     *
     * @return true if enabled and visible, false otherwise
     */
    public boolean isEnabled()
    {
        return this.enabled && this.visible;
    }
    
    /**
     * Sets the enabled state of this element.
     *
     * @param enabled true to enable the element, false to disable it
     */
    public void setEnabled(boolean enabled)
    {
        this.enabled = enabled;
    }

    /**
     * Whether this element is visible on the screen.
     * 
     * @return true if visible, false otherwise
     */
    public boolean isVisible()
    {
        return this.visible;
    }

    /**
     * Sets the visibility of this element.
     * When called, sets the visibility of this element to the specified value.
     *
     * @param visible the new visibility of this element
     */
    public void setVisible(boolean visible)
    {
        this.visible = visible;
    }

    /**
     * Toggles the visibility of this element.
     * When called, the visibility of this element is inverted.
     * If the element is currently visible, it will become invisible.
     * If the element is currently invisible, it will become visible.
     */
    public void toggleVisible()
    {
        this.visible = !this.visible;
    }
    
    /**
     * Checks if this element can be seen on the screen.
     * It checks if the element is visible, has a parent, and if all its parents are visible.
     *
     * @return true if the element can be seen, false otherwise
     */
    @SuppressWarnings("rawtypes")
    public boolean canBeSeen()
    {
        if (!this.hasParent() || !this.isVisible())
        {
            return false;
        }
        GuiElement element = this;
        while (true)
        {
            if (!element.isVisible())
            {
                return false;
            }
            GuiElement parent = element.getParent();
            if (parent instanceof GuiDelegateElement && ((GuiDelegateElement) parent).delegate != element)
            {
                return false;
            }
            if (parent == null)
            {
                break;
            }
            element = parent;
        }
        return element instanceof GuiBase.GuiRootElement;
    }

    /* Overriding those methods so it would be much easier to 
     * override only needed methods in subclasses */

    /**
     * Resizes the element and its children.
     * If this element has a resizer, it applies it to the area of this element.
     * If this element has children, it resizes them.
     * If this element has a resizer, it applies it to the area of this element.
     */
    public void resize()
    {
        if (this.resizer != null)
        {
            this.resizer.apply(this.area);
        }
        if (this.children != null)
        {
            this.children.resize();
        }
        if (this.resizer != null)
        {
            this.resizer.postApply(this.area);
        }
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
        if (this.children != null && this.children.mouseGetsClicked(context))
        {
            return true;
        }
        if (!context.awaitsRightClick && this.area.isInside(context) && context.mouseButton == 1)
        {
            if (!context.hasContextMenu())
            {
                GuiContextMenu menu = this.createContextMenu(context);
                if (menu != null)
                {
                    context.setContextMenu(menu);
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Clicks this element itself using the default mouse button (0).
     *
     * This method simulates a mouse click event on this element.
     * The mouse button is temporarily replaced with the default mouse button (0).
     *
     * @param context the context of the mouse click
     */
    public void clicker(GuiContext context)
    {
        this.clickItself(context, 0);
    }

    /**
     * Clicks this element itself using the given mouse button.
     *
     * This method simulates a mouse click event on this element.
     * The mouse button is temporarily replaced with the specified mouse button.
     *
     * @param context the context of the mouse click
     * @param mouseButton the mouse button to simulate
     */
    public void clickItself(GuiContext context, int mouseButton)
    {
        if (!this.isEnabled())
        {
            return;
        }
        int mouseX = context.mouseX;
        int mouseY = context.mouseY;
        int button = context.mouseButton;
        context.mouseX = this.area.x + 1;
        context.mouseY = this.area.y + 1;
        context.mouseButton = mouseButton;
        this.mouseGetsClicked(context);
        context.mouseX = mouseX;
        context.mouseY = mouseY;
        context.mouseButton = button;
    }

    /**
     * Create a context menu instance
     *
     * Some subclasses of GuiElement might want to override this method in order to create their
     * own context menus.
     */
    
    /**
     * Create a context menu instance. If the context menu is null, returns null. Otherwise, returns the
     * result of calling the context menu's get() method.
     *
     * @param context the context of the mouse right click
     * @return the created context menu, or null if contextMenu is null
     */
    public GuiContextMenu createContextMenu(GuiContext context)
    {
        return this.contextMenu == null ? null : this.contextMenu.get();
    }

    /**
     * Mouse wheel was scrolled
     *
     * @param context the context of the mouse scroll
     * @return the result of calling the children's mouseScrolled method, or false if the children are null
     */
    public boolean mouseScrolled(GuiContext context)
    {
        return this.children != null && this.children.mouseScrolled(context);
    }

    /**
     * Mouse was released.
     *
     * If the children are not null, call the children's mouseReleased method.
     *
     * @param context the context of the mouse release
     */
    public void mouseGetsReleased(GuiContext context)
    {
        if (this.children != null)
        {
            this.children.mouseGetsReleased(context);
        }
    }

    /**
     * Key was typed.
     *
     * If the children are not null and the children's keyTyped method returns true, returns true.
     * If the keybinds are not null and the keybinds' check method returns true and the area is inside the context, returns true.
     * Otherwise, returns false.
     *
     * @param context the context of the key typed
     * @return true if the children's keyTyped method returns true, or if the keybinds' check method returns true and the area is inside the context, otherwise false
     */
    public boolean keyTyped(GuiContext context)
    {
        if (this.children != null && this.children.keyTyped(context))
        {
            return true;
        }
        if (this.keybinds != null && this.keybinds.check(context, this.area.isInside(context)))
        {
            return true;
        }
        return false;
    }

    public boolean canBeDrawn(Area viewport)
    {
        return !this.culled || viewport.intersects(this.area);
    }

    public void draw(GuiContext context)
    {
        if (this.keybinds != null && this.isEnabled())
        {
            this.keybinds.add(context, this.area.isInside(context));
        }
        if (this.tooltip != null && this.area.isInside(context))
        {
            context.tooltip.set(context, this);
        }
        else if ((this.hideTooltip || this.container) && this.area.isInside(context))
        {
            context.resetTooltip();
        }
        if (this.children != null)
        {
            this.children.draw(context);
        }
    }

    public void drawTooltip(GuiContext context, Area area)
    {
        context.tooltip.draw(this.tooltip, context);
    }
}