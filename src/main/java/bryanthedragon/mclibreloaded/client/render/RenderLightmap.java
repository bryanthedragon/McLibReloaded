package bryanthedragon.mclibreloaded.client.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.resources.ResourceLocation;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;


/**
 * Render brightness
 * 
 * This class is a workaround class which allows using lightmap methods 
 * without having to resort to straight copy-pasting the code.
 */
@OnlyIn(Dist.CLIENT)
public class RenderLightmap extends RenderLivingBase<EntityLivingBase>
{
    /**
     * Private instance 
     */
    private static RenderLightmap instance;

    public static RenderLightmap getInstance()
    {
        if (instance == null)
        {
            instance = new RenderLightmap(Minecraft.getInstance().getEntityRenderDispatcher(), null, 0);
        }

        return instance;
    }

    public static boolean canRenderNamePlate(EntityLivingBase entity)
    {
        return getInstance().canRenderName(entity);
    }

    public static boolean set(EntityLivingBase entity, float partialTicks)
    {
        return getInstance().setBrightness(entity, partialTicks, true);
    }

    public static void unset()
    {
        getInstance().unsetBrightness();
    }

    public RenderLightmap(EntityRenderDispatcher EntityRenderDispatcherIn, ModelBase modelBaseIn, float shadowSizeIn)
    {
        super(EntityRenderDispatcherIn, modelBaseIn, shadowSizeIn);
    }

    @Override
    protected int getColorMultiplier(EntityLivingBase entitylivingbaseIn, float lightBrightness, float partialTickTime)
    {
        return 0;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityLivingBase entity)
    {
        return null;
    }
}