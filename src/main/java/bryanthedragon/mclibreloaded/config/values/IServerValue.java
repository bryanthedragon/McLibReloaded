package bryanthedragon.mclibreloaded.config.values;

public interface IServerValue
{
    public void resetServer();

    public boolean parseFromCommand(String value);

    public void copyServer(Value value);
}