package bryanthedragon.mclibreloaded.network.mclib.common;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import io.netty.buffer.ByteBuf;

import bryanthedragon.mclibreloaded.McLibReloaded;
import bryanthedragon.mclibreloaded.permissions.PermissionCategory;

import javax.annotation.Nullable;
import java.util.NoSuchElementException;
import java.util.Optional;

public class PacketRequestPermission implements CustomPacketPayload, IAnswerRequest<Boolean>
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

    public void setCallbackID(int callbackID)
    {
        this.callbackID = callbackID;
    }

    public Optional<Integer> getCallbackID()
    {
        return Optional.of(this.callbackID == -1 ? null : this.callbackID);
    }

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
    public Type<? extends CustomPacketPayload> type() 
    {
        // only for implementation
        return null;
    }
}
