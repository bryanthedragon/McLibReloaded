package bryanthedragon.mclibreloaded.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;

public class EntityUtils
{
    public static GameType getGameMode()
    {
        return getGameMode(Minecraft.getInstance().player);
    }

    public static GameType getGameMode(Player player)
    {
        PlayerInfo info = getNetworkInfo(player);
        return info == null ? GameType.SURVIVAL : info.getGameMode();
    }

    public static boolean isAdventureMode(Player player)
    {
        PlayerInfo info = getNetworkInfo(player);
        return info != null && info.getGameMode() == GameType.ADVENTURE;
    }
    public static boolean IsCreativeMode(Player player)
    {
        PlayerInfo info = getNetworkInfo(player);
        return info != null && info.getGameMode() == GameType.CREATIVE;
    }

    public static PlayerInfo getNetworkInfo(Player player)
    {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        if (connection == null) return null;

        return connection.getPlayerInfo(player.getUUID());
    }
}
