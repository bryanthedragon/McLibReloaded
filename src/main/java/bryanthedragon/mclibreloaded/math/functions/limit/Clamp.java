package bryanthedragon.mclibreloaded.math.functions.limit;

import bryanthedragon.mclibreloaded.math.IValue;
import bryanthedragon.mclibreloaded.math.functions.NNFunction;
import bryanthedragon.mclibreloaded.utils.MathUtils;

public class Clamp extends NNFunction
{
    public Clamp(IValue[] values, String name) throws Exception
    {
        super(values, name);
    }

    @Override
    public int getRequiredArguments()
    {
        return 3;
    }

    @Override
    public double doubleValue()
    {
        return MathUtils.clamperDouble(this.getArg(0).doubleValue(), this.getArg(1).doubleValue(), this.getArg(2).doubleValue());
    }
}