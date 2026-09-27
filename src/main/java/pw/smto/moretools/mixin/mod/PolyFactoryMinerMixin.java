package pw.smto.moretools.mixin.mod;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import eu.pb4.polyfactory.block.mechanical.machines.MinerBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import pw.smto.moretools.util.MiningBlockBreakingDirection;

@Pseudo // Really should be a mixin config plugin, but I cant be bothered
@Mixin(MinerBlockEntity.class)
public class PolyFactoryMinerMixin {
    @WrapOperation(method = "ticker", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;mineBlock(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)V"))
    private static void setDirection(ItemStack instance, Level level, BlockState state, BlockPos pos, Player owner, Operation<Void> original, @Local(name = "dir") Direction dir) {
        try {
            MiningBlockBreakingDirection.direction.set(dir.getOpposite());
            original.call(instance, level, state, pos, owner);
        } finally {
            MiningBlockBreakingDirection.direction.remove();
        }
    }
}
