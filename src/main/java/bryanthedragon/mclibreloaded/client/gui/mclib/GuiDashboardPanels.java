package bryanthedragon.mclibreloaded.client.gui.mclib;

import bryanthedragon.mclibreloaded.client.gui.framework.GuiBase;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.GuiPanelBase;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.buttons.GuiIconElement;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiContext;
import bryanthedragon.mclibreloaded.client.gui.utils.Icon;
import bryanthedragon.mclibreloaded.client.gui.utils.keys.IKey;
import bryanthedragon.mclibreloaded.permissions.PermissionUtils;
import bryanthedragon.mclibreloaded.utils.Direction;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import java.util.function.Consumer;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;

@SuppressWarnings("rawtypes")
public class GuiDashboardPanels extends GuiPanelBase<GuiDashboardPanel>
{
    public GuiDashboardPanels(Minecraft mc)
    {
        super(mc, Direction.LEFT);
    }

    public void open()
    {
        for (GuiDashboardPanel panel : this.panels)
        {
            Consumer<Boolean> task = (enabled) ->
            {
                if (enabled)
                {
                    panel.open();
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
    }

    public void close()
    {
        for (GuiDashboardPanel panel : this.panels)
        {
            panel.close();
        }
    }

    @Override
    public void setPanel(GuiDashboardPanel panel)
    {
        if (this.view.delegate != null)
        {
            this.view.delegate.disappear();
        }

        super.setPanel(panel);

        if (this.view.delegate != null)
        {
            this.view.delegate.appear();
        }
    }

    @Override
    public GuiIconElement registerPanel(GuiDashboardPanel panel, IKey tooltip, Icon icon)
    {
        GuiIconElement element = super.registerPanel(panel, tooltip, icon);

        int key = this.getKeybind();

        if (key != -1)
        {
            element.keys()
                .register(IKey.comp(IKey.lang("mclib.gui.dashboard.open_panel"), tooltip), key, () -> element.clickItself(GuiBase.getCurrent()))
                .category(IKey.lang("mclib.gui.dashboard.category"));
        }

        return element;
    }

    protected int getKeybind()
    {
        int size = this.panels.size();

        switch (size)
        {
            case 1: return InputConstants.KEY_NUMPAD0;
            case 2: return InputConstants.KEY_NUMPAD1;
            case 3: return InputConstants.KEY_NUMPAD2;
            case 4: return InputConstants.KEY_NUMPAD3;
            case 5: return InputConstants.KEY_NUMPAD4;
            case 6: return InputConstants.KEY_NUMPAD5;
            case 7: return InputConstants.KEY_NUMPAD6;
            case 8: return InputConstants.KEY_NUMPAD7;
            case 9: return InputConstants.KEY_NUMPAD8;
            case 10: return InputConstants.KEY_NUMPAD9;
        }

        return -1;
    }

    @Override
    protected void drawBackground(GuiContext context, int x, int y, int w, int h)
    {
        Gui.drawRect(x, y, x + w, y + h, 0xff111111);
    }
}