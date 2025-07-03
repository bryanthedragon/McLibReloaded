package bryanthedragon.mclibreloaded.commands.config;

import bryanthedragon.mclibreloaded.config.values.Value;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

public class SubCommandConfigPrint extends SubCommandConfigBase
{
    public String getName()
    {
        return "print";
    }

    public String getUsage(CommandSourceStack sender)
    {
        return "mclib.commands.mclib.config.print";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}mclib {8}config print{r} {7}<mod.category.option>{r}";
    }

    public void executeCommand(MinecraftServer server, CommandSourceStack sender, String[] args) throws CommandException
    {
        Value value = SubCommandConfig.get(args[0]);

        if (!value.isClientSide())
        {
            this.getL10n().info(String.valueOf(sender), "config.print", args[0], value.toString());
        }
        else
        {
            this.getL10n().info(String.valueOf(sender), "config.client_side", args[0]);
        }
    }
}
