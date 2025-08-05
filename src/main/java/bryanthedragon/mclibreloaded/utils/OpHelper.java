package bryanthedragon.mclibreloaded.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class OpHelper
{
    /**
     * Minimum OP level according to vanilla code
     */
    public static final int VANILLA_OP_LEVEL = 2;

    /**
     * Gets the permission level of the client player
     * @return the permission level of the player, or 0 if the player is null
     */
    @OnlyIn(Dist.CLIENT)
    public static int getPlayerOpLevel()
    {
        LocalPlayer player = Minecraft.getInstance().player;
        return player == null ? 0 : player.getPermissionLevel();
    }

    /**
     * Gets whether the local client player is an OP
     * @return whether the local client player is an OP
     */
    @OnlyIn(Dist.CLIENT)
    public static boolean isPlayerOp()
    {
        return isOp(getPlayerOpLevel());
    }


    /**
     * Gets whether the server player is an OP
     * @return whether the server player is an OP
     */
    public static boolean isPlayerOp(ServerPlayer player)
    {
        if (player == null)
        {
            return false;
        }
        return player.hasPermissions(VANILLA_OP_LEVEL);
    }

    /**
     * Determines whether the given permission level is an OP.
     * @param opLevel the permission level to check
     * @return whether the given permission level is an OP
     */
    public static boolean isOp(int opLevel)
    {
        return opLevel >= VANILLA_OP_LEVEL;
    }
}
