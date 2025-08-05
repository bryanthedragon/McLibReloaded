package bryanthedragon.mclibreloaded.network.mclib.server;

import bryanthedragon.mclibreloaded.McLibReloaded;
import bryanthedragon.mclibreloaded.config.Config;
import bryanthedragon.mclibreloaded.config.ConfigManager;
import bryanthedragon.mclibreloaded.network.ServerMessageHandler;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketConfig;
import bryanthedragon.mclibreloaded.utils.OpHelper;

import net.minecraft.server.level.ServerPlayer;

public class ServerHandlerConfig extends ServerMessageHandler<PacketConfig>
{
    /**
     * Handles a server-side message by checking if the player is an OP and if
     * the config exists in the config manager. If both conditions are true, this
     * method will copy the config from the message to the config manager and
     * save it. If the config has any syncable fields, those fields will be
     * synchronized to all players on the server.
     * 
     * @param player The player associated with the message.
     * @param message The message to be processed.
     */
    public void run(ServerPlayer player, PacketConfig message)
    {
        if (!OpHelper.isPlayerOp(player))
        {
            return;
        }
        Config present = McLibReloaded.commonproxy.configs.modules.get(message.config.id);
        if (present != null)
        {
            present.copy(message.config);
            present.save();
            if (present.hasSyncable())
            {
                ConfigManager.synchronizeConfig(present.filterSyncable(), player.getServer(), null);
            }
        }
    }
}