package bryanthedragon.mclibreloaded.utils;

import bryanthedragon.mclibreloaded.McLib;

public class PayloadASM
{
    public static final int MIN_SIZE = 32767;

    /**
     * ASM hook which is used in {@link bryanthedragon.mclibreloaded.core.transformers.CPacketCustomPayloadTransformer}
     */
    public static int getPayloadSize()
    {
        if (McLib.maxPacketSize == null)
        {
            return MIN_SIZE;
        }

        return Math.max(MIN_SIZE, McLib.maxPacketSize.get());
    }
}