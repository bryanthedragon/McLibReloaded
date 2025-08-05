package bryanthedragon.mclibreloaded.client;

import bryanthedragon.mclibreloaded.McLibReloaded;
import bryanthedragon.mclibreloaded.client.gui.framework.elements.utils.GuiDraw;
import bryanthedragon.mclibreloaded.client.gui.utils.Icons;
import bryanthedragon.mclibreloaded.utils.ColorUtils;
import bryanthedragon.mclibreloaded.utils.Interpolation;
import bryanthedragon.mclibreloaded.utils.Keys;
import bryanthedragon.mclibreloaded.utils.MatrixUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Mouse renderer
 * 
 * This class is responsible for rendering a mouse pointer on the screen 
 */
@OnlyIn(Dist.CLIENT)
public class InputRenderer
{
    private static final PoseStack poseStack = new PoseStack();
    public static boolean disabledForFrame = false;
    private List<PressedKey> pressedKeys = new ArrayList<PressedKey>();
    private float lastQX = 1;
    private float lastQY = 0;
    private float currentQX = 0;
    private float currentQY = 1;
    private long lastDWheelTime;
    private int lastDWheelScroll;

    public static void disable()
    {
        disabledForFrame = true;
    }

    /**
     * Called by ASM
     */
    public static void preRenderOverlay()
    {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();
        setupOrthoProjection(width, height);
        // McLib.EVENT_BUS.post(new RenderOverlayEvent.Pre(mc, resolution));
    }

    /**
     * Called by ASM
     */
    public static void postRenderOverlay()
    {
        Minecraft mc = Minecraft.getInstance();
        int width = mc.getWindow().getGuiScaledWidth();
        int height = mc.getWindow().getGuiScaledHeight();
        setupOrthoProjection(width, height);
        // McLib.EVENT_BUS.post(new RenderOverlayEvent.Post(mc, resolution));
    }

    /* Shift -6 and -8 to get it into the center */
    public static void renderMouseButtons(int x, int y, int scroll, boolean left, boolean right, boolean middle, boolean isScrolling)
    {
        /* Outline */
        Gui.drawRect(x - 1, y, x + 13, y + 16, 0xff000000);
        Gui.drawRect(x, y - 1, x + 12, y + 17, 0xff000000);
        /* Background */
        Gui.drawRect(x, y + 1, x + 12, y + 15, 0xffffffff);
        Gui.drawRect(x + 1, y, x + 11, y + 1, 0xffffffff);
        Gui.drawRect(x + 1, y + 15, x + 11, y + 16, 0xffffffff);
        /* Over outline */
        Gui.drawRect(x, y + 7, x + 12, y + 8, 0xffeeeeee);

        if (left)
        {
            Gui.drawRect(x + 1, y, x + 6, y + 7, 0xffcccccc);
            Gui.drawRect(x, y + 1, x + 1, y + 7, 0xffaaaaaa);
        }

        if (right)
        {
            Gui.drawRect(x + 6, y, x + 11, y + 7, 0xffaaaaaa);
            Gui.drawRect(x + 11, y + 1, x + 12, y + 7, 0xff888888);
        }

        if (middle || isScrolling)
        {
            int offset = 0;

            if (isScrolling)
            {
                offset = scroll < 0 ? 1 : -1;
            }

            Gui.drawRect(x + 4, y, x + 8, y + 6, 0x20000000);
            Gui.drawRect(x + 5, y + 1 + offset, x + 7, y + 5 + offset, 0xff444444);
            Gui.drawRect(x + 5, y + 4 + offset, x + 7, y + 5 + offset, 0xff333333);
        }
    }

    public static void renderMouseWheel(int x, int y, int scroll, long current)
    {
        int color = McLibReloaded.primaryColor.get();

        GuiDraw.drawDropShadow(x, y, x + 4, y + 16, 2, ColorUtils.HALF_BLACK + color, color);
        Gui.drawRect(x, y, x + 4, y + 16, 0xff111111);
        Gui.drawRect(x + 1, y, x + 3, y + 15, 0xff2a2a2a);

        int offset = (int) ((current % 1000 / 50) % 4);

        if (scroll >= 0)
        {
            offset = 3 - offset;
        }

        for (int i = 0; i < 4; i++)
        {
            Gui.drawRect(x, y + offset, x + 4, y + offset + 1, 0x88555555);

            y += 4;
        }
    }

    private static void setupOrthoProjection(int width, int height)
    {
        poseStack.pushPose();                  // replaces pushMatrix
        poseStack.translate(0, 0, 1000);       // replaces translatef
        RenderSystem.setShaderColor(1, 1, 1, 1); // replaces color4f
        RenderSystem.enableBlend();
    }
    @SubscribeEvent
    public void onDrawEvent(DrawScreenEvent.Post event)
    {
        if (disabledForFrame)
        {
            disabledForFrame = false;

            return;
        }

        int x = event.getMouseX();
        int y = event.getMouseY();

        this.renderMouse(x, y);

        if (McLib.enableKeystrokeRendering.get())
        {
            this.renderKeys(event.getGui(), x, y);
        }
    }

    /**
     * Draw mouse cursor
     */
    private void renderMouse(int x, int y)
    {
        poseStack.pushPose();
        poseStack.translate(0, 0, 1000);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.enableBlend();

        if (McLib.enableCursorRendering.get())
        {
            Icons.CURSOR.render(x, y);
        }

        if (McLib.enableMouseButtonRendering.get())
        {
            long window = Minecraft.getInstance().getWindow().getWindow();

            boolean left = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;
            boolean right = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_RIGHT) == GLFW.GLFW_PRESS;
            boolean middle = GLFW.glfwGetMouseButton(window, GLFW.GLFW_MOUSE_BUTTON_MIDDLE) == GLFW.GLFW_PRESS;

            int scroll = this.lastDWheelScroll; // Use value set by your MouseScrollEvent handler
            long current = System.currentTimeMillis();
            boolean isScrolling = scroll != 0 || current - this.lastDWheelTime < 500;

            if (scroll != 0)
            {
                this.lastDWheelTime = current;
                this.lastDWheelScroll = scroll;
            }

            if (scroll == 0 && isScrolling)
            {
                scroll = this.lastDWheelScroll;
            }

            x += 16;
            y += 2;

            if (left || right || middle || isScrolling)
            {
                renderMouseButtons(x, y, scroll, left, right, middle, isScrolling);
            }

            if (isScrolling)
            {
                x += 16;

                renderMouseWheel(x, y, scroll, current);
            }
        }

        RenderSystem.disableBlend();
        poseStack.popPose(); // replaces popMatrix
    }

    /**
     * Render pressed key strokes
     */
    private void renderKeys(Screen screen, int mouseX, int mouseY)
    {
        float lqx = Math.round(mouseX / (float) screen.width);
        float lqy = Math.round(mouseY / (float) screen.height);
        int mode = McLib.keystrokeMode.get();

        if (lqx == this.currentQX && lqy == this.currentQY)
        {
            this.currentQX = this.lastQX;
            this.currentQY = this.lastQY;
        }

        if (mode == 1)
        {
            this.currentQX = 0;
            this.currentQY = 1;
        }
        else if (mode == 2)
        {
            this.currentQX = 1;
            this.currentQY = 1;
        }
        else if (mode == 3)
        {
            this.currentQX = 1;
            this.currentQY = 0;
        }
        else if (mode == 4)
        {
            this.currentQX = 0;
            this.currentQY = 0;
        }

        float qx = this.currentQX;
        float qy = this.currentQY;

        int fy = qy > 0.5F ? 1 : -1;
        int offset = McLib.keystrokeOffset.get();
        int mx = offset + (int) (qx * (screen.width - offset * 2));
        int my = offset + (int) (qy * (screen.height - 20 - offset * 2));

        FontRenderer font = Minecraft.getInstance().fontRenderer;
        Iterator<PressedKey> it = this.pressedKeys.iterator();

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

        while (it.hasNext())
        {
            PressedKey key = it.next();

            if (key.expired())
            {
                it.remove();
            }
            else
            {
                int x = mx + (qx < 0.5F ? key.x : -(key.x + key.width + 10));
                int y = my + (int) (Interpolation.EXP_INOUT.interpolate(0, 1, key.getFactor()) * 50 * fy) + (key.i % 2 == 0 ? -1 : 0);

                GuiDraw.drawDropShadow(x, y, x + 10 + key.width, y + 20, 4, 0x44000000, 0);
                Gui.drawRect(x, y, x + 10 + key.width, y + 20, 0xff000000 + McLibReloaded.primaryColor.get());
                font.drawStringWithShadow(key.getLabel(), x + 5, y + 6, 0xffffff);
            }
        }

        this.lastQX = lqx;
        this.lastQY = lqy;
    }

    @SubscribeEvent
    public void onKeyPressedInGUI(InputEvent.Key event)
    {
        // Optional: check screen
        if (Minecraft.getInstance().screen == null) return;

        if (event.getAction() == GLFW.GLFW_PRESS)
        {
            int key = event.getKey();
            if (key == GLFW.GLFW_KEY_UNKNOWN)
            {
                return;
            }
            // Then do your PressedKey logic here...
        }
    }

    /**
     * Release the matrix at the end of frame to avoid messing
     * up matrix capture even more
     */
    @SubscribeEvent
    public void onRenderLast(RenderWorldLastEvent event)
    {
        MatrixUtils.releaseMatrix();
    }

    /**
     * Information about pressed key strokes
     */
    @OnlyIn(Dist.CLIENT)
    public static class PressedKey
    {
        public static int INDEX = 0;

        public int key;
        public long time;
        public int x;

        public String name;
        public int width;
        public int i;
        public int times = 1;

        public PressedKey(int key, int x)
        {
            this.key = key;
            this.time = System.currentTimeMillis();
            this.x = x;

            this.name = Keys.getKeyName(key);
            this.width = Minecraft.getInstance().fontRenderer.getStringWidth(this.name);
            this.i = INDEX ++;
        }

        public float getFactor()
        {
            return (System.currentTimeMillis() - this.time - 500) / 1000F;
        }

        public boolean expired()
        {
            if (GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), key) == GLFW.GLFW_PRESS)
            {
                this.time = System.currentTimeMillis();
            }

            return System.currentTimeMillis() - this.time > 1500;
        }

        public String getLabel()
        {
            if (this.times > 1)
            {
                return this.name + " (" + this.times + ")";
            }

            return this.name;
        }

        public int increment()
        {
            int lastWidth = this.width;

            this.times ++;
            this.width = Minecraft.getInstance().fontRenderer.getStringWidth(this.getLabel());

            return this.width - lastWidth;
        }
    }
}