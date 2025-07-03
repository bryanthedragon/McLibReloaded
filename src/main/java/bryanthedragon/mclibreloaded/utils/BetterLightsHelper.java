package bryanthedragon.mclibreloaded.utils;

import java.lang.reflect.Field;

public class BetterLightsHelper
{
    @SuppressWarnings("rawtypes")
    private static ReflectionElement<Class> betterLightsClass = new ReflectionElement<>();
    private static ReflectionElement<Field> shadowPass = new ReflectionElement<>();


    /**
     * Checks whether BetterLights is currently rendering shadow map. Thanks to
     * BetterLights mod author for suggesting how to do it!
     *
     * @return true if BetterLights is currently rendering shadow map, false otherwise
     */
    @SuppressWarnings("rawtypes")
    public static boolean isBetterLightsShadowPass()
    {
        /* only check once for isShadowPass Field to avoid too many reflection calls */
        if (!shadowPass.checked)
        {
            try
            {
                Class context = Class.forName("dz.betterlights.utils.BetterLightsContext");

                shadowPass.element = context.getDeclaredField("isBlShadowPass");
                shadowPass.element.setAccessible(true);
            }
            catch (Exception e)
            {
                shadowPass.element = null;
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
     * @return true if the BetterLights mod is loaded, false otherwise
     */
    public static boolean isBetterLightsLoaded()
    {
        return findBetterLightsClass();
    }

    /**
     * Checks if the BetterLights class is available by attempting to load
     * the "dz.betterlights.BetterLightsMod" class using reflection.
     * This method only performs the check once to minimize reflection calls.
     * 
     * @return true if the BetterLights class is successfully found, false otherwise
     */
    private static boolean findBetterLightsClass()
    {
        /* only check once if Optifine is there - avoid too many reflection calls*/
        if (!betterLightsClass.checked)
        {
            try
            {
                betterLightsClass.element = Class.forName("dz.betterlights.BetterLightsMod");
            }
            catch (Exception e)
            { 
                betterLightsClass.element = null;
                e.printStackTrace();
            }

            betterLightsClass.checked = true;
        }

        return betterLightsClass.element != null;
    }


    private static class ReflectionElement<T>
    {
        private T element;
        private boolean checked;
    }
}
