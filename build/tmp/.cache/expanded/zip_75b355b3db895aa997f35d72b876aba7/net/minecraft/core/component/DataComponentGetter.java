package net.minecraft.core.component;

import javax.annotation.Nullable;

public interface DataComponentGetter {
    @Nullable
    <T> T get(DataComponentType<? extends T> p_395766_);

    default <T> T getOrDefault(DataComponentType<? extends T> p_396161_, T p_396548_) {
        T t = this.get(p_396161_);
        return t != null ? t : p_396548_;
    }

    @Nullable
    default <T> TypedDataComponent<T> getTyped(DataComponentType<T> p_396283_) {
        T t = this.get(p_396283_);
        return t != null ? new TypedDataComponent<>(p_396283_, t) : null;
    }
}