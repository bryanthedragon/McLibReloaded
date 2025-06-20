package bryanthedragon.mclibreloaded.network;

import com.mojang.brigadier.Message;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;

/**
 * This class passes operation from Netty to Minecraft (Server) Thread. This
 * class will prevent the client-side message handling method from appearing in
 * server message handler classes.
 *
 * @author Ernio (Ernest Sadowski)
 */
public abstract class ServerMessageHandler<T extends Message> extends AbstractMessageHandler<T>
{
    public abstract void run(final ServerPlayer player, final T message);

    public Message handleServerMessage(final ServerPlayer player, final T message)
    {
        player.getServer().addScheduledTask(new Runnable()
        {
            public void run()
            {
                ServerMessageHandler.this.run(player, message);
            }
        });

        return null;
    }

    public final Message handleClientMessage(final T message)
    {
        return null;
    }

    /**
     * Safe way to get a tile entity on the server without exposing code 
     * to ACG (Arbitrary Chunk Generation) exploit (thanks to Paul Fulham)
     */
    protected TileEntity getTE(ServerPlayer player, BlockPos pos)
    {
        Level world = player.level();

        if (world.isLoaded(pos))
        {
            return world.getTileEntity(pos);
        }

        return null;
    }
}