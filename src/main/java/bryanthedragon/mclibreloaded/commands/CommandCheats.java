package bryanthedragon.mclibreloaded.commands;

import bryanthedragon.mclibreloaded.McLib;
import bryanthedragon.mclibreloaded.McLibReloaded;
import bryanthedragon.mclibreloaded.commands.utils.L10n;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class CommandCheats extends McCommandBase
{
    @Override
    public L10n getL10n()
    {
        return McLibReloaded.l10n;
    }

    @Override
    public String getName()
    {
        return "cheats";
    }

    @Override
    public String getUsage(ICommandSender sender)
    {
        return "mclib.commands.cheats";
    }

    @Override
    public String getSyntax()
    {
        return "{l}{6}/{r}cheats {7}<enabled:true|false>{r}";
    }

    @Override
    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
    {
        return true;
    }

    @Override
    public int getRequiredArgs()
    {
        return 1;
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        sender.getEntityWorld().getWorldInfo().setAllowCommands(CommandBase.parseBoolean(args[0]));
        server.saveAllWorlds(false);
    }

    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1)
        {
            return getListOfStringsMatchingLastWord(args, BOOLEANS);
        }

        return super.getTabCompletions(server, sender, args, targetPos);
    }
}
