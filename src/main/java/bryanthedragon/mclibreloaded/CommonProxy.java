package bryanthedragon.mclibreloaded;

import bryanthedragon.mclibreloaded.client.gui.utils.Icons;
import bryanthedragon.mclibreloaded.config.ConfigManager;
import bryanthedragon.mclibreloaded.events.EventHandler;
import bryanthedragon.mclibreloaded.events.RegisterPermissionsEvent;
import net.minecraftforge.common.MinecraftForge;


import java.io.File;

public class CommonProxy
{
    public ConfigManager configs = new ConfigManager();
    public File configFolder;

    // public void preInit( event)
    // {
    //     this.configFolder = event.getModConfigurationDirectory();

    //     Dispatcher.register();

    //     MinecraftForge.EVENT_BUS.register(new ConfigHandler());
    // }

    public void init()
    {
        this.configs.register(this.configFolder);

        RegisterPermissionsEvent permissions = new RegisterPermissionsEvent();

        /* let the mods register their permissions */
        McLibReloaded.EVENT_BUS.post(permissions);

        permissions.loadPermissions();

        Icons.register();

        MinecraftForge.EVENT_BUS.register(new EventHandler());
    }
}