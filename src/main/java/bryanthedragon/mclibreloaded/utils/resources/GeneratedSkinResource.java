package bryanthedragon.mclibreloaded.utils.resources;

import java.io.InputStream;

import net.minecraft.resources.ResourceLocation;

public class GeneratedSkinResource 
{
    private final ResourceLocation location;
    private final InputStream stream;

    public GeneratedSkinResource(ResourceLocation location, InputStream stream) 
    {
        this.location = location;
        this.stream = stream;
    }

    /**
     * Returns the resource location of this generated skin resource.
     * @return the resource location
     */
    public ResourceLocation getLocation() 
    {
        return location;
    }

    /**
     * Returns the input stream from which the generated skin resource can be read.
     * @return the input stream
     */
    public InputStream getStream() 
    {
        return stream;
    }
}
