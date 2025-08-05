package bryanthedragon.mclibreloaded.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntityUtils 
{

    /**
     * Get the current game mode of the client's player.
     * @return client player's game mode
     */
    public static GameType getGameMode() 
    {
        return getGameMode(Minecraft.getInstance().player);
    }

    /**
     * Get the current game mode of the specified player.
     * @return specified player's game mode
     */
    public static GameType getGameMode(Player player) 
    {
        PlayerInfo info = getNetworkInfo(player);
        return info == null ? GameType.SURVIVAL : info.getGameMode();
    }

    /**
     * Checks if the player is in adventure mode.
     *
     * @param player to check
     * @return true if the player is in adventure mode, false otherwise
     */
    public static boolean isAdventureMode(Player player) 
    {
        PlayerInfo info = getNetworkInfo(player);
        return info != null && info.getGameMode() == GameType.ADVENTURE;
    }

    /**
     * Get the network info of the specified player.
     * @param player the player to get the network info of
     * @return the network info of the player, or null if the player or the connection is null
     */
    public static PlayerInfo getNetworkInfo(Player player) 
    {
        Minecraft mc = Minecraft.getInstance();
        ClientPacketListener connection = mc.getConnection();
        if (connection == null || player == null) 
        {
            return null;
        }
        return connection.getPlayerInfo(player.getGameProfile().getId());
    }
}

