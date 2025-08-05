package bryanthedragon.mclibreloaded.network.mclib.common;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import bryanthedragon.mclibreloaded.client.gui.utils.keys.IKey;
import bryanthedragon.mclibreloaded.client.gui.utils.keys.KeyParser;
import bryanthedragon.mclibreloaded.network.mclib.client.ClientHandlerConfirm;
import bryanthedragon.mclibreloaded.network.mclib.server.ServerHandlerConfirm;

import java.util.Map;
import java.util.function.Consumer;

public class PacketConfirm implements CustomPacketPayload
{

    public int consumerID;
    public ClientHandlerConfirm.GUI gui;
    public IKey langKey;
    public boolean confirm;

    @SuppressWarnings("unchecked")
    public PacketConfirm(ClientHandlerConfirm.GUI gui, IKey langKey, Consumer<Boolean> callback)
    {
        this.gui = gui;
        this.langKey = langKey;
        Map.Entry<Integer, Consumer<Boolean>> entry = ServerHandlerConfirm.getLastConsumerEntry();
        this.consumerID = (entry != null) ? entry.getKey()+1 : 0;
        ServerHandlerConfirm.addConsumer(consumerID, callback);
    }

    public PacketConfirm()
    {

    }

    /**
     * Reads the packet data from the given byte buffer.
     *
     * @param buf The buffer to read from
     */
    public void fromBytes(ByteBuf buf)
    {
        this.langKey = KeyParser.keyFromBytes(buf);
        this.gui = ClientHandlerConfirm.GUI.values()[buf.readInt()];
        this.consumerID = buf.readInt();
        this.confirm = buf.readBoolean();
    }

    /**
     * Write the packet data to the given byte buffer.
     *
     * @param buf The buffer to write to
     */
    public void toBytes(ByteBuf buf)
    {
        KeyParser.keyToBytes(buf, this.langKey);
        buf.writeInt(this.gui.ordinal());
        buf.writeInt(this.consumerID);
        buf.writeBoolean(this.confirm);
    }

    public Type<? extends CustomPacketPayload> type() 
    {
        // only here for inheritance
        return null;
    }
}