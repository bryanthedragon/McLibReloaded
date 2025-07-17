package bryanthedragon.mclibreloaded.client.gui.utils.keys;


public class LangKey implements IKey
{
    public static long lastTime;

    public String key;
    public String string;
    public long time = -1;
    public Object[] args = new Object[0];

    public LangKey(String key)
    {
        this.key = key;
    }

    public LangKey args(Object... args)
    {
        this.args = args;

        return this;
    }

    public String update()
    {
        this.time = -1;

        return this.get();
    }

    @Override
    public String get()
    {
        if (lastTime > time)
        {
            this.time = lastTime;
            this.string = I18n.format(this.key, this.args);
        }

        return this.string;
    }

    @Override
    public void set(String string)
    {
        this.key = string;
        this.string = I18n.format(this.key);
    }

    @Override
    public boolean equals(Object obj)
    {
        if (super.equals(obj))
        {
            return true;
        }

        if (obj instanceof LangKey)
        {
            return this.get().equals(((LangKey) obj).get());
        }

        return false;
    }

    @Override
    public String toString()
    {
        return this.get();
    }
}