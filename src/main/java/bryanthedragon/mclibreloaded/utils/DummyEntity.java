package bryanthedragon.mclibreloaded.utils;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.Arrays;



/**
 * Dummy entity
 *
 * This class is used in model editor as a player substitution for the model
 * methods.
 */
public class DummyEntity extends EntityLivingBase
{
    private final ItemStack[] held;
    public ItemStack right;
    public ItemStack left;

    public DummyEntity(World worldIn)
    {
        super(worldIn);

        this.right = new ItemStack(Items.DIAMOND_SWORD);
        this.left = new ItemStack(Items.GOLDEN_SWORD);
        this.held = new ItemStack[] {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
    }

    public void setItems(ItemStack left, ItemStack right)
    {
        this.left = left;
        this.right = right;
    }

    public void toggleItems(boolean toggle)
    {
        int main = EntityEquipmentSlot.MAINHAND.getSlotIndex();
        int off = EntityEquipmentSlot.OFFHAND.getSlotIndex();

        if (toggle)
        {
            this.held[main] = this.right;
            this.held[off] = this.left;
        }
        else
        {
            this.held[main] = this.held[off] = ItemStack.EMPTY;
        }
    }

    @Override
    public Iterable<ItemStack> getArmorInventoryList()
    {
        return Arrays.asList(this.held);
    }

    @Override
    public ItemStack getItemStackFromSlot(EntityEquipmentSlot slotIn)
    {
        return this.held[slotIn.getSlotIndex()];
    }

    @Override
    public void setItemStackToSlot(EntityEquipmentSlot slotIn, ItemStack stack)
    {
        this.held[slotIn.getSlotIndex()] = stack;
    }

    @Override
    public EnumHandSide getPrimaryHand()
    {
        return EnumHandDist.RIGHT;
    }
}