package pw.smto.moretools.mixin;

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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerPlayerGameMode;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(ServerPlayerGameMode.class)
public abstract class ServerPlayerInteractionManagerMixin {
    @Shadow
    protected ServerLevel level;
    @Final @Shadow
    protected ServerPlayer player;

    @Redirect(method = "destroyBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;removeBlock(Lnet/minecraft/core/BlockPos;Z)Z"))
    private boolean removeBlockProxy(ServerLevel world, BlockPos pos, boolean dropItems) {
        BlockState state = world.getBlockState(pos);
        boolean cached = world.removeBlock(pos, dropItems);

        var handStack = this.player.getItemInHand(InteractionHand.MAIN_HAND);
        if (handStack.getItem() instanceof BaseToolItem item) {
            var d = ServerPlayerInteractionManagerMixin.BLOCK_BREAK_DIRECTIONS.remove(this.player);
            if (d != null) {
                item.doToolPowerIfAllowed(state, pos, d, this.player, this.level, handStack);
            }
        }

        return cached;
    }

    @Inject(method = "handleBlockBreakAction", at = @At("HEAD"))
    private void processBlockBreakingAction(BlockPos pos, ServerboundPlayerActionPacket.Action action, Direction direction, int maxY, int sequence, CallbackInfo ci) {
        ServerPlayerInteractionManagerMixin.BLOCK_BREAK_DIRECTIONS.put(this.player, direction);
    }

    @Unique
    private static final HashMap<Player,Direction> BLOCK_BREAK_DIRECTIONS = new HashMap<>();
}
