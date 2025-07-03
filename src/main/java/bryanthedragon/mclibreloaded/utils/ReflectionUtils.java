package bryanthedragon.mclibreloaded.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

public class ReflectionUtils
{
    /**
     * Minecraft texture manager's field to the texture map (a map of 
     * {@link ITextureObject} which is used to cache references to 
     * OpenGL textures). 
     */
    public static Field TEXTURE_MAP;

    /**
     * Whether isShadowPass field was checked
     */
    private static boolean SHADOW_PASS_CHECK;

    /**
     * Optifine's shadow pass field
     */
    private static Field SHADOW_PASS;

    /**
     * Get texture map from texture manager using reflection API
     * @param <ITextureObject>
     */
    @SuppressWarnings("unchecked")
    public static <ITextureObject> Map<ResourceLocation, ITextureObject> getTextures(TextureManager manager)
    {
        if (TEXTURE_MAP == null)
        {
            setupTextureMapField(manager);
        }

        try
        {
            return (Map<ResourceLocation, ITextureObject>) TEXTURE_MAP.get(manager);
        }
        catch (Exception e)
        {
            return null;
        }
    }

    /**
     * Setup texture map field which is looked up using the reflection API
     */
    @SuppressWarnings("rawtypes")
    public static void setupTextureMapField(TextureManager manager)
    {
        /* Finding the field which has holds the texture cache */
        for (Field field : manager.getClass().getDeclaredFields())
        {
            if (Modifier.isStatic(field.getModifiers()))
            {
                continue;
            }

            field.setAccessible(true);

            try
            {
                Object value = field.get(manager);

                if (value instanceof Map && ((Map) value).keySet().iterator().next() instanceof ResourceLocation)
                {
                    TEXTURE_MAP = field;

                    break;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    @SuppressWarnings("unchecked")
    @OnlyIn(Dist.CLIENT)
    public static <SimpleReloadableResourceManager, IResourcePack> boolean registerResourcePack(IResourcePack pack)
    {
        try
        {
            Field field = FMLClientHandler.class.getDeclaredField("resourcePackList");
            field.setAccessible(true);

            List<IResourcePack> packs = (List<IResourcePack>) field.get(FMLClientHandler.instance());
            packs.add(pack);
            ResourceManager manager = Minecraft.getInstance().getResourceManager();

            if (manager instanceof SimpleReloadableResourceManager)
            {
                ((SimpleReloadableResourceManager) manager).reloadResourcePack(pack);
            }

            return false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Use {@link OptifineHelper} class!
     */
    @SuppressWarnings("rawtypes")
    @Deprecated
    public static boolean isOptifineShadowPass()
    {
        if (!SHADOW_PASS_CHECK)
        {
            try
            {
                Class clazz = Class.forName("net.optifine.shaders.Shaders");

                SHADOW_PASS = clazz.getDeclaredField("isShadowPass");
            }
            catch (Exception e)
            {
                e.printStackTrace();
                SHADOW_PASS = null; // Reset the field if it fails to avoid further issues
            }

            SHADOW_PASS_CHECK = true;
        }

        if (SHADOW_PASS != null)
        {
            try
            {
                return (boolean) SHADOW_PASS.get(null);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                SHADOW_PASS = null; // Reset the field if it fails to avoid further issues
            }
        }

        return false;
    }
}