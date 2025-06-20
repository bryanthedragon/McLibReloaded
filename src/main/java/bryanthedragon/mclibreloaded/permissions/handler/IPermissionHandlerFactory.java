package bryanthedragon.mclibreloaded.permissions.handler;

import net.minecraftforge.server.permission.nodes.PermissionNode;

import java.util.Collection;

@FunctionalInterface
public interface IPermissionHandlerFactory
{
    IPermissionHandler create(Collection<PermissionNode<?>> permissions);
}
