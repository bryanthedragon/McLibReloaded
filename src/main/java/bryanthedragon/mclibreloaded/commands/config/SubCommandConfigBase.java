package bryanthedragon.mclibreloaded.commands.config;

import bryanthedragon.mclibreloaded.McLib;
import bryanthedragon.mclibreloaded.commands.McCommandBase;
import bryanthedragon.mclibreloaded.commands.utils.L10n;
import bryanthedragon.mclibreloaded.config.Config;
import bryanthedragon.mclibreloaded.config.values.Value;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;

import javax.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class SubCommandConfigBase extends McCommandBase
{
    @Override
    public int getRequiredArgs()
    {
        return 1;
    }

    @Override
    public L10n getL10n()
    {
        return McLib.l10n;
    }

    public List<String> getTabCompletions(MinecraftServer server, CommandSourceStack sender, String[] args, @Nullable BlockPos targetPos)
    {
        if (args.length == 1)
        {
            List<String> ids = new ArrayList<>();

            for (Config config : McLib.proxy.configs.modules.values())
            {
                for (Value category : config.values.values())
                {
                    for (Value value : category.getSubValues())
                    {
                        if (value.isClientSide())
                        {
                            continue;
                        }

                        ids.add(config.id + "." + category.id + "." + value.id);
                    }
                }
            }

            return getListOfStringsMatchingLastWord(args, ids);
        }

        return super.getTabCompletions(server, sender, args, targetPos);
    }
}
