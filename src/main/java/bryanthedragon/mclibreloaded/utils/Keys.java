package bryanthedragon.mclibreloaded.utils;

import net.minecraft.client.Minecraft;
import java.util.List;
import org.lwjgl.glfw.GLFW;
import com.google.common.collect.ImmutableList;

public class Keys
{
    public static final String[] KEYS = new String[GLFW.GLFW_KEY_LAST + 1];
    public static final List<Integer> MODIFIERS = ImmutableList.<Integer>of(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_CONTROL, GLFW.GLFW_KEY_RIGHT_SHIFT, GLFW.GLFW_KEY_RIGHT_ALT);
    public static final String[] MODNAME = new String[] {"Ctrl", "Shift", "Alt"};

    public static String getKeyName(int key)
    {
        if (key < GLFW.GLFW_KEY_NONE || key >= GLFW.GLFW_KEY_LAST)
        {
            return null;
        }

	// Adding this line prevents a null-pointer exception
        if (KEYS[key] == null)
        {
            KEYS[key] = getKey(key);
            if (KEYS[key] == null) {
                return "Unknown key";
            }
        }

        return KEYS[key];
    }

    private static String getKey(int key)
    {
        switch (key)
        {
            case GLFW.GLFW_KEY_MINUS:
                return "-";
            case GLFW.GLFW_KEY_EQUALS:
                return "=";
            case GLFW.GLFW_KEY_LBRACKET:
                return "[";
            case GLFW.GLFW_KEY_RBRACKET:
                return "]";
            case GLFW.GLFW_KEY_SEMICOLON:
                return ";";
            case GLFW.GLFW_KEY_APOSTROPHE:
                return "'";
            case GLFW.GLFW_KEY_BACKSLASH:
                return "\\";
            case GLFW.GLFW_KEY_COMMA:
                return ",";
            case GLFW.GLFW_KEY_PERIOD:
                return ".";
            case GLFW.GLFW_KEY_SLASH:
                return "/";
            case GLFW.GLFW_KEY_GRAVE:
                return "`";
            case GLFW.GLFW_KEY_TAB:
                return "Tab";
            case GLFW.GLFW_KEY_CAPITAL:
                return "Caps Lock";
            case GLFW.GLFW_KEY_LSHIFT:
                return "L. Shift";
            case GLFW.GLFW_KEY_LCONTROL:
                return "L. Ctrl";
            case GLFW.GLFW_KEY_LMENU:
                return "L. Alt";
            case GLFW.GLFW_KEY_LMETA:
                return Minecraft.IS_RUNNING_ON_MAC ? "L. Cmd" : "L. Win";
            case GLFW.GLFW_KEY_RSHIFT:
                return "R. Shift";
            case GLFW.GLFW_KEY_RCONTROL:
                return "R. Ctrl";
            case GLFW.GLFW_KEY_RMENU:
                return "R. Alt";
            case GLFW.GLFW_KEY_RMETA:
                return Minecraft.IS_RUNNING_ON_MAC ? "R. Cmd" : "R. Win";
            case GLFW.GLFW_KEY_DIVIDE:
                return "Numpad /";
            case GLFW.GLFW_KEY_MULTIPLY:
                return "Numpad *";
            case GLFW.GLFW_KEY_SUBTRACT:
                return "Numpad -";
            case GLFW.GLFW_KEY_ADD:
                return "Numpad +";
            case GLFW.GLFW_KEY_DECIMAL:
                return "Numpad .";
        }

        String name = GLFW.GLFW_getKeyName(key);

	// Adding this line prevents a null-pointer exception
	if (name == null) {
            return null;
        }

        if (name.length() > 1)
        {
            name = name.substring(0, 1) + name.substring(1).toLowerCase();
        }

        if (name.startsWith("Numpad"))
        {
            name = name.replace("Numpad", "Numpad ");
        }

        return name;
    }

    /* Combo keys */

    public static int getComboKeyCode(int[] held, int keyCode)
    {
        int comboKey = keyCode;
        int modifierIndex = MODIFIERS.indexOf(keyCode) % 3;

        if (held != null)
        {
            for (int heldKey : held)
            {
                int index = MODIFIERS.indexOf(heldKey) % 3;

                if (index >= 0 && index != modifierIndex)
                {
                    comboKey |= 1 << 31 - index;
                }
            }
        }

        return comboKey;
    }

    public static void main(String...args)
    {
        System.out.println(getComboKeyName(getComboKeyCode(new int[] {GLFW.GLFW_KEY_RSHIFT, GLFW.GLFW_KEY_LMENU, GLFW.GLFW_KEY_LCONTROL, GLFW.GLFW_KEY_RCONTROL}, GLFW.GLFW_KEY_RMENU)));
    }

    public static int getMainKey(int comboKey)
    {
        int key = comboKey & 0x1FFFFFFF;

        if (key >= GLFW.GLFW_KEYBOARD_SIZE)
        {
            key = GLFW.GLFW_KEY_NONE;
        }

        return key;
    }

    public static String getComboKeyName(int comboKey)
    {
        StringBuilder builder = new StringBuilder();
        int mainKey = getMainKey(comboKey);

        if (mainKey == GLFW.GLFW_KEY_NONE)
        {
            return getKeyName(mainKey);
        }

        for (int i = 0; i < 3; i++)
        {
            if ((comboKey & 1 << 31 - i) != 0)
            {
                builder.append(MODNAME[i]).append(" + ");
            }
        }

        builder.append(getKeyName(mainKey));

        return builder.toString();
    }

    public static boolean checkModifierKeys(int comboKey)
    {
        int index = MODIFIERS.indexOf(getMainKey(comboKey)) % 3;

        for (int i = 0; i < 3; i++)
        {
            if (i == index)
            {
                continue;
            }

            if ((comboKey & 1 << 31 - i) != 0 != isKeyDown(MODIFIERS.get(i)))
            {
                return false;
            }
        }

        return true;
    }

    public static boolean isKeyDown(int key)
    {
        if (key == GLFW.GLFW_KEY_LSHIFT || key == GLFW.GLFW_KEY_RSHIFT)
        {
            return GLFW.GLFW_isKeyDown(GLFW.GLFW_KEY_LSHIFT) || GLFW.GLFW_isKeyDown(GLFW.GLFW_KEY_RSHIFT);
        }
        else if (key == GLFW.GLFW_KEY_LCONTROL || key == GLFW.GLFW_KEY_RCONTROL)
        {
            return GLFW.GLFW_isKeyDown(GLFW.GLFW_KEY_LCONTROL) || GLFW.GLFW_isKeyDown(GLFW.GLFW_KEY_RCONTROL);
        }
        else if (key == GLFW.GLFW_KEY_LMENU || key == GLFW.GLFW_KEY_RMENU)
        {
            return GLFW.GLFW_isKeyDown(GLFW.GLFW_KEY_LMENU) || GLFW.GLFW_isKeyDown(GLFW.GLFW_KEY_RMENU);
        }

        return GLFW.GLFW_isKeyDown(key);
    }
}
