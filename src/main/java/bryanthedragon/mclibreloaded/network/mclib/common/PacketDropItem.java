package bryanthedragon.mclibreloaded.network.mclib.common;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class PacketDropItem implements CustomPacketPayload
{
    public ItemStack stack = ItemStack.EMPTY;

    public PacketDropItem() 
    {
        
    }

    public PacketDropItem(ItemStack stack) 
    {
        this.stack = stack;
    }

    // Deserialize packet from buffer
    @SuppressWarnings({ "unchecked", "null" })
    public void fromBytes(FriendlyByteBuf buf) 
    {
        Registry<Item> itemRegistry = (Registry<Item>) Registries.ITEM;
        Item item = itemRegistry.byId(buf.readVarInt());
        int count = buf.readVarInt();
        stack = new ItemStack(item, count);    
    }

    // Serialize packet to buffer
    @SuppressWarnings("unchecked")
    public void toBytes(FriendlyByteBuf buf) 
    {
        Registry<Item> itemRegistry = (Registry<Item>) Registries.ITEM;
        buf.writeVarInt(itemRegistry.getId(stack.getItem()));
        buf.writeVarInt(stack.getCount());
    }

    @Override
    public Type<? extends CustomPacketPayload> type() 
    {
        // only here for inheritance
        return null;
    }
    public static void encode(PacketDropItem packet, FriendlyByteBuf buffer) 
    {
        packet.toBytes(buffer);
    }
}

