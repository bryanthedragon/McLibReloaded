package bryanthedragon.mclibreloaded.network.mclib;

import bryanthedragon.mclibreloaded.McLibReloaded;
import bryanthedragon.mclibreloaded.network.AbstractDispatcher;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketConfig;
import bryanthedragon.mclibreloaded.network.mclib.client.ClientHandlerAnswer;
import bryanthedragon.mclibreloaded.network.mclib.client.ClientHandlerBoolean;
import bryanthedragon.mclibreloaded.network.mclib.client.ClientHandlerConfig;
import bryanthedragon.mclibreloaded.network.mclib.client.ClientHandlerConfirm;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketAnswer;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketBoolean;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketConfirm;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketDropItem;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketRequestConfigs;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketRequestPermission;
import bryanthedragon.mclibreloaded.network.mclib.server.ServerHandlerConfig;
import bryanthedragon.mclibreloaded.network.mclib.server.ServerHandlerConfirm;
import bryanthedragon.mclibreloaded.network.mclib.server.ServerHandlerDropItem;
import bryanthedragon.mclibreloaded.network.mclib.server.ServerHandlerPermissionRequest;
import bryanthedragon.mclibreloaded.network.mclib.server.ServerHandlerRequestConfigs;
import com.mojang.brigadier.Message;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class Dispatcher
{
    public static final AbstractDispatcher DISPATCHER = new AbstractDispatcher(McLibReloaded.MOD_ID)
    {
        @Override
        public void register()
        {
            register(PacketDropItem.class, ServerHandlerDropItem.class, Side.SERVER);

            /* Config related packets */
            register(PacketRequestConfigs.class, ServerHandlerRequestConfigs.class, Side.SERVER);
            register(PacketConfig.class, ServerHandlerConfig.class, Side.SERVER);
            register(PacketConfig.class, ClientHandlerConfig.class, Side.CLIENT);

            //TODO abstract confirm thing into server to client to server answer thing - see IAnswerRequest etc.
            /* Confirm related packets */
            register(PacketConfirm.class, ClientHandlerConfirm.class, Side.CLIENT);
            register(PacketConfirm.class, ServerHandlerConfirm.class, Side.SERVER);

            /* client answer related packets */
            register(PacketAnswer.class, ClientHandlerAnswer.class, Side.CLIENT);
            register(PacketBoolean.class, ClientHandlerBoolean.class, Side.CLIENT);

            register(PacketRequestPermission.class, ServerHandlerPermissionRequest.class, Side.SERVER);
        }
    };

    /**
     * Send message to players who are tracking given entity
     */
    public static void sendToTracked(Entity entity, Message message)
    {
        DISPATCHER.sendToTracked(entity, message);
    }

    /**
     * Send message to given player
     */
    public static void sendTo(Message message, Player player)
    {
        DISPATCHER.sendTo((Object) message, (ServerPlayer) player);
    }

    /**
     * Send message to the server
     */
    public static void sendToServer(PacketConfig message)
    {
        DISPATCHER.sendToServer((Message) message);
    }

    /**
     * Register all the networking messages and message handlers
     */
    public static void register()
    {
        DISPATCHER.register();
    }
}