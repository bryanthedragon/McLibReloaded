package bryanthedragon.mclibreloaded.network;


public interface INBTSerializable
{
    public void fromNBT(NBTTagCompound tag);

    public NBTTagCompound toNBT(NBTTagCompound tag);

    public default NBTTagCompound toNBT()
    {
        return this.toNBT(new NBTTagCompound());
    }
}
