package net.minecraft.world.entity.animal;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.List;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.variant.ModelAndTexture;
import net.minecraft.world.entity.variant.PriorityProvider;
import net.minecraft.world.entity.variant.SpawnCondition;
import net.minecraft.world.entity.variant.SpawnContext;
import net.minecraft.world.entity.variant.SpawnPrioritySelectors;

public record CowVariant(ModelAndTexture<CowVariant.ModelType> modelAndTexture, SpawnPrioritySelectors spawnConditions)
    implements PriorityProvider<SpawnContext, SpawnCondition> {
    public static final Codec<CowVariant> DIRECT_CODEC = RecordCodecBuilder.create(
        p_393839_ -> p_393839_.group(
                ModelAndTexture.codec(CowVariant.ModelType.CODEC, CowVariant.ModelType.NORMAL).forGetter(CowVariant::modelAndTexture),
                SpawnPrioritySelectors.CODEC.fieldOf("spawn_conditions").forGetter(CowVariant::spawnConditions)
            )
            .apply(p_393839_, CowVariant::new)
    );
    public static final Codec<CowVariant> NETWORK_CODEC = RecordCodecBuilder.create(
        p_393431_ -> p_393431_.group(ModelAndTexture.codec(CowVariant.ModelType.CODEC, CowVariant.ModelType.NORMAL).forGetter(CowVariant::modelAndTexture))
            .apply(p_393431_, CowVariant::new)
    );
    public static final Codec<Holder<CowVariant>> CODEC = RegistryFixedCodec.create(Registries.COW_VARIANT);
    public static final StreamCodec<RegistryFriendlyByteBuf, Holder<CowVariant>> STREAM_CODEC = ByteBufCodecs.holderRegistry(Registries.COW_VARIANT);

    private CowVariant(ModelAndTexture<CowVariant.ModelType> p_393713_) {
        this(p_393713_, SpawnPrioritySelectors.EMPTY);
    }

    @Override
    public List<PriorityProvider.Selector<SpawnContext, SpawnCondition>> selectors() {
        return this.spawnConditions.selectors();
    }

    public static enum ModelType implements StringRepresentable {
        NORMAL("normal"),
        COLD("cold"),
        WARM("warm");

        public static final Codec<CowVariant.ModelType> CODEC = StringRepresentable.fromEnum(CowVariant.ModelType::values);
        private final String name;

        private ModelType(final String p_391779_) {
            this.name = p_391779_;
        }

        @Override
        public String getSerializedName() {
            return this.name;
        }
    }
}