package bryanthedragon.mclibreloaded.network.mclib.common;

import io.netty.buffer.ByteBuf;
import bryanthedragon.mclibreloaded.utils.ByteBufUtils;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class PacketAnswer<T extends Serializable> implements IMessage
{
    protected int callBackID;
    protected T answer;

    public PacketAnswer()
    { }

    public PacketAnswer(int callBackID, T answer)
    {
        this.callBackID = callBackID;
        this.answer = answer;
    }

    public int getCallbackID()
    {
        return this.callBackID;
    }

    public T getValue()
    {
        return this.answer;
    }

    @SuppressWarnings("unchecked")
    public void fromBytes(ByteBuf buf)
    {
        this.callBackID = buf.readInt();

        try
        {
            this.answer = (T) ByteBufUtils.readObject(buf);
        }
        catch (ClassCastException e)
        {
            e.printStackTrace();
        }
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.callBackID);
        ByteBufUtils.writeObject(buf, this.answer);
    }
}
