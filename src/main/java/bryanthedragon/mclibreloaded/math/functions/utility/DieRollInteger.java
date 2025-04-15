package bryanthedragon.mclibreloaded.math.functions.utility;

import bryanthedragon.mclibreloaded.math.IValue;

public class DieRollInteger extends DieRoll
{
    public DieRollInteger(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public double doubleValue()
    {
        return (int) super.doubleValue();
    }
}