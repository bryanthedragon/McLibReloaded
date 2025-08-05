package bryanthedragon.mclibreloaded.network.mclib.client;

import bryanthedragon.mclibreloaded.network.mclib.common.PacketAnswer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/**
 * Handler for generic PacketAnswer which uses java's Serializable and Streams, which cat waste a lot of bytes
 * @param <T>
 */
@SuppressWarnings("rawtypes")
public class ClientHandlerAnswer extends AbstractClientHandlerAnswer<PacketAnswer>
{
    /**
     * Runs the handler for a PacketAnswer message.
     *
     * @param player The player associated with the message.
     * @param message The message to be processed.
     */
    public void run(LocalPlayer player, CustomPacketPayload message) 
    {
        // only here for inheritance
    }

    /**
     * Handles a server-side message by scheduling it to be processed on the main server thread.
     *
     * @param player The player associated with the message.
     * @param message The message to be processed.
     */
    public void handleServerMessage(ServerPlayer player, CustomPacketPayload message) 
    {

    }
}
