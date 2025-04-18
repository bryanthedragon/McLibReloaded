package bryanthedragon.mclibreloaded.client.gui.mclib;

import bryanthedragon.mclibreloaded.McLib;
import bryanthedragon.mclibreloaded.client.gui.utils.Icons;
import bryanthedragon.mclibreloaded.client.gui.utils.keys.IKey;
import bryanthedragon.mclibreloaded.config.gui.GuiConfigPanel;
import net.minecraft.client.Minecraft;

public class GuiDashboard extends GuiAbstractDashboard
{
    public static GuiDashboard dashboard;

    public GuiConfigPanel config;

    public static GuiDashboard get()
    {
        if (dashboard == null)
        {
            dashboard = new GuiDashboard(Minecraft.getMinecraft());
        }

        return dashboard;
    }

    public GuiDashboard(Minecraft mc)
    {
        super(mc);

        this.panels.registerPanel(new GuiGraphPanel(mc, this), IKey.lang("mclib.gui.graph.tooltip"), Icons.GRAPH);
    }

    @Override
    protected GuiDashboardPanels createDashboardPanels(Minecraft mc)
    {
        return new GuiDashboardPanels(mc);
    }

    @Override
    protected void registerPanels(Minecraft mc)
    {
        this.panels.registerPanel(this.config = new GuiConfigPanel(mc, this), IKey.lang("mclib.gui.config.tooltip"), Icons.GEAR);
        this.defaultPanel = this.config;

        if (McLib.debugPanel.get())
        {
            this.panels.registerPanel(new GuiDebugPanel(mc, this), IKey.str("Debug"), Icons.POSE);
        }

        this.panels.setPanel(this.config);
    }
}