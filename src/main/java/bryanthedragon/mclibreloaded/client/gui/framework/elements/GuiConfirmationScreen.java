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

    public boolean doesGuiPauseGame()
    {
        return false;
    }

    protected void closeScreen()
    {
        this.callback.accept(this.value);
        super.closeScreen();
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();

        super.drawScreen(mouseX, mouseY, partialTicks);
    }
}
