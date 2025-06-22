package bryanthedragon.mclibreloaded.network.mclib.client;

import bryanthedragon.mclibreloaded.McLib;
import bryanthedragon.mclibreloaded.client.gui.mclib.GuiDashboard;
import bryanthedragon.mclibreloaded.config.Config;
import bryanthedragon.mclibreloaded.config.gui.GuiConfigPanel;
import bryanthedragon.mclibreloaded.network.ClientMessageHandler;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;


public class ClientHandlerConfig extends ClientMessageHandler<PacketConfig> 
{

    public void run(LocalPlayer player, PacketConfig message) 
    {
        if (message.overwrite) 
        {
            Config present = McLib.proxy.configs.modules.get(message.config.id);
            present.copyServer(message.config);
        } 
        else 
        {
            var screen = Minecraft.getInstance().screen;

            if (screen instanceof GuiDashboard dashboard) 
            {
                GuiConfigPanel panel = dashboard.config;
                panel.storeServerConfig(message.config);
            }
        }
    }
}
