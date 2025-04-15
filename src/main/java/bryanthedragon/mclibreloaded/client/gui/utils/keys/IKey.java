package bryanthedragon.mclibreloaded.client.gui.utils.keys;

import bryanthedragon.mclibreloaded.client.gui.framework.tooltips.ITooltip;

import java.io.Serializable;

public interface IKey extends Serializable
{
    public static final IKey EMPTY = new StringKey("");

    public static ITooltip lang(String key)
    {
        return new LangKey(key);
    }

    public static IKey format(String key, Object... args)
    {
        return new LangKey(key).args(args);
    }

    public static IKey str(String key)
    {
        return new StringKey(key);
    }

    public static IKey comp(IKey... keys)
    {
        return new CompoundKey(keys);
    }

    public String get();

    public void set(String string);
}