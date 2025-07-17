package bryanthedragon.mclibreloaded.client.gui.framework.elements.modals;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.GuiElement;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.buttons.GuiButtonElement;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiContext;
import bryanthedragon.mclibreloaded.client.gui.utils.Area;
import bryanthedragon.mclibreloaded.client.gui.utils.keys.IKey;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.function.Consumer;

public class GuiConfirmModal extends GuiModal
{
    public GuiButtonElement confirm;
    public GuiButtonElement cancel;

    public Consumer<Boolean> callback;

    public GuiConfirmModal(Minecraft mc, IKey label, Consumer<Boolean> callback)
    {
        super(mc, label);

        this.callback = callback;

        this.confirm = new GuiButtonElement(mc, IKey.lang("mclib.gui.ok"), (b) -> this.close(true));
        this.cancel = new GuiButtonElement(mc, IKey.lang("mclib.gui.cancel"), (b) -> this.close(false));

        this.bar.add(this.confirm, this.cancel);
    }

    public static GuiConfirmModal createTemplate(Minecraft mc, GuiElement parent, IKey label, Consumer<Boolean> callback)
    {
        GuiConfirmModal modal = new GuiConfirmModal(mc, label, callback);

        modal.flex().relative(parent).xy(0.5F, 0.5F).wh(160, 180).anchor(0.5F, 0.5F);

        return modal;
    }

    public static GuiConfirmModal createTemplate(Minecraft mc, Area area, IKey label, Consumer<Boolean> callback)
    {
        GuiConfirmModal modal = new GuiConfirmModal(mc, label, callback);

        modal.flex().relative(area).xy(0.5F, 0.5F).wh(160, 180).anchor(0.5F, 0.5F);

        return modal;
    }

    public void close(boolean confirmed)
    {
        if (this.callback != null)
        {
            this.callback.accept(confirmed);
        }

        this.removeFromParent();
    }

    @Override
    public boolean keyTyped(GuiContext context)
    {
        if (super.keyTyped(context))
        {
            return true;
        }

        if (context.keyCode == GLFW.GLFW_KEY_ENTER || context.keyCode == GLFW.GLFW_KEY_ESCAPE)
        {
            (context.keyCode == GLFW.GLFW_KEY_ENTER ? this.confirm : this.cancel).clickItself(context);

            return true;
        }

        return false;
    }
}