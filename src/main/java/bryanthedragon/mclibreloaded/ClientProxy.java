package bryanthedragon.mclibreloaded;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.MinecraftForge;



import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import bryanthedragon.mclibreloaded.client.InputRenderer;
import bryanthedragon.mclibreloaded.client.KeyboardHandler;
import bryanthedragon.mclibreloaded.client.gui.utils.KeybindConfig;
import bryanthedragon.mclibreloaded.client.gui.utils.keys.LangKey;
import bryanthedragon.mclibreloaded.events.RenderingHandler;
import bryanthedragon.mclibreloaded.utils.ReflectionUtils;
import bryanthedragon.mclibreloaded.utils.resources.MultiResourceLocation;
import net.minecraftforge.api.distmarker.Dist; 

@OnlyIn(Dist.CLIENT)
public class ClientProxy extends CommonProxy
{
    public static KeybindConfig keybinds;

    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);

        MinecraftForge.EVENT_BUS.register(new KeyboardHandler());
        MinecraftForge.EVENT_BUS.register(new InputRenderer());
        MinecraftForge.EVENT_BUS.register(new RenderingHandler());

        keybinds = new KeybindConfig();
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);

        Minecraft mc = Minecraft.getInstance();

        /* OMG, thank you very much Forge! */
        if (!mc.getFramebuffer().isStencilEnabled())
        {
            mc.getFramebuffer().enableStencil();
        }

        ((IReloadableResourceManager) mc.getResourceManager()).registerReloadListener((manager) ->
        {
            LangKey.lastTime = System.currentTimeMillis();

            if (McLib.multiskinClear.get())
            {
                mc.addScheduledTask(this::clearMultiTextures);
            }
        });

        this.configs.modules.put(keybinds.id, keybinds);
    }

    private void clearMultiTextures()
    {
        Minecraft mc = Minecraft.getInstance();
        Map<ResourceLocation, ITextureObject> map = ReflectionUtils.getTextures(mc.renderEngine);
        List<ResourceLocation> toClear = new ArrayList<ResourceLocation>();

        for (ResourceLocation location : map.keySet())
        {
            if (location instanceof MultiResourceLocation)
            {
                toClear.add(location);
            }
        }

        for (ResourceLocation location : toClear)
        {
            mc.renderEngine.deleteTexture(location);
        }
    }
}