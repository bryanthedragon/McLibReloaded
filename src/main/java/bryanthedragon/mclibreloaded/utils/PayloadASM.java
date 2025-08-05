package bryanthedragon.mclibreloaded.utils;

import bryanthedragon.mclibreloaded.McLibReloaded;

public class PayloadASM
{
    public static final int MIN_SIZE = 32767;

    /**
     * ASM hook which is used in {@link CPacketCustomPayloadTransformer}
     */
    public static int getPayloadSize()
    {
        if (McLibReloaded.maxPacketSize == null)
        {
            return MIN_SIZE;
        }

        return Math.max(MIN_SIZE, McLibReloaded.maxPacketSize.get());
    }
}