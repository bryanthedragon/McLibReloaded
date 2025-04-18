package net.minecraft.world.level.block;

import com.mojang.serialization.MapCodec;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public class SpawnerBlock extends BaseEntityBlock {
    public static final MapCodec<SpawnerBlock> CODEC = simpleCodec(SpawnerBlock::new);

    @Override
    public MapCodec<SpawnerBlock> codec() {
        return CODEC;
    }

    public SpawnerBlock(BlockBehaviour.Properties p_56781_) {
        super(p_56781_);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos p_154687_, BlockState p_154688_) {
        return new SpawnerBlockEntity(p_154687_, p_154688_);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_154683_, BlockState p_154684_, BlockEntityType<T> p_154685_) {
        return createTickerHelper(p_154685_, BlockEntityType.MOB_SPAWNER, p_154683_.isClientSide ? SpawnerBlockEntity::clientTick : SpawnerBlockEntity::serverTick);
    }

    @Override
    protected void spawnAfterBreak(BlockState p_222477_, ServerLevel p_222478_, BlockPos p_222479_, ItemStack p_222480_, boolean p_222481_) {
        super.spawnAfterBreak(p_222477_, p_222478_, p_222479_, p_222480_, p_222481_);
        if (false && p_222481_) { // Forge: moved to getExpDrop
            int i = 15 + p_222478_.random.nextInt(15) + p_222478_.random.nextInt(15);
            this.popExperience(p_222478_, p_222479_, i);
        }
    }

    @Override
    public int getExpDrop(BlockState state, net.minecraft.world.level.LevelReader level, net.minecraft.util.RandomSource randomSource, BlockPos pos, int fortuneLevel, int silkTouchLevel) {
        return 15 + randomSource.nextInt(15) + randomSource.nextInt(15);
    }
}
