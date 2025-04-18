package bryanthedragon.mclibreloaded.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Abstract sub-command base handler command
 *
 * This abstract command implements sub-commands system. By extending this
 * class, it allows to add sub-commands.
 */
public abstract class SubCommandBase extends McCommandBase
{
    /**
     * Sub-commands list, add your sub commands in this list.
     */
    protected Map<String, McCommandBase> subcommands = new LinkedHashMap<String, McCommandBase>();

    /**
     * Drop only the first argument
     */
    public static String[] dropFirstArgument(String[] input)
    {
        return dropFirstArguments(input, 1);
    }

    /**
     * Totally not copied from CommandHandler.
     */
    public static String[] dropFirstArguments(String[] input, int amount)
    {
        String[] astring = new String[input.length - amount];
        System.arraycopy(input, amount, astring, 0, input.length - amount);

        return astring;
    }

    /**
     * Add a sub-command to the sub-commands map
     */
    protected void add(McCommandBase subcommand)
    {
        this.subcommands.put(subcommand.getName(), subcommand);
    }

    @Override
    public String getSyntax()
    {
        return "";
    }

    @Override
    public ITextComponent getUsageMessage(ICommandSender sender)
    {
        ITextComponent message = new TextComponentTranslation(this.getUsage(sender));

        message.getStyle().setColor(TextFormatting.WHITE);
        message.appendSibling(new TextComponentString("\n\n"));

        int i = 0;
        int c = this.subcommands.size();

        for (McCommandBase command : this.subcommands.values())
        {
            String extra = i == c - 1 ? "" : "\n";

            message.appendSibling(new TextComponentString(command.getProcessedSyntax() + extra));

            i += 1;
        }

        return message;
    }

    /**
     * Delegate isUsernameIndex method to a subcommand
     */
    @Override
    public boolean isUsernameIndex(String[] args, int index)
    {
        McCommandBase command = this.subcommands.get(args.length >= 1 ? args[0] : "");

        if (command != null && command.isUsernameIndex(dropFirstArgument(args), index - 1))
        {
            return true;
        }

        return super.isUsernameIndex(args, index);
    }

    /**
     * Execute the command
     *
     * This method basically delegates the execution to the matched sub-command,
     * if the command was found, otherwise it shows usage message. */
    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length < 1)
        {
            throw new WrongUsageException("mclib.commands.wrapper", this.getUsageMessage(sender));
        }

        McCommandBase command = this.subcommands.get(args[0]);

        if (command != null)
        {
            if (args.length >= 2 && args[1].equals("-h"))
            {
                throw new WrongUsageException("mclib.commands.wrapper", command.getUsageMessage(sender));
            }

            command.execute(server, sender, dropFirstArgument(args));
        }
        else
        {
            throw new WrongUsageException("mclib.commands.wrapper", this.getUsageMessage(sender));
        }
    }

    @Override
    public void executeCommand(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
    {}

    /**
     * Get completions for this command or its sub-commands.
     *
     * This method is responsible for giving completions of this command (names
     * of sub-commands) or completions of sub-command.
     */
    @Override
    public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos pos)
    {
        if (args.length == 0)
        {
            return super.getTabCompletions(server, sender, args, pos);
        }

        Collection<McCommandBase> commands = this.subcommands.values();

        if (args.length == 1)
        {
            List<String> options = new ArrayList<String>();

            for (CommandBase command : commands)
            {
                options.add(command.getName());
            }

            return getListOfStringsMatchingLastWord(args, options);
        }

        for (CommandBase command : commands)
        {
            if (command.getName().equals(args[0]))
            {
                return command.getTabCompletions(server, sender, dropFirstArgument(args), pos);
            }
        }

        return super.getTabCompletions(server, sender, args, pos);
    }
}