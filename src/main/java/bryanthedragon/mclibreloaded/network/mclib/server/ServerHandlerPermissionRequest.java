package bryanthedragon.mclibreloaded.network.mclib.server;

import bryanthedragon.mclibreloaded.network.ServerMessageHandler;
import bryanthedragon.mclibreloaded.network.mclib.client.ClientHandlerAnswer;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketRequestPermission;
import bryanthedragon.mclibreloaded.permissions.PermissionCategory;
import net.minecraft.entity.player.PlayerMP;

public class ServerHandlerPermissionRequest extends ServerMessageHandler<PacketRequestPermission>
{
    @Override
    public void run(PlayerMP player, PacketRequestPermission message)
    {
        PermissionCategory perm = message.getPermissionRequest();

        boolean hasPermission = perm != null && perm.playerHasPermission(player);

        ClientHandlerAnswer.sendAnswerTo(player, message.getAnswer(hasPermission));
    }
}
