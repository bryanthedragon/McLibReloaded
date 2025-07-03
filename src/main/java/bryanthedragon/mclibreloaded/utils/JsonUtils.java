package bryanthedragon.mclibreloaded.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.stream.JsonWriter;

import java.io.StringWriter;

public class JsonUtils
{
    /**
     * Converts a JsonElement into a pretty-printed JSON string.
     *
     * This method uses Gson's pretty printing capability to convert the given
     * JsonElement into a formatted string with indentations. Arrays within the
     * JSON are specifically prettified for better readability.
     *
     * @param element the JsonElement to be converted into a pretty-printed string
     * @return a string representing the pretty-printed JSON
     */
    public static String jsonToPretty(JsonElement element)
    {
        StringWriter writer = new StringWriter();
        JsonWriter jsonWriter = new JsonWriter(writer);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        jsonWriter.setIndent("    ");
        gson.toJson(element, jsonWriter);

        /* Prettify arrays */
        return writer.toString();
    }
}