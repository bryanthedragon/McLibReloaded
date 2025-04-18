package net.minecraft.client.renderer.texture.atlas.sources;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder.Instance;
import java.util.Optional;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.slf4j.Logger;

@OnlyIn(Dist.CLIENT)
public record SingleFile(ResourceLocation resourceId, Optional<ResourceLocation> spriteId) implements SpriteSource {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final MapCodec<SingleFile> MAP_CODEC = RecordCodecBuilder.mapCodec(
        p_261903_ -> p_261903_.group(
                ResourceLocation.CODEC.fieldOf("resource").forGetter(SingleFile::resourceId),
                ResourceLocation.CODEC.optionalFieldOf("sprite").forGetter(SingleFile::spriteId)
            )
            .apply(p_261903_, SingleFile::new)
    );

    public SingleFile(ResourceLocation p_397775_) {
        this(p_397775_, Optional.empty());
    }

    @Override
    public void run(ResourceManager p_261920_, SpriteSource.Output p_261578_) {
        ResourceLocation resourcelocation = TEXTURE_ID_CONVERTER.idToFile(this.resourceId);
        Optional<Resource> optional = p_261920_.getResource(resourcelocation);
        if (optional.isPresent()) {
            p_261578_.add(this.spriteId.orElse(this.resourceId), optional.get());
        } else {
            LOGGER.warn("Missing sprite: {}", resourcelocation);
        }
    }

    @Override
    public MapCodec<SingleFile> codec() {
        return MAP_CODEC;
    }
}