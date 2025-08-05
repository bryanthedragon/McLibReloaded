package bryanthedragon.mclibreloaded.utils.resources.textures;

import java.util.ArrayList;
import java.util.List;

import bryanthedragon.mclibreloaded.utils.resources.IResourceTransformer;

public class TextureLocations 
{
    private static List<IResourceTransformer> transformers = new ArrayList<IResourceTransformer>();

    /**
     * Applies all registered resource transformers to the given path and returns a 
     * {@link TextureLocationFinder} using the transformed path.
     * 
     * @param path the path to transform
     * @return a {@link TextureLocationFinder} using the transformed path
     */
    public static TextureLocationFinder fromTransformer(String path)
    {
        for (IResourceTransformer transformer : transformers)
        {
            path = transformer.transform(path);
        }
        return new TextureLocationFinder(path);
    }
    public static TextureLocationFinder fromResourceTransformer(String path)
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
    public static TextureLocationFinder fromDomainPath(String domain, String path)
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
}
