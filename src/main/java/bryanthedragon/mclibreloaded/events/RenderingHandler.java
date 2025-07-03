package bryanthedragon.mclibreloaded.events;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

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
