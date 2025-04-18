package net.minecraft.world.level.block;

import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public interface BonemealableBlock {
    boolean isValidBonemealTarget(LevelReader p_256559_, BlockPos p_50898_, BlockState p_50899_);

    boolean isBonemealSuccess(Level p_220878_, RandomSource p_220879_, BlockPos p_220880_, BlockState p_220881_);

    void performBonemeal(ServerLevel p_220874_, RandomSource p_220875_, BlockPos p_220876_, BlockState p_220877_);

    static boolean hasSpreadableNeighbourPos(LevelReader p_394969_, BlockPos p_392373_, BlockState p_397330_) {
        return getSpreadableNeighbourPos(Direction.Plane.HORIZONTAL.stream().toList(), p_394969_, p_392373_, p_397330_).isPresent();
    }

    static Optional<BlockPos> findSpreadableNeighbourPos(Level p_392087_, BlockPos p_394114_, BlockState p_393728_) {
        return getSpreadableNeighbourPos(Direction.Plane.HORIZONTAL.shuffledCopy(p_392087_.random), p_392087_, p_394114_, p_393728_);
    }

    private static Optional<BlockPos> getSpreadableNeighbourPos(List<Direction> p_395759_, LevelReader p_397008_, BlockPos p_391411_, BlockState p_394976_) {
        for (Direction direction : p_395759_) {
            BlockPos blockpos = p_391411_.relative(direction);
            if (p_397008_.isEmptyBlock(blockpos) && p_394976_.canSurvive(p_397008_, blockpos)) {
                return Optional.of(blockpos);
            }
        }

        return Optional.empty();
    }

    default BlockPos getParticlePos(BlockPos p_335812_) {
        return switch (this.getType()) {
            case NEIGHBOR_SPREADER -> p_335812_.above();
            case GROWER -> p_335812_;
        };
    }

    default BonemealableBlock.Type getType() {
        return BonemealableBlock.Type.GROWER;
    }

    public static enum Type {
        NEIGHBOR_SPREADER,
        GROWER;
    }
}