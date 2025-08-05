package bryanthedragon.mclibreloaded.network;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent;

/**
 * Base of all MessageHandlers.
 *
 * @author Ernio (Ernest Sadowski)
 */
public abstract class AbstractMessageHandler<T extends CustomPacketPayload>
{
    /**
     * Handle a message received on the client side
     *
     * @return a message to send back to the Server, or null if no reply is
     *         necessary
     */
    @OnlyIn(Dist.CLIENT)
    public abstract void handleClientMessage(final T message);

    /**
     * Handle a message received on the server side
     *
     * @return a message to send back to the Client, or null if no reply is
     *         necessary
     */
    public abstract void handleServerMessage(final ServerPlayer player, final T message);


    /**
     * Handle a message received on either the client or server side.
     *
     * @param message the received message
     * @param ctx the context of the received message
     * @throws NullPointerException if the context is null
     */
    public final void handle(T message, CustomPayloadEvent.Context ctx)
    {
        if (ctx.isClientSide()) // Packet came from server to client
        {
            handleClientMessage(message);
        }
        else // Packet came from client to server
        {
            ServerPlayer player = ctx.getSender(); // No cast needed
            if (player != null) // Defensive check
            {
                handleServerMessage(player, message);
            }
        }
        ctx.enqueueWork(() -> {}); // Ensure thread safety
        ctx.setPacketHandled(true);
    }
}