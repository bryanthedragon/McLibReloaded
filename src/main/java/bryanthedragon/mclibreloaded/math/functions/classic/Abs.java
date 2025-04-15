package bryanthedragon.mclibreloaded.math.functions.classic;

import bryanthedragon.mclibreloaded.math.IValue;
import bryanthedragon.mclibreloaded.math.functions.NNFunction;

/**
 * Absolute value function 
 */
public class Abs extends NNFunction
{
    public Abs(IValue[] values, String name) throws Exception
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
        return Math.abs(this.getArg(0).doubleValue());
    }
}