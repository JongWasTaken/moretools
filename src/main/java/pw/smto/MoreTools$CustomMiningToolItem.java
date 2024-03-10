package pw.smto;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

public interface MoreTools$CustomMiningToolItem {

    void breakBlocks(BlockPos pos, ServerPlayerEntity player, ServerWorld world);
}
