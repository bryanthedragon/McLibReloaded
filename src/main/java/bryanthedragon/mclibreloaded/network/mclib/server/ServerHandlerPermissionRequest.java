package bryanthedragon.mclibreloaded.network.mclib.server;

import bryanthedragon.mclibreloaded.network.ServerMessageHandler;
import bryanthedragon.mclibreloaded.network.mclib.client.ClientHandlerAnswer;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketRequestPermission;
import bryanthedragon.mclibreloaded.permissions.PermissionCategory;
import net.minecraft.server.level.ServerPlayer;

public class ServerHandlerPermissionRequest extends ServerMessageHandler<PacketRequestPermission>
{
    public void run(ServerPlayer player, PacketRequestPermission message)
    {
        PermissionCategory perm = message.getPermissionRequest();
        boolean hasPermission = perm != null && perm.playerHasServerPermission(player);
        ClientHandlerAnswer.sendAnswerTo(player, message.getAnswer(hasPermission));
    }
}
