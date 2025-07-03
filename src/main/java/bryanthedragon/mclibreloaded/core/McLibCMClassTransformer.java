package bryanthedragon.mclibreloaded.core;

import bryanthedragon.mclibreloaded.core.transformers.CPacketCustomPayloadTransformer;
import bryanthedragon.mclibreloaded.core.transformers.EntityRendererTransformer;
import bryanthedragon.mclibreloaded.core.transformers.PacketBufferTransformer;
import bryanthedragon.mclibreloaded.core.transformers.SimpleReloadableResourceManagerTransformer;
import bryanthedragon.mclibreloaded.utils.coremod.CoreClassTransformer;

public class McLibCMClassTransformer extends CoreClassTransformer
{
    private SimpleReloadableResourceManagerTransformer resourcePack = new SimpleReloadableResourceManagerTransformer();
    private CPacketCustomPayloadTransformer customPayload = new CPacketCustomPayloadTransformer();
    private EntityRendererTransformer entityRenderer = new EntityRendererTransformer();
    private PacketBufferTransformer packetBuffer = new PacketBufferTransformer();

    public byte[] transform(String name, String transformedName, byte[] basicClass)
    {
        if (checkName(name, "cev", "net.minecraft.client.resources.SimpleReloadableResourceManager"))
        {
            System.out.println("McLib: Transforming SimpleReloadableResourceManager class (" + name + ")");

            return this.resourcePack.transform(name, basicClass);
        }
        else if (checkName(name, "lh", "net.minecraft.network.play.client.CPacketCustomPayload"))
        {
            System.out.println("McLib: Transforming CPacketCustomPayloadTransformer class (" + name + ")");

            return this.customPayload.transform(name, basicClass);
        }
        else if (checkName(name, "buq", "net.minecraft.client.renderer.EntityRenderer"))
        {
            System.out.println("McLib: patching EntityRenderer (" + name + ")");

            return this.entityRenderer.transform(name, basicClass);
        }
        else if (checkName(name, "gy", "net.minecraft.network.PacketBuffer"))
        {
            System.out.println("McLib: Transforming PacketBuffer class (" + name + ")");

            return this.packetBuffer.transform(name, basicClass);
        }

        return basicClass;
    }
}