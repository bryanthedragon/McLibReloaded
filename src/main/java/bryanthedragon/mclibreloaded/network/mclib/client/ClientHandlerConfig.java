package bryanthedragon.mclibreloaded.network.mclib.client;

import bryanthedragon.mclibreloaded.McLibReloaded;
import bryanthedragon.mclibreloaded.client.gui.mclib.GuiDashboard;
import bryanthedragon.mclibreloaded.config.Config;
import bryanthedragon.mclibreloaded.config.gui.GuiConfigPanel;
import bryanthedragon.mclibreloaded.network.ClientMessageHandler;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.player.LocalPlayer;

public class ClientHandlerConfig extends ClientMessageHandler<PacketConfig>
{
    @Override
    public void run(LocalPlayer player, PacketConfig message)
    {
        if (message.overwrite)
        {
            Config present = McLibReloaded.proxy.configs.modules.get(message.config.id);

            present.copyServer(message.config);
        }
        else
        {
            Screen screen = Minecraft.getInstance().screen;

            if (screen instanceof GuiDashboard)
            {
                GuiConfigPanel panel = ((GuiDashboard) screen).config;

                panel.storeServerConfig(message.config);
            }
        }
    }
}
