package net.minecraft.world.entity;

import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.EnchantmentEffectComponents;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class ExperienceOrb extends Entity {
    protected static final EntityDataAccessor<Integer> DATA_VALUE = SynchedEntityData.defineId(ExperienceOrb.class, EntityDataSerializers.INT);
    private static final int LIFETIME = 6000;
    private static final int ENTITY_SCAN_PERIOD = 20;
    private static final int MAX_FOLLOW_DIST = 8;
    private static final int ORB_GROUPS_PER_AREA = 40;
    private static final double ORB_MERGE_DISTANCE = 0.5;
    private static final short DEFAULT_HEALTH = 5;
    private static final short DEFAULT_AGE = 0;
    private static final short DEFAULT_VALUE = 0;
    private static final int DEFAULT_COUNT = 1;
    private int age = 0;
    private int health = 5;
    private int count = 1;
    @Nullable
    private Player followingPlayer;
    private final InterpolationHandler interpolation = new InterpolationHandler(this);

    public ExperienceOrb(Level p_20776_, double p_20777_, double p_20778_, double p_20779_, int p_20780_) {
        this(EntityType.EXPERIENCE_ORB, p_20776_);
        this.setPos(p_20777_, p_20778_, p_20779_);
        if (!this.level().isClientSide) {
            this.setYRot((float)(this.random.nextDouble() * 360.0));
            this.setDeltaMovement(
                (this.random.nextDouble() * 0.2F - 0.1F) * 2.0, this.random.nextDouble() * 0.2 * 2.0, (this.random.nextDouble() * 0.2F - 0.1F) * 2.0
            );
        }

        this.setValue(p_20780_);
    }

    public ExperienceOrb(EntityType<? extends ExperienceOrb> p_20773_, Level p_20774_) {
        super(p_20773_, p_20774_);
    }

    @Override
    protected Entity.MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder p_329424_) {
        p_329424_.define(DATA_VALUE, 0);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.03;
    }

    @Override
    public void tick() {
        this.interpolation.interpolate();
        if (this.firstTick && this.level().isClientSide) {
            this.firstTick = false;
        } else {
            super.tick();
            boolean flag = !this.level().noCollision(this.getBoundingBox());
            if (this.isEyeInFluid(FluidTags.WATER)) {
                this.setUnderwaterMovement();
            } else if (!flag) {
                this.applyGravity();
            }

            if (this.level().getFluidState(this.blockPosition()).is(FluidTags.LAVA)) {
                this.setDeltaMovement(
                    (this.random.nextFloat() - this.random.nextFloat()) * 0.2F, 0.2F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
                );
            }

            if (this.tickCount % 20 == 1) {
                this.scanForMerges();
            }

            this.followNearbyPlayer();
            if (this.followingPlayer == null && !this.level().isClientSide && flag) {
                this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
                this.hasImpulse = true;
            }

            double d0 = this.getDeltaMovement().y;
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.applyEffectsFromBlocks();
            float f = 0.98F;
            if (this.onGround()) {
                BlockPos pos = getBlockPosBelowThatAffectsMyMovement();
                f = this.level().getBlockState(pos).getFriction(this.level(), pos, this) * 0.98F;
            }

            this.setDeltaMovement(this.getDeltaMovement().scale(f));
            if (this.verticalCollisionBelow && d0 < -this.getGravity()) {
                this.setDeltaMovement(new Vec3(this.getDeltaMovement().x, -d0 * 0.4, this.getDeltaMovement().z));
            }

            this.age++;
            if (this.age >= 6000) {
                this.discard();
            }
        }
    }

    private void followNearbyPlayer() {
        if (this.followingPlayer == null || this.followingPlayer.isSpectator() || this.followingPlayer.distanceToSqr(this) > 64.0) {
            Player player = this.level().getNearestPlayer(this, 8.0);
            if (player != null && !player.isSpectator() && !player.isDeadOrDying()) {
                this.followingPlayer = player;
            } else {
                this.followingPlayer = null;
            }
        }

        if (this.followingPlayer != null) {
            Vec3 vec3 = new Vec3(
                this.followingPlayer.getX() - this.getX(),
                this.followingPlayer.getY() + this.followingPlayer.getEyeHeight() / 2.0 - this.getY(),
                this.followingPlayer.getZ() - this.getZ()
            );
            double d0 = vec3.lengthSqr();
            double d1 = 1.0 - Math.sqrt(d0) / 8.0;
            this.setDeltaMovement(this.getDeltaMovement().add(vec3.normalize().scale(d1 * d1 * 0.1)));
        }
    }

    @Override
    public BlockPos getBlockPosBelowThatAffectsMyMovement() {
        return this.getOnPos(0.999999F);
    }

    private void scanForMerges() {
        if (this.level() instanceof ServerLevel) {
            for (ExperienceOrb experienceorb : this.level()
                .getEntities(EntityTypeTest.forClass(ExperienceOrb.class), this.getBoundingBox().inflate(0.5), this::canMerge)) {
                this.merge(experienceorb);
            }
        }
    }

    public static void award(ServerLevel p_147083_, Vec3 p_147084_, int p_147085_) {
        while (p_147085_ > 0) {
            int i = getExperienceValue(p_147085_);
            p_147085_ -= i;
            if (!tryMergeToExisting(p_147083_, p_147084_, i)) {
                p_147083_.addFreshEntity(new ExperienceOrb(p_147083_, p_147084_.x(), p_147084_.y(), p_147084_.z(), i));
            }
        }
    }

    private static boolean tryMergeToExisting(ServerLevel p_147097_, Vec3 p_147098_, int p_147099_) {
        AABB aabb = AABB.ofSize(p_147098_, 1.0, 1.0, 1.0);
        int i = p_147097_.getRandom().nextInt(40);
        List<ExperienceOrb> list = p_147097_.getEntities(EntityTypeTest.forClass(ExperienceOrb.class), aabb, p_147081_ -> canMerge(p_147081_, i, p_147099_));
        if (!list.isEmpty()) {
            ExperienceOrb experienceorb = list.get(0);
            experienceorb.count++;
            experienceorb.age = 0;
            return true;
        } else {
            return false;
        }
    }

    private boolean canMerge(ExperienceOrb p_147087_) {
        return p_147087_ != this && canMerge(p_147087_, this.getId(), this.getValue());
    }

    private static boolean canMerge(ExperienceOrb p_147089_, int p_147090_, int p_147091_) {
        return !p_147089_.isRemoved() && (p_147089_.getId() - p_147090_) % 40 == 0 && p_147089_.getValue() == p_147091_;
    }

    private void merge(ExperienceOrb p_147101_) {
        this.count = this.count + p_147101_.count;
        this.age = Math.min(this.age, p_147101_.age);
        p_147101_.discard();
    }

    private void setUnderwaterMovement() {
        Vec3 vec3 = this.getDeltaMovement();
        this.setDeltaMovement(vec3.x * 0.99F, Math.min(vec3.y + 5.0E-4F, 0.06F), vec3.z * 0.99F);
    }

    @Override
    protected void doWaterSplashEffect() {
    }

    @Override
    public final boolean hurtClient(DamageSource p_369585_) {
        return !this.isInvulnerableToBase(p_369585_);
    }

    @Override
    public final boolean hurtServer(ServerLevel p_365476_, DamageSource p_362340_, float p_369855_) {
        if (this.isInvulnerableToBase(p_362340_)) {
            return false;
        } else {
            this.markHurt();
            this.health = (int)(this.health - p_369855_);
            if (this.health <= 0) {
                this.discard();
            }

            return true;
        }
    }

    @Override
    public void addAdditionalSaveData(CompoundTag p_20796_) {
        p_20796_.putShort("Health", (short)this.health);
        p_20796_.putShort("Age", (short)this.age);
        p_20796_.putShort("Value", (short)this.getValue());
        p_20796_.putInt("Count", this.count);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag p_20788_) {
        this.health = p_20788_.getShortOr("Health", (short)5);
        this.age = p_20788_.getShortOr("Age", (short)0);
        this.setValue(p_20788_.getShortOr("Value", (short)0));
        this.count = p_20788_.read("Count", ExtraCodecs.POSITIVE_INT).orElse(1);
    }

    @Override
    public void playerTouch(Player p_20792_) {
        if (p_20792_ instanceof ServerPlayer serverplayer) {
            if (p_20792_.takeXpDelay == 0) {
                if (net.minecraftforge.event.ForgeEventFactory.onPlayerPickupXp(p_20792_, this)) return;
                p_20792_.takeXpDelay = 2;
                p_20792_.take(this, 1);
                int i = this.repairPlayerItems(serverplayer, this.getValue());
                if (i > 0) {
                    p_20792_.giveExperiencePoints(i);
                }

                this.count--;
                if (this.count == 0) {
                    this.discard();
                }
            }
        }
    }

    private int repairPlayerItems(ServerPlayer p_343572_, int p_147094_) {
        Optional<EnchantedItemInUse> optional = EnchantmentHelper.getRandomItemWith(EnchantmentEffectComponents.REPAIR_WITH_XP, p_343572_, ItemStack::isDamaged);
        if (optional.isPresent()) {
            ItemStack itemstack = optional.get().itemStack();
            int i = EnchantmentHelper.modifyDurabilityToRepairFromXp(p_343572_.serverLevel(), itemstack, p_147094_);
            int j = Math.min(i, itemstack.getDamageValue());
            itemstack.setDamageValue(itemstack.getDamageValue() - j);
            if (j > 0) {
                int k = p_147094_ - j * p_147094_ / i;
                if (k > 0) {
                    return this.repairPlayerItems(p_343572_, k);
                }
            }

            return 0;
        } else {
            return p_147094_;
        }
    }

    public int getValue() {
        return this.entityData.get(DATA_VALUE);
    }

    private void setValue(int p_396669_) {
        this.entityData.set(DATA_VALUE, p_396669_);
    }

    public int getIcon() {
        int i = this.getValue();
        if (i >= 2477) {
            return 10;
        } else if (i >= 1237) {
            return 9;
        } else if (i >= 617) {
            return 8;
        } else if (i >= 307) {
            return 7;
        } else if (i >= 149) {
            return 6;
        } else if (i >= 73) {
            return 5;
        } else if (i >= 37) {
            return 4;
        } else if (i >= 17) {
            return 3;
        } else if (i >= 7) {
            return 2;
        } else {
            return i >= 3 ? 1 : 0;
        }
    }

    public static int getExperienceValue(int p_20783_) {
        if (p_20783_ >= 2477) {
            return 2477;
        } else if (p_20783_ >= 1237) {
            return 1237;
        } else if (p_20783_ >= 617) {
            return 617;
        } else if (p_20783_ >= 307) {
            return 307;
        } else if (p_20783_ >= 149) {
            return 149;
        } else if (p_20783_ >= 73) {
            return 73;
        } else if (p_20783_ >= 37) {
            return 37;
        } else if (p_20783_ >= 17) {
            return 17;
        } else if (p_20783_ >= 7) {
            return 7;
        } else {
            return p_20783_ >= 3 ? 3 : 1;
        }
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public SoundSource getSoundSource() {
        return SoundSource.AMBIENT;
    }

    @Override
    public InterpolationHandler getInterpolation() {
        return this.interpolation;
    }
}
