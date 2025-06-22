package bryanthedragon.mclibreloaded.network.mclib.server;

import bryanthedragon.mclibreloaded.network.ServerMessageHandler;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketConfirm;
import net.minecraft.entity.player.PlayerMP;

import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Consumer;

public class ServerHandlerConfirm extends ServerMessageHandler<PacketConfirm>
{
    private static TreeMap<Integer, Consumer<Boolean>> consumers = new TreeMap<Integer, Consumer<Boolean>>();

    @Override
    public void run(PlayerMP PlayerMP, PacketConfirm packetConfirm)
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

    public static Entry getLastConsumerEntry()
    {
        return consumers.lastEntry();
    }
}
