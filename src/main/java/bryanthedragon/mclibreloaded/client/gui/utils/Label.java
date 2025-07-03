package bryanthedragon.mclibreloaded.client.gui.utils;

import bryanthedragon.mclibreloaded.client.gui.utils.keys.IKey;

import java.util.Objects;

public class Label<T>
{
    public IKey title;
    public T value;

    public Label(IKey title, T value)
    {
        this.title = title;
        this.value = value;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public boolean equals(Object obj)
    {
        if (obj instanceof Label)
        {
            Label label = (Label) obj;

            return Objects.equals(this.title, label.title) && Objects.equals(this.value, label.value);
        }

        return super.equals(obj);
    }
}
