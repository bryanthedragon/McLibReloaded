package bryanthedragon.mclibreloaded.utils.resources;

import com.google.gson.JsonElement;

import bryanthedragon.mclibreloaded.McLibReloaded;
import bryanthedragon.mclibreloaded.utils.resources.location.IWritableLocation;
import bryanthedragon.mclibreloaded.utils.resources.location.MultiResourceLocation;
import bryanthedragon.mclibreloaded.utils.resources.location.ResourceLocations;
import bryanthedragon.mclibreloaded.utils.resources.textures.TextureLocationFinder;
import bryanthedragon.mclibreloaded.utils.resources.textures.TextureLocations;

import net.minecraft.client.Minecraft;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.imageio.ImageIO;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * {@link ResourceLocation} utility methods
 *
 * This class has utils for saving and reading {@link ResourceLocation} from
 * actor model and skin.
 */
public class RLUtils
{
    private static List<IResourceTransformer> transformers = new ArrayList<IResourceTransformer>();
    private static final ResourceLocation PIXEL = ResourceLocation.fromNamespaceAndPath("mclib", "textures/pixel.png");


    /**
     * Get a stream for a multi-skin.
     *
     * This method will either return a stream for the multi-skin if
     * {@link ConfigValues#multiskinMultiThreaded} is
     * {@code false}, or request the multi-skin to be processed in the
     * multiskin thread if it is {@code true}.
     *
     * @param multi the multi-skin to get a stream for
     * @return a stream for the multi-skin, or an empty optional if
     *         {@link ConfigValues#multiskinMultiThreaded}
     *         is {@code true}
     * @throws IOException if the multi-skin is empty or if the
     *                     multiskin thread is used and the
     *                     multiskin is not successfully processed
     */
    @OnlyIn(Dist.CLIENT)
    public static Optional<Resource> getStreamForMultiskin(MultiResourceLocation multi) throws IOException
    {
        if (multi.children.isEmpty())
        {
            throw new IOException("Multi-skin is empty!");
        }

        try
        {
            if (McLibReloaded.multiskinMultiThreaded.get())
            {
                MultiskinThread.add(multi);
                return Minecraft.getInstance().getResourceManager().getResource(PIXEL);
            }
            else
            {
                MultiskinThread.clear();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                ImageIO.write(TextureProcessor.postProcess(multi), "png", stream);
                return Optional.empty();
            }
        }
        catch (IOException e)
        {
            throw e;
        }
        catch (Exception e)
        {
            throw new IOException(e);
        }
    }

    /**
     * Registers a new resource transformer by adding it to the list of transformers.
     *
     * @param transformer the resource transformer to register
     */
    public static void register(IResourceTransformer transformer)
    {
        transformers.add(transformer);
    }

    /**
     * Applies all registered resource transformers to the given path and returns a
     * {@link TextureLocationFinder} using the transformed path.
     * 
     * @param path the path to transform
     * @return a {@link TextureLocationFinder} using the transformed path
     */
    public static TextureLocationFinder createTextureTransformer(String path)
    {
        for (IResourceTransformer transformer : transformers)
        {
            path = transformer.transform(path);
        }
        return new TextureLocationFinder(path);
    }

    /**
     * Applies all registered resource transformers to the given domain and path and returns a
     * {@link TextureLocationFinder} using the transformed domain and path.
     * 
     * @param domain the domain to transform
     * @param path the path to transform
     * @return a {@link TextureLocationFinder} using the transformed domain and path
     */
    public static TextureLocationFinder createTexture(String domain, String path)
    {
        for (IResourceTransformer transformer : transformers)
        {
            String newDomain = transformer.transformDomain(domain, path);
            String newPath = transformer.transformPath(domain, path);
            domain = newDomain;
            path = newPath;
        }
        return new TextureLocationFinder(domain, path);
    }

    /**
     * Attempts to convert the specified NBT tag into a Comparable object that is
     * Comparable to ResourceLocation objects. If the given tag is a ListTag, it
     * attempts to convert it into a MultiResourceLocation object. If the given
     * tag is a StringTag, it attempts to convert it into a ResourceLocation
     * object. If the given tag is neither a ListTag nor a StringTag, it returns
     * null.
     * 
     * @param base the NBT tag to convert
     * @return a Comparable object if successful, otherwise null
     */
    @SuppressWarnings("unchecked")
    public static Comparable<ResourceLocation> createNBTTag(Tag base)
    {
        MultiResourceLocation location = MultiResourceLocation.fromNBTList(base);
        if (location != null)
        {
            return (Comparable<ResourceLocation>) location;
        }
        if (base instanceof StringTag)
        {
            return ResourceLocations.fromNBTToString(String.valueOf(((StringTag) base).asString()));
        }
        return null;
    }

    /**
     * Creates a MultiResourceLocation from a given JsonElement.
     * 
     * If the given element is a JsonArray, it attempts to convert it into a MultiResourceLocation
     * object. If the given element is a JsonPrimitive, it attempts to convert the string in the
     * JsonPrimitive into a MultiResourceLocation object. If the given element is neither a JsonArray
     * nor a JsonPrimitive, it returns null.
     * 
     * @param element the JsonElement to convert
     * @return a MultiResourceLocation object if successful, otherwise null
     */
    public static MultiResourceLocation createResource(JsonElement element)
    {
        MultiResourceLocation location = MultiResourceLocation.fromJsonList(element);
        if (location != null)
        {
            return location;
        }
        if (element.isJsonPrimitive())
        {
            return MultiResourceLocation.fromJsonList(ResourceLocations.toJson(element.getAsString()));
        }
        return null;
    }

    /**
     * Clones the given path object.
     * 
     * If the path is an instance of IWritableLocation, it attempts to create a copy
     * and returns it as a ResourceLocation if applicable. If not, it converts the path 
     * to a string and attempts to transform it using TextureLocations.
     * 
     * @param path the object to clone, can be an IWritableLocation or other object
     * @return a cloned ResourceLocation object if successful, otherwise a transformed
     *         TextureLocationFinder or null
     */
    @SuppressWarnings("rawtypes")
    public static Object clone(Object path)
    {
        if (path instanceof IWritableLocation)
        {
            Object copy = ((IWritableLocation) path).copier();

            if (copy instanceof ResourceLocation)
            {
                return (ResourceLocation) copy;
            }
        }

        if (path != null)
        {
            return TextureLocations.fromTransformer(path.toString());
        }

        return null;
    }
}