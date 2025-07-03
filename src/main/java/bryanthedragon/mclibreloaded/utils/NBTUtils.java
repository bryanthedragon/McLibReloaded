package bryanthedragon.mclibreloaded.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.EncoderException;

import net.minecraft.nbt.CompoundTag;

import java.io.IOException;

/**
 * NBT utils 
 */
public class NBTUtils
{
    public static void readFloatList(ListTag list, float[] array)
    {
        int count = Math.min(array.length, list.tagCount());

        for (int i = 0; i < count; i++)
        {
            array[i] = list.getFloatAt(i);
        }
    }

    public static ListTag writeFloatList(ListTag list, float[] array)
    {
        for (int i = 0; i < array.length; i++)
        {
            list.appendTag(new NBTTagFloat(array[i]));
        }

        return list;
    }

    public static void readFloatList(ListTag list, Vector3f vector)
    {
        if (list.tagCount() != 3)
        {
            return;
        }

        vector.x = list.getFloatAt(0);
        vector.y = list.getFloatAt(1);
        vector.z = list.getFloatAt(2);
    }

    public static ListTag writeFloatList(ListTag list, Vector3f vector)
    {
        list.appendTag(new NBTTagFloat(vector.x));
        list.appendTag(new NBTTagFloat(vector.y));
        list.appendTag(new NBTTagFloat(vector.z));

        return list;
    }

    public static CompoundTag readInfiniteTag(ByteBuf buf)
    {
        int i = buf.readerIndex();
        byte b0 = buf.readByte();

        if (b0 == 0)
        {
            return null;
        }
        else
        {
            buf.readerIndex(i);

            try
            {
                return CompressedStreamTools.read(new ByteBufInputStream(buf), NBTSizeTracker.INFINITE);
            }
            catch (IOException ioexception)
            {
                throw new EncoderException(ioexception);
            }
        }
    }
}