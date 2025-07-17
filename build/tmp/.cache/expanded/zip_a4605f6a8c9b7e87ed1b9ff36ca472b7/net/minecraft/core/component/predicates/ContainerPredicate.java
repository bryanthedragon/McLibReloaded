package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.advancements.critereon.CollectionPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public record ContainerPredicate(Optional<CollectionPredicate<ItemStack, ItemPredicate>> items)
    implements SingleComponentItemPredicate<ItemContainerContents> {
    public static final Codec<ContainerPredicate> CODEC = RecordCodecBuilder.create(
        p_395520_ -> p_395520_.group(
                CollectionPredicate.<ItemStack, ItemPredicate>codec(ItemPredicate.CODEC)
                    .optionalFieldOf("items")
                    .forGetter(ContainerPredicate::items)
            )
            .apply(p_395520_, ContainerPredicate::new)
    );

    @Override
    public DataComponentType<ItemContainerContents> componentType() {
        return DataComponents.CONTAINER;
    }

    public boolean matches(ItemContainerContents p_397019_) {
        return !this.items.isPresent() || this.items.get().test(p_397019_.nonEmptyItems());
    }
}