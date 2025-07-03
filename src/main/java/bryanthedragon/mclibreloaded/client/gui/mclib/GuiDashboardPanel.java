package bryanthedragon.mclibreloaded.client.gui.mclib;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.GuiElement;
import bryanthedragon.mclibreloaded.permissions.PermissionCategory;
import bryanthedragon.mclibreloaded.utils.OpHelper;
import net.minecraft.client.Minecraft;

public class GuiDashboardPanel <T extends GuiAbstractDashboard> extends GuiElement
{
    public final T dashboard;

    public GuiDashboardPanel(Minecraft mc, T dashboard)
    {
        super(mc);

        this.dashboard = dashboard;
        this.markContainer();
    }

    @Deprecated
    public boolean canBeOpened(int opLevel)
    {
        return this.isClientSideOnly() || OpHelper.isOp(opLevel);
    }

    public PermissionCategory getRequiredPermission()
    {
        return null;
    }

    public boolean isClientSideOnly()
    {
        return false;
    }

    public boolean needsBackground()
    {
        return true;
    }

    public void appear()
    {}

    public void disappear()
    {}

    public void open()
    {}

    public void close()
    {}

    public int getRequiredOpLevel() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRequiredOpLevel'");
    }
}