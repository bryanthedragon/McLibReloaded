package bryanthedragon.mclibreloaded.network.mclib.server;

import bryanthedragon.mclibreloaded.McLibReloaded;
import bryanthedragon.mclibreloaded.config.Config;
import bryanthedragon.mclibreloaded.config.ConfigManager;
import bryanthedragon.mclibreloaded.network.ServerMessageHandler;
import bryanthedragon.mclibreloaded.network.mclib.Dispatcher;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketConfig;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketRequestConfigs;
import bryanthedragon.mclibreloaded.utils.OpHelper;

import net.minecraft.server.level.ServerPlayer;

public class ServerHandlerRequestConfigs extends ServerMessageHandler<PacketRequestConfigs>
{
    public void run(ServerPlayer player, PacketRequestConfigs message)
    {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }
        ConfigManager manager = (ConfigManager) McLibReloaded.proxy.configs;
        for (Config config : manager.modules.values())
        {
            Config serverSide = config.filterServerSide();
            if (!serverSide.values.isEmpty())
            {
                Dispatcher.sendTo(new PacketConfig(serverSide), player);
            }
        }
    }
}