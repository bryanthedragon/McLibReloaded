package bryanthedragon.mclibreloaded.client.gui.framework.elements;

import bryanthedragon.mclibreloaded.client.gui.framework.GuiBase;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.modals.GuiConfirmModal;
import bryanthedragon.mclibreloaded.client.gui.utils.keys.IKey;

import net.minecraft.client.Minecraft;

import java.util.function.Consumer;

public class GuiConfirmationScreen extends GuiBase
{
    private Consumer<Boolean> callback;
    private boolean value;

    public GuiConfirmationScreen(IKey label, Consumer<Boolean> callback)
    {
        super();
        this.callback = callback;
        this.root.add(GuiConfirmModal.createTemplate(Minecraft.getInstance(), this.viewport, label, (value) ->
        {
            this.value = value;
            closeScreen();
        }));
    }

    /**
     * {@inheritDoc}
     *
     * This method returns false to indicate that the game should not be paused while this screen is open.
     *
     * @return false
     */
    public boolean doesGuiPauseGame()
    {
        return false;
    }

    /**
     * Closes the screen and calls the callback with the value.
     * Subclasses should call this method to close the screen and pass the value to the callback.
     */
    protected void closeScreen()
    {
        this.callback.accept(this.value);
        super.closeScreen();
    }

    /**
     * Draws the screen with the default background and calls the super drawScreen method.
     *
     * @param mouseX the x-coordinate of the mouse cursor
     * @param mouseY the y-coordinate of the mouse cursor
     * @param partialTicks the partial ticks for rendering
     */
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
