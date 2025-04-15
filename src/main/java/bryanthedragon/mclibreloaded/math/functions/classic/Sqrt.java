package bryanthedragon.mclibreloaded.math.functions.classic;

import bryanthedragon.mclibreloaded.math.IValue;
import bryanthedragon.mclibreloaded.math.functions.NNFunction;

public class Sqrt extends NNFunction
{
    public Sqrt(IValue[] values, String name) throws Exception
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
        return Math.sqrt(this.getArg(0).doubleValue());
    }
}