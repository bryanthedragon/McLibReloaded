package bryanthedragon.mclibreloaded.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.entity.BlockEntity;

/**
 * A class that uses reflection to access functionalities of the Optifine mod
 */
@SuppressWarnings("rawtypes")
public class OptifineHelper
{
    private static ReflectionElement<Field> shadowPass = new ReflectionElement<>();
    private static ReflectionElement<Class> shadersClass = new ReflectionElement<>();
    private static ReflectionElement<Method> nextEntity = new ReflectionElement<>();
    private static ReflectionElement<Method> nextBlockEntity = new ReflectionElement<>();

    /**
     * Checks whether Optifine is currently rendering shadow map. Thanks to
     * MiaoNLI for suggesting how to do it!
     */
    public static boolean isOptifineShadowPass()
    {
        /* only check once for isShadowPass Field to avoid too many reflection calls */
        if (!shadowPass.checked)
        {
            try
            {
                if (findShadersClass())
                {
                    shadowPass.element = shadersClass.element.getDeclaredField("isShadowPass");
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            shadowPass.checked = true;
        }
        if (shadowPass.element != null)
        {
            try
            {
                return (boolean) shadowPass.element.get(null);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * Invokes net.optifine.shaders.Shaders.nextEntity(Entity) when Optifine is present
     * @param entity
     */
    @SuppressWarnings("unchecked")
    public static void nextEntity(Entity entity)
    {
        if (!nextEntity.checked)
        {
            try
            {
                if (findShadersClass())
                {
                    nextEntity.element = shadersClass.element.getMethod("nextEntity", Entity.class);
                }
            }
            catch (Exception var1)
            {
                var1.printStackTrace();
            }
            nextEntity.checked = true;
        }
        if (nextEntity.element != null)
        {
            try
            {
                nextEntity.element.invoke(null, entity);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Invokes net.optifine.shaders.Shaders.nextBlockEntity(BlockEntity) when Optifine is present
     * @param BlockEntity
     */
    @SuppressWarnings("unchecked")
    public static void nextBlockEntity(BlockEntity BlockEntity)
    {
        if (!nextBlockEntity.checked)
        {
            try
            {
                if (findShadersClass())
                {
                    nextBlockEntity.element = shadersClass.element.getMethod("nextBlockEntity", BlockEntity.class);
                }
            }
            catch (Exception e)
            {
                nextBlockEntity.element = null;
                e.printStackTrace();
            }
            nextBlockEntity.checked = true;
        }
        if (nextBlockEntity.element != null)
        {
            try
            {
                nextBlockEntity.element.invoke(null, BlockEntity);
            }
            catch (Exception e)
            { 
                nextBlockEntity.element = null;
                e.printStackTrace();
            }
        }
    }

    /**
     * @return true if the Shaders Class was found i.e. not equal to null
     */
    private static boolean findShadersClass()
    {
        /* only check once if Optifine is there - avoid too many reflection calls*/
        if (!shadersClass.checked)
        {
            try
            {
                shadersClass.element = Class.forName("net.optifine.shaders.Shaders");
            }
            catch (Exception e)
            {
                shadersClass.element = null;
                e.printStackTrace();
            }
            shadersClass.checked = true;
        }
        return shadersClass.element != null;
    }

    /* avoid too many reflection calls by saving whether it was checked */
    private static class ReflectionElement<T>
    {
        private T element;
        private boolean checked;
    }
}
