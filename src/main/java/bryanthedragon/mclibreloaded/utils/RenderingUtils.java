package bryanthedragon.mclibreloaded.utils;

import net.minecraft.client.CameraType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

import org.joml.Matrix3d;
import org.joml.Matrix4d;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.mojang.blaze3d.opengl.GlStateManager;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;

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

        MatrixUtils.matrixToFloatBuffer(matrixBuffer, getRevertRotationScaler());

        GL11.glMultMatrixf(matrixBuffer);
    }

    public static Matrix4f getRevertRotationScaler()
    {
        Matrix4d[] transformation = MatrixUtils.getTransformation();

        Matrix4d invertRotScale = new Matrix4d();

        invertRotScale.identity();

        invertRotScale.set(0, 0, ((transformation[2].get(0, 0) != 0) ? 1 / transformation[2].get(0, 0) : 0));
        invertRotScale.set(1, 1, ((transformation[2].get(1, 1) != 0) ? 1 / transformation[2].get(1, 1) : 0));
        invertRotScale.set(2, 2, ((transformation[2].get(2, 2) != 0) ? 1 / transformation[2].get(2, 2) : 0));

        try
        {
            transformation[1].invert();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

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

        GL11.glScalef((float) invSx, (float) invSy, (float) invSz);

        float rotx = (float) -Math.toDegrees(rotation.x);
        float roty = (float) -Math.toDegrees(rotation.y);
        float rotz = (float) -Math.toDegrees(rotation.z);

        switch (rotationOrder)
        {
            case ZYX:
                GL11.glRotatef(rotz, 0, 0, 1);
                GL11.glRotatef(roty, 0, 1, 0);
                GL11.glRotatef(rotx, 1, 0, 0);

                break;
            case XYZ:
                GL11.glRotatef(rotx, 1, 0, 0);
                GL11.glRotatef(roty, 0, 1, 0);
                GL11.glRotatef(rotz, 0, 0, 1);

                break;
            case XZY:
                GL11.glRotatef(rotx, 1, 0, 0);
                GL11.glRotatef(rotz, 0, 0, 1);
                GL11.glRotatef(roty, 0, 1, 0);

                break;
            case YZX:
                GL11.glRotatef(roty, 0, 1, 0);
                GL11.glRotatef(rotz, 0, 0, 1);
                GL11.glRotatef(rotx, 1, 0, 0);

                break;
            case YXZ:
                GL11.glRotatef(roty, 0, 1, 0);
                GL11.glRotatef(rotx, 1, 0, 0);
                GL11.glRotatef(rotz, 0, 0, 1);

                break;
            case ZXY:
                GL11.glRotatef(rotz, 0, 0, 1);
                GL11.glRotatef(rotx, 1, 0, 0);
                GL11.glRotatef(roty, 0, 1, 0);

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
        rotation.identity();
        Matrix3d transform = new Matrix3d();
        transform.rotateY(Math.toRadians(getYaw(normal)));
        rotation.mul(transform);
        transform.rotateX(Math.toRadians(getPitch(normal)));
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

    public static float YawGetter(Vector3f direction)
    {
        return (float) getYaw(new Vector3d(direction));
    }

    public static float PitchGetter(Vector3f direction)
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

    public static void rendImager(ResourceLocation image, float scale)
    {
        renderImage(image, null, image, scale, new Color(1F, 1F, 1F, 1F));
    }

    @SuppressWarnings("deprecation")
    public static void renderImage(ResourceLocation image, MultiBufferSource bufferSource, ResourceLocation texture, float scale, Color color)
    {

        PoseStack poseStack = new PoseStack();
        Minecraft.getInstance().getTextureManager().getTexture(image);

        boolean isCulling = GL11.glIsEnabled(GL11.GL_CULL_FACE);

        GL11.glAlphaFunc(GL11.GL_GREATER, 0);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);

        if (ReflectionUtils.isOptifineShadowPass())
        {
            GlStateManager._disableCull();
        }
        else
        {
            GlStateManager._enableCull();
        }

        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        VertexConsumer buffer = bufferSource.getBuffer(RenderType.entityTranslucent(texture));
        Matrix4f matrix = poseStack.last().pose();

        GL11.glColor4f(color.r, color.g, color.b, color.a);

        int perspective = (Minecraft.getInstance().options.getCameraType() == CameraType.THIRD_PERSON_BACK || Minecraft.getInstance().options.getCameraType() == CameraType.THIRD_PERSON_FRONT) ? 2 : 1;
        float width = scale * (perspective == 2 ? -1 : 1) * 0.5F;
        float height = scale * 0.5F;

        int packedLight = LightTexture.pack(15, 15);
        /* Frontface */
        buffer.addVertex(matrix, -width,  height, 0).setColor(color.r, color.g, color.b, color.a).setUv(0, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight);
        buffer.addVertex(matrix, -width, -height, 0).setColor(color.r, color.g, color.b, color.a).setUv(0, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight);

        /* Backface */
        buffer.addVertex(matrix,  width, -height, 0).setColor(color.r, color.g, color.b, color.a).setUv(1, 1).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight);
        buffer.addVertex(matrix,  width,  height, 0).setColor(color.r, color.g, color.b, color.a).setUv(1, 0).setOverlay(OverlayTexture.NO_OVERLAY).setLight(packedLight);

        if (isCulling)
        {
            GlStateManager._enableCull();
        }
        else
        {
            GlStateManager._disableCull();
        }

        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.1F);
    }

    public static Matrix4f FacingRotationGetter(Facing facing, Vector3f position, Vector3f direction)
    {
        return getFacingRotation(facing, position, null, 0);
    }

    public static Matrix4f getFacingRotation(Facing facing, Vector3f position, Vector3f direction, float partialTicks)
    {
        if (facing.isDirection && direction == null)
        {
            throw new IllegalArgumentException("Argument direction cannot be null when the facing mode has isDirection=true");
        }

        Entity camera = Minecraft.getInstance().getCameraEntity();
        if (camera == null)
        {
            return new Matrix4f();
        }
        Matrix4f transform = new Matrix4f();
        Matrix4f rotation = new Matrix4f();

        transform.identity();

        float cYaw = - Interpolations.lerp(camera.yRotO, camera.getYRot(), partialTicks);
        float cPitch = Interpolations.lerp(camera.xRotO, camera.getXRot(), partialTicks);
        double cX = Interpolations.lerp(camera.xOld, camera.getX(), partialTicks);
        double cY = Interpolations.lerp(camera.yOld, camera.getY(), partialTicks) + camera.getEyeHeight();
        double cZ = Interpolations.lerp(camera.zOld, camera.getZ(), partialTicks);

        if (facing.isLookAt && !facing.isDirection)
        {
            double dX = cX - position.x;
            double dY = cY - position.y;
            double dZ = cZ - position.z;
            double horizontalDistance = Math.sqrt(dX * dX + dZ * dZ);
            cYaw = 180 - (float) (Math.toDegrees(Math.atan2(dZ, dX)) - 90.0F);
            cPitch = (float) (Math.toDegrees(Math.atan2(dY, horizontalDistance)));
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
                rotation.rotateX((float) Math.toRadians(cPitch));
                transform.mul(rotation);
                rotation.rotateY((float) Math.toRadians(180 - cYaw));
                transform.mul(rotation);
                break;
            case ROTATE_Y:
            case LOOKAT_Y:
                rotation.rotateY((float) Math.toRadians(180 - cYaw));
                transform.mul(rotation);
                break;
            case LOOKAT_DIRECTION:
                rotation.identity();
                rotation.rotateY((float) Math.toRadians(YawGetter(direction)));
                transform.mul(rotation);
                rotation.rotateX((float) Math.toRadians(PitchGetter(direction) + 90));
                transform.mul(rotation);

                Vector3f cameraDir = new Vector3f((float) (cX - position.x), (float) (cY - position.y), (float) (cZ - position.z));

                Vector3f rotatedNormal = new Vector3f(0,0,1);

                transform.transformPosition(rotatedNormal);

                /*
                 * The direction vector is the normal of the plane used for calculating the rotation around local y Axis.
                 * Project the cameraDir onto that plane to find out the axis angle (direction vector is the y axis).
                 */
                Vector3f projectDir = new Vector3f(direction);
                projectDir.mul(cameraDir.dot(direction));
                cameraDir.sub(projectDir);

                if (cameraDir.lengthSquared() < 1.0e-30) 
                {
                    break;
                }

                cameraDir.normalize();

                /*
                 * The angle between two vectors is only between 0 and 180 degrees.
                 * RotationDirection will be parallel to direction but pointing in different directions depending
                 * on the rotation of cameraDir. Use this to find out the sign of the angle
                 * between cameraDir and the rotatedNormal.
                 */
                Vector3f rotationDirection = new Vector3f();
                rotationDirection.cross(cameraDir, rotatedNormal);

                rotation.rotateY(-Math.copySign(cameraDir.angle(rotatedNormal), rotationDirection.dot(direction)));
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
        MatrixUtils.matrixToFloatBuffer(matrixBuffer, FacingRotationGetter(facing, position, direction));

        GL11.glMultMatrixf(matrixBuffer);
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
