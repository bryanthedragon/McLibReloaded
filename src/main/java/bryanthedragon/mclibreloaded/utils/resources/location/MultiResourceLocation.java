package bryanthedragon.mclibreloaded.utils.resources.location;

import com.google.common.base.Objects;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import bryanthedragon.mclibreloaded.utils.resources.FilteredResourceLocation;
import bryanthedragon.mclibreloaded.utils.resources.RLUtils;
import bryanthedragon.mclibreloaded.utils.resources.textures.TextureLocations;

import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Multiple resource location class
 * 
 * This bad boy allows constructing a single texture out of several 
 * {@link ResourceLocation}s. It doesn't really make sense for other 
 * types of resources beside pictures.
 */
public class MultiResourceLocation 
{
    public List<FilteredResourceLocation> children = new ArrayList<FilteredResourceLocation>();
    private int id = -1;

    public MultiResourceLocation()
    {
        /* This needed so there would less chances to match with an
         * actual ResourceLocation */
        super();
    }
    public MultiResourceLocation(String resourceName)
    {
        this();
        this.children.add(new FilteredResourceLocation(TextureLocations.fromTransformer(resourceName)));
    }

    public MultiResourceLocation(String resourceDomainIn, String resourcePathIn)
    {
        this();
        this.children.add(new FilteredResourceLocation(RLUtils.createTextureLocation(resourceDomainIn, resourcePathIn)));
    }

    /**
     * Populates a MultiResourceLocation from the given NBT tag.
     * The NBT tag is expected to be a ListTag containing serialized
     * FilteredResourceLocation objects. Each valid object is deserialized
     * and added to the children list of this MultiResourceLocation.
     *
     * @param nbt the NBT tag to deserialize from
     * @return the MultiResourceLocation, or null if deserialization fails
     */
    public static MultiResourceLocation fromNBTList(Tag nbt)
    {
        ListTag list = nbt instanceof ListTag ? (ListTag) nbt : null;

        if (list == null || list.size() == 0)
        {
            return null;
        }

        MultiResourceLocation multi = new MultiResourceLocation();

        try
        {
            multi.fromNbt(nbt);

            return multi;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Creates a MultiResourceLocation from a given JSON element.
     * 
     * This method attempts to convert the specified JSON element into a 
     * MultiResourceLocation object by deserializing the JSON array. If the 
     * given element is not a JSON array or the array is empty, it returns null.
     * 
     * @param element the JSON element to convert
     * @return a MultiResourceLocation object if successful, otherwise null
     */
    public static MultiResourceLocation fromJsonList(JsonElement element)
    {
        JsonArray list = element.isJsonArray() ? (JsonArray) element : null;

        if (list == null || list.size() == 0)
        {
            return null;
        }

        MultiResourceLocation multi = new MultiResourceLocation();

        try
        {
            multi.fromJson(element);
            return multi;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Recalculates the unique id for this MultiResourceLocation by 
     * looking up the id in the id map maintained by the 
     * MultiResourceLocationManager.
     * 
     * This method is used to ensure that the id of this 
     * MultiResourceLocation is up to date.
     */
    public void recalculateId()
    {
        this.id = MultiResourceLocationManager.getId(this);
    }

    /**
     * Retrieves the resource domain of the first child in the list of
     * FilteredResourceLocations. If the children list is empty, an empty
     * string is returned. Otherwise, the resource domain of the first 
     * child's ResourceLocation is returned.
     *
     * @return the resource domain of the first child, or an empty string 
     *         if there are no children
     */
    @SuppressWarnings("static-access")
    public String getResourceDomain()
    {
        return this.children.isEmpty() ? "" : (this.children.get(0).Jsonpath).getNamespace();
    }

    /**
     * Retrieves the resource path of the first child in the list of
     * FilteredResourceLocations. If the children list is empty, an empty
     * string is returned. Otherwise, the resource path of the first child's
     * ResourceLocation is returned.
     *
     * @return the resource path of the first child, or an empty string if
     *         there are no children
     */
    @SuppressWarnings({ "static-access" })
    public String getResourcePath()
    {
        return this.children.isEmpty() ? "" : (this.children.get(0).Jsonpath).getPath();
    }

    @SuppressWarnings("static-access")
    public ResourceLocation toResourceLocation() 
    {
        if (this.children.isEmpty()) 
        {
            // build a safe synthetic key
            return ResourceLocation.fromNamespaceAndPath("multiskin", "empty_" + Integer.toUnsignedString(this.hashCode()));
        }
        // your FilteredResourceLocation already holds a ResourceLocation
        return this.children.get(0).Jsonpath;
    }

    /**
     * This is mostly for looks, but it doesn't really makes sense by  
     * itself
     */
    public String toString()
    {
        return this.getResourceDomain() + ":" + this.getResourcePath();
    }

    /**
     * Checks if the provided object is equal to this MultiResourceLocation.
     * This is the case if the object is a MultiResourceLocation and the
     * children lists are equal.
     */
    public boolean equals(Object obj)
    {
        if (obj instanceof MultiResourceLocation)
        {
            MultiResourceLocation multi = (MultiResourceLocation) obj;
            return Objects.equal(this.children, multi.children);
        }
        return super.equals(obj);
    }

    /**
     * Return the id of this MultiResourceLocation. The id is a unique
     * identifier for each MultiResourceLocation, and is used for
     * comparing MultiResourceLocations. If the id is negative, it
     * is recalculated using {@link #recalculateId()}.
     */
    public int hashCode()
    {
        if (this.id < 0)
        {
            this.recalculateId();
        }
        return this.id;
    }

    /**
     * Populates this MultiResourceLocation from the given NBT tag.
     * The NBT tag is expected to be a ListTag containing serialized
     * FilteredResourceLocation objects. Each valid object is deserialized
     * and added to the children list of this MultiResourceLocation.
     *
     * @param nbt the NBT tag to deserialize from
     * @throws Exception if deserialization of any FilteredResourceLocation fails
     */
    public void fromNbt(Tag nbt) throws Exception
    {
        ListTag list = (ListTag) nbt;

        for (int i = 0; i < list.size(); i++)
        {
            FilteredResourceLocation location = FilteredResourceLocation.fromNBTResourceLocation(list.get(i));
            if (location != null)
            {
                this.children.add(location);
            }
        }
    }

    /**
     * Deserializes a JSON array into this object
     * @param element the JSON element to deserialize
     * @throws Exception if the element is not a JSON array
     */
    public void fromJsonArray(JsonElement element) throws Exception
    {
        JsonArray array = (JsonArray) element;

        for (int i = 0; i < array.size(); i++)
        {
            FilteredResourceLocation location = FilteredResourceLocation.fromObjectResourceLocation(array.get(i));
            if (location != null)
            {
                this.children.add(location);
            }
        }
    }


    /**
     * Serializes the children of this MultiResourceLocation into a ListTag.
     * Each child is serialized using {@link FilteredResourceLocation#writeNbt()},
     * and the resulting tags are added to the ListTag in the order they appear
     * in the children list.
     * @return the ListTag containing the serialized children
     */
    public Tag toNbt()
    {
        ListTag list = new ListTag();
        for (FilteredResourceLocation child : this.children)
        {
            Tag tag = child.ToNbt();
            if (tag != null)
            {
                list.add(tag);
            }
        }
        return list;
    }


    /**
     * Serializes the children of this MultiResourceLocation into a JsonArray.
     * Each child is serialized using {@link FilteredResourceLocation#writeJson()},
     * and the resulting elements are added to the JsonArray in the order they appear
     * in the children list.
     * @return the JsonArray containing the serialized children
     */
    public JsonElement toJson()
    {
        JsonArray array = new JsonArray();

        for (FilteredResourceLocation child : this.children)
        {
            JsonElement element = child.ToJson();
            if (element != null)
            {
                array.add(element);
            }
        }
        return array;
    }

    /**
     * Creates a copy of this MultiResourceLocation, with all its children
     * copied using their {@link FilteredResourceLocation#copy()} method.
     * @return a new MultiResourceLocation with the same children as this
     *         instance
     */
    public MultiResourceLocation copy()
    {
        MultiResourceLocation newMulti = new MultiResourceLocation();
        for (FilteredResourceLocation child : this.children)
        {
            newMulti.children.add(child.copier());
        }
        return newMulti;
    }


    /**
     * {@inheritDoc}
     * 
     * @throws Exception
     *             Should never happen
     */
    public  void fromJson(JsonElement element) throws Exception 
    {
        // required due to implementation
    }


    /**
     * @return null; this is only here to satisfy the interface for the default
     *         implementation of {@link IWritableLocation}
     */
    public Tag ToNbt() 
    {
        // required due to implementation
        return null;
    }


    /**
     * @return null; this is only here to satisfy the interface for the default
     *         implementation of {@link IWritableLocation}
     */
    public JsonElement ToJson() 
    {
        // required due to implementation
        return null;
    }
}