package bryanthedragon.mclibreloaded.events;

import bryanthedragon.mclibreloaded.forge.fml.common.eventhandler.Cancelable;

import net.minecraft.client.Minecraft;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
@OnlyIn(Dist.CLIENT)
public abstract class RenderOverlayEvent extends Event
{
    public final Minecraft mc;
    public final ScaledResolution resolution;

    public RenderOverlayEvent(Minecraft mc, ScaledResolution resolution)
    {
        this.mc = mc;
        this.resolution = resolution;
    }

    public static class Pre extends RenderOverlayEvent
    {
        public Pre(Minecraft mc, ScaledResolution resolution)
        {
            super(mc, resolution);
        }
    }

    public static class Post extends RenderOverlayEvent
    {
        public Post(Minecraft mc, ScaledResolution resolution)
        {
            super(mc, resolution);
        }
    }
}