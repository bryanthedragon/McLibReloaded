package bryanthedragon.mclibreloaded.commands.utils;

import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

/**
 * Localization utils
 *
 * This class provides shortcuts for sending messages to players. Pretty tired
 * of typing a lot of characters with provided API.
 *
 * API should be clear, short and concise.
 */
public class L10n
{
    public static final Component ERROR_MARKER = Component.literal("(X)").withStyle(ChatFormatting.DARK_RED, ChatFormatting.BOLD);
    public static final Component SUCCESS_MARKER = Component.literal("(V)").withStyle(ChatFormatting.DARK_GREEN, ChatFormatting.BOLD);
    public static final Component INFO_MARKER = Component.literal("(i)").withStyle(ChatFormatting.DARK_AQUA, ChatFormatting.BOLD);

    private final String id;

    public L10n(String id)
    {
        this.id = id;
    }

    /**
     * Send a translated message to player
     */
    public void send(CommandSourceStack sender, String key, Object... objects)
    {
        sender.sendSuccess(() -> Component.translatable(key, objects), false);
    }

    /**
     * Send a translated message to player
     */
    public void sendColored(CommandSourceStack sender,ChatFormatting color, String key, Object... objects)
    {
        Component text = Component.translatable(key, objects);
        sender.sendSuccess(() -> text, false);
    }

    /**
     * Send error message to the sender
     */
    public void error(CommandSourceStack sender, String key, Object... objects)
    {
        this.sendWithMarker(sender, String.valueOf(ERROR_MARKER), this.id + ".error." + key, objects);
    }

    /**
     * Get error message
     */
    public Component error(String key, Object... objects)
    {
        return this.messageWithMarker(String.valueOf(ERROR_MARKER), this.id + ".error." + key, objects);
    }

    /**
     * Send success message to the sender
     */
    public void success(CommandSourceStack sender, String key, Object... objects)
    {
        this.sendWithMarker(sender, String.valueOf(SUCCESS_MARKER), this.id + ".success." + key, objects);
    }

    /**
     * Get success message
     */
    public Component success(String key, Object... objects)
    {
        return this.messageWithMarker(String.valueOf(SUCCESS_MARKER), this.id + ".success." + key, objects);
    }

    /**
     * Send informing message to the sender
     */
    public void info(CommandSourceStack sender, String key, Object... objects)
    {
        this.sendWithMarker(sender, String.valueOf(INFO_MARKER), this.id + ".info." + key, objects);
    }

    /**
     * Get informing message
     */
    public Component info(String key, Object... objects)
    {
        return  this.messageWithMarker(String.valueOf(INFO_MARKER), this.id + ".info." + key, objects);
    }

    /**
     * Send a message with given marker
     */
    public void sendWithMarker(CommandSourceStack sender, String marker, String key, Object... objects)
    {
        sender.sendSuccess(() -> messageWithMarker(marker, key, objects),false);
    }

    public Component messageWithMarker(String marker, String key, Object... objects)
    {
        return Component.literal(marker).append(Component.translatable(key, objects).withStyle(ChatFormatting.GRAY));
    }
}