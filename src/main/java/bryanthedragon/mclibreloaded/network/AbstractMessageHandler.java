package bryanthedragon.mclibreloaded.network;

import com.mojang.brigadier.Message;
import net.minecraft.world.entity.player.Player;

/**
 * Base of all MessageHandlers.
 *
 * @author Ernio (Ernest Sadowski)
 */
public abstract class AbstractMessageHandler<T extends Message> implements MessageHandler<T, Message>
{
    /**
     * Handle a message received on the client side
     *
     * @return a message to send back to the Server, or null if no reply is
     *         necessary
     */

    public abstract Message handleClientMessage(final T message);

    /**
     * Handle a message received on the server side
     *
     * @return a message to send back to the Client, or null if no reply is
     *         necessary
     */
    public abstract Message handleServerMessage(final Player player, final T message);

    @Override
    public Message onMessage(T message, MessageContext ctx)
    {
        if (ctx.side.isClient())
        {
            return this.handleClientMessage(message);
        }
        else
        {
            return this.handleServerMessage(ctx.getServerHandler().player, message);
        }
    }
}