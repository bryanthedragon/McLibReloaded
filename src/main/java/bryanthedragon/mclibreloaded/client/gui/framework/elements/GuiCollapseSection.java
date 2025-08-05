package bryanthedragon.mclibreloaded.client.gui.framework.elements;

import bryanthedragon.mclibreloaded.McLibReloaded;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiContext;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiLabel;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.IconContainer;
import bryanthedragon.mclibreloaded.client.gui.utils.Elements;
import bryanthedragon.mclibreloaded.client.gui.utils.Icons;
import bryanthedragon.mclibreloaded.client.gui.utils.keys.IKey;
import bryanthedragon.mclibreloaded.utils.ColorUtils;

import net.minecraft.client.Minecraft;

import java.util.function.Supplier;

public class GuiCollapseSection extends GuiElement
{
    protected GuiLabel title;
    protected GuiElement fields;
    protected boolean collapsed;
    protected IconContainer collapsedIcon = new IconContainer(Icons.MOVE_RIGHT, 14, 10, 0.5F, 0.5F, -2, -2);
    protected IconContainer openedIcon = new IconContainer(Icons.MOVE_DOWN, 14, 10, 0.5F, 0.5F, -2, 0);

    /**
     * @param mc
     * @param title
     * @param titleBackground the background color of the title as Supplier to allow changes of the color through configurations.
     * @param collapsed true if it should be collapsed by default, false if it should display its fields.
     */
    public GuiCollapseSection(Minecraft mc, IKey title, Supplier<Integer> titleBackground, boolean collapsed)
    {
        super(mc);

        this.title = Elements.label(title).background(titleBackground);
        this.title.setLeftIconContainer(this.collapsedIcon);
        this.fields = new GuiElement(mc);
        this.fields.flex().relative(this).column(5).stretch().vertical().height(20);
        this.flex().column(5).stretch().vertical();
        super.add(this.title);
        if (!collapsed)
        {
            super.add(this.fields);
            this.title.setLeftIconContainer(this.openedIcon);
        }
        this.collapsed = collapsed;
    }

    public GuiCollapseSection(Minecraft mc, IKey title, Supplier<Integer> titleBackground)
    {
        this(mc, title, titleBackground, false);
    }

    public GuiCollapseSection(Minecraft mc, IKey title)
    {
        this(mc, title, () -> ColorUtils.HALF_BLACK + McLibReloaded.primaryColor.get());
    }

    /**
     * Sets the collapsed state of the GuiCollapseSection. If the desired state is different from the current state,
     * the updateCollapse method is called.
     *
     * @param collapsed the desired state of the GuiCollapseSection
     */
    public void setCollapsed(boolean collapsed)
    {
        if (this.collapsed != collapsed)
        {
            this.updateCollapse();
        }
    }

    /**
     * Returns whether this GuiCollapseSection is collapsed or not.
     *
     * @return true if this GuiCollapseSection is collapsed, false otherwise.
     */
    public boolean isCollapsed()
    {
        return this.collapsed;
    }

    /**
     * @deprecated Use {@link #add(IGuiElement...)} instead.
     * Adds a field to the GuiCollapseSection.
     *
     * @param element the GuiElement to be added to the fields.
     */
    public void addField(GuiElement element)
    {
        this.fields.add(element);
    }

    /**
     * Adds multiple GuiElements to the fields of the GuiCollapseSection.
     *
     * @param element the GuiElements to be added to the fields.
     */
    public void addFields(GuiElement... element)
    {
        this.fields.addArray(element);
    }

    /**
     * Adds multiple GuiElements to the fields of the GuiCollapseSection. The order of the elements is preserved.
     *
     * @param elements the GuiElements to be added to the fields.
     */
    public void addGetArray(IGuiElement... elements)
    {
        this.fields.addArray(elements);
    }

    /**
     * Adds a GuiElement to the fields of the GuiCollapseSection.
     *
     * @param element the GuiElement to be added to the fields.
     */
    public void add(IGuiElement element)
    {
        this.fields.add(element);
    }

    /**
     * Gets the title of the GuiCollapseSection.
     *
     * @return the title of the GuiCollapseSection.
     */
    public GuiLabel getTitle()
    {
        return this.title;
    }

    /**
     * Updates the collapsed state of the GuiCollapseSection. If the section is currently collapsed, it removes its fields from
     * the parent and sets the left icon of its title to the collapsed icon. If the section is not collapsed, it adds its fields
     * back to the parent and sets the left icon of its title to the opened icon.
     */
    protected void updateCollapse()
    {
        if (!this.collapsed)
        {
            this.fields.removeFromParent();
            this.title.setLeftIconContainer(this.collapsedIcon);
            this.collapsed = true;
        }
        else
        {
            super.add(this.fields);
            this.title.setLeftIconContainer(this.openedIcon);
            this.collapsed = false;
        }
    }

    /**
     * Handles the mouse click event. If the mouse click occurred on the title of the GuiCollapseSection, the collapse state of the
     * section is updated and its parent is resized. If the parent is not null, it is resized. If the mouse click did not occur on the
     * title, the method returns false.
     *
     * @param context the GuiContext of the mouse click event
     * @return true if the mouse click event occurred on the title of the GuiCollapseSection, false otherwise
     */
    public boolean mouseClicked(GuiContext context)
    {
        if (super.mouseGetsClicked(context))
        {
            return true;
        }
        if (this.title.area.isInside(context))
        {
            this.updateCollapse();
            GuiElement element = this.getRoot();
            if (element != null) 
            {
                element.resize();
            }
            return true;
        }
        return false;
    }
}
