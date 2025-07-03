package bryanthedragon.mclibreloaded;

import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;

import bryanthedragon.mclibreloaded.client.InputRenderer;
import bryanthedragon.mclibreloaded.client.KeyboardHandler;
import bryanthedragon.mclibreloaded.events.RenderingHandler;
import net.minecraftforge.api.distmarker.Dist; 

@OnlyIn(Dist.CLIENT)
public class ClientProxy 
{
    public static void register() 
    {
        MinecraftForge.EVENT_BUS.register(new KeyboardHandler());
        MinecraftForge.EVENT_BUS.register(new InputRenderer());
        MinecraftForge.EVENT_BUS.register(new RenderingHandler());
        McLib.LOGGER.info("ClientProxy: Registered client-side handlers");
    }
}