package bryanthedragon.mclibreloaded.utils;

import bryanthedragon.mclibreloaded.client.render.VertexBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;
import javax.vecmath.*;
import java.nio.FloatBuffer;

public class RenderingUtils
{
    private static final FloatBuffer matrixBuffer = BufferUtils.createFloatBuffer(16);

    /**
     * This method inverts the scale and rotation of the modelview matrix and
     * multiplies it on the OpenGL stack.
     */
    public static void glRevertRotationScale()
    {
        matrixBuffer.clear();

        MatrixUtils.matrixToFloatBuffer(matrixBuffer, getRevertRotationScale());

        GL11.glMultMatrix(matrixBuffer);
    }

    public static Matrix4f getRevertRotationScale()
    {
        Matrix4d[] transformation = MatrixUtils.getTransformation();

        Matrix4d invertRotScale = new Matrix4d();

        invertRotScale.setIdentity();

        invertRotScale.m00 = ((transformation[2].m00 != 0) ? 1 / transformation[2].m00 : 0);
        invertRotScale.m11 = ((transformation[2].m11 != 0) ? 1 / transformation[2].m11 : 0);
        invertRotScale.m22 = ((transformation[2].m22 != 0) ? 1 / transformation[2].m22 : 0);

        try
        {
            transformation[1].invert();
        }
        catch (SingularMatrixException e)
        { }

        invertRotScale.mul(transformation[1], invertRotScale);

        return new Matrix4f(invertRotScale);
    }


    /**
     * This method inverts the scale and rotation using the provided parameters
     * and multiplies the OpenGL matrix stack with it.
     * @param scale
     * @param rotation angles in radians
     * @param rotationOrder the order of the rotation
     */
    public static void glRevertRotationScale(Vector3d rotation, Vector3d scale, MatrixUtils.RotationOrder rotationOrder)
    {
        double invSx = (scale.x != 0) ? 1 / scale.x : 0;
        double invSy = (scale.y != 0) ? 1 / scale.y : 0;
        double invSz = (scale.z != 0) ? 1 / scale.z : 0;

        GlStateManager.scale(invSx, invSy, invSz);

        float rotx = (float) -Math.toDegrees(rotation.x);
        float roty = (float) -Math.toDegrees(rotation.y);
        float rotz = (float) -Math.toDegrees(rotation.z);

        switch (rotationOrder)
        {
            case ZYX:
                GlStateManager.rotate(rotz, 0, 0, 1);
                GlStateManager.rotate(roty, 0, 1, 0);
                GlStateManager.rotate(rotx, 1, 0, 0);

                break;
            case XYZ:
                GlStateManager.rotate(rotx, 1, 0, 0);
                GlStateManager.rotate(roty, 0, 1, 0);
                GlStateManager.rotate(rotz, 0, 0, 1);

                break;
            case XZY:
                GlStateManager.rotate(rotx, 1, 0, 0);
                GlStateManager.rotate(rotz, 0, 0, 1);
                GlStateManager.rotate(roty, 0, 1, 0);

                break;
            case YZX:
                GlStateManager.rotate(roty, 0, 1, 0);
                GlStateManager.rotate(rotz, 0, 0, 1);
                GlStateManager.rotate(rotx, 1, 0, 0);

                break;
            case YXZ:
                GlStateManager.rotate(roty, 0, 1, 0);
                GlStateManager.rotate(rotx, 1, 0, 0);
                GlStateManager.rotate(rotz, 0, 0, 1);

                break;
            case ZXY:
                GlStateManager.rotate(rotz, 0, 0, 1);
                GlStateManager.rotate(rotx, 1, 0, 0);
                GlStateManager.rotate(roty, 0, 1, 0);

                break;
        }

    }

    public static void renderCircle(Vector3d center, Vector3d normal, float radius, int divisions, Color color, float thickness)
    {
        renderCircleDotted(center, normal, radius, divisions, color, thickness, 0);
    }

    public static void renderCircleDotted(Vector3d center, Vector3d normal, float radius, int divisions, Color color, float thickness, int skipDivision)
    {
        Matrix3d rotation = new Matrix3d();
        rotation.setIdentity();
        Matrix3d transform = new Matrix3d();
        transform.rotY(Math.toRadians(getYaw(normal)));
        rotation.mul(transform);
        transform.rotX(Math.toRadians(getPitch(normal)));
        rotation.mul(transform);

        GL11.glColor4f(color.r, color.g, color.b, color.a);
        GL11.glLineWidth(thickness);
        GL11.glBegin(GL11.GL_LINES);

        for (int i = 1; i <= divisions; i += skipDivision + 1)
        {
            double angle0 = 2 * Math.PI / divisions * (i - 1);
            double angle1 = 2 * Math.PI / divisions * i;

            Vector3d a = new Vector3d(radius * Math.cos(angle0), radius * Math.sin(angle0), 0);
            Vector3d b = new Vector3d(radius * Math.cos(angle1), radius * Math.sin(angle1), 0);
            rotation.transform(a);
            rotation.transform(b);
            a.add(center);
            b.add(center);

            GL11.glVertex3d(a.x, a.y, a.z);
            GL11.glVertex3d(b.x, b.y, b.z);
        }

        GL11.glEnd();
    }

    public static float getYaw(Vector3f direction)
    {
        return (float) getYaw(new Vector3d(direction));
    }

    public static float getPitch(Vector3f direction)
    {
        return (float) getPitch(new Vector3d(direction));
    }

    public static double getYaw(Vector3d direction)
    {
        double yaw = Math.atan2(-direction.x, direction.z);
        yaw = Math.toDegrees(yaw);
        if (yaw < -180)
        {
            yaw += 360;
        }
        else if (yaw > 180)
        {
            yaw -= 360;
        }
        return -yaw;
    }

    public static double getPitch(Vector3d direction)
    {
        double pitch = Math.atan2(direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z));
        return -Math.toDegrees(pitch);
    }

    public static void renderImage(ResourceLocation image, float scale)
    {
        renderImage(image, scale, new Color(1F, 1F, 1F, 1F));
    }

    public static void renderImage(ResourceLocation image, float scale, Color color)
    {
        Minecraft.getInstance().renderEngine.bindTexture(image);

        boolean isCulling = GL11.glIsEnabled(GL11.GL_CULL_FACE);

        GlStateManager.alphaFunc(GL11.GL_GREATER, 0);
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();

        if (ReflectionUtils.isOptifineShadowPass())
        {
            GlStateManager.disableCull();
        }
        else
        {
            GlStateManager.enableCull();
        }

        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        GlStateManager.color(color.r, color.g, color.b, color.a);

        buffer.begin(GL11.GL_QUADS, VertexBuilder.getFormat(false, true, false, true));

        int perspective = Minecraft.getInstance().gameSettings.thirdPersonView;
        float width = scale * (perspective == 2 ? -1 : 1) * 0.5F;
        float height = scale * 0.5F;

        /* Frontface */
        buffer.pos(-width, height, 0.0F).tex(0, 0).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(-width, -height, 0.0F).tex(0, 1).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(width, -height, 0.0F).tex(1, 1).normal(0.0F, 0.0F, 1.0F).endVertex();
        buffer.pos(width, height, 0.0F).tex(1, 0).normal(0.0F, 0.0F, 1.0F).endVertex();
        /* backface */
        buffer.pos(width,height, 0.0F).tex(1, 0).normal(0.0F, 0.0F, -1.0F).endVertex();
        buffer.pos(width, -height, 0.0F).tex(1, 1).normal(0.0F, 0.0F, -1.0F).endVertex();
        buffer.pos(-width, -height, 0.0F).tex(0, 1).normal(0.0F, 0.0F, -1.0F).endVertex();
        buffer.pos(-width, height, 0.0F).tex(0, 0).normal(0.0F, 0.0F, -1.0F).endVertex();

        tessellator.draw();

        if (isCulling)
        {
            GlStateManager.enableCull();
        }
        else
        {
            GlStateManager.disableCull();
        }

        GlStateManager.disableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.alphaFunc(GL11.GL_GREATER, 0.1F);
    }

    public static Matrix4f getFacingRotation(Facing facing, Vector3f position)
    {
        return getFacingRotation(facing, position, null);
    }

    public static Matrix4f getFacingRotation(Facing facing, Vector3f position, Vector3f direction)
    {
        if (facing.isDirection && direction == null)
        {
            throw new IllegalArgumentException("Argument direction cannot be null when the facing mode has isDirection=true");
        }

        Entity camera = Minecraft.getInstance().getRenderViewEntity();
        float partialTicks = Minecraft.getInstance().getRenderPartialTicks();
        Matrix4f transform = new Matrix4f();
        Matrix4f rotation = new Matrix4f();

        transform.setIdentity();

        float cYaw = - Interpolations.lerp(camera.prevRotationYaw, camera.rotationYaw, partialTicks);
        float cPitch = Interpolations.lerp(camera.prevRotationPitch, camera.rotationPitch, partialTicks);
        double cX = Interpolations.lerp(camera.prevPosX, camera.posX, partialTicks);
        double cY = Interpolations.lerp(camera.prevPosY, camera.posY, partialTicks) + camera.getEyeHeight();
        double cZ = Interpolations.lerp(camera.prevPosZ, camera.posZ, partialTicks);

        if (facing.isLookAt && !facing.isDirection)
        {
            double dX = cX - position.x;
            double dY = cY - position.y;
            double dZ = cZ - position.z;
            double horizontalDistance = MathHelper.sqrt(dX * dX + dZ * dZ);

            cYaw = 180 - (float) (Math.toDegrees(MathHelper.atan2(dZ, dX)) - 90.0F);
            cPitch = (float) (Math.toDegrees(MathHelper.atan2(dY, horizontalDistance)));
        }

        if (facing.isDirection)
        {
            double lengthSq = direction.lengthSquared();
            if (lengthSq < 0.0001)
            {
                direction.set(1, 0, 0);
            }
            else if (Math.abs(lengthSq - 1) > 0.0001)
            {
                direction.normalize();
            }
        }

        switch (facing)
        {
            case LOOKAT_XYZ:
            case ROTATE_XYZ:
                rotation.rotX((float) Math.toRadians(cPitch));
                transform.mul(rotation);
                rotation.rotY((float) Math.toRadians(180 - cYaw));
                transform.mul(rotation);
                break;
            case ROTATE_Y:
            case LOOKAT_Y:
                rotation.rotY((float) Math.toRadians(180 - cYaw));
                transform.mul(rotation);
                break;
            case LOOKAT_DIRECTION:
                rotation.setIdentity();
                rotation.rotY((float) Math.toRadians(getYaw(direction)));
                transform.mul(rotation);
                rotation.rotX((float) Math.toRadians(getPitch(direction) + 90));
                transform.mul(rotation);

                Vector3f cameraDir = new Vector3f(
                        (float) (cX - position.x),
                        (float) (cY - position.y),
                        (float) (cZ - position.z));

                Vector3f rotatedNormal = new Vector3f(0,0,1);

                transform.transform(rotatedNormal);

                /*
                 * The direction vector is the normal of the plane used for calculating the rotation around local y Axis.
                 * Project the cameraDir onto that plane to find out the axis angle (direction vector is the y axis).
                 */
                Vector3f projectDir = new Vector3f(direction);
                projectDir.scale(cameraDir.dot(direction));
                cameraDir.sub(projectDir);

                if (cameraDir.lengthSquared() < 1.0e-30) break;

                cameraDir.normalize();

                /*
                 * The angle between two vectors is only between 0 and 180 degrees.
                 * RotationDirection will be parallel to direction but pointing in different directions depending
                 * on the rotation of cameraDir. Use this to find out the sign of the angle
                 * between cameraDir and the rotatedNormal.
                 */
                Vector3f rotationDirection = new Vector3f();
                rotationDirection.cross(cameraDir, rotatedNormal);

                rotation.rotY(-Math.copySign(cameraDir.angle(rotatedNormal), rotationDirection.dot(direction)));
                transform.mul(rotation);
                break;
        }

        return transform;
    }

    /**
     * This method multiples the openGL matrix stack with a rotation matrix
     * according to the facing parameter
     */
    public static void glFacingRotation(Facing facing, Vector3f position, Vector3f direction)
    {
        matrixBuffer.clear();
        MatrixUtils.matrixToFloatBuffer(matrixBuffer, getFacingRotation(facing, position, direction));

        GL11.glMultMatrix(matrixBuffer);
    }

    public static void glFacingRotation(Facing facing, Vector3f position)
    {
        glFacingRotation(facing, position, null);
    }

    public enum Facing
    {
        ROTATE_XYZ("rotate_xyz"),
        ROTATE_Y("rotate_y"),
        LOOKAT_XYZ("lookat_xyz", true, false),
        LOOKAT_Y("lookat_y", true, false),
        LOOKAT_DIRECTION("lookat_direction", true, true);


        public final String id;
        public final boolean isLookAt;
        public final boolean isDirection;

        public static Facing fromString(String string)
        {
            for (Facing facing : values())
            {
                if (facing.id.equals(string))
                {
                    return facing;
                }
            }

            return null;
        }

        Facing(String id, boolean isLookAt, boolean isDirection)
        {
            this.id = id;
            this.isLookAt = isLookAt;
            this.isDirection = isDirection;
        }

        Facing(String id)
        {
            this(id, false, false);
        }
    }
}
