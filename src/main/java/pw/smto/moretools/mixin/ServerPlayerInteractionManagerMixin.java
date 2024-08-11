package pw.smto.moretools.mixin;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import pw.smto.moretools.item.BaseToolItem;

import java.util.HashMap;

@Mixin(ServerPlayerInteractionManager.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Shadow
    protected ServerWorld world;
    @Final @Shadow
    protected ServerPlayerEntity player;

    @Redirect(method = "tryBreakBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;removeBlock(Lnet/minecraft/util/math/BlockPos;Z)Z"))
    private boolean removeBlockProxy(ServerWorld world, BlockPos pos, boolean dropItems) {
        BlockState state = world.getBlockState(pos);
        boolean cached = world.removeBlock(pos, dropItems);

        if (this.player.getStackInHand(Hand.MAIN_HAND).getItem() instanceof BaseToolItem item) {
            var d = blockBreakDirections.remove(this.player);
            if (d != null) {
                item.postBlockBreak(state, pos, d, this.player, this.world);
            }
        }

        return cached;
    }

    @Inject(method = "processBlockBreakingAction", at = @At("HEAD"))
    private void processBlockBreakingAction(BlockPos pos, PlayerActionC2SPacket.Action action, Direction direction, int worldHeight, int sequence, CallbackInfo ci) {
        blockBreakDirections.put(this.player, direction);
    }

    @Unique
    private static final HashMap<PlayerEntity,Direction> blockBreakDirections = new HashMap<>();
}
