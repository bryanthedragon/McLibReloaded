package bryanthedragon.mclibreloaded.utils;

public class TextUtils
{
    public static String processColoredText(String text)
    {
        if (!text.contains("["))
        {
            return text;
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0, c = text.length(); i < c; i++)
        {
            char character = text.charAt(i);

            if (character == '\\' && i < c - 1 && text.charAt(i + 1) == '[')
            {
                builder.append('[');
                i += 1;
            }
            else
            {
                builder.append(character == '[' ? '\u00A7' : character);
            }
        }

        return builder.toString();
    }
}
