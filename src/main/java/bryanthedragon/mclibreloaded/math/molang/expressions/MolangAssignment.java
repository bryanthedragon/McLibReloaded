package bryanthedragon.mclibreloaded.math.molang.expressions;

import bryanthedragon.mclibreloaded.math.IValue;
import bryanthedragon.mclibreloaded.math.Variable;
import bryanthedragon.mclibreloaded.math.molang.MolangParser;

public class MolangAssignment extends MolangExpression
{
    public Variable variable;
    public IValue expression;

    public MolangAssignment(MolangParser context, Variable variable, IValue expression)
    {
        super(context);

        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public double get()
    {
        double value = this.expression.get().doubleValue();

        this.variable.set(value);

        return value;
    }

    @Override
    public String toString()
    {
        return this.variable.getName() + " = " + this.expression.toString();
    }
}