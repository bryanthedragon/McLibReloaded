package bryanthedragon.mclibreloaded.utils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.MinecraftServer;

import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.List;

/**
 * Utility class for common Forge calls in modern versions.
 */
public class ForgeUtils 
{
    /**
     * @return a list of all players currently connected to the server.
     */
    public static List<ServerPlayer> getServerPlayers() 
    {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) 
        {
            return server.getPlayerList().getPlayers();
        }
        return List.of(); // Empty list if server not available
    }
}
