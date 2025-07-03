package bryanthedragon.mclibreloaded;

import net.minecraftforge.common.ForgeConfigSpec;

public class McLibConfig 
{
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.BooleanValue ENABLE_BORDERS;
    public static final ForgeConfigSpec.IntValue TRACKPAD_DECIMAL_PLACES;
    public static final ForgeConfigSpec.ConfigValue<String> BACKGROUND_IMAGE;

    static {
        BUILDER.push("appearance");

        ENABLE_BORDERS = BUILDER
            .comment("Enable GUI borders")
            .define("enableBorders", false);

        TRACKPAD_DECIMAL_PLACES = BUILDER
            .comment("Trackpad decimal precision")
            .defineInRange("trackpadDecimalPlaces", 6, 3, 31);

        BACKGROUND_IMAGE = BUILDER
            .comment("Background image path")
            .define("backgroundImage", "");

        BUILDER.pop();

        SPEC = BUILDER.build();
    }
}
