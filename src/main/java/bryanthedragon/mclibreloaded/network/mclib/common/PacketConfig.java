package bryanthedragon.mclibreloaded.network.mclib.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import bryanthedragon.mclibreloaded.config.Config;
import bryanthedragon.mclibreloaded.forge.fml.common.network.ForgeByteBufUtils;

public class PacketConfig implements CustomPacketPayload
{
    public Config config;
    public boolean overwrite;

    public PacketConfig()
    {

    }

    public PacketConfig(Config config)
    {
        this(config, false);
    }

    public PacketConfig(Config config, boolean overwrite)
    {
        this.config = config;
        this.overwrite = overwrite;
    }


    /**
     * Reads the packet from the given ByteBuf.
     *
     * @param buf The buffer to read from
     */
    public void fromBytes(ByteBuf buf)
    {
        this.config = new Config(ForgeByteBufUtils.readUTF8String(buf));
        this.config.fromBytes(buf);
        this.overwrite = buf.readBoolean();
    }


    /**
     * Writes the packet data to the given ByteBuf.
     *
     * @param buf The buffer to write to
     */
    public void toBytes(ByteBuf buf)
    {
        ForgeByteBufUtils.writeUTF8String(buf, this.config.id);
        this.config.toBytes(buf);
        buf.writeBoolean(this.overwrite);
    }

    /**
     * Returns the type of this packet.
     *
     * @return The packet type.
     */
    public Type<? extends CustomPacketPayload> type()
    {
        // only here for inheritance
        return null;
    }
}