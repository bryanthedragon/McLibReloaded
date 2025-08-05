package bryanthedragon.mclibreloaded.network.mclib.common;

import io.netty.buffer.ByteBuf;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class PacketRequestConfigs implements CustomPacketPayload
{
    public PacketRequestConfigs()
    {

    }

    public void fromBytes(ByteBuf buf)
    {

    }

    public void toBytes(ByteBuf buf)
    {

    }

    public Type<? extends CustomPacketPayload> type() 
    {
        // only for implementation
        return null;
    }
}