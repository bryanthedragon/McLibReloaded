package bryanthedragon.mclibreloaded.network.mclib.common;

import io.netty.buffer.ByteBuf;

public class PacketBoolean extends PacketAnswer<Boolean>
{
    public PacketBoolean()
    { 
        
    }

    public PacketBoolean(int callbackID, boolean value)
    {
        super(callbackID, value);
    }

    /**
     * Reads the packet data from the given byte buffer.
     *
     * @param buf The buffer to read from
     */
    public void fromBytes(ByteBuf buf)
    {
        this.callBackID = buf.readInt();
        this.answer = buf.readBoolean();
    }

    /**
     * Writes the packet data to the given byte buffer.
     *
     * @param buf The buffer to write to
     */
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.callBackID);
        buf.writeBoolean(this.getValue());
    }
}
