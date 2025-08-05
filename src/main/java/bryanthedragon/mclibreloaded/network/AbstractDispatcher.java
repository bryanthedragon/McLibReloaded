package bryanthedragon.mclibreloaded.network;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.network.Channel;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;

public abstract class AbstractDispatcher 
{
    protected static SimpleChannel Simple_Channel;
    private int nextPacketID = 0;
    public static final int PROTOCOL_VERSION = 1;

    static 
    {
        Simple_Channel = ChannelBuilder.named(ResourceLocation.fromNamespaceAndPath("mclibreloaded", "network")).networkProtocolVersion(PROTOCOL_VERSION).clientAcceptedVersions((status, ver) -> ver == PROTOCOL_VERSION).serverAcceptedVersions((status, ver) -> ver == PROTOCOL_VERSION).simpleChannel();
    }


    // Gets a new unique ID for each packet

    /**
     * Returns the next packet ID and increments the counter.
     * 
     * @return the next packet ID
     */
    protected int nextId() 
    {
        return nextPacketID++;
    }

    /** Implement this to register packets. */
    
    /**
     * Registers a packet with the SimpleChannel. This method should be
     * called in the constructor of the class extending AbstractDispatcher.
     *
     * @param clazz The class of the packet message.
     * @param encoder A BiConsumer that encodes the packet message.
     * @param decoder A Function that decodes the packet message.
     * @param handler A BiConsumer that handles the packet message.
     * @param direction The direction of the packet message.
     */
    @SuppressWarnings({ "deprecation", "unchecked", "rawtypes" })
    protected <MSG> void register(Class<MSG> clazz, BiConsumer<MSG, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, MSG> decoder, BiConsumer<MSG, Supplier<CustomPayloadEvent.Context>> handler, NetworkDirection direction) 
    {
        Simple_Channel.messageBuilder(clazz, nextId(), direction).encoder(encoder).decoder(decoder).consumer(handler).add();
    }

    /** Accessor for send methods */
    
    /**
     * Returns the SimpleChannel used for network communication.
     *
     * @return the SimpleChannel used for network communication
     */
    public SimpleChannel getChannel() 
    {
        return Simple_Channel;
    }


    public AbstractDispatcher(String modID) 
    {
        // Optional: Needed if packets are optional (mod doesn't have to exist on both sides)
        // SimpleChannel: returns a Channel<Object>
        AbstractDispatcher.Simple_Channel = ChannelBuilder.named(ResourceLocation.parse(modID + ":network")).networkProtocolVersion(PROTOCOL_VERSION).clientAcceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION)).serverAcceptedVersions(Channel.VersionTest.exact(PROTOCOL_VERSION)).optional().simpleChannel();
    }

    /** Register all your packets in your concrete subclass here. */

    /**
     * Send a packet to the server.
     *
     * @param message The object to serialize and send over the network.
     * @param connection The connection to send the packet over.
     */
    public void sendToServer(Object message, Connection connection) 
    {
        Simple_Channel.send(message, connection);
    }

    /**
     * Sends a packet to a single player.
     *
     * @param message The object to serialize and send over the network.
     * @param player The player to send the packet to.
     */
    public void sendTo(Object message, ServerPlayer player) 
    {
        Simple_Channel.send(message, player.connection.getConnection()); // raw Netty connection
    }

    /**
     * Sends a packet to all players that are currently tracking the entity. A player is tracking an entity if they are in the same world and have line of sight to the entity.
     *
     * @param entity The entity to check players against.
     * @param message The object to serialize and send over the network.
     */
    @SuppressWarnings("null")
    public void sendToTracked(Entity entity, Object message) 
    {
        for (ServerPlayer player : entity.level().getServer().getPlayerList().getPlayers()) 
        {
            if (player.level() == entity.level() && player.hasLineOfSight(entity)) 
            {
                sendTo(message, player);
            }
        }
    }
    // @SuppressWarnings("deprecation")
    // public static <MSG> void registerMessage(Class<MSG> messageType,BiConsumer<MSG, FriendlyByteBuf> encoder,Function<FriendlyByteBuf, MSG> decoder,BiConsumer<MSG, Context> handler) 
    // {
    //     ((SimpleChannel) channel).messageBuilder(messageType).encoder(encoder).decoder(decoder).consumer(handler).add();
    // }

    // public <P> void registerServerPacket(Class<P> packetClass, BiConsumer<P, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, P> decoder) 
    // {
    //     AbstractDispatcher.registerMessage(packetClass, encoder, decoder, (msg, ctx) -> ctx.get().handleServerMessage(msg));
    // }

    // public <P> void registerClientPacket(Class<P> packetClass, BiConsumer<P, FriendlyByteBuf> encoder, Function<FriendlyByteBuf, P> decoder)
    // {
    //     AbstractDispatcher.registerMessage(packetClass, encoder, decoder, (msg, ctx) -> ctx.get().handleClientMessage(msg));
    // }

    // @SuppressWarnings({ "unchecked", "deprecation" })
    // public static void registerMessageClientBool(Class<PacketBoolean> class1, Object encoder, Object decoder, BiConsumer<PacketBoolean, Context> handler) 
    // {
    //     ((SimpleChannel) channel).messageBuilder(class1).encoder((BiConsumer<PacketBoolean, FriendlyByteBuf>) encoder).decoder((Function<FriendlyByteBuf, PacketBoolean>) decoder).consumer(handler).add();
    // }

    // @SuppressWarnings({ "unchecked", "deprecation" })
    // public static void registerMessagePacketConfirm(Class<PacketConfirm> class1, Object encoder, Object decoder, BiConsumer<PacketConfirm, Context> handler) 
    // {
    //     ((SimpleChannel) channel).messageBuilder(class1).encoder((BiConsumer<PacketConfirm, FriendlyByteBuf>) encoder).decoder((Function<FriendlyByteBuf, PacketConfirm>) decoder).consumer(handler).add();
    // }

    // @SuppressWarnings({ "unchecked", "deprecation" })
    // public static void registerMessagePacketBool(Class<PacketBoolean> class1, Object encoder, Object decoder, BiConsumer<PacketBoolean, Context> handler) 
    // {
    //         ((SimpleChannel) channel).messageBuilder(class1).encoder((BiConsumer<PacketBoolean, FriendlyByteBuf>) encoder).decoder((Function<FriendlyByteBuf, PacketBoolean>) decoder).consumer(handler).add();
    // }
    // @SuppressWarnings({ "unchecked", "deprecation" })
    // public static void registerMessagePacketConfig(Class<PacketConfig> class1, Object encoder, Object decoder, BiConsumer<PacketConfig, Context> handler) 
    // {
    //     ((SimpleChannel) channel).messageBuilder(class1).encoder((BiConsumer<PacketConfig, FriendlyByteBuf>) encoder).decoder((Function<FriendlyByteBuf, PacketConfig>) decoder).consumer(handler).add();
    // }
    // @SuppressWarnings({ "unchecked", "deprecation" })
    // public static void registerMessageDropItem(Class<PacketDropItem> class1, Object encoder, Object decoder, BiConsumer<PacketDropItem, Context> handler) 
    // {
    //     ((SimpleChannel) channel).messageBuilder(class1).encoder((BiConsumer<PacketDropItem, FriendlyByteBuf>) encoder).decoder((Function<FriendlyByteBuf, PacketDropItem>) decoder).consumer(handler).add();
    // }
    // @SuppressWarnings({ "unchecked", "deprecation" })
    // public static void registerMessagePacketRequestConfigs(Class<PacketRequestConfigs> class1, Object encoder, Object decoder, BiConsumer<PacketRequestConfigs, Context> handler) 
    // {
    //     ((SimpleChannel) channel).messageBuilder(class1).encoder((BiConsumer<PacketRequestConfigs, FriendlyByteBuf>) encoder).decoder((Function<FriendlyByteBuf, PacketRequestConfigs>) decoder).consumer(handler).add();
    // }
    // @SuppressWarnings({ "unchecked", "deprecation" })
    // public static void registerMessagePacketRequestPermission(Class<PacketRequestPermission> class1, Object encoder, Object decoder, BiConsumer<PacketRequestPermission, Context> handler) 
    // {
    //     ((SimpleChannel) channel).messageBuilder(class1).encoder((BiConsumer<PacketRequestPermission, FriendlyByteBuf>) encoder).decoder((Function<FriendlyByteBuf, PacketRequestPermission>) decoder).consumer(handler).add();
    // }
    // @SuppressWarnings({ "unchecked", "deprecation" })
    // public static void registerMessageServerConfirm(Class<PacketConfirm> class1, Object encoder, Object decoder, BiConsumer<PacketConfirm, Context> handler) 
    // {
    //     ((SimpleChannel) channel).messageBuilder(class1).encoder((BiConsumer<PacketConfirm, FriendlyByteBuf>) encoder).decoder((Function<FriendlyByteBuf, PacketConfirm>) decoder).consumer(handler).add();
    // }

    // @SuppressWarnings({ "unchecked", "deprecation" })
    // public static void registerMessageClientConfirm(Class<PacketConfirm> class1, Object encoder, Object decoder, BiConsumer<PacketConfirm, Context> handler) 
    // {
    //     ((SimpleChannel) channel).messageBuilder(class1).encoder((BiConsumer<PacketConfirm, FriendlyByteBuf>) encoder).decoder((Function<FriendlyByteBuf, PacketConfirm>) decoder).consumer(handler).add();
    // }
    // @SuppressWarnings({ "unchecked", "deprecation" })
    // public static void registerMessageServerPacketRequest(Class<PacketRequestPermission> class1, Object encoder, Object decoder, BiConsumer<PacketRequestPermission, Context> handler) 
    // {
    //     ((SimpleChannel) channel).messageBuilder(class1).encoder((BiConsumer<PacketRequestPermission, FriendlyByteBuf>) encoder).decoder((Function<FriendlyByteBuf, PacketRequestPermission>) decoder).consumer(handler).add();
    // }

    // @SuppressWarnings({ "unchecked", "deprecation" })
    // public static void registerMessageClientAnswer(Class<PacketAnswer> class1, Object encoder, Object decoder, BiConsumer<PacketAnswer, Context> handler) 
    // {
    //     ((SimpleChannel) channel).messageBuilder(class1).encoder((BiConsumer<PacketAnswer, FriendlyByteBuf>) encoder).decoder((Function<FriendlyByteBuf, PacketAnswer>) decoder).consumer(handler).add();
    // }

    // @SuppressWarnings({ "unchecked", "deprecation" })
    // public static void registerMessageServerConfig(Class<PacketConfig> class1, Object encoder, Object decoder, BiConsumer<PacketConfig, Context> handler) 
    // {
    //     ((SimpleChannel) channel).messageBuilder(class1).encoder((BiConsumer<PacketConfig, FriendlyByteBuf>) encoder).decoder((Function<FriendlyByteBuf, PacketConfig>) decoder).consumer(handler).add();
    // }

    // @SuppressWarnings({ "unchecked", "deprecation" })
    // public static void registerMessageClientRequestConfig(Class<PacketConfig> class1, Object encoder, Object decoder, BiConsumer<PacketConfig, Context> handler) 
    // {
    //     ((SimpleChannel) channel).messageBuilder(class1).encoder((BiConsumer<PacketConfig, FriendlyByteBuf>) encoder).decoder((Function<FriendlyByteBuf, PacketConfig>) decoder).consumer(handler).add();
    // }
    // @SuppressWarnings({ "unchecked", "deprecation" })
    // public static void registerMessageServerRequestConfig(Class<PacketRequestConfigs> class1, Object encoder, Object decoder, BiConsumer<PacketRequestConfigs, Context> handler) 
    // {
    //     ((SimpleChannel) channel).messageBuilder(class1).encoder((BiConsumer<PacketRequestConfigs, FriendlyByteBuf>) encoder).decoder((Function<FriendlyByteBuf, PacketRequestConfigs>) decoder).consumer(handler).add();
    // }

    // @SuppressWarnings({ "unchecked", "deprecation" })
    // public static void registerMessagePacketDropItem(Class<PacketDropItem> class1, Object encoder, Object decoder, BiConsumer<PacketDropItem, Context> handler)
    // {
    //     ((SimpleChannel) channel).messageBuilder(class1).encoder((BiConsumer<PacketDropItem, FriendlyByteBuf>) encoder).decoder((Function<FriendlyByteBuf, PacketDropItem>) decoder).consumer(handler).add();
    // }
    // @SuppressWarnings({ "unchecked", "deprecation" })
    // public static void registerMessageClientConfig(Class<PacketConfig> class1, Object encoder, Object decoder, BiConsumer<PacketConfig, Context> handler) 
    // {
    //     ((SimpleChannel) channel).messageBuilder(class1).encoder((BiConsumer<PacketConfig, FriendlyByteBuf>) encoder).decoder((Function<FriendlyByteBuf, PacketConfig>) decoder).consumer(handler).add();
    // }
}
