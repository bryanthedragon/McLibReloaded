package net.minecraft.network.codec;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.Supplier;
import java.util.function.ToIntFunction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.IdMap;
import net.minecraft.core.Registry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.EndTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.Utf8String;
import net.minecraft.network.VarInt;
import net.minecraft.network.VarLong;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public interface ByteBufCodecs {
    int MAX_INITIAL_COLLECTION_SIZE = 65536;
    StreamCodec<ByteBuf, Boolean> BOOL = new StreamCodec<ByteBuf, Boolean>() {
        public Boolean decode(ByteBuf p_332480_) {
            return p_332480_.readBoolean();
        }

        public void encode(ByteBuf p_332710_, Boolean p_330535_) {
            p_332710_.writeBoolean(p_330535_);
        }
    };
    StreamCodec<ByteBuf, Byte> BYTE = new StreamCodec<ByteBuf, Byte>() {
        public Byte decode(ByteBuf p_332150_) {
            return p_332150_.readByte();
        }

        public void encode(ByteBuf p_328538_, Byte p_327835_) {
            p_328538_.writeByte(p_327835_);
        }
    };
    StreamCodec<ByteBuf, Float> ROTATION_BYTE = BYTE.map(Mth::unpackDegrees, Mth::packDegrees);
    StreamCodec<ByteBuf, Short> SHORT = new StreamCodec<ByteBuf, Short>() {
        public Short decode(ByteBuf p_331682_) {
            return p_331682_.readShort();
        }

        public void encode(ByteBuf p_329734_, Short p_332862_) {
            p_329734_.writeShort(p_332862_);
        }
    };
    StreamCodec<ByteBuf, Integer> UNSIGNED_SHORT = new StreamCodec<ByteBuf, Integer>() {
        public Integer decode(ByteBuf p_333416_) {
            return p_333416_.readUnsignedShort();
        }

        public void encode(ByteBuf p_334768_, Integer p_335195_) {
            p_334768_.writeShort(p_335195_);
        }
    };
    StreamCodec<ByteBuf, Integer> INT = new StreamCodec<ByteBuf, Integer>() {
        public Integer decode(ByteBuf p_334363_) {
            return p_334363_.readInt();
        }

        public void encode(ByteBuf p_328174_, Integer p_329350_) {
            p_328174_.writeInt(p_329350_);
        }
    };
    StreamCodec<ByteBuf, Integer> VAR_INT = new StreamCodec<ByteBuf, Integer>() {
        public Integer decode(ByteBuf p_334861_) {
            return VarInt.read(p_334861_);
        }

        public void encode(ByteBuf p_333121_, Integer p_329976_) {
            VarInt.write(p_333121_, p_329976_);
        }
    };
    StreamCodec<ByteBuf, OptionalInt> OPTIONAL_VAR_INT = VAR_INT.map(
        p_358482_ -> p_358482_ == 0 ? OptionalInt.empty() : OptionalInt.of(p_358482_ - 1), p_358481_ -> p_358481_.isPresent() ? p_358481_.getAsInt() + 1 : 0
    );
    StreamCodec<ByteBuf, Long> LONG = new StreamCodec<ByteBuf, Long>() {
        public Long decode(ByteBuf p_330259_) {
            return p_330259_.readLong();
        }

        public void encode(ByteBuf p_332625_, Long p_327681_) {
            p_332625_.writeLong(p_327681_);
        }
    };
    StreamCodec<ByteBuf, Long> VAR_LONG = new StreamCodec<ByteBuf, Long>() {
        public Long decode(ByteBuf p_335511_) {
            return VarLong.read(p_335511_);
        }

        public void encode(ByteBuf p_331177_, Long p_364567_) {
            VarLong.write(p_331177_, p_364567_);
        }
    };
    StreamCodec<ByteBuf, Float> FLOAT = new StreamCodec<ByteBuf, Float>() {
        public Float decode(ByteBuf p_330378_) {
            return p_330378_.readFloat();
        }

        public void encode(ByteBuf p_329698_, Float p_365105_) {
            p_329698_.writeFloat(p_365105_);
        }
    };
    StreamCodec<ByteBuf, Double> DOUBLE = new StreamCodec<ByteBuf, Double>() {
        public Double decode(ByteBuf p_331124_) {
            return p_331124_.readDouble();
        }

        public void encode(ByteBuf p_327898_, Double p_363039_) {
            p_327898_.writeDouble(p_363039_);
        }
    };
    StreamCodec<ByteBuf, byte[]> BYTE_ARRAY = new StreamCodec<ByteBuf, byte[]>() {
        public byte[] decode(ByteBuf p_330658_) {
            return FriendlyByteBuf.readByteArray(p_330658_);
        }

        public void encode(ByteBuf p_332407_, byte[] p_327934_) {
            FriendlyByteBuf.writeByteArray(p_332407_, p_327934_);
        }
    };
    StreamCodec<ByteBuf, long[]> LONG_ARRAY = new StreamCodec<ByteBuf, long[]>() {
        public long[] decode(ByteBuf p_329846_) {
            return FriendlyByteBuf.readLongArray(p_329846_);
        }

        public void encode(ByteBuf p_336297_, long[] p_397679_) {
            FriendlyByteBuf.writeLongArray(p_336297_, p_397679_);
        }
    };
    StreamCodec<ByteBuf, String> STRING_UTF8 = stringUtf8(32767);
    StreamCodec<ByteBuf, Tag> TAG = tagCodec(() -> NbtAccounter.create(2097152L));
    StreamCodec<ByteBuf, Tag> TRUSTED_TAG = tagCodec(NbtAccounter::unlimitedHeap);
    StreamCodec<ByteBuf, CompoundTag> COMPOUND_TAG = compoundTagCodec(() -> NbtAccounter.create(2097152L));
    StreamCodec<ByteBuf, CompoundTag> TRUSTED_COMPOUND_TAG = compoundTagCodec(NbtAccounter::unlimitedHeap);
    StreamCodec<ByteBuf, Optional<CompoundTag>> OPTIONAL_COMPOUND_TAG = new StreamCodec<ByteBuf, Optional<CompoundTag>>() {
        public Optional<CompoundTag> decode(ByteBuf p_328716_) {
            return Optional.ofNullable(FriendlyByteBuf.readNbt(p_328716_));
        }

        public void encode(ByteBuf p_327986_, Optional<CompoundTag> p_393792_) {
            FriendlyByteBuf.writeNbt(p_327986_, p_393792_.orElse(null));
        }
    };
    StreamCodec<ByteBuf, Vector3f> VECTOR3F = new StreamCodec<ByteBuf, Vector3f>() {
        public Vector3f decode(ByteBuf p_335035_) {
            return FriendlyByteBuf.readVector3f(p_335035_);
        }

        public void encode(ByteBuf p_328446_, Vector3f p_394463_) {
            FriendlyByteBuf.writeVector3f(p_328446_, p_394463_);
        }
    };
    StreamCodec<ByteBuf, Quaternionf> QUATERNIONF = new StreamCodec<ByteBuf, Quaternionf>() {
        public Quaternionf decode(ByteBuf p_331156_) {
            return FriendlyByteBuf.readQuaternion(p_331156_);
        }

        public void encode(ByteBuf p_328803_, Quaternionf p_392816_) {
            FriendlyByteBuf.writeQuaternion(p_328803_, p_392816_);
        }
    };
    StreamCodec<ByteBuf, Integer> CONTAINER_ID = new StreamCodec<ByteBuf, Integer>() {
        public Integer decode(ByteBuf p_329844_) {
            return FriendlyByteBuf.readContainerId(p_329844_);
        }

        public void encode(ByteBuf p_335209_, Integer p_393774_) {
            FriendlyByteBuf.writeContainerId(p_335209_, p_393774_);
        }
    };
    StreamCodec<ByteBuf, PropertyMap> GAME_PROFILE_PROPERTIES = new StreamCodec<ByteBuf, PropertyMap>() {
        private static final int MAX_PROPERTY_NAME_LENGTH = 64;
        private static final int MAX_PROPERTY_VALUE_LENGTH = 32767;
        private static final int MAX_PROPERTY_SIGNATURE_LENGTH = 1024;
        private static final int MAX_PROPERTIES = 16;

        public PropertyMap decode(ByteBuf p_395955_) {
            int i = ByteBufCodecs.readCount(p_395955_, 16);
            PropertyMap propertymap = new PropertyMap();

            for (int j = 0; j < i; j++) {
                String s = Utf8String.read(p_395955_, 64);
                String s1 = Utf8String.read(p_395955_, 32767);
                String s2 = FriendlyByteBuf.readNullable(p_395955_, p_392203_ -> Utf8String.read(p_392203_, 1024));
                Property property = new Property(s, s1, s2);
                propertymap.put(property.name(), property);
            }

            return propertymap;
        }

        public void encode(ByteBuf p_395895_, PropertyMap p_393829_) {
            ByteBufCodecs.writeCount(p_395895_, p_393829_.size(), 16);

            for (Property property : p_393829_.values()) {
                Utf8String.write(p_395895_, property.name(), 64);
                Utf8String.write(p_395895_, property.value(), 32767);
                FriendlyByteBuf.writeNullable(p_395895_, property.signature(), (p_392569_, p_396118_) -> Utf8String.write(p_392569_, p_396118_, 1024));
            }
        }
    };
    StreamCodec<ByteBuf, GameProfile> GAME_PROFILE = new StreamCodec<ByteBuf, GameProfile>() {
        public GameProfile decode(ByteBuf p_397129_) {
            UUID uuid = UUIDUtil.STREAM_CODEC.decode(p_397129_);
            String s = Utf8String.read(p_397129_, 16);
            GameProfile gameprofile = new GameProfile(uuid, s);
            gameprofile.getProperties().putAll(ByteBufCodecs.GAME_PROFILE_PROPERTIES.decode(p_397129_));
            return gameprofile;
        }

        public void encode(ByteBuf p_393289_, GameProfile p_397987_) {
            UUIDUtil.STREAM_CODEC.encode(p_393289_, p_397987_.getId());
            Utf8String.write(p_393289_, p_397987_.getName(), 16);
            ByteBufCodecs.GAME_PROFILE_PROPERTIES.encode(p_393289_, p_397987_.getProperties());
        }
    };

    static StreamCodec<ByteBuf, byte[]> byteArray(final int p_329369_) {
        return new StreamCodec<ByteBuf, byte[]>() {
            public byte[] decode(ByteBuf p_330658_) {
                return FriendlyByteBuf.readByteArray(p_330658_, p_329369_);
            }

            public void encode(ByteBuf p_332407_, byte[] p_327934_) {
                if (p_327934_.length > p_329369_) {
                    throw new EncoderException("ByteArray with size " + p_327934_.length + " is bigger than allowed " + p_329369_);
                } else {
                    FriendlyByteBuf.writeByteArray(p_332407_, p_327934_);
                }
            }
        };
    }

    static StreamCodec<ByteBuf, String> stringUtf8(final int p_332577_) {
        return new StreamCodec<ByteBuf, String>() {
            public String decode(ByteBuf p_363937_) {
                return Utf8String.read(p_363937_, p_332577_);
            }

            public void encode(ByteBuf p_367629_, String p_392026_) {
                Utf8String.write(p_367629_, p_392026_, p_332577_);
            }
        };
    }

    static StreamCodec<ByteBuf, Tag> tagCodec(final Supplier<NbtAccounter> p_334674_) {
        return new StreamCodec<ByteBuf, Tag>() {
            public Tag decode(ByteBuf p_397230_) {
                Tag tag = FriendlyByteBuf.readNbt(p_397230_, p_334674_.get());
                if (tag == null) {
                    throw new DecoderException("Expected non-null compound tag");
                } else {
                    return tag;
                }
            }

            public void encode(ByteBuf p_397825_, Tag p_393534_) {
                if (p_393534_ == EndTag.INSTANCE) {
                    throw new EncoderException("Expected non-null compound tag");
                } else {
                    FriendlyByteBuf.writeNbt(p_397825_, p_393534_);
                }
            }
        };
    }

    static StreamCodec<ByteBuf, CompoundTag> compoundTagCodec(Supplier<NbtAccounter> p_334293_) {
        return tagCodec(p_334293_).map(p_329005_ -> {
            if (p_329005_ instanceof CompoundTag compoundtag) {
                return compoundtag;
            } else {
                throw new DecoderException("Not a compound tag: " + p_329005_);
            }
        }, p_331817_ -> (Tag)p_331817_);
    }

    static <T> StreamCodec<ByteBuf, T> fromCodecTrusted(Codec<T> p_332454_) {
        return fromCodec(p_332454_, NbtAccounter::unlimitedHeap);
    }

    static <T> StreamCodec<ByteBuf, T> fromCodec(Codec<T> p_330766_) {
        return fromCodec(p_330766_, () -> NbtAccounter.create(2097152L));
    }

    static <T> StreamCodec<ByteBuf, T> fromCodec(Codec<T> p_332152_, Supplier<NbtAccounter> p_333221_) {
        return tagCodec(p_333221_)
            .map(
                p_328837_ -> p_332152_.parse(NbtOps.INSTANCE, p_328837_)
                    .getOrThrow(p_328190_ -> new DecoderException("Failed to decode: " + p_328190_ + " " + p_328837_)),
                p_329084_ -> p_332152_.encodeStart(NbtOps.INSTANCE, (T)p_329084_)
                    .getOrThrow(p_332410_ -> new EncoderException("Failed to encode: " + p_332410_ + " " + p_329084_))
            );
    }

    static <T> StreamCodec<RegistryFriendlyByteBuf, T> fromCodecWithRegistriesTrusted(Codec<T> p_331690_) {
        return fromCodecWithRegistries(p_331690_, NbtAccounter::unlimitedHeap);
    }

    static <T> StreamCodec<RegistryFriendlyByteBuf, T> fromCodecWithRegistries(Codec<T> p_334037_) {
        return fromCodecWithRegistries(p_334037_, () -> NbtAccounter.create(2097152L));
    }

    static <T> StreamCodec<RegistryFriendlyByteBuf, T> fromCodecWithRegistries(final Codec<T> p_332747_, Supplier<NbtAccounter> p_329046_) {
        final StreamCodec<ByteBuf, Tag> streamcodec = tagCodec(p_329046_);
        return new StreamCodec<RegistryFriendlyByteBuf, T>() {
            public T decode(RegistryFriendlyByteBuf p_391179_) {
                Tag tag = streamcodec.decode(p_391179_);
                RegistryOps<Tag> registryops = p_391179_.registryAccess().createSerializationContext(NbtOps.INSTANCE);
                return p_332747_.parse(registryops, tag).getOrThrow(p_395940_ -> new DecoderException("Failed to decode: " + p_395940_ + " " + tag));
            }

            public void encode(RegistryFriendlyByteBuf p_397169_, T p_396882_) {
                RegistryOps<Tag> registryops = p_397169_.registryAccess().createSerializationContext(NbtOps.INSTANCE);
                Tag tag = p_332747_.encodeStart(registryops, p_396882_)
                    .getOrThrow(p_392233_ -> new EncoderException("Failed to encode: " + p_392233_ + " " + p_396882_));
                streamcodec.encode(p_397169_, tag);
            }
        };
    }

    static <B extends ByteBuf, V> StreamCodec<B, Optional<V>> optional(final StreamCodec<B, V> p_333614_) {
        return new StreamCodec<B, Optional<V>>() {
            public Optional<V> decode(B p_336330_) {
                return p_336330_.readBoolean() ? Optional.of(p_333614_.decode(p_336330_)) : Optional.empty();
            }

            public void encode(B p_329166_, Optional<V> p_391573_) {
                if (p_391573_.isPresent()) {
                    p_329166_.writeBoolean(true);
                    p_333614_.encode(p_329166_, p_391573_.get());
                } else {
                    p_329166_.writeBoolean(false);
                }
            }
        };
    }

    static int readCount(ByteBuf p_335948_, int p_329745_) {
        int i = VarInt.read(p_335948_);
        if (i > p_329745_) {
            throw new DecoderException(i + " elements exceeded max size of: " + p_329745_);
        } else {
            return i;
        }
    }

    static void writeCount(ByteBuf p_332743_, int p_332779_, int p_330804_) {
        if (p_332779_ > p_330804_) {
            throw new EncoderException(p_332779_ + " elements exceeded max size of: " + p_330804_);
        } else {
            VarInt.write(p_332743_, p_332779_);
        }
    }

    static <B extends ByteBuf, V, C extends Collection<V>> StreamCodec<B, C> collection(IntFunction<C> p_329603_, StreamCodec<? super B, V> p_335274_) {
        return collection(p_329603_, p_335274_, Integer.MAX_VALUE);
    }

    static <B extends ByteBuf, V, C extends Collection<V>> StreamCodec<B, C> collection(
        final IntFunction<C> p_330282_, final StreamCodec<? super B, V> p_329504_, final int p_331395_
    ) {
        return new StreamCodec<B, C>() {
            public C decode(B p_328010_) {
                int i = ByteBufCodecs.readCount(p_328010_, p_331395_);
                C c = p_330282_.apply(Math.min(i, 65536));

                for (int j = 0; j < i; j++) {
                    c.add(p_329504_.decode(p_328010_));
                }

                return c;
            }

            public void encode(B p_335266_, C p_391188_) {
                ByteBufCodecs.writeCount(p_335266_, p_391188_.size(), p_331395_);

                for (V v : p_391188_) {
                    p_329504_.encode(p_335266_, v);
                }
            }
        };
    }

    static <B extends ByteBuf, V, C extends Collection<V>> StreamCodec.CodecOperation<B, V, C> collection(IntFunction<C> p_333333_) {
        return p_331526_ -> collection(p_333333_, p_331526_);
    }

    static <B extends ByteBuf, V> StreamCodec.CodecOperation<B, V, List<V>> list() {
        return p_331787_ -> collection(ArrayList::new, p_331787_);
    }

    static <B extends ByteBuf, V> StreamCodec.CodecOperation<B, V, List<V>> list(int p_331728_) {
        return p_328420_ -> collection(ArrayList::new, p_328420_, p_331728_);
    }

    static <B extends ByteBuf, K, V, M extends Map<K, V>> StreamCodec<B, M> map(
        IntFunction<? extends M> p_329613_, StreamCodec<? super B, K> p_335749_, StreamCodec<? super B, V> p_332695_
    ) {
        return map(p_329613_, p_335749_, p_332695_, Integer.MAX_VALUE);
    }

    static <B extends ByteBuf, K, V, M extends Map<K, V>> StreamCodec<B, M> map(
        final IntFunction<? extends M> p_331225_, final StreamCodec<? super B, K> p_334555_, final StreamCodec<? super B, V> p_330391_, final int p_331122_
    ) {
        return new StreamCodec<B, M>() {
            public void encode(B p_362090_, M p_396250_) {
                ByteBufCodecs.writeCount(p_362090_, p_396250_.size(), p_331122_);
                p_396250_.forEach((p_389932_, p_389933_) -> {
                    p_334555_.encode(p_362090_, (K)p_389932_);
                    p_330391_.encode(p_362090_, (V)p_389933_);
                });
            }

            public M decode(B p_365796_) {
                int i = ByteBufCodecs.readCount(p_365796_, p_331122_);
                M m = (M)p_331225_.apply(Math.min(i, 65536));

                for (int j = 0; j < i; j++) {
                    K k = p_334555_.decode(p_365796_);
                    V v = p_330391_.decode(p_365796_);
                    m.put(k, v);
                }

                return m;
            }
        };
    }

    static <B extends ByteBuf, L, R> StreamCodec<B, Either<L, R>> either(
        final StreamCodec<? super B, L> p_330563_, final StreamCodec<? super B, R> p_328664_
    ) {
        return new StreamCodec<B, Either<L, R>>() {
            public Either<L, R> decode(B p_368316_) {
                return p_368316_.readBoolean() ? Either.left(p_330563_.decode(p_368316_)) : Either.right(p_328664_.decode(p_368316_));
            }

            public void encode(B p_361972_, Either<L, R> p_394208_) {
                p_394208_.ifLeft(p_392082_ -> {
                    p_361972_.writeBoolean(true);
                    p_330563_.encode(p_361972_, (L)p_392082_);
                }).ifRight(p_392370_ -> {
                    p_361972_.writeBoolean(false);
                    p_328664_.encode(p_361972_, (R)p_392370_);
                });
            }
        };
    }

    static <B extends ByteBuf, V> StreamCodec.CodecOperation<B, V, V> lengthPrefixed(int p_393461_, BiFunction<B, ByteBuf, B> p_397581_) {
        return p_389928_ -> new StreamCodec<B, V>() {
            public V decode(B p_391476_) {
                int i = VarInt.read(p_391476_);
                if (i > p_393461_) {
                    throw new DecoderException("Buffer size " + i + " is larger than allowed limit of " + p_393461_);
                } else {
                    int j = p_391476_.readerIndex();
                    B b = (B)((ByteBuf)p_397581_.apply(p_391476_, p_391476_.slice(j, i)));
                    p_391476_.readerIndex(j + i);
                    return (V)p_389928_.decode(b);
                }
            }

            public void encode(B p_391889_, V p_362272_) {
                B b = (B)((ByteBuf)p_397581_.apply(p_391889_, p_391889_.alloc().buffer()));

                try {
                    p_389928_.encode(b, p_362272_);
                    int i = b.readableBytes();
                    if (i > p_393461_) {
                        throw new EncoderException("Buffer size " + i + " is  larger than allowed limit of " + p_393461_);
                    }

                    VarInt.write(p_391889_, i);
                    p_391889_.writeBytes(b);
                } finally {
                    b.release();
                }
            }
        };
    }

    static <V> StreamCodec.CodecOperation<RegistryFriendlyByteBuf, V, V> lengthPrefixed(int p_396380_) {
        return lengthPrefixed(p_396380_, (p_389924_, p_389925_) -> new RegistryFriendlyByteBuf(p_389925_, p_389924_.registryAccess()));
    }

    static <T> StreamCodec<ByteBuf, T> idMapper(final IntFunction<T> p_333433_, final ToIntFunction<T> p_334959_) {
        return new StreamCodec<ByteBuf, T>() {
            public T decode(ByteBuf p_393204_) {
                int i = VarInt.read(p_393204_);
                return p_333433_.apply(i);
            }

            public void encode(ByteBuf p_391372_, T p_394895_) {
                int i = p_334959_.applyAsInt(p_394895_);
                VarInt.write(p_391372_, i);
            }
        };
    }

    static <T> StreamCodec<ByteBuf, T> idMapper(IdMap<T> p_332036_) {
        return idMapper(p_332036_::byIdOrThrow, p_332036_::getIdOrThrow);
    }

    private static <T, R> StreamCodec<RegistryFriendlyByteBuf, R> registry(
        final ResourceKey<? extends Registry<T>> p_332046_, final Function<Registry<T>, IdMap<R>> p_332827_
    ) {
        return new StreamCodec<RegistryFriendlyByteBuf, R>() {
            private IdMap<R> getRegistryOrThrow(RegistryFriendlyByteBuf p_391225_) {
                return p_332827_.apply(p_391225_.registryAccess().lookupOrThrow(p_332046_));
            }

            public R decode(RegistryFriendlyByteBuf p_362854_) {
                int i = VarInt.read(p_362854_);
                return (R)this.getRegistryOrThrow(p_362854_).byIdOrThrow(i);
            }

            public void encode(RegistryFriendlyByteBuf p_362273_, R p_391276_) {
                int i = this.getRegistryOrThrow(p_362273_).getIdOrThrow(p_391276_);
                VarInt.write(p_362273_, i);
            }
        };
    }

    static <T> StreamCodec<RegistryFriendlyByteBuf, T> registry(ResourceKey<? extends Registry<T>> p_332712_) {
        return registry(p_332712_, p_335792_ -> p_335792_);
    }

    static <T> StreamCodec<RegistryFriendlyByteBuf, Holder<T>> holderRegistry(ResourceKey<? extends Registry<T>> p_332639_) {
        return registry(p_332639_, Registry::asHolderIdMap);
    }

    static <T> StreamCodec<RegistryFriendlyByteBuf, Holder<T>> holder(
        final ResourceKey<? extends Registry<T>> p_335347_, final StreamCodec<? super RegistryFriendlyByteBuf, T> p_329304_
    ) {
        return new StreamCodec<RegistryFriendlyByteBuf, Holder<T>>() {
            private static final int DIRECT_HOLDER_ID = 0;

            private IdMap<Holder<T>> getRegistryOrThrow(RegistryFriendlyByteBuf p_393125_) {
                return p_393125_.registryAccess().lookupOrThrow(p_335347_).asHolderIdMap();
            }

            public Holder<T> decode(RegistryFriendlyByteBuf p_393260_) {
                int i = VarInt.read(p_393260_);
                return i == 0 ? Holder.direct(p_329304_.decode(p_393260_)) : (Holder)this.getRegistryOrThrow(p_393260_).byIdOrThrow(i - 1);
            }

            public void encode(RegistryFriendlyByteBuf p_392751_, Holder<T> p_397139_) {
                switch (p_397139_.kind()) {
                    case REFERENCE:
                        int i = this.getRegistryOrThrow(p_392751_).getIdOrThrow(p_397139_);
                        VarInt.write(p_392751_, i + 1);
                        break;
                    case DIRECT:
                        VarInt.write(p_392751_, 0);
                        p_329304_.encode(p_392751_, p_397139_.value());
                }
            }
        };
    }

    static <T> StreamCodec<RegistryFriendlyByteBuf, HolderSet<T>> holderSet(final ResourceKey<? extends Registry<T>> p_328506_) {
        return new StreamCodec<RegistryFriendlyByteBuf, HolderSet<T>>() {
            private static final int NAMED_SET = -1;
            private final StreamCodec<RegistryFriendlyByteBuf, Holder<T>> holderCodec = ByteBufCodecs.holderRegistry(p_328506_);

            public HolderSet<T> decode(RegistryFriendlyByteBuf p_391777_) {
                int i = VarInt.read(p_391777_) - 1;
                if (i == -1) {
                    Registry<T> registry = p_391777_.registryAccess().lookupOrThrow(p_328506_);
                    return registry.get(TagKey.create(p_328506_, ResourceLocation.STREAM_CODEC.decode(p_391777_))).orElseThrow();
                } else {
                    List<Holder<T>> list = new ArrayList<>(Math.min(i, 65536));

                    for (int j = 0; j < i; j++) {
                        list.add(this.holderCodec.decode(p_391777_));
                    }

                    return HolderSet.direct(list);
                }
            }

            public void encode(RegistryFriendlyByteBuf p_393392_, HolderSet<T> p_396616_) {
                Optional<TagKey<T>> optional = p_396616_.unwrapKey();
                if (optional.isPresent()) {
                    VarInt.write(p_393392_, 0);
                    ResourceLocation.STREAM_CODEC.encode(p_393392_, optional.get().location());
                } else {
                    VarInt.write(p_393392_, p_396616_.size() + 1);

                    for (Holder<T> holder : p_396616_) {
                        this.holderCodec.encode(p_393392_, holder);
                    }
                }
            }
        };
    }
}
