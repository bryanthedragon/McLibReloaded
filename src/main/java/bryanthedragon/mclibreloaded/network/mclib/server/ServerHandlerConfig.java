package bryanthedragon.mclibreloaded.network.mclib.server;

import bryanthedragon.mclibreloaded.McLib;
import bryanthedragon.mclibreloaded.config.Config;
import bryanthedragon.mclibreloaded.config.ConfigManager;
import bryanthedragon.mclibreloaded.network.ServerMessageHandler;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketConfig;
import bryanthedragon.mclibreloaded.utils.OpHelper;

public class ServerHandlerConfig extends ServerMessageHandler<PacketConfig>
{
    @Override
    public void run(PlayerMP player, PacketConfig message)
    {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }

        Config present = McLib.proxy.configs.modules.get(message.config.id);

        if (present != null)
        {
            present.copy(message.config);
            present.save();

            if (present.hasSyncable())
            {
                ConfigManager.synchronizeConfig(present.filterSyncable(), player.getServerWorld().getMinecraftServer(), null);
            }
        }
    }
}