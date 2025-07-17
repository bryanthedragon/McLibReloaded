package bryanthedragon.mclibreloaded.client.gui.framework.elements.utils;

import bryanthedragon.mclibreloaded.client.gui.framework.elements.GuiElement;
import bryanthedragon.mclibreloaded.client.gui.utils.Area;
import bryanthedragon.mclibreloaded.client.gui.utils.Icons;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;

import org.lwjgl.opengl.GL11;

public abstract class GuiCanvasEditor extends GuiCanvas
{
    private static Area processed = new Area();

    public GuiElement editor;

    /* Width and height of the frame that being currently edited */
    protected int w;
    protected int h;

    public GuiCanvasEditor(Minecraft mc)
    {
        super(mc);

        this.editor = new GuiElement(mc);
        this.editor.flex().relative(this).xy(1F, 1F).w(130).anchor(1F, 1F).column(5).stretch().vertical().padding(10);
        this.add(this.editor);
    }

    public int getWidth()
    {
        return this.w;
    }

    public int getHeight()
    {
        return this.h;
    }

    public void setSize(int w, int h)
    {
        this.w = w;
        this.h = h;

        this.scaleX.set(0, 2);
        this.scaleY.set(0, 2);
        this.scaleX.viewOffset(-this.w / 2, this.w / 2, 20);
        this.scaleY.viewOffset(-this.h / 2, this.h / 2, 20);

        double min = Math.min(this.scaleX.getZoom(), this.scaleY.getZoom());

        this.scaleX.setZoom(min);
        this.scaleY.setZoom(min);
    }

    @Override
    protected void drawCanvas(GuiContext context)
    {
        this.drawBackground(context);

        Area area = this.calculate(-this.w / 2, -this.h / 2, this.w / 2, this.h / 2);

        Gui.drawRect(area.x - 1, area.y - 1, area.ex() + 1, area.ey() + 1, 0xff181818);
        RenderSystem.color(1, 1, 1, 1);

        if (!this.shouldDrawCanvas(context))
        {
            return;
        }

        GuiDraw.scissor(area.x, area.y, area.w, area.h, context);

        int ox = (this.area.x - area.x) % 16;
        int oy = (this.area.y - area.y) % 16;

        processed.copy(this.area);
        processed.offsetX(ox < 0 ? 16 + ox : ox);
        processed.offsetY(oy < 0 ? 16 + oy : oy);
        processed.clamp(area);
        Icons.CHECKBOARD.renderArea(area.x, area.y, area.w, area.h);

        RenderSystem.alphaFunc(GL11.GL_GREATER, 0);
        RenderSystem.enableBlend();
        RenderSystem.enableAlpha();

        this.drawCanvasFrame(context);

        RenderSystem.color(1F, 1F, 1F);
        RenderSystem.alphaFunc(GL11.GL_GREATER, 0.1F);
        GuiDraw.unscissor(context);
    }

    protected void drawBackground(GuiContext context)
    {
        this.area.draw(0xff2f2f2f);
    }

    protected abstract void drawCanvasFrame(GuiContext context);

    protected boolean shouldDrawCanvas(GuiContext context)
    {
        return true;
    }

    protected Area calculateRelative(int a, int b, int c, int d)
    {
        return this.calculate(-this.w / 2 + a, -this.h / 2 + b, -this.w / 2 + c, -this.h / 2 + d);
    }

    protected Area calculate(int ix1, int iy1, int ix2, int iy2)
    {
        int x1 = this.toX(ix1);
        int y1 = this.toY(iy1);
        int x2 = this.toX(ix2);
        int y2 = this.toY(iy2);

        int x = x1;
        int y = y1;
        int fw = x2 - x;
        int fh = y2 - y;

        Area.SHARED.set(x, y, fw, fh);

        return Area.SHARED;
    }
}
