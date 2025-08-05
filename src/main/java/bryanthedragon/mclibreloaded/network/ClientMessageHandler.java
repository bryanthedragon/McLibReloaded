package bryanthedragon.mclibreloaded.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class passes operation from Netty to Minecraft (Client) Thread. Also
 * prevents the server-side message handling method from appearing in client
 * message handler classes.
 *
 * @author Ernio (Ernest Sadowski)
 */
public abstract class ClientMessageHandler<T extends CustomPacketPayload> extends AbstractMessageHandler<T>
{
    /**
     * Executes the client-side message handler.
     *
     * @param player The player associated with the current client session.
     * @param message The message to be processed.
     */
    @OnlyIn(Dist.CLIENT)
    public abstract void run(final LocalPlayer player, final T message);
    
    /**
     * Handle a client-side message by scheduling it to be processed on the main client thread.
     *
     * @param message The message to be processed.
     */
    @OnlyIn(Dist.CLIENT)
    public void handleClientMessage(final T message)
    {
        Minecraft.getInstance().execute(new Runnable()
        {
            public void run()
            {
                ClientMessageHandler.this.run(Minecraft.getInstance().player, message);
            }
        });
    }
}