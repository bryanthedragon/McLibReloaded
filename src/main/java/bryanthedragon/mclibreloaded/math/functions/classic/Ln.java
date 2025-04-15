package bryanthedragon.mclibreloaded.math.functions.classic;

import bryanthedragon.mclibreloaded.math.IValue;
import bryanthedragon.mclibreloaded.math.functions.NNFunction;

public class Ln extends NNFunction
{
    public Ln(IValue[] values, String name) throws Exception
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
        return Math.log(this.getArg(0).doubleValue());
    }
}