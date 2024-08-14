package pw.smto.moretools.item;

import eu.pb4.polymer.core.api.item.PolymerItem;
import eu.pb4.polymer.core.api.utils.PolymerClientDecoded;
import eu.pb4.polymer.core.api.utils.PolymerKeepModel;
import eu.pb4.polymer.resourcepack.api.PolymerModelData;
import eu.pb4.polymer.resourcepack.api.PolymerResourcePackUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ShovelItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import pw.smto.moretools.MoreTools;
import pw.smto.moretools.util.BlockBoxUtils;

import java.util.ArrayList;
import java.util.List;

public class ExcavatorToolItem extends BaseToolItem implements PolymerItem, PolymerKeepModel, PolymerClientDecoded {
    private final PolymerModelData model;

    public ExcavatorToolItem(ShovelItem base) {
        super(base, BlockTags.SHOVEL_MINEABLE);
        this.model = PolymerResourcePackUtils.requestModel(base, Identifier.of(MoreTools.MOD_ID,
                "item/" + Registries.ITEM.getId(base).getPath().replace("shovel", "excavator")));
    }

    @Override
    public Item getPolymerItem(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        if (MoreTools.PLAYERS_WITH_CLIENT.contains(player)) {
            return this;
        }
        return this.model.item();
    }

    @Override
    public int getPolymerCustomModelData(ItemStack itemStack, @Nullable ServerPlayerEntity player) {
        return this.model.value();
    }

    @Override
    public String getGimmickText() {
        return "Allows breaking blocks in a 3x3 radius.";
    }

    @Override
    public List<BlockPos> getAffectedArea(@Nullable World world, BlockPos pos, @Nullable Direction d, @Nullable Block target) {
        var result = BlockBoxUtils.getSurroundingBlocks(pos, d, 1).toList();
        var list = new ArrayList<BlockPos>();
        for (BlockPos blockPos : result) {
            if (world.getBlockState(blockPos).isIn(BlockTags.SHOVEL_MINEABLE)) {
                list.add(blockPos);
            }
        }
        return list;
    }

    @Override
    public void doToolPower(BlockState state, BlockPos pos, Direction d, ServerPlayerEntity player, World world) {
        List<BlockPos> selection = getAffectedArea(world, pos, d, state.getBlock());
        BlockState blockBoxSelection;
        for (BlockPos blockBoxSelectionPos : selection) {
            blockBoxSelection = world.getBlockState(blockBoxSelectionPos);
            if (!blockBoxSelectionPos.equals(pos)) {
                blockBoxSelection.getBlock().onBreak(world, pos, blockBoxSelection, player);
                boolean bl = world.breakBlock(blockBoxSelectionPos, false);
                if (bl) {
                    blockBoxSelection.getBlock().onBroken(world, pos, blockBoxSelection);
                }
                if (!player.isCreative()) {
                    ItemStack itemStack = player.getMainHandStack();
                    ItemStack itemStack2 = itemStack.copy();
                    boolean bl2 = player.canHarvest(blockBoxSelection);
                    itemStack.postMine(world, blockBoxSelection, pos, player);
                    if (bl && bl2) {
                        blockBoxSelection.getBlock().afterBreak(world, player, pos, blockBoxSelection, world.getBlockEntity(blockBoxSelectionPos), itemStack2);
                    }
                }
            }
        }
    }
}
