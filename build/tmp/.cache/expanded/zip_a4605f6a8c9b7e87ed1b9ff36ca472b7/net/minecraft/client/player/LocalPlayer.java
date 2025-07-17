package net.minecraft.client.player;

import com.google.common.collect.Lists;
import com.mojang.logging.LogUtils;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.StreamSupport;
import javax.annotation.Nullable;
import net.minecraft.client.ClientRecipeBook;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.DeathScreen;
import net.minecraft.client.gui.screens.ReceivingLevelScreen;
import net.minecraft.client.gui.screens.WinScreen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.BookEditScreen;
import net.minecraft.client.gui.screens.inventory.CommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.HangingSignEditScreen;
import net.minecraft.client.gui.screens.inventory.JigsawBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.MinecartCommandBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.SignEditScreen;
import net.minecraft.client.gui.screens.inventory.StructureBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.TestBlockEditScreen;
import net.minecraft.client.gui.screens.inventory.TestInstanceBlockEditScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.resources.sounds.AmbientSoundHandler;
import net.minecraft.client.resources.sounds.BiomeAmbientSoundsHandler;
import net.minecraft.client.resources.sounds.BubbleColumnAmbientSoundHandler;
import net.minecraft.client.resources.sounds.ElytraOnPlayerSoundInstance;
import net.minecraft.client.resources.sounds.RidingMinecartSoundInstance;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundHandler;
import net.minecraft.client.resources.sounds.UnderwaterAmbientSoundInstances;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundClientCommandPacket;
import net.minecraft.network.protocol.game.ServerboundContainerClosePacket;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.network.protocol.game.ServerboundMoveVehiclePacket;
import net.minecraft.network.protocol.game.ServerboundPlayerAbilitiesPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerCommandPacket;
import net.minecraft.network.protocol.game.ServerboundPlayerInputPacket;
import net.minecraft.network.protocol.game.ServerboundRecipeBookSeenRecipePacket;
import net.minecraft.network.protocol.game.ServerboundSwingPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.StatsCounter;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.util.TickThrottler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Input;
import net.minecraft.world.entity.vehicle.AbstractBoat;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.WritableBookContent;
import net.minecraft.world.item.crafting.display.RecipeDisplayId;
import net.minecraft.world.level.BaseCommandBlock;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Portal;
import net.minecraft.world.level.block.entity.CommandBlockEntity;
import net.minecraft.world.level.block.entity.HangingSignBlockEntity;
import net.minecraft.world.level.block.entity.JigsawBlockEntity;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.entity.TestBlockEntity;
import net.minecraft.world.level.block.entity.TestInstanceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public class LocalPlayer extends AbstractClientPlayer {
    public static final Logger LOGGER = LogUtils.getLogger();
    private static final int POSITION_REMINDER_INTERVAL = 20;
    private static final int WATER_VISION_MAX_TIME = 600;
    private static final int WATER_VISION_QUICK_TIME = 100;
    private static final float WATER_VISION_QUICK_PERCENT = 0.6F;
    private static final double SUFFOCATING_COLLISION_CHECK_SCALE = 0.35;
    private static final double MINOR_COLLISION_ANGLE_THRESHOLD_RADIAN = 0.13962634F;
    public static final float USING_ITEM_SPEED_FACTOR = 0.2F;
    public final ClientPacketListener connection;
    private final StatsCounter stats;
    private final ClientRecipeBook recipeBook;
    private final TickThrottler dropSpamThrottler = new TickThrottler(20, 1280);
    private final List<AmbientSoundHandler> ambientSoundHandlers = Lists.newArrayList();
    private int permissionLevel = 0;
    private double xLast;
    private double yLast;
    private double zLast;
    private float yRotLast;
    private float xRotLast;
    private boolean lastOnGround;
    private boolean lastHorizontalCollision;
    private boolean crouching;
    private boolean wasShiftKeyDown;
    private boolean wasSprinting;
    private int positionReminder;
    private boolean flashOnSetHealth;
    public ClientInput input = new ClientInput();
    private Input lastSentInput = Input.EMPTY;
    protected final Minecraft minecraft;
    protected int sprintTriggerTime;
    public float yBob;
    public float xBob;
    public float yBobO;
    public float xBobO;
    private int jumpRidingTicks;
    private float jumpRidingScale;
    public float portalEffectIntensity;
    public float oPortalEffectIntensity;
    private boolean startedUsingItem;
    @Nullable
    private InteractionHand usingItemHand;
    private boolean handsBusy;
    private boolean autoJumpEnabled = true;
    private int autoJumpTime;
    private boolean wasFallFlying;
    private int waterVisionTime;
    private boolean showDeathScreen = true;
    private boolean doLimitedCrafting = false;

    public LocalPlayer(
        Minecraft p_108621_,
        ClientLevel p_108622_,
        ClientPacketListener p_108623_,
        StatsCounter p_108624_,
        ClientRecipeBook p_108625_,
        boolean p_108626_,
        boolean p_108627_
    ) {
        super(p_108622_, p_108623_.getLocalGameProfile());
        this.minecraft = p_108621_;
        this.connection = p_108623_;
        this.stats = p_108624_;
        this.recipeBook = p_108625_;
        this.wasShiftKeyDown = p_108626_;
        this.wasSprinting = p_108627_;
        this.ambientSoundHandlers.add(new UnderwaterAmbientSoundHandler(this, p_108621_.getSoundManager()));
        this.ambientSoundHandlers.add(new BubbleColumnAmbientSoundHandler(this));
        this.ambientSoundHandlers.add(new BiomeAmbientSoundsHandler(this, p_108621_.getSoundManager(), p_108622_.getBiomeManager()));
    }

    @Override
    public void heal(float p_108708_) {
    }

    @Override
    public boolean startRiding(Entity p_108667_, boolean p_108668_) {
        if (!super.startRiding(p_108667_, p_108668_)) {
            return false;
        } else {
            if (p_108667_ instanceof AbstractMinecart) {
                this.minecraft.getSoundManager().play(new RidingMinecartSoundInstance(this, (AbstractMinecart)p_108667_, true));
                this.minecraft.getSoundManager().play(new RidingMinecartSoundInstance(this, (AbstractMinecart)p_108667_, false));
            }

            return true;
        }
    }

    @Override
    public void removeVehicle() {
        super.removeVehicle();
        this.handsBusy = false;
    }

    @Override
    public float getViewXRot(float p_108742_) {
        return this.getXRot();
    }

    @Override
    public float getViewYRot(float p_108753_) {
        return this.isPassenger() ? super.getViewYRot(p_108753_) : this.getYRot();
    }

    @Override
    public void tick() {
        this.tickClientLoadTimeout();
        if (this.hasClientLoaded()) {
            this.dropSpamThrottler.tick();
            super.tick();
            this.sendShiftKeyState();
            if (!this.lastSentInput.equals(this.input.keyPresses)) {
                this.connection.send(new ServerboundPlayerInputPacket(this.input.keyPresses));
                this.lastSentInput = this.input.keyPresses;
            }

            if (this.isPassenger()) {
                this.connection.send(new ServerboundMovePlayerPacket.Rot(this.getYRot(), this.getXRot(), this.onGround(), this.horizontalCollision));
                Entity entity = this.getRootVehicle();
                if (entity != this && entity.isLocalInstanceAuthoritative()) {
                    this.connection.send(ServerboundMoveVehiclePacket.fromEntity(entity));
                    this.sendIsSprintingIfNeeded();
                }
            } else {
                this.sendPosition();
            }

            for (AmbientSoundHandler ambientsoundhandler : this.ambientSoundHandlers) {
                ambientsoundhandler.tick();
            }
        }
    }

    public float getCurrentMood() {
        for (AmbientSoundHandler ambientsoundhandler : this.ambientSoundHandlers) {
            if (ambientsoundhandler instanceof BiomeAmbientSoundsHandler) {
                return ((BiomeAmbientSoundsHandler)ambientsoundhandler).getMoodiness();
            }
        }

        return 0.0F;
    }

    private void sendPosition() {
        this.sendIsSprintingIfNeeded();
        if (this.isControlledCamera()) {
            double d0 = this.getX() - this.xLast;
            double d1 = this.getY() - this.yLast;
            double d2 = this.getZ() - this.zLast;
            double d3 = this.getYRot() - this.yRotLast;
            double d4 = this.getXRot() - this.xRotLast;
            this.positionReminder++;
            boolean flag = Mth.lengthSquared(d0, d1, d2) > Mth.square(2.0E-4) || this.positionReminder >= 20;
            boolean flag1 = d3 != 0.0 || d4 != 0.0;
            if (flag && flag1) {
                this.connection
                    .send(new ServerboundMovePlayerPacket.PosRot(this.position(), this.getYRot(), this.getXRot(), this.onGround(), this.horizontalCollision));
            } else if (flag) {
                this.connection.send(new ServerboundMovePlayerPacket.Pos(this.position(), this.onGround(), this.horizontalCollision));
            } else if (flag1) {
                this.connection.send(new ServerboundMovePlayerPacket.Rot(this.getYRot(), this.getXRot(), this.onGround(), this.horizontalCollision));
            } else if (this.lastOnGround != this.onGround() || this.lastHorizontalCollision != this.horizontalCollision) {
                this.connection.send(new ServerboundMovePlayerPacket.StatusOnly(this.onGround(), this.horizontalCollision));
            }

            if (flag) {
                this.xLast = this.getX();
                this.yLast = this.getY();
                this.zLast = this.getZ();
                this.positionReminder = 0;
            }

            if (flag1) {
                this.yRotLast = this.getYRot();
                this.xRotLast = this.getXRot();
            }

            this.lastOnGround = this.onGround();
            this.lastHorizontalCollision = this.horizontalCollision;
            this.autoJumpEnabled = this.minecraft.options.autoJump().get();
        }
    }

    private void sendShiftKeyState() {
        boolean flag = this.isShiftKeyDown();
        if (flag != this.wasShiftKeyDown) {
            ServerboundPlayerCommandPacket.Action serverboundplayercommandpacket$action = flag
                ? ServerboundPlayerCommandPacket.Action.PRESS_SHIFT_KEY
                : ServerboundPlayerCommandPacket.Action.RELEASE_SHIFT_KEY;
            this.connection.send(new ServerboundPlayerCommandPacket(this, serverboundplayercommandpacket$action));
            this.wasShiftKeyDown = flag;
        }
    }

    private void sendIsSprintingIfNeeded() {
        boolean flag = this.isSprinting();
        if (flag != this.wasSprinting) {
            ServerboundPlayerCommandPacket.Action serverboundplayercommandpacket$action = flag
                ? ServerboundPlayerCommandPacket.Action.START_SPRINTING
                : ServerboundPlayerCommandPacket.Action.STOP_SPRINTING;
            this.connection.send(new ServerboundPlayerCommandPacket(this, serverboundplayercommandpacket$action));
            this.wasSprinting = flag;
        }
    }

    public boolean drop(boolean p_108701_) {
        ServerboundPlayerActionPacket.Action serverboundplayeractionpacket$action = p_108701_
            ? ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS
            : ServerboundPlayerActionPacket.Action.DROP_ITEM;
        if (isUsingItem() && getUsedItemHand() == InteractionHand.MAIN_HAND && (p_108701_ || getUseItem().getCount() == 1)) stopUsingItem(); // Forge: fix MC-231097 on the clientside
        ItemStack itemstack = this.getInventory().removeFromSelected(p_108701_);
        this.connection.send(new ServerboundPlayerActionPacket(serverboundplayeractionpacket$action, BlockPos.ZERO, Direction.DOWN));
        return !itemstack.isEmpty();
    }

    @Override
    public void swing(InteractionHand p_108660_) {
        super.swing(p_108660_);
        this.connection.send(new ServerboundSwingPacket(p_108660_));
    }

    @Override
    public void respawn() {
        this.connection.send(new ServerboundClientCommandPacket(ServerboundClientCommandPacket.Action.PERFORM_RESPAWN));
        KeyMapping.resetToggleKeys();
    }

    @Override
    public void closeContainer() {
        this.connection.send(new ServerboundContainerClosePacket(this.containerMenu.containerId));
        this.clientSideCloseContainer();
    }

    public void clientSideCloseContainer() {
        super.closeContainer();
        this.minecraft.setScreen(null);
    }

    public void hurtTo(float p_108761_) {
        if (this.flashOnSetHealth) {
            float f = this.getHealth() - p_108761_;
            if (f <= 0.0F) {
                this.setHealth(p_108761_);
                if (f < 0.0F) {
                    this.invulnerableTime = 10;
                }
            } else {
                this.lastHurt = f;
                this.invulnerableTime = 20;
                this.setHealth(p_108761_);
                this.hurtDuration = 10;
                this.hurtTime = this.hurtDuration;
            }
        } else {
            this.setHealth(p_108761_);
            this.flashOnSetHealth = true;
        }
    }

    @Override
    public void onUpdateAbilities() {
        this.connection.send(new ServerboundPlayerAbilitiesPacket(this.getAbilities()));
    }

    @Override
    public boolean isLocalPlayer() {
        return true;
    }

    @Override
    public boolean isSuppressingSlidingDownLadder() {
        return !this.getAbilities().flying && super.isSuppressingSlidingDownLadder();
    }

    @Override
    public boolean canSpawnSprintParticle() {
        return !this.getAbilities().flying && super.canSpawnSprintParticle();
    }

    protected void sendRidingJump() {
        this.connection
            .send(
                new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.START_RIDING_JUMP, Mth.floor(this.getJumpRidingScale() * 100.0F))
            );
    }

    public void sendOpenInventory() {
        this.connection.send(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.OPEN_INVENTORY));
    }

    public StatsCounter getStats() {
        return this.stats;
    }

    public ClientRecipeBook getRecipeBook() {
        return this.recipeBook;
    }

    public void removeRecipeHighlight(RecipeDisplayId p_369927_) {
        if (this.recipeBook.willHighlight(p_369927_)) {
            this.recipeBook.removeHighlight(p_369927_);
            this.connection.send(new ServerboundRecipeBookSeenRecipePacket(p_369927_));
        }
    }

    @Override
    public int getPermissionLevel() {
        return this.permissionLevel;
    }

    public void setPermissionLevel(int p_108649_) {
        this.permissionLevel = p_108649_;
    }

    @Override
    public void displayClientMessage(Component p_108696_, boolean p_108697_) {
        this.minecraft.getChatListener().handleSystemMessage(p_108696_, p_108697_);
    }

    private void moveTowardsClosestSpace(double p_108705_, double p_108706_) {
        BlockPos blockpos = BlockPos.containing(p_108705_, this.getY(), p_108706_);
        if (this.suffocatesAt(blockpos)) {
            double d0 = p_108705_ - blockpos.getX();
            double d1 = p_108706_ - blockpos.getZ();
            Direction direction = null;
            double d2 = Double.MAX_VALUE;
            Direction[] adirection = new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH};

            for (Direction direction1 : adirection) {
                double d3 = direction1.getAxis().choose(d0, 0.0, d1);
                double d4 = direction1.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - d3 : d3;
                if (d4 < d2 && !this.suffocatesAt(blockpos.relative(direction1))) {
                    d2 = d4;
                    direction = direction1;
                }
            }

            if (direction != null) {
                Vec3 vec3 = this.getDeltaMovement();
                if (direction.getAxis() == Direction.Axis.X) {
                    this.setDeltaMovement(0.1 * direction.getStepX(), vec3.y, vec3.z);
                } else {
                    this.setDeltaMovement(vec3.x, vec3.y, 0.1 * direction.getStepZ());
                }
            }
        }
    }

    private boolean suffocatesAt(BlockPos p_108747_) {
        AABB aabb = this.getBoundingBox();
        AABB aabb1 = new AABB(
                p_108747_.getX(), aabb.minY, p_108747_.getZ(), p_108747_.getX() + 1.0, aabb.maxY, p_108747_.getZ() + 1.0
            )
            .deflate(1.0E-7);
        return this.level().collidesWithSuffocatingBlock(this, aabb1);
    }

    public void setExperienceValues(float p_108645_, int p_108646_, int p_108647_) {
        this.experienceProgress = p_108645_;
        this.totalExperience = p_108646_;
        this.experienceLevel = p_108647_;
    }

    @Override
    public void handleEntityEvent(byte p_108643_) {
        if (p_108643_ >= 24 && p_108643_ <= 28) {
            this.setPermissionLevel(p_108643_ - 24);
        } else {
            super.handleEntityEvent(p_108643_);
        }
    }

    public void setShowDeathScreen(boolean p_108712_) {
        this.showDeathScreen = p_108712_;
    }

    public boolean shouldShowDeathScreen() {
        return this.showDeathScreen;
    }

    public void setDoLimitedCrafting(boolean p_300578_) {
        this.doLimitedCrafting = p_300578_;
    }

    public boolean getDoLimitedCrafting() {
        return this.doLimitedCrafting;
    }

    @Override
    public void playSound(SoundEvent p_108651_, float p_108652_, float p_108653_) {
        var holder = net.minecraft.core.registries.BuiltInRegistries.SOUND_EVENT.wrapAsHolder(p_108651_);
        var event = net.minecraftforge.event.ForgeEventFactory.onPlaySoundAtEntity(this.level(), this, holder, this.getSoundSource(), p_108652_, p_108653_);
        if (event.isCanceled() || event.getSound() == null) return;
        this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), event.getSound().get(), event.getSource(), event.getNewVolume(), event.getNewPitch(), false);

    }

    @Override
    public void playNotifySound(SoundEvent p_108655_, SoundSource p_108656_, float p_108657_, float p_108658_) {
        this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), p_108655_, p_108656_, p_108657_, p_108658_, false);
    }

    @Override
    public void startUsingItem(InteractionHand p_108718_) {
        ItemStack itemstack = this.getItemInHand(p_108718_);
        if (!itemstack.isEmpty() && !this.isUsingItem()) {
            super.startUsingItem(p_108718_);
            this.startedUsingItem = true;
            this.usingItemHand = p_108718_;
        }
    }

    @Override
    public boolean isUsingItem() {
        return this.startedUsingItem;
    }

    @Override
    public void stopUsingItem() {
        super.stopUsingItem();
        this.startedUsingItem = false;
    }

    @Override
    public InteractionHand getUsedItemHand() {
        return Objects.requireNonNullElse(this.usingItemHand, InteractionHand.MAIN_HAND);
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> p_108699_) {
        super.onSyncedDataUpdated(p_108699_);
        if (DATA_LIVING_ENTITY_FLAGS.equals(p_108699_)) {
            boolean flag = (this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 1) > 0;
            InteractionHand interactionhand = (this.entityData.get(DATA_LIVING_ENTITY_FLAGS) & 2) > 0 ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
            if (flag && !this.startedUsingItem) {
                this.startUsingItem(interactionhand);
            } else if (!flag && this.startedUsingItem) {
                this.stopUsingItem();
            }
        }

        if (DATA_SHARED_FLAGS_ID.equals(p_108699_) && this.isFallFlying() && !this.wasFallFlying) {
            this.minecraft.getSoundManager().play(new ElytraOnPlayerSoundInstance(this));
        }
    }

    @Nullable
    public PlayerRideableJumping jumpableVehicle() {
        return this.getControlledVehicle() instanceof PlayerRideableJumping playerrideablejumping && playerrideablejumping.canJump() ? playerrideablejumping : null;
    }

    public float getJumpRidingScale() {
        return this.jumpRidingScale;
    }

    @Override
    public boolean isTextFilteringEnabled() {
        return this.minecraft.isTextFilteringEnabled();
    }

    @Override
    public void openTextEdit(SignBlockEntity p_277970_, boolean p_277980_) {
        if (p_277970_ instanceof HangingSignBlockEntity hangingsignblockentity) {
            this.minecraft.setScreen(new HangingSignEditScreen(hangingsignblockentity, p_277980_, this.minecraft.isTextFilteringEnabled()));
        } else {
            this.minecraft.setScreen(new SignEditScreen(p_277970_, p_277980_, this.minecraft.isTextFilteringEnabled()));
        }
    }

    @Override
    public void openMinecartCommandBlock(BaseCommandBlock p_108678_) {
        this.minecraft.setScreen(new MinecartCommandBlockEditScreen(p_108678_));
    }

    @Override
    public void openCommandBlock(CommandBlockEntity p_108680_) {
        this.minecraft.setScreen(new CommandBlockEditScreen(p_108680_));
    }

    @Override
    public void openStructureBlock(StructureBlockEntity p_108686_) {
        this.minecraft.setScreen(new StructureBlockEditScreen(p_108686_));
    }

    @Override
    public void openTestBlock(TestBlockEntity p_393736_) {
        this.minecraft.setScreen(new TestBlockEditScreen(p_393736_));
    }

    @Override
    public void openTestInstanceBlock(TestInstanceBlockEntity p_393476_) {
        this.minecraft.setScreen(new TestInstanceBlockEditScreen(p_393476_));
    }

    @Override
    public void openJigsawBlock(JigsawBlockEntity p_108682_) {
        this.minecraft.setScreen(new JigsawBlockEditScreen(p_108682_));
    }

    @Override
    public void openItemGui(ItemStack p_108673_, InteractionHand p_108674_) {
        WritableBookContent writablebookcontent = p_108673_.get(DataComponents.WRITABLE_BOOK_CONTENT);
        if (writablebookcontent != null) {
            this.minecraft.setScreen(new BookEditScreen(this, p_108673_, p_108674_, writablebookcontent));
        }
    }

    @Override
    public void crit(Entity p_108665_) {
        this.minecraft.particleEngine.createTrackingEmitter(p_108665_, ParticleTypes.CRIT);
    }

    @Override
    public void magicCrit(Entity p_108710_) {
        this.minecraft.particleEngine.createTrackingEmitter(p_108710_, ParticleTypes.ENCHANTED_HIT);
    }

    @Override
    public boolean isShiftKeyDown() {
        return this.input.keyPresses.shift();
    }

    @Override
    public boolean isCrouching() {
        return this.crouching;
    }

    public boolean isMovingSlowly() {
        return this.isCrouching() || this.isVisuallyCrawling();
    }

    @Override
    public void applyInput() {
        if (this.isControlledCamera()) {
            Vec2 vec2 = this.modifyInput(this.input.getMoveVector());
            this.xxa = vec2.x;
            this.zza = vec2.y;
            this.jumping = this.input.keyPresses.jump();
            this.yBobO = this.yBob;
            this.xBobO = this.xBob;
            this.xBob = this.xBob + (this.getXRot() - this.xBob) * 0.5F;
            this.yBob = this.yBob + (this.getYRot() - this.yBob) * 0.5F;
        } else {
            super.applyInput();
        }
    }

    private Vec2 modifyInput(Vec2 p_397409_) {
        if (p_397409_.lengthSquared() == 0.0F) {
            return p_397409_;
        } else {
            Vec2 vec2 = p_397409_.scale(0.98F);
            if (this.isUsingItem() && !this.isPassenger()) {
                vec2 = vec2.scale(0.2F);
            }

            if (this.isMovingSlowly()) {
                float f = (float)this.getAttributeValue(Attributes.SNEAKING_SPEED);
                vec2 = vec2.scale(f);
            }

            return modifyInputSpeedForSquareMovement(vec2);
        }
    }

    private static Vec2 modifyInputSpeedForSquareMovement(Vec2 p_393358_) {
        float f = p_393358_.length();
        if (f <= 0.0F) {
            return p_393358_;
        } else {
            Vec2 vec2 = p_393358_.scale(1.0F / f);
            float f1 = distanceToUnitSquare(vec2);
            float f2 = Math.min(f * f1, 1.0F);
            return vec2.scale(f2);
        }
    }

    private static float distanceToUnitSquare(Vec2 p_393841_) {
        float f = Math.abs(p_393841_.x);
        float f1 = Math.abs(p_393841_.y);
        float f2 = f1 > f ? f / f1 : f1 / f;
        return Mth.sqrt(1.0F + Mth.square(f2));
    }

    protected boolean isControlledCamera() {
        return this.minecraft.getCameraEntity() == this;
    }

    public void resetPos() {
        this.setPose(Pose.STANDING);
        if (this.level() != null) {
            for (double d0 = this.getY(); d0 > this.level().getMinY() && d0 <= this.level().getMaxY(); d0++) {
                this.setPos(this.getX(), d0, this.getZ());
                if (this.level().noCollision(this)) {
                    break;
                }
            }

            this.setDeltaMovement(Vec3.ZERO);
            this.setXRot(0.0F);
        }

        this.setHealth(this.getMaxHealth());
        this.deathTime = 0;
    }

    @Override
    public void aiStep() {
        if (this.sprintTriggerTime > 0) {
            this.sprintTriggerTime--;
        }

        if (!(this.minecraft.screen instanceof ReceivingLevelScreen)) {
            this.handlePortalTransitionEffect(this.getActivePortalLocalTransition() == Portal.Transition.CONFUSION);
            this.processPortalCooldown();
        }

        boolean flag = this.input.keyPresses.jump();
        boolean flag1 = this.input.keyPresses.shift();
        boolean flag2 = this.input.hasForwardImpulse();
        Abilities abilities = this.getAbilities();
        this.crouching = !abilities.flying
            && !this.isSwimming()
            && !this.isPassenger()
            && this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.CROUCHING)
            && (this.isShiftKeyDown() || !this.isSleeping() && !this.canPlayerFitWithinBlocksAndEntitiesWhen(Pose.STANDING));
        this.input.tick();
        net.minecraftforge.client.ForgeHooksClient.onMovementInputUpdate(this, this.input);
        this.minecraft.getTutorial().onInput(this.input);
        boolean flag3 = false;
        if (this.autoJumpTime > 0) {
            this.autoJumpTime--;
            flag3 = true;
            this.input.makeJump();
        }

        if (!this.noPhysics) {
            this.moveTowardsClosestSpace(this.getX() - this.getBbWidth() * 0.35, this.getZ() + this.getBbWidth() * 0.35);
            this.moveTowardsClosestSpace(this.getX() - this.getBbWidth() * 0.35, this.getZ() - this.getBbWidth() * 0.35);
            this.moveTowardsClosestSpace(this.getX() + this.getBbWidth() * 0.35, this.getZ() - this.getBbWidth() * 0.35);
            this.moveTowardsClosestSpace(this.getX() + this.getBbWidth() * 0.35, this.getZ() + this.getBbWidth() * 0.35);
        }

        if (flag1 || this.isUsingItem() && !this.isPassenger() || this.input.keyPresses.backward()) {
            this.sprintTriggerTime = 0;
        }

        if (this.canStartSprinting()) {
            if (!flag2) {
                if (this.sprintTriggerTime > 0) {
                    this.setSprinting(true);
                } else {
                    this.sprintTriggerTime = 7;
                }
            }

            if (this.input.keyPresses.sprint()) {
                this.setSprinting(true);
            }
        }

        if (this.isSprinting()) {
            if (this.isSwimming()) {
                if (this.shouldStopSwimSprinting()) {
                    this.setSprinting(false);
                }
            } else if (this.shouldStopRunSprinting()) {
                this.setSprinting(false);
            }
        }

        boolean flag4 = false;
        if (abilities.mayfly) {
            if (this.minecraft.gameMode.isAlwaysFlying()) {
                if (!abilities.flying) {
                    abilities.flying = true;
                    flag4 = true;
                    this.onUpdateAbilities();
                }
            } else if (!flag && this.input.keyPresses.jump() && !flag3) {
                if (this.jumpTriggerTime == 0) {
                    this.jumpTriggerTime = 7;
                } else if (!this.isSwimming()) {
                    abilities.flying = !abilities.flying;
                    if (abilities.flying && this.onGround()) {
                        this.jumpFromGround();
                    }

                    flag4 = true;
                    this.onUpdateAbilities();
                    this.jumpTriggerTime = 0;
                }
            }
        }

        if (this.input.keyPresses.jump() && !flag4 && !flag && !this.onClimbable() && this.tryToStartFallFlying()) {
            this.connection.send(new ServerboundPlayerCommandPacket(this, ServerboundPlayerCommandPacket.Action.START_FALL_FLYING));
        }

        this.wasFallFlying = this.isFallFlying();
        var fluidType = this.getMaxHeightFluidType();
        if ((this.isInWater() || (!fluidType.isAir() && this.canSwimInFluidType(fluidType))) && this.input.keyPresses.shift() && this.isAffectedByFluids()) {
           this.sinkInFluid(this.isInWater() ? net.minecraftforge.common.ForgeMod.WATER_TYPE.get() : fluidType);
        }

        if (this.isEyeInFluid(FluidTags.WATER)) {
            int i = this.isSpectator() ? 10 : 1;
            this.waterVisionTime = Mth.clamp(this.waterVisionTime + i, 0, 600);
        } else if (this.waterVisionTime > 0) {
            this.isEyeInFluid(FluidTags.WATER);
            this.waterVisionTime = Mth.clamp(this.waterVisionTime - 10, 0, 600);
        }

        if (abilities.flying && this.isControlledCamera()) {
            int j = 0;
            if (this.input.keyPresses.shift()) {
                j--;
            }

            if (this.input.keyPresses.jump()) {
                j++;
            }

            if (j != 0) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0, j * abilities.getFlyingSpeed() * 3.0F, 0.0));
            }
        }

        PlayerRideableJumping playerrideablejumping = this.jumpableVehicle();
        if (playerrideablejumping != null && playerrideablejumping.getJumpCooldown() == 0) {
            if (this.jumpRidingTicks < 0) {
                this.jumpRidingTicks++;
                if (this.jumpRidingTicks == 0) {
                    this.jumpRidingScale = 0.0F;
                }
            }

            if (flag && !this.input.keyPresses.jump()) {
                this.jumpRidingTicks = -10;
                playerrideablejumping.onPlayerJump(Mth.floor(this.getJumpRidingScale() * 100.0F));
                this.sendRidingJump();
            } else if (!flag && this.input.keyPresses.jump()) {
                this.jumpRidingTicks = 0;
                this.jumpRidingScale = 0.0F;
            } else if (flag) {
                this.jumpRidingTicks++;
                if (this.jumpRidingTicks < 10) {
                    this.jumpRidingScale = this.jumpRidingTicks * 0.1F;
                } else {
                    this.jumpRidingScale = 0.8F + 2.0F / (this.jumpRidingTicks - 9) * 0.1F;
                }
            }
        } else {
            this.jumpRidingScale = 0.0F;
        }

        super.aiStep();
        if (this.onGround() && abilities.flying && !this.minecraft.gameMode.isAlwaysFlying()) {
            abilities.flying = false;
            this.onUpdateAbilities();
        }
    }

    private boolean shouldStopRunSprinting() {
        return this.hasBlindness()
            || this.isPassenger() && !this.vehicleCanSprint(this.getVehicle())
            || !this.input.hasForwardImpulse()
            || !this.hasEnoughFoodToSprint()
            || this.horizontalCollision && !this.minorHorizontalCollision
            || this.isInWater() && !this.isUnderWater()
            || this.isInFluidType((fluidType, height) -> this.canSwimInFluidType(fluidType)) && !this.canStartSwimming();
    }

    private boolean shouldStopSwimSprinting() {
        return this.hasBlindness()
            || this.isPassenger() && !this.vehicleCanSprint(this.getVehicle())
            || !this.isInWaterOrSwimmable()
            || !this.input.hasForwardImpulse() && !this.onGround() && !this.input.keyPresses.shift()
            || !this.hasEnoughFoodToSprint();
    }

    private boolean hasBlindness() {
        return this.hasEffect(MobEffects.BLINDNESS);
    }

    public Portal.Transition getActivePortalLocalTransition() {
        return this.portalProcess == null ? Portal.Transition.NONE : this.portalProcess.getPortalLocalTransition();
    }

    @Override
    protected void tickDeath() {
        this.deathTime++;
        if (this.deathTime == 20) {
            this.remove(Entity.RemovalReason.KILLED);
        }
    }

    private void handlePortalTransitionEffect(boolean p_342626_) {
        this.oPortalEffectIntensity = this.portalEffectIntensity;
        float f = 0.0F;
        if (p_342626_ && this.portalProcess != null && this.portalProcess.isInsidePortalThisTick()) {
            if (this.minecraft.screen != null
                && !this.minecraft.screen.isPauseScreen()
                && !(this.minecraft.screen instanceof DeathScreen)
                && !(this.minecraft.screen instanceof WinScreen)) {
                if (this.minecraft.screen instanceof AbstractContainerScreen) {
                    this.closeContainer();
                }

                this.minecraft.setScreen(null);
            }

            if (this.portalEffectIntensity == 0.0F) {
                this.minecraft.getSoundManager().play(SimpleSoundInstance.forLocalAmbience(SoundEvents.PORTAL_TRIGGER, this.random.nextFloat() * 0.4F + 0.8F, 0.25F));
            }

            f = 0.0125F;
            this.portalProcess.setAsInsidePortalThisTick(false);
        } else if (this.portalEffectIntensity > 0.0F) {
            f = -0.05F;
        }

        this.portalEffectIntensity = Mth.clamp(this.portalEffectIntensity + f, 0.0F, 1.0F);
    }

    @Override
    public void rideTick() {
        super.rideTick();
        if (this.wantsToStopRiding() && this.isPassenger())
            this.input.keyPresses = this.input.keyPresses.jump(false);
        this.handsBusy = false;
        if (this.getControlledVehicle() instanceof AbstractBoat abstractboat) {
            abstractboat.setInput(
                this.input.keyPresses.left(),
                this.input.keyPresses.right(),
                this.input.keyPresses.forward(),
                this.input.keyPresses.backward()
            );
            this.handsBusy = this.handsBusy
                | (
                    this.input.keyPresses.left()
                        || this.input.keyPresses.right()
                        || this.input.keyPresses.forward()
                        || this.input.keyPresses.backward()
                );
        }
    }

    public boolean isHandsBusy() {
        return this.handsBusy;
    }

    @Override
    public void move(MoverType p_108670_, Vec3 p_108671_) {
        double d0 = this.getX();
        double d1 = this.getZ();
        super.move(p_108670_, p_108671_);
        float f = (float)(this.getX() - d0);
        float f1 = (float)(this.getZ() - d1);
        this.updateAutoJump(f, f1);
        this.walkDist = this.walkDist + Mth.length(f, f1) * 0.6F;
    }

    public boolean isAutoJumpEnabled() {
        return this.autoJumpEnabled;
    }

    @Override
    public boolean shouldRotateWithMinecart() {
        return this.minecraft.options.rotateWithMinecart().get();
    }

    protected void updateAutoJump(float p_108744_, float p_108745_) {
        if (this.canAutoJump()) {
            Vec3 vec3 = this.position();
            Vec3 vec31 = vec3.add(p_108744_, 0.0, p_108745_);
            Vec3 vec32 = new Vec3(p_108744_, 0.0, p_108745_);
            float f = this.getSpeed();
            float f1 = (float)vec32.lengthSqr();
            if (f1 <= 0.001F) {
                Vec2 vec2 = this.input.getMoveVector();
                float f2 = f * vec2.x;
                float f3 = f * vec2.y;
                float f4 = Mth.sin(this.getYRot() * (float) (Math.PI / 180.0));
                float f5 = Mth.cos(this.getYRot() * (float) (Math.PI / 180.0));
                vec32 = new Vec3(f2 * f5 - f3 * f4, vec32.y, f3 * f5 + f2 * f4);
                f1 = (float)vec32.lengthSqr();
                if (f1 <= 0.001F) {
                    return;
                }
            }

            float f12 = Mth.invSqrt(f1);
            Vec3 vec312 = vec32.scale(f12);
            Vec3 vec313 = this.getForward();
            float f13 = (float)(vec313.x * vec312.x + vec313.z * vec312.z);
            if (!(f13 < -0.15F)) {
                CollisionContext collisioncontext = CollisionContext.of(this);
                BlockPos blockpos = BlockPos.containing(this.getX(), this.getBoundingBox().maxY, this.getZ());
                BlockState blockstate = this.level().getBlockState(blockpos);
                if (blockstate.getCollisionShape(this.level(), blockpos, collisioncontext).isEmpty()) {
                    blockpos = blockpos.above();
                    BlockState blockstate1 = this.level().getBlockState(blockpos);
                    if (blockstate1.getCollisionShape(this.level(), blockpos, collisioncontext).isEmpty()) {
                        float f6 = 7.0F;
                        float f7 = 1.2F;
                        if (this.hasEffect(MobEffects.JUMP_BOOST)) {
                            f7 += (this.getEffect(MobEffects.JUMP_BOOST).getAmplifier() + 1) * 0.75F;
                        }

                        float f8 = Math.max(f * 7.0F, 1.0F / f12);
                        Vec3 vec34 = vec31.add(vec312.scale(f8));
                        float f9 = this.getBbWidth();
                        float f10 = this.getBbHeight();
                        AABB aabb = new AABB(vec3, vec34.add(0.0, f10, 0.0)).inflate(f9, 0.0, f9);
                        Vec3 $$23 = vec3.add(0.0, 0.51F, 0.0);
                        vec34 = vec34.add(0.0, 0.51F, 0.0);
                        Vec3 vec35 = vec312.cross(new Vec3(0.0, 1.0, 0.0));
                        Vec3 vec36 = vec35.scale(f9 * 0.5F);
                        Vec3 vec37 = $$23.subtract(vec36);
                        Vec3 vec38 = vec34.subtract(vec36);
                        Vec3 vec39 = $$23.add(vec36);
                        Vec3 vec310 = vec34.add(vec36);
                        Iterable<VoxelShape> iterable = this.level().getCollisions(this, aabb);
                        Iterator<AABB> iterator = StreamSupport.stream(iterable.spliterator(), false)
                            .flatMap(p_234124_ -> p_234124_.toAabbs().stream())
                            .iterator();
                        float f11 = Float.MIN_VALUE;

                        while (iterator.hasNext()) {
                            AABB aabb1 = iterator.next();
                            if (aabb1.intersects(vec37, vec38) || aabb1.intersects(vec39, vec310)) {
                                f11 = (float)aabb1.maxY;
                                Vec3 vec311 = aabb1.getCenter();
                                BlockPos blockpos1 = BlockPos.containing(vec311);

                                for (int i = 1; i < f7; i++) {
                                    BlockPos blockpos2 = blockpos1.above(i);
                                    BlockState blockstate2 = this.level().getBlockState(blockpos2);
                                    VoxelShape voxelshape;
                                    if (!(voxelshape = blockstate2.getCollisionShape(this.level(), blockpos2, collisioncontext)).isEmpty()) {
                                        f11 = (float)voxelshape.max(Direction.Axis.Y) + blockpos2.getY();
                                        if (f11 - this.getY() > f7) {
                                            return;
                                        }
                                    }

                                    if (i > 1) {
                                        blockpos = blockpos.above();
                                        BlockState blockstate3 = this.level().getBlockState(blockpos);
                                        if (!blockstate3.getCollisionShape(this.level(), blockpos, collisioncontext).isEmpty()) {
                                            return;
                                        }
                                    }
                                }
                                break;
                            }
                        }

                        if (f11 != Float.MIN_VALUE) {
                            float f14 = (float)(f11 - this.getY());
                            if (!(f14 <= 0.5F) && !(f14 > f7)) {
                                this.autoJumpTime = 1;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected boolean isHorizontalCollisionMinor(Vec3 p_197411_) {
        float f = this.getYRot() * (float) (Math.PI / 180.0);
        double d0 = Mth.sin(f);
        double d1 = Mth.cos(f);
        double d2 = this.xxa * d1 - this.zza * d0;
        double d3 = this.zza * d1 + this.xxa * d0;
        double d4 = Mth.square(d2) + Mth.square(d3);
        double d5 = Mth.square(p_197411_.x) + Mth.square(p_197411_.z);
        if (!(d4 < 1.0E-5F) && !(d5 < 1.0E-5F)) {
            double d6 = d2 * p_197411_.x + d3 * p_197411_.z;
            double d7 = Math.acos(d6 / Math.sqrt(d4 * d5));
            return d7 < 0.13962634F;
        } else {
            return false;
        }
    }

    private boolean canAutoJump() {
        return this.isAutoJumpEnabled() && this.autoJumpTime <= 0 && this.onGround() && !this.isStayingOnGroundSurface() && !this.isPassenger() && this.isMoving() && this.getBlockJumpFactor() >= 1.0;
    }

    private boolean isMoving() {
        return this.input.getMoveVector().lengthSquared() > 0.0F;
    }

    private boolean canStartSprinting() {
        return !this.isSprinting()
            && this.input.hasForwardImpulse()
            && this.hasEnoughFoodToSprint()
            && !this.isUsingItem()
            && !this.hasBlindness()
            && (!this.isPassenger() || this.vehicleCanSprint(this.getVehicle()))
            && (!this.isFallFlying() || this.isUnderWater())
            && (!this.isMovingSlowly() || this.isUnderWater())
            && (!this.isInWater() || this.isUnderWater() || this.canStartSwimming());
    }

    private boolean vehicleCanSprint(Entity p_265184_) {
        return p_265184_.canSprint() && p_265184_.isLocalInstanceAuthoritative();
    }

    private boolean hasEnoughFoodToSprint() {
        return this.isPassenger() || this.getFoodData().getFoodLevel() > 6.0F || this.getAbilities().mayfly;
    }

    public float getWaterVision() {
        if (!this.isEyeInFluid(FluidTags.WATER)) {
            return 0.0F;
        } else {
            float f = 600.0F;
            float f1 = 100.0F;
            if (this.waterVisionTime >= 600.0F) {
                return 1.0F;
            } else {
                float f2 = Mth.clamp(this.waterVisionTime / 100.0F, 0.0F, 1.0F);
                float f3 = this.waterVisionTime < 100.0F ? 0.0F : Mth.clamp((this.waterVisionTime - 100.0F) / 500.0F, 0.0F, 1.0F);
                return f2 * 0.6F + f3 * 0.39999998F;
            }
        }
    }

    public void onGameModeChanged(GameType p_287675_) {
        if (p_287675_ == GameType.SPECTATOR) {
            this.setDeltaMovement(this.getDeltaMovement().with(Direction.Axis.Y, 0.0));
        }
    }

    @Override
    public boolean isUnderWater() {
        return this.wasUnderwater;
    }

    @Override
    protected boolean updateIsUnderwater() {
        boolean flag = this.wasUnderwater;
        boolean flag1 = super.updateIsUnderwater();
        if (this.isSpectator()) {
            return this.wasUnderwater;
        } else {
            if (!flag && flag1) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_ENTER, SoundSource.AMBIENT, 1.0F, 1.0F, false);
                this.minecraft.getSoundManager().play(new UnderwaterAmbientSoundInstances.UnderwaterAmbientSoundInstance(this));
            }

            if (flag && !flag1) {
                this.level().playLocalSound(this.getX(), this.getY(), this.getZ(), SoundEvents.AMBIENT_UNDERWATER_EXIT, SoundSource.AMBIENT, 1.0F, 1.0F, false);
            }

            return this.wasUnderwater;
        }
    }

    @Override
    public Vec3 getRopeHoldPosition(float p_108758_) {
        if (this.minecraft.options.getCameraType().isFirstPerson()) {
            float f = Mth.lerp(p_108758_ * 0.5F, this.getYRot(), this.yRotO) * (float) (Math.PI / 180.0);
            float f1 = Mth.lerp(p_108758_ * 0.5F, this.getXRot(), this.xRotO) * (float) (Math.PI / 180.0);
            double d0 = this.getMainArm() == HumanoidArm.RIGHT ? -1.0 : 1.0;
            Vec3 vec3 = new Vec3(0.39 * d0, -0.6, 0.3);
            return vec3.xRot(-f1).yRot(-f).add(this.getEyePosition(p_108758_));
        } else {
            return super.getRopeHoldPosition(p_108758_);
        }
    }

    @Override
    public void updateTutorialInventoryAction(ItemStack p_172532_, ItemStack p_172533_, ClickAction p_172534_) {
        this.minecraft.getTutorial().onInventoryAction(p_172532_, p_172533_, p_172534_);
    }

    @Override
    public float getVisualRotationYInDegrees() {
        return this.getYRot();
    }

    public void updateSyncFields(LocalPlayer old) {
        this.xLast = old.xLast;
        this.yLast = old.yLast;
        this.zLast = old.zLast;
        this.yRotLast = old.yRotLast;
        this.xRotLast = old.xRotLast;
        this.lastOnGround = old.lastOnGround;
        this.wasShiftKeyDown = old.wasShiftKeyDown;
        this.wasSprinting = old.wasSprinting;
        this.positionReminder = old.positionReminder;
    }

    @Override
    public void handleCreativeModeItemDrop(ItemStack p_369228_) {
        this.minecraft.gameMode.handleCreativeModeItemDrop(p_369228_);
    }

    @Override
    public boolean canDropItems() {
        return this.dropSpamThrottler.isUnderThreshold();
    }

    public TickThrottler getDropSpamThrottler() {
        return this.dropSpamThrottler;
    }
}
