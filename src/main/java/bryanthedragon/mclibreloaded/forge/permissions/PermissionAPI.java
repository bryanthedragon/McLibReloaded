package bryanthedragon.mclibreloaded.forge.permissions;

import bryanthedragon.mclibreloaded.forge.permissions.bridger.LegacyPermissionBridge;
import net.minecraft.server.level.ServerPlayer;

public class PermissionAPI 
{
    private static final LegacyPermissionBridge legacyBridge = new LegacyPermissionBridge();

    public static boolean hasPermission(ServerPlayer player, String node) 
    {
        return legacyBridge.hasPermission(player, node);
    }
}