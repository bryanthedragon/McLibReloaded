package bryanthedragon.mclibreloaded.network.mclib.server;

import bryanthedragon.mclibreloaded.McLibReloaded;
import bryanthedragon.mclibreloaded.network.ServerMessageHandler;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketDropItem;
import bryanthedragon.mclibreloaded.utils.OpHelper;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;


public class ServerHandlerDropItem extends ServerMessageHandler<PacketDropItem>
{
    /**
     * Handles the drop item packet on the server.
     * If the player is in creative mode and {@link McLibReloaded#opDropItems} is true or the player is op,
     * the item is added to the player's inventory and a sound is played.
     *
     * @param player the player who sent the packet
     * @param message the packet containing the item to be dropped
     */
    public void run(ServerPlayer player, PacketDropItem message)
    {
        if (player.isCreative() && McLibReloaded.opDropItems.get() || OpHelper.isPlayerOp(player))
        {
            ItemStack stack = message.stack;
            player.getInventory().add(stack);
            player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, 1F);
            player.containerMenu.broadcastChanges();
        }
    }
}