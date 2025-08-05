package bryanthedragon.mclibreloaded.utils;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.Arrays;
import java.util.List;

/**
 * Dummy entity
 *
 * Used in model editor as a player substitution for the model methods.
 */
public class DummyEntity extends LivingEntity 
{

    private final ItemStack[] held;
    public ItemStack right;
    public ItemStack left;

    public DummyEntity(Level level) 
    {
        super(EntityType.PLAYER, level); // Using PLAYER as placeholder; replace with your own EntityType if needed
        this.right = new ItemStack(Items.DIAMOND_SWORD);
        this.left = new ItemStack(Items.GOLDEN_SWORD);
        this.held = new ItemStack[EquipmentSlot.values().length];
        Arrays.fill(this.held, ItemStack.EMPTY);
    }

    /**
     * Set the items held by the dummy entity
     *
     * @param left  left hand item
     * @param right right hand item
     */
    public void setItems(ItemStack left, ItemStack right) 
    {
        this.left = left;
        this.right = right;
    }

    /**
     * Toggles the held items of the dummy entity
     *
     * @param toggle true to set the right and left hand items, false to clear them
     */
    public void toggleItems(boolean toggle) 
    {
        if (toggle) 
        {
            this.held[EquipmentSlot.MAINHAND.getIndex()] = this.right;
            this.held[EquipmentSlot.OFFHAND.getIndex()] = this.left;
        } 
        else 
        {
            this.held[EquipmentSlot.MAINHAND.getIndex()] = ItemStack.EMPTY;
            this.held[EquipmentSlot.OFFHAND.getIndex()] = ItemStack.EMPTY;
        }
    }

    /**
     * @return an iterable of the dummy entity's held items
     */
    public Iterable<ItemStack> getArmorInventoryList() 
    {
        return List.of(this.held);
    }

    /**
     * @param slotIn the slot to retrieve an item from
     * @return the ItemStack in the given slot
     */
    public ItemStack getItemStackFromSlot(EquipmentSlot slotIn) 
    {
        return this.held[slotIn.getIndex()];
    }

    /**
     * Sets the ItemStack in the specified equipment slot.
     *
     * @param slotIn the equipment slot to set the item in
     * @param stack the ItemStack to set in the specified slot
     */
    public void setItemStackToSlot(EquipmentSlot slotIn, ItemStack stack) 
    {
        this.held[slotIn.getIndex()] = stack;
    }

    /**
     * Returns the main arm of the dummy entity.
     *
     * @return the main arm, which is right in this case
     */
    @Override
    public HumanoidArm getMainArm() 
    {
        return HumanoidArm.RIGHT;
    }
}