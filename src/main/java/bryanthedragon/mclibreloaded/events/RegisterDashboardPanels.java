package bryanthedragon.mclibreloaded.events;

import bryanthedragon.mclibreloaded.client.gui.mclib.GuiAbstractDashboard;
import net.minecraftforge.eventbus.api.Event;

public class RegisterDashboardPanels extends Event
{
    public final GuiAbstractDashboard dashboard;

    public RegisterDashboardPanels(GuiAbstractDashboard dashboard)
    {
        this.dashboard = dashboard;
    }
}