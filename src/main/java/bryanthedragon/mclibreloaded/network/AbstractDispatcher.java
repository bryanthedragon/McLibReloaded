package bryanthedragon.mclibreloaded.network;

import com.mojang.brigadier.Message;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

/**
 * Network dispatcher
 *
 * @author Ernio (Ernest Sadowski)
 */
public abstract class AbstractDispatcher
{
    private final SimpleChannel dispatcher;
    private byte nextPacketID;

    public AbstractDispatcher(String modID)
    {
        this.dispatcher = NetworkRegistry.INSTANCE.newSimpleChannel(modID);
    }

    public SimpleChannel get()
    {
        return this.dispatcher;
    }

    /**
     * Here you supposed to register packets to handlers 
     */
    public abstract void register();

    /**
     * Send message to players who are tracking given entity
     */
    public void sendToTracked(Entity entity, Message message)
    {
        EntityTracker tracker = ((WorldServer) entity.level).getEntityTracker();

        for (Player player : tracker.getTrackingPlayers(entity))
        {
            this.dispatcher.sendTo(message, player);
        }
    }

    public void sendToTracked(Entity entity, Object msg)
    {
        dispatcher.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), (PacketDistributor.PacketTarget) msg);
    }

    public void sendTo(Object msg, ServerPlayer player)
    {
        dispatcher.send(PacketDistributor.PLAYER.with(() -> player), msg);
    }

    public void sendToServer(Object msg)
    {
        dispatcher.sendToServer(msg);
    }
}