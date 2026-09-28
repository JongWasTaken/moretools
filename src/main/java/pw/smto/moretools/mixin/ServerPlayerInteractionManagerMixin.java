package pw.smto.moretools.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayerGameMode;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import pw.smto.moretools.util.MiningBlockBreakingDirection;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @WrapOperation(method = "handleBlockBreakAction", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayerGameMode;destroyAndAck(Lnet/minecraft/core/BlockPos;ILjava/lang/String;)V"))
    void passDestroyDirectionDown(ServerPlayerGameMode instance, BlockPos pos, int sequence, String exitId, Operation<Void> original, @Local(argsOnly = true, name = "direction") Direction direction) {
        try {
            MiningBlockBreakingDirection.direction.set(direction);
            original.call(instance, pos, sequence, exitId);
        } finally {
            MiningBlockBreakingDirection.direction.remove();
        }
    }
}
