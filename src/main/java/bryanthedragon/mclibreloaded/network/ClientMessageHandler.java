package bryanthedragon.mclibreloaded.network;

import com.mojang.brigadier.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * This class passes operation from Netty to Minecraft (Client) Thread. Also
 * prevents the server-side message handling method from appearing in client
 * message handler classes.
 *
 * @author Ernio (Ernest Sadowski)
 */
public abstract class ClientMessageHandler<T extends Message> extends AbstractMessageHandler<T>
{
    public abstract void run(final LocalPlayer player, final T message);

    @Override

    public Message handleClientMessage(final T message)
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
    public final Message handleServerMessage(final Player player, final T message)
    {
        return null;
    }
}