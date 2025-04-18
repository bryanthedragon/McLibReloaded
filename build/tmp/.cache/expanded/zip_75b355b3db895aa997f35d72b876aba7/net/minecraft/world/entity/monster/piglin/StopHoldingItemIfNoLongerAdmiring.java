package net.minecraft.world.entity.monster.piglin;

import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.BehaviorControl;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.behavior.declarative.MemoryAccessor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;

public class StopHoldingItemIfNoLongerAdmiring {
    public static BehaviorControl<Piglin> create() {
        return BehaviorBuilder.create(
            p_259197_ -> p_259197_.group(p_259197_.absent(MemoryModuleType.ADMIRING_ITEM)).apply(p_259197_, p_259512_ -> (p_390715_, p_390716_, p_390717_) -> {
                if (!p_390716_.getOffhandItem().isEmpty() && !p_390716_.getOffhandItem().has(DataComponents.BLOCKS_ATTACKS)) {
                    PiglinAi.stopHoldingOffHandItem(p_390715_, p_390716_, true);
                    return true;
                } else {
                    return false;
                }
            })
        );
    }
}