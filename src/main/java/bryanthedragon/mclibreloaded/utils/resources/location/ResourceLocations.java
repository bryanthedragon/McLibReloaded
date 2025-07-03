package bryanthedragon.mclibreloaded.utils.resources.location;

import java.util.Optional;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;

import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

public class ResourceLocations
{

    /**
     * Creates a MultiResourceLocation from a given NBT tag.
     * The NBT tag is expected to be a ListTag containing serialized
     * FilteredResourceLocation objects. Each valid object is deserialized
     * and added to the children list of the returned MultiResourceLocation.
     * 
     * @param base the NBT tag to deserialize from
     * @return a MultiResourceLocation object if successful, otherwise null
     */
    public static MultiResourceLocation fromNBTMulti(Tag base) 
    {
        return MultiResourceLocation.fromNBTList(base);
    }

    /**
    // From NBT Tag to ResourceLocation
    // 
    // This method attempts to convert the specified NBT tag into a
    // ResourceLocation object. If the given tag is not a StringTag or
    // the tag is empty, it returns null.
    // 
    // @param base the NBT tag to convert
    // @return a ResourceLocation object if successful, otherwise null
    */
    public static ResourceLocation fromNBTSingle(Tag base) 
    {
        if (base instanceof StringTag) 
        {
            String path = String.valueOf(((StringTag) base).asString());  // Always a String
            return fromNBTToString(path);
        }
        return null;
    }

    /**
     * From JSON Element to ResourceLocation
     * 
     * This method attempts to convert the specified JSON element into a
     * ResourceLocation object. If the given element is a JSON array, it
     * attempts to convert it into a MultiResourceLocation object. If the
     * given element is a JSON primitive, it attempts to convert it into a
     * ResourceLocation object. If the given element is neither a JSON array
     * nor a JSON primitive, it returns null.
     * 
     * @param element the JSON element to convert
     * @return a ResourceLocation object if successful, otherwise null
     */
    public static Object fromJson(JsonElement element)
    {
        MultiResourceLocation multi = MultiResourceLocation.fromJsonList(element);
        if (multi != null)
        {
            return multi;
        }

        if (element.isJsonPrimitive())
        {
            return fromJsonToString(element.getAsString());
        }

        return null;
    }

    /**
     * Converts a string path to a ResourceLocation object. If the path is invalid,
     * throws an IllegalArgumentException.
     * 
     * @param path the string path to convert
     * @return a ResourceLocation object if successful, otherwise null
     */
    public static ResourceLocation fromNBTToString(String path) 
    {
        ResourceLocation rl = ResourceLocation.tryParse(path);
        if (rl == null) 
        {
            throw new IllegalArgumentException("Invalid ResourceLocation: " + path);
        }
        return rl;
    }

    /**
     * Converts a string path from a JSON primitive to a ResourceLocation object. If the path is invalid,
     * throws an IllegalArgumentException.
     * 
     * @param path the string path to convert
     * @return a ResourceLocation object if successful, otherwise null
     */
    public static ResourceLocation fromJsonToString(String path)
    {
        ResourceLocation rl = ResourceLocation.tryParse(path);
        if (rl == null) 
        {
            throw new IllegalArgumentException("Invalid ResourceLocation: " + path);
        }
        return rl;    
}


    /**
     * Converts a domain and path into a ResourceLocation object. 
     * If the combined domain and path is invalid, throws an 
     * IllegalArgumentException.
     * 
     * @param domain the domain to use in the ResourceLocation
     * @param path the path to use in the ResourceLocation
     * @return a ResourceLocation object if successful
     * @throws IllegalArgumentException if the combined domain and path 
     *         is invalid
     */
    public static ResourceLocation fromDomainPath(String domain, String path)
    {
        String combined = domain + ":" + path;
        ResourceLocation rl = ResourceLocation.tryParse(combined);
        if (rl == null) 
        {
            throw new IllegalArgumentException("Invalid ResourceLocation: " + combined);
        }
        return rl;
    }

    // To NBT Tag from ResourceLocation
    public static Tag toNBT(ResourceLocation location)
    {
        if (location instanceof IWritableLocation writable) 
        {
            return writable.ToNbt();
        }
        else if (location != null)
        {
            return StringTag.valueOf(location.toString());
        }
        return null;
    }

    // To JsonElement from ResourceLocation
    public static JsonElement toJson(String location)
    {
        if (location instanceof IWritableLocation writable) {
            return writable.ToJson();
        }
        else if (location != null)
        {
            return new JsonPrimitive(location.toString());
        }
        return JsonNull.INSTANCE;
    }
}
