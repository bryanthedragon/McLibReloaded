package bryanthedragon.mclibreloaded.forge.permissions;

import bryanthedragon.mclibreloaded.forge.permissions.bridger.LegacyPermissionBridge;
import bryanthedragon.mclibreloaded.forge.permissions.events.LegacyPermissionEvents;
import bryanthedragon.mclibreloaded.forge.permissions.exceptions.LegacyPermissionExceptions;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;

public class PermissionAPI 
{
    private static final LegacyPermissionBridge legacyBridge = new LegacyPermissionBridge();
    private static final LegacyPermissionEvents legacyEvents = new LegacyPermissionEvents();
    private static final LegacyPermissionExceptions legacyExceptions = new LegacyPermissionExceptions();

    public static boolean hasServerPermission(ServerPlayer player, String node) 
    {
        return legacyBridge.hasServerPermission(player, node);
    }
    public static boolean hasLocalPermission(LocalPlayer player, String node) 
    {
        return legacyBridge.hasLocalPermission(player, node);
    }
    protected static LegacyPermissionEvents GetLegacyEvents()
    {
        return legacyEvents;
    }
    protected static LegacyPermissionExceptions GetLegacyExceptions()
    {
        return legacyExceptions;
    }
    public static LegacyPermissionBridge GetLegacyPermissionBridge()
    {
        return legacyBridge;
    }
}