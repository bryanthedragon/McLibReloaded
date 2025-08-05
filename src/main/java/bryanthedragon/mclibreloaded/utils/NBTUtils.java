package bryanthedragon.mclibreloaded.utils;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.handler.codec.EncoderException;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

import java.io.IOException;

import org.joml.Vector3f;

/**
 * NBT utils
 */
public class NBTUtils
{
    /**
     * Reads a float array from a ListTag into the given float array. It will
     * not throw an exception if the ListTag is null or shorter than the given
     * array. If the ListTag is shorter than the given array, the remaining
     * elements of the array will be left unchanged.
     * 
     * @param list the ListTag to read from
     * @param array the array to write to
     */
    public static void readFloatList(ListTag list, float[] array)
    {
        if (list == null || array == null) 
        {
            return;
        }
        int count = Math.min(array.length, list.size());
        for (int i = 0; i < count; i++)
        {
            array[i] = list.getFloat(i).orElse(0f);
        }
    }

    /**
     * Writes a float array into a ListTag
     * @param list the ListTag to write to. If null, a new ListTag will be created.
     * @param array the array to write. If null, a single 0f tag will be added to the list.
     * @return the modified ListTag.
     */
    public static ListTag writeFloatList(ListTag list, float[] array)
    {
        if (list == null) 
        {
            list = new ListTag();
        }
        if (array == null) 
        {
            list.add(FloatTag.valueOf(0f));
        }
        return list;
    }

    /**
     * Reads a ListTag into a Vector3f
     * @param list the list tag to read from
     * @param vector the vector to write to
     *              if the list tag is null, or the vector is null
     *              or the list tag does not have exactly 3 elements
     *              this function will return and do nothing
     *              otherwise, it will set the vector's x, y, and z components
     *              to the values at indices 0, 1, and 2 of the list tag, respectively
     *              if any of the values are not present in the list, they will be ignored
     *              and the vector's corresponding components will not be modified
     */
    public static void readFloatListVector(ListTag list, Vector3f vector)
    {
        if (list == null || vector == null) 
        {
            return;
        }
        if (list.size() != 3)
        {
            return;
        }
        vector.x = list.getFloat(0).orElse(0f);
        vector.y = list.getFloat(1).orElse(0f);
        vector.z = list.getFloat(2).orElse(0f);
    }

    /**
     * Writes the x, y, and z components of the given Vector3f to the specified ListTag as FloatTags.
     * If the provided ListTag is null, a new ListTag is created.
     * If the provided Vector3f is null, the list is returned without modification.
     * 
     * @param list the ListTag to which the Vector3f components will be added
     * @param vector the Vector3f containing the float values to be written
     * @return the ListTag containing the vector components as FloatTags
     */
    public static ListTag writeFloatListVector(ListTag list, Vector3f vector)
    {
        if (list == null) 
        {
            list = new ListTag();
        }
        if (vector == null) 
        {
            return list;
        }
        list.add(FloatTag.valueOf(vector.x));
        list.add(FloatTag.valueOf(vector.y));
        list.add(FloatTag.valueOf(vector.z));
        return list;
    }


    /**
     * Reads an infinite tag from the given buf. Infinite tags are special tags that are compressed with gzip and prefixed with a byte that indicates whether they are present or not.
     * @param buf The buf to read from
     * @return The read tag, or null if no tag is present
     * @throws EncoderException if an IO exception occurs while reading the tag
     */
    public static CompoundTag readInfiniteTag(ByteBuf buf)
    {
        int i = buf.readerIndex();
        if (!buf.isReadable())
        {
            return null;
        }
        byte b0 = buf.readByte();
        if (b0 == 0)
        {
            return null;
        }
        else
        {
            buf.readerIndex(i);
            try (ByteBufInputStream in = new ByteBufInputStream(buf))
            {
                // NbtIo.readCompressed reads a gzip-compressed NBT stream and returns a CompoundTag
                return NbtIo.readCompressed(in, NbtAccounter.unlimitedHeap());
            }
            catch (IOException ioexception)
            {
                throw new EncoderException(ioexception);
            }
        }
    }
}
