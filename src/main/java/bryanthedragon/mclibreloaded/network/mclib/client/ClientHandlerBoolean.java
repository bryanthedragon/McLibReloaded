package bryanthedragon.mclibreloaded.network.mclib.client;

import bryanthedragon.mclibreloaded.network.mclib.common.PacketBoolean;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

/**
 * A special handler just for PacketBoolean to allow for efficient transport of just 5 bytes in total.
 * Currently packets cannot be transported via inheritance to handlers, every packet requires an own handler
 */
public class ClientHandlerBoolean extends AbstractClientHandlerAnswer<PacketBoolean>
{

    /**
     * Runs the handler for a PacketBoolean message.
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
        // only here for inheritance
    }

}
