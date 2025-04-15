package bryanthedragon.mclibreloaded.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

public class OpHelper
{
    /*
    * Minimum OP level according to vanilla code
    */
    public static final int VANILLA_OP_LEVEL = 2;

    public static int getPlayerOpLevel()
    {
        EntityPlayerSP player = Minecraft.getInstance().player;

        return player == null ? 0 : player.getPermissionLevel();
    }

    public static boolean isPlayerOp()
    {
        return isOp(getPlayerOpLevel());
    }

    public static boolean isPlayerOp(EntityPlayerMP player)
    {
        if (player == null)
        {
            return false;
        }

        MinecraftServer server = player.mcServer;

        if (server.getPlayerList().canSendCommands(player.getGameProfile()))
        {
            UserListOpsEntry userEntry = server.getPlayerList().getOppedPlayers().getEntry(player.getGameProfile());

            return isOp(userEntry == null ? server.getOpPermissionLevel() : userEntry.getPermissionLevel());
        }

        return false;
    }

    public static boolean isOp(int opLevel)
    {
        return opLevel >= VANILLA_OP_LEVEL;
    }
}
