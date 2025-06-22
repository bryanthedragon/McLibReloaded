package bryanthedragon.mclibreloaded.network.mclib;

import bryanthedragon.mclibreloaded.McLib;
import bryanthedragon.mclibreloaded.network.AbstractDispatcher;
import bryanthedragon.mclibreloaded.network.mclib.client.ClientHandlerAnswer;
import bryanthedragon.mclibreloaded.network.mclib.client.ClientHandlerBoolean;
import bryanthedragon.mclibreloaded.network.mclib.client.ClientHandlerConfig;
import bryanthedragon.mclibreloaded.network.mclib.client.ClientHandlerConfirm;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketAnswer;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketBoolean;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketConfig;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketConfirm;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketDropItem;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketRequestConfigs;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketRequestPermission;
import bryanthedragon.mclibreloaded.network.mclib.server.ServerHandlerConfig;
import bryanthedragon.mclibreloaded.network.mclib.server.ServerHandlerConfirm;
import bryanthedragon.mclibreloaded.network.mclib.server.ServerHandlerDropItem;
import bryanthedragon.mclibreloaded.network.mclib.server.ServerHandlerPermissionRequest;
import bryanthedragon.mclibreloaded.network.mclib.server.ServerHandlerRequestConfigs;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;


public class Dispatcher
{
    public static final AbstractDispatcher DISPATCHER = new AbstractDispatcher(McLib.MOD_ID)
    {
        @Override
        public void register()
        {
            register(PacketDropItem.class, ServerHandlerDropItem.class, Dist.DEDICATED_SERVER);

            /* Config related packets */
            register(PacketRequestConfigs.class, ServerHandlerRequestConfigs.class, Dist.DEDICATED_SERVER);
            register(PacketConfig.class, ServerHandlerConfig.class, Dist.DEDICATED_SERVER);
            register(PacketConfig.class, ClientHandlerConfig.class, Dist.CLIENT);

            //TODO abstract confirm thing into server to client to server answer thing - see IAnswerRequest etc.
            /* Confirm related packets */
            register(PacketConfirm.class, ClientHandlerConfirm.class, Dist.CLIENT);
            register(PacketConfirm.class, ServerHandlerConfirm.class, Dist.DEDICATED_SERVER);

            /* client answer related packets */
            register(PacketAnswer.class, ClientHandlerAnswer.class, Dist.CLIENT);
            register(PacketBoolean.class, ClientHandlerBoolean.class, Dist.CLIENT);

            register(PacketRequestPermission.class, ServerHandlerPermissionRequest.class, Dist.DEDICATED_SERVER);
        }
    };

    /**
     * Send message to players who are tracking given entity
     */
    public static void sendToTracked(Entity entity, IMessage message)
    {
        DISPATCHER.sendToTracked(entity, message);
    }

    /**
     * Send message to given player
     */
    public static void sendTo(IMessage message, PlayerMP player)
    {
        DISPATCHER.sendTo(message, player);
    }

    /**
     * Send message to the server
     */
    public static void sendToServer(IMessage message)
    {
        DISPATCHER.sendToServer(message);
    }

    /**
     * Register all the networking messages and message handlers
     */
    public static void register()
    {
        DISPATCHER.register();
    }
}