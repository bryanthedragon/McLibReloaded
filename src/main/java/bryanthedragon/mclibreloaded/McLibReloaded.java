package bryanthedragon.mclibreloaded;

import bryanthedragon.mclibreloaded.client.gui.utils.ValueColors;
import bryanthedragon.mclibreloaded.client.gui.utils.keys.IKey;
import bryanthedragon.mclibreloaded.commands.utils.L10n;
import bryanthedragon.mclibreloaded.events.RegisterPermissionsEvent;
import bryanthedragon.mclibreloaded.permissions.McLibPermissions;
import bryanthedragon.mclibreloaded.config.ConfigBuilder;
import bryanthedragon.mclibreloaded.config.values.ValueBoolean;
import bryanthedragon.mclibreloaded.config.values.ValueInt;
import bryanthedragon.mclibreloaded.config.values.ValueRL;
import bryanthedragon.mclibreloaded.events.RegisterConfigEvent;
import bryanthedragon.mclibreloaded.math.IValue;
import bryanthedragon.mclibreloaded.math.MathBuilder;
import bryanthedragon.mclibreloaded.math.Operation;
import bryanthedragon.mclibreloaded.math.Operator;
import bryanthedragon.mclibreloaded.math.Variable;
import bryanthedragon.mclibreloaded.permissions.PermissionCategory;
import bryanthedragon.mclibreloaded.permissions.PermissionFactory;
import bryanthedragon.mclibreloaded.utils.ColorUtils;
import bryanthedragon.mclibreloaded.utils.PayloadASM;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.util.Map;

/**
 * McLib mod
 *
 * All it does is provides common code for McHorse's mods.
 */
@Mod(McLibReloaded.MOD_ID)
public class McLibReloaded
{
    public static final String MOD_ID = "mclib";
    public static final String VERSION = "%VERSION%";

    /* Proxies */
    public static final String CLIENT_PROXY = "bryanthedragon.mclibreloaded.ClientProxy";
    public static final String SERVER_PROXY = "bryanthedragon.mclibreloaded.CommonProxy";

    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = SERVER_PROXY)
    public static CommonProxy proxy;

    public static final EventBus EVENT_BUS = new EventBus();

    public static final Logger LOGGER = LogManager.getLogger(McLibReloaded.MOD_ID);

    public static L10n l10n = new L10n(MOD_ID);

    /**
     * A factory containing all permissions that have been registered through the {@link RegisterPermissionsEvent}.
     */
    public static final PermissionFactory permissionFactory = new PermissionFactory();

    /* Configuration */
    public static ValueBoolean opDropItems;

    public static ValueBoolean debugPanel;
    public static ValueColors favoriteColors;
    public static ValueInt primaryColor;
    public static ValueBoolean enableBorders;
    public static ValueBoolean enableCheckboxRendering;
    public static ValueBoolean enableTrackpadIncrements;
    public static ValueBoolean enableGridRendering;
    public static ValueInt userIntefaceScale;
    public static ValueInt tooltipStyle;
    public static ValueInt trackpadDecimalPlaces;
    public static ValueBoolean renderTranslateTextColors;

    public static ValueBoolean enableCursorRendering;
    public static ValueBoolean enableMouseButtonRendering;
    public static ValueBoolean enableKeystrokeRendering;
    public static ValueInt keystrokeOffset;
    public static ValueInt keystrokeMode;

    public static ValueRL backgroundImage;
    public static ValueInt backgroundColor;

    public static ValueBoolean scrollbarFlat;
    public static ValueInt scrollbarShadow;
    public static ValueInt scrollbarWidth;

    public static ValueBoolean multiskinMultiThreaded;
    public static ValueBoolean multiskinClear;

    public static ValueInt maxPacketSize;

    @SubscribeEvent
    public void onConfigRegister(RegisterConfigEvent event)
    {
        opDropItems = event.opAccess.category(MOD_ID).getBoolean("drop_items", true);

        /* McLib's options */
        ConfigBuilder builder = event.createBuilder(MOD_ID);

        /* Appearance category */
        debugPanel = builder.category("appearance").getBoolean("debug_panel", false);
        debugPanel.invisible();
        primaryColor = builder.getInt("primary_color", 0x0088ff).color();
        enableBorders = builder.getBoolean("enable_borders", false);
        enableCheckboxRendering = builder.getBoolean("enable_checkbox_rendering", false);
        enableTrackpadIncrements = builder.getBoolean("enable_trackpad_increments", true);
        trackpadDecimalPlaces = builder.getInt("trackpad_decimal_places", 6, 3, 31);
        enableGridRendering = builder.getBoolean("enable_grid_rendering", true);
        userIntefaceScale = builder.getInt("user_interface_scale", 2, 0, 4);
        tooltipStyle = builder.getInt("tooltip_style", 1).modes((IKey) IKey.lang("mclib.tooltip_style.light"), (IKey) IKey.lang("mclib.tooltip_style.dark")
        );
        renderTranslateTextColors = builder.getBoolean("render_translation_text_colours", false);

        favoriteColors = new ValueColors("favorite_colors");
        builder.register(favoriteColors);

        builder.getCategory().markClientSide();

        /* Tutorials category */
        enableCursorRendering = builder.category("tutorials").getBoolean("enable_mouse_rendering", false);
        enableMouseButtonRendering = builder.getBoolean("enable_mouse_buttons_rendering", false);
        enableKeystrokeRendering = builder.getBoolean("enable_keystrokes_rendering", false);
        keystrokeOffset = builder.getInt("keystroke_offset", 10, 0, 20);
        keystrokeMode = builder.getInt("keystroke_position", 1).modes((IKey) IKey.lang("mclibreload.keystrokes_position.auto"), (IKey) IKey.lang("mclibreload.keystrokes_position.bottom_left"), (IKey) IKey.lang("mclibreload.keystrokes_position.bottom_right"), (IKey) IKey.lang("mclibreload.keystrokes_position.top_right"), (IKey) IKey.lang("mclibreload.keystrokes_position.top_left"));

        builder.getCategory().markClientSide();

        /* Background category */
        backgroundImage = builder.category("background").getRL("image",  null);
        backgroundColor = builder.getInt("color",  0xcc000000).colorAlpha();

        builder.getCategory().markClientSide();

        /* Scrollbars category */
        scrollbarFlat = builder.category("scrollbars").getBoolean("flat", false);
        scrollbarShadow = builder.getInt("shadow", ColorUtils.HALF_BLACK).colorAlpha();
        scrollbarWidth = builder.getInt("width", 4, 2, 10);

        builder.getCategory().markClientSide();

        /* Multiskin category */
        multiskinMultiThreaded = builder.category("multiskin").getBoolean("multithreaded", true);
        multiskinClear = builder.getBoolean("clear", true);

        builder.getCategory().markClientSide();

        /* Vanilla category */
        maxPacketSize = builder.category("vanilla").getInt("max_packet_size", PayloadASM.MIN_SIZE, PayloadASM.MIN_SIZE, Integer.MAX_VALUE / 4);
        maxPacketSize.syncable();
    }

    @SubscribeEvent
    public void onPermissionRegister(RegisterPermissionsEvent event)
    {
        event.registerMod(MOD_ID, DefaultPermissionLevel.OP);

        event.registerPermission(McLibPermissions.configEdit = new PermissionCategory("edit_config"));

        event.registerCategory(new PermissionCategory("gui"));
        event.registerPermission(McLibPermissions.accessGui = new PermissionCategory("access_gui"));

        event.endMod();
    }

    @SubscribeEvent



    @NetworkCheckHandler
    public boolean checkModDependencies(Map<String, String> map, Side side) {
        return true;
    }

    public static void main(String[] args) throws Exception
    {
        Operator.DEBUG = true;
        MathBuilder builder = new MathBuilder();

        test(builder, "1 - 2 * 3 + 4 ", 1 - 2 * 3 + 4  );
        test(builder, "2 * 3 - 8 + 7 ", 2 * 3 - 8 + 7  );
        test(builder, "3 - 7 + 2 * 4 ", 3 - 7 + 2 * 4  );
        test(builder, "8 / 4 - 3 * 10", 8 / 4 - 3 * 10 );
        test(builder, "2 - 4 * 5 / 8 ", 2 - 4 * 5 / 8D );
        test(builder, "3 / 4 * 8 - 10", 3 / 4D * 8 - 10);
        test(builder, "2 * 3 / 4 * 5 ", 2D * 3 / 4 * 5 );
        test(builder, "2 + 3 - 4 + 5 ", 2 + 3 - 4 + 5  );
        test(builder, "7 - 2 ^ 4 - 4 * 5 + 15 ^ 2", 7 - Math.pow(2, 4) - 4 * 5 + Math.pow(15, 2));
        test(builder, "5 -(10 + 20)", 5 -(10 + 20));
        test(builder, "1 << 4 - 1", 1 << 4 - 1);
        test(builder, "256 >> 4 + 2", 256 >> 4 + 2);
        test(builder, "255 & 7 + 1", 255 & 7 + 1);
        test(builder, "256 | 7 + 1", 256 | 7 + 1);
        test(builder, "5 % 2 + 1 == 0 * 2", 5 % 2 + 1 == 0 * 2 ? 1 : 0);

        builder.variables.put("abc", new Variable("abc", 1));
        IValue test = builder.parse("- (40 + 2) / -2");

        System.out.println(test.isNumber() + " " + test.stringValue() + " " + test.booleanValue() + " " + test.doubleValue());
    }

    public static void test(MathBuilder builder, String expression, double result) throws Exception
    {
        IValue value = builder.parse(expression);

        System.out.println(expression + " = " + value.get() + " (" + result + ") is " + Operation.equals(value.get().doubleValue(), result));
        System.out.println(value.toString() + "\n");
    }
}