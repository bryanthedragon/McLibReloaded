package net.minecraft.client.gui.components.toasts;

import com.google.common.collect.Queues;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.mutable.MutableBoolean;

@OnlyIn(Dist.CLIENT)
public class ToastManager {
    private static final int SLOT_COUNT = 5;
    private static final int ALL_SLOTS_OCCUPIED = -1;
    final Minecraft minecraft;
    private final List<ToastManager.ToastInstance<?>> visibleToasts = new ArrayList<>();
    private final BitSet occupiedSlots = new BitSet(5);
    private final Deque<Toast> queued = Queues.newArrayDeque();
    private final Set<SoundEvent> playedToastSounds = new HashSet<>();

    public ToastManager(Minecraft p_363951_) {
        this.minecraft = p_363951_;
    }

    public void update() {
        MutableBoolean mutableboolean = new MutableBoolean(false);
        this.visibleToasts.removeIf(p_389308_ -> {
            Toast.Visibility toast$visibility = p_389308_.visibility;
            p_389308_.update();
            if (p_389308_.visibility != toast$visibility && mutableboolean.isFalse()) {
                mutableboolean.setTrue();
                p_389308_.visibility.playSound(this.minecraft.getSoundManager());
            }

            if (p_389308_.hasFinishedRendering()) {
                this.occupiedSlots.clear(p_389308_.firstSlotIndex, p_389308_.firstSlotIndex + p_389308_.occupiedSlotCount);
                return true;
            } else {
                return false;
            }
        });
        if (!this.queued.isEmpty() && this.freeSlotCount() > 0) {
            this.queued.removeIf(p_389309_ -> {
                int i = p_389309_.occcupiedSlotCount();
                int j = this.findFreeSlotsIndex(i);
                if (j == -1) {
                    return false;
                } else {
                    this.visibleToasts.add(new ToastManager.ToastInstance<>(p_389309_, j, i));
                    this.occupiedSlots.set(j, j + i);
                    SoundEvent soundevent = p_389309_.getSoundEvent();
                    if (soundevent != null && this.playedToastSounds.add(soundevent)) {
                        this.minecraft.getSoundManager().play(SimpleSoundInstance.forUI(soundevent, 1.0F, 1.0F));
                    }

                    return true;
                }
            });
        }

        this.playedToastSounds.clear();
    }

    public void render(GuiGraphics p_366590_) {
        if (!this.minecraft.options.hideGui) {
            int i = p_366590_.guiWidth();

            for (ToastManager.ToastInstance<?> toastinstance : this.visibleToasts) {
                toastinstance.render(p_366590_, i);
            }
        }
    }

    private int findFreeSlotsIndex(int p_366194_) {
        if (this.freeSlotCount() >= p_366194_) {
            int i = 0;

            for (int j = 0; j < 5; j++) {
                if (this.occupiedSlots.get(j)) {
                    i = 0;
                } else if (++i == p_366194_) {
                    return j + 1 - i;
                }
            }
        }

        return -1;
    }

    private int freeSlotCount() {
        return 5 - this.occupiedSlots.cardinality();
    }

    @Nullable
    public <T extends Toast> T getToast(Class<? extends T> p_368822_, Object p_361635_) {
        for (ToastManager.ToastInstance<?> toastinstance : this.visibleToasts) {
            if (toastinstance != null
                && p_368822_.isAssignableFrom(toastinstance.getToast().getClass())
                && toastinstance.getToast().getToken().equals(p_361635_)) {
                return (T)toastinstance.getToast();
            }
        }

        for (Toast toast : this.queued) {
            if (p_368822_.isAssignableFrom(toast.getClass()) && toast.getToken().equals(p_361635_)) {
                return (T)toast;
            }
        }

        return null;
    }

    public void clear() {
        this.occupiedSlots.clear();
        this.visibleToasts.clear();
        this.queued.clear();
    }

    public void addToast(Toast p_360768_) {
        if (net.minecraftforge.client.event.ForgeEventFactoryClient.onToastAdd(p_360768_)) return;
        this.queued.add(p_360768_);
    }

    public Minecraft getMinecraft() {
        return this.minecraft;
    }

    public double getNotificationDisplayTimeMultiplier() {
        return this.minecraft.options.notificationDisplayTime().get();
    }

    @OnlyIn(Dist.CLIENT)
    class ToastInstance<T extends Toast> {
        private static final long SLIDE_ANIMATION_DURATION_MS = 600L;
        private final T toast;
        final int firstSlotIndex;
        final int occupiedSlotCount;
        private long animationStartTime = -1L;
        private long becameFullyVisibleAt = -1L;
        Toast.Visibility visibility = Toast.Visibility.HIDE;
        private long fullyVisibleFor;
        private float visiblePortion;
        private boolean hasFinishedRendering;

        ToastInstance(final T p_369780_, final int p_370007_, final int p_366058_) {
            this.toast = p_369780_;
            this.firstSlotIndex = p_370007_;
            this.occupiedSlotCount = p_366058_;
        }

        public T getToast() {
            return this.toast;
        }

        public boolean hasFinishedRendering() {
            return this.hasFinishedRendering;
        }

        private void calculateVisiblePortion(long p_367026_) {
            float f = Mth.clamp((float)(p_367026_ - this.animationStartTime) / 600.0F, 0.0F, 1.0F);
            f *= f;
            if (this.visibility == Toast.Visibility.HIDE) {
                this.visiblePortion = 1.0F - f;
            } else {
                this.visiblePortion = f;
            }
        }

        public void update() {
            long i = Util.getMillis();
            if (this.animationStartTime == -1L) {
                this.animationStartTime = i;
                this.visibility = Toast.Visibility.SHOW;
            }

            if (this.visibility == Toast.Visibility.SHOW && i - this.animationStartTime <= 600L) {
                this.becameFullyVisibleAt = i;
            }

            this.fullyVisibleFor = i - this.becameFullyVisibleAt;
            this.calculateVisiblePortion(i);
            this.toast.update(ToastManager.this, this.fullyVisibleFor);
            Toast.Visibility toast$visibility = this.toast.getWantedVisibility();
            if (toast$visibility != this.visibility) {
                this.animationStartTime = i - (int)((1.0F - this.visiblePortion) * 600.0F);
                this.visibility = toast$visibility;
            }

            this.hasFinishedRendering = this.visibility == Toast.Visibility.HIDE && i - this.animationStartTime > 600L;
        }

        public void render(GuiGraphics p_369740_, int p_366638_) {
            p_369740_.pose().pushPose();
            p_369740_.pose().translate(p_366638_ - this.toast.width() * this.visiblePortion, this.firstSlotIndex * 32, 800.0F);
            this.toast.render(p_369740_, ToastManager.this.minecraft.font, this.fullyVisibleFor);
            p_369740_.pose().popPose();
        }
    }
}
