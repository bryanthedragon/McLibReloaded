package bryanthedragon.mclibreloaded.client;

import javax.swing.text.JTextComponent.KeyBinding;
import bryanthedragon.mclibreloaded.McLib;
import bryanthedragon.mclibreloaded.client.gui.framework.GuiBase;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiInventoryElement;
import bryanthedragon.mclibreloaded.client.gui.mclib.GuiDashboard;
import bryanthedragon.mclibreloaded.config.values.ValueRL;
import bryanthedragon.mclibreloaded.events.RemoveDashboardPanels;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

public class KeyboardHandler
{
    public KeyBinding dashboard;

    private int lastGuiScale = -1;

    public KeyboardHandler()
    {
        this.dashboard = new KeyBinding("key.mclib.dashboard", GLFW.GLFW_KEY_0, "key.mclib.category");

        ClientRegistry.registerKeyBinding(this.dashboard);
    }

    @SubscribeEvent
    public void onKeyboardInput(InputEvent.KeyInputEvent event)
    {
        if (this.dashboard.isPressed())
        {
            GuiDashboard dashboard = GuiDashboard.get();

            Minecraft.getInstance().displayGuiScreen(dashboard);

            if (GuiScreen.isCtrlKeyDown())
            {
                dashboard.panels.setPanel(dashboard.config);
            }
        }
    }

    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event)
    {
        if (event.getGui() instanceof GuiBase)
        {
            if (this.lastGuiScale == -1)
            {
                this.lastGuiScale = Minecraft.getInstance().gameSettings.guiScale;

                int scale = McLib.userIntefaceScale.get();

                if (scale > 0)
                {
                    Minecraft.getInstance().gameSettings.guiScale = scale;
                }
            }
        }
        else
        {
            if (this.lastGuiScale != -1)
            {
                Minecraft.getInstance().gameSettings.guiScale = this.lastGuiScale;
                this.lastGuiScale = -1;
            }

            if (Minecraft.getInstance().world == null)
            {
                GuiDashboard.dashboard = null;
                ValueRL.picker = null;
                GuiInventoryElement.container = null;

                McLib.proxy.configs.resetServerValues();
                McLib.EVENT_BUS.post(new RemoveDashboardPanels());
            }
        }
    }
}