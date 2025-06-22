package bryanthedragon.mclibreloaded.core.transformers;

import bryanthedragon.mclibreloaded.utils.PayloadASM;
import bryanthedragon.mclibreloaded.utils.coremod.ClassTransformer;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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