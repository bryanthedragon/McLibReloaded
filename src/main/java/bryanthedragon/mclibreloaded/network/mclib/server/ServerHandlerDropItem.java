package bryanthedragon.mclibreloaded.network.mclib.server;

import bryanthedragon.mclibreloaded.McLib;
import bryanthedragon.mclibreloaded.network.ServerMessageHandler;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketDropItem;
import bryanthedragon.mclibreloaded.utils.OpHelper;
import net.minecraft.entity.player.PlayerMP;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;


public class ServerHandlerDropItem extends ServerMessageHandler<PacketDropItem>
{
    @Override
    public void run(PlayerMP player, PacketDropItem message)
    {
        if (player.isCreative() && McLib.opDropItems.get() || OpHelper.isPlayerOp(player))
        {
            ItemStack stack = message.stack;

            player.inventory.addItemStackToInventory(stack);
            player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, 1F);
            player.inventoryContainer.detectAndSendChanges();
        }
    }
}