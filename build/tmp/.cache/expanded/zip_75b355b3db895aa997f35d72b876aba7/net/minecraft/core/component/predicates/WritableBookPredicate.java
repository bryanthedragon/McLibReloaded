package net.minecraft.core.component.predicates;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import java.util.function.Predicate;
import net.minecraft.advancements.critereon.CollectionPredicate;
import net.minecraft.advancements.critereon.SingleComponentItemPredicate;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.network.Filterable;
import net.minecraft.world.item.component.WritableBookContent;

public record WritableBookPredicate(Optional<CollectionPredicate<Filterable<String>, WritableBookPredicate.PagePredicate>> pages)
    implements SingleComponentItemPredicate<WritableBookContent> {
    public static final Codec<WritableBookPredicate> CODEC = RecordCodecBuilder.create(
        p_392086_ -> p_392086_.group(
                CollectionPredicate.<Filterable<String>, WritableBookPredicate.PagePredicate>codec(WritableBookPredicate.PagePredicate.CODEC)
                    .optionalFieldOf("pages")
                    .forGetter(WritableBookPredicate::pages)
            )
            .apply(p_392086_, WritableBookPredicate::new)
    );

    @Override
    public DataComponentType<WritableBookContent> componentType() {
        return DataComponents.WRITABLE_BOOK_CONTENT;
    }

    public boolean matches(WritableBookContent p_396091_) {
        return !this.pages.isPresent() || this.pages.get().test(p_396091_.pages());
    }

    public record PagePredicate(String contents) implements Predicate<Filterable<String>> {
        public static final Codec<WritableBookPredicate.PagePredicate> CODEC = Codec.STRING
            .xmap(WritableBookPredicate.PagePredicate::new, WritableBookPredicate.PagePredicate::contents);

        public boolean test(Filterable<String> p_394701_) {
            return p_394701_.raw().equals(this.contents);
        }
    }
}