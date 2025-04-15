package bryanthedragon.mclibreloaded.client.gui.utils.resizers;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.GuiElement;
import bryanthedragon.mclibreloaded.client.gui.utils.Area;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IResizer
{
    public void preApply(Area area);

    public void apply(Area area);

    public void postApply(Area area);

    @SideOnly(Side.CLIENT)
    public void add(GuiElement parent, GuiElement child);

    @SideOnly(Side.CLIENT)
    public void remove(GuiElement parent, GuiElement child);

    public int getX();

    public int getY();

    public int getW();

    public int getH();
}