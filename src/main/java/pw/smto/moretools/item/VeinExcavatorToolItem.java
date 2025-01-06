package pw.smto.moretools.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.*;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pw.smto.moretools.MoreTools;
import pw.smto.moretools.util.BlockBoxUtils;
import xyz.nucleoid.packettweaker.PacketContext;

import java.util.ArrayList;
import java.util.List;

public class VeinExcavatorToolItem extends BaseToolItem implements PolymerItem, PolymerKeepModel, PolymerClientDecoded {
    private final Item baseItem;
    private final int range;

    public VeinExcavatorToolItem(ShovelItem base, ToolMaterial baseMaterial, int range) {
        super(Identifier.of(MoreTools.MOD_ID, Registries.ITEM.getId(base).getPath().replace("shovel", "vein_excavator")), baseMaterial, BlockTags.SHOVEL_MINEABLE);
        this.baseItem = base;
        this.range = range;
    }

    public VeinExcavatorToolItem(ShovelItem base, ToolMaterial baseMaterial) {
        this(base, baseMaterial, 3);
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, PacketContext context) {
        if (MoreTools.PLAYERS_WITH_CLIENT.contains(context.getPlayer())) {
            return this;
        }
        return this.baseItem;
    }


    @Override
    public @Nullable Identifier getPolymerItemModel(ItemStack stack, PacketContext context) {
        return super.id;
    }

    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("item.moretools.vein_excavator.tooltip").formatted(Formatting.GOLD));
    }

    @Override
    public List<BlockPos> getAffectedArea(@Nullable World world, BlockPos pos, BlockState state, @Nullable Direction d, @Nullable Block target) {
        var list = new ArrayList<BlockPos>();
        int range = 3;
        BlockState targetState = null;
        if (target != null) targetState = target.getDefaultState();
        if (targetState == null) return list;
        boolean useVanillaDirections = true;
        if (targetState.isIn(MoreTools.BlockTags.VEIN_EXCAVATOR_APPLICABLE)) {
            range = this.range;
            useVanillaDirections = false;
        }

        List<BlockPos> result;
        if (useVanillaDirections) {
            result = BlockBoxUtils.getBlockCluster(target, pos, world, range, BlockBoxUtils.DirectionSets.CARDINAL);
        } else result = BlockBoxUtils.getBlockCluster(target, pos, world, range, BlockBoxUtils.DirectionSets.EXTENDED);

        if (world != null) {
            for (BlockPos blockPos : result) {
                if (world.getBlockState(blockPos).isIn(BlockTags.SHOVEL_MINEABLE)) {
                    list.add(blockPos);
                }
            }
        }
        return list;
    }

    public void doToolPower(BlockState state, BlockPos pos, Direction d, ServerPlayerEntity player, World world) {
        List<BlockPos> selection = this.getAffectedArea(world, pos, state, d, state.getBlock());
        for (BlockPos blockBoxSelectionPos : selection) {
            if (!blockBoxSelectionPos.equals(pos)) {
                player.interactionManager.tryBreakBlock(blockBoxSelectionPos);
            }
        }
    }
}
