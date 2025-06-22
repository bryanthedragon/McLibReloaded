package bryanthedragon.mclibreloaded;

import bryanthedragon.mclibreloaded.client.gui.utils.Icons;
import bryanthedragon.mclibreloaded.config.ConfigHandler;
import bryanthedragon.mclibreloaded.config.ConfigManager;
import bryanthedragon.mclibreloaded.events.EventHandler;
import bryanthedragon.mclibreloaded.events.RegisterPermissionsEvent;
import bryanthedragon.mclibreloaded.forge.fml.common.event.FMLInitializationEvent;
import bryanthedragon.mclibreloaded.forge.fml.common.event.FMLPreInitializationEvent;
import bryanthedragon.mclibreloaded.network.mclib.Dispatcher;

import net.minecraftforge.common.MinecraftForge;

import java.io.File;

public class CommonProxy
{
    public ConfigManager configs = new ConfigManager();
    public File configFolder;

    public void preInit(FMLPreInitializationEvent event)
    {
        this.configFolder = event.getModConfigurationDirectory();

        Dispatcher.register();

        MinecraftForge.EVENT_BUS.register(new ConfigHandler());
    }

    public void init(FMLInitializationEvent event)
    {
        this.configs.register(this.configFolder);

        RegisterPermissionsEvent permissions = new RegisterPermissionsEvent();

        /* let the mods register their permissions */
        McLib.EVENT_BUS.post(permissions);

        permissions.loadPermissions();

        Icons.register();

        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }
}