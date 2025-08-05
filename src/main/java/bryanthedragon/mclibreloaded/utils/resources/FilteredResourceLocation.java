package bryanthedragon.mclibreloaded.utils.resources;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import bryanthedragon.mclibreloaded.utils.resources.location.IWritableLocation;
import bryanthedragon.mclibreloaded.utils.resources.location.ResourceLocations;
import bryanthedragon.mclibreloaded.utils.resources.textures.TextureLocations;
import bryanthedragon.mclibreloaded.utils.resources.textures.TextureLocationFinder;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class FilteredResourceLocation implements IWritableLocation<FilteredResourceLocation>
{
    public static final int DEFAULT_COLOR = 0xffffffff;
    public static TextureLocationFinder NBTpath;
    public static ResourceLocation Jsonpath;
    public boolean autoSize = true;
    public int sizeW;
    public int sizeH;
    public int color = DEFAULT_COLOR;
    public float scale = 1F;
    public boolean scaleToLargest;
    public int shiftX;
    public int shiftY;

    /* Filters */
    public int pixelate = 1;
    public boolean erase;

    public static FilteredResourceLocation fromNBTResourceLocation(Tag base)
    {
        try
        {
            FilteredResourceLocation location = new FilteredResourceLocation(NBTpath);
            location.tagChecker(base);
            return location;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public static FilteredResourceLocation fromObjectResourceLocation(JsonElement element)
    {
        try
        {
            FilteredResourceLocation location = new FilteredResourceLocation(Jsonpath);
            location.objectChecker(element);
            return location;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    public FilteredResourceLocation(TextureLocationFinder textureLocationFinder)
    {
        
    }

    public FilteredResourceLocation(ResourceLocation path)
    {
        FilteredResourceLocation.Jsonpath = path;
    }

    public int getWidth(int width)
    {
        if (!this.autoSize && this.sizeW > 0)
        {
            return this.sizeW;
        }
        return width;
    }

    public int getHeight(int height)
    {
        if (!this.autoSize && this.sizeH > 0)
        {
            return this.sizeH;
        }
        return height;
    }

    public String NBTPathToString()
    {
        return FilteredResourceLocation.NBTpath == null ? "" : FilteredResourceLocation.NBTpath.toString();
    }

    public String JsonPathToString()
    {
        return FilteredResourceLocation.Jsonpath == null ? "" : FilteredResourceLocation.Jsonpath.toString();
    }

    /**
     * Checks if the given object is equal to this FilteredResourceLocation
     * 
     * This is the case if the object is a FilteredResourceLocation and the
     * path, size, autoSize, scaleToLargest, color, scale, shiftX, shiftY, pixelate and erase
     * values are equal.
     * 
     * @param obj the object to compare to
     * @return true if the object is equal to this FilteredResourceLocation, false otherwise
     */
    public boolean equals(Object obj)
    {
        if (super.equals(obj))
        {
            return true;
        }
        if (obj instanceof FilteredResourceLocation)
        {
            FilteredResourceLocation frl = (FilteredResourceLocation) obj;
            return Objects.equals(FilteredResourceLocation.Jsonpath, FilteredResourceLocation.Jsonpath) && this.autoSize == frl.autoSize && this.sizeW == frl.sizeW && this.sizeH == frl.sizeH && this.scaleToLargest == frl.scaleToLargest && this.color == frl.color && this.scale == frl.scale && this.shiftX == frl.shiftX && this.shiftY == frl.shiftY && this.pixelate == frl.pixelate && this.erase == frl.erase;
        }
        return false;
    }

    /**
     * Returns a hash code for this FilteredResourceLocation, based on the JSON representation
     * of this object. This is useful for comparing FilteredResourceLocation objects in JSON
     * format.
     * 
     * @return a hash code for this FilteredResourceLocation
     */
    public int JsonHashCode()
    {
        int hashCode = FilteredResourceLocation.Jsonpath.hashCode();
        hashCode = 31 * hashCode + (this.autoSize ? 1 : 0);
        hashCode = 31 * hashCode + this.sizeW;
        hashCode = 31 * hashCode + this.sizeH;
        hashCode = 31 * hashCode + (this.scaleToLargest ? 1 : 0);
        hashCode = 31 * hashCode + this.color;
        hashCode = 31 * hashCode + (int) (this.scale * 1000);
        hashCode = 31 * hashCode + this.shiftX;
        hashCode = 31 * hashCode + this.shiftY;
        hashCode = 31 * hashCode + this.pixelate;
        hashCode = 31 * hashCode + (this.erase ? 1 : 0);
        return hashCode;
    }

    /**
     * Returns a hash code for this FilteredResourceLocation, based on the NBT representation
     * of this object. This is useful for comparing FilteredResourceLocation objects in NBT
     * format.
     * 
     * @return a hash code for this FilteredResourceLocation
     */
    public int NBTHashCode()
    {
        int hashCode = FilteredResourceLocation.NBTpath.hashCode();
        hashCode = 31 * hashCode + (this.autoSize ? 1 : 0);
        hashCode = 31 * hashCode + this.sizeW;
        hashCode = 31 * hashCode + this.sizeH;
        hashCode = 31 * hashCode + (this.scaleToLargest ? 1 : 0);
        hashCode = 31 * hashCode + this.color;
        hashCode = 31 * hashCode + (int) (this.scale * 1000);
        hashCode = 31 * hashCode + this.shiftX;
        hashCode = 31 * hashCode + this.shiftY;
        hashCode = 31 * hashCode + this.pixelate;
        hashCode = 31 * hashCode + (this.erase ? 1 : 0);
        return hashCode;
    }

    /**
     * Checks if the current FilteredResourceLocation is set to its default state.
     *
     * @return true if the FilteredResourceLocation has default settings: autoSize is enabled or both sizeW and sizeH are zero,
     *         color equals DEFAULT_COLOR, scaleToLargest is false, scale is 1.0, shiftX and shiftY are zero,
     *         pixelate is less than or equal to 1, and erase is false.
     */
    public boolean isDefault()
    {
        return (this.autoSize || (this.sizeW == 0 && this.sizeH == 0)) && this.color == DEFAULT_COLOR && !this.scaleToLargest && this.scale == 1F && this.shiftX == 0 && this.shiftY == 0 && this.pixelate <= 1 && !this.erase;
    }

    /**
     * Populates this FilteredResourceLocation from the given NBT tag.
     * The NBT tag is expected to be a StringTag or a CompoundTag.
     * If the tag is a StringTag, it is used as the path of the underlying
     * ResourceLocation. If the tag is a CompoundTag, the following keys are
     * read from the tag and used to set the corresponding fields of this
     * FilteredResourceLocation:
     * <ul>
     *     <li>"Path": the path of the underlying ResourceLocation</li>
     *     <li>"Color": the color of the FilteredResourceLocation</li>
     *     <li>"Scale": the scale of the FilteredResourceLocation</li>
     *     <li>"ScaleToLargest": whether the FilteredResourceLocation should
     *     be scaled to its largest dimension</li>
     *     <li>"ShiftX": the x-shift of the FilteredResourceLocation</li>
     *     <li>"ShiftY": the y-shift of the FilteredResourceLocation</li>
     *     <li>"Pixelate": the pixelation of the FilteredResourceLocation</li>
     *     <li>"Erase": whether the FilteredResourceLocation should be erased</li>
     *     <li>"AutoSize": whether the FilteredResourceLocation should autosize</li>
     *     <li>"SizeW": the width of the FilteredResourceLocation</li>
     *     <li>"SizeH": the height of the FilteredResourceLocation</li>
     * </ul>
     * If any of these keys are not present in the tag, the corresponding field
     * is left unchanged.
     * @param nbt the NBT tag to read from
     * @throws Exception if deserialization of the FilteredResourceLocation fails
     */
    public void tagChecker(Tag nbt) throws Exception
    {
        if (nbt instanceof StringTag)
        {
            FilteredResourceLocation.NBTpath = (TextureLocationFinder) RLUtils.createNBTTag(nbt);
            return;
        }
        CompoundTag tag = (CompoundTag) nbt;
        FilteredResourceLocation.NBTpath =  TextureLocations.fromResourceTransformer(String.valueOf(tag.getString("Path")));
        if (tag.contains("Color"))
        {
            this.color = tag.getInt("Color").orElse(DEFAULT_COLOR);
        }
        if (tag.contains("Scale"))
        {
            this.scale = tag.getFloat("Scale").orElse(1F);
        }
        if (tag.contains("ScaleToLargest"))
        {
            this.scaleToLargest = tag.getBoolean("ScaleToLargest").orElse(false);
        }
        if (tag.contains("ShiftX"))
        {
            this.shiftX = tag.getInt("ShiftX").orElse(0);
        }
        if (tag.contains("ShiftY"))
        {
            this.shiftY = tag.getInt("ShiftY").orElse(0);
        }
        if (tag.contains("Pixelate"))
        {
            this.pixelate = tag.getInt("Pixelate").orElse(1);
        }
        if (tag.contains("Erase"))
        {
            this.erase = tag.getBoolean("Erase").orElse(false);
        }
        if (tag.contains("AutoSize"))
        {
            this.autoSize = tag.getBoolean("AutoSize").orElse(true);
        }
        if (tag.contains("SizeW"))
        {
            this.sizeW = tag.getInt("SizeW").orElse(0);
        }
        if (tag.contains("SizeH"))
        {
            this.sizeH = tag.getInt("SizeH").orElse(0);
        }
    }

    public void objectChecker(JsonElement element) throws Exception
    {
        if (element.isJsonPrimitive())
        {
            FilteredResourceLocation.Jsonpath = (ResourceLocation) ResourceLocations.fromJson(element);

            return;
        }
        JsonObject object = element.getAsJsonObject();
        FilteredResourceLocation.Jsonpath = ResourceLocations.fromJsonToString(object.get("path").getAsString());
        if (object.has("color"))
        {
            this.color = object.get("color").getAsInt();
        }
        if (object.has("scale"))
        {
            this.scale = object.get("scale").getAsFloat();
        }
        if (object.has("scaleToLargest"))
        {
            this.scaleToLargest = object.get("scaleToLargest").getAsBoolean();
        }
        if (object.has("shiftX"))
        {
            this.shiftX = object.get("shiftX").getAsInt();
        }
        if (object.has("shiftY"))
        {
            this.shiftY = object.get("shiftY").getAsInt();
        }
        if (object.has("pixelate"))
        {
            this.pixelate = object.get("pixelate").getAsInt();
        }
        if (object.has("erase"))
        {
            this.erase = object.get("erase").getAsBoolean();
        }
        if (object.has("autoSize"))
        {
            this.autoSize = object.get("autoSize").getAsBoolean();
        }
        if (object.has("sizeW"))
        {
            this.sizeW = object.get("sizeW").getAsInt();
        }
        if (object.has("sizeH"))
        {
            this.sizeH = object.get("sizeH").getAsInt();
        }
    }

    public Tag ToNbt()
    {
        CompoundTag tag = new CompoundTag();
        tag.putString("Path", this.toString());
        if (this.color != DEFAULT_COLOR) 
        {
            tag.putInt("Color", this.color);
        }
        if (this.scale != 1) 
        {
            tag.putFloat("Scale", this.scale);
        }
        if (this.scaleToLargest) 
        {
            tag.putBoolean("ScaleToLargest", this.scaleToLargest);
        }
        if (this.shiftX != 0) 
        {
            tag.putInt("ShiftX", this.shiftX);
        }
        if (this.shiftY != 0) 
        {
            tag.putInt("ShiftY", this.shiftY);
        }
        if (this.pixelate > 1) 
        {
            tag.putInt("Pixelate", this.pixelate);
        }
        if (this.erase) 
        {
            tag.putBoolean("Erase", this.erase);
        }
        if (!this.autoSize) 
        {
            tag.putBoolean("AutoSize", this.autoSize);
        }
        if (this.sizeW > 0) 
        {
            tag.putInt("SizeW", this.sizeW);
        }
        if (this.sizeH > 0) 
        {
            tag.putInt("SizeH", this.sizeH);
        }
        return tag;
    }

    public JsonElement ToJson()
    {
        JsonObject object = new JsonObject();
        object.addProperty("path", this.toString());
        if (this.color != DEFAULT_COLOR) 
        {
            object.addProperty("color", this.color);
        }
        if (this.scale != 1) 
        {
            object.addProperty("scale", this.scale);
        }
        if (this.scaleToLargest) 
        {
            object.addProperty("scaleToLargest", this.scaleToLargest);
        }
        if (this.shiftX != 0) 
        {
            object.addProperty("shiftX", this.shiftX);
        }
        if (this.shiftY != 0) 
        {
            object.addProperty("shiftY", this.shiftY);
        }
        if (this.pixelate > 1) 
        {
            object.addProperty("pixelate", this.pixelate);
        }
        if (this.erase) 
        {
            object.addProperty("erase", this.erase);
        }
        if (!this.autoSize) 
        {
            object.addProperty("autoSize", this.autoSize);
        }
        if (this.sizeW > 0) 
        {
            object.addProperty("sizeW", this.sizeW);
        }
        if (this.sizeH > 0) 
        {
            object.addProperty("sizeH", this.sizeH);
        }
        return object;
    }

    public Object clone()
    {
        return RLUtils.clone(FilteredResourceLocation.NBTpath);
    }

    public FilteredResourceLocation copier()
    {
        return FilteredResourceLocation.fromNBTResourceLocation(this.ToNbt());
    }
}