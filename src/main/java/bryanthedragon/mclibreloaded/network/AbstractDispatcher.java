package bryanthedragon.mclibreloaded.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.Connection;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;

import javax.naming.Context;

import java.util.function.BiConsumer;
import java.util.function.Function;

public abstract class AbstractDispatcher {
    private static final int PROTOCOL_VERSION = 1;
    protected final Channel channel;
    private int nextPacketID = 0;

    public AbstractDispatcher(String modID) {
        this.channel = ChannelBuilder
            .named(ResourceLocation.parse(modID + ":network"))
            .networkProtocolVersion(PROTOCOL_VERSION)
            .clientAcceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION))
            .serverAcceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION))
            .optional() // Needed if packets are optional (mod doesn't have to exist on both sides)
            .simpleChannel(); // Returns a Channel<Object>
    }

    /** Register all your packets in your concrete subclass here. */
    public abstract void register();

    /** Register a single packet type. */
    public <MSG> void registerMessage(
            Class<MSG> messageType,
            BiConsumer<MSG, FriendlyByteBuf> encoder,
            Function<FriendlyByteBuf, MSG> decoder,
            BiConsumer<MSG, Context> handler
    ) {
        channel.messageBuilder(messageType)
            .encoder(encoder)
            .decoder(decoder)
            .consumer(handler)
            .add();
    }

    public void sendToServer(Object message, Connection connection) {
        channel.send(message, connection);
    }

    public void sendTo(Object message, ServerPlayer player) {
        channel.send(message, player.connection.getConnection()); // raw Netty connection
    }

    public void sendToTracked(Entity entity, Object message) {
        for (ServerPlayer player : entity.level().getServer().getPlayerList().getPlayers()) {
            if (player.level() == entity.level() && player.hasLineOfSight(entity)) {
                sendTo(message, player);
            }
        }
    }
}
