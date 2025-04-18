package net.minecraft.client.renderer.entity.state;

import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.CrashReportCategory;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EntityRenderState {
    public EntityType<?> entityType;
    public double x;
    public double y;
    public double z;
    public float ageInTicks;
    public float boundingBoxWidth;
    public float boundingBoxHeight;
    public float eyeHeight;
    public double distanceToCameraSq;
    public boolean isInvisible;
    public boolean isDiscrete;
    public boolean displayFireAnimation;
    @Nullable
    public Vec3 passengerOffset;
    @Nullable
    public Component nameTag;
    @Nullable
    public Vec3 nameTagAttachment;
    @Nullable
    public EntityRenderState.LeashState leashState;
    @Nullable
    public HitboxesRenderState hitboxesRenderState;
    @Nullable
    public ServerHitboxesRenderState serverHitboxesRenderState;

    public void fillCrashReportCategory(CrashReportCategory p_392281_) {
        p_392281_.setDetail("EntityRenderState", this.getClass().getCanonicalName());
        p_392281_.setDetail("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.x, this.y, this.z));
    }

    @OnlyIn(Dist.CLIENT)
    public static class LeashState {
        public Vec3 offset = Vec3.ZERO;
        public Vec3 start = Vec3.ZERO;
        public Vec3 end = Vec3.ZERO;
        public int startBlockLight = 0;
        public int endBlockLight = 0;
        public int startSkyLight = 15;
        public int endSkyLight = 15;
    }
}