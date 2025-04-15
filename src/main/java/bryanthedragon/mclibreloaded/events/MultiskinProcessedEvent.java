package bryanthedragon.mclibreloaded.events;

import bryanthedragon.mclibreloaded.utils.resources.MultiResourceLocation;
import net.minecraftforge.eventbus.api.Event;
import java.awt.image.BufferedImage;


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