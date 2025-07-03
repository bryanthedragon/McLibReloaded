package bryanthedragon.mclibreloaded.utils;

import io.netty.buffer.ByteBuf;

import javax.annotation.Nullable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ByteBufUtils
{
    /**
     * Writes an object to a ByteBuf by serializing it to a byte array and calling writeByteArray.
     * @param to The ByteBuf to write the object to.
     * @param object The object to write.
     * @throws IOException If serialization fails.
     */
    public static void writeObject(ByteBuf to, Object object)
    {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        try
        {
            ObjectOutputStream output = new ObjectOutputStream(bos);

            output.writeObject(object);
            output.flush();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        writeByteArray(to, bos.toByteArray());
    }

    /**
     * Reads an object from a ByteBuf by deserializing a byte array using readByteArray.
     * @param from The ByteBuf to read the object from.
     * @return The deserialized object, or null if deserialization fails.
     */
    @Nullable
    public static Object readObject(ByteBuf from)
    {
        ByteArrayInputStream bis = new ByteArrayInputStream(readByteArray(from));
        Object result = null;

        try
        {
            ObjectInputStream input = new ObjectInputStream(bis);

            result = input.readObject();
        }
        catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Reads a byte array from a ByteBuf. The array length is read from the buffer as an int, and then the specified
     * number of bytes are read from the buffer and returned as a byte array.
     * @param from The ByteBuf to read the byte array from.
     * @return The read byte array.
     */
    public static byte[] readByteArray(ByteBuf from)
    {
        int size = from.readInt();
        ByteBuf bytes = from.readBytes(size);

        byte[] array = new byte[bytes.capacity()];

        bytes.getBytes(0, array);

        return array;
    }

    /**
     * Writes a byte array to a ByteBuf. The array length is written to the buffer as an int,
     * followed by the bytes of the array itself.
     * @param to The ByteBuf to write the byte array to.
     * @param array The byte array to write.
     */
    public static void writeByteArray(ByteBuf to, byte[] array)
    {
        to.writeInt(array.length);
        to.writeBytes(array);
    }
}
