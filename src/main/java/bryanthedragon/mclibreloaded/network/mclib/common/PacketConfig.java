package bryanthedragon.mclibreloaded.network.mclib.common;

import bryanthedragon.mclibreloaded.config.Config;
import bryanthedragon.mclibreloaded.utils.ByteBufUtils;

import com.mojang.brigadier.Message;

import io.netty.buffer.ByteBuf;

public class PacketConfig implements Message
{
    public Config config;
    public boolean overwrite;

    public PacketConfig()
    {}

    public PacketConfig(Config config)
    {
        this(config, false);
    }

    public PacketConfig(Config config, boolean overwrite)
    {
        this.config = config;
        this.overwrite = overwrite;
    }

    public void fromBytes(ByteBuf buf)
    {
        this.config = new Config(ByteBufUtils.readUTF8String(buf));
        this.config.fromBytes(buf);
        this.overwrite = buf.readBoolean();
    }

    public void toBytes(ByteBuf buf)
    {
        ByteBufUtils.writeUTF8String(buf, this.config.id);

        this.config.toBytes(buf);
        buf.writeBoolean(this.overwrite);
    }

    @Override
    public String getString() 
    {
        return "Config: " + this.config.id + ", Overwrite: " + this.overwrite;
    }
}