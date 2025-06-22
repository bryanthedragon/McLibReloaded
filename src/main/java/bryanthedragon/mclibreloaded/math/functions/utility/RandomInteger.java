package bryanthedragon.mclibreloaded.math.functions.utility;

import bryanthedragon.mclibreloaded.math.IValue;
import bryanthedragon.mclibreloaded.math.functions.Function;

public class RandomInteger extends Random
{
    public RandomInteger(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public double doubleValue()
    {
        return (int) super.doubleValue();
    }
}