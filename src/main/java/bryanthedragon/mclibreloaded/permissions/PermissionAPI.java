package bryanthedragon.mclibreloaded.permissions;

import com.mojang.authlib.GameProfile;

import bryanthedragon.mclibreloaded.permissions.nodes.DefaultPermissionLevel;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import net.minecraftforge.common.ForgeConfig;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.crafting.conditions.ICondition.IContext;
import net.minecraftforge.server.ServerLifecycleHooks;
import net.minecraftforge.server.permission.events.PermissionGatherEvent;
import net.minecraftforge.server.permission.exceptions.UnregisteredPermissionException;
import net.minecraftforge.server.permission.handler.DefaultPermissionHandler;
import net.minecraftforge.server.permission.handler.IPermissionHandler;
import net.minecraftforge.server.permission.handler.IPermissionHandlerFactory;
import net.minecraftforge.server.permission.nodes.PermissionDynamicContext;
import net.minecraftforge.server.permission.nodes.PermissionNode;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Preconditions;

import org.apache.logging.log4j.Level;

public final class PermissionAPI
{
    private static final Logger LOGGER = LogManager.getLogger();
    private static IPermissionHandler activeHandler = null;

    public static Collection<PermissionNode<?>> getRegisteredNodes()
    {
        return activeHandler == null ? Collections.emptySet() : activeHandler.getRegisteredNodes();
    }

    private PermissionAPI()
    {
    }

    /**
     * @return the Identifier of the currently active permission handler
     */
    @Nullable
    public static ResourceLocation getActivePermissionHandler()
    {
        return activeHandler == null ? null : activeHandler.getIdentifier();
    }

    /**
     * <p>Queries a player's permission for a given node and contexts</p>
     * <p><strong>Warning:</strong> PermissionNodes <strong>must</strong> be registered using the
     * {@link PermissionGatherEvent.Nodes} event before querying.</p>
     *
     * @param player  player for which you want to check permissions
     * @param node    the PermissionNode for which you want to query
     * @param context optional array of PermissionDynamicContext, single entries will be ignored if they weren't
     *                registered to the node
     * @param <T>     type of the queried PermissionNode
     * @return a value of type {@code <T>}, that the combination of Player and PermissionNode map to, defaults to the
     * PermissionNodes default handler.
     * @throws UnregisteredPermissionException when the PermissionNode wasn't registered properly
     */
    public static <T> T getPermission(ServerPlayer player, PermissionNode<T> node, PermissionDynamicContext<?>... context)
    {
        if (!activeHandler.getRegisteredNodes().contains(node)) throw new UnregisteredPermissionException(node);
        return activeHandler.getPermission(player, node, context);
    }

    /**
     * See {@link PermissionAPI#getPermission(ServerPlayer, PermissionNode, PermissionDynamicContext[])}
     *
     * @param player  offline player for which you want to check permissions
     * @param node    the PermissionNode for which you want to query
     * @param context optional array of PermissionDynamicContext, single entries will be ignored if they weren't
     *                registered to the node
     * @param <T>     type of the queried PermissionNode
     * @return a value of type {@code <T>}, that the combination of Player and PermissionNode map to, defaults to the
     * PermissionNodes default handler.
     * @throws UnregisteredPermissionException when the PermissionNode wasn't registered properly
     */
    public static <T> T getOfflinePermission(UUID player, PermissionNode<T> node, PermissionDynamicContext<?>... context)
    {
        if (!activeHandler.getRegisteredNodes().contains(node)) throw new UnregisteredPermissionException(node);
        return activeHandler.getOfflinePermission(player, node, context);
    }


    /**
     * <p>Helper method for internal use only!</p>
     * <p>Initializes the active permission handler based on the users config.</p>
     */
    public static void initializePermissionAPI()
    {
        Class<?> callerClass = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE).getCallerClass();
        if (callerClass != ServerLifecycleHooks.class)
        {
            LOGGER.warn("{} tried to initialize the PermissionAPI, this call will be ignored.", callerClass.getName());
            return;
        }

        PermissionAPI.activeHandler = null;

        PermissionGatherEvent.Handler handlerEvent = new PermissionGatherEvent.Handler();
        MinecraftForge.EVENT_BUS.post(handlerEvent);
        Map<ResourceLocation, IPermissionHandlerFactory> availableHandlers = handlerEvent.getAvailablePermissionHandlerFactories();

        try
        {
            ResourceLocation selectedPermissionHandler = ResourceLocation.parse(ForgeConfig.SERVER.permissionHandler.get());
            if (!availableHandlers.containsKey(selectedPermissionHandler))
            {
                LOGGER.error("Unable to find configured permission handler {}, will use {}", selectedPermissionHandler, DefaultPermissionHandler.IDENTIFIER);
                selectedPermissionHandler = DefaultPermissionHandler.IDENTIFIER;
            }

            IPermissionHandlerFactory factory = availableHandlers.get(selectedPermissionHandler);

            PermissionGatherEvent.Nodes nodesEvent = new PermissionGatherEvent.Nodes();
            MinecraftForge.EVENT_BUS.post(nodesEvent);

            PermissionAPI.activeHandler = factory.create(nodesEvent.getNodes());

            if(!selectedPermissionHandler.equals(activeHandler.getIdentifier()))
                LOGGER.warn("Identifier for permission handler {} does not match registered one {}", activeHandler.getIdentifier(), selectedPermissionHandler);

            LOGGER.info("Successfully initialized permission handler {}", PermissionAPI.activeHandler.getIdentifier());
        }
        catch (ResourceLocationException e)
        {
            LOGGER.error("Error parsing config value 'permissionHandler'", e);
        }
    }
    public static void setPermissionHandler(IPermissionHandler handler)
    {
        Preconditions.checkNotNull(handler, "Permission handler can't be null!");
        Preconditions.checkState(Loader.instance().getLoaderState().ordinal() <= LoaderState.PREINITIALIZATION.ordinal(), "Can't register after IPermissionHandler PreInit!");
        FMLLog.log.warn("Replacing {} with {}", activeHandler.getClass().getName(), handler.getClass().getName());
        activeHandler = handler;
    }

    public static IPermissionHandler getPermissionHandler()
    {
        return activeHandler;
    }

    /**
     * <b>Only use this after PreInit state!</b>
     *
     * @param node  Permission node, best if it's lowercase and contains '.' (e.g. <code>"modid.subgroup.permission_id"</code>)
     * @param level Default permission level for this node. If not isn't registered, it's level is going to be 'NONE'
     * @param desc  Optional description of the node
     */
    public static String registerNode(String node, DefaultPermissionLevel level, String desc)
    {
        Preconditions.checkNotNull(node, "Permission node can't be null!");
        Preconditions.checkNotNull(level, "Permission level can't be null!");
        Preconditions.checkNotNull(desc, "Permission description can't be null!");
        Preconditions.checkArgument(!node.isEmpty(), "Permission node can't be empty!");
        Preconditions.checkState(Loader.instance().getLoaderState().ordinal() > LoaderState.PREINITIALIZATION.ordinal(), "Can't register permission nodes before Init!");
        permissionHandler.registerNode(node, level, desc);
        return node;
    }

    /**
     * @param profile GameProfile of the player who is requesting permission. The player doesn't have to be online
     * @param node    Permission node. See {@link #registerNode(String, DefaultPermissionLevel, String)}
     * @param context Context for this permission. Highly recommended to not be null. See {@link IContext}
     * @return true, if player has permission, false if he does not.
     * @see DefaultPermissionHandler
     */
    public static boolean hasPermission(GameProfile profile, String node, @Nullable IContext context)
    {
        Preconditions.checkNotNull(profile, "GameProfile can't be null!");
        Preconditions.checkNotNull(node, "Permission node can't be null!");
        Preconditions.checkArgument(!node.isEmpty(), "Permission node can't be empty!");
        return permissionHandler.hasPermission(profile, node, context);
    }

    /**
     * Shortcut method using EntityPlayer and creating PlayerContext
     *
     * @see PermissionAPI#hasPermission(GameProfile, String, IContext)
     */
    public static boolean hasPermission(Player player, String node)
    {
        Preconditions.checkNotNull(player, "Player can't be null!");
        return hasPermission(player.getGameProfile(), node, new PlayerContext(player));
    }
}