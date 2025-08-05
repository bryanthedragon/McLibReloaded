package bryanthedragon.mclibreloaded.network;

import net.minecraft.nbt.CompoundTag;

public interface INBTSerializable
{
    public void fromNBT(CompoundTag tag);

    public CompoundTag toNBT(CompoundTag tag);

    /**
     * Serializes the object to a new NBT tag and returns it.
     * This method provides a default implementation that calls
     * {@link #toNBT(CompoundTag)} with a new empty CompoundTag.
     *
     * @return a CompoundTag containing the serialized data of the object
     */
    public default CompoundTag toNBT()
    {
        return this.toNBT(new CompoundTag());
    }
}
