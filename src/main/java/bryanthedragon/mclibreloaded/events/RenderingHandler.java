package bryanthedragon.mclibreloaded.events;

import net.minecraftforge.event.TickEvent;

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
