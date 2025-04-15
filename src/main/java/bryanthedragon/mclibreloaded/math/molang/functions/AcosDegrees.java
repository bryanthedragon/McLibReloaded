package bryanthedragon.mclibreloaded.math.molang.functions;

import bryanthedragon.mclibreloaded.math.IValue;
import bryanthedragon.mclibreloaded.math.functions.trig.Acos;

public class AcosDegrees extends Acos
{
    public AcosDegrees(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public double doubleValue()
    {
        return super.doubleValue() / Math.PI * 180;
    }
}