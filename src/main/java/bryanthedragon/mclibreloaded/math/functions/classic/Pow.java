package bryanthedragon.mclibreloaded.math.functions.classic;

import bryanthedragon.mclibreloaded.math.IValue;
import bryanthedragon.mclibreloaded.math.functions.NNFunction;

public class Pow extends NNFunction
{
    public Pow(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 2;
    }

    @Override
    public double doubleValue()
    {
        return Math.pow(this.getArg(0).doubleValue(), this.getArg(1).doubleValue());
    }
}