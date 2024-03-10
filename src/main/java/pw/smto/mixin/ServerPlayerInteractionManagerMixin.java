package pw.smto.mixin;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import pw.smto.MoreTools$CustomMiningToolItem;

@Mixin(ServerPlayerInteractionManager.class)
public class ServerPlayerInteractionManagerMixin {
    @Shadow
    protected ServerWorld world;
    @Final @Shadow
    protected ServerPlayerEntity player;
    @Inject(method = "tryBreakBlock", at = @At(value="INVOKE", target = "Lnet/minecraft/block/Block;onBroken(Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;)V"),  cancellable = true)
    private void tryBreakBlock(BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if (this.player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof MoreTools$CustomMiningToolItem) {
            ((MoreTools$CustomMiningToolItem) this.player.getStackInHand(Hand.MAIN_HAND).getItem()).breakBlocks(pos, this.player, this.world);
        }
    }
}
