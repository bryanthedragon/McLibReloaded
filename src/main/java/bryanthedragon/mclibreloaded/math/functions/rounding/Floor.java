package bryanthedragon.mclibreloaded.math.functions.rounding;

import bryanthedragon.mclibreloaded.math.IValue;
import bryanthedragon.mclibreloaded.math.functions.NNFunction;

public class Floor extends NNFunction
{
    public Floor(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 1;
    }

    @Override
    public double doubleValue()
    {
        return Math.floor(this.getArg(0).doubleValue());
    }
}