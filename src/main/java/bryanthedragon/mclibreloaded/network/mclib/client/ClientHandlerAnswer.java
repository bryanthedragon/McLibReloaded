package bryanthedragon.mclibreloaded.network.mclib.client;

import bryanthedragon.mclibreloaded.network.mclib.common.PacketAnswer;

/**
 * Handler for generic PacketAnswer which uses java's Serializable and Streams, which cat waste a lot of bytes
 */
@SuppressWarnings("rawtypes")
public class ClientHandlerAnswer extends AbstractClientHandlerAnswer<PacketAnswer>
{

}
