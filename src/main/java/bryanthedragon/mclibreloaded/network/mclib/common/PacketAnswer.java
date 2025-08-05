package bryanthedragon.mclibreloaded.network.mclib.common;

import io.netty.buffer.ByteBuf;

import bryanthedragon.mclibreloaded.utils.ByteBufUtils;

import java.io.Serializable;

public class PacketAnswer<T extends Serializable>
{
    protected int callBackID;
    protected T answer;

    public PacketAnswer()
    { 
        
    }

    public PacketAnswer(int callBackID, T answer)
    {
        this.callBackID = callBackID;
        this.answer = answer;
    }

    /**
     * Returns the callback ID of this packet.
     * 
     * @return the callback ID of this packet
     */
    public int getCallbackID()
    {
        return this.callBackID;
    }

    /**
     * Returns the value of this packet.
     * 
     * @return the value of this packet
     */
    public T getValue()
    {
        return this.answer;
    }

    /**
     * Reads the packet data from the given byte buffer.
     *
     * @param buf The buffer to read from
     */
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

    /**
     * Writes the packet data to the given byte buffer.
     *
     * @param buf The buffer to write to
     */
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.callBackID);
        ByteBufUtils.writeObject(buf, this.answer);
    }
}
