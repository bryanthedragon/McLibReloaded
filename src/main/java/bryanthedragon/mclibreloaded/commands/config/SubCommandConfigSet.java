package bryanthedragon.mclibreloaded.commands.config;

import bryanthedragon.mclibreloaded.McLibReloaded;
import bryanthedragon.mclibreloaded.commands.SubCommandBase;
import bryanthedragon.mclibreloaded.config.ConfigManager;
import bryanthedragon.mclibreloaded.config.values.IServerValue;
import bryanthedragon.mclibreloaded.config.values.Value;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;

public class SubCommandConfigSet extends SubCommandConfigBase
{
    public String getName()
    {
        return "set";
    }

    public String getUsage(CommandSourceStack sender)
    {
        return "mclib.commands.mclib.config.set";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}mclib {8}config set{r} {7}<mod.category.option> <value...>{r}";
    }

    @Override
    public int getRequiredArgs()
    {
        return 2;
    }

    public void executeCommand(MinecraftServer server, CommandSourceStack sender, String[] args) throws CommandException
    {
        Value value = SubCommandConfig.get(args[0]);

        if (!value.isClientSide() && value instanceof IServerValue)
        {
            String command = String.join(" ", SubCommandBase.dropFirstArguments(args, 1));

            if (((IServerValue) value).parseFromCommand(command))
            {
                if (value.isSyncable())
                {
                    String mod = args[0].substring(0, args[0].indexOf("."));

                    ConfigManager.synchronizeConfig(McLibReloaded.commonproxy.configs.modules.get(mod).filterSyncable(), server);
                }

                this.getL10n().info(String.valueOf(sender), "config.set", args[0], value.toString());
            }
            else
            {
                throw new CommandException("config.invalid_format", args[0], args[1]);
            }
        }
        else
        {
            this.getL10n().info(String.valueOf(sender), "config.client_side", args[0]);
        }
    }
}
