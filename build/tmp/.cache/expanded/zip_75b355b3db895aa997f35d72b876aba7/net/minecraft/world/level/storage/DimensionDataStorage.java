package net.minecraft.world.level.storage;

import com.google.common.collect.Iterables;
import com.mojang.datafixers.DataFixer;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.RegistryOps;
import net.minecraft.util.FastBufferedInputStream;
import net.minecraft.util.Mth;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import org.slf4j.Logger;

public class DimensionDataStorage implements AutoCloseable {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final SavedData.Context context;
    private final Map<SavedDataType<?>, Optional<SavedData>> cache = new HashMap<>();
    private final DataFixer fixerUpper;
    private final HolderLookup.Provider registries;
    private final Path dataFolder;
    private CompletableFuture<?> pendingWriteFuture = CompletableFuture.completedFuture(null);

    public DimensionDataStorage(SavedData.Context p_394962_, Path p_364133_, DataFixer p_78150_, HolderLookup.Provider p_336063_) {
        this.context = p_394962_;
        this.fixerUpper = p_78150_;
        this.dataFolder = p_364133_;
        this.registries = p_336063_;
    }

    private Path getDataFile(String p_78157_) {
        return this.dataFolder.resolve(p_78157_ + ".dat");
    }

    public <T extends SavedData> T computeIfAbsent(SavedDataType<T> p_393516_) {
        T t = this.get(p_393516_);
        if (t != null) {
            return t;
        } else {
            T t1 = (T)p_393516_.constructor().apply(this.context);
            this.set(p_393516_, t1);
            return t1;
        }
    }

    @Nullable
    public <T extends SavedData> T get(SavedDataType<T> p_395126_) {
        Optional<SavedData> optional = this.cache.get(p_395126_);
        if (optional == null) {
            optional = Optional.ofNullable(this.readSavedData(p_395126_));
            this.cache.put(p_395126_, optional);
        }

        return (T)optional.orElse(null);
    }

    @Nullable
    private <T extends SavedData> T readSavedData(SavedDataType<T> p_395608_) {
        try {
            Path path = this.getDataFile(p_395608_.id());
            if (Files.exists(path)) {
                CompoundTag compoundtag = this.readTagFromDisk(p_395608_.id(), p_395608_.dataFixType(), SharedConstants.getCurrentVersion().getDataVersion().getVersion());
                RegistryOps<Tag> registryops = this.registries.createSerializationContext(NbtOps.INSTANCE);
                return p_395608_.codec()
                    .apply(this.context)
                    .parse(registryops, compoundtag.get("data"))
                    .resultOrPartial(p_391114_ -> LOGGER.error("Failed to parse saved data for '{}': {}", p_395608_, p_391114_))
                    .orElse(null);
            }
        } catch (Exception exception) {
            LOGGER.error("Error loading saved data: {}", p_395608_, exception);
        }

        return null;
    }

    public <T extends SavedData> void set(SavedDataType<T> p_397374_, T p_164857_) {
        this.cache.put(p_397374_, Optional.of(p_164857_));
        p_164857_.setDirty();
    }

    public CompoundTag readTagFromDisk(String p_78159_, DataFixTypes p_301060_, int p_78160_) throws IOException {
        CompoundTag compoundtag1;
        try (
            InputStream inputstream = Files.newInputStream(this.getDataFile(p_78159_));
            PushbackInputStream pushbackinputstream = new PushbackInputStream(new FastBufferedInputStream(inputstream), 2);
        ) {
            CompoundTag compoundtag;
            if (this.isGzip(pushbackinputstream)) {
                compoundtag = NbtIo.readCompressed(pushbackinputstream, NbtAccounter.unlimitedHeap());
            } else {
                try (DataInputStream datainputstream = new DataInputStream(pushbackinputstream)) {
                    compoundtag = NbtIo.read(datainputstream);
                }
            }

            int i = NbtUtils.getDataVersion(compoundtag, 1343);
            // Forge: Allow the data fixer to be null, leaving the modder responsible for keeping track of their own data formats
            if (p_301060_ == null)
                compoundtag1 = compoundtag;
            else
            compoundtag1 = p_301060_.update(this.fixerUpper, compoundtag, i, p_78160_);
        }

        return compoundtag1;
    }

    private boolean isGzip(PushbackInputStream p_78155_) throws IOException {
        byte[] abyte = new byte[2];
        boolean flag = false;
        int i = p_78155_.read(abyte, 0, 2);
        if (i == 2) {
            int j = (abyte[1] & 255) << 8 | abyte[0] & 255;
            if (j == 35615) {
                flag = true;
            }
        }

        if (i != 0) {
            p_78155_.unread(abyte, 0, i);
        }

        return flag;
    }

    public CompletableFuture<?> scheduleSave() {
        Map<SavedDataType<?>, CompoundTag> map = this.collectDirtyTagsToSave();
        if (map.isEmpty()) {
            return CompletableFuture.completedFuture(null);
        } else {
            int i = Util.maxAllowedExecutorThreads();
            int j = map.size();
            if (j > i) {
                this.pendingWriteFuture = this.pendingWriteFuture.thenCompose(p_391121_ -> {
                    List<CompletableFuture<?>> list = new ArrayList<>(i);
                    int k = Mth.positiveCeilDiv(j, i);

                    for (List<Entry<SavedDataType<?>, CompoundTag>> list1 : Iterables.partition(map.entrySet(), k)) {
                        list.add(CompletableFuture.runAsync(() -> {
                            for (Entry<SavedDataType<?>, CompoundTag> entry : list1) {
                                this.tryWrite(entry.getKey(), entry.getValue());
                            }
                        }, Util.ioPool()));
                    }

                    return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
                });
            } else {
                this.pendingWriteFuture = this.pendingWriteFuture
                    .thenCompose(
                        p_391127_ -> CompletableFuture.allOf(
                            map.entrySet()
                                .stream()
                                .map(p_391115_ -> CompletableFuture.runAsync(() -> this.tryWrite(p_391115_.getKey(), p_391115_.getValue()), Util.ioPool()))
                                .toArray(CompletableFuture[]::new)
                        )
                    );
            }

            return this.pendingWriteFuture;
        }
    }

    private Map<SavedDataType<?>, CompoundTag> collectDirtyTagsToSave() {
        Map<SavedDataType<?>, CompoundTag> map = new Object2ObjectArrayMap<>();
        RegistryOps<Tag> registryops = this.registries.createSerializationContext(NbtOps.INSTANCE);
        this.cache.forEach((p_391111_, p_391112_) -> p_391112_.filter(SavedData::isDirty).ifPresent(p_391125_ -> {
            map.put(p_391111_, this.encodeUnchecked(p_391111_, p_391125_, registryops));
            p_391125_.setDirty(false);
        }));
        return map;
    }

    private <T extends SavedData> CompoundTag encodeUnchecked(SavedDataType<T> p_394470_, SavedData p_393813_, RegistryOps<Tag> p_392892_) {
        Codec<T> codec = p_394470_.codec().apply(this.context);
        CompoundTag compoundtag = new CompoundTag();
        compoundtag.put("data", codec.encodeStart(p_392892_, (T)p_393813_).getOrThrow());
        NbtUtils.addCurrentDataVersion(compoundtag);
        return compoundtag;
    }

    private void tryWrite(SavedDataType<?> p_395426_, CompoundTag p_378799_) {
        Path path = this.getDataFile(p_395426_.id());

        try {
            NbtIo.writeCompressed(p_378799_, path);
        } catch (IOException ioexception) {
            LOGGER.error("Could not save data to {}", path.getFileName(), ioexception);
        }
    }

    public void saveAndJoin() {
        this.scheduleSave().join();
    }

    @Override
    public void close() {
        this.saveAndJoin();
    }
}
