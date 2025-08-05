package bryanthedragon.mclibreloaded.commands;

import bryanthedragon.mclibreloaded.McLibReloaded;
import bryanthedragon.mclibreloaded.commands.utils.L10n;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;
import java.util.List;

public class CommandCheats extends McCommandBase
{
    @Override
    public L10n getL10n()
    {
        return McLibReloaded.l10n;
    }


    public String getName()
    {
        return "cheats";
    }


    public String getUsage(CommandSourceStack sender)
    {
        return "mclib.commands.cheats";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}cheats {7}<enabled:true|false>{r}";
    }

    public boolean checkPermission(MinecraftServer server, CommandSourceStack sender)
    {
        return true;
    }

    @Override
    public int getRequiredArgs()
    {
        return 1;
    }

    public void executeCommand(MinecraftServer server, CommandSourceStack sender, String[] args) throws CommandException
    {
        sender.getEntityWorld().getWorldInfo().setAllowCommands(CommandBase.parseBoolean(args[0]));
        server.saveAllWorlds(false);
    }

    public List<String> getTabCompletions(MinecraftServer server, CommandSourceStack sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, McCommandBase.BOOLEANS);
        }

        return super.getTabCompletions(server, sender, args, targetPos);
    }
}
