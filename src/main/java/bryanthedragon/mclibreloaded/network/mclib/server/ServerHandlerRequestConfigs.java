package bryanthedragon.mclibreloaded.network.mclib.server;

import bryanthedragon.mclibreloaded.McLibReloaded;
import bryanthedragon.mclibreloaded.config.Config;
import bryanthedragon.mclibreloaded.config.ConfigManager;
import bryanthedragon.mclibreloaded.network.ServerMessageHandler;
import bryanthedragon.mclibreloaded.network.mclib.Dispatcher;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketConfig;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketRequestConfigs;
import bryanthedragon.mclibreloaded.utils.OpHelper;
import com.mojang.brigadier.Message;
import net.minecraft.world.entity.player.Player;

public class ServerHandlerRequestConfigs extends ServerMessageHandler<PacketRequestConfigs>
{
    @Override
    public void run(Player player, PacketRequestConfigs message)
    {
        if (!OpHelper.isPlayerOp())
        {
            return;
        }

        ConfigManager manager = McLibReloaded.proxy.configs;

        for (Config config : manager.modules.values())
        {
            Config serverSide = config.filterServerSide();

            if (!serverSide.values.isEmpty())
            {
                Dispatcher.sendTo((Message) new PacketConfig(serverSide), player);
            }
        }
    }
}