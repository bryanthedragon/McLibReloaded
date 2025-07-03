package bryanthedragon.mclibreloaded.utils.resources.textures;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import net.minecraft.resources.ResourceLocation;

/**
 * Texture location
 *
 * A hack class that allows to use uppercase characters in the path 1.11.2 and
 * up.
 */
public class TextureLocationFinder
{
    public TextureLocationFinder(String domain, String path)
    {
        super();

        this.set(domain, path);
    }

    public TextureLocationFinder(String path)
    {
        super();

        this.set(path);
    }

    /**
     * Sets the domain and path for the ResourceLocation by using reflection to
     * access and modify private fields.
     *
     * This method takes a colon-separated string argument, and splits it into
     * two parts. The first part, if it exists, is used as the domain, and the
     * second part is used as the path. If the first part does not exist, the
     * domain is set to "minecraft".
     *
     * @param location the location to set the domain and path to
     */
    public void set(String location)
    {
        String[] split = location.split(":");
        String domain = split.length > 0 ? split[0] : "minecraft";
        String path = split.length > 1 ? split[1] : "";

        this.set(domain, path);
    }

    /**
     * Sets the domain and path for the ResourceLocation by using reflection to
     * access and modify private fields.
     *
     * This method iterates through the declared fields of the ResourceLocation
     * class, unlocking them to allow modifications. It sets the first field to
     * the specified domain and the second field to the specified path. If any
     * exceptions occur during the process, they are caught and their stack traces
     * are printed.
     *
     * @param domain the domain to set
     * @param path the path to set
     */
    public void set(String domain, String path)
    {
        /* Guess what it does */
        Field[] fields = ResourceLocation.class.getDeclaredFields();

        for (Field field : fields)
        {
            try
            {
                this.unlockField(field);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        try
        {
            fields[0].set(this, domain);
            fields[1].set(this, path);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Unlocks a field for modification by removing the 'final' modifier.
     *
     * This method sets the field's accessibility to true and modifies the
     * field's modifiers to remove the FINAL modifier, allowing changes to
     * the field's value. This is useful for modifying final fields using
     * reflection.
     *
     * @param field the field to be unlocked
     * @throws Exception if an error occurs while accessing or modifying the field
     */
    protected void unlockField(Field field) throws Exception
    {
        field.setAccessible(true);

        Field modifiers = Field.class.getDeclaredField("modifiers");
        modifiers.setAccessible(true);
        modifiers.setInt(field, field.getModifiers() & ~Modifier.FINAL);
    }
}