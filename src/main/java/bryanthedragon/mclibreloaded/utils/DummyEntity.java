package bryanthedragon.mclibreloaded.utils;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.level.Level;
import java.util.Arrays;


/**
 * Dummy entity
 *
 * This class is used in model editor as a player substitution for the model
 * methods.
 */
public class DummyEntity extends LivingEntity
{
    private final ItemStack[] held;
    public ItemStack right;
    public ItemStack left;

    public DummyEntity(Level worldIn) {
        super(null, worldIn);

        this.right = new ItemStack(Items.DIAMOND_SWORD);
        this.left = new ItemStack(Items.GOLDEN_SWORD);
        this.held = new ItemStack[] {ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY};
    }

    public void setItems(ItemStack left, ItemStack right) {
        this.left = left;
        this.right = right;
    }

    public void toggleItems(boolean toggle) {
        int main = EquipmentSlot.MAINHAND.getIndex();
        int off = EquipmentSlot.OFFHAND.getIndex();

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

    public Iterable<ItemStack> getArmorInventoryList() {
        return Arrays.asList(this.held);
    }

    public ItemStack getItemStackFromSlot(EquipmentSlot slotIn)
    {
        return this.held[slotIn.getIndex()];
    }

    public void setItemStackToSlot(EquipmentSlot slotIn, ItemStack stack)
    {
        this.held[slotIn.getIndex()] = stack;
    }

    public HumanoidArm getPrimaryHand()
    {
        return HumanoidArm.RIGHT;
    }

    public HumanoidArm getArms() {
        return null;
    }

    @Override
    public HumanoidArm getMainArm() {
        return null;
    }
}