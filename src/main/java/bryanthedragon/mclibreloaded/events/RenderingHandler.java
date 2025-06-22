package bryanthedragon.mclibreloaded.events;

import bryanthedragon.mclibreloaded.forge.fml.common.eventhandler.SubscribeEvent;
import bryanthedragon.mclibreloaded.forge.fml.common.gameevent.TickEvent;

public class RenderingHandler
{
    /**
     * Indicates whether Minecraft is rendering
     */
    private static boolean isRendering;

    public static boolean isMinecraftRendering()
    {
        return isRendering;
    }

    @SubscribeEvent
    public void onRenderTick(TickEvent.RenderTickEvent event)
    {
        if (event.phase == TickEvent.Phase.START)
        {
            isRendering = true;
        }

        if (event.phase == TickEvent.Phase.END)
        {
            isRendering = false;
        }
    }
}
