package bryanthedragon.mclibreloaded.client.gui.utils.resizers;

import bryanthedragon.mclibreloaded.client.gui.utils.Area;

public interface IParentResizer
{
    public void apply(Area area, IResizer resizer, ChildResizer child);
}
