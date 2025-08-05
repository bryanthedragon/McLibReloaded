package bryanthedragon.mclibreloaded.utils;

import net.minecraft.client.Minecraft;

import java.util.List;

import org.lwjgl.glfw.GLFW;

import com.google.common.collect.ImmutableList;

public class Keys
{
    public static final int KEYBOARD_SIZE = 512;
    public static final String[] KEYS = new String[KEYBOARD_SIZE];
    public static final List<Integer> MODIFIERS = ImmutableList.<Integer>of(GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_LEFT_SHIFT, GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_RIGHT_CONTROL, GLFW.GLFW_KEY_RIGHT_SHIFT, GLFW.GLFW_KEY_RIGHT_ALT);
    public static final String[] MODNAME = new String[] {"Ctrl", "Shift", "Alt"};

    /**
     * Retrieves the human-readable name for a given GLFW key code.
     * If the key code is not within the valid range, returns null.
     * Caches the key name for performance optimization.
     * If the key is unknown, returns "Unknown key".
     *
     * @param key the GLFW key code
     * @return the human-readable name of the key, or null if out of range
     */
    public static String getKeyName(int key)
    {
        if (key < GLFW.GLFW_KEY_UNKNOWN || key >= KEYBOARD_SIZE)
        {
            return null;
        }
	// Adding this line prevents a null-pointer exception
        if (KEYS[key] == null)
        {
            KEYS[key] = getKey(key);
            if (KEYS[key] == null) 
            {
                return "Unknown key";
            }
        }
        return KEYS[key];
    }

    /**
     * Returns a human-readable name for the given GLFW key code.
     * The name is cached for performance reasons.
     * @param key the key code
     * @return the human-readable name, or null if the key is unknown
     */
    @SuppressWarnings("null")
    private static String getKey(int key)
    {
        switch (key)
        {
            case GLFW.GLFW_KEY_MINUS:
                return "-";
            case GLFW.GLFW_KEY_EQUAL:
                return "=";
            case GLFW.GLFW_KEY_LEFT_BRACKET:
                return "[";
            case GLFW.GLFW_KEY_RIGHT_BRACKET:
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
            case GLFW.GLFW_KEY_GRAVE_ACCENT:
                return "`";
            case GLFW.GLFW_KEY_TAB:
                return "Tab";
            case GLFW.GLFW_KEY_CAPS_LOCK:
                return "Caps Lock";
            case GLFW.GLFW_KEY_LEFT_SHIFT:
                return "L. Shift";
            case GLFW.GLFW_KEY_LEFT_CONTROL:
                return "L. Ctrl";
            case GLFW.GLFW_KEY_LEFT_ALT:
                return "L. Alt";
            case GLFW.GLFW_KEY_LEFT_SUPER:
                return isMac() ? "L. Cmd" : "L. Win";
            case GLFW.GLFW_KEY_RIGHT_SHIFT:
                return "R. Shift";
            case GLFW.GLFW_KEY_RIGHT_CONTROL:
                return "R. Ctrl";
            case GLFW.GLFW_KEY_RIGHT_ALT:
                return "R. Alt";
            case GLFW.GLFW_KEY_RIGHT_SUPER:
                return isMac() ? "R. Cmd" : "R. Win";
            case GLFW.GLFW_KEY_KP_DIVIDE:
                return "Numpad /";
            case GLFW.GLFW_KEY_KP_MULTIPLY:
                return "Numpad *";
            case GLFW.GLFW_KEY_KP_SUBTRACT:
                return "Numpad -";
            case GLFW.GLFW_KEY_KP_ADD:
                return "Numpad +";
            case GLFW.GLFW_KEY_KP_DECIMAL:
                return "Numpad .";
        }
        String name = GLFW.glfwGetKeyName(key,0);
        if (name != null)
        {
            return name;
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

    /**
     * Generates a combo key code from a list of held keys and a key code.
     * <p>
     * The combo key code is a combination of the key code and the modifiers.
     * The modifiers are stored in the high-order three bits of the combo key code.
     * The low-order 29 bits of the combo key code are the key code.
     * <p>
     * The modifier index is the position of the modifier in the MODIFIERS list.
     * The modifier index is used to determine which modifier to set in the combo key code.
     * <p>
     * If the key code is a modifier, the modifier is ignored in the combo key code.
     * If the held keys contain multiple modifiers, the last modifier in the list is used.
     * <p>
     * For example, if the key code is GLFW.GLFW_KEY_A and the held keys are GLFW.GLFW_KEY_LEFT_SHIFT and GLFW.GLFW_KEY_LEFT_CONTROL, the combo key code is GLFW.GLFW_KEY_A | (1 << 31 - 0) | (1 << 31 - 1).
     * <p>
     * @param held the list of held keys
     * @param keyCode the key code
     * @return the combo key code
     */
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

    /**
     * A simple test to verify that the combo key generation works as expected.
     * <p>
     * This program prints the combo key name for the key combination
     * RightShift+LeftAlt+LeftControl+RightControl+RightAlt.
     */
    public static void main(String...args)
    {
        System.out.println(getComboKeyName(getComboKeyCode(new int[] {GLFW.GLFW_KEY_RIGHT_SHIFT, GLFW.GLFW_KEY_LEFT_ALT, GLFW.GLFW_KEY_LEFT_CONTROL, GLFW.GLFW_KEY_RIGHT_CONTROL}, GLFW.GLFW_KEY_RIGHT_ALT)));
    }

    /**
     * Extracts the main key from a combo key.
     * <p>
     * This method masks the combo key to retrieve the main key part and checks if 
     * it is within the valid keyboard size. If the key is not valid, it defaults 
     * to an unknown key.
     * 
     * @param comboKey the combo key to extract the main key from
     * @return the main key extracted from the combo key, or GLFW.GLFW_KEY_UNKNOWN 
     *         if the key is invalid
     */
    public static int getMainKey(int comboKey)
    {
        int key = comboKey & 0x1FFFFFFF;
        if (key >= KEYBOARD_SIZE)
        {
            key = GLFW.GLFW_KEY_UNKNOWN;
        }
        return key;
    }

    /**
     * Returns a string representation of the given combo key.
     * <p>
     * Example: "Ctrl + Shift + Space"
     * @param comboKey the combo key
     * @return the string representation of the combo key
     */
    public static String getComboKeyName(int comboKey)
    {
        StringBuilder builder = new StringBuilder();
        int mainKey = getMainKey(comboKey);
        if (mainKey == GLFW.GLFW_KEY_UNKNOWN)
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

    /**
     * Checks if the modifier keys for the specified combo key are currently pressed.
     * <p>
     * This is useful for checking if the modifier keys for a given combo key are currently held down.
     * @param comboKey the combo key to check
     * @return true if the modifier keys are currently pressed
     */
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

    /**
     * Checks if the specified key is currently pressed (or repeating) in the Minecraft window.
     * <p>
     * This is a utility method for checking if a key is pressed from various parts of the codebase.
     * @param key the key to check
     * @return true if the key is currently pressed (or repeating)
     */
    public static boolean isKeyDown(int key)
    {
        long window = Minecraft.getInstance().getWindow().getWindow();
        if (key == GLFW.GLFW_KEY_LEFT_SHIFT || key == GLFW.GLFW_KEY_RIGHT_SHIFT) 
        {
            return glfwKeyPressed(window, GLFW.GLFW_KEY_LEFT_SHIFT) || glfwKeyPressed(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
        } 
        else if (key == GLFW.GLFW_KEY_LEFT_CONTROL || key == GLFW.GLFW_KEY_RIGHT_CONTROL) 
        {
            return glfwKeyPressed(window, GLFW.GLFW_KEY_LEFT_CONTROL) || glfwKeyPressed(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
        } 
        else if (key == GLFW.GLFW_KEY_LEFT_ALT || key == GLFW.GLFW_KEY_RIGHT_ALT) 
        {
            return glfwKeyPressed(window, GLFW.GLFW_KEY_LEFT_ALT) || glfwKeyPressed(window, GLFW.GLFW_KEY_RIGHT_ALT);
        }
        return glfwKeyPressed(window, key);
    }

    /**
     * Checks if the specified key is currently pressed (or repeating)
     * in the specified window.
     * <p>
     * This uses GLFW's {@code glfwGetKey} function to query the state of
     * the key in the specified window.
     * <p>
     * Note that this will return true if the key is repeating, not just
     * if it was initially pressed.
     * <p>
     * This is a utility method for checking if a key is pressed from
     * various parts of the codebase.
     * @param window the window to check the key in
     * @param key the key to check
     * @return true if the key is currently pressed (or repeating)
     */
    private static boolean glfwKeyPressed(long window, int key) 
    {
        int state = GLFW.glfwGetKey(window, key);
        return state == GLFW.GLFW_PRESS || state == GLFW.GLFW_REPEAT;
    }

    /**
     * Checks if the current OS is a Mac.
     * <p>
     * This uses the {@code os.name} system property, which is set to
     * {@code "Mac OS X"} on Macs.
     * @return true if the current OS is a Mac
     */
    private static boolean isMac()
    {
        return System.getProperty("os.name").toLowerCase().contains("mac");
    }

    /**
     * Returns the GLFW key name for the given key.
     * <p>
     * Since GLFW.glfwGetKeyName requires a scancode, and you don't have one, 0 is passed as the scancode.
     * <p>
     * This method is not intended to be used, and is only here for debugging purposes.
     * @param key the key
     * @return the GLFW key name
     */
    @SuppressWarnings("unused")
    private static String getGlfwKeyName(int key)
    {
        // GLFW key names are often obtained by GLFW.glfwGetKeyName, which takes the key and scancode.
        // Since you don't have a scancode, pass 0
        String name = GLFW.glfwGetKeyName(key, 0);
        return name;
    }
}
