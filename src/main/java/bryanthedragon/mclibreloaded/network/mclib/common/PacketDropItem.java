package bryanthedragon.mclibreloaded.network.mclib.common;

import com.mojang.brigadier.Message;

import bryanthedragon.mclibreloaded.utils.ByteBufUtils;

import io.netty.buffer.ByteBuf;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class PacketDropItem implements Message
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
        ItemLike tagCompound = ByteBufUtils.readTag(buf);

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

    @Override
    public String getString() 
    {
        return "Drop Item Packet: " + (this.stack.isEmpty() ? "Empty" : this.stack.getItem().getDescriptionId());
    }
}