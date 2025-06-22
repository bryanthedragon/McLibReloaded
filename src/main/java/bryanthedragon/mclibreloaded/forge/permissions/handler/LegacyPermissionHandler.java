package bryanthedragon.mclibreloaded.forge.permissions.handler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Set;

public class LegacyPermissionHandler 
{
    private final Map<UUID, Set<String>> permissions = new HashMap<>();

    public LegacyPermissionHandler() 
    {
        // Stub/mock permission loading
        // Pretend this was loaded from a file or DB
        UUID sampleAdmin = UUID.fromString("00000000-0000-0000-0000-000000000000");
        permissions.put(sampleAdmin, new HashSet<>(List.of("yourmodid.command.openadminmenu")));
    }

    public boolean checkPermission(String uuidStr, String node) 
    {
        UUID uuid = UUID.fromString(uuidStr);
        Set<String> playerPermissions = permissions.get(uuid);
        return playerPermissions != null && playerPermissions.contains(node);
    }

    // Optional: For testing or setup
    public void grantPermission(UUID uuid, String node) 
    {
        permissions.computeIfAbsent(uuid, k -> new HashSet<>()).add(node);
    }
}
