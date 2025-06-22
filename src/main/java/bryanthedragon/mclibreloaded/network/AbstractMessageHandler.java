package bryanthedragon.mclibreloaded.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Base of all MessageHandlers.
 *
 * @author Ernio (Ernest Sadowski)
 */
public abstract class AbstractMessageHandler<T extends IMessage> implements IMessageHandler<T, IMessage>
{
    /**
     * Handle a message received on the client side
     *
     * @return a message to send back to the Server, or null if no reply is
     *         necessary
     */
    @OnlyIn(Dist.CLIENT)
    public abstract IMessage handleClientMessage(final T message);

    /**
     * Handle a message received on the server side
     *
     * @return a message to send back to the Client, or null if no reply is
     *         necessary
     */
    public abstract IMessage handleServerMessage(final ServerPlayer player, final T message);

    @Override
    public IMessage onMessage(T message, MessageContext ctx)
    {
        if (ctx.Dist.isClient())
        {
            return this.handleClientMessage(message);
        }
        else
        {
            return this.handleServerMessage(ctx.getServerHandler().player, message);
        }
    }
}