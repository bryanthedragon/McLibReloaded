package bryanthedragon.mclibreloaded.client.gui.framework.elements.modals;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.buttons.GuiButtonElement;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiContext;
import bryanthedragon.mclibreloaded.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

public class GuiMessageModal extends GuiModal
{
    public GuiButtonElement button;

    public GuiMessageModal(Minecraft mc, IKey label)
    {
        super(mc, label);

        this.button = new GuiButtonElement(mc, IKey.lang("mclib.gui.ok"), (b) -> this.removeFromParent());

        this.bar.add(this.button);
    }

    @Override
    public boolean keyTyped(GuiContext context)
    {
        if (super.keyTyped(context))
        {
            return true;
        }

        if (context.keyCode == Keyboard.KEY_RETURN || context.keyCode == Keyboard.KEY_ESCAPE)
        {
            this.button.clickItself(context);

            return true;
        }

        return false;
    }
}