package bryanthedragon.mclibreloaded.math.molang.functions;

import bryanthedragon.mclibreloaded.math.IValue;
import bryanthedragon.mclibreloaded.math.functions.NNFunction;

public class CosDegrees extends NNFunction
{
    public CosDegrees(IValue[] values, String name) throws Exception
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
        return Math.cos(this.getArg(0).doubleValue() / 180 * Math.PI);
    }
}