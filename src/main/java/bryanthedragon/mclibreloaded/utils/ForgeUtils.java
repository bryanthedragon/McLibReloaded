package bryanthedragon.mclibreloaded.utils;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;
import java.util.List;

/*
* A Utils class that holds common Forge calls.
* Should help in abstraction for porting.
*/
public class ForgeUtils
{
    /*
    * @return a list of players on the server instance
    */
    public static List<ServerPlayer> getServerPlayers()
    {
        return ServerLifecycleHooks.getCurrentServer().getPlayerList().getPlayers();
    }
}
