package bryanthedragon.mclibreloaded.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * Passes a packet from Netty to the Minecraft server thread.
 * Prevents client-side handling in server packet classes.
 *
 * @param <T> The packet type
 */
public abstract class ServerMessageHandler<T extends CustomPacketPayload> extends AbstractMessageHandler<T>
{
    public abstract void run(final ServerPlayer player, final T message);

    /**
     * Handles a server-side message by scheduling it to be processed on the main server thread.
     *
     * @param player The player associated with the message.
     * @param message The message to be processed.
     */
    public void handleServerMessage(final ServerPlayer player, final T message)
    {
        // Schedule on the main server thread
        player.server.execute(() -> run(player, message));
    }

    /**
     * Since server packet handlers are not allowed to send packets back to the
     * client, this method is left unimplemented and will return the message
     * itself. This method is final to prevent accidental overriding.
     *
     * @param message The packet to send back to the client.
     * @return The packet itself.
     */
    public final void handleClientMessage(final T message)
    {
        // This method is intentionally left blank
        // is only here for implementation purposes
    }

    /**
     * Gets the BlockEntity at the given BlockPos for the given player, or
     * null if the chunk does not exist. This method is a convenience wrapper
     * around Level.getBlockEntity, which is deprecated in 1.17.
     *
     * @param player The player to get the BlockEntity for.
     * @param pos The BlockPos of the BlockEntity.
     * @return The BlockEntity at the given BlockPos, or null if the chunk
     *         does not exist.
     */
    @SuppressWarnings("deprecation")
    protected BlockEntity getTE(ServerPlayer player, BlockPos pos)
    {
        Level world = player.level();
        if (world.hasChunkAt(pos))
        {
            return world.getBlockEntity(pos);
        }
        return null;
    }
}
