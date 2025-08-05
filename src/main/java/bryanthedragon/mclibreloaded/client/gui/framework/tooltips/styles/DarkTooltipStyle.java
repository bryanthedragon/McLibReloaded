package bryanthedragon.mclibreloaded.client.gui.framework.tooltips.styles;

import bryanthedragon.mclibreloaded.McLibReloaded;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiDraw;
import bryanthedragon.mclibreloaded.client.gui.utils.Area;

public class DarkTooltipStyle extends TooltipStyle
{
    @Override
    public void drawBackground(Area area)
    {
        int color = McLibReloaded.primaryColor.get();

        GuiDraw.drawDropShadow(area.x, area.y, area.ex(), area.ey(), 6, 0x44000000 + color, color);
        area.draw(0xff000000);
    }

    @Override
    public int getTextColor()
    {
        return 0xffffff;
    }

    @Override
    public int getForegroundColor()
    {
        return McLibReloaded.primaryColor.get();
    }
}