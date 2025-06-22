package bryanthedragon.mclibreloaded.network.mclib.common;

import io.netty.buffer.ByteBuf;
import bryanthedragon.mclibreloaded.utils.ByteBufUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;


public class PacketDropItem implements IMessage
{
    public ItemStack stack = ItemStack.EMPTY;

    public PacketDropItem()
    {}

    public PacketDropItem(ItemStack stack)
    {
        this.stack = stack;
    }

    public void fromBytes(ByteBuf buf)
    {
        CompoundTag tagCompound = ByteBufUtils.readTag(buf);

        if (tagCompound != null)
        {
            this.stack = new ItemStack(tagCompound);
        }
    }

    public void toBytes(ByteBuf buf)
    {
        if (!this.stack.isEmpty())
        {
            ByteBufUtils.writeTag(buf, this.stack.writeToNBT(new CompoundTag()));
        }
    }
}