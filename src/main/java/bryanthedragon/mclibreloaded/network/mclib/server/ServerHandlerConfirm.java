package bryanthedragon.mclibreloaded.network.mclib.server;

import bryanthedragon.mclibreloaded.network.ServerMessageHandler;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketConfirm;
import net.minecraft.world.entity.player.Player;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Consumer;

public class ServerHandlerConfirm extends ServerMessageHandler<PacketConfirm>
{
    private static final TreeMap<Integer, Consumer<Boolean>> consumers = new TreeMap<Integer, Consumer<Boolean>>();

    public void run(Player player, PacketConfirm packetConfirm)
    {
        if (consumers.containsKey(packetConfirm.consumerID))
        {
            consumers.remove(packetConfirm.consumerID).accept(packetConfirm.confirm);
        }
    }

    public static void addConsumer(int id, Consumer<Boolean> item)
    {
        consumers.put(id, item);
    }

    @SuppressWarnings("rawtypes")
    public static Entry getLastConsumerEntry()
    {
        return consumers.lastEntry();
    }
}
