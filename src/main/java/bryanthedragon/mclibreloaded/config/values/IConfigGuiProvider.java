package bryanthedragon.mclibreloaded.config.values;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.GuiElement;
import bryanthedragon.mclibreloaded.config.gui.GuiConfigPanel;
import net.minecraft.client.Minecraft;
import java.util.List;

public interface IConfigGuiProvider
{
    @SideOnly(Dist.CLIENT)
    public List<GuiElement> getFields(Minecraft mc, GuiConfigPanel gui);
}