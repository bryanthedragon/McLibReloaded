package bryanthedragon.mclibreloaded.core.transformers;

import bryanthedragon.mclibreloaded.utils.PayloadASM;
import bryanthedragon.mclibreloaded.utils.coremod.ClassTransformer;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class CPacketCustomPayloadTransformer extends ClassTransformer
{
    @Override
    public void process(String name, ClassNode node)
    {
        for (MethodNode method : node.methods)
        {
            PacketBufferTransformer.replaceConstant(method, node.name, PayloadASM.MIN_SIZE);
        }
    }
}