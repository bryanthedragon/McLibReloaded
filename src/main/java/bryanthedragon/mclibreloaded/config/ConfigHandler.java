package bryanthedragon.mclibreloaded.config;

import bryanthedragon.mclibreloaded.McLibReloaded;
import bryanthedragon.mclibreloaded.network.mclib.Dispatcher;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketConfig;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ConfigHandler
{
    @SubscribeEvent
    public void onPlayerLogIn(PlayerEvent.PlayerLoggedInEvent event)
    {
        MinecraftServer server = event.player.getServer();

        if (server == null || server.isSingleplayer())
        {
            return;
        }

        for (Config config : McLibReloaded.proxy.configs.modules.values())
        {
            if (config.hasSyncable())
            {
                Dispatcher.sendTo(new PacketConfig(config.filterSyncable(), true), (Player) event.player);
            }
        }
    }
}
