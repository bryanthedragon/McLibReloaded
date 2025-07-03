package bryanthedragon.mclibreloaded.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OpHelper
{
    /**
     * Minimum OP level according to vanilla code
     */
    public static final int VANILLA_OP_LEVEL = 2;

    @OnlyIn(Dist.CLIENT)
    public static int getPlayerOpLevel()
    {
        PlayerSP player = Minecraft.getInstance().player;

        return player == null ? 0 : player.getPermissionLevel();
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isPlayerOp()
    {
        return isOp(getPlayerOpLevel());
    }

    public static boolean isPlayerOp(PlayerMP player)
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
