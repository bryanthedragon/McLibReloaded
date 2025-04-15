package bryanthedragon.mclibreloaded.math.molang.functions;

import bryanthedragon.mclibreloaded.math.IValue;
import bryanthedragon.mclibreloaded.math.functions.trig.Atan2;

public class Atan2Degrees extends Atan2
{
    public Atan2Degrees(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public double doubleValue()
    {
        return super.doubleValue() / Math.PI * 180;
    }
}