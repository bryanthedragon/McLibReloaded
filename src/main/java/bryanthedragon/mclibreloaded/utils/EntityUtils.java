package bryanthedragon.mclibreloaded.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EntityUtils
{
    @OnlyIn(Dist.CLIENT)
    public static GameType getGameMode()
    {
        return getGameMode(Minecraft.getInstance().player);
    }

    @OnlyIn(Dist.CLIENT)
    public static GameType getGameMode(Player player)
    {
        NetworkPlayerInfo networkplayerinfo = EntityUtils.getNetworkInfo(player);

        return networkplayerinfo == null ? GameType.SURVIVAL : networkplayerinfo.getGameType();
    }

    @OnlyIn(Dist.CLIENT)
    public static boolean isAdventureMode(Player player)
    {
        NetworkPlayerInfo info = getNetworkInfo(player);

        return info != null && info.getGameType() == GameType.ADVENTURE;
    }

    @OnlyIn(Dist.CLIENT)
    public static NetworkPlayerInfo getNetworkInfo(Player player)
    {
        NetHandlerPlayClient connection = Minecraft.getInstance().getConnection();

        if (connection == null)
        {
            return null;
        }

        return connection.getPlayerInfo(player.getGameProfile().getId());
    }
}
