package bryanthedragon.mclibreloaded.network.mclib.common;

import io.netty.buffer.ByteBuf;
import bryanthedragon.mclibreloaded.McLibReloaded;
import bryanthedragon.mclibreloaded.permissions.PermissionCategory;
import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.Optional;

public class PacketRequestPermission implements IAnswerRequest<Boolean>
{
    private PermissionCategory request;
    private int callbackID = -1;

    public PacketRequestPermission()
    {

    }

    public PacketRequestPermission(int callbackID, PermissionCategory permission)
    {
        this.callbackID = callbackID;
        this.request = permission;
    }

    @Nullable
    public PermissionCategory getPermissionRequest()
    {
        return this.request;
    }

    @Override
    public void setCallbackID(int callbackID)
    {
        this.callbackID = callbackID;
    }

    @Override
    public Optional<Integer> getCallbackID()
    {
        return Optional.of(this.callbackID == -1 ? null : this.callbackID);
    }

    @Override
    public PacketBoolean getAnswer(Boolean value) throws NoSuchElementException
    {
        return new PacketBoolean(this.getCallbackID().get(), value);
    }

    public void fromBytes(ByteBuf buf)
    {
        this.callbackID = buf.readInt();
        this.request = McLibReloaded.permissionFactory.getPermission(buf.readInt());
    }

    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(this.callbackID);
        buf.writeInt(McLibReloaded.permissionFactory.getPermissionID(this.request));
    }

    @Override
    public String getString()
    {
        return "Request Permission Packet: " + (this.request != null ? this.request.getName() : "null") + ", CallbackID: " + this.callbackID;
    }
}
