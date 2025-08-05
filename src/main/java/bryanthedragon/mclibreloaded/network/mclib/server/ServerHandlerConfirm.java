package bryanthedragon.mclibreloaded.network.mclib.server;

import bryanthedragon.mclibreloaded.network.ServerMessageHandler;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketConfirm;
import net.minecraft.server.level.ServerPlayer;

import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Consumer;

public class ServerHandlerConfirm extends ServerMessageHandler<PacketConfirm>
{
    private static TreeMap<Integer, Consumer<Boolean>> consumers = new TreeMap<Integer, Consumer<Boolean>>();

    /**
     * Handles the packet received from the client. If the packet has a known consumer ID,
     * remove the consumer from the map and call the consumer's accept method with the packet's
     * confirm boolean.
     *
     * @param ServerPlayer the player who sent the packet
     * @param packetConfirm the packet received from the client
     */
    public void run(ServerPlayer ServerPlayer, PacketConfirm packetConfirm)
    {
        if (consumers.containsKey(packetConfirm.consumerID))
        {
            consumers.remove(packetConfirm.consumerID).accept(packetConfirm.confirm);
        }
    }

    /**
     * Adds a consumer to the consumers map.
     *
     * @param id the unique id for this consumer
     * @param item the consumer to add to the map
     */
    public static void addConsumer(int id, Consumer<Boolean> item)
    {
        consumers.put(id, item);
    }

    /**
     * Returns the last (highest ID) consumer entry from the consumers TreeMap.
     *
     * @return the last (highest ID) consumer entry, or null if the map is empty
     */
    @SuppressWarnings("rawtypes")
    public static Entry getLastConsumerEntry()
    {
        return consumers.lastEntry();
    }
}
