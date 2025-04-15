package bryanthedragon.mclibreloaded.network.mclib.common;

import com.mojang.brigadier.Message;
import io.netty.buffer.ByteBuf;
import net.minecraft.world.item.ItemStack;

public class PacketDropItem implements Message
{
    public net.minecraft.world.item.ItemStack stack = ItemStack.EMPTY;

    public PacketDropItem()
    {}

    public PacketDropItem(ItemStack stack)
    {
        this.stack = stack;
    }

    public void fromBytes(ByteBuf buf)
    {
        NBTTagCompound tagCompound = ByteBufUtils.readTag(buf);

        if (tagCompound != null)
        {
            this.stack = new ItemStack(tagCompound);
        }
    }

    public void toBytes(ByteBuf buf)
    {
        if (!this.stack.isEmpty())
        {
            ByteBufUtils.writeTag(buf, this.stack.writeToNBT(new NBTTagCompound()));
        }
    }
}