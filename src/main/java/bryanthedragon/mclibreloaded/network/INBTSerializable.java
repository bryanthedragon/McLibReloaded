package bryanthedragon.mclibreloaded.network;

import net.minecraft.nbt.CompoundTag;

public interface INBTSerializable
{
    public void fromNBT(CompoundTag tag);

    public CompoundTag toNBT(CompoundTag tag);

    public default CompoundTag toNBT()
    {
        return this.toNBT(new CompoundTag());
    }
}
