package bryanthedragon.mclibreloaded.network;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.PlayerSP;
import net.minecraft.entity.player.PlayerMP;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * This class passes operation from Netty to Minecraft (Client) Thread. Also
 * prevents the server-side message handling method from appearing in client
 * message handler classes.
 *
 * @author Ernio (Ernest Sadowski)
 */
public abstract class ClientMessageHandler<T extends IMessage> extends AbstractMessageHandler<T>
{
    @OnlyIn(Dist.CLIENT)
    public abstract void run(final PlayerSP player, final T message);

    @Override
    @OnlyIn(Dist.CLIENT)
    public IMessage handleClientMessage(final T message)
    {
        Minecraft.getInstance().addScheduledTask(new Runnable()
        {
            @Override
            public void run()
            {
                ClientMessageHandler.this.run(Minecraft.getInstance().player, message);
            }
        });

        return null;
    }

    @Override
    public final IMessage handleServerMessage(final PlayerMP player, final T message)
    {
        return null;
    }
}