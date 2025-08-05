package bryanthedragon.mclibreloaded.utils;

import net.minecraftforge.fml.ModContainer;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class ModHelper
{
    public static ModContainer getCallerMod()
    {
        Class<?> caller = getCallerClass(2);
        if (caller == null) 
        {
            return null;
        }
        String jar = caller.getProtectionDomain().getCodeSource().getLocation().getPath().substring(5);
        if (jar.lastIndexOf('!') == -1) 
        {
            return null;
        }
        jar = jar.substring(0, jar.lastIndexOf('!'));
        try
        {
            jar = URLDecoder.decode(jar, StandardCharsets.UTF_8.name());
        }
        catch (UnsupportedEncodingException e)
        {
        }
        File jarFile = new File(jar);
        for (ModContainer mod : Loader.instance().getActiveModList())
        {
            if (jarFile.equals(mod.getSource()))
            {
                return mod;
            }
        }
        return null;
    }

    /**
     * Returns the {@link Class} object of the caller of this method. The offset
     * parameter allows you to get the caller of the caller, and so on.
     *
     * @param offset the offset of the caller, 0 for the caller, 1 for the caller of the caller, and so on.
     * @return the {@link Class} object of the caller, or {@code null} if the caller class could not be found.
     */
    public static Class<?> getCallerClass(int offset)
    {
        StackTraceElement element = new Exception().getStackTrace()[offset + 1];
        try
        {
            return Class.forName(element.getClassName());
        }
        catch (ClassNotFoundException e)
        {
            return null;
        }
    }
}
