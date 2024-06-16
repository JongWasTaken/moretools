package pw.smto.moretools;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.HashMap;
// Taken from IndexLib, my shared library I am currently working on
public class MoreTools$CustomMiningToolItem {
    private static final HashMap<PlayerEntity,Direction> blockBreakDirections = new HashMap<>();
    public static void cacheBlockBreakDirection(PlayerEntity p, Direction d) {
        blockBreakDirections.put(p, d);
    }
    public static Direction getBlockBreakDirection(PlayerEntity p) {
        return blockBreakDirections.remove(p);
    }
    public interface MoreTools$Interface {
        void postBlockBreak(BlockState state, BlockPos pos, Direction d, ServerPlayerEntity player, World world);
    }
}

