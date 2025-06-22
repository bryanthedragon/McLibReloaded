package bryanthedragon.mclibreloaded.config;

import bryanthedragon.mclibreloaded.config.values.Value;
import bryanthedragon.mclibreloaded.config.values.ValueBoolean;
import bryanthedragon.mclibreloaded.config.values.ValueDouble;
import bryanthedragon.mclibreloaded.config.values.ValueFloat;
import bryanthedragon.mclibreloaded.config.values.ValueInt;
import bryanthedragon.mclibreloaded.config.values.ValueRL;
import bryanthedragon.mclibreloaded.config.values.ValueString;
import net.minecraft.resources.ResourceLocation;
import java.io.File;

public class ConfigBuilder
{
    private Config config;
    private Value category;

    public ConfigBuilder(String id, File file)
    {
        this.config = new Config(id, file);
    }

    public Config getConfig()
    {
        return this.config;
    }

    public Value getCategory()
    {
        return this.category;
    }

    public ConfigBuilder category(String id)
    {
        this.config.values.put(id, this.category = new Value(id));
        this.category.setConfig(this.config);

        return this;
    }

    public ConfigBuilder register(Value value)
    {
        this.category.addSubValue(value);
        value.setConfig(this.config);

        return this;
    }

    public ValueInt getInt(String id, int defaultValue)
    {
        ValueInt value = new ValueInt(id, defaultValue);

        this.register(value);

        return value;
    }

    public ValueInt getInt(String id, int defaultValue, int min, int max)
    {
        ValueInt value = new ValueInt(id, defaultValue, min, max);

        this.register(value);

        return value;
    }

    public ValueFloat getFloat(String id, float defaultValue)
    {
        ValueFloat value = new ValueFloat(id, defaultValue);

        this.register(value);

        return value;
    }

    public ValueFloat getFloat(String id, float defaultValue, float min, float max)
    {
        ValueFloat value = new ValueFloat(id, defaultValue, min, max);

        this.register(value);

        return value;
    }

    public ValueDouble getDouble(String id, double defaultValue)
    {
        ValueDouble value = new ValueDouble(id, defaultValue);

        this.register(value);

        return value;
    }

    public ValueDouble getDouble(String id, double defaultValue, double min, double max)
    {
        ValueDouble value = new ValueDouble(id, defaultValue, min, max);

        this.register(value);

        return value;
    }

    public ValueBoolean getBoolean(String id, boolean defaultValue)
    {
        ValueBoolean value = new ValueBoolean(id, defaultValue);

        this.register(value);

        return value;
    }

    public ValueString getString(String id, String defaultValue)
    {
        ValueString value = new ValueString(id, defaultValue);

        this.register(value);

        return value;
    }

    public ValueRL getRL(String id, ResourceLocation defaultValue)
    {
        ValueRL value = new ValueRL(id, defaultValue);

        this.register(value);

        return value;
    }
}