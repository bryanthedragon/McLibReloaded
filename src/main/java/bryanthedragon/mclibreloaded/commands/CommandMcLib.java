package bryanthedragon.mclibreloaded.commands;

import bryanthedragon.mclibreloaded.McLibReloaded;
import bryanthedragon.mclibreloaded.commands.config.SubCommandConfig;
import bryanthedragon.mclibreloaded.commands.utils.L10n;

import net.minecraft.commands.CommandSourceStack;

public class CommandMcLib extends SubCommandBase
{
    public CommandMcLib()
    {
        this.add(new SubCommandConfig());
    }

    public String getName()
    {
        return "mclib";
    }

    public String getUsage(CommandSourceStack sender)
    {
        return "mclib.commands.mclib.help";
    }

    @Override
    public String getSyntax()
    {
        return "";
    }

    @Override
    public L10n getL10n()
    {
        return McLibReloaded.l10n;
    }
}
