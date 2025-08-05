package bryanthedragon.mclibreloaded.network.mclib;

import bryanthedragon.mclibreloaded.McLibReloaded;
import bryanthedragon.mclibreloaded.network.AbstractDispatcher;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketAnswer;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketBoolean;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketConfig;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketConfirm;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketDropItem;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketRequestConfigs;
import bryanthedragon.mclibreloaded.network.mclib.common.PacketRequestPermission;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

public class Dispatcher 
{

    public static final AbstractDispatcher DISPATCHER = new AbstractDispatcher(McLibReloaded.MOD_ID) 
    {
        public void register() 
        {

            // Drop item packet
            register(PacketDropItem.class, PacketDropItem::encode, PacketDropItem::new, PacketDropItem::handle);

            // Config packets
            register(PacketRequestConfigs.class, PacketRequestConfigs::encode, PacketRequestConfigs::new, PacketRequestConfigs::handle);
            register(PacketConfig.class, PacketConfig::encode, PacketConfig::new, PacketConfig::handle);

            // Confirm packets
            register(PacketConfirm.class, PacketConfirm::encode, PacketConfirm::new, PacketConfirm::handle);

            // Client answer packets
            register(PacketAnswer.class, PacketAnswer::encode, PacketAnswer::new, PacketAnswer::handle);
            register(PacketBoolean.class, PacketBoolean::encode, PacketBoolean::new, PacketBoolean::handle);
            register(PacketRequestPermission.class, PacketRequestPermission::encode, PacketRequestPermission::new, PacketRequestPermission::handle);
        }
    };

    /** Send message to players tracking given entity */
    public static void sendToTracked(Entity entity, Object message) 
    {
        DISPATCHER.getChannel().send(PacketDistributor.TRACKING_ENTITY.with(entity), (Connection) message);
    }

    /** Send message to given player */
    public static void sendTo(Object message, ServerPlayer player) 
    {
        DISPATCHER.getChannel().send(PacketDistributor.PLAYER.with(player), (Connection) message);
    }

    /** Send message to the server */
    public static void sendToServer(Object message) 
    {
        Dispatcher.sendToServer(message);
    }

    /** Register all packets */
    public static void register() 
    {
        DISPATCHER.register();
    }
}
