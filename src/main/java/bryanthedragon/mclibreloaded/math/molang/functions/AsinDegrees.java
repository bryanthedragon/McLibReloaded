package bryanthedragon.mclibreloaded.math.molang.functions;

import bryanthedragon.mclibreloaded.math.IValue;
import bryanthedragon.mclibreloaded.math.functions.trig.Asin;

public class AsinDegrees extends Asin
{
    public AsinDegrees(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public double doubleValue()
    {
        return super.doubleValue() / Math.PI * 180;
    }
}