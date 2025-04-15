package bryanthedragon.mclibreloaded.network.mclib.server;

import bryanthedragon.mclibreloaded.McLibReloaded;
import bryanthedragon.mclibreloaded.network.ServerMessageHandler;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketDropItem;
import bryanthedragon.mclibreloaded.utils.OpHelper;
import com.mojang.brigadier.Message;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class ServerHandlerDropItem extends ServerMessageHandler<PacketDropItem>
{
    public void run(Player player, PacketDropItem message)
    {
        if (player.isCreative() && McLibReloaded.opDropItems.get() || OpHelper.isPlayerOp(player))
        {
            ItemStack stack = message.stack;

            player.inventory.addItemStackToInventory(stack);
            player.world.playSound(null, player.posX, player.posY, player.posZ, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.PLAYERS, 0.2F, 1F);
            player.inventoryContainer.detectAndSendChanges();
        }
    }

    @Override
    public Message handleServerMessage(Player player, PacketDropItem message) {
        return null;
    }
}