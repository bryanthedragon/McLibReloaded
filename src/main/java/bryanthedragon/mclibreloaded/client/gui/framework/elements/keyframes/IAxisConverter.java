package bryanthedragon.mclibreloaded.client.gui.framework.elements.keyframes;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.input.GuiTrackpadElement;
import bryanthedragon.mclibreloaded.utils.keyframes.Keyframe;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IAxisConverter
{
    public String format(double value);

    public double from(double x);

    public double to(double x);

    @OnlyIn(Dist.CLIENT)
    public void updateField(GuiTrackpadElement element);

    @OnlyIn(Dist.CLIENT)
    public boolean forceInteger(Keyframe keyframe, Selection selection, boolean forceInteger);
}