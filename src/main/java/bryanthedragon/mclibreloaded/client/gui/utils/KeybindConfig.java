package bryanthedragon.mclibreloaded.client.gui.utils;

import java.io.File;
import java.util.Map;

import bryanthedragon.mclibreloaded.McLib;
import bryanthedragon.mclibreloaded.client.gui.utils.keys.IKey;
import bryanthedragon.mclibreloaded.config.Config;
import bryanthedragon.mclibreloaded.config.json.ConfigParser;
import bryanthedragon.mclibreloaded.config.values.Value;
import bryanthedragon.mclibreloaded.config.values.ValueInt;
import bryanthedragon.mclibreloaded.utils.Keys;
import net.minecraftforge.fml.ModContainer;


public class KeybindConfig extends Config
{
    public transient Map<String, IKey> keyMap;

    public KeybindConfig()
    {
        super("keybinds", new File(McLib.proxy.configFolder, "mclib/keybinds.json"));

        this.load();
    }

    public void addKeybind(Keybind key)
    {
        if (Keys.getMainKey(key.keyCode) == Keyboard.KEY_ESCAPE)
        {
            return;
        }

        String modid = key.modid;
        ModKeybinds mod = (ModKeybinds) this.values.get(modid);
        
        mod.addKeybind(key);
    }

    public void updateCategory(Keybind key, IKey categoryKey)
    {
        if (key.category != IKey.EMPTY)
        {
            return;
        }

        Value category = this.values.get(key.modid).getSubValue("");

        category.removeSubValue(key.labelToken);
        key.setCategory(categoryKey);
        this.addKeybind(key);
    }

    public ValueInt getKeybind(String modid, String categoryId, String id)
    {
        Value category = this.get(modid, categoryId);

        if (category != null)
        {
            return (ValueInt) category.getSubValue(id);
        }
        else
        {
            return null;
        }
    }

    public void load()
    {
        for (ModContainer container : Loader.instance().getActiveModList())
        {
            Value mod = new ModKeybinds(container);

            mod.setConfig(this);

            this.values.put(mod.id, mod);
        }
        ModKeybinds modKeybinds = new ModKeybinds(null);

        modKeybinds.setConfig(this);

        this.values.put("", modKeybinds);

        ConfigParser.fromJson(this, this.file);
    }

    @Override
    public String getCategoryTitleKey(Value value)
    {
        return value.getLabelKey();
    }

    @Override
    public String getCategoryTooltipKey(Value value)
    {
        return "";
    }

    @Override
    public String getValueLabelKey(Value value)
    {
        return "";
    }

    @Override
    public String getValueCommentKey(Value value)
    {
        return "";
    }
}
