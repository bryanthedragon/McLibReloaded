package bryanthedragon.mclibreloaded.permissions;

import bryanthedragon.mclibreloaded.network.mclib.Dispatcher;
import bryanthedragon.mclibreloaded.network.mclib.client.ClientHandlerAnswer;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketRequestPermission;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

public class PermissionUtils
{
    /**
     * Side independent method for checking if it has the permission and then passing the result to the callback
     * @param permission the full name of the permission
     * @param player the player to check for the permission
     * @param callback the callback to be executed after checking the player or after the server has sent the permission result.
     */
    @SuppressWarnings("null")
    public static void hasServerPermission(ServerPlayer player, PermissionCategory permission, Consumer<Boolean> callback)
    {
        assert Minecraft.getInstance().level != null;
        if (!Minecraft.getInstance().level.isClientSide())
        {
            callback.accept(permission.playerHasServerPermission(player));
        }
        else
        {
            //in singleplayer there is no use in having a permission system
            if (Minecraft.getInstance().getSingleplayerServer() != null)
            {
                callback.accept(true);
            }
            else
            {
                ClientHandlerAnswer.requestServerAnswer(Dispatcher.DISPATCHER, new PacketRequestPermission(-1, permission), callback);
            }
        }
    }

    @SuppressWarnings("null")
    public static void hasLocalPermission(LocalPlayer player, PermissionCategory requiredPermission, Consumer<Boolean> task) 
    {
        if (player == null || !player.isAlive())
        {
            task.accept(false);
            return;
        }
        if (Minecraft.getInstance().level != null && !Minecraft.getInstance().level.isClientSide())
        {
            task.accept(requiredPermission.playerHasLocalPermission(player));
        }
        else
        {
            // In multiplayer client, request permission from server
            ClientHandlerAnswer.requestServerAnswer(Dispatcher.DISPATCHER, new PacketRequestPermission(-1, requiredPermission), task);
        }
    }
}
