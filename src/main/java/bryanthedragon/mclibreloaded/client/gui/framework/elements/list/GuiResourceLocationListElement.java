package bryanthedragon.mclibreloaded.client.gui.framework.elements.list;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;


/**
 * Similar to {@link GuiStringListElement}, but uses {@link ResourceLocation}s 
 */
public class GuiResourceLocationListElement extends GuiListElement<ResourceLocation>
{
    public GuiResourceLocationListElement(Minecraft mc, Consumer<List<ResourceLocation>> callback)
    {
        super(mc, callback);
        this.scroll.scrollItemSize = 16;
    }

    @Override
    protected boolean sortElements()
    {
        Collections.sort(this.list, (a, b) -> a.toString().compareToIgnoreCase(b.toString()));
        return true;
    }
}