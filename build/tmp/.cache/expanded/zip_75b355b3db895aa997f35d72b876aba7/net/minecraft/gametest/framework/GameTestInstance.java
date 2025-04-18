package net.minecraft.gametest.framework;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Rotation;

public abstract class GameTestInstance {
    public static final Codec<GameTestInstance> DIRECT_CODEC = BuiltInRegistries.TEST_INSTANCE_TYPE
        .byNameCodec()
        .dispatch(GameTestInstance::codec, p_396358_ -> p_396358_);
    private final TestData<Holder<TestEnvironmentDefinition>> info;

    public static MapCodec<? extends GameTestInstance> bootstrap(Registry<MapCodec<? extends GameTestInstance>> p_393018_) {
        register(p_393018_, "block_based", BlockBasedTestInstance.CODEC);
        return register(p_393018_, "function", FunctionGameTestInstance.CODEC);
    }

    private static MapCodec<? extends GameTestInstance> register(
        Registry<MapCodec<? extends GameTestInstance>> p_393004_, String p_397574_, MapCodec<? extends GameTestInstance> p_394315_
    ) {
        return Registry.register(p_393004_, ResourceKey.create(Registries.TEST_INSTANCE_TYPE, ResourceLocation.withDefaultNamespace(p_397574_)), p_394315_);
    }

    protected GameTestInstance(TestData<Holder<TestEnvironmentDefinition>> p_395082_) {
        this.info = p_395082_;
    }

    public abstract void run(GameTestHelper p_392369_);

    public abstract MapCodec<? extends GameTestInstance> codec();

    public Holder<TestEnvironmentDefinition> batch() {
        return this.info.environment();
    }

    public ResourceLocation structure() {
        return this.info.structure();
    }

    public int maxTicks() {
        return this.info.maxTicks();
    }

    public int setupTicks() {
        return this.info.setupTicks();
    }

    public boolean required() {
        return this.info.required();
    }

    public boolean manualOnly() {
        return this.info.manualOnly();
    }

    public int maxAttempts() {
        return this.info.maxAttempts();
    }

    public int requiredSuccesses() {
        return this.info.requiredSuccesses();
    }

    public boolean skyAccess() {
        return this.info.skyAccess();
    }

    public Rotation rotation() {
        return this.info.rotation();
    }

    protected TestData<Holder<TestEnvironmentDefinition>> info() {
        return this.info;
    }

    protected abstract MutableComponent typeDescription();

    public Component describe() {
        return this.describeType().append(this.describeInfo());
    }

    protected MutableComponent describeType() {
        return this.descriptionRow("test_instance.description.type", this.typeDescription());
    }

    protected Component describeInfo() {
        return this.descriptionRow("test_instance.description.structure", this.info.structure().toString())
            .append(this.descriptionRow("test_instance.description.batch", this.info.environment().getRegisteredName()));
    }

    protected MutableComponent descriptionRow(String p_394982_, String p_397845_) {
        return this.descriptionRow(p_394982_, Component.literal(p_397845_));
    }

    protected MutableComponent descriptionRow(String p_397192_, MutableComponent p_393907_) {
        return Component.translatable(p_397192_, p_393907_.withStyle(ChatFormatting.BLUE)).append(Component.literal("\n"));
    }
}