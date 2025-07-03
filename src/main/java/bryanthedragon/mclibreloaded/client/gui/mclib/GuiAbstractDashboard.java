package bryanthedragon.mclibreloaded.client.gui.mclib;

import bryanthedragon.mclibreloaded.client.gui.framework.GuiBase;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.buttons.GuiIconElement;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiDraw;
import bryanthedragon.mclibreloaded.client.gui.utils.Keybind;
import bryanthedragon.mclibreloaded.events.RegisterDashboardPanels;
import bryanthedragon.mclibreloaded.permissions.PermissionUtils;
import bryanthedragon.mclibreloaded.utils.OpHelper;
import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public abstract class GuiAbstractDashboard extends GuiBase
{
    public GuiDashboardPanels panels;
    @SuppressWarnings("rawtypes")
    public GuiDashboardPanel defaultPanel;

    private boolean wasClosed = true;
    public GuiAbstractDashboard(Minecraft mc)
    {
        this.panels = this.createDashboardPanels(mc);

        this.panels.flex().relative(this.viewport).wh(1F, 1F);
        this.registerPanels(mc);

        McLib.EVENT_BUS.post(new RegisterDashboardPanels(this));

        this.root.add(this.panels);
    }

    protected abstract GuiDashboardPanels createDashboardPanels(Minecraft mc);

    protected abstract void registerPanels(Minecraft mc);

    public boolean doesGuiPauseGame()
    {
        return false;
    }

    public void onGuiClosed()
    {
        this.close();
        super.onGuiClosed();
    }

    private void close()
    {
        this.panels.close();
        this.wasClosed = true;
    }

    public void setWorldAndResolution(Minecraft mc, int width, int height)
    {
        this.checkPermissions();

        if (this.wasClosed)
        {
            this.wasClosed = false;
            this.panels.open();
            this.panels.setPanel(this.panels.view.delegate);
        }

        super.setWorldAndResolution(mc, width, height);
    }

    @SuppressWarnings({ "rawtypes", "unused" })
    private void checkPermissions()
    {
        int newOpLevel = OpHelper.getPlayerOpLevel();

        for (GuiDashboardPanel panel : this.panels.panels)
        {
            GuiIconElement button = this.panels.getButton(panel);

            Consumer<Boolean> task = (enabled) ->
            {
                button.setEnabled(enabled);

                for (Keybind keybind : this.panels.keys().keybinds)
                {
                    keybind.active(enabled);
                }
            };

            if (panel.getRequiredPermission() != null)
            {
                PermissionUtils.hasPermission(Minecraft.getInstance().player, panel.getRequiredPermission(), task);
            }
            else
            {
                task.accept(true);
            }
        }

        GuiDashboardPanel current = this.panels.view.delegate;

        if (current != null && current.getRequiredPermission() != null)
        {
            this.panels.setPanel(null);

            PermissionUtils.hasPermission(Minecraft.getInstance().player, current.getRequiredPermission(), (allowed) ->
            {
                if (allowed)
                {
                    this.panels.setPanel(current);
                }
                else
                {
                    this.panels.setPanel(this.defaultPanel);
                }
            });
        }
        else if (current == null)
        {
            this.panels.setPanel(null);
        }
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        if (this.panels.view.delegate != null && this.panels.view.delegate.needsBackground())
        {
            GuiDraw.drawCustomBackground(0, 0, this.width, this.height);
        }
        else
        {
            this.drawGradientRect(0, 0, this.width, this.height / 8, 0x44000000, 0);
            this.drawGradientRect(0, this.height - this.height / 8, this.width, this.height, 0, 0x44000000);
        }

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}