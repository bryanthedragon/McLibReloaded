package bryanthedragon.mclibreloaded.network.mclib.client;

import bryanthedragon.mclibreloaded.network.mclib.common.PacketBoolean;

/**
 * A special handler just for PacketBoolean to allow for efficient transport of just 5 bytes in total.
 * Currently packets cannot be transported via inheritance to handlers, every packet requires an own handler TODO check in port
 */
public class ClientHandlerBoolean extends AbstractClientHandlerAnswer<PacketBoolean>
{

    @Override
    public void run(PlayerSP player, PacketBoolean message) 
    {
        // TODO Auto-generated method stub
        this.handleAnswer(player, message);
    }

}
