package bryanthedragon.mclibreloaded.forge.permissions.bridger;

import bryanthedragon.mclibreloaded.forge.permissions.handler.LegacyPermissionHandler;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;

// This class bridges the legacy permission system to the new API
// It allows the new API to check permissions using the legacy format
// This is a simple example, in a real implementation you might need to handle UUIDs differently
// and ensure the legacy handler is properly initialized with existing permissions.
public class LegacyPermissionBridge 
{
    private final LegacyPermissionHandler legacyHandler = new LegacyPermissionHandler();

    public boolean hasServerPermission(ServerPlayer player, String node) 
    {
        // Translate player to legacy format, maybe via UUID
        return legacyHandler.checkServerPermission(player.getUUID().toString(), node);
    }

    public boolean hasLocalPermission(LocalPlayer player, String node) 
    {
        // Translate player to legacy format, maybe via UUID
        return legacyHandler.checkLocalPermission(player.getUUID().toString(), node);
    }
}