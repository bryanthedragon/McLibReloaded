package bryanthedragon.mclibreloaded.utils.resources;

import java.io.InputStream;
import java.util.Optional;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceMetadata;

public class SimpleMemoryResource
{
    private final ResourceLocation location;
    private final InputStream stream;

    public SimpleMemoryResource(ResourceLocation location, InputStream stream)
    {
        this.location = location;
        this.stream = stream;
    }

    /**
     * @return the resource location of this in-memory resource
     */
    public ResourceLocation getLocation()
    {
        return location;
    }

    /**
     * Returns an input stream to read the contents of this in-memory resource from.
     * @return the input stream
     */
    public InputStream open()
    {
        return stream;
    }

    /**
     * Provides metadata associated with this in-memory resource, if available.
     * Currently, this implementation returns an empty Optional, indicating
     * that no metadata is present.
     *
     * @return an Optional containing the resource metadata, or an empty Optional
     *         if no metadata is available
     */
    public Optional<ResourceMetadata> metadata()
    {
        return Optional.empty();
    }
}
