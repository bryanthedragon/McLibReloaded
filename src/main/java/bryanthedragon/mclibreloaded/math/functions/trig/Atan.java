package bryanthedragon.mclibreloaded.math.functions.trig;

import bryanthedragon.mclibreloaded.math.IValue;
import bryanthedragon.mclibreloaded.math.functions.NNFunction;

public class Atan extends NNFunction
{
    public Atan(IValue[] values, String name) throws Exception
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
        return Math.atan(this.getArg(0).doubleValue());
    }
}
