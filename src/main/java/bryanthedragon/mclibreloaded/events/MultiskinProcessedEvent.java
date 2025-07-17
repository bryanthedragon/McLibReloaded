package bryanthedragon.mclibreloaded.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.Event;

import java.awt.image.BufferedImage;

import bryanthedragon.mclibreloaded.utils.resources.location.MultiResourceLocation;

@OnlyIn(Dist.CLIENT)
public class MultiskinProcessedEvent extends Event
{
    public MultiResourceLocation location;
    public BufferedImage image;

    public MultiskinProcessedEvent(MultiResourceLocation location, BufferedImage image)
    {
        this.location = location;
        this.image = image;
    }
}