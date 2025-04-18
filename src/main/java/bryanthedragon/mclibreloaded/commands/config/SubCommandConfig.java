package bryanthedragon.mclibreloaded.commands.config;

import bryanthedragon.mclibreloaded.McLib;
import bryanthedragon.mclibreloaded.commands.SubCommandBase;
import bryanthedragon.mclibreloaded.commands.utils.L10n;
import mchorse.mclib.config.values.Value;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;

public class SubCommandConfig extends SubCommandBase
{
    public static Value get(String id) throws CommandException
    {
        try
        {
            String[] splits = id.split("\\.");

            if (splits.length != 3)
            {
                throw new Exception("Identifier should have exactly 3 strings separated by a period!");
            }

            return McLib.proxy.configs.modules.get(splits[0]).values.get(splits[1]).getSubValue(splits[2]);
        }
        catch (Exception e)
        {
            throw new CommandException("config.invalid_id", id);
        }
    }

    public SubCommandConfig()
    {
        this.add(new SubCommandConfigPrint());
        this.add(new SubCommandConfigSet());
    }

    @Override
    public String getName()
    {
        return "config";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "mclib.commands.mclib.config.help";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}mclib {8}config{r}";
    }

    @Override
    public L10n getL10n()
    {
        return McLib.l10n;
    }
}
