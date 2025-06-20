package bryanthedragon.mclibreloaded.network;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.SimpleChannel;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public abstract class AbstractDispatcher {
    private final SimpleChannel dispatcher;
    private int packetId = 0;

    public AbstractDispatcher(String modID) {
        this.dispatcher = NetworkRegistry.ChannelBuilder
            .named(new ResourceLocation(modID, "main"))
            .networkProtocolVersion(() -> "1")
            .clientAcceptedVersions(s -> true)
            .serverAcceptedVersions(s -> true)
            .simpleChannel();
    }

    public SimpleChannel get() {
        return dispatcher;
    }

    // Your new register method:
    public <MSG> void register(Class<MSG> messageClass, Function<MSG, ?> encoder, Function<?, MSG> decoder, BiConsumer<MSG, Supplier<NetworkEvent.Context>> handler, Dist dist) {
        dispatcher.messageBuilder(messageClass, packetId++, dist)
        .encoder(encoder)
        .decoder(decoder)
        .consumer(handler)
        .add();
    }

    public void register(Class<?> packet, Class<?> handler, Dist dist)
    {
        // Implement the registration logic that binds packet and handler to the channel,
        // handles client/server distinction using Dist,
        // and assigns incrementing packet IDs.
        dispatcher.messageBuilder(packet, packetId++, dist)
            .encoder(encoder)
            .decoder(decoder)
            .consumer(handler)
            .add();
    }

}
