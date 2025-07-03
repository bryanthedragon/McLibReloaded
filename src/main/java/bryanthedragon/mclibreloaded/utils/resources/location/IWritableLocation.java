package bryanthedragon.mclibreloaded.utils.resources.location;

import com.google.gson.JsonElement;
import bryanthedragon.mclibreloaded.utils.ICopy;
import net.minecraft.nbt.Tag;

public interface IWritableLocation<T> extends ICopy<T>
{
    public void fromNbt(Tag nbt) throws Exception;

    public void fromJson(JsonElement element) throws Exception;

    public Tag ToNbt();

    public JsonElement ToJson();
}