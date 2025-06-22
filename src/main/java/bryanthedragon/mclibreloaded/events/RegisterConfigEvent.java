package bryanthedragon.mclibreloaded.events;

import bryanthedragon.mclibreloaded.config.Config;
import bryanthedragon.mclibreloaded.config.ConfigBuilder;

import net.minecraftforge.eventbus.api.Event;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class RegisterConfigEvent extends Event
{
    public final File configs;
    public List<Config> modules = new ArrayList<Config>();

    public final ConfigBuilder opAccess;

    public RegisterConfigEvent(File configs)
    {
        this.configs = configs;

        this.opAccess = this.createBuilder("op_access", "mclib/op_access.json");
    }

    public ConfigBuilder createBuilder(String id)
    {
        ConfigBuilder builder = this.createBuilder(id, id + "/config.json");

        this.modules.add(builder.getConfig());

        return builder;
    }

    public ConfigBuilder createBuilder(String id, String path)
    {
        ConfigBuilder builder = new ConfigBuilder(id, new File(this.configs, path));

        this.modules.add(builder.getConfig());

        return builder;
    }
}