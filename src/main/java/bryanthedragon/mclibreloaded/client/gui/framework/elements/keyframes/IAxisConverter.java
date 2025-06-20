package bryanthedragon.mclibreloaded.client.gui.framework.elements.keyframes;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.input.GuiTrackpadElement;
import bryanthedragon.mclibreloaded.utils.keyframes.Keyframe;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IAxisConverter
{
    public String format(double value);

    public double from(double x);

    public double to(double x);

    @SideOnly(Dist.CLIENT)
    public void updateField(GuiTrackpadElement element);

    @SideOnly(Dist.CLIENT)
    public boolean forceInteger(Keyframe keyframe, Selection selection, boolean forceInteger);
}